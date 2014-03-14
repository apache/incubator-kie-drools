package org.drools.compiler.compiler;

import org.kie.internal.utils.ServiceRegistryImpl;


public class ProcessBuilderFactory {

    private static final String PROVIDER_CLASS = "org.jbpm.process.builder.ProcessBuilderFactoryServiceImpl";

    private static ProcessBuilderFactoryService provider;

    public static ProcessBuilder newProcessBuilder(PackageBuilder packageBuilder) {
        return getProcessBuilderFactoryService().newProcessBuilder(packageBuilder);
    }

    public static synchronized void setProcessBuilderFactoryService(ProcessBuilderFactoryService provider) {
        ProcessBuilderFactory.provider = provider;
    }

    public static synchronized ProcessBuilderFactoryService getProcessBuilderFactoryService() {
        if (provider == null) {
            loadProvider();
        }
        return provider;
    }

    private static void loadProvider() {
        ServiceRegistryImpl.getInstance().addDefault( ProcessBuilderFactoryService.class, PROVIDER_CLASS );
        setProcessBuilderFactoryService(ServiceRegistryImpl.getInstance().get( ProcessBuilderFactoryService.class ) );
    }


    public static synchronized void loadProvider(ClassLoader cl) {
        if (provider == null) {
            try {
                provider = (ProcessBuilderFactoryService)Class.forName(PROVIDER_CLASS, true, cl).newInstance();
            } catch (Exception e) { }
        }
    }
}
