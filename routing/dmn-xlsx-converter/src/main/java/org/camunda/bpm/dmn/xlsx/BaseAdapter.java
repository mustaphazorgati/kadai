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


package org.camunda.bpm.dmn.xlsx;

import java.util.List;
import org.camunda.bpm.dmn.xlsx.api.Spreadsheet;
import org.camunda.bpm.dmn.xlsx.api.SpreadsheetAdapter;

public abstract class BaseAdapter implements SpreadsheetAdapter {

  @Override
  public String determineDecisionName(Spreadsheet context) {
    return context.getName();
  }

  @Override
  public List<org.camunda.bpm.dmn.xlsx.CellContentHandler> getCellContentHandlers(
      Spreadsheet context) {
    return CellContentHandler.DEFAULT_HANDLERS;
  }
}
