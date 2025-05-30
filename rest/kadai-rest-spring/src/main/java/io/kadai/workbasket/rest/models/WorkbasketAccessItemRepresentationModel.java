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

package io.kadai.workbasket.rest.models;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.hateoas.RepresentationModel;

@Schema(description = "EntityModel class for WorkbasketAccessItem")
public class WorkbasketAccessItemRepresentationModel
    extends RepresentationModel<WorkbasketAccessItemRepresentationModel> {

  @Schema(name = "accessItemId", description = "Unique Id.")
  private String accessItemId;

  @Schema(name = "workbasketId", description = "The workbasket Id.")
  private String workbasketId;

  @Schema(
      name = "accessId",
      description = "The Access Id. This could be either a user Id or a full qualified group Id.")
  private String accessId;

  @Schema(name = "workbasketKey", description = "The workbasket key.")
  private String workbasketKey;

  @Schema(name = "accessName", description = "The name.")
  private String accessName;

  @Schema(
      name = "permRead",
      description = "The permission to read the information about the workbasket.")
  private boolean permRead;

  @Schema(
      name = "permReadTasks",
      description = "The permission to access a task from the workbasket.")
  private boolean permReadTasks;

  @Schema(
      name = "permOpen",
      description = "The permission to view the content (the tasks) of a workbasket.")
  private boolean permOpen;

  @Schema(
      name = "permAppend",
      description =
          "The permission to add tasks to the workbasket. Required for creation and transferring "
              + "of tasks.")
  private boolean permAppend;

  @Schema(
      name = "permEditTasks",
      description = "The permission to edit a task from the workbasket.")
  private boolean permEditTasks;

  @Schema(
      name = "permTransfer",
      description = "The permission to transfer tasks (out of the current workbasket).")
  private boolean permTransfer;

  @Schema(
      name = "permDistribute",
      description = "The permission to distribute tasks from the workbasket.")
  private boolean permDistribute;

  @Schema(name = "permCustom1", description = "The custom permission with the name '1'.")
  private boolean permCustom1;

  @Schema(name = "permCustom2", description = "The custom permission with the name '2'.")
  private boolean permCustom2;

  @Schema(name = "permCustom3", description = "The custom permission with the name '3'.")
  private boolean permCustom3;

  @Schema(name = "permCustom4", description = "The custom permission with the name '4'.")
  private boolean permCustom4;

  @Schema(name = "permCustom5", description = "The custom permission with the name '5'.")
  private boolean permCustom5;

  @Schema(name = "permCustom6", description = "The custom permission with the name '6'.")
  private boolean permCustom6;

  @Schema(name = "permCustom7", description = "The custom permission with the name '7'.")
  private boolean permCustom7;

  @Schema(name = "permCustom8", description = "The custom permission with the name '8'.")
  private boolean permCustom8;

  @Schema(name = "permCustom9", description = "The custom permission with the name '9'.")
  private boolean permCustom9;

  @Schema(name = "permCustom10", description = "The custom permission with the name '10'.")
  private boolean permCustom10;

  @Schema(name = "permCustom11", description = "The custom permission with the name '11'.")
  private boolean permCustom11;

  @Schema(name = "permCustom12", description = "The custom permission with the name '12'.")
  private boolean permCustom12;

  public String getAccessItemId() {
    return accessItemId;
  }

  public void setAccessItemId(String accessItemId) {
    this.accessItemId = accessItemId;
  }

  public String getWorkbasketId() {
    return workbasketId;
  }

  public void setWorkbasketId(String workbasketId) {
    this.workbasketId = workbasketId;
  }

  public String getWorkbasketKey() {
    return workbasketKey;
  }

  public void setWorkbasketKey(String workbasketKey) {
    this.workbasketKey = workbasketKey;
  }

  public String getAccessId() {
    return accessId;
  }

  public void setAccessId(String accessId) {
    this.accessId = accessId;
  }

  public String getAccessName() {
    return accessName;
  }

  public void setAccessName(String accessName) {
    this.accessName = accessName;
  }

  public boolean isPermRead() {
    return permRead;
  }

  public void setPermRead(boolean permRead) {
    this.permRead = permRead;
  }

  public boolean isPermReadTasks() {
    return permReadTasks;
  }

  public void setPermReadTasks(boolean permReadTasks) {
    this.permReadTasks = permReadTasks;
  }

  public boolean isPermOpen() {
    return permOpen;
  }

  public void setPermOpen(boolean permOpen) {
    this.permOpen = permOpen;
  }

  public boolean isPermAppend() {
    return permAppend;
  }

  public void setPermAppend(boolean permAppend) {
    this.permAppend = permAppend;
  }

  public boolean isPermEditTasks() {
    return permEditTasks;
  }

  public void setPermEditTasks(boolean permEditTasks) {
    this.permEditTasks = permEditTasks;
  }

  public boolean isPermTransfer() {
    return permTransfer;
  }

  public void setPermTransfer(boolean permTransfer) {
    this.permTransfer = permTransfer;
  }

  public boolean isPermDistribute() {
    return permDistribute;
  }

  public void setPermDistribute(boolean permDistribute) {
    this.permDistribute = permDistribute;
  }

  public boolean isPermCustom1() {
    return permCustom1;
  }

  public void setPermCustom1(boolean permCustom1) {
    this.permCustom1 = permCustom1;
  }

  public boolean isPermCustom2() {
    return permCustom2;
  }

  public void setPermCustom2(boolean permCustom2) {
    this.permCustom2 = permCustom2;
  }

  public boolean isPermCustom3() {
    return permCustom3;
  }

  public void setPermCustom3(boolean permCustom3) {
    this.permCustom3 = permCustom3;
  }

  public boolean isPermCustom4() {
    return permCustom4;
  }

  public void setPermCustom4(boolean permCustom4) {
    this.permCustom4 = permCustom4;
  }

  public boolean isPermCustom5() {
    return permCustom5;
  }

  public void setPermCustom5(boolean permCustom5) {
    this.permCustom5 = permCustom5;
  }

  public boolean isPermCustom6() {
    return permCustom6;
  }

  public void setPermCustom6(boolean permCustom6) {
    this.permCustom6 = permCustom6;
  }

  public boolean isPermCustom7() {
    return permCustom7;
  }

  public void setPermCustom7(boolean permCustom7) {
    this.permCustom7 = permCustom7;
  }

  public boolean isPermCustom8() {
    return permCustom8;
  }

  public void setPermCustom8(boolean permCustom8) {
    this.permCustom8 = permCustom8;
  }

  public boolean isPermCustom9() {
    return permCustom9;
  }

  public void setPermCustom9(boolean permCustom9) {
    this.permCustom9 = permCustom9;
  }

  public boolean isPermCustom10() {
    return permCustom10;
  }

  public void setPermCustom10(boolean permCustom10) {
    this.permCustom10 = permCustom10;
  }

  public boolean isPermCustom11() {
    return permCustom11;
  }

  public void setPermCustom11(boolean permCustom11) {
    this.permCustom11 = permCustom11;
  }

  public boolean isPermCustom12() {
    return permCustom12;
  }

  public void setPermCustom12(boolean permCustom12) {
    this.permCustom12 = permCustom12;
  }
}
