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

package org.junit.runners.model;

// Testcontainers currently requires junit4 as a runtime dependency.
// Because we use junit5 we have to use this workaround to "simulate" the classes testcontainers
// requires in the classpath. They are not used unless a junit4 runtime is used.
// See: https://github.com/testcontainers/testcontainers-java/issues/970#issuecomment-625044008
@SuppressWarnings("unused")
public interface Statement {}
