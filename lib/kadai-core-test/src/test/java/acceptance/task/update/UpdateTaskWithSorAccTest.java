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

package acceptance.task.update;

import static io.kadai.testapi.DefaultTestEntities.defaultTestClassification;
import static io.kadai.testapi.DefaultTestEntities.defaultTestObjectReference;
import static io.kadai.testapi.DefaultTestEntities.defaultTestWorkbasket;
import static org.assertj.core.api.Assertions.assertThat;

import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.models.ClassificationSummary;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.models.ObjectReference;
import io.kadai.task.api.models.Task;
import io.kadai.testapi.KadaiInject;
import io.kadai.testapi.KadaiIntegrationTest;
import io.kadai.testapi.builder.ObjectReferenceBuilder;
import io.kadai.testapi.builder.TaskBuilder;
import io.kadai.testapi.builder.WorkbasketAccessItemBuilder;
import io.kadai.testapi.security.WithAccessId;
import io.kadai.workbasket.api.WorkbasketPermission;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.models.WorkbasketSummary;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/** Acceptance test for "update task" scenarios that involve secondary {@link ObjectReference}s. */
@KadaiIntegrationTest
class UpdateTaskWithSorAccTest {

  @KadaiInject TaskService taskService;
  @KadaiInject WorkbasketService workbasketService;
  @KadaiInject ClassificationService classificationService;

  ClassificationSummary defaultClassificationSummary;
  WorkbasketSummary defaultWorkbasketSummary;
  ObjectReference defaultObjectReference;

  @WithAccessId(user = "businessadmin")
  @BeforeAll
  void setup() throws Exception {
    defaultClassificationSummary =
        defaultTestClassification().buildAndStoreAsSummary(classificationService);
    defaultWorkbasketSummary = defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary.getId())
        .accessId("user-1-1")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.EDITTASKS)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);
    defaultObjectReference = defaultTestObjectReference().build();
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_NotChangeSor_When_UpdateWithoutChanges() throws Exception {
    Task task =
        createDefaultTask()
            .objectReferences(
                defaultSecondaryObjectReference("0"), defaultSecondaryObjectReference("1"))
            .buildAndStore(taskService);
    taskService.updateTask(task);
    Task result = taskService.getTask(task.getId());

    assertThat(result.getSecondaryObjectReferences())
        .extracting(ObjectReference::getType)
        .containsExactlyInAnyOrder("Type0", "Type1");

    assertThat(result.getSecondaryObjectReferences())
        .extracting(ObjectReference::getValue)
        .containsExactlyInAnyOrder("Value0", "Value1");

    assertThat(result.getSecondaryObjectReferences())
        .extracting(ObjectReference::getCompany)
        .containsExactlyInAnyOrder("Company0", "Company1");
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_UpdateExistingSor_When_SorChangedInUpdatedTask() throws Exception {
    Task task =
        createDefaultTask()
            .objectReferences(
                defaultSecondaryObjectReference("0"), defaultSecondaryObjectReference("1"))
            .buildAndStore(taskService);

    ObjectReference sorToUpdate = task.getSecondaryObjectReferences().get(0);
    sorToUpdate.setType("NewType");

    taskService.updateTask(task);
    Task result = taskService.getTask(task.getId());

    assertThat(result.getSecondaryObjectReferences())
        .extracting(ObjectReference::getType)
        .containsExactlyInAnyOrder("NewType", "Type1");
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_AddNewSor_When_UpdatedTaskContainsNewSor() throws Exception {
    Task task =
        createDefaultTask()
            .objectReferences(
                defaultSecondaryObjectReference("0"), defaultSecondaryObjectReference("1"))
            .buildAndStore(taskService);
    task.addSecondaryObjectReference("NewCompany", null, null, "NewType", "NewValue");
    taskService.updateTask(task);
    Task result = taskService.getTask(task.getId());

    assertThat(result.getSecondaryObjectReferences())
        .extracting(ObjectReference::getType)
        .containsExactlyInAnyOrder("Type0", "Type1", "NewType");

    assertThat(result.getSecondaryObjectReferences())
        .extracting(ObjectReference::getValue)
        .containsExactlyInAnyOrder("Value0", "Value1", "NewValue");

    assertThat(result.getSecondaryObjectReferences())
        .extracting(ObjectReference::getCompany)
        .containsExactlyInAnyOrder("Company0", "Company1", "NewCompany");
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_DeleteOneSor_When_UpdatedTaskContainsOneLessSor() throws Exception {
    Task task =
        createDefaultTask()
            .objectReferences(
                defaultSecondaryObjectReference("0"), defaultSecondaryObjectReference("1"))
            .buildAndStore(taskService);
    task.removeSecondaryObjectReference(task.getSecondaryObjectReferences().get(0).getId());
    taskService.updateTask(task);
    Task result = taskService.getTask(task.getId());

    assertThat(result.getSecondaryObjectReferences()).hasSize(1);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_DeleteAllSor_When_UpdatedTaskContainsNoSor() throws Exception {
    Task task =
        createDefaultTask()
            .objectReferences(
                defaultSecondaryObjectReference("0"), defaultSecondaryObjectReference("1"))
            .buildAndStore(taskService);
    task.removeSecondaryObjectReference(task.getSecondaryObjectReferences().get(0).getId());
    task.removeSecondaryObjectReference(task.getSecondaryObjectReferences().get(0).getId());
    taskService.updateTask(task);
    Task result = taskService.getTask(task.getId());

    assertThat(result.getSecondaryObjectReferences()).isEmpty();
  }

  private TaskBuilder createDefaultTask() {
    return (TaskBuilder.newTask()
        .classificationSummary(defaultClassificationSummary)
        .workbasketSummary(defaultWorkbasketSummary)
        .primaryObjRef(defaultObjectReference));
  }

  private ObjectReference defaultSecondaryObjectReference(String suffix) {
    return ObjectReferenceBuilder.newObjectReference()
        .company("Company" + suffix)
        .value("Value" + suffix)
        .type("Type" + suffix)
        .build();
  }
}
