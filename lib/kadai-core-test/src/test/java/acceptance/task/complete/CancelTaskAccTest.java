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

package acceptance.task.complete;

import static io.kadai.testapi.DefaultTestEntities.defaultTestClassification;
import static io.kadai.testapi.DefaultTestEntities.defaultTestObjectReference;
import static io.kadai.testapi.DefaultTestEntities.defaultTestWorkbasket;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import acceptance.task.complete.CompleteTaskWithSpiAccTest.SetCustomAttributeToEndstate;
import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.models.ClassificationSummary;
import io.kadai.common.internal.util.Triplet;
import io.kadai.spi.task.api.TaskEndstatePreprocessor;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.TaskState;
import io.kadai.task.api.exceptions.InvalidTaskStateException;
import io.kadai.task.api.models.ObjectReference;
import io.kadai.task.api.models.Task;
import io.kadai.testapi.DefaultTestEntities;
import io.kadai.testapi.KadaiInject;
import io.kadai.testapi.KadaiIntegrationTest;
import io.kadai.testapi.WithServiceProvider;
import io.kadai.testapi.builder.TaskBuilder;
import io.kadai.testapi.builder.WorkbasketAccessItemBuilder;
import io.kadai.testapi.security.WithAccessId;
import io.kadai.user.api.UserService;
import io.kadai.workbasket.api.WorkbasketPermission;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.exceptions.NotAuthorizedOnWorkbasketException;
import io.kadai.workbasket.api.models.WorkbasketSummary;
import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.function.ThrowingConsumer;

@KadaiIntegrationTest
class CancelTaskAccTest {
  @KadaiInject TaskService taskService;
  @KadaiInject ClassificationService classificationService;
  @KadaiInject WorkbasketService workbasketService;
  @KadaiInject UserService userService;

  ClassificationSummary defaultClassificationSummary;
  WorkbasketSummary defaultWorkbasketSummary;
  ObjectReference defaultObjectReference;

  @WithAccessId(user = "admin")
  @BeforeAll
  void setup() throws Exception {
    defaultClassificationSummary =
        defaultTestClassification().buildAndStoreAsSummary(classificationService);
    defaultWorkbasketSummary = defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary.getId())
        .accessId("user-1-2")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);

    defaultObjectReference = defaultTestObjectReference().build();
  }

  @WithAccessId(user = "admin")
  @Test
  void should_CancelReadyTask() throws Exception {
    Task task =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .state(TaskState.READY)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .buildAndStore(taskService);

    taskService.cancelTask(task.getId());

    Task readTask = taskService.getTask(task.getId());
    assertThat(readTask.getState()).isEqualTo(TaskState.CANCELLED);
  }

  @WithAccessId(user = "admin")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_CancelTask_When_NoExplicitPermissionsButUserIsInAdministrativeRole()
      throws Exception {
    Task taskToCancel =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .state(TaskState.CLAIMED)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .buildAndStore(taskService);

    Task cancelledTask = taskService.cancelTask(taskToCancel.getId());

    assertThat(cancelledTask.getState()).isEqualTo(TaskState.CANCELLED);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_CancelClaimedTask() throws Exception {
    Task claimedTask =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .state(TaskState.CLAIMED)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .buildAndStore(taskService);

    Task cancelledTask = taskService.cancelTask(claimedTask.getId());

    assertThat(cancelledTask.getState()).isEqualTo(TaskState.CANCELLED);
  }

  @WithAccessId(user = "user-taskrouter")
  @Test
  void should_ThrowException_When_UserNotAuthorized() throws Exception {
    Task task =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .state(TaskState.CLAIMED)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .buildAndStore(taskService, "admin");

    ThrowingCallable call = () -> taskService.cancelTask(task.getId());

    NotAuthorizedOnWorkbasketException e =
        catchThrowableOfType(NotAuthorizedOnWorkbasketException.class, call);
    assertThat(e.getRequiredPermissions())
        .containsExactly(WorkbasketPermission.READ, WorkbasketPermission.READTASKS);
    assertThat(e.getCurrentUserId()).isEqualTo("user-taskrouter");
    assertThat(e.getWorkbasketId()).isEqualTo(defaultWorkbasketSummary.getId());
  }

  @WithAccessId(user = "user-1-2")
  @TestFactory
  Stream<DynamicTest> should_ThrowException_When_CancellingATaskInEndState() throws Exception {
    Task completedTask =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .state(TaskState.COMPLETED)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .buildAndStore(taskService);
    Task terminatedTask =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .state(TaskState.TERMINATED)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .buildAndStore(taskService);
    Task cancelledTask =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .state(TaskState.CANCELLED)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .buildAndStore(taskService);
    List<Triplet<String, Task, TaskState>> list =
        List.of(
            Triplet.of("When Cancelling Completed Task", completedTask, TaskState.COMPLETED),
            Triplet.of("When Cancelling Terminated Task", terminatedTask, TaskState.TERMINATED),
            Triplet.of("When Cancelling Cancelled Task", cancelledTask, TaskState.CANCELLED));
    ThrowingConsumer<Triplet<String, Task, TaskState>> testCancelTask =
        t -> {
          ThrowingCallable call = () -> taskService.cancelTask(t.getMiddle().getId());

          InvalidTaskStateException e = catchThrowableOfType(InvalidTaskStateException.class, call);
          assertThat(e.getRequiredTaskStates())
              .containsExactlyInAnyOrder(
                  TaskState.READY,
                  TaskState.IN_REVIEW,
                  TaskState.READY_FOR_REVIEW,
                  TaskState.CLAIMED);
          assertThat(e.getTaskId()).isEqualTo(t.getMiddle().getId());
          assertThat(e.getTaskState()).isEqualTo(t.getRight());
        };
    return DynamicTest.stream(list.iterator(), Triplet::getLeft, testCancelTask);
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  @WithServiceProvider(
      serviceProviderInterface = TaskEndstatePreprocessor.class,
      serviceProviders = SetCustomAttributeToEndstate.class)
  class ServiceProviderSetsCustomAttributeToCancelled {

    @KadaiInject TaskService taskService;

    @WithAccessId(user = "user-1-2")
    @Test
    void should_SetCustomAttribute_When_UserCancelsTask() throws Exception {
      Task task =
          TaskBuilder.newTask()
              .classificationSummary(defaultClassificationSummary)
              .workbasketSummary(defaultWorkbasketSummary)
              .state(TaskState.CLAIMED)
              .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
              .buildAndStore(taskService);
      Task processedTask = taskService.cancelTask(task.getId());
      assertThat(processedTask.getState()).isEqualTo(TaskState.CANCELLED);
      assertThat(processedTask.getCustomAttributeMap())
          .containsEntry(
              "camunda:attribute1",
              "{\"valueInfo\":{\"objectTypeName\":\"java.lang.String\"},"
                  + "\"type\":\"String\",\"value\":\"CANCELLED\"}");
    }
  }
}
