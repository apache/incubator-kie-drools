package org.kie.api.runtime;

import java.util.Collection;

public interface Globals {
    Object get(String identifier);

    void set(String identifier,
             Object value);

    void setDelegate(Globals delegate);

    Collection<String> getGlobalKeys();
}
