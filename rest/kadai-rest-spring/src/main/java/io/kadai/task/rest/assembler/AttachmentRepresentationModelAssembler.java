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

package io.kadai.task.rest.assembler;

import io.kadai.classification.rest.assembler.ClassificationSummaryRepresentationModelAssembler;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.models.Attachment;
import io.kadai.task.internal.models.AttachmentImpl;
import io.kadai.task.rest.models.AttachmentRepresentationModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/** EntityModel assembler for {@link AttachmentRepresentationModel}. */
@Component
public class AttachmentRepresentationModelAssembler
    implements RepresentationModelAssembler<Attachment, AttachmentRepresentationModel> {

  private final TaskService taskService;

  private final ClassificationSummaryRepresentationModelAssembler classificationSummaryAssembler;
  private final ObjectReferenceRepresentationModelAssembler objectReferenceAssembler;

  @Autowired
  public AttachmentRepresentationModelAssembler(
      TaskService taskService,
      ClassificationSummaryRepresentationModelAssembler classificationSummaryAssembler,
      ObjectReferenceRepresentationModelAssembler objectReferenceAssembler) {
    this.taskService = taskService;
    this.classificationSummaryAssembler = classificationSummaryAssembler;
    this.objectReferenceAssembler = objectReferenceAssembler;
  }

  @NonNull
  @Override
  public AttachmentRepresentationModel toModel(@NonNull Attachment attachment) {
    AttachmentRepresentationModel repModel = new AttachmentRepresentationModel();
    repModel.setAttachmentId(attachment.getId());
    repModel.setTaskId(attachment.getTaskId());
    repModel.setCreated(attachment.getCreated());
    repModel.setModified(attachment.getModified());
    repModel.setReceived(attachment.getReceived());
    repModel.setClassificationSummary(
        classificationSummaryAssembler.toModel(attachment.getClassificationSummary()));
    repModel.setObjectReference(objectReferenceAssembler.toModel(attachment.getObjectReference()));
    repModel.setChannel(attachment.getChannel());
    repModel.setCustomAttributes(attachment.getCustomAttributeMap());
    return repModel;
  }

  public Attachment toEntityModel(AttachmentRepresentationModel attachmentRepresentationModel) {
    AttachmentImpl attachment = (AttachmentImpl) taskService.newAttachment();
    attachment.setId(attachmentRepresentationModel.getAttachmentId());
    attachment.setTaskId(attachmentRepresentationModel.getTaskId());
    attachment.setCreated(attachmentRepresentationModel.getCreated());
    attachment.setModified(attachmentRepresentationModel.getModified());
    attachment.setReceived(attachmentRepresentationModel.getReceived());
    attachment.setClassificationSummary(
        classificationSummaryAssembler.toEntityModel(
            attachmentRepresentationModel.getClassificationSummary()));
    if (attachmentRepresentationModel.getObjectReference() != null) {
      attachment.setObjectReference(
          objectReferenceAssembler.toEntity(attachmentRepresentationModel.getObjectReference()));
    }
    attachment.setChannel(attachmentRepresentationModel.getChannel());
    attachment.setCustomAttributeMap(attachmentRepresentationModel.getCustomAttributes());
    return attachment;
  }
}
