package org.kie.api.runtime;

public interface Environment {

    Object get(String identifier);

    void set(String identifier,
             Object object);

    void setDelegate(Environment delegate);

}
