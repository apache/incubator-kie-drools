package org.kie.api.management;

public interface KieBaseConfigurationMonitorMBean {

    @Deprecated
    public int getAlphaNodeHashingThreshold();

    public String getAssertBehaviour();

    @Deprecated
    public int getCompositeKeyDepth();

    public String getEventProcessingMode();

    @Deprecated
    public int getMaxThreads();

    @Deprecated
    public String getSequentialAgenda();

    @Deprecated
    public boolean isIndexLeftBetaMemory();

    @Deprecated
    public boolean isIndexRightBetaMemory();

    @Deprecated
    public boolean isMaintainTms();

    public boolean isMBeansEnabled();

    public boolean isRemoveIdentities();

    @Deprecated
    public boolean isSequential();

    @Deprecated
    public boolean isShareAlphaNodes();

    @Deprecated
    public boolean isShareBetaNodes();

}
