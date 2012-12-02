package org.kie.concurrent;

import org.kie.util.ServiceRegistryImpl;

public class ExecutorProviderFactory {

    private static class ExecutorProviderHolder {
        private static final KieExecutors executorProvider = ServiceRegistryImpl.getInstance().get( KieExecutors.class );
    }

    public static KieExecutors getExecutorProvider() {
        return ExecutorProviderHolder.executorProvider;
    }
}
