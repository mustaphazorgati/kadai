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

import io.kadai.task.api.models.TaskComment;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class TaskCommentImpl implements TaskComment {

  private String id;
  private String taskId;
  private String textField;
  private String creator;
  private String creatorFullName;
  private Instant created;
  private Instant modified;

  public TaskCommentImpl() {}

  public TaskCommentImpl(TaskCommentImpl copyFrom) {
    this.taskId = copyFrom.taskId;
    this.textField = copyFrom.textField;
    this.creator = copyFrom.creator;
    this.creatorFullName = copyFrom.creatorFullName;
    this.created = copyFrom.created != null ? Instant.from(copyFrom.created) : null;
    this.modified = copyFrom.modified != null ? Instant.from(copyFrom.modified) : null;
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
  public String getCreator() {
    return creator;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  @Override
  public String getCreatorFullName() {
    return creatorFullName;
  }

  public void setCreatorFullName(String creatorFullName) {
    this.creatorFullName = creatorFullName;
  }

  public String getTextField() {
    return textField;
  }

  public void setTextField(String textField) {
    this.textField = textField;
  }

  @Override
  public Instant getCreated() {
    return created != null ? created.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  public void setCreated(Instant created) {
    this.created = created != null ? created.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  @Override
  public Instant getModified() {
    return modified != null ? modified.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  public void setModified(Instant modified) {
    this.modified = modified != null ? modified.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  @Override
  public TaskCommentImpl copy() {
    return new TaskCommentImpl(this);
  }

  protected boolean canEqual(Object other) {
    return (other instanceof TaskCommentImpl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, taskId, textField, creator, creatorFullName, created, modified);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof TaskCommentImpl)) {
      return false;
    }
    TaskCommentImpl other = (TaskCommentImpl) obj;

    if (!other.canEqual(this)) {
      return false;
    }

    return Objects.equals(id, other.getId())
        && Objects.equals(taskId, other.getTaskId())
        && Objects.equals(textField, other.getTextField())
        && Objects.equals(creator, other.getCreator())
        && Objects.equals(creatorFullName, other.getCreatorFullName())
        && Objects.equals(created, other.getCreated())
        && Objects.equals(modified, other.getModified());
  }

  @Override
  public String toString() {
    return "TaskCommentImpl [id="
        + id
        + ", taskId="
        + taskId
        + ", textField="
        + textField
        + ", creator="
        + creator
        + ", creatorFullName="
        + creatorFullName
        + ", created="
        + created
        + ", modified="
        + modified
        + "]";
  }
}
