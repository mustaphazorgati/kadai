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

package io.kadai.common.internal.security;

import static java.util.function.Predicate.not;

import io.kadai.common.api.security.CurrentUserContext;
import io.kadai.common.api.security.GroupPrincipal;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.security.auth.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CurrentUserContextImpl implements CurrentUserContext {

  private static final String GET_UNIQUE_SECURITY_NAME_METHOD = "getUniqueSecurityName";
  private static final String GET_CALLER_SUBJECT_METHOD = "getCallerSubject";
  private static final String WSSUBJECT_CLASSNAME = "com.ibm.websphere.security.auth.WSSubject";

  private static final Logger LOGGER = LoggerFactory.getLogger(CurrentUserContextImpl.class);
  private final boolean shouldUseLowerCaseForAccessIds;
  private boolean runningOnWebSphere;

  public CurrentUserContextImpl(boolean shouldUseLowerCaseForAccessIds) {
    this.shouldUseLowerCaseForAccessIds = shouldUseLowerCaseForAccessIds;
    try {
      Class.forName(WSSUBJECT_CLASSNAME);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("WSSubject detected. Assuming that Kadai runs on IBM WebSphere.");
      }
      runningOnWebSphere = true;
    } catch (ClassNotFoundException e) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("No WSSubject detected. Using JAAS subject further on.");
      }
      runningOnWebSphere = false;
    }
  }

  @Override
  public String getUserid() {
    return runningOnWebSphere ? getUserIdFromWsSubject() : getUserIdFromJaasSubject();
  }

  @Override
  @SuppressWarnings("removal")
  public List<String> getGroupIds() {
    // TODO replace with Subject.current() when migrating to newer Version than 17
    Subject subject = Subject.getSubject(AccessController.getContext());
    LOGGER.trace("Subject of caller: {}", subject);
    if (subject != null) {
      Set<GroupPrincipal> groups = subject.getPrincipals(GroupPrincipal.class);
      LOGGER.trace("Public groups of caller: {}", groups);
      return groups.stream()
          .map(Principal::getName)
          .filter(Objects::nonNull)
          .map(this::convertAccessId)
          .toList();
    }
    LOGGER.trace("No groupIds found in subject!");
    return Collections.emptyList();
  }

  @Override
  public List<String> getAccessIds() {
    List<String> accessIds = new ArrayList<>(getGroupIds());
    accessIds.add(getUserid());
    return accessIds;
  }

  /**
   * Returns the unique security name of the first public credentials found in the WSSubject as
   * userid.
   *
   * @return the userid of the caller. If the userid could not be obtained, null is returned.
   */
  private String getUserIdFromWsSubject() {
    try {
      Class<?> wsSubjectClass = Class.forName(WSSUBJECT_CLASSNAME);
      Method getCallerSubjectMethod =
          wsSubjectClass.getMethod(GET_CALLER_SUBJECT_METHOD, (Class<?>[]) null);
      Subject callerSubject = (Subject) getCallerSubjectMethod.invoke(null, (Object[]) null);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Subject of caller: {}", callerSubject);
      }
      if (callerSubject != null) {
        Set<Object> publicCredentials = callerSubject.getPublicCredentials();
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("Public credentials of caller: {}", publicCredentials);
        }
        return publicCredentials.stream()
            .map(
                // we could use CheckedFunction#wrap here, but this either requires a dependency
                // to kadai-common or an inclusion of the class CheckedFunction in this module.
                // The first is not possible due to a cyclic dependency.
                // The second is not desired, since this module is a very slim security module and
                // the inclusion of CheckedFunction and its transitive dependencies would increase
                // the module scope and introduce inconsistency.
                credential -> {
                  try {
                    return credential
                        .getClass()
                        .getMethod(GET_UNIQUE_SECURITY_NAME_METHOD, (Class<?>[]) null)
                        .invoke(credential, (Object[]) null);
                  } catch (Exception e) {
                    throw new SecurityException("Could not retrieve principal", e);
                  }
                })
            .peek(
                o ->
                    LOGGER.debug(
                        "Returning the unique security name of first public credential: {}", o))
            .map(Object::toString)
            .map(this::convertAccessId)
            .findFirst()
            .orElse(null);
      }
    } catch (Exception e) {
      LOGGER.warn("Could not get user from WSSubject. Going ahead unauthorized.");
    }
    return null;
  }

  @SuppressWarnings("removal")
  private String getUserIdFromJaasSubject() {
    // TODO replace with Subject.current() when migrating to newer Version than 17
    Subject subject = Subject.getSubject(AccessController.getContext());
    LOGGER.trace("Subject of caller: {}", subject);
    if (subject != null) {
      Set<Principal> principals = subject.getPrincipals();
      LOGGER.trace("Public principals of caller: {}", principals);
      return principals.stream()
          .filter(not(GroupPrincipal.class::isInstance))
          .map(Principal::getName)
          .filter(Objects::nonNull)
          .map(this::convertAccessId)
          .findFirst()
          .orElse(null);
    }
    LOGGER.trace("No userId found in subject!");
    return null;
  }

  private String convertAccessId(String accessId) {
    String toReturn = accessId;
    if (shouldUseLowerCaseForAccessIds) {
      toReturn = accessId.toLowerCase();
    }
    LOGGER.trace("Found AccessId '{}'. Returning AccessId '{}' ", accessId, toReturn);
    return toReturn;
  }
}
