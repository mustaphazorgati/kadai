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

package io.kadai.workbasket.rest;

import static io.kadai.rest.test.RestHelper.CLIENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.kadai.common.rest.RestEndpoints;
import io.kadai.rest.test.KadaiSpringBootTest;
import io.kadai.rest.test.RestHelper;
import io.kadai.workbasket.api.WorkbasketType;
import io.kadai.workbasket.rest.models.DistributionTargetsCollectionRepresentationModel;
import io.kadai.workbasket.rest.models.WorkbasketAccessItemCollectionRepresentationModel;
import io.kadai.workbasket.rest.models.WorkbasketAccessItemRepresentationModel;
import io.kadai.workbasket.rest.models.WorkbasketRepresentationModel;
import io.kadai.workbasket.rest.models.WorkbasketSummaryPagedRepresentationModel;
import io.kadai.workbasket.rest.models.WorkbasketSummaryRepresentationModel;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;

/** Test WorkbasketController. */
@KadaiSpringBootTest
class WorkbasketControllerIntTest {

  private final RestHelper restHelper;

  @Autowired
  WorkbasketControllerIntTest(RestHelper restHelper) {
    this.restHelper = restHelper;
  }

  @Test
  void testGetWorkbasket() {
    final String url =
        restHelper.toUrl(
            RestEndpoints.URL_WORKBASKET_ID, "WBI%3A100000000000000000000000000000000006");

    ResponseEntity<WorkbasketRepresentationModel> response =
        CLIENT
            .get()
            .uri(URLDecoder.decode(url, StandardCharsets.UTF_8))
            .headers(headers -> headers.addAll(RestHelper.generateHeadersForUser("teamlead-1")))
            .retrieve()
            .toEntity(WorkbasketRepresentationModel.class);

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).contains(Link.of(url));
    assertThat(response.getHeaders().getContentType()).isEqualTo(MediaTypes.HAL_JSON);
  }

  @Test
  void testGetAllWorkbaskets() {
    String url = restHelper.toUrl(RestEndpoints.URL_WORKBASKET);

    ResponseEntity<WorkbasketSummaryPagedRepresentationModel> response =
        CLIENT
            .get()
            .uri(url)
            .headers(headers -> headers.addAll(RestHelper.generateHeadersForUser("teamlead-1")))
            .retrieve()
            .toEntity(WorkbasketSummaryPagedRepresentationModel.class);

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
  }

  @Test
  void testGetAllWorkbasketsBusinessAdminHasOpenPermission() {
    String url = restHelper.toUrl(RestEndpoints.URL_WORKBASKET) + "?required-permission=OPEN";

    ResponseEntity<WorkbasketSummaryPagedRepresentationModel> response =
        CLIENT
            .get()
            .uri(url)
            .headers(headers -> headers.addAll(RestHelper.generateHeadersForUser("teamlead-1")))
            .retrieve()
            .toEntity(WorkbasketSummaryPagedRepresentationModel.class);

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getRequiredLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(6);
  }

  @Test
  void testGetAllWorkbasketsKeepingFilters() {
    String parameters = "?type=PERSONAL&sort-by=KEY&order=DESCENDING";
    String url = restHelper.toUrl(RestEndpoints.URL_WORKBASKET) + parameters;

    ResponseEntity<WorkbasketSummaryPagedRepresentationModel> response =
        CLIENT
            .get()
            .uri(url)
            .headers(headers -> headers.addAll(RestHelper.generateHeadersForUser("teamlead-1")))
            .retrieve()
            .toEntity(WorkbasketSummaryPagedRepresentationModel.class);

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getRequiredLink(IanaLinkRelations.SELF).getHref())
        .endsWith(parameters);
  }

  @Test
  void testUpdateWorkbasketWithConcurrentModificationShouldThrowException() {
    String url =
        restHelper.toUrl(
            RestEndpoints.URL_WORKBASKET_ID, "WBI:100000000000000000000000000000000001");

    HttpHeaders httpHeaders = RestHelper.generateHeadersForUser("teamlead-1");

    ResponseEntity<WorkbasketRepresentationModel> initialWorkbasketResourceRequestResponse =
        CLIENT
            .get()
            .uri(url)
            .headers(headers -> headers.addAll(httpHeaders))
            .retrieve()
            .toEntity(WorkbasketRepresentationModel.class);
    WorkbasketRepresentationModel workbasketRepresentationModel =
        initialWorkbasketResourceRequestResponse.getBody();
    assertThat(workbasketRepresentationModel).isNotNull();

    workbasketRepresentationModel.setKey("GPK_KSC");
    workbasketRepresentationModel.setDomain("DOMAIN_A");
    workbasketRepresentationModel.setType(WorkbasketType.PERSONAL);
    workbasketRepresentationModel.setName("was auch immer");
    workbasketRepresentationModel.setOwner("Joerg");
    workbasketRepresentationModel.setModified(Instant.now());

    ThrowingCallable httpCall =
        () ->
            CLIENT
                .put()
                .uri(url)
                .headers(headers -> headers.addAll(httpHeaders))
                .body(workbasketRepresentationModel)
                .retrieve()
                .toEntity(WorkbasketRepresentationModel.class);
    assertThatThrownBy(httpCall)
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  void testUpdateWorkbasketOfNonExistingWorkbasketShouldThrowException() {
    String url =
        restHelper.toUrl(
            RestEndpoints.URL_WORKBASKET_ID, "WBI:100004857400039500000999999999999999");

    ThrowingCallable httpCall =
        () ->
            CLIENT
                .get()
                .uri(url)
                .headers(
                    headers -> headers.addAll(RestHelper.generateHeadersForUser("businessadmin")))
                .retrieve()
                .toEntity(WorkbasketRepresentationModel.class);

    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpStatusCodeException.class)
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void should_UpdateWorkbasket() {
    String url =
        restHelper.toUrl(
            RestEndpoints.URL_WORKBASKET_ID, "WBI:100000000000000000000000000000000001");

    HttpHeaders httpHeaders = RestHelper.generateHeadersForUser("admin");

    ResponseEntity<WorkbasketRepresentationModel> response =
        CLIENT
            .get()
            .uri(URLDecoder.decode(url, StandardCharsets.UTF_8))
            .headers(headers -> headers.addAll(httpHeaders))
            .retrieve()
            .toEntity(WorkbasketRepresentationModel.class);

    assertThat(response.getBody()).isNotNull();
    WorkbasketRepresentationModel workbasketToUpdate = response.getBody();
    workbasketToUpdate.setName("new name");

    ResponseEntity<WorkbasketRepresentationModel> responseUpdate =
        CLIENT
            .put()
            .uri(url)
            .headers(headers -> headers.addAll(httpHeaders))
            .body(workbasketToUpdate)
            .retrieve()
            .toEntity(WorkbasketRepresentationModel.class);

    assertThat(responseUpdate.getBody()).isNotNull();
    assertThat(responseUpdate.getBody().getName()).isEqualTo("new name");
  }

  @Test
  void testGetSecondPageSortedByKey() {
    String parameters = "?sort-by=KEY&order=DESCENDING&page-size=5&page=2";
    String url = restHelper.toUrl(RestEndpoints.URL_WORKBASKET) + parameters;

    ResponseEntity<WorkbasketSummaryPagedRepresentationModel> response =
        CLIENT
            .get()
            .uri(url)
            .headers(headers -> headers.addAll(RestHelper.generateHeadersForUser("teamlead-1")))
            .retrieve()
            .toEntity(WorkbasketSummaryPagedRepresentationModel.class);

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(5);
    assertThat(response.getBody().getContent().iterator().next().getKey()).isEqualTo("USER-1-1");
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.FIRST)).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.LAST)).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.NEXT)).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.PREV)).isNotNull();
    assertThat(response.getBody().getRequiredLink(IanaLinkRelations.SELF).getHref())
        .endsWith(parameters);
  }

  @Test
  void testMarkWorkbasketForDeletionAsBusinessAdminWithoutExplicitReadPermission() {
    String url =
        restHelper.toUrl(
            RestEndpoints.URL_WORKBASKET_ID, "WBI:100000000000000000000000000000000005");

    ResponseEntity<?> response =
        CLIENT
            .delete()
            .uri(url)
            .headers(headers -> headers.addAll(RestHelper.generateHeadersForUser("businessadmin")))
            .retrieve()
            .toEntity(Void.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
  }

  @Test
  void statusCode423ShouldBeReturnedIfWorkbasketContainsNonCompletedTasks() {
    String url =
        restHelper.toUrl(
            RestEndpoints.URL_WORKBASKET_ID, "WBI:100000000000000000000000000000000004");

    ThrowingCallable call =
        () ->
            CLIENT
                .delete()
                .uri(url)
                .headers(
                    headers -> headers.addAll(RestHelper.generateHeadersForUser("businessadmin")))
                .retrieve()
                .toEntity(Void.class);

    assertThatThrownBy(call)
        .isInstanceOf(HttpStatusCodeException.class)
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.LOCKED);
  }

  @Test
  void testRemoveWorkbasketAsDistributionTarget() {
    String url =
        restHelper.toUrl(
            RestEndpoints.URL_WORKBASKET_ID_DISTRIBUTION,
            "WBI:100000000000000000000000000000000007");
    HttpHeaders httpHeaders = RestHelper.generateHeadersForUser("teamlead-1");

    ResponseEntity<?> response =
        CLIENT
            .delete()
            .uri(url)
            .headers(headers -> headers.addAll(httpHeaders))
            .retrieve()
            .toEntity(Void.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    String url2 =
        restHelper.toUrl(
            RestEndpoints.URL_WORKBASKET_ID_DISTRIBUTION,
            "WBI:100000000000000000000000000000000002");
    ResponseEntity<DistributionTargetsCollectionRepresentationModel> response2 =
        CLIENT
            .get()
            .uri(url2)
            .headers(headers -> headers.addAll(httpHeaders))
            .retrieve()
            .toEntity(DistributionTargetsCollectionRepresentationModel.class);

    assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response2.getBody()).isNotNull();
    assertThat(response2.getBody().getContent())
        .extracting(WorkbasketSummaryRepresentationModel::getWorkbasketId)
        .doesNotContain("WBI:100000000000000000000000000000000007");
  }

  @Test
  void testGetWorkbasketAccessItems() {
    String url =
        restHelper.toUrl(
            RestEndpoints.URL_WORKBASKET_ID_ACCESS_ITEMS,
            "WBI:100000000000000000000000000000000005");

    ResponseEntity<WorkbasketAccessItemCollectionRepresentationModel> response =
        CLIENT
            .get()
            .uri(url)
            .headers(headers -> headers.addAll(RestHelper.generateHeadersForUser("teamlead-1")))
            .retrieve()
            .toEntity(WorkbasketAccessItemCollectionRepresentationModel.class);

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getHeaders().getContentType()).isEqualTo(MediaTypes.HAL_JSON);
    assertThat(response.getBody().getContent()).hasSize(4);
  }

  @Test
  void should_SetWorkbasketAccessItemsForAWorkbasket() {
    String workbasketId = "WBI:000000000000000000000000000000000900";
    String url = restHelper.toUrl(RestEndpoints.URL_WORKBASKET_ID_ACCESS_ITEMS, workbasketId);
    WorkbasketAccessItemRepresentationModel wbAccessItem =
        new WorkbasketAccessItemRepresentationModel();
    wbAccessItem.setWorkbasketId(workbasketId);
    wbAccessItem.setAccessId("new-access-id");
    wbAccessItem.setAccessName("new-access-name");
    wbAccessItem.setPermOpen(true);

    WorkbasketAccessItemCollectionRepresentationModel repModel =
        new WorkbasketAccessItemCollectionRepresentationModel(List.of(wbAccessItem));

    HttpHeaders httpHeaders = RestHelper.generateHeadersForUser("admin");

    ResponseEntity<WorkbasketAccessItemCollectionRepresentationModel> response =
        CLIENT
            .put()
            .uri(url)
            .headers(headers -> headers.addAll(httpHeaders))
            .body(repModel)
            .retrieve()
            .toEntity(WorkbasketAccessItemCollectionRepresentationModel.class);

    assertThat(response.getBody()).isNotNull();

    ResponseEntity<WorkbasketAccessItemCollectionRepresentationModel> responseGetAccessItems =
        CLIENT
            .get()
            .uri(url)
            .headers(headers -> headers.addAll(httpHeaders))
            .retrieve()
            .toEntity(WorkbasketAccessItemCollectionRepresentationModel.class);

    assertThat(responseGetAccessItems.getBody()).isNotNull();
    assertThat(responseGetAccessItems.getBody().getContent()).hasSize(1);
    Collection<WorkbasketAccessItemRepresentationModel> collection =
        responseGetAccessItems.getBody().getContent();
    Iterator<WorkbasketAccessItemRepresentationModel> iterator = collection.iterator();
    WorkbasketAccessItemRepresentationModel returnedWbAccessItem = iterator.next();
    assertThat(returnedWbAccessItem.getWorkbasketId()).isEqualTo(workbasketId);
    assertThat(returnedWbAccessItem.getAccessId()).isEqualTo("new-access-id");
    assertThat(returnedWbAccessItem.getAccessName()).isEqualTo("new-access-name");
    assertThat(returnedWbAccessItem.isPermOpen()).isTrue();
  }

  @Test
  void testGetWorkbasketDistributionTargets() {
    String url =
        restHelper.toUrl(
            RestEndpoints.URL_WORKBASKET_ID_DISTRIBUTION,
            "WBI:100000000000000000000000000000000001");

    ResponseEntity<DistributionTargetsCollectionRepresentationModel> response =
        CLIENT
            .get()
            .uri(url)
            .headers(headers -> headers.addAll(RestHelper.generateHeadersForUser("teamlead-1")))
            .retrieve()
            .toEntity(DistributionTargetsCollectionRepresentationModel.class);

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getHeaders().getContentType()).isEqualTo(MediaTypes.HAL_JSON);
    assertThat(response.getBody().getContent()).hasSize(4);
  }

  @Test
  void should_SetAllDistributionTargets() {
    List<String> distributionTargets =
        List.of(
            "WBI:100000000000000000000000000000000003", "WBI:100000000000000000000000000000000004");
    String url =
        restHelper.toUrl(
            RestEndpoints.URL_WORKBASKET_ID_DISTRIBUTION,
            "WBI:100000000000000000000000000000000002");

    HttpHeaders httpHeaders = RestHelper.generateHeadersForUser("admin");

    ResponseEntity<DistributionTargetsCollectionRepresentationModel> response =
        CLIENT
            .put()
            .uri(url)
            .headers(headers -> headers.addAll(httpHeaders))
            .body(distributionTargets)
            .retrieve()
            .toEntity(DistributionTargetsCollectionRepresentationModel.class);

    assertThat(response.getBody()).isNotNull();

    ResponseEntity<DistributionTargetsCollectionRepresentationModel>
        responseGetDistributionTargets =
            CLIENT
                .get()
                .uri(url)
                .headers(headers -> headers.addAll(httpHeaders))
                .retrieve()
                .toEntity(DistributionTargetsCollectionRepresentationModel.class);
    assertThat(responseGetDistributionTargets.getBody()).isNotNull();
    assertThat(responseGetDistributionTargets.getBody().getContent()).hasSize(2);
    assertThat(
            responseGetDistributionTargets.getBody().getContent().stream()
                .map(WorkbasketSummaryRepresentationModel::getWorkbasketId)
                .toList())
        .containsExactlyInAnyOrder(
            "WBI:100000000000000000000000000000000003", "WBI:100000000000000000000000000000000004");
  }

  @Test
  void should_ThrowException_When_ProvidingInvalidFilterParams() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_WORKBASKET)
            + "?type=PERSONAL"
            + "&illegalParam=illegal"
            + "&anotherIllegalParam=stillIllegal"
            + "&sort-by=KEY&order=DESCENDING&page-size=5&page=2";

    ThrowingCallable httpCall =
        () ->
            CLIENT
                .get()
                .uri(url)
                .headers(headers -> headers.addAll(RestHelper.generateHeadersForUser("teamlead-1")))
                .retrieve()
                .toEntity(WorkbasketSummaryPagedRepresentationModel.class);

    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpStatusCodeException.class)
        .hasMessageContaining(
            "Unknown request parameters found: [anotherIllegalParam, illegalParam]")
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void should_CreateWorkbasket() {
    String url = restHelper.toUrl(RestEndpoints.URL_WORKBASKET);

    WorkbasketRepresentationModel workbasketToCreate = new WorkbasketRepresentationModel();
    workbasketToCreate.setKey("newKey");
    workbasketToCreate.setDomain("DOMAIN_A");
    workbasketToCreate.setType(WorkbasketType.GROUP);
    workbasketToCreate.setName("this is a wonderful workbasket name");

    ResponseEntity<WorkbasketRepresentationModel> responseCreate =
        CLIENT
            .post()
            .uri(url)
            .headers(headers -> headers.addAll(RestHelper.generateHeadersForUser("admin")))
            .body(workbasketToCreate)
            .retrieve()
            .toEntity(WorkbasketRepresentationModel.class);

    assertThat(responseCreate.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseCreate.getBody()).isNotNull();

    String wbIdOfCreatedWb = responseCreate.getBody().getWorkbasketId();
    assertThat(wbIdOfCreatedWb).startsWith("WBI:");
  }
}
