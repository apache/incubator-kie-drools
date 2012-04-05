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
 * <ul>
 * <li>drools.agent.scanResources = &lt;true|false&gt;</li>
 * <li>drools.agent.scanDirectories = &lt;true|false&gt;</li>
 * <li>drools.agent.newInstance = &lt;true|false&gt;</li>
 * <li>drools.agent.monitorChangeSetEvents = &lt;true|false&gt;</li>
 * </ul>
 * @see org.drools.agent.KnowledgeAgent
 * @see org.drools.agent.KnowledgeAgentConfiguration 
 */
public interface KnowledgeAgentConfiguration
    extends
    PropertiesConfiguration {

    public boolean isScanResources();

    public boolean isScanDirectories();

    public boolean isMonitorChangeSetEvents();

    /**
     * @return true create a KnowledgeBase each time, false for incremental rebuilding of the KnowledgeBase
     */
    public boolean isNewInstance();

    public boolean isUseKBaseClassLoaderForCompiling();

    public int     getValidationTimeout();

}
