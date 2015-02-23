package org.jbpm.kie.services.api;

import java.util.Map;

public interface AttributesAware {

	void addAttribute(String name, String value);
	
	String removeAttribute(String name);
	
	Map<String, String> getAttributes();
}
