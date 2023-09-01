package org.drools.core.management;

import org.drools.core.RuleBaseConfiguration;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.conf.MBeansOption;
import org.kie.api.management.KieBaseConfigurationMonitorMBean;

/**
 * An mbean monitor class for the knowledgebase configuration
 */
public class KieBaseConfigurationMonitor implements KieBaseConfigurationMonitorMBean {

    private RuleBaseConfiguration ruleBaseConf;

    private KieBaseConfiguration kieBaseConf;

    public KieBaseConfigurationMonitor(RuleBaseConfiguration ruleBaseConf, KieBaseConfiguration kieBaseConf) {
        this.ruleBaseConf = ruleBaseConf;
        this.kieBaseConf = kieBaseConf;
    }

    /* (non-Javadoc)
     * @see org.kie.api.management.KBaseConfigurationMonitorMBean#getAlphaNodeHashingThreshold()
     */
    public int getAlphaNodeHashingThreshold() {
        return ruleBaseConf.getAlphaNodeHashingThreshold();
    }

    /* (non-Javadoc)
     * @see org.kie.api.management.KBaseConfigurationMonitorMBean#getAssertBehaviour()
     */
    public String getAssertBehaviour() {
        return ruleBaseConf.getAssertBehaviour().toExternalForm();
    }

    /* (non-Javadoc)
     * @see org.kie.api.management.KBaseConfigurationMonitorMBean#getCompositeKeyDepth()
     */
    public int getCompositeKeyDepth() {
        return ruleBaseConf.getCompositeKeyDepth();
    }

    /* (non-Javadoc)
     * @see org.kie.api.management.KBaseConfigurationMonitorMBean#getEventProcessingMode()
     */
    public String getEventProcessingMode() {
        return ruleBaseConf.getEventProcessingMode().toExternalForm();
    }

    /* (non-Javadoc)
     * @see org.kie.api.management.KBaseConfigurationMonitorMBean#getMaxThreads()
     */
    public int getMaxThreads() {
        return ruleBaseConf.getMaxThreads();
    }

    /* (non-Javadoc)
     * @see org.kie.api.management.KBaseConfigurationMonitorMBean#getSequentialAgenda()
     */
    public String getSequentialAgenda() {
        return ruleBaseConf.getSequentialAgenda().toExternalForm();
    }

    /* (non-Javadoc)
     * @see org.kie.api.management.KBaseConfigurationMonitorMBean#isIndexLeftBetaMemory()
     */
    public boolean isIndexLeftBetaMemory() {
        return ruleBaseConf.isIndexLeftBetaMemory();
    }

    /* (non-Javadoc)
     * @see org.kie.api.management.KBaseConfigurationMonitorMBean#isIndexRightBetaMemory()
     */
    public boolean isIndexRightBetaMemory() {
        return ruleBaseConf.isIndexRightBetaMemory();
    }

    /* (non-Javadoc)
     * @see org.kie.api.management.KBaseConfigurationMonitorMBean#isMaintainTms()
     */
    public boolean isMaintainTms() {
        return ruleBaseConf.isMaintainTms();
    }

    /* (non-Javadoc)
     * @see org.kie.api.management.KBaseConfigurationMonitorMBean#isMBeansEnabled()
     */
    public boolean isMBeansEnabled() {
        return kieBaseConf.getOption(MBeansOption.KEY).isEnabled();
    }

    /* (non-Javadoc)
     * @see org.kie.api.management.KBaseConfigurationMonitorMBean#isRemoveIdentities()
     */
    public boolean isRemoveIdentities() {
        return ruleBaseConf.isRemoveIdentities();
    }

    /* (non-Javadoc)
     * @see org.kie.api.management.KBaseConfigurationMonitorMBean#isSequential()
     */
    public boolean isSequential() {
        return ruleBaseConf.isSequential();
    }

    /* (non-Javadoc)
     * @see org.kie.api.management.KBaseConfigurationMonitorMBean#isShareAlphaNodes()
     */
    public boolean isShareAlphaNodes() {
        return ruleBaseConf.isShareAlphaNodes();
    }

    /* (non-Javadoc)
     * @see org.kie.api.management.KBaseConfigurationMonitorMBean#isShareBetaNodes()
     */
    public boolean isShareBetaNodes() {
        return ruleBaseConf.isShareBetaNodes();
    }

}
