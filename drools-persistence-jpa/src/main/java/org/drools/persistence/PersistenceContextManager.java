/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.persistence;

import javax.persistence.EntityManager;
import javax.transaction.Synchronization;

import org.drools.core.command.CommandService;
import org.kie.api.command.Command;
import org.kie.api.runtime.KieSession;

public interface PersistenceContextManager {
    
    /**
     * @return a {@link PersistenceContext} instance containing the Application Scoped {@link EntityManager}.
     */
    PersistenceContext getApplicationScopedPersistenceContext();
    
    /**
     * @return a {@link PersistenceContext} instance containing the Command Scoped {@link EntityManager}.
     */
    PersistenceContext getCommandScopedPersistenceContext();
   
    /**
     * This method should be called at the beginning of a {@link CommandService#execute(org.kie.api.command.Command)} method, 
     * when the given {@link CommandService} instance is responsible for handling persistence. 
     * See the {@link SingleSessionCommandService} class.
     * </p>
     * The first responsibility of this method is to make sure that the Command Scoped {@link EntityManager} (CSEM) joins
     * the ongoing transaction.
     * </p>
     * When the CSEM is internally managed, this method is also responsible for creating a new CSEM for use during execution
     * of the {@link Command} or operation being executed by the {@link KieSession}.
     */
    void beginCommandScopedEntityManager();
    
    /**
     * This method should only called in the {@link Synchronization#afterCompletion(int)} method.
     * </p>
     * It is responsible for cleaning up the Command Scoped {@link EntityManager} (CSEM) instance, but <i>only</i> when 
     * the CSEM is an <i>internal</i> one, and not one supplied (and managed) by the user. 
     * </p>
     * If the CSEM is (internally) managed, then this method will take the necessary actions in order to make sure that a 
     * new CSEM can be generated at the beginning of the next operation or command on the persistent {@link KieSession}.
     * </p>
     * if the CSEM is supplied (and managed) by the user, this method will do nothing with the CSEM.
     */
    void endCommandScopedEntityManager();

    /**
     * Executes the necessary actions in order to clean up and dispose of the internal fields of this instance.
     */
    void dispose();
}
