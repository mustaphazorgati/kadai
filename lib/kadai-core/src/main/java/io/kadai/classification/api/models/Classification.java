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

package io.kadai.classification.api.models;

import io.kadai.classification.api.ClassificationCustomField;
import java.time.Instant;

/** Interface used to specify the Classification-Model. */
public interface Classification extends ClassificationSummary {

  /**
   * Sets/Changes a reference to the current parent Classification via id. If the Classification has
   * no parent, the value of parentId should be an empty String.
   *
   * @param parentId the id of the parent Classification
   */
  void setParentId(String parentId);

  /**
   * Sets/Changes a reference to the current parent Classification via key. If the Classification
   * has no parent, the value of parentKey should be an empty String.
   *
   * @param parentKey the key of the parent Classification
   */
  void setParentKey(String parentKey);

  /**
   * Sets/Changes the category of the Classification.
   *
   * @param category the category of the Classification
   */
  void setCategory(String category);

  /**
   * Returns the current domain of the Classification.
   *
   * @return domain
   */
  String getDomain();

  /**
   * Returns the logical name of the associated applicationEntryPoint of the Classification.
   *
   * @return applicationEntryPoint
   */
  String getApplicationEntryPoint();

  /**
   * Sets the logical name of the associated applicationEntryPoint of the Classification.
   *
   * @param applicationEntryPoint the applicationEntryPoint
   */
  void setApplicationEntryPoint(String applicationEntryPoint);

  /**
   * Duplicates the Classification without the id.
   *
   * @param key for the new Classification
   * @return a copy of this Classification
   */
  Classification copy(String key);

  /**
   * Returns whether the Classification is currently valid in the used domain.
   *
   * @return isValidInDomain
   */
  Boolean getIsValidInDomain();

  /**
   * Sets/Changes the flag which marks the Classification as valid/invalid in the currently used
   * domain.
   *
   * @param isValidInDomain flag
   */
  void setIsValidInDomain(Boolean isValidInDomain);

  /**
   * Returns the time when the Classification was created.
   *
   * @return the time of creation as Instant
   */
  Instant getCreated();

  /**
   * Returns the time when the Classification was modified the last time.
   *
   * @return the time of last modification as Instant
   */
  Instant getModified();

  /**
   * Sets/Changes the name of the Classification.
   *
   * @param name the name of the Classification
   */
  void setName(String name);

  /**
   * Returns the description of the Classification.
   *
   * @return description
   */
  String getDescription();

  /**
   * Sets/Changes the description of the Classification.
   *
   * @param description the description of the Classification
   */
  void setDescription(String description);

  /**
   * Sets/Changes the numeric priority of the Classification.
   *
   * @param priority the priority of the Classification
   */
  void setPriority(int priority);

  /**
   * Sets/Changes the serviceLevel of the Classification.
   *
   * @param serviceLevel the serviceLevel; must be a String in ISO-8601 duration format; see the
   *     parse() method of {@code Duration} for details
   */
  void setServiceLevel(String serviceLevel);

  /**
   * Sets the value for {@linkplain ClassificationCustomField}.
   *
   * @param customField identifies which {@linkplain ClassificationCustomField} is to be set
   * @param value the value of the {@linkplain ClassificationCustomField} to be set
   */
  void setCustomField(ClassificationCustomField customField, String value);

  /**
   * Return a summary of the Classification.
   *
   * @return the {@linkplain ClassificationSummary} object for the current Classification
   */
  ClassificationSummary asSummary();
}
