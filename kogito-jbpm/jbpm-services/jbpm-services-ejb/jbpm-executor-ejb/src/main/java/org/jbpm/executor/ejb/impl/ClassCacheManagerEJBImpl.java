package org.jbpm.executor.ejb.impl;


import javax.annotation.PreDestroy;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;

import org.jbpm.executor.impl.ClassCacheManager;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class ClassCacheManagerEJBImpl extends ClassCacheManager {

	@Override
	@PreDestroy
    public void dispose() {
		super.dispose();
	}
}
