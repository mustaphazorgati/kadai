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

import io.kadai.classification.api.models.ClassificationSummary;
import io.kadai.classification.internal.models.ClassificationSummaryImpl;
import io.kadai.task.api.models.AttachmentSummary;
import io.kadai.task.api.models.ObjectReference;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/** The most important fields of the Attachment entity. */
public class AttachmentSummaryImpl implements AttachmentSummary {

  protected String id;
  protected String taskId;
  protected Instant created;
  protected Instant modified;
  protected ClassificationSummary classificationSummary;
  protected ObjectReference objectReference;
  protected String channel;
  protected Instant received;

  AttachmentSummaryImpl() {}

  protected AttachmentSummaryImpl(AttachmentSummaryImpl copyFrom) {
    this.created = copyFrom.created != null ? Instant.from(copyFrom.created) : null;
    this.modified = copyFrom.modified != null ? Instant.from(copyFrom.modified) : null;
    this.classificationSummary = copyFrom.classificationSummary;
    this.objectReference = copyFrom.objectReference;
    this.channel = copyFrom.channel;
    this.received = copyFrom.received != null ? Instant.from(copyFrom.received) : null;
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
  public ObjectReference getObjectReference() {
    return objectReference;
  }

  public void setObjectReference(ObjectReference objectReference) {
    this.objectReference = objectReference;
  }

  @Override
  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  @Override
  public ClassificationSummary getClassificationSummary() {
    return classificationSummary;
  }

  public void setClassificationSummary(ClassificationSummary classificationSummary) {
    this.classificationSummary = classificationSummary;
  }

  @Override
  public Instant getReceived() {
    return received != null ? received.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  public void setReceived(Instant received) {
    this.received = received != null ? received.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  @Override
  public AttachmentSummaryImpl copy() {
    return new AttachmentSummaryImpl(this);
  }

  // auxiliary method to enable MyBatis access to classificationSummary
  @SuppressWarnings("unused")
  public ClassificationSummaryImpl getClassificationSummaryImpl() {
    return (ClassificationSummaryImpl) classificationSummary;
  }

  // auxiliary method to enable MyBatis access to classificationSummary
  @SuppressWarnings("unused")
  public void setClassificationSummaryImpl(ClassificationSummaryImpl classificationSummary) {
    this.classificationSummary = classificationSummary;
  }

  // auxiliary method to enable MyBatis access to objectReference
  public ObjectReferenceImpl getObjectReferenceImpl() {
    return (ObjectReferenceImpl) objectReference;
  }

  // auxiliary method to enable MyBatis access to objectReference
  public void setObjectReferenceImpl(ObjectReferenceImpl objectReferenceImpl) {
    this.objectReference = objectReferenceImpl;
  }

  protected boolean canEqual(Object other) {
    return (!(other instanceof AttachmentSummaryImpl));
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id, taskId, created, modified, classificationSummary, objectReference, channel, received);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof AttachmentSummaryImpl)) {
      return false;
    }
    AttachmentSummaryImpl other = (AttachmentSummaryImpl) obj;
    if (other.canEqual(this)) {
      return false;
    }
    return Objects.equals(id, other.id)
        && Objects.equals(taskId, other.taskId)
        && Objects.equals(created, other.created)
        && Objects.equals(modified, other.modified)
        && Objects.equals(classificationSummary, other.classificationSummary)
        && Objects.equals(objectReference, other.objectReference)
        && Objects.equals(channel, other.channel)
        && Objects.equals(received, other.received);
  }

  @Override
  public String toString() {
    return "AttachmentSummaryImpl [id="
        + id
        + ", taskId="
        + taskId
        + ", created="
        + created
        + ", modified="
        + modified
        + ", classificationSummary="
        + classificationSummary
        + ", objectReference="
        + objectReference
        + ", channel="
        + channel
        + ", received="
        + received
        + "]";
  }
}
