/**
 * Copyright 2012 JBoss Inc
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
package org.jbpm.integration.console.kbase;

import org.kie.KnowledgeBase;

/**
 * Manager interface responsible for maintaining knowledge base that includes:
 * <ul>
 *  <li>creation</li>
 *  <li>synchronization with data store</li>
 *  <li>disposal</li>
 * </ul>
 * Various implementation can be provided to support different ways of building knowledge base, 
 * for instance from different sources.
 *
 */
public interface KnowledgeBaseManager {

    /**
     * Returns knowledge base maintained by this manager
     * @return knowledge base fully initialized
     */
    KnowledgeBase getKnowledgeBase();
    
    /**
     * Sync packages currently in knowledge base with underlying asset store
     */
    void syncPackages();

    /**
     * Dispose knowledge base maintained by this manager
     * including all supporting components (for instance knowledge agent)
     */
    void dispose();
}
