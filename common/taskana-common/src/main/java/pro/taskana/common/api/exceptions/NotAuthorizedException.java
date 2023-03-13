package pro.taskana.common.api.exceptions;

import java.util.Arrays;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.internal.util.MapCreator;

/**
 * This exception is thrown when the current user is not in a certain {@linkplain TaskanaRole role}
 * it is supposed to be.
 */
public class NotAuthorizedException extends TaskanaException {

  public static final String ERROR_KEY = "NOT_AUTHORIZED";
  private final String currentUserId;
  private final TaskanaRole[] roles;

  public NotAuthorizedException(String currentUserId, TaskanaRole... roles) {
    super(
        String.format(
            "Not authorized. The current user '%s' is not member of role(s) '%s'.",
            currentUserId, Arrays.toString(roles)),
        ErrorCode.of(ERROR_KEY, MapCreator.of("roles", roles, "currentUserId", currentUserId)));

    this.currentUserId = currentUserId;
    this.roles = roles;
  }

  public TaskanaRole[] getRoles() {
    return roles;
  }

  public String getCurrentUserId() {
    return currentUserId;
  }
}
