/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.management;

import org.drools.core.RuleBaseConfiguration;
import org.kie.api.management.KieBaseConfigurationMonitorMBean;

/**
 * An mbean monitor class for the knowledgebase configuration
 */
public class KieBaseConfigurationMonitor implements KieBaseConfigurationMonitorMBean {
    
    private RuleBaseConfiguration conf;

    public KieBaseConfigurationMonitor(RuleBaseConfiguration conf) {
        this.conf = conf;
    }

    /* (non-Javadoc)
     * @see org.kie.api.management.KBaseConfigurationMonitorMBean#getAlphaNodeHashingThreshold()
     */
    public int getAlphaNodeHashingThreshold() {
        return conf.getAlphaNodeHashingThreshold();
    }

    /* (non-Javadoc)
     * @see org.kie.api.management.KBaseConfigurationMonitorMBean#getAssertBehaviour()
     */
    public String getAssertBehaviour() {
        return conf.getAssertBehaviour().toExternalForm();
    }

    /* (non-Javadoc)
     * @see org.kie.api.management.KBaseConfigurationMonitorMBean#getCompositeKeyDepth()
     */
    public int getCompositeKeyDepth() {
        return conf.getCompositeKeyDepth();
    }

    /* (non-Javadoc)
     * @see org.kie.api.management.KBaseConfigurationMonitorMBean#getEventProcessingMode()
     */
    public String getEventProcessingMode() {
        return conf.getEventProcessingMode().toExternalForm();
    }

    /* (non-Javadoc)
     * @see org.kie.api.management.KBaseConfigurationMonitorMBean#getMaxThreads()
     */
    public int getMaxThreads() {
        return conf.getMaxThreads();
    }

    /* (non-Javadoc)
     * @see org.kie.api.management.KBaseConfigurationMonitorMBean#getSequentialAgenda()
     */
    public String getSequentialAgenda() {
        return conf.getSequentialAgenda().toExternalForm();
    }

    /* (non-Javadoc)
     * @see org.kie.api.management.KBaseConfigurationMonitorMBean#isAdvancedProcessRuleIntegration()
     */
    public boolean isAdvancedProcessRuleIntegration() {
        return conf.isAdvancedProcessRuleIntegration();
    }

    /* (non-Javadoc)
     * @see org.kie.api.management.KBaseConfigurationMonitorMBean#isIndexLeftBetaMemory()
     */
    public boolean isIndexLeftBetaMemory() {
        return conf.isIndexLeftBetaMemory();
    }

    /* (non-Javadoc)
     * @see org.kie.api.management.KBaseConfigurationMonitorMBean#isIndexRightBetaMemory()
     */
    public boolean isIndexRightBetaMemory() {
        return conf.isIndexRightBetaMemory();
    }

    /* (non-Javadoc)
     * @see org.kie.api.management.KBaseConfigurationMonitorMBean#isMaintainTms()
     */
    public boolean isMaintainTms() {
        return conf.isMaintainTms();
    }

    /* (non-Javadoc)
     * @see org.kie.api.management.KBaseConfigurationMonitorMBean#isMBeansEnabled()
     */
    public boolean isMBeansEnabled() {
        return conf.isMBeansEnabled();
    }

    /* (non-Javadoc)
     * @see org.kie.api.management.KBaseConfigurationMonitorMBean#isMultithreadEvaluation()
     */
    public boolean isMultithreadEvaluation() {
        return conf.isMultithreadEvaluation();
    }

    /* (non-Javadoc)
     * @see org.kie.api.management.KBaseConfigurationMonitorMBean#isRemoveIdentities()
     */
    public boolean isRemoveIdentities() {
        return conf.isRemoveIdentities();
    }

    /* (non-Javadoc)
     * @see org.kie.api.management.KBaseConfigurationMonitorMBean#isSequential()
     */
    public boolean isSequential() {
        return conf.isSequential();
    }

    /* (non-Javadoc)
     * @see org.kie.api.management.KBaseConfigurationMonitorMBean#isShareAlphaNodes()
     */
    public boolean isShareAlphaNodes() {
        return conf.isShareAlphaNodes();
    }

    /* (non-Javadoc)
     * @see org.kie.api.management.KBaseConfigurationMonitorMBean#isShareBetaNodes()
     */
    public boolean isShareBetaNodes() {
        return conf.isShareBetaNodes();
    }

}
