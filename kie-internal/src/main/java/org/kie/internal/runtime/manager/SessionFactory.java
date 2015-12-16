/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
package org.kie.internal.runtime.manager;

import org.kie.api.runtime.KieSession;

/**
 * Factory that produces <code>KieSession</code> instances.
 *
 */
public interface SessionFactory {

	/**
	 * Produces new instance of <code>KieSession</code>
	 * @return new instance of <code>KieSession</code>
	 */
    KieSession newKieSession();
    
    /**
     * Loads <code>KieSession</code> form data store (such as db) based on given id.
     * @param sessionId identifier of ksession
     * @return loaded instance of <code>KieSession</code>
     * @throws RuntimeException in case session cannot be loaded
     */
    KieSession findKieSessionById(Long sessionId);
    
    /**
     * Closes the factory and releases all resources
     */
    void close();
}
