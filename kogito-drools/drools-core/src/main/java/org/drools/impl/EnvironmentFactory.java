package org.drools.impl;

import org.drools.runtime.Environment;

public class EnvironmentFactory {
	
	private static ThreadLocal<Environment> environment = new ThreadLocal<Environment>();
	
	public static Environment newEnvironment() {
		Environment environment = EnvironmentFactory.environment.get();
		if (environment == null) {
			environment = new EnvironmentImpl();
			EnvironmentFactory.environment.set(environment);
		}
		return environment;
	}

}
