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

package io.kadai.testapi.builder;

import static io.kadai.testapi.builder.ClassificationBuilder.newClassification;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import io.kadai.classification.api.ClassificationCustomField;
import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.models.Classification;
import io.kadai.classification.api.models.ClassificationSummary;
import io.kadai.classification.internal.models.ClassificationImpl;
import io.kadai.classification.internal.models.ClassificationSummaryImpl;
import io.kadai.common.internal.util.Quadruple;
import io.kadai.testapi.KadaiInject;
import io.kadai.testapi.KadaiIntegrationTest;
import io.kadai.testapi.security.WithAccessId;
import java.time.Instant;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

@KadaiIntegrationTest
class ClassificationBuilderTest {

  @KadaiInject ClassificationService classificationService;

  @WithAccessId(user = "businessadmin")
  @Test
  void should_PersistClassification_When_UsingClassificationBuilder() throws Exception {
    Classification classification =
        newClassification().key("key0_A").domain("DOMAIN_A").buildAndStore(classificationService);

    Classification receivedClassification =
        classificationService.getClassification(classification.getId());
    assertThat(receivedClassification).isEqualTo(classification);
  }

  @Test
  void should_PersistClassificationAsUser_When_UsingClassificationBuilder() throws Exception {
    Classification classification =
        newClassification()
            .key("key1_A")
            .domain("DOMAIN_A")
            .buildAndStore(classificationService, "businessadmin");

    Classification receivedClassification =
        classificationService.getClassification(classification.getId());
    assertThat(receivedClassification).isEqualTo(classification);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_PopulateClassification_When_UsingEveryBuilderFunction() throws Exception {
    Classification parentClassification =
        newClassification()
            .key("parent classification")
            .domain("DOMAIN_A")
            .buildAndStore(classificationService);
    final Classification classification =
        newClassification()
            .applicationEntryPoint("application entry point")
            .category("MANUAL")
            .domain("DOMAIN_A")
            .key("key2_A")
            .name("dope Classification name")
            .parentId(parentClassification.getId())
            .parentKey(parentClassification.getKey())
            .priority(1337)
            .serviceLevel("P15D")
            .type("TASK")
            .customAttribute(ClassificationCustomField.CUSTOM_1, "custom 1 value")
            .customAttribute(ClassificationCustomField.CUSTOM_2, "custom 2 value")
            .customAttribute(ClassificationCustomField.CUSTOM_3, "custom 3 value")
            .customAttribute(ClassificationCustomField.CUSTOM_4, "custom 4 value")
            .customAttribute(ClassificationCustomField.CUSTOM_5, "custom 5 value")
            .customAttribute(ClassificationCustomField.CUSTOM_6, "custom 6 value")
            .customAttribute(ClassificationCustomField.CUSTOM_7, "custom 7 value")
            .customAttribute(ClassificationCustomField.CUSTOM_8, "custom 8 value")
            .isValidInDomain(false)
            .created(Instant.parse("2021-05-17T07:16:26.747Z"))
            .modified(Instant.parse("2021-05-18T07:16:26.747Z"))
            .description("this is a dope description")
            .buildAndStore(classificationService);

    ClassificationImpl expectedClassification =
        (ClassificationImpl) classificationService.newClassification("key2_A", "DOMAIN_A", "TASK");
    expectedClassification.setApplicationEntryPoint("application entry point");
    expectedClassification.setCategory("MANUAL");
    expectedClassification.setName("dope Classification name");
    expectedClassification.setParentId(parentClassification.getId());
    expectedClassification.setParentKey(parentClassification.getKey());
    expectedClassification.setPriority(1337);
    expectedClassification.setServiceLevel("P15D");
    expectedClassification.setCustomField(ClassificationCustomField.CUSTOM_1, "custom 1 value");
    expectedClassification.setCustomField(ClassificationCustomField.CUSTOM_2, "custom 2 value");
    expectedClassification.setCustomField(ClassificationCustomField.CUSTOM_3, "custom 3 value");
    expectedClassification.setCustomField(ClassificationCustomField.CUSTOM_4, "custom 4 value");
    expectedClassification.setCustomField(ClassificationCustomField.CUSTOM_5, "custom 5 value");
    expectedClassification.setCustomField(ClassificationCustomField.CUSTOM_6, "custom 6 value");
    expectedClassification.setCustomField(ClassificationCustomField.CUSTOM_7, "custom 7 value");
    expectedClassification.setCustomField(ClassificationCustomField.CUSTOM_8, "custom 8 value");
    expectedClassification.setIsValidInDomain(false);
    expectedClassification.setCreated(Instant.parse("2021-05-17T07:16:26.747Z"));
    expectedClassification.setModified(Instant.parse("2021-05-18T07:16:26.747Z"));
    expectedClassification.setDescription("this is a dope description");

    assertThat(classification)
        .hasNoNullFieldsOrProperties()
        .usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(expectedClassification);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_ResetClassificationId_When_StoringClassificationMultipleTimes() {
    ClassificationBuilder builder = newClassification().domain("DOMAIN_A");

    assertThatCode(
            () -> {
              builder.key("key4_A").buildAndStore(classificationService);
              builder.key("key5_A").buildAndStore(classificationService);
            })
        .doesNotThrowAnyException();
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_ReturnClassificationImpl_When_BuildingClassification() throws Exception {
    Classification classification =
        newClassification().key("key6_A").domain("DOMAIN_A").buildAndStore(classificationService);

    assertThat(classification.getClass()).isEqualTo(ClassificationImpl.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_ReturnClassificationSummaryImpl_When_BuildingClassificationAsSummary()
      throws Exception {
    ClassificationSummary classificationSummary =
        newClassification()
            .key("key7_A")
            .domain("DOMAIN_A")
            .buildAndStoreAsSummary(classificationService);

    assertThat(classificationSummary.getClass()).isEqualTo(ClassificationSummaryImpl.class);
  }

  @WithAccessId(user = "businessadmin")
  @TestFactory
  Stream<DynamicContainer> should_PersistClassification_When_CreatingEntityWithInvalidApiValues() {
    List<
            Quadruple<
                String,
                Object,
                BiFunction<ClassificationBuilder, Object, ClassificationBuilder>,
                Function<Classification, Object>>>
        list =
            List.of(
                Quadruple.of(
                    "created",
                    Instant.parse("2020-05-17T07:16:26.747Z"),
                    (b, v) -> b.created((Instant) v),
                    Classification::getCreated),
                Quadruple.of(
                    "modified",
                    Instant.parse("2019-05-17T07:16:26.747Z"),
                    (b, v) -> b.modified((Instant) v),
                    Classification::getModified));

    Stream<DynamicTest> applyBuilderFunction =
        DynamicTest.stream(
            list.iterator(),
            q -> String.format("for field: '%s'", q.getFirst()),
            q -> applyBuilderFunctionAndVerifyValue(q.getSecond(), q.getThird(), q.getFourth()));

    Stream<DynamicTest> overrideBuilderFunctionWithApiDefault =
        DynamicTest.stream(
            list.iterator(),
            q -> String.format("for field: '%s'", q.getFirst()),
            t -> applyAndOverrideWithApiDefaultValue(t.getSecond(), t.getThird(), t.getFourth()));

    return Stream.of(
        DynamicContainer.dynamicContainer(
            "set values which are invalid through API", applyBuilderFunction),
        DynamicContainer.dynamicContainer(
            "override with API default value", overrideBuilderFunctionWithApiDefault));
  }

  private <T> void applyBuilderFunctionAndVerifyValue(
      T value,
      BiFunction<ClassificationBuilder, T, ClassificationBuilder> builderFunction,
      Function<Classification, T> retriever)
      throws Exception {
    ClassificationBuilder builder =
        newClassification().domain("DOMAIN_A").key("A" + builderFunction.hashCode());

    builderFunction.apply(builder, value);
    Classification classification = builder.buildAndStore(classificationService);
    T retrievedValue = retriever.apply(classification);

    assertThat(retrievedValue).isEqualTo(value);
  }

  private <T> void applyAndOverrideWithApiDefaultValue(
      T value,
      BiFunction<ClassificationBuilder, T, ClassificationBuilder> builderFunction,
      Function<Classification, T> retriever)
      throws Exception {
    ClassificationBuilder builder =
        newClassification().domain("DOMAIN_A").key("B" + builderFunction.hashCode());

    builderFunction.apply(builder, value);
    builderFunction.apply(builder, null);

    Classification classification = builder.buildAndStore(classificationService);
    T retrievedValue = retriever.apply(classification);

    assertThat(retrievedValue).isNotNull().isNotEqualTo(value);
  }
}
