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

package io.kadai.common.internal.util;

import static io.kadai.common.internal.util.CheckedFunction.wrapping;
import static java.util.function.Predicate.not;

import io.kadai.common.api.exceptions.SystemException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.json.JSONObject;

public class ObjectAttributeChangeDetector {

  private ObjectAttributeChangeDetector() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Determines changes in fields between two objects.
   *
   * @param oldObject the old object for the comparison
   * @param newObject the new object for the comparison
   * @param <T> The generic type parameter
   * @return the details of all changed fields as JSON string
   * @throws SystemException when any parameter is null or the class of oldObject and newObject do
   *     not match
   */
  public static <T> String determineChangesInAttributes(T oldObject, T newObject) {
    if (oldObject == null || newObject == null) {
      throw new SystemException(
          "Null was provided as a parameter. Please provide two objects of the same type");
    }

    Class<?> objectClass = oldObject.getClass();
    if (List.class.isAssignableFrom(objectClass)) {
      return compareLists(oldObject, newObject);
    }

    // this has to be checked after we deal with List data types, because
    // we want to allow different implementations of the List interface to work as well.
    if (!oldObject.getClass().equals(newObject.getClass())
        && !oldObject.getClass().isAssignableFrom(newObject.getClass())) {
      throw new SystemException(
          String.format(
              "The classes differ between the oldObject(%s) and newObject(%s). "
                  + "In order to detect changes properly they should not differ.",
              oldObject.getClass().getName(), newObject.getClass().getName()));
    }

    List<JSONObject> changedAttributes =
        ReflectionUtil.retrieveAllFields(objectClass).stream()
            .peek(field -> field.setAccessible(true))
            .filter(not(field -> "customAttributes".equals(field.getName())))
            .map(wrapping(field -> Triplet.of(field, field.get(oldObject), field.get(newObject))))
            .filter(not(t -> Objects.equals(t.getMiddle(), t.getRight())))
            .map(t -> generateChangedAttribute(t.getLeft(), t.getMiddle(), t.getRight()))
            .toList();

    JSONObject changes = new JSONObject();
    changes.put("changes", changedAttributes);
    return changes.toString();
  }

  private static JSONObject generateChangedAttribute(
      Field field, Object oldValue, Object newValue) {
    JSONObject changedAttribute = new JSONObject();
    changedAttribute.put("fieldName", field.getName());
    changedAttribute.put(
        "oldValue", Optional.ofNullable(oldValue).map(JSONObject::wrap).orElse(""));
    changedAttribute.put(
        "newValue", Optional.ofNullable(newValue).map(JSONObject::wrap).orElse(""));
    return changedAttribute;
  }

  private static <T> String compareLists(T oldObject, T newObject) {
    if (oldObject.equals(newObject)) {
      return "";
    }

    JSONObject changedAttribute = new JSONObject();
    changedAttribute.put("oldValue", oldObject);
    changedAttribute.put("newValue", newObject);

    JSONObject changes = new JSONObject();
    changes.put("changes", changedAttribute);

    return changes.toString();
  }
}
