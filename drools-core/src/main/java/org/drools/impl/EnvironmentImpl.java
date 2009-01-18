package org.drools.impl;

import java.util.HashMap;
import java.util.Map;

import org.drools.runtime.Environment;

public class EnvironmentImpl implements Environment {

	private Map<String, Object> environment = new HashMap<String, Object>();
	
	public Object get(String name) {
		return environment.get(name);
	}

	public void set(String name, Object object) {
		environment.put(name, object);
	}

}
