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

package io.kadai.spi.task.internal;

import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.exceptions.SystemException;
import io.kadai.common.internal.util.SpiLoader;
import io.kadai.spi.task.api.BeforeRequestChangesProvider;
import io.kadai.task.api.models.Task;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeforeRequestChangesManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(BeforeRequestChangesManager.class);

  private final List<BeforeRequestChangesProvider> beforeRequestChangesProviders;

  public BeforeRequestChangesManager(KadaiEngine kadaiEngine) {
    beforeRequestChangesProviders = SpiLoader.load(BeforeRequestChangesProvider.class);
    for (BeforeRequestChangesProvider serviceProvider : beforeRequestChangesProviders) {
      serviceProvider.initialize(kadaiEngine);
      LOGGER.info(
          "Registered BeforeRequestChangesProvider service provider: {}",
          serviceProvider.getClass().getName());
    }
    if (beforeRequestChangesProviders.isEmpty()) {
      LOGGER.info(
          "No BeforeRequestChangesProvider service provider found. "
              + "Running without any BeforeRequestChangesProvider implementation.");
    }
  }

  public Task beforeRequestChanges(Task task) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Sending Task to BeforeRequestChangesProvider service providers: {}", task);
    }
    for (BeforeRequestChangesProvider serviceProvider : beforeRequestChangesProviders) {
      try {
        task = serviceProvider.beforeRequestChanges(task);
      } catch (Exception e) {
        throw new SystemException(
            String.format(
                "service provider '%s' threw an exception", serviceProvider.getClass().getName()),
            e);
      }
    }
    return task;
  }
}
