package org.kie.internal.task.api;

import java.util.List;

public interface EventService<T> {

    void registerTaskEventListener(T listener);

    List<T> getTaskEventListeners();

    void clearTaskEventListeners();

    void removeTaskEventListener(T listener);
}
