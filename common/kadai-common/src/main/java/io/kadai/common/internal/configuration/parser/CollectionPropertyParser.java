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

package io.kadai.common.internal.configuration.parser;

import io.kadai.common.api.exceptions.SystemException;
import io.kadai.common.internal.util.ReflectionUtil;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class CollectionPropertyParser<T extends Collection<Object>> implements PropertyParser<T> {

  private final Supplier<T> emptyCollection;
  private final Class<T> targetClass;

  public CollectionPropertyParser(Class<T> targetClass, Supplier<T> emptyCollection) {
    this.emptyCollection = emptyCollection;
    this.targetClass = targetClass;
  }

  @Override
  public Class<?> getTargetClass() {
    return targetClass;
  }

  @Override
  public Optional<T> parse(String value, String separator, Type type) {
    Class<?> rawClass = ReflectionUtil.wrap(ReflectionUtil.getRawClass(type));
    if (!getTargetClass().isAssignableFrom(rawClass)) {
      throw new SystemException(
          String.format(
              "Cannot initialize '%s' because target type '%s' is not a '%s'",
              value, rawClass, getTargetClass()));
    }
    return Optional.ofNullable(value)
        .flatMap(
            string -> {
              Class<?> elementClass =
                  ReflectionUtil.getRawClass(
                      ((ParameterizedType) type).getActualTypeArguments()[0]);

              PropertyParser<?> parser = PropertyParser.getPropertyParser(elementClass);
              T collection = emptyCollection.get();
              for (String it : value.split(Pattern.quote(separator))) {
                it = it.trim();
                if (!it.isEmpty()) {
                  parser.parse(it, separator, elementClass).ifPresent(collection::add);
                }
              }
              if (collection.isEmpty()) {
                return Optional.empty();
              }
              return Optional.of(collection);
            });
  }
}
