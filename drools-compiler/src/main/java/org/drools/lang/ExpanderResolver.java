package org.drools.lang;

public interface ExpanderResolver {
	
	Expander get(String name, String config);

}
