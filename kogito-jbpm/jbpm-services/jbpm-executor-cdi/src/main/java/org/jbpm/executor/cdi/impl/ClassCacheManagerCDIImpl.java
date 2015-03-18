package org.jbpm.executor.cdi.impl;


import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

import org.jbpm.executor.impl.ClassCacheManager;

@ApplicationScoped
public class ClassCacheManagerCDIImpl extends ClassCacheManager {

	@Override
	@PreDestroy
    public void dispose() {
		super.dispose();
	}
}
