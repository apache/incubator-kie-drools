package org.jbpm.services.task.persistence;

import javax.persistence.EntityManagerFactory;

import org.kie.api.task.TaskLifeCycleEventListener;
import org.kie.internal.task.api.TaskPersistenceContext;

public abstract class PersistableEventListener implements TaskLifeCycleEventListener {

	private EntityManagerFactory emf;

	public PersistableEventListener(EntityManagerFactory emf) {
		this.emf = emf;
	}

	protected TaskPersistenceContext getPersistenceContext(TaskPersistenceContext persistenceContext) {
		if (emf != null) {
			return new JPATaskPersistenceContext(emf.createEntityManager()) {

				@Override
				public void close() {
					em.flush();
					super.close();
				}
				
			};
		}

		return persistenceContext;
	}

	protected void cleanup(TaskPersistenceContext persistenceContext) {
		if (emf != null) {
			persistenceContext.close();
		}
	}



}
