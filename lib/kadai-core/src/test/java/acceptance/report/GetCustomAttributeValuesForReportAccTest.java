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

package acceptance.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.common.test.security.JaasExtension;
import io.kadai.common.test.security.WithAccessId;
import io.kadai.monitor.api.MonitorService;
import io.kadai.task.api.TaskCustomField;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/** Acceptance test for all "classification report" scenarios. */
@ExtendWith(JaasExtension.class)
class GetCustomAttributeValuesForReportAccTest extends AbstractReportAccTest {

  private static final MonitorService MONITOR_SERVICE = kadaiEngine.getMonitorService();

  @Test
  void testRoleCheck() {
    ThrowingCallable call =
        () ->
            MONITOR_SERVICE
                .createWorkbasketReportBuilder()
                .listCustomAttributeValuesForCustomAttributeName(TaskCustomField.CUSTOM_2);
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetCustomAttributeValuesForOneWorkbasket() throws Exception {
    List<String> values =
        MONITOR_SERVICE
            .createWorkbasketReportBuilder()
            .workbasketIdIn(List.of("WBI:000000000000000000000000000000000001"))
            .listCustomAttributeValuesForCustomAttributeName(TaskCustomField.CUSTOM_2);

    assertThat(values).containsExactlyInAnyOrder("Vollkasko", "Teilkasko");
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetCustomAttributeValuesForOneDomain() throws Exception {
    List<String> values =
        MONITOR_SERVICE
            .createWorkbasketReportBuilder()
            .domainIn(List.of("DOMAIN_A"))
            .listCustomAttributeValuesForCustomAttributeName(TaskCustomField.CUSTOM_16);
    assertThat(values).hasSize(26);
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_ReturnCustomAttributeValuesOfCategoryReport_When_FilteringWithCustomAttributeIn()
      throws Exception {
    List<String> values =
        MONITOR_SERVICE
            .createClassificationCategoryReportBuilder()
            .customAttributeIn(TaskCustomField.CUSTOM_2, "Vollkasko")
            .customAttributeIn(TaskCustomField.CUSTOM_1, "Geschaeftsstelle A")
            .listCustomAttributeValuesForCustomAttributeName(TaskCustomField.CUSTOM_16);

    assertThat(values).hasSize(12);
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_ReturnCustomAttributeValuesOfCategoryReport_When_FilteringWithCustomAttributeNotIn()
      throws Exception {
    List<String> values =
        MONITOR_SERVICE
            .createClassificationCategoryReportBuilder()
            .customAttributeNotIn(TaskCustomField.CUSTOM_2, "Vollkasko")
            .customAttributeNotIn(TaskCustomField.CUSTOM_1, "Geschaeftsstelle A")
            .listCustomAttributeValuesForCustomAttributeName(TaskCustomField.CUSTOM_16);

    assertThat(values).hasSize(16);
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_ReturnCustomAttributeValuesOfCategoryReport_When_FilteringWithCustomAttributeLike()
      throws Exception {
    List<String> values =
        MONITOR_SERVICE
            .createClassificationCategoryReportBuilder()
            .customAttributeLike(TaskCustomField.CUSTOM_2, "%ollkas%")
            .customAttributeLike(TaskCustomField.CUSTOM_1, "%aeftsstelle A")
            .listCustomAttributeValuesForCustomAttributeName(TaskCustomField.CUSTOM_16);

    assertThat(values).hasSize(12);
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetCustomAttributeValuesForExcludedClassifications() throws Exception {
    List<String> domains = List.of("DOMAIN_A", "DOMAIN_B", "DOMAIN_C");

    List<String> values =
        MONITOR_SERVICE
            .createClassificationCategoryReportBuilder()
            .domainIn(domains)
            .excludedClassificationIdIn(List.of("CLI:000000000000000000000000000000000003"))
            .listCustomAttributeValuesForCustomAttributeName(TaskCustomField.CUSTOM_16);

    assertThat(values).hasSize(43);
  }
}
