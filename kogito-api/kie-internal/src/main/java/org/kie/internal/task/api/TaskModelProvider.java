package org.kie.internal.task.api;

import org.kie.internal.utils.ServiceRegistryImpl;

public class TaskModelProvider {
	
    private static final String PROVIDER_CLASS = "org.jbpm.services.task.persistence.TaskModelProviderImpl";

    private static TaskModelProviderService provider;

    public static TaskModelFactory getFactory() {
        return getTaskModelProviderService().getTaskModelFactory();
    }

    public static synchronized void setTaskModelProviderService(TaskModelProviderService provider) {
    	TaskModelProvider.provider = provider;
    }

    public static synchronized TaskModelProviderService getTaskModelProviderService() {
        if (provider == null) {
            loadProvider();
        }
        return provider;
    }

    private static void loadProvider() {
        ServiceRegistryImpl.getInstance().addDefault( TaskModelProviderService.class, PROVIDER_CLASS );
        setTaskModelProviderService(ServiceRegistryImpl.getInstance().get( TaskModelProviderService.class ) );
    }


    public static synchronized void loadProvider(ClassLoader cl) {
        if (provider == null) {
            try {
                provider = (TaskModelProviderService) Class.forName(PROVIDER_CLASS, true, cl).newInstance();
            } catch (Exception e) { }
        }
    }

}
