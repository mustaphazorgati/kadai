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

package io.kadai.common.rest;

import io.kadai.common.rest.models.CustomAttributesRepresentationModel;
import io.kadai.common.rest.models.KadaiUserInfoRepresentationModel;
import io.kadai.common.rest.models.VersionRepresentationModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import java.util.Map;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface KadaiEngineApi {

  @Operation(
      summary = "This endpoint retrieves all configured Domains.",
      responses =
          @ApiResponse(
              responseCode = "200",
              description = "An array with the domain-names as strings",
              content =
                  @Content(
                      mediaType = MediaTypes.HAL_JSON_VALUE,
                      schema = @Schema(implementation = String[].class))))
  @GetMapping(path = RestEndpoints.URL_DOMAIN)
  ResponseEntity<List<String>> getDomains();

  @Operation(
      summary =
          "This endpoint retrieves the configured classification categories for a specific "
              + "classification type.",
      parameters =
          @Parameter(
              name = "type",
              description =
                  "The classification type whose categories should be determined. If not specified "
                      + "all classification categories will be returned."),
      responses =
          @ApiResponse(
              responseCode = "200",
              description = "The classification categories for the requested type.",
              content =
                  @Content(
                      mediaType = MediaTypes.HAL_JSON_VALUE,
                      schema = @Schema(implementation = String[].class))))
  @GetMapping(path = RestEndpoints.URL_CLASSIFICATION_CATEGORIES)
  ResponseEntity<List<String>> getClassificationCategories(
      @RequestParam(value = "type", required = false) String type);

  @Operation(
      summary = "This endpoint retrieves the configured classification types.",
      responses =
          @ApiResponse(
              responseCode = "200",
              description = "The configured classification types.",
              content =
                  @Content(
                      mediaType = MediaTypes.HAL_JSON_VALUE,
                      schema = @Schema(implementation = String[].class))))
  @GetMapping(path = RestEndpoints.URL_CLASSIFICATION_TYPES)
  ResponseEntity<List<String>> getClassificationTypes();

  @Operation(
      summary =
          "This endpoint retrieves all configured classification categories grouped by each "
              + "classification type.",
      responses =
          @ApiResponse(
              responseCode = "200",
              description = "The configured classification categories.",
              content =
                  @Content(
                      mediaType = MediaTypes.HAL_JSON_VALUE,
                      schema = @Schema(ref = "#/components/schemas/TypeMapSchema"))))
  @GetMapping(path = RestEndpoints.URL_CLASSIFICATION_CATEGORIES_BY_TYPES)
  ResponseEntity<Map<String, List<String>>> getClassificationCategoriesByTypeMap();

  @Operation(
      summary = "This endpoint computes all information of the current user.",
      responses =
          @ApiResponse(
              responseCode = "200",
              description = "The information of the current user.",
              content =
                  @Content(
                      mediaType = MediaTypes.HAL_JSON_VALUE,
                      schema = @Schema(implementation = KadaiUserInfoRepresentationModel.class))))
  @GetMapping(path = RestEndpoints.URL_CURRENT_USER)
  ResponseEntity<KadaiUserInfoRepresentationModel> getCurrentUserInfo();

  @Operation(
      summary = "This endpoint checks if the history module is in use.",
      responses =
          @ApiResponse(
              responseCode = "200",
              description = "True, when the history is enabled, otherwise false",
              content =
                  @Content(
                      mediaType = MediaTypes.HAL_JSON_VALUE,
                      schema = @Schema(implementation = Boolean.class))))
  @GetMapping(path = RestEndpoints.URL_HISTORY_ENABLED)
  ResponseEntity<Boolean> getIsHistoryProviderEnabled();

  @Operation(
      summary = "Get custom configuration",
      description = "This endpoint retrieves the saved custom configuration.",
      responses =
          @ApiResponse(
              responseCode = "200",
              description = "The custom configuration.",
              content =
                  @Content(
                      mediaType = MediaTypes.HAL_JSON_VALUE,
                      schema =
                          @Schema(implementation = CustomAttributesRepresentationModel.class))))
  @GetMapping(path = RestEndpoints.URL_CUSTOM_ATTRIBUTES)
  ResponseEntity<CustomAttributesRepresentationModel> getCustomAttributes();

  @Operation(
      summary = "Set all custom configuration",
      description = "This endpoint overrides the custom configuration.",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "The new custom configuration",
              required = true,
              content =
                  @Content(
                      schema = @Schema(implementation = CustomAttributesRepresentationModel.class),
                      examples =
                          @ExampleObject(
                              value =
                                  "{\n"
                                      + "  \"customAttributes\": {\n"
                                      + "    \"schema\": {\n"
                                      + "      \"Filter\": {\n"
                                      + "        \"displayName\": "
                                      + "\"Filter for Task-Priority-Report\",\n"
                                      + "        \"members\": {\n"
                                      + "          \"filter\": {\n"
                                      + "            \"displayName\": \"Filter values\",\n"
                                      + "            \"type\": \"json\",\n"
                                      + "            \"min\": \"1\"\n"
                                      + "          }\n"
                                      + "        }\n"
                                      + "      },\n"
                                      + "      \"filter\": \"{ \\\"Tasks with state READY\\\": "
                                      + "{ \\\"state\\\": [\\\"READY\\\"]}, \\\"Tasks with state "
                                      + "CLAIMED\\\": {\\\"state\\\": [\\\"CLAIMED\\\"] }}\"\n"
                                      + "    }\n"
                                      + "  }\n"
                                      + "}"))),
      responses =
          @ApiResponse(
              responseCode = "200",
              description = "The new custom configuration.",
              content =
                  @Content(
                      mediaType = MediaTypes.HAL_JSON_VALUE,
                      schema =
                          @Schema(implementation = CustomAttributesRepresentationModel.class))))
  @PutMapping(path = RestEndpoints.URL_CUSTOM_ATTRIBUTES)
  @Transactional(rollbackFor = Exception.class)
  ResponseEntity<CustomAttributesRepresentationModel> setCustomAttributes(
      @RequestBody CustomAttributesRepresentationModel customAttributes);

  @Operation(
      summary = "Get the current application version",
      responses =
          @ApiResponse(
              responseCode = "200",
              description = "The current version.",
              content =
                  @Content(
                      mediaType = MediaTypes.HAL_JSON_VALUE,
                      schema = @Schema(implementation = VersionRepresentationModel.class))))
  @GetMapping(path = RestEndpoints.URL_VERSION)
  ResponseEntity<VersionRepresentationModel> currentVersion();
}
