package org.jbpm.process.workitem.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;

public class CamelContextService {

	private static final CamelContext instance;
	 
	static {
		try {
			instance = new DefaultCamelContext();
			instance.start();
		} catch (Exception e) {
			throw new RuntimeException("Exception starting Camel context.");
		}
	}
	
    public static CamelContext getInstance() {
        return instance;
    }
    
}
