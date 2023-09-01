package org.kie.internal.task.api;

import org.kie.api.internal.utils.KieService;

public class TaskModelProvider {

    private static class LazyHolder {
        private static final TaskModelProviderService provider = KieService.load(TaskModelProviderService.class);
    }

    public static TaskModelFactory getFactory() {
        return getTaskModelProviderService().getTaskModelFactory();
    }

    public static TaskModelProviderService getTaskModelProviderService() {
        return LazyHolder.provider;
    }

}
