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

package io.kadai.testapi;

import static io.kadai.testapi.builder.ClassificationBuilder.newClassification;
import static io.kadai.testapi.builder.ObjectReferenceBuilder.newObjectReference;
import static io.kadai.testapi.builder.UserBuilder.newUser;
import static io.kadai.testapi.builder.WorkbasketBuilder.newWorkbasket;

import io.kadai.testapi.builder.ClassificationBuilder;
import io.kadai.testapi.builder.ObjectReferenceBuilder;
import io.kadai.testapi.builder.UserBuilder;
import io.kadai.testapi.builder.WorkbasketBuilder;
import io.kadai.workbasket.api.WorkbasketType;
import java.util.Random;
import java.util.UUID;

/**
 * Utility-Class providing prepared
 * {@linkplain io.kadai.testapi.builder.EntityBuilder Entity-Builders} to build default entities
 * for tests.
 */
public class DefaultTestEntities {

  private DefaultTestEntities() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Returns the default {@link ClassificationBuilder}.
   *
   * @return the default {@link ClassificationBuilder}
   */
  public static ClassificationBuilder defaultTestClassification() {
    return newClassification()
        .key(UUID.randomUUID().toString().replace("-", ""))
        .domain("DOMAIN_A");
  }

  /**
   * Returns the default {@link WorkbasketBuilder}.
   *
   * @return the default {@link WorkbasketBuilder}
   */
  public static WorkbasketBuilder defaultTestWorkbasket() {
    return newWorkbasket()
        .key(UUID.randomUUID().toString())
        .domain("DOMAIN_A")
        .name("Megabasket")
        .type(WorkbasketType.GROUP)
        .orgLevel1("company");
  }

  /**
   * Returns the default {@link ObjectReferenceBuilder}.
   *
   * @return the default {@link ObjectReferenceBuilder}
   */
  public static ObjectReferenceBuilder defaultTestObjectReference() {
    return newObjectReference()
        .company("Company1")
        .system("System1")
        .systemInstance("Instance1")
        .type("Type1")
        .value("Value1");
  }

  /**
   * Returns a {@link UserBuilder} with random {@linkplain UserBuilder#id(String) id},
   * {@linkplain UserBuilder#firstName(String) firstName} and
   * {@linkplain UserBuilder#lastName(String) lastName}.
   *
   * @return the default {@link ClassificationBuilder}
   */
  public static UserBuilder randomTestUser() {
    return newUser()
        .id(UUID.randomUUID().toString().replace("-", ""))
        .firstName(RandomStringGenerator.generateRandomString(10))
        .lastName(RandomStringGenerator.generateRandomString(12));
  }

  private static class RandomStringGenerator {
    private static final Random RANDOM = new Random(15);

    private static String generateRandomString(int length) {
      // see ascii table for details number -> char conversion.
      return RANDOM
          .ints('0', 'z' + 1)
          .filter(i -> (i <= '9' || i >= 'A') && (i <= 'Z' || i >= 'a'))
          .limit(length)
          .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
          .toString();
    }
  }
}
