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

package io.kadai.task.internal.models;

import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.task.api.models.ObjectReference;
import java.util.Objects;

/** ObjectReference entity. */
public class ObjectReferenceImpl implements ObjectReference {
  private String id;
  private String taskId;
  private String company;
  private String system;
  private String systemInstance;
  private String type;
  private String value;

  public ObjectReferenceImpl() {}

  public ObjectReferenceImpl(
      String company, String system, String systemInstance, String type, String value) {
    this.company = company;
    this.system = system;
    this.systemInstance = systemInstance;
    this.type = type;
    this.value = value;
  }

  private ObjectReferenceImpl(ObjectReferenceImpl copyFrom) {
    this.company = copyFrom.company;
    this.system = copyFrom.system;
    this.systemInstance = copyFrom.systemInstance;
    this.type = copyFrom.type;
    this.value = copyFrom.value;
  }

  public static void validate(ObjectReference objectReference, String objRefType, String objName)
      throws InvalidArgumentException {
    // check that all values in the ObjectReference are set correctly
    if (objectReference == null) {
      throw new InvalidArgumentException(
          String.format("%s of %s must not be null.", objRefType, objName));
    } else if (objectReference.getCompany() == null || objectReference.getCompany().isEmpty()) {
      throw new InvalidArgumentException(
          String.format("Company of %s of %s must not be empty", objRefType, objName));
    } else if (objectReference.getType() == null || objectReference.getType().length() == 0) {
      throw new InvalidArgumentException(
          String.format("Type of %s of %s must not be empty", objRefType, objName));
    } else if (objectReference.getValue() == null || objectReference.getValue().length() == 0) {
      throw new InvalidArgumentException(
          String.format("Value of %s of %s must not be empty", objRefType, objName));
    }
  }

  @Override
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  public String getTaskId() {
    return taskId;
  }

  public void setTaskId(String taskId) {
    this.taskId = taskId;
  }

  @Override
  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company == null ? null : company.trim();
  }

  @Override
  public String getSystem() {
    return system;
  }

  public void setSystem(String system) {
    this.system = system == null ? null : system.trim();
  }

  @Override
  public String getSystemInstance() {
    return systemInstance;
  }

  public void setSystemInstance(String systemInstance) {
    this.systemInstance = systemInstance;
  }

  @Override
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type == null ? null : type.trim();
  }

  @Override
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value == null ? null : value.trim();
  }

  @Override
  public ObjectReferenceImpl copy() {
    return new ObjectReferenceImpl(this);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, taskId, company, system, systemInstance, type, value);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ObjectReferenceImpl)) {
      return false;
    }
    ObjectReferenceImpl other = (ObjectReferenceImpl) obj;
    return Objects.equals(id, other.id)
        && Objects.equals(taskId, other.taskId)
        && Objects.equals(company, other.company)
        && Objects.equals(system, other.system)
        && Objects.equals(systemInstance, other.systemInstance)
        && Objects.equals(type, other.type)
        && Objects.equals(value, other.value);
  }

  @Override
  public String toString() {
    return "ObjectReference ["
        + "id="
        + this.id
        + ", taskId="
        + this.taskId
        + ", company="
        + this.company
        + ", system="
        + this.system
        + ", systemInstance="
        + this.systemInstance
        + ", type="
        + this.type
        + ", value="
        + this.value
        + "]";
  }
}
