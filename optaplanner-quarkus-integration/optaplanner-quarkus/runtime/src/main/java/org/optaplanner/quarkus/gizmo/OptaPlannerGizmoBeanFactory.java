package org.optaplanner.quarkus.gizmo;

public interface OptaPlannerGizmoBeanFactory {
    <T> T newInstance(Class<T> clazz);
}
