/*
 * Copyright [2025] [envite consulting GmbH]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 *
 */

package io.kadai.classification.rest;

import static io.kadai.common.internal.util.CheckedConsumer.rethrowing;
import static io.kadai.common.internal.util.CheckedFunction.wrapping;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.kadai.classification.api.ClassificationCustomField;
import io.kadai.classification.api.ClassificationQuery;
import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.exceptions.ClassificationAlreadyExistException;
import io.kadai.classification.api.exceptions.ClassificationNotFoundException;
import io.kadai.classification.api.exceptions.MalformedServiceLevelException;
import io.kadai.classification.api.models.Classification;
import io.kadai.classification.api.models.ClassificationSummary;
import io.kadai.classification.rest.assembler.ClassificationDefinitionCollectionRepresentationModel;
import io.kadai.classification.rest.assembler.ClassificationDefinitionRepresentationModelAssembler;
import io.kadai.classification.rest.models.ClassificationDefinitionRepresentationModel;
import io.kadai.classification.rest.models.ClassificationRepresentationModel;
import io.kadai.classification.rest.models.ClassificationSummaryRepresentationModel;
import io.kadai.common.api.exceptions.ConcurrencyException;
import io.kadai.common.api.exceptions.DomainNotFoundException;
import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.common.rest.RestEndpoints;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** Controller for Importing / Exporting classifications. */
@RestController
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class ClassificationDefinitionController implements ClassificationDefinitionApi {

  private final ObjectMapper mapper;
  private final ClassificationService classificationService;
  private final ClassificationDefinitionRepresentationModelAssembler assembler;

  @Autowired
  ClassificationDefinitionController(
      ObjectMapper mapper,
      ClassificationService classificationService,
      ClassificationDefinitionRepresentationModelAssembler assembler) {
    this.mapper = mapper;
    this.classificationService = classificationService;
    this.assembler = assembler;
  }

  @GetMapping(path = RestEndpoints.URL_CLASSIFICATION_DEFINITIONS)
  public ResponseEntity<ClassificationDefinitionCollectionRepresentationModel>
      exportClassifications(@RequestParam(value = "domain", required = false) String[] domain) {
    ClassificationQuery query = classificationService.createClassificationQuery();

    List<ClassificationSummary> summaries =
        domain != null ? query.domainIn(domain).list() : query.list();

    ClassificationDefinitionCollectionRepresentationModel collectionModel =
        summaries.stream()
            .map(ClassificationSummary::getId)
            .map(wrapping(classificationService::getClassification))
            .collect(collectingAndThen(toList(), assembler::toKadaiCollectionModel));

    return ResponseEntity.ok(collectionModel);
  }

  @PostMapping(
      path = RestEndpoints.URL_CLASSIFICATION_DEFINITIONS,
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<Void> importClassifications(@RequestParam("file") MultipartFile file)
      throws InvalidArgumentException,
          ConcurrencyException,
          ClassificationNotFoundException,
          ClassificationAlreadyExistException,
          DomainNotFoundException,
          IOException,
          MalformedServiceLevelException,
          NotAuthorizedException {
    Map<String, String> systemIds = getSystemIds();
    ClassificationDefinitionCollectionRepresentationModel collection =
        extractClassificationResourcesFromFile(file);
    checkForDuplicates(collection.getContent());

    Map<Classification, String> childrenInFile =
        mapChildrenToParentKeys(collection.getContent(), systemIds);
    insertOrUpdateClassificationsWithoutParent(collection.getContent(), systemIds);
    updateParentChildrenRelations(childrenInFile);
    return ResponseEntity.noContent().build();
  }

  private Map<String, String> getSystemIds() {
    return classificationService.createClassificationQuery().list().stream()
        .collect(toMap(i -> logicalId(i.getKey(), i.getDomain()), ClassificationSummary::getId));
  }

  private ClassificationDefinitionCollectionRepresentationModel
      extractClassificationResourcesFromFile(MultipartFile file) throws IOException {
    return mapper.readValue(
        file.getInputStream(), ClassificationDefinitionCollectionRepresentationModel.class);
  }

  private void checkForDuplicates(
      Collection<ClassificationDefinitionRepresentationModel> definitionList)
      throws ClassificationAlreadyExistException {
    Set<String> seen = new HashSet<>();
    definitionList.stream()
        .map(ClassificationDefinitionRepresentationModel::getClassification)
        .forEach(
            rethrowing(
                cl -> {
                  String key = cl.getKey();
                  String domain = cl.getDomain();
                  if (!seen.add(logicalId(key, domain))) {
                    throw new ClassificationAlreadyExistException(key, domain);
                  }
                }));
  }

  private Map<Classification, String> mapChildrenToParentKeys(
      Collection<ClassificationDefinitionRepresentationModel> definitionList,
      Map<String, String> systemIds) {

    Set<String> keysWithDomain =
        definitionList.stream()
            .map(ClassificationDefinitionRepresentationModel::getClassification)
            .map(this::logicalId)
            .collect(toSet());

    Map<String, String> classificationIdToKey =
        definitionList.stream()
            .map(ClassificationDefinitionRepresentationModel::getClassification)
            .filter(
                classification ->
                    classification.getClassificationId() != null && classification.getKey() != null)
            .collect(
                toMap(
                    ClassificationSummaryRepresentationModel::getClassificationId,
                    ClassificationSummaryRepresentationModel::getKey,
                    (existing, replacement) -> existing));

    definitionList.stream()
        .map(ClassificationDefinitionRepresentationModel::getClassification)
        .map(this::normalizeNullValues)
        .filter(cl -> !cl.getParentId().isEmpty() && cl.getParentKey().isEmpty())
        .forEach(cl -> cl.setParentKey(classificationIdToKey.getOrDefault(cl.getParentId(), "")));

    return definitionList.stream()
        .map(def -> Map.entry(def, def.getClassification()))
        .filter(
            defClassEntry ->
                hasResolvableParent(defClassEntry.getValue(), keysWithDomain, systemIds))
        .collect(
            toMap(
                defClassEntry -> assembler.toEntityModel(defClassEntry.getKey()),
                defClassEntry -> defClassEntry.getValue().getParentKey()));
  }

  ClassificationRepresentationModel normalizeNullValues(ClassificationRepresentationModel cl) {
    if (cl.getParentKey() == null) {
      cl.setParentKey("");
    }
    if (cl.getParentId() == null) {
      cl.setParentId("");
    }
    return cl;
  }

  Boolean hasResolvableParent(
      ClassificationRepresentationModel cl,
      Set<String> keysWithDomain,
      Map<String, String> systemIds) {
    String parentKeyAndDomain = logicalId(cl);
    return !cl.getParentKey().isEmpty()
        && (keysWithDomain.contains(parentKeyAndDomain)
            || systemIds.containsKey(parentKeyAndDomain));
  }

  private void insertOrUpdateClassificationsWithoutParent(
      Collection<ClassificationDefinitionRepresentationModel> definitionList,
      Map<String, String> systemIds)
      throws ClassificationNotFoundException,
          InvalidArgumentException,
          ClassificationAlreadyExistException,
          DomainNotFoundException,
          ConcurrencyException,
          MalformedServiceLevelException,
          NotAuthorizedException {
    for (ClassificationDefinitionRepresentationModel definition : definitionList) {
      ClassificationRepresentationModel classificationRepModel = definition.getClassification();
      classificationRepModel.setParentKey(null);
      classificationRepModel.setParentId(null);
      classificationRepModel.setClassificationId(null);

      Classification newClassification = assembler.toEntityModel(definition);

      String systemId = systemIds.get(logicalId(classificationRepModel));
      if (systemId != null) {
        updateExistingClassification(newClassification, systemId);
      } else {
        classificationService.createClassification(newClassification);
      }
    }
  }

  private void updateParentChildrenRelations(Map<Classification, String> childrenInFile)
      throws ClassificationNotFoundException,
          ConcurrencyException,
          InvalidArgumentException,
          MalformedServiceLevelException,
          NotAuthorizedException {
    for (Map.Entry<Classification, String> entry : childrenInFile.entrySet()) {
      Classification childRes = entry.getKey();
      String parentKey = entry.getValue();
      String classificationKey = childRes.getKey();
      String classificationDomain = childRes.getDomain();

      Classification child =
          classificationService.getClassification(classificationKey, classificationDomain);
      String parentId =
          (parentKey == null)
              ? ""
              : classificationService.getClassification(parentKey, classificationDomain).getId();

      child.setParentKey(parentKey);
      child.setParentId(parentId);

      classificationService.updateClassification(child);
    }
  }

  private void updateExistingClassification(Classification newClassification, String systemId)
      throws ClassificationNotFoundException,
          ConcurrencyException,
          InvalidArgumentException,
          MalformedServiceLevelException,
          NotAuthorizedException {
    Classification currentClassification = classificationService.getClassification(systemId);
    if (newClassification.getType() != null
        && !newClassification.getType().equals(currentClassification.getType())) {
      throw new InvalidArgumentException("Can not change the type of a classification.");
    }
    currentClassification.setCategory(newClassification.getCategory());
    currentClassification.setIsValidInDomain(newClassification.getIsValidInDomain());
    currentClassification.setName(newClassification.getName());
    currentClassification.setParentId(newClassification.getParentId());
    currentClassification.setParentKey(newClassification.getParentKey());
    currentClassification.setDescription(newClassification.getDescription());
    currentClassification.setPriority(newClassification.getPriority());
    currentClassification.setServiceLevel(newClassification.getServiceLevel());
    currentClassification.setApplicationEntryPoint(newClassification.getApplicationEntryPoint());
    currentClassification.setCustomField(
        ClassificationCustomField.CUSTOM_1,
        newClassification.getCustomField(ClassificationCustomField.CUSTOM_1));
    currentClassification.setCustomField(
        ClassificationCustomField.CUSTOM_2,
        newClassification.getCustomField(ClassificationCustomField.CUSTOM_2));
    currentClassification.setCustomField(
        ClassificationCustomField.CUSTOM_3,
        newClassification.getCustomField(ClassificationCustomField.CUSTOM_3));
    currentClassification.setCustomField(
        ClassificationCustomField.CUSTOM_4,
        newClassification.getCustomField(ClassificationCustomField.CUSTOM_4));
    currentClassification.setCustomField(
        ClassificationCustomField.CUSTOM_5,
        newClassification.getCustomField(ClassificationCustomField.CUSTOM_5));
    currentClassification.setCustomField(
        ClassificationCustomField.CUSTOM_6,
        newClassification.getCustomField(ClassificationCustomField.CUSTOM_6));
    currentClassification.setCustomField(
        ClassificationCustomField.CUSTOM_7,
        newClassification.getCustomField(ClassificationCustomField.CUSTOM_7));
    currentClassification.setCustomField(
        ClassificationCustomField.CUSTOM_8,
        newClassification.getCustomField(ClassificationCustomField.CUSTOM_8));
    classificationService.updateClassification(currentClassification);
  }

  private String logicalId(ClassificationRepresentationModel classification) {
    return logicalId(classification.getKey(), classification.getDomain());
  }

  private String logicalId(String key, String domain) {
    return key + "|" + domain;
  }
}
