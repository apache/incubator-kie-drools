package org.drools.management;

public interface KBaseConfigurationMonitorMBean {

    public int getAlphaNodeHashingThreshold();

    public String getAssertBehaviour();

    public int getCompositeKeyDepth();

    public String getEventProcessingMode();

    public int getMaxThreads();

    public String getSequentialAgenda();

    public boolean isAdvancedProcessRuleIntegration();

    public boolean isIndexLeftBetaMemory();

    public boolean isIndexRightBetaMemory();

    public boolean isMaintainTms();

    public boolean isMBeansEnabled();

    public boolean isMultithreadEvaluation();

    public boolean isRemoveIdentities();

    public boolean isSequential();

    public boolean isShareAlphaNodes();

    public boolean isShareBetaNodes();

}