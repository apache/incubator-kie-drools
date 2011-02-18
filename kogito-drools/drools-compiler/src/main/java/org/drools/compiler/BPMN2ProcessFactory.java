package org.drools.compiler;

import org.drools.KnowledgeBaseFactoryService;
import org.drools.util.ServiceRegistryImpl;


public class BPMN2ProcessFactory {

	private static BPMN2ProcessProvider provider;

	public static void configurePackageBuilder(PackageBuilder packageBuilder) {
		getBPMN2ProcessProvider().configurePackageBuilder(packageBuilder);
	}

	public static synchronized void setBPMN2ProcessProvider(BPMN2ProcessProvider provider) {
		BPMN2ProcessFactory.provider = provider;
	}

	public static synchronized BPMN2ProcessProvider getBPMN2ProcessProvider() {
		if (provider == null) {
			loadProvider();
		}
		return provider;
	}

	@SuppressWarnings("unchecked")
	private static void loadProvider() {	    	    	    
        ServiceRegistryImpl.getInstance().addDefault( BPMN2ProcessProvider.class,  "org.jbpm.bpmn2.BPMN2ProcessProviderImpl" );            
        setBPMN2ProcessProvider(ServiceRegistryImpl.getInstance().get( BPMN2ProcessProvider.class ) );	    
	}
	
}
