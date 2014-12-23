package org.jbpm.services.task.persistence;

import javax.persistence.EntityManager;

import org.drools.persistence.jpa.AbstractPersistenceContextManager;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.internal.task.api.TaskPersistenceContext;
import org.kie.internal.task.api.TaskPersistenceContextManager;

public class JPATaskPersistenceContextManager extends
		AbstractPersistenceContextManager implements
		TaskPersistenceContextManager {
	
	public JPATaskPersistenceContextManager(Environment environment) {
		super(environment);
	}

	@Override
	public TaskPersistenceContext getPersistenceContext() {
		Boolean locking = (Boolean) env.get(EnvironmentName.USE_PESSIMISTIC_LOCKING);
        if( locking == null ) { 
            locking = false;
        }
        
        boolean useJTA = true; 
		EntityManager em = getCommandScopedEntityManager();
		return new JPATaskPersistenceContext(em, useJTA, locking);
	}

	@Override
	public void beginCommandScopedEntityManager() {
		getCommandScopedEntityManager();
	}

}
