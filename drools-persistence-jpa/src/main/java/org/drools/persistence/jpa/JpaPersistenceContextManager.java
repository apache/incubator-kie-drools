/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 package org.drools.persistence.jpa;

import javax.persistence.EntityManager;

import org.drools.persistence.PersistenceContext;
import org.drools.persistence.PersistenceContextManager;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;

/**
 * This class manages {@link JpaPersistenceContext} objects, and the underlying persistence context ({@link EntityManager}) 
 * instances for a persistent {@link KieSession} and other infrastructure classes that use persistence in KIE projects.
 * </p>
 * (For reference in the following documentation: the {@link EntityManager} is the class used to represent a persistence context)
 * </p>
 * There are 2 issues to take into account when looking at or modifying the code here: <ol>
 * <li>One of the features made available here is the ability for the user to supply their own (Command Scoped) persistence 
 *     context for use by the {@link KieSession}</li>
 * <li>However, significant race-conditions arise when a Command Scoped persistence context is used in one persistent
 * {@link KieSession} by multiple threads. In other words, when multiple threads call operations on a Singleton persistent 
 * {@link KieSession}.</li>
 * </ol>
 * 
 * This class uses {@link ThreadLocal} instances for two things:<ol>
 * <li>The internal Command Scoped {@link EntityManager} instance.</li>
 * <li></li>
 * </ol>
 */
public class JpaPersistenceContextManager extends AbstractPersistenceContextManager
    implements
    PersistenceContextManager {


    public JpaPersistenceContextManager(Environment env) {
        super(env);
    }
    
    public PersistenceContext getApplicationScopedPersistenceContext() {

        return new JpaPersistenceContext( getApplicationScopedEntityManager(), txm );
    }

    public PersistenceContext getCommandScopedPersistenceContext() {
        return new JpaPersistenceContext( getCommandScopedEntityManager(), txm );
    }

    public void beginCommandScopedEntityManager() {
        getCommandScopedPersistenceContext();
    }

}
