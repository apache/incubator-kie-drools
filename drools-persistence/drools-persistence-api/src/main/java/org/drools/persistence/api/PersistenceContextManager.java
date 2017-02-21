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

package org.drools.persistence.api;

import org.kie.api.runtime.ExecutableRunner;
import org.kie.api.command.Command;
import org.kie.api.runtime.KieSession;

import javax.transaction.Synchronization;

public interface PersistenceContextManager {
    
    /**
     * @return a {@link PersistenceContext} instance containing the Application Scoped EntityManager or persistence helper class.
     */
    PersistenceContext getApplicationScopedPersistenceContext();
    
    /**
     * @return a {@link PersistenceContext} instance containing the Command Scoped EntityManager or persistence helper class.
     */
    PersistenceContext getCommandScopedPersistenceContext();
   
    /**
     * This method should be called at the beginning of a {@link ExecutableRunner#execute(org.kie.api.command.Command)} method,
     * when the given {@link ExecutableRunner} instance is responsible for handling persistence.
     * See the {@link PersistableRunner} class.
     * </p>
     * The first responsibility of this method is to make sure that the Command Scoped EntityManager (CSEM) joins
     * the ongoing transaction.
     * </p>
     * When the CSEM is internally managed, this method is also responsible for creating a new CSEM for use during execution
     * of the {@link Command} or operation being executed by the {@link KieSession}.
     */
    void beginCommandScopedEntityManager();
    
    /**
     * This method should only called in the {@link Synchronization#afterCompletion(int)} method.
     * </p>
     * It is responsible for cleaning up the Command Scoped EntityManager (CSEM) instance, but <i>only</i> when 
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
