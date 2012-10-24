/*
 * Copyright 2010 JBoss Inc
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

package org.drools.agent;

import org.drools.PropertiesConfiguration;

/**
 * <p> This class configures and allows inspection of the current configuration of a {@link org.drools.agent.KnowledgeAgent KnowledgeAgent}</p>
 * <ul>
 * <li><code><b>drools.agent.scanResources</b> = &lt;<b>true</b>|false&gt;</code></li>
 * Enables(true) or Disables(false) the continuous scan of resources.
 * <li><code><b>drools.agent.scanDirectories</b> = &lt;<b>true</b>|false&gt;</code></li>
 * Enables(true) or Disables(false) the scan of directories. 
 * <li><code><b>drools.agent.newInstance</b> = &lt;<b>true</b>|false&gt;</code></li>
 * Setting the newInstance option to true (default) will make the agent to create a brand new KnowledgeBase 
 * every time there is a change to the source assets. Previously created sessions will continue
 * to reference and use the previously existing KnowledgeBase, so this option should be
 * used in scenarios where sessions are short lived, as they are never updated. 
 * Setting this option to false will make the agent keep and incrementally update the existing 
 * knowledge base, automatically updating all existing sessions. This option should be used for
 * scenarios with long living sessions that should be updated automatically when the source
 * assets change. 
 * <li><code><b>drools.agent.monitorChangeSetEvents</b> = &lt;<b>true</b>|false&gt;</code></li>
 * Enables(true) the monitoring of changes in the resources.
 * <li><code><b>drools.agent.useKBaseClassLoaderForCompiling</b> = &lt;<b>false</b>|true&gt;</code></li>
 * If this option is set to true, the agent will use the same classloader to compile the resources
 * that it uses on the knowledge base. If false (default), it will use an exclusive classloader for
 * compilation.
 * <li><code><b>drools.agent.validationTimeout</b> = &lt;milliseconds&gt;</code></li>
 * Sets the timeout for the validation of remote XML schemas. Default is 0 (that means no timeout).
 * </ul>
 * @see org.drools.agent.KnowledgeAgent
 */
public interface KnowledgeAgentConfiguration
        extends
        PropertiesConfiguration {

    /**
     * @return true if the continuous scan of resources is enabled. false otherwise.
     */
    public boolean isScanResources();

    /**
     * @return true if the continuous scan of directories is enabled. false otherwise.
     */
    public boolean isScanDirectories();

    /**
     * @return true if the monitoring of changes on resources is enabled. false otherwise.
     */
    public boolean isMonitorChangeSetEvents();

    /**
    * <p>Returns the configured state of the <code>drools.agent.newInstance</code> option.</p> 
    * 
    * <p>If true (default), the agent creates a brand new KnowledgeBase 
    * every time there is a change to the source assets. Previously created sessions will continue
    * to reference and use the previously existing KnowledgeBase, so this option should be
    * used in scenarios where sessions are short lived, as they are never updated. </p>
    * 
    * <p>If false will, the agent keeps and incrementally update the existing 
    * knowledge base, automatically updating all existing sessions. This option should be used for
    * scenarios with long living sessions that should be updated automatically when the source
    * assets change.</p> 
    * 
    * @return the configured state of the <code>drools.agent.newInstance</code> option.
    */
    public boolean isNewInstance();

    /**
     * @return true if the agent is configured to use the same classloader for compilation
     *         and runtime execution. false otherwise. 
     */
    public boolean isUseKBaseClassLoaderForCompiling();

    /**
     * @return the timeout in milliseconds for the validation of remote XML schemas.
     */
    public int getValidationTimeout();

}
