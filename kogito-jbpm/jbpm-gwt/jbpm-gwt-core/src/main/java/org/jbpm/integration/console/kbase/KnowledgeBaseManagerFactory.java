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

/**
 * Factory for providing <code>KnowledgeBaseManager</code> instances
 */
public class KnowledgeBaseManagerFactory {

    /**
     * Returns new instance of <code>KnowledgeBaseManager</code>  that is either default implementation
     * or custom one that is defined by system property: <code>jbpm.knowledgebase.manager</code><br/>
     * Example:<br/>
     * -Djbpm.knowledgebase.manager=com.company.jbpm.CustomKnowledgeBaseManager
     * <br/>
     * Custom implementation must implement <code>org.jbpm.integration.console.kbase.KnowledgeBaseManager</code> interface<br/>
     * 
     * Default implementation is <code>org.jbpm.integration.console.kbase.DefaultKnowledgeBaseManager</code>
     * that builds knowledge base according to settings given in either:
     * <ul>
     *  <li>default.jbpm.console.properties - properties that are bundled with jbpm as defaults - read only</li>
     *  <li>jbpm.console.properties - custom configuration of jbpm console</li>
     * </ul>
     * @return new instance of <code>KnowledgeBaseManager</code>
     */
    @SuppressWarnings("unchecked")
    public static KnowledgeBaseManager newKnowledgeBaseManager() {
        String knowledgeBaseManager = System.getProperty("jbpm.knowledgebase.manager");
        if (knowledgeBaseManager == null) {
            return new DefaultKnowledgeBaseManager();
        }
        
        DefaultKnowledgeBaseManager knowledgeBaseManagerInstance = null;
        try {
            // build session manager based on given class
            Class<DefaultKnowledgeBaseManager> knowledgeBaseManagerClass = 
                    (Class<DefaultKnowledgeBaseManager>) Class.forName(knowledgeBaseManager);
            knowledgeBaseManagerInstance = knowledgeBaseManagerClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot create KnowledgeBaseManager from class " + knowledgeBaseManager, e);
        }
        
        return knowledgeBaseManagerInstance;
    }
}
