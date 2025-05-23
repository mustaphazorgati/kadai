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

package io.kadai.classification.rest.assembler;

import static io.kadai.classification.api.ClassificationCustomField.CUSTOM_1;
import static io.kadai.classification.api.ClassificationCustomField.CUSTOM_2;
import static io.kadai.classification.api.ClassificationCustomField.CUSTOM_3;
import static io.kadai.classification.api.ClassificationCustomField.CUSTOM_4;
import static io.kadai.classification.api.ClassificationCustomField.CUSTOM_5;
import static io.kadai.classification.api.ClassificationCustomField.CUSTOM_6;
import static io.kadai.classification.api.ClassificationCustomField.CUSTOM_7;
import static io.kadai.classification.api.ClassificationCustomField.CUSTOM_8;

import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.models.ClassificationSummary;
import io.kadai.classification.internal.models.ClassificationSummaryImpl;
import io.kadai.classification.rest.models.ClassificationSummaryPagedRepresentationModel;
import io.kadai.classification.rest.models.ClassificationSummaryRepresentationModel;
import io.kadai.common.rest.assembler.PagedRepresentationModelAssembler;
import io.kadai.common.rest.models.PageMetadata;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/** EntityModel assembler for {@link ClassificationSummaryRepresentationModel}. */
@Component
public class ClassificationSummaryRepresentationModelAssembler
    implements PagedRepresentationModelAssembler<
        ClassificationSummary,
        ClassificationSummaryRepresentationModel,
        ClassificationSummaryPagedRepresentationModel> {

  private final ClassificationService classificationService;

  @Autowired
  public ClassificationSummaryRepresentationModelAssembler(
      ClassificationService classificationService) {
    this.classificationService = classificationService;
  }

  @NonNull
  @Override
  public ClassificationSummaryRepresentationModel toModel(
      @NonNull ClassificationSummary classificationSummary) {
    ClassificationSummaryRepresentationModel repModel =
        new ClassificationSummaryRepresentationModel();
    repModel.setClassificationId(classificationSummary.getId());
    repModel.setApplicationEntryPoint(classificationSummary.getApplicationEntryPoint());
    repModel.setCategory(classificationSummary.getCategory());
    repModel.setDomain(classificationSummary.getDomain());
    repModel.setKey(classificationSummary.getKey());
    repModel.setName(classificationSummary.getName());
    repModel.setParentId(classificationSummary.getParentId());
    repModel.setParentKey(classificationSummary.getParentKey());
    repModel.setPriority(classificationSummary.getPriority());
    repModel.setServiceLevel(classificationSummary.getServiceLevel());
    repModel.setType(classificationSummary.getType());
    repModel.setCustom1(classificationSummary.getCustomField(CUSTOM_1));
    repModel.setCustom2(classificationSummary.getCustomField(CUSTOM_2));
    repModel.setCustom3(classificationSummary.getCustomField(CUSTOM_3));
    repModel.setCustom4(classificationSummary.getCustomField(CUSTOM_4));
    repModel.setCustom5(classificationSummary.getCustomField(CUSTOM_5));
    repModel.setCustom6(classificationSummary.getCustomField(CUSTOM_6));
    repModel.setCustom7(classificationSummary.getCustomField(CUSTOM_7));
    repModel.setCustom8(classificationSummary.getCustomField(CUSTOM_8));
    return repModel;
  }

  public ClassificationSummary toEntityModel(ClassificationSummaryRepresentationModel repModel) {
    ClassificationSummaryImpl classification =
        (ClassificationSummaryImpl)
            classificationService
                .newClassification(repModel.getKey(), repModel.getDomain(), repModel.getType())
                .asSummary();
    classification.setId(repModel.getClassificationId());
    classification.setApplicationEntryPoint(repModel.getApplicationEntryPoint());
    classification.setCategory(repModel.getCategory());
    classification.setName(repModel.getName());
    classification.setParentId(repModel.getParentId());
    classification.setParentKey(repModel.getParentKey());
    classification.setPriority(repModel.getPriority());
    classification.setServiceLevel(repModel.getServiceLevel());
    classification.setCustom1(repModel.getCustom1());
    classification.setCustom2(repModel.getCustom2());
    classification.setCustom3(repModel.getCustom3());
    classification.setCustom4(repModel.getCustom4());
    classification.setCustom5(repModel.getCustom5());
    classification.setCustom6(repModel.getCustom6());
    classification.setCustom7(repModel.getCustom7());
    classification.setCustom8(repModel.getCustom8());
    return classification;
  }

  @Override
  public ClassificationSummaryPagedRepresentationModel buildPageableEntity(
      Collection<ClassificationSummaryRepresentationModel> content, PageMetadata pageMetadata) {
    return new ClassificationSummaryPagedRepresentationModel(content, pageMetadata);
  }
}
