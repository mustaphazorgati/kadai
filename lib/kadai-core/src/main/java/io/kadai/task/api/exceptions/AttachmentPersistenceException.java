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

package io.kadai.task.api.exceptions;

import io.kadai.common.api.exceptions.ErrorCode;
import io.kadai.common.api.exceptions.KadaiException;
import io.kadai.task.api.models.Attachment;
import io.kadai.task.api.models.Task;
import java.util.Map;

/**
 * This exception is thrown when an {@linkplain Attachment} should be inserted to the DB, but it
 * does already exist. <br>
 * This may happen when a not inserted {@linkplain Attachment} with the same {@linkplain
 * Attachment#getId() id} will be added twice on a {@linkplain Task}. This can't happen if the
 * correct {@linkplain Task}-Methods will be used instead of the List ones.
 */
public class AttachmentPersistenceException extends KadaiException {
  public static final String ERROR_KEY = "ATTACHMENT_ALREADY_EXISTS";
  private final String attachmentId;
  private final String taskId;

  public AttachmentPersistenceException(String attachmentId, String taskId, Throwable cause) {
    super(
        String.format(
            "Cannot insert Attachment with id '%s' for Task with id '%s' "
                + "because it already exists.",
            attachmentId, taskId),
        ErrorCode.of(
            ERROR_KEY,
            Map.ofEntries(
                Map.entry("attachmentId", ensureNullIsHandled(attachmentId)),
                Map.entry("taskId", ensureNullIsHandled(taskId)))),
        cause);
    this.attachmentId = attachmentId;
    this.taskId = taskId;
  }

  public String getAttachmentId() {
    return attachmentId;
  }

  public String getTaskId() {
    return taskId;
  }
}
