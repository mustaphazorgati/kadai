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
import io.kadai.workbasket.rest.models.WorkbasketAccessItemPagedRepresentationModel;
import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.function.ThrowingConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;

/** Test WorkbasketAccessItemController. */
@TestMethodOrder(MethodOrderer.MethodName.class)
@KadaiSpringBootTest
class WorkbasketAccessItemControllerIntTest {

  private final RestHelper restHelper;

  @Autowired
  WorkbasketAccessItemControllerIntTest(RestHelper restHelper) {
    this.restHelper = restHelper;
  }

  @Test
  void testGetAllWorkbasketAccessItems() {
    String url = restHelper.toUrl(RestEndpoints.URL_WORKBASKET_ACCESS_ITEMS);

    ResponseEntity<WorkbasketAccessItemPagedRepresentationModel> response =
        CLIENT
            .get()
            .uri(url)
            .headers(headers -> headers.addAll(RestHelper.generateHeadersForUser("teamlead-1")))
            .retrieve()
            .toEntity(WorkbasketAccessItemPagedRepresentationModel.class);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
  }

  @Test
  void testGetWorkbasketAccessItemsKeepingFilters() {
    String parameters =
        "?sort-by=WORKBASKET_KEY&order=ASCENDING&page-size=9&access-id=user-1-1&page=1";
    String url = restHelper.toUrl(RestEndpoints.URL_WORKBASKET_ACCESS_ITEMS) + parameters;

    ResponseEntity<WorkbasketAccessItemPagedRepresentationModel> response =
        CLIENT
            .get()
            .uri(url)
            .headers(headers -> headers.addAll(RestHelper.generateHeadersForUser("teamlead-1")))
            .retrieve()
            .toEntity(WorkbasketAccessItemPagedRepresentationModel.class);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF))
        .isNotEmpty()
        .get()
        .extracting(Link::getHref)
        .asString()
        .endsWith(parameters);
  }

  @Test
  void should_GetSortedAccessItems_When_OrderingByWorkbasketKey() {
    String parameters =
        "?sort-by=WORKBASKET_KEY&order=ASCENDING&page=1&page-size=9&access-id=user-1-1";
    String url = restHelper.toUrl(RestEndpoints.URL_WORKBASKET_ACCESS_ITEMS) + parameters;

    ResponseEntity<WorkbasketAccessItemPagedRepresentationModel> response =
        CLIENT
            .get()
            .uri(url)
            .headers(headers -> headers.addAll(RestHelper.generateHeadersForUser("teamlead-1")))
            .retrieve()
            .toEntity(WorkbasketAccessItemPagedRepresentationModel.class);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(1);
    assertThat(response.getBody().getContent().iterator().next().getAccessId())
        .isEqualTo("user-1-1");
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF))
        .isNotEmpty()
        .get()
        .extracting(Link::getHref)
        .asString()
        .endsWith(parameters);
    assertThat(response.getBody().getLink(IanaLinkRelations.FIRST)).isNotEmpty();
    assertThat(response.getBody().getLink(IanaLinkRelations.LAST)).isNotEmpty();
    assertThat(response.getBody().getPageMetadata().getSize()).isEqualTo(9);
    assertThat(response.getBody().getPageMetadata().getTotalElements()).isEqualTo(1);
    assertThat(response.getBody().getPageMetadata().getTotalPages()).isEqualTo(1);
    assertThat(response.getBody().getPageMetadata().getNumber()).isEqualTo(1);
  }

  @Test
  void should_notSplitQueryParameterByComma_When_accessId_containsTwo() {
    String parameters =
        "?sort-by=WORKBASKET_KEY&order=ASCENDING&page=1&page-size=9&access-id=user-1-1,user-1-2";
    String url = restHelper.toUrl(RestEndpoints.URL_WORKBASKET_ACCESS_ITEMS) + parameters;

    ResponseEntity<WorkbasketAccessItemPagedRepresentationModel> response =
        CLIENT
            .get()
            .uri(url)
            .headers(headers -> headers.addAll(RestHelper.generateHeadersForUser("teamlead-1")))
            .retrieve()
            .toEntity(WorkbasketAccessItemPagedRepresentationModel.class);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent()).isEmpty();
    assertThat(response.getBody().getPageMetadata().getSize()).isEqualTo(9);
    assertThat(response.getBody().getPageMetadata().getTotalElements()).isZero();
    assertThat(response.getBody().getPageMetadata().getTotalPages()).isZero();
    assertThat(response.getBody().getPageMetadata().getNumber()).isEqualTo(1);
  }

  @Test
  void should_DeleteAllAccessItemForUser_ifValidAccessIdOfUserIsSupplied() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_WORKBASKET_ACCESS_ITEMS) + "?access-id=teamlead-2";

    ResponseEntity<Void> response =
        CLIENT
            .delete()
            .uri(url)
            .headers(headers -> headers.addAll(RestHelper.generateHeadersForUser("teamlead-1")))
            .retrieve()
            .toEntity(Void.class);
    assertThat(response.getBody()).isNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  void should_ThrowException_When_ProvidingInvalidFilterParams() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_WORKBASKET_ACCESS_ITEMS)
            + "?access-id=teamlead-2"
            + "&illegalParam=illegal"
            + "&anotherIllegalParam=stillIllegal"
            + "&sort-by=WORKBASKET_KEY&order=DESCENDING&page-size=5&page=2";

    ThrowingCallable httpCall =
        () ->
            CLIENT
                .get()
                .uri(url)
                .headers(headers -> headers.addAll(RestHelper.generateHeadersForUser("teamlead-1")))
                .retrieve()
                .toEntity(WorkbasketAccessItemPagedRepresentationModel.class);
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpStatusCodeException.class)
        .hasMessageContaining(
            "Unknown request parameters found: [anotherIllegalParam, illegalParam]")
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @TestFactory
  Stream<DynamicTest> should_ReturnBadRequest_When_AccessIdIsInvalid() {
    List<String> accessIds =
        List.of(
            "cn=organisationseinheit ksc,cn=organisation,ou=test,o=kadai",
            "cn=monitor-users,cn=groups,ou=test,o=kadai",
            "user-1");

    ThrowingConsumer<String> test =
        accessId -> {
          String url =
              restHelper.toUrl(RestEndpoints.URL_WORKBASKET_ACCESS_ITEMS)
                  + "?access-id="
                  + accessId;

          ThrowingCallable httpCall =
              () ->
                  CLIENT
                      .delete()
                      .uri(url)
                      .headers(
                          headers ->
                              headers.addAll(RestHelper.generateHeadersForUser("teamlead-1")))
                      .retrieve()
                      .toEntity(Void.class);
          assertThatThrownBy(httpCall)
              .isInstanceOf(HttpStatusCodeException.class)
              .extracting(HttpStatusCodeException.class::cast)
              .extracting(HttpStatusCodeException::getStatusCode)
              .isEqualTo(HttpStatus.BAD_REQUEST);
        };

    return DynamicTest.stream(accessIds.iterator(), s -> String.format("for user '%s'", s), test);
  }
}
