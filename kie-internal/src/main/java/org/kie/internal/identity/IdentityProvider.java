package org.kie.internal.identity;

import java.util.List;

public interface IdentityProvider {

    public static final String UNKNOWN_USER_IDENTITY = "unknown";

    String getName();

    List<String> getRoles();

    boolean hasRole(String role);

    default void setContextIdentity(String userId) {
        // do nothing
    }

    default void removeContextIdentity() {
        // do nothing
    }
}
