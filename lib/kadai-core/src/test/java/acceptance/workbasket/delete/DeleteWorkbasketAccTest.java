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

package acceptance.workbasket.delete;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.common.api.exceptions.KadaiException;
import io.kadai.common.test.security.JaasExtension;
import io.kadai.common.test.security.WithAccessId;
import io.kadai.task.internal.models.TaskImpl;
import io.kadai.workbasket.api.WorkbasketPermission;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.exceptions.NotAuthorizedOnWorkbasketException;
import io.kadai.workbasket.api.exceptions.WorkbasketInUseException;
import io.kadai.workbasket.api.exceptions.WorkbasketNotFoundException;
import io.kadai.workbasket.api.models.Workbasket;
import io.kadai.workbasket.api.models.WorkbasketAccessItem;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

/** Acceptance test which does test the deletion of a workbasket and all wanted failures. */
@ExtendWith(JaasExtension.class)
class DeleteWorkbasketAccTest extends AbstractAccTest {

  private WorkbasketService workbasketService;

  @BeforeEach
  void setUpMethod() {
    workbasketService = kadaiEngine.getWorkbasketService();
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_ThrowWorkbasketNotFoundException_When_TheWorkbasketHasAlreadyBeenDeleted()
      throws Exception {
    Workbasket wb = workbasketService.getWorkbasket("USER-2-2", "DOMAIN_A");

    ThrowingCallable call =
        () -> {
          workbasketService.deleteWorkbasket(wb.getId());
          workbasketService.getWorkbasket("USER-2-2", "DOMAIN_A");
        };
    assertThatThrownBy(call)
        .describedAs("There should be no result for a deleted Workbasket.")
        .isInstanceOf(WorkbasketNotFoundException.class);
  }

  @WithAccessId(user = "user-1-1")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_ThrowException_When_UserRoleIsNotAdminOrBusinessAdmin() {

    ThrowingCallable deleteWorkbasketCall =
        () -> {
          Workbasket wb = workbasketService.getWorkbasket("TEAMLEAD-2", "DOMAIN_A");
          workbasketService.deleteWorkbasket(wb.getId());
        };
    assertThatThrownBy(deleteWorkbasketCall).isInstanceOf(KadaiException.class);

    deleteWorkbasketCall =
        () -> {
          Workbasket wb =
              workbasketService.getWorkbasket("WBI:100000000000000000000000000000000005");
          workbasketService.deleteWorkbasket(wb.getId());
        };
    assertThatThrownBy(deleteWorkbasketCall).isInstanceOf(KadaiException.class);
  }

  @Test
  void should_ThrowNotAuthorizedException_When_UnauthorizedTryingToGetWorkbaskets() {
    assertThatThrownBy(() -> workbasketService.getWorkbasket("TEAMLEAD-2", "DOMAIN_A"))
        .isInstanceOf(NotAuthorizedOnWorkbasketException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_RemoveWorkbasketsFromDistributionTargets_When_WorkbasketIsDeleted() throws Exception {
    Workbasket wb = workbasketService.getWorkbasket("GPK_KSC_1", "DOMAIN_A");
    int distTargets =
        workbasketService
            .getDistributionTargets("GPK_KSC", "DOMAIN_A")
            .size(); // has GPK_KSC_1 as a distribution target (+ 3 other Workbaskets)

    ThrowingCallable call =
        () -> {
          // WB deleted
          workbasketService.deleteWorkbasket(wb.getId());
          workbasketService.getWorkbasket("GPK_KSC_1", "DOMAIN_A");
        };
    assertThatThrownBy(call)
        .describedAs("There should be no result for a deleted Workbasket.")
        .isInstanceOf(WorkbasketNotFoundException.class);

    int newDistTargets = workbasketService.getDistributionTargets("GPK_KSC", "DOMAIN_A").size();
    assertThat(newDistTargets).isEqualTo(3).isLessThan(distTargets);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_ThrowInvalidArgumentException_When_TryingToDeleteNullOrEmptyWorkbasket() {
    // Test Null-Value
    assertThatThrownBy(() -> workbasketService.deleteWorkbasket(null))
        .describedAs(
            "delete() should have thrown an InvalidArgumentException, "
                + "when the param ID is null.")
        .isInstanceOf(InvalidArgumentException.class);

    // Test EMPTY-Value
    assertThatThrownBy(() -> workbasketService.deleteWorkbasket(""))
        .describedAs(
            "delete() should have thrown an InvalidArgumentException, \"\n"
                + "            + \"when the param ID is EMPTY-String.")
        .isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_ThrowWorkbasketNotFoundException_When_TryingToDeleteNonExistingWorkbasket() {
    assertThatThrownBy(() -> workbasketService.deleteWorkbasket("SOME NOT EXISTING ID"))
        .isInstanceOf(WorkbasketNotFoundException.class);
  }

  @WithAccessId(user = "user-1-2", groups = "businessadmin")
  @Test
  void should_ThrowWorkbasketInUseException_When_TryingToDeleteWorkbasketWhichIsInUse()
      throws Exception {
    Workbasket wb =
        workbasketService.getWorkbasket("user-1-2", "DOMAIN_A"); // all rights, DOMAIN_A with Tasks
    assertThatThrownBy(() -> workbasketService.deleteWorkbasket(wb.getId()))
        .isInstanceOf(WorkbasketInUseException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_DeleteWorkbasketAccessItems_When_WorkbasketIsDeleted() throws Exception {
    Workbasket wb = workbasketService.getWorkbasket("WBI:100000000000000000000000000000000008");
    String wbId = wb.getId();
    // create 2 access Items
    WorkbasketAccessItem accessItem = workbasketService.newWorkbasketAccessItem(wbId, "TEAMLEAD-2");
    accessItem.setPermission(WorkbasketPermission.APPEND, true);
    accessItem.setPermission(WorkbasketPermission.READ, true);
    accessItem.setPermission(WorkbasketPermission.OPEN, true);
    workbasketService.createWorkbasketAccessItem(accessItem);
    accessItem = workbasketService.newWorkbasketAccessItem(wbId, "elena");
    accessItem.setPermission(WorkbasketPermission.APPEND, true);
    accessItem.setPermission(WorkbasketPermission.READ, true);
    accessItem.setPermission(WorkbasketPermission.OPEN, true);
    workbasketService.createWorkbasketAccessItem(accessItem);
    List<WorkbasketAccessItem> accessItemsBefore = workbasketService.getWorkbasketAccessItems(wbId);
    assertThat(accessItemsBefore).hasSize(5);

    ThrowingCallable call =
        () -> {
          workbasketService.deleteWorkbasket(wbId);
          workbasketService.getWorkbasket("WBI:100000000000000000000000000000000008");
        };
    assertThatThrownBy(call)
        .describedAs("There should be no result for a deleted Workbasket.")
        .isInstanceOf(WorkbasketNotFoundException.class);

    List<WorkbasketAccessItem> accessItemsAfter = workbasketService.getWorkbasketAccessItems(wbId);
    assertThat(accessItemsAfter).isEmpty();
  }

  @WithAccessId(user = "admin")
  @Test
  void should_MarkWorkbasketForDeletion_When_TryingToDeleteWorkbasketWithTasks() throws Exception {
    final Workbasket wb =
        workbasketService.getWorkbasket("WBI:100000000000000000000000000000000006");

    TaskImpl task = (TaskImpl) taskService.getTask("TKI:000000000000000000000000000000000000");
    taskService.forceCompleteTask(task.getId());
    task = (TaskImpl) taskService.getTask("TKI:000000000000000000000000000000000001");
    taskService.forceCompleteTask(task.getId());
    task = (TaskImpl) taskService.getTask("TKI:000000000000000000000000000000000002");
    taskService.forceCompleteTask(task.getId());
    task = (TaskImpl) taskService.getTask("TKI:000000000000000000000000000000000066");
    taskService.forceCompleteTask(task.getId());
    task = (TaskImpl) taskService.getTask("TKI:100000000000000000000000000000000066");
    taskService.forceCompleteTask(task.getId());
    task = (TaskImpl) taskService.getTask("TKI:200000000000000000000000000000000066");
    taskService.forceCompleteTask(task.getId());
    task = (TaskImpl) taskService.getTask("TKI:000000000000000000000000000000000201");
    taskService.forceCompleteTask(task.getId());

    boolean canBeDeletedNow = workbasketService.deleteWorkbasket(wb.getId());
    assertThat(canBeDeletedNow).isFalse();

    Workbasket wb2 = workbasketService.getWorkbasket(wb.getId());
    assertThat(wb2.isMarkedForDeletion()).isTrue();
  }
}
