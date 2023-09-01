package org.kie.internal.concurrent;

import org.kie.api.concurrent.KieExecutors;
import org.kie.api.internal.utils.KieService;

public class ExecutorProviderFactory {

    private static class ExecutorProviderHolder {
        private static final KieExecutors executorProvider = KieService.load(KieExecutors.class);
    }

    public static KieExecutors getExecutorProvider() {
        return ExecutorProviderHolder.executorProvider;
    }
}
