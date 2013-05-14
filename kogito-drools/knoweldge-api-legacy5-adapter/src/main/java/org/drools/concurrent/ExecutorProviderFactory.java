package org.drools.concurrent;

import org.drools.util.ServiceRegistryImpl;

public class ExecutorProviderFactory {

    private static class ExecutorProviderHolder {
        private static final ExecutorProvider executorProvider = ServiceRegistryImpl.getInstance().get( ExecutorProvider.class );
    }

    public static ExecutorProvider getExecutorProvider() {
        return ExecutorProviderHolder.executorProvider;
    }
}
