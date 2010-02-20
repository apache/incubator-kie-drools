package org.drools.compiler;


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
		try {
			// we didn't find anything in properties so lets try and us reflection
			Class<BPMN2ProcessProvider> cls = (Class<BPMN2ProcessProvider>)
				Class.forName("org.drools.bpmn2.BPMN2ProcessProviderImpl");
			setBPMN2ProcessProvider(cls.newInstance());
		} catch (Exception e2) {
			throw new RuntimeException(
				"Provider org.drools.bpmn2.BPMN2ProcessProviderImpl could not be set.", e2);
		}
	}
	
}
