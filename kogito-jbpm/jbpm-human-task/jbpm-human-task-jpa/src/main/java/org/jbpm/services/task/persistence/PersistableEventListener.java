/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
