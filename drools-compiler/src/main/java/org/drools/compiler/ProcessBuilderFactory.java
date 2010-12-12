package org.drools.compiler;

import org.drools.util.ServiceRegistryImpl;


public class ProcessBuilderFactory {

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
        ServiceRegistryImpl.getInstance().addDefault( ProcessBuilderFactoryService.class, "org.jbpm.process.builder.ProcessBuilderFactoryServiceImpl" );            
        setProcessBuilderFactoryService(ServiceRegistryImpl.getInstance().get( ProcessBuilderFactoryService.class ) );	    
	}
	
}
