package pro.taskana.common.rest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.rest.models.CustomAttributesRepresentationModel;
import pro.taskana.common.rest.models.TaskanaUserInfoRepresentationModel;
import pro.taskana.rest.test.RestHelper;
import pro.taskana.rest.test.TaskanaSpringBootTest;

/** Test TaskanaEngineController. */
@TaskanaSpringBootTest
class TaskanaEngineControllerIntTest {

  private static final RestTemplate TEMPLATE = RestHelper.TEMPLATE;

  private final RestHelper restHelper;

  @Autowired
  TaskanaEngineControllerIntTest(RestHelper restHelper) {
    this.restHelper = restHelper;
  }

  @Test
  void testDomains() {
    String url = restHelper.toUrl(RestEndpoints.URL_DOMAIN);
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<List<String>> response =
        TEMPLATE.exchange(
            url, HttpMethod.GET, auth, ParameterizedTypeReference.forType(List.class));
    assertThat(response.getBody()).contains("DOMAIN_A");
  }

  @Test
  void testClassificationTypes() {
    String url = restHelper.toUrl(RestEndpoints.URL_CLASSIFICATION_TYPES);
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<List<String>> response =
        TEMPLATE.exchange(
            url, HttpMethod.GET, auth, ParameterizedTypeReference.forType(List.class));
    assertThat(response.getBody()).containsExactlyInAnyOrder("TASK", "DOCUMENT");
  }

  @Test
  void should_ReturnAllClassifications_When_GetClassificationCategories_isCalledWithoutType() {
    String url = restHelper.toUrl(RestEndpoints.URL_CLASSIFICATION_CATEGORIES);
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<List<String>> response =
        TEMPLATE.exchange(
            url, HttpMethod.GET, auth, ParameterizedTypeReference.forType(List.class));
    assertThat(response.getBody())
        .containsExactlyInAnyOrder("EXTERNAL", "MANUAL", "AUTOMATIC", "PROCESS", "EXTERNAL");
  }

  @Test
  void should_ReturnOnlyClassificationsForTypeTask_When_GetClassificationCategories_isCalled() {
    String url = restHelper.toUrl(RestEndpoints.URL_CLASSIFICATION_CATEGORIES) + "?type=TASK";
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<List<String>> response =
        TEMPLATE.exchange(
            url, HttpMethod.GET, auth, ParameterizedTypeReference.forType(List.class));
    assertThat(response.getBody()).containsExactly("EXTERNAL", "MANUAL", "AUTOMATIC", "PROCESS");
  }

  @Test
  void should_ReturnOnlyClassificationsForTypeDocument_When_GetClassificationCategories_isCalled() {
    String url = restHelper.toUrl(RestEndpoints.URL_CLASSIFICATION_CATEGORIES) + "?type=DOCUMENT";
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<List<String>> response =
        TEMPLATE.exchange(
            url, HttpMethod.GET, auth, ParameterizedTypeReference.forType(List.class));
    assertThat(response.getBody()).containsExactly("EXTERNAL");
  }

  @Test
  void testGetCurrentUserInfo() {
    String url = restHelper.toUrl(RestEndpoints.URL_CURRENT_USER);
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<TaskanaUserInfoRepresentationModel> response =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(TaskanaUserInfoRepresentationModel.class));
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getUserId()).isEqualTo("teamlead-1");
    assertThat(response.getBody().getGroupIds())
        .contains("cn=business-admins,cn=groups,ou=test,o=taskana");
    assertThat(response.getBody().getRoles())
        .contains(TaskanaRole.BUSINESS_ADMIN)
        .doesNotContain(TaskanaRole.ADMIN);
  }

  @Test
  void should_ReturnCustomAttributes() {
    String url = restHelper.toUrl(RestEndpoints.URL_CUSTOM_ATTRIBUTES);
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<CustomAttributesRepresentationModel> response =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(CustomAttributesRepresentationModel.class));

    assertThat(response.getBody()).isNotNull();
  }
}
