package org.drools.runtime;

public interface Environment {
	
	Object get(String name);
	
	void set(String name, Object object);

}
