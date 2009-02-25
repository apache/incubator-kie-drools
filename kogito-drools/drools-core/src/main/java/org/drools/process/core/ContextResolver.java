package org.drools.process.core;

public interface ContextResolver {
	
	Context resolveContext(String contextId, Object param);

}
