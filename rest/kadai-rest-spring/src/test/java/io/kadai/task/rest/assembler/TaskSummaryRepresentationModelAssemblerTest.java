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

package io.kadai.task.rest.assembler;

import static io.kadai.task.api.TaskCustomField.CUSTOM_1;
import static io.kadai.task.api.TaskCustomField.CUSTOM_10;
import static io.kadai.task.api.TaskCustomField.CUSTOM_11;
import static io.kadai.task.api.TaskCustomField.CUSTOM_12;
import static io.kadai.task.api.TaskCustomField.CUSTOM_13;
import static io.kadai.task.api.TaskCustomField.CUSTOM_14;
import static io.kadai.task.api.TaskCustomField.CUSTOM_15;
import static io.kadai.task.api.TaskCustomField.CUSTOM_16;
import static io.kadai.task.api.TaskCustomField.CUSTOM_2;
import static io.kadai.task.api.TaskCustomField.CUSTOM_3;
import static io.kadai.task.api.TaskCustomField.CUSTOM_4;
import static io.kadai.task.api.TaskCustomField.CUSTOM_5;
import static io.kadai.task.api.TaskCustomField.CUSTOM_6;
import static io.kadai.task.api.TaskCustomField.CUSTOM_7;
import static io.kadai.task.api.TaskCustomField.CUSTOM_8;
import static io.kadai.task.api.TaskCustomField.CUSTOM_9;
import static org.assertj.core.api.Assertions.assertThat;

import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.models.ClassificationSummary;
import io.kadai.classification.rest.models.ClassificationSummaryRepresentationModel;
import io.kadai.rest.test.KadaiSpringBootTest;
import io.kadai.task.api.TaskCustomIntField;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.TaskState;
import io.kadai.task.api.models.AttachmentSummary;
import io.kadai.task.api.models.TaskSummary;
import io.kadai.task.internal.models.AttachmentSummaryImpl;
import io.kadai.task.internal.models.ObjectReferenceImpl;
import io.kadai.task.internal.models.TaskSummaryImpl;
import io.kadai.task.rest.models.AttachmentRepresentationModel;
import io.kadai.task.rest.models.AttachmentSummaryRepresentationModel;
import io.kadai.task.rest.models.ObjectReferenceRepresentationModel;
import io.kadai.task.rest.models.TaskRepresentationModel;
import io.kadai.task.rest.models.TaskSummaryRepresentationModel;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.models.WorkbasketSummary;
import io.kadai.workbasket.rest.models.WorkbasketSummaryRepresentationModel;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@KadaiSpringBootTest
class TaskSummaryRepresentationModelAssemblerTest {

  private final TaskService taskService;
  private final WorkbasketService workbasketService;
  private final ClassificationService classificationService;
  private final TaskSummaryRepresentationModelAssembler assembler;

  @Autowired
  TaskSummaryRepresentationModelAssemblerTest(
      TaskService taskService,
      TaskSummaryRepresentationModelAssembler assembler,
      WorkbasketService workbasketService,
      ClassificationService classificationService) {
    this.taskService = taskService;
    this.assembler = assembler;
    this.workbasketService = workbasketService;
    this.classificationService = classificationService;
  }

  static void testEquality(TaskSummary taskSummary, TaskSummaryRepresentationModel repModel) {
    assertThat(taskSummary).hasNoNullFieldsOrProperties();
    assertThat(repModel).hasNoNullFieldsOrProperties();
    assertThat(taskSummary.getId()).isEqualTo(repModel.getTaskId());
    assertThat(taskSummary.getExternalId()).isEqualTo(repModel.getExternalId());
    assertThat(taskSummary.getCreated()).isEqualTo(repModel.getCreated());
    assertThat(taskSummary.getClaimed()).isEqualTo(repModel.getClaimed());
    assertThat(taskSummary.getCompleted()).isEqualTo(repModel.getCompleted());
    assertThat(taskSummary.getModified()).isEqualTo(repModel.getModified());
    assertThat(taskSummary.getPlanned()).isEqualTo(repModel.getPlanned());
    assertThat(taskSummary.getReceived()).isEqualTo(repModel.getReceived());
    assertThat(taskSummary.getDue()).isEqualTo(repModel.getDue());
    assertThat(taskSummary.getName()).isEqualTo(repModel.getName());
    assertThat(taskSummary.getCreator()).isEqualTo(repModel.getCreator());
    assertThat(taskSummary.getNote()).isEqualTo(repModel.getNote());
    assertThat(taskSummary.getDescription()).isEqualTo(repModel.getDescription());
    assertThat(taskSummary.getPriority()).isEqualTo(repModel.getPriority());
    assertThat(taskSummary.getManualPriority()).isEqualTo(repModel.getManualPriority());
    assertThat(taskSummary.getState()).isEqualTo(repModel.getState());
    assertThat(taskSummary.getNumberOfComments()).isEqualTo(repModel.getNumberOfComments());
    assertThat(taskSummary.getClassificationSummary().getId())
        .isEqualTo(repModel.getClassificationSummary().getClassificationId());
    assertThat(taskSummary.getWorkbasketSummary().getId())
        .isEqualTo(repModel.getWorkbasketSummary().getWorkbasketId());
    assertThat(taskSummary.getBusinessProcessId()).isEqualTo(repModel.getBusinessProcessId());
    assertThat(taskSummary.getParentBusinessProcessId())
        .isEqualTo(repModel.getParentBusinessProcessId());
    assertThat(taskSummary.getOwner()).isEqualTo(repModel.getOwner());
    assertThat(taskSummary.getOwnerLongName()).isEqualTo(repModel.getOwnerLongName());
    ObjectReferenceRepresentationModelAssemblerTest.testEquality(
        taskSummary.getPrimaryObjRef(), repModel.getPrimaryObjRef());
    assertThat(taskSummary.isRead()).isEqualTo(repModel.isRead());
    assertThat(taskSummary.isTransferred()).isEqualTo(repModel.isTransferred());
    assertThat(taskSummary.isReopened()).isEqualTo(repModel.isReopened());
    assertThat(taskSummary.getGroupByCount()).isEqualTo(repModel.getGroupByCount());
    assertThat(taskSummary.getCustomField(CUSTOM_1)).isEqualTo(repModel.getCustom1());
    assertThat(taskSummary.getCustomField(CUSTOM_2)).isEqualTo(repModel.getCustom2());
    assertThat(taskSummary.getCustomField(CUSTOM_3)).isEqualTo(repModel.getCustom3());
    assertThat(taskSummary.getCustomField(CUSTOM_4)).isEqualTo(repModel.getCustom4());
    assertThat(taskSummary.getCustomField(CUSTOM_5)).isEqualTo(repModel.getCustom5());
    assertThat(taskSummary.getCustomField(CUSTOM_6)).isEqualTo(repModel.getCustom6());
    assertThat(taskSummary.getCustomField(CUSTOM_7)).isEqualTo(repModel.getCustom7());
    assertThat(taskSummary.getCustomField(CUSTOM_8)).isEqualTo(repModel.getCustom8());
    assertThat(taskSummary.getCustomField(CUSTOM_9)).isEqualTo(repModel.getCustom9());
    assertThat(taskSummary.getCustomField(CUSTOM_10)).isEqualTo(repModel.getCustom10());
    assertThat(taskSummary.getCustomField(CUSTOM_11)).isEqualTo(repModel.getCustom11());
    assertThat(taskSummary.getCustomField(CUSTOM_12)).isEqualTo(repModel.getCustom12());
    assertThat(taskSummary.getCustomField(CUSTOM_13)).isEqualTo(repModel.getCustom13());
    assertThat(taskSummary.getCustomField(CUSTOM_14)).isEqualTo(repModel.getCustom14());
    assertThat(taskSummary.getCustomField(CUSTOM_15)).isEqualTo(repModel.getCustom15());
    assertThat(taskSummary.getCustomField(CUSTOM_16)).isEqualTo(repModel.getCustom16());
    assertThat(taskSummary.getCustomIntField(TaskCustomIntField.CUSTOM_INT_1))
        .isEqualTo(repModel.getCustomInt1());
    assertThat(taskSummary.getCustomIntField(TaskCustomIntField.CUSTOM_INT_2))
        .isEqualTo(repModel.getCustomInt2());
    assertThat(taskSummary.getCustomIntField(TaskCustomIntField.CUSTOM_INT_3))
        .isEqualTo(repModel.getCustomInt3());
    assertThat(taskSummary.getCustomIntField(TaskCustomIntField.CUSTOM_INT_4))
        .isEqualTo(repModel.getCustomInt4());
    assertThat(taskSummary.getCustomIntField(TaskCustomIntField.CUSTOM_INT_5))
        .isEqualTo(repModel.getCustomInt5());
    assertThat(taskSummary.getCustomIntField(TaskCustomIntField.CUSTOM_INT_6))
        .isEqualTo(repModel.getCustomInt6());
    assertThat(taskSummary.getCustomIntField(TaskCustomIntField.CUSTOM_INT_7))
        .isEqualTo(repModel.getCustomInt7());
    assertThat(taskSummary.getCustomIntField(TaskCustomIntField.CUSTOM_INT_8))
        .isEqualTo(repModel.getCustomInt8());
    testEqualityAttachments(
        taskSummary.getAttachmentSummaries(), repModel.getAttachmentSummaries());
  }

  private static void testEqualityAttachments(
      List<AttachmentSummary> attachmentSummaries,
      List<AttachmentSummaryRepresentationModel> resources) {
    assertThat(attachmentSummaries).hasSameSizeAs(resources);

    for (int i = 0; i < resources.size(); ++i) {
      AttachmentSummaryRepresentationModel resource = resources.get(i);
      AttachmentSummary attachmentSummary = attachmentSummaries.get(i);
      assertThat(attachmentSummary.getId()).isEqualTo(resource.getAttachmentId());
    }
  }

  @Test
  void should_ReturnRepresentationModel_When_ConvertingEntityToRepresentationModel() {
    ObjectReferenceImpl primaryObjRef = new ObjectReferenceImpl();
    primaryObjRef.setId("abc");
    ClassificationSummary classification =
        this.classificationService.newClassification("ckey", "cdomain", "MANUAL").asSummary();
    AttachmentSummaryImpl attachment =
        (AttachmentSummaryImpl) this.taskService.newAttachment().asSummary();
    attachment.setClassificationSummary(classification);
    attachment.setId("attachmentId");
    attachment.setObjectReference(primaryObjRef);
    final WorkbasketSummary workbasket =
        this.workbasketService.newWorkbasket("key", "domain").asSummary();
    TaskSummaryImpl task = (TaskSummaryImpl) this.taskService.newTask().asSummary();
    task.setAttachmentSummaries(List.of(attachment));
    task.setClassificationSummary(classification);
    task.setWorkbasketSummary(workbasket);
    task.setId("taskId");
    task.setExternalId("externalId");
    task.setCreated(Instant.parse("2019-09-13T08:44:17.588Z"));
    task.setClaimed(Instant.parse("2019-09-13T08:44:17.588Z"));
    task.setCompleted(Instant.parse("2019-09-13T08:44:17.588Z"));
    task.setModified(Instant.parse("2019-09-13T08:44:17.588Z"));
    task.setPlanned(Instant.parse("2019-09-13T08:44:17.588Z"));
    task.setReceived(Instant.parse("2019-09-13T08:44:17.588Z"));
    task.setDue(Instant.parse("2019-09-13T08:44:17.588Z"));
    task.setName("name");
    task.setCreator("creator");
    task.setDescription("desc");
    task.setNote("note");
    task.setPriority(123);
    task.setManualPriority(-1);
    task.setState(TaskState.READY);
    task.setNumberOfComments(2);
    task.setBusinessProcessId("businessProcessId");
    task.setParentBusinessProcessId("parentBusinessProcessId");
    task.setOwner("owner");
    task.setOwnerLongName("ownerLongName");
    task.setPrimaryObjRef(primaryObjRef);
    task.setRead(true);
    task.setTransferred(true);
    task.setReopened(true);
    task.setGroupByCount(0);
    task.setCustom1("custom1");
    task.setCustom2("custom2");
    task.setCustom3("custom3");
    task.setCustom4("custom4");
    task.setCustom5("custom5");
    task.setCustom6("custom6");
    task.setCustom7("custom7");
    task.setCustom8("custom8");
    task.setCustom9("custom9");
    task.setCustom10("custom10");
    task.setCustom11("custom11");
    task.setCustom12("custom12");
    task.setCustom13("custom13");
    task.setCustom14("custom14");
    task.setCustom15("custom15");
    task.setCustom16("custom16");
    task.setCustomInt1(1);
    task.setCustomInt2(2);
    task.setCustomInt3(3);
    task.setCustomInt4(4);
    task.setCustomInt5(5);
    task.setCustomInt6(6);
    task.setCustomInt7(7);
    task.setCustomInt8(8);

    TaskSummaryRepresentationModel repModel = assembler.toModel(task);

    testEquality(task, repModel);
  }

  @Test
  void should_ReturnEntity_When_ConvertingRepresentationModelToEntity() {
    ObjectReferenceRepresentationModel primaryObjRef = new ObjectReferenceRepresentationModel();
    primaryObjRef.setId("abc");
    WorkbasketSummaryRepresentationModel workbasketResource =
        new WorkbasketSummaryRepresentationModel();
    workbasketResource.setWorkbasketId("workbasketId");
    ClassificationSummaryRepresentationModel classificationSummary =
        new ClassificationSummaryRepresentationModel();
    classificationSummary.setKey("keyabc");
    classificationSummary.setDomain("DOMAIN_A");
    classificationSummary.setType("MANUAL");
    AttachmentRepresentationModel attachment = new AttachmentRepresentationModel();
    attachment.setClassificationSummary(classificationSummary);
    attachment.setAttachmentId("attachmentId");
    attachment.setObjectReference(primaryObjRef);
    TaskSummaryRepresentationModel repModel = new TaskRepresentationModel();
    repModel.setAttachmentSummaries(List.of(attachment));
    repModel.setClassificationSummary(classificationSummary);
    repModel.setWorkbasketSummary(workbasketResource);
    repModel.setPrimaryObjRef(primaryObjRef);
    repModel.setTaskId("taskId");
    repModel.setExternalId("externalId");
    repModel.setCreated(Instant.parse("2019-09-13T08:44:17.588Z"));
    repModel.setClaimed(Instant.parse("2019-09-13T08:44:17.588Z"));
    repModel.setCompleted(Instant.parse("2019-09-13T08:44:17.588Z"));
    repModel.setModified(Instant.parse("2019-09-13T08:44:17.588Z"));
    repModel.setPlanned(Instant.parse("2019-09-13T08:44:17.588Z"));
    repModel.setReceived(Instant.parse("2019-09-13T08:44:17.588Z"));
    repModel.setDue(Instant.parse("2019-09-13T08:44:17.588Z"));
    repModel.setName("name");
    repModel.setCreator("creator");
    repModel.setDescription("desc");
    repModel.setNote("note");
    repModel.setPriority(123);
    repModel.setManualPriority(123);
    repModel.setState(TaskState.READY);
    repModel.setNumberOfComments(2);
    repModel.setBusinessProcessId("businessProcessId");
    repModel.setParentBusinessProcessId("parentBusinessProcessId");
    repModel.setOwner("owner");
    repModel.setOwnerLongName("ownerLongName");
    repModel.setRead(true);
    repModel.setTransferred(true);
    repModel.setReopened(true);
    repModel.setGroupByCount(0);
    repModel.setCustom1("custom1");
    repModel.setCustom2("custom2");
    repModel.setCustom3("custom3");
    repModel.setCustom4("custom4");
    repModel.setCustom5("custom5");
    repModel.setCustom6("custom6");
    repModel.setCustom7("custom7");
    repModel.setCustom8("custom8");
    repModel.setCustom9("custom9");
    repModel.setCustom10("custom10");
    repModel.setCustom11("custom11");
    repModel.setCustom12("custom12");
    repModel.setCustom13("custom13");
    repModel.setCustom14("custom14");
    repModel.setCustom15("custom15");
    repModel.setCustom16("custom16");
    repModel.setCustomInt1(1);
    repModel.setCustomInt2(2);
    repModel.setCustomInt3(3);
    repModel.setCustomInt4(4);
    repModel.setCustomInt5(5);
    repModel.setCustomInt6(6);
    repModel.setCustomInt7(7);
    repModel.setCustomInt8(8);
    // when
    TaskSummary task = assembler.toEntityModel(repModel);
    // then
    testEquality(task, repModel);
  }

  @Test
  void should_ReturnEntity_When_ConvertingRepresentationModelWithoutWorkbasketSummaryToEntity() {
    // given
    ObjectReferenceRepresentationModel primaryObjRef = new ObjectReferenceRepresentationModel();
    primaryObjRef.setId("abc");
    ClassificationSummaryRepresentationModel classificationSummary =
        new ClassificationSummaryRepresentationModel();
    classificationSummary.setKey("keyabc");
    classificationSummary.setDomain("DOMAIN_A");
    classificationSummary.setType("MANUAL");
    AttachmentRepresentationModel attachment = new AttachmentRepresentationModel();
    attachment.setClassificationSummary(classificationSummary);
    attachment.setAttachmentId("attachmentId");
    attachment.setObjectReference(primaryObjRef);
    TaskRepresentationModel repModel = new TaskRepresentationModel();
    repModel.setTaskId("taskId");
    repModel.setExternalId("externalId");
    repModel.setClassificationSummary(classificationSummary);
    repModel.setPrimaryObjRef(primaryObjRef);
    // when
    TaskSummary taskSummary = assembler.toEntityModel(repModel);
    // then
    assertThat(repModel.getWorkbasketSummary()).isNull();
    assertThat(taskSummary.getWorkbasketSummary())
        .isNotNull()
        .hasAllNullFieldsOrPropertiesExcept(
            "markedForDeletion", "custom1", "custom2", "custom3", "custom4");
  }

  @Test
  void should_Equal_When_ComparingEntityWithConvertedEntity() {
    // given
    ObjectReferenceImpl primaryObjRef = new ObjectReferenceImpl();
    primaryObjRef.setId("abc");
    final WorkbasketSummary workbasket =
        workbasketService.newWorkbasket("key", "domain").asSummary();
    ClassificationSummary classification =
        classificationService.newClassification("ckey", "cdomain", "MANUAL").asSummary();
    AttachmentSummaryImpl attachment =
        (AttachmentSummaryImpl) taskService.newAttachment().asSummary();
    attachment.setClassificationSummary(classification);
    attachment.setId("attachmentId");
    TaskSummaryImpl task = (TaskSummaryImpl) taskService.newTask().asSummary();
    task.setId("taskId");
    task.setExternalId("externalId");
    task.setCreated(Instant.parse("2019-09-13T08:44:17.588Z"));
    task.setClaimed(Instant.parse("2019-09-13T08:44:17.588Z"));
    task.setCompleted(Instant.parse("2019-09-13T08:44:17.588Z"));
    task.setModified(Instant.parse("2019-09-13T08:44:17.588Z"));
    task.setPlanned(Instant.parse("2019-09-13T08:44:17.588Z"));
    task.setReceived(Instant.parse("2019-09-13T08:44:17.588Z"));
    task.setDue(Instant.parse("2019-09-13T08:44:17.588Z"));
    task.setName("name");
    task.setCreator("creator");
    task.setDescription("desc");
    task.setNote("note");
    task.setPriority(123);
    task.setManualPriority(-5);
    task.setState(TaskState.READY);
    task.setNumberOfComments(2);
    task.setClassificationSummary(classification);
    task.setWorkbasketSummary(workbasket);
    task.setBusinessProcessId("businessProcessId");
    task.setParentBusinessProcessId("parentBusinessProcessId");
    task.setOwner("owner");
    task.setOwnerLongName("ownerLongName");
    task.setPrimaryObjRef(primaryObjRef);
    task.setRead(true);
    task.setTransferred(true);
    task.setReopened(true);
    task.setGroupByCount(0);
    task.setCustom1("custom1");
    task.setCustom2("custom2");
    task.setCustom3("custom3");
    task.setCustom4("custom4");
    task.setCustom5("custom5");
    task.setCustom6("custom6");
    task.setCustom7("custom7");
    task.setCustom8("custom8");
    task.setCustom9("custom9");
    task.setCustom10("custom10");
    task.setCustom11("custom11");
    task.setCustom12("custom12");
    task.setCustom13("custom13");
    task.setCustom14("custom14");
    task.setCustom15("custom15");
    task.setCustom16("custom16");
    task.setCustomInt1(1);
    task.setCustomInt2(2);
    task.setCustomInt3(3);
    task.setCustomInt4(4);
    task.setCustomInt5(5);
    task.setCustomInt6(6);
    task.setCustomInt7(7);
    task.setCustomInt8(8);
    // when
    TaskSummaryRepresentationModel repModel = assembler.toModel(task);
    TaskSummary task2 = assembler.toEntityModel(repModel);
    // then
    assertThat(task).hasNoNullFieldsOrProperties().isNotSameAs(task2).isEqualTo(task2);
  }
}
