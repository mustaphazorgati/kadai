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

package acceptance.task.get;

import static io.kadai.testapi.DefaultTestEntities.defaultTestClassification;
import static io.kadai.testapi.DefaultTestEntities.defaultTestObjectReference;
import static io.kadai.testapi.DefaultTestEntities.defaultTestWorkbasket;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowableOfType;

import io.kadai.KadaiConfiguration;
import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.models.ClassificationSummary;
import io.kadai.task.api.CallbackState;
import io.kadai.task.api.TaskCustomField;
import io.kadai.task.api.TaskCustomIntField;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.TaskState;
import io.kadai.task.api.exceptions.TaskNotFoundException;
import io.kadai.task.api.models.ObjectReference;
import io.kadai.task.api.models.Task;
import io.kadai.testapi.KadaiConfigurationModifier;
import io.kadai.testapi.KadaiInject;
import io.kadai.testapi.KadaiIntegrationTest;
import io.kadai.testapi.builder.TaskBuilder;
import io.kadai.testapi.builder.TaskCommentBuilder;
import io.kadai.testapi.builder.UserBuilder;
import io.kadai.testapi.builder.WorkbasketAccessItemBuilder;
import io.kadai.testapi.security.WithAccessId;
import io.kadai.user.api.UserService;
import io.kadai.workbasket.api.WorkbasketPermission;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.exceptions.NotAuthorizedOnWorkbasketException;
import io.kadai.workbasket.api.models.WorkbasketSummary;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestTemplate;

@KadaiIntegrationTest
class GetTaskAccTest {
  @KadaiInject TaskService taskService;
  @KadaiInject ClassificationService classificationService;
  @KadaiInject WorkbasketService workbasketService;
  @KadaiInject UserService userService;

  ClassificationSummary defaultClassificationSummary;
  WorkbasketSummary defaultWorkbasketSummary;
  WorkbasketSummary wbWithoutReadTasksPerm;
  WorkbasketSummary wbWithoutReadPerm;
  ObjectReference defaultObjectReference;
  Task task;
  Task task2;
  Task task3;
  Map<String, String> callbackInfo;

  @WithAccessId(user = "admin")
  @BeforeAll
  void setup() throws Exception {
    defaultClassificationSummary =
        defaultTestClassification().buildAndStoreAsSummary(classificationService);
    defaultWorkbasketSummary = defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);
    wbWithoutReadTasksPerm = defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);
    wbWithoutReadPerm = defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);
    defaultObjectReference = defaultTestObjectReference().build();
    callbackInfo = createSimpleCustomPropertyMap(3);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary.getId())
        .accessId("user-1-1")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);
    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(wbWithoutReadTasksPerm.getId())
        .accessId("user-1-1")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);
    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(wbWithoutReadPerm.getId())
        .accessId("user-1-1")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);

    UserBuilder.newUser()
        .id("user-1-1")
        .firstName("Max")
        .lastName("Mustermann")
        .longName("Long name of user-1-1")
        .buildAndStore(userService);

    task =
        TaskBuilder.newTask()
            .name("Task99")
            .description("Lorem ipsum was n Quatsch dolor sit amet.")
            .note("Some custom Note")
            .state(TaskState.CLAIMED)
            .businessProcessId("BPI21")
            .parentBusinessProcessId("PBPI21")
            .owner("user-1-1")
            .read(true)
            .transferred(false)
            .reopened(false)
            .callbackInfo(callbackInfo)
            .customAttribute(TaskCustomField.CUSTOM_1, "custom1")
            .customAttribute(TaskCustomField.CUSTOM_2, "custom2")
            .customAttribute(TaskCustomField.CUSTOM_3, "custom3")
            .customAttribute(TaskCustomField.CUSTOM_4, "custom4")
            .customAttribute(TaskCustomField.CUSTOM_5, "custom5")
            .customAttribute(TaskCustomField.CUSTOM_6, "custom6")
            .customAttribute(TaskCustomField.CUSTOM_7, "custom7")
            .customAttribute(TaskCustomField.CUSTOM_8, "custom8")
            .customAttribute(TaskCustomField.CUSTOM_9, "custom9")
            .customAttribute(TaskCustomField.CUSTOM_10, "custom10")
            .customAttribute(TaskCustomField.CUSTOM_11, "custom11")
            .customAttribute(TaskCustomField.CUSTOM_12, "custom12")
            .customAttribute(TaskCustomField.CUSTOM_13, "custom13")
            .customAttribute(TaskCustomField.CUSTOM_14, "abc")
            .customAttribute(TaskCustomField.CUSTOM_15, "custom15")
            .customAttribute(TaskCustomField.CUSTOM_16, "custom16")
            .customIntField(TaskCustomIntField.CUSTOM_INT_1, 1)
            .customIntField(TaskCustomIntField.CUSTOM_INT_2, 2)
            .customIntField(TaskCustomIntField.CUSTOM_INT_3, 3)
            .customIntField(TaskCustomIntField.CUSTOM_INT_4, 4)
            .customIntField(TaskCustomIntField.CUSTOM_INT_5, 5)
            .customIntField(TaskCustomIntField.CUSTOM_INT_6, 6)
            .customIntField(TaskCustomIntField.CUSTOM_INT_7, 7)
            .customIntField(TaskCustomIntField.CUSTOM_INT_8, 8)
            .callbackState(CallbackState.CLAIMED)
            .received(Instant.now())
            .claimed(Instant.now())
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .buildAndStore(taskService);

    task2 =
        TaskBuilder.newTask()
            .workbasketSummary(wbWithoutReadTasksPerm)
            .classificationSummary(defaultClassificationSummary)
            .primaryObjRef(defaultObjectReference)
            .buildAndStore(taskService);

    task3 =
        TaskBuilder.newTask()
            .workbasketSummary(wbWithoutReadPerm)
            .classificationSummary(defaultClassificationSummary)
            .primaryObjRef(defaultObjectReference)
            .buildAndStore(taskService);

    TaskCommentBuilder.newTaskComment()
        .taskId(task.getId())
        .textField("Text1")
        .created(Instant.now())
        .modified(Instant.now())
        .buildAndStore(taskService);

    TaskCommentBuilder.newTaskComment()
        .taskId(task.getId())
        .textField("Text1")
        .created(Instant.now())
        .modified(Instant.now())
        .buildAndStore(taskService);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ReturnTask_When_RequestingTaskByTaskId() throws Exception {
    Task readTask = taskService.getTask(task.getId());

    assertThat(readTask.getCompleted()).isNull();
    assertThat(readTask.getName()).isEqualTo("Task99");
    assertThat(readTask.getCreator()).isEqualTo("admin");
    assertThat(readTask.getDescription()).isEqualTo("Lorem ipsum was n Quatsch dolor sit amet.");
    assertThat(readTask.getNote()).isEqualTo("Some custom Note");
    assertThat(readTask.getPriority()).isZero();
    assertThat(readTask.getState()).isEqualTo(TaskState.CLAIMED);
    assertThat(readTask.getClassificationCategory())
        .isEqualTo(defaultClassificationSummary.getCategory());
    assertThat(readTask.getClassificationSummary().getKey())
        .isEqualTo(defaultClassificationSummary.getKey());
    assertThat(readTask.getClassificationSummary().getId())
        .isEqualTo(defaultClassificationSummary.getId());
    assertThat(readTask.getWorkbasketSummary().getId()).isEqualTo(defaultWorkbasketSummary.getId());
    assertThat(readTask.getWorkbasketKey()).isEqualTo(defaultWorkbasketSummary.getKey());
    assertThat(readTask.getDomain()).isEqualTo(defaultWorkbasketSummary.getDomain());
    assertThat(readTask.getBusinessProcessId()).isEqualTo("BPI21");
    assertThat(readTask.getParentBusinessProcessId()).isEqualTo("PBPI21");
    assertThat(readTask.getOwner()).isEqualTo("user-1-1");
    assertThat(readTask.getPrimaryObjRef().getCompany())
        .isEqualTo(defaultObjectReference.getCompany());
    assertThat(readTask.getPrimaryObjRef().getSystem())
        .isEqualTo(defaultObjectReference.getSystem());
    assertThat(readTask.getPrimaryObjRef().getSystemInstance())
        .isEqualTo(defaultObjectReference.getSystemInstance());
    assertThat(readTask.getPrimaryObjRef().getType()).isEqualTo(defaultObjectReference.getType());
    assertThat(readTask.getPrimaryObjRef().getValue()).isEqualTo(defaultObjectReference.getValue());
    assertThat(readTask.isRead()).isTrue();
    assertThat(readTask.isTransferred()).isFalse();
    assertThat(readTask.isReopened()).isFalse();
    assertThat(readTask.getNumberOfComments()).isEqualTo(2);
    assertThat(readTask.getCallbackInfo()).isEqualTo(callbackInfo);
    assertThat(readTask.getCustomAttributeMap()).isEqualTo(new HashMap<String, String>());
    assertThat(readTask.getCustomField(TaskCustomField.CUSTOM_1)).isEqualTo("custom1");
    assertThat(readTask.getCustomField(TaskCustomField.CUSTOM_2)).isEqualTo("custom2");
    assertThat(readTask.getCustomField(TaskCustomField.CUSTOM_3)).isEqualTo("custom3");
    assertThat(readTask.getCustomField(TaskCustomField.CUSTOM_4)).isEqualTo("custom4");
    assertThat(readTask.getCustomField(TaskCustomField.CUSTOM_5)).isEqualTo("custom5");
    assertThat(readTask.getCustomField(TaskCustomField.CUSTOM_6)).isEqualTo("custom6");
    assertThat(readTask.getCustomField(TaskCustomField.CUSTOM_7)).isEqualTo("custom7");
    assertThat(readTask.getCustomField(TaskCustomField.CUSTOM_8)).isEqualTo("custom8");
    assertThat(readTask.getCustomField(TaskCustomField.CUSTOM_9)).isEqualTo("custom9");
    assertThat(readTask.getCustomField(TaskCustomField.CUSTOM_10)).isEqualTo("custom10");
    assertThat(readTask.getCustomField(TaskCustomField.CUSTOM_11)).isEqualTo("custom11");
    assertThat(readTask.getCustomField(TaskCustomField.CUSTOM_12)).isEqualTo("custom12");
    assertThat(readTask.getCustomField(TaskCustomField.CUSTOM_13)).isEqualTo("custom13");
    assertThat(readTask.getCustomField(TaskCustomField.CUSTOM_14)).isEqualTo("abc");
    assertThat(readTask.getCustomField(TaskCustomField.CUSTOM_15)).isEqualTo("custom15");
    assertThat(readTask.getCustomField(TaskCustomField.CUSTOM_16)).isEqualTo("custom16");
    assertThatCode(() -> readTask.getCustomAttributeMap().put("X", "Y")).doesNotThrowAnyException();
    assertThatCode(() -> readTask.getCallbackInfo().put("X", "Y")).doesNotThrowAnyException();
    assertThat(readTask)
        .hasNoNullFieldsOrPropertiesExcept("ownerLongName", "completed", "groupByCount");
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_NoReadTasksPerm() {
    ThrowingCallable call = () -> taskService.getTask(task2.getId());

    NotAuthorizedOnWorkbasketException e =
        catchThrowableOfType(NotAuthorizedOnWorkbasketException.class, call);

    assertThat(e.getRequiredPermissions())
        .containsExactlyInAnyOrder(WorkbasketPermission.READ, WorkbasketPermission.READTASKS);
    assertThat(e.getCurrentUserId()).isEqualTo("user-1-1");
    assertThat(e.getWorkbasketId()).isEqualTo(wbWithoutReadTasksPerm.getId());
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_UserHasReadTasksButNoReadPerm() {
    ThrowingCallable call = () -> taskService.getTask(task3.getId());

    NotAuthorizedOnWorkbasketException e =
        catchThrowableOfType(NotAuthorizedOnWorkbasketException.class, call);

    assertThat(e.getRequiredPermissions())
        .containsExactlyInAnyOrder(WorkbasketPermission.READ, WorkbasketPermission.READTASKS);
    assertThat(e.getCurrentUserId()).isEqualTo("user-1-1");
    assertThat(e.getWorkbasketId()).isEqualTo(wbWithoutReadPerm.getId());
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_RequestedTaskByIdIsNotExisting() {
    ThrowingCallable call = () -> taskService.getTask("INVALID");

    TaskNotFoundException e = catchThrowableOfType(TaskNotFoundException.class, call);
    assertThat(e.getTaskId()).isEqualTo("INVALID");
  }

  @WithAccessId(user = "admin")
  @Test
  void should_NotSetTaskOwnerLongNameOfTask_When_PropertyDisabled() throws Exception {
    Task readTask = taskService.getTask(task.getId());

    assertThat(readTask).extracting(Task::getOwnerLongName).isNull();
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ThrowException_When_UserIsNotAuthorizedToGetTask() {
    ThrowingCallable getTaskCall = () -> taskService.getTask(task.getId());

    NotAuthorizedOnWorkbasketException e =
        catchThrowableOfType(NotAuthorizedOnWorkbasketException.class, getTaskCall);
    assertThat(e.getCurrentUserId()).isEqualTo("user-1-2");
    assertThat(e.getWorkbasketId()).isEqualTo(defaultWorkbasketSummary.getId());
    assertThat(e.getRequiredPermissions()).contains(WorkbasketPermission.READ);
  }

  @WithAccessId(user = "user-taskrouter")
  @Test
  void should_ThrowException_When_UserIsNotAuthorizedToGetTaskAndMemberOfTaskRouterRole() {
    ThrowingCallable call = () -> taskService.getTask(task.getId());

    NotAuthorizedOnWorkbasketException e =
        catchThrowableOfType(NotAuthorizedOnWorkbasketException.class, call);
    assertThat(e.getCurrentUserId()).isEqualTo("user-taskrouter");
    assertThat(e.getWorkbasketId()).isEqualTo(defaultWorkbasketSummary.getId());
    assertThat(e.getRequiredPermissions()).contains(WorkbasketPermission.READ);
  }

  @WithAccessId(user = "admin")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_ReturnTask_When_NoExplicitPermissionsButUserIsInAdministrativeRole()
      throws Exception {
    Task readTask = taskService.getTask(task.getId());
    assertThat(readTask).isNotNull();
  }

  private Map<String, String> createSimpleCustomPropertyMap(int propertiesCount) {
    return IntStream.rangeClosed(1, propertiesCount)
        .mapToObj(String::valueOf)
        .collect(Collectors.toMap("Property_"::concat, "Property Value of Property_"::concat));
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class WithAdditionalUserInfoEnabled implements KadaiConfigurationModifier {

    @KadaiInject TaskService taskService;

    @Override
    public KadaiConfiguration.Builder modify(KadaiConfiguration.Builder builder) {
      return builder.addAdditionalUserInfo(true);
    }

    @WithAccessId(user = "admin")
    @Test
    void should_SetTaskOwnerLongNameOfTask_When_PropertyEnabled() throws Exception {
      Task readTask = taskService.getTask(task.getId());
      assertThat(readTask).extracting(Task::getOwnerLongName).isEqualTo("Long name of user-1-1");
    }
  }
}
