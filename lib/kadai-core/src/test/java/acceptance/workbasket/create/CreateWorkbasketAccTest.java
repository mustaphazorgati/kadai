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

package acceptance.workbasket.create;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import io.kadai.common.api.exceptions.DomainNotFoundException;
import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.common.test.security.JaasExtension;
import io.kadai.common.test.security.WithAccessId;
import io.kadai.workbasket.api.WorkbasketCustomField;
import io.kadai.workbasket.api.WorkbasketPermission;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.WorkbasketType;
import io.kadai.workbasket.api.exceptions.WorkbasketAccessItemAlreadyExistException;
import io.kadai.workbasket.api.exceptions.WorkbasketAlreadyExistException;
import io.kadai.workbasket.api.models.Workbasket;
import io.kadai.workbasket.api.models.WorkbasketAccessItem;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

/** Acceptance test for all "create workbasket" scenarios. */
@ExtendWith(JaasExtension.class)
class CreateWorkbasketAccTest extends AbstractAccTest {

  CreateWorkbasketAccTest() {
    super();
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testCreateWorkbasket() throws Exception {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();
    final int before = workbasketService.createWorkbasketQuery().domainIn("DOMAIN_A").list().size();

    Workbasket workbasket = workbasketService.newWorkbasket("NT1234", "DOMAIN_A");
    workbasket.setName("Megabasket");
    workbasket.setType(WorkbasketType.GROUP);
    workbasket.setOrgLevel1("company");
    workbasket = workbasketService.createWorkbasket(workbasket);
    WorkbasketAccessItem wbai =
        workbasketService.newWorkbasketAccessItem(workbasket.getId(), "user-1-2");
    wbai.setPermission(WorkbasketPermission.READ, true);
    workbasketService.createWorkbasketAccessItem(wbai);

    int after = workbasketService.createWorkbasketQuery().domainIn("DOMAIN_A").list().size();
    assertThat(after).isEqualTo(before + 1);
    Workbasket createdWorkbasket = workbasketService.getWorkbasket("NT1234", "DOMAIN_A");
    assertThat(createdWorkbasket).isNotNull();
    assertThat(createdWorkbasket.getId()).startsWith("WBI");
    assertThat(createdWorkbasket).isEqualTo(workbasket);
    Workbasket createdWorkbasket2 = workbasketService.getWorkbasket(createdWorkbasket.getId());
    assertThat(createdWorkbasket).isNotNull();
    assertThat(createdWorkbasket2).isEqualTo(createdWorkbasket);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_CreateWorkbasketWithCustomValues() throws Exception {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();

    Workbasket workbasket = workbasketService.newWorkbasket("NT2345", "DOMAIN_A");
    workbasket.setName("Megabasket");
    workbasket.setType(WorkbasketType.GROUP);
    workbasket.setOrgLevel1("company");
    workbasket.setCustomField(WorkbasketCustomField.CUSTOM_1, "custom1");
    workbasket.setCustomField(WorkbasketCustomField.CUSTOM_2, "custom2");
    workbasket.setCustomField(WorkbasketCustomField.CUSTOM_3, "custom3");
    workbasket.setCustomField(WorkbasketCustomField.CUSTOM_4, "custom4");
    workbasket.setCustomField(WorkbasketCustomField.CUSTOM_5, "custom5");
    workbasket.setCustomField(WorkbasketCustomField.CUSTOM_6, "custom6");
    workbasket.setCustomField(WorkbasketCustomField.CUSTOM_7, "custom7");
    workbasket.setCustomField(WorkbasketCustomField.CUSTOM_8, "custom8");
    workbasket = workbasketService.createWorkbasket(workbasket);
    WorkbasketAccessItem wbai =
        workbasketService.newWorkbasketAccessItem(workbasket.getId(), "user-1-2");
    wbai.setPermission(WorkbasketPermission.READ, true);
    workbasketService.createWorkbasketAccessItem(wbai);

    Workbasket receivedWorkbasket = workbasketService.getWorkbasket(workbasket.getId());
    assertThat(receivedWorkbasket.getCustomField(WorkbasketCustomField.CUSTOM_1))
        .isEqualTo("custom1");
    assertThat(receivedWorkbasket.getCustomField(WorkbasketCustomField.CUSTOM_2))
        .isEqualTo("custom2");
    assertThat(receivedWorkbasket.getCustomField(WorkbasketCustomField.CUSTOM_3))
        .isEqualTo("custom3");
    assertThat(receivedWorkbasket.getCustomField(WorkbasketCustomField.CUSTOM_4))
        .isEqualTo("custom4");
    assertThat(receivedWorkbasket.getCustomField(WorkbasketCustomField.CUSTOM_5))
        .isEqualTo("custom5");
    assertThat(receivedWorkbasket.getCustomField(WorkbasketCustomField.CUSTOM_6))
        .isEqualTo("custom6");
    assertThat(receivedWorkbasket.getCustomField(WorkbasketCustomField.CUSTOM_7))
        .isEqualTo("custom7");
    assertThat(receivedWorkbasket.getCustomField(WorkbasketCustomField.CUSTOM_8))
        .isEqualTo("custom8");
  }

  @WithAccessId(user = "user-1-1")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_ThrowException_When_UserRoleIsNotAdminOrBusinessAdmin() {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();

    Workbasket workbasket = workbasketService.newWorkbasket("key3", "DOMAIN_A");
    workbasket.setName("Megabasket");
    workbasket.setType(WorkbasketType.GROUP);
    workbasket.setOrgLevel1("company");

    ThrowingCallable call = () -> workbasketService.createWorkbasket(workbasket);
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_BeAbleToCreateNewWorkbasket_When_WorkbasketCopy() throws Exception {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();
    Workbasket oldWorkbasket = workbasketService.getWorkbasket("GPK_KSC", "DOMAIN_A");

    Workbasket newWorkbasket = oldWorkbasket.copy("keyname");
    newWorkbasket = workbasketService.createWorkbasket(newWorkbasket);

    assertThat(newWorkbasket.getId()).isNotNull();
    assertThat(newWorkbasket.getId()).isNotEqualTo(oldWorkbasket.getId());
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testCreateWorkbasketWithInvalidDomain() {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();

    Workbasket workbasket = workbasketService.newWorkbasket("key3", "UNKNOWN_DOMAIN");
    workbasket.setName("Megabasket");
    workbasket.setType(WorkbasketType.GROUP);
    workbasket.setOrgLevel1("company");
    ThrowingCallable call = () -> workbasketService.createWorkbasket(workbasket);
    assertThatThrownBy(call).isInstanceOf(DomainNotFoundException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testCreateWorkbasketWithMissingRequiredField() {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();

    Workbasket workbasket = workbasketService.newWorkbasket(null, "envite");
    workbasket.setName("Megabasket");
    workbasket.setType(WorkbasketType.GROUP);
    workbasket.setOrgLevel1("company");
    // missing key
    assertThatThrownBy(() -> workbasketService.createWorkbasket(workbasket))
        .isInstanceOf(InvalidArgumentException.class);

    Workbasket workbasket2 = workbasketService.newWorkbasket("key", "envite");
    workbasket2.setType(WorkbasketType.GROUP);
    workbasket2.setOrgLevel1("company");
    // missing name
    assertThatThrownBy(() -> workbasketService.createWorkbasket(workbasket2))
        .isInstanceOf(InvalidArgumentException.class);

    Workbasket workbasket3 = workbasketService.newWorkbasket("key", "envite");
    workbasket3.setName("Megabasket");
    workbasket3.setOrgLevel1("company");
    // missing type
    assertThatThrownBy(() -> workbasketService.createWorkbasket(workbasket3))
        .isInstanceOf(InvalidArgumentException.class);

    Workbasket workbasket4 = workbasketService.newWorkbasket("key", null);
    workbasket4.setName("Megabasket");
    workbasket4.setType(WorkbasketType.GROUP);
    workbasket4.setOrgLevel1("company");
    // missing domain
    assertThatThrownBy(() -> workbasketService.createWorkbasket(workbasket4))
        .isInstanceOf(InvalidArgumentException.class);

    Workbasket workbasket5 = workbasketService.newWorkbasket("", "envite");
    workbasket5.setName("Megabasket");
    workbasket5.setType(WorkbasketType.GROUP);
    workbasket5.setOrgLevel1("company");
    // empty key
    assertThatThrownBy(() -> workbasketService.createWorkbasket(workbasket5))
        .isInstanceOf(InvalidArgumentException.class);

    Workbasket workbasket6 = workbasketService.newWorkbasket("key", "envite");
    workbasket6.setName("");
    workbasket6.setType(WorkbasketType.GROUP);
    workbasket6.setOrgLevel1("company");
    // empty name
    assertThatThrownBy(() -> workbasketService.createWorkbasket(workbasket))
        .isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testThrowsExceptionIfWorkbasketWithCaseInsensitiveSameKeyDomainIsCreated() throws Exception {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();

    Workbasket workbasket = workbasketService.newWorkbasket("X123456", "DOMAIN_A");
    workbasket.setName("Personal Workbasket for UID X123456");
    workbasket.setType(WorkbasketType.PERSONAL);
    workbasketService.createWorkbasket(workbasket);

    Workbasket duplicateWorkbasketWithSmallX =
        workbasketService.newWorkbasket("x123456", "DOMAIN_A");
    duplicateWorkbasketWithSmallX.setName("Personal Workbasket for UID X123456");
    duplicateWorkbasketWithSmallX.setType(WorkbasketType.PERSONAL);

    assertThatThrownBy(() -> workbasketService.createWorkbasket(duplicateWorkbasketWithSmallX))
        .isInstanceOf(WorkbasketAlreadyExistException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testCreateWorkbasketWithAlreadyExistingKeyAndDomainAndEmptyIdUpdatesOlderWorkbasket()
      throws Exception {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();
    // First create a new Workbasket.
    Workbasket wb = workbasketService.newWorkbasket("newKey", "DOMAIN_A");
    wb.setType(WorkbasketType.GROUP);
    wb.setName("this name");
    workbasketService.createWorkbasket(wb);

    // Second create a new Workbasket with same Key and Domain.
    Workbasket sameKeyAndDomain = workbasketService.newWorkbasket("newKey", "DOMAIN_A");
    sameKeyAndDomain.setType(WorkbasketType.TOPIC);
    sameKeyAndDomain.setName("new name");

    assertThatThrownBy(() -> workbasketService.createWorkbasket(sameKeyAndDomain))
        .isInstanceOf(WorkbasketAlreadyExistException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testWorkbasketAccessItemSetName() throws Exception {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();

    Workbasket workbasket = workbasketService.newWorkbasket("WBAIT1234", "DOMAIN_A");
    workbasket.setName("MyNewBasket");
    workbasket.setType(WorkbasketType.PERSONAL);
    workbasket.setOrgLevel1("company");
    workbasket = workbasketService.createWorkbasket(workbasket);
    WorkbasketAccessItem wbai =
        workbasketService.newWorkbasketAccessItem(workbasket.getId(), "user-1-2");
    wbai.setPermission(WorkbasketPermission.READ, true);
    wbai.setAccessName("Karl Napf");
    workbasketService.createWorkbasketAccessItem(wbai);

    Workbasket createdWorkbasket = workbasketService.getWorkbasket("WBAIT1234", "DOMAIN_A");
    assertThat(createdWorkbasket).isNotNull();
    assertThat(createdWorkbasket.getId()).isNotNull();

    List<WorkbasketAccessItem> accessItems =
        workbasketService.getWorkbasketAccessItems(createdWorkbasket.getId());
    WorkbasketAccessItem item =
        accessItems.stream().filter(t -> wbai.getId().equals(t.getId())).findFirst().orElse(null);
    assertThat(item)
        .isNotNull()
        .extracting(WorkbasketAccessItem::getAccessName)
        .isEqualTo("Karl Napf");
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_SetReadTask_When_CreatingWorkbasketAccessItem() throws Exception {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();

    WorkbasketAccessItem wbai =
        workbasketService.newWorkbasketAccessItem(
            "WBI:100000000000000000000000000000000001", "test-id");
    wbai.setPermission(WorkbasketPermission.READTASKS, true);
    workbasketService.createWorkbasketAccessItem(wbai);

    List<WorkbasketAccessItem> accessItems =
        workbasketService.getWorkbasketAccessItems("WBI:100000000000000000000000000000000001");
    WorkbasketAccessItem item =
        accessItems.stream().filter(t -> wbai.getId().equals(t.getId())).findFirst().orElse(null);
    assertThat(item).isNotNull();
    assertThat(item.getPermission(WorkbasketPermission.READTASKS)).isTrue();
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_SetEditTasks_When_CreatingWorkbasketAccessItem() throws Exception {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();

    WorkbasketAccessItem wbai =
        workbasketService.newWorkbasketAccessItem(
            "WBI:100000000000000000000000000000000001", "test-id2");
    wbai.setPermission(WorkbasketPermission.EDITTASKS, true);
    workbasketService.createWorkbasketAccessItem(wbai);

    List<WorkbasketAccessItem> accessItems =
        workbasketService.getWorkbasketAccessItems("WBI:100000000000000000000000000000000001");
    WorkbasketAccessItem item =
        accessItems.stream().filter(t -> wbai.getId().equals(t.getId())).findFirst().orElse(null);
    assertThat(item).isNotNull();
    assertThat(item.getPermission(WorkbasketPermission.EDITTASKS)).isTrue();
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testCreateDuplicateWorkbasketAccessListFails() throws Exception {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();

    Workbasket workbasket = workbasketService.newWorkbasket("NT4321", "DOMAIN_A");
    workbasket.setName("Terabasket");
    workbasket.setType(WorkbasketType.GROUP);
    workbasket.setOrgLevel1("company");
    workbasket = workbasketService.createWorkbasket(workbasket);
    WorkbasketAccessItem wbai =
        workbasketService.newWorkbasketAccessItem(workbasket.getId(), "user-3-2");
    wbai.setPermission(WorkbasketPermission.READ, true);
    workbasketService.createWorkbasketAccessItem(wbai);

    assertThatThrownBy(() -> workbasketService.createWorkbasketAccessItem(wbai))
        .isInstanceOf(WorkbasketAccessItemAlreadyExistException.class);
  }
}
