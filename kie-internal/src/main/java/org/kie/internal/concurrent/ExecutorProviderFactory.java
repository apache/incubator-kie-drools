package org.kie.internal.concurrent;

import org.kie.concurrent.KieExecutors;
import org.kie.internal.utils.ServiceRegistryImpl;

public class ExecutorProviderFactory {

    private static class ExecutorProviderHolder {
        private static final KieExecutors executorProvider = ServiceRegistryImpl.getInstance().get( KieExecutors.class );
    }

    public static KieExecutors getExecutorProvider() {
        return ExecutorProviderHolder.executorProvider;
    }
}
