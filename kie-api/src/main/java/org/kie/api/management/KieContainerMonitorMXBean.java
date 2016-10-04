package org.kie.api.management;

public interface KieContainerMonitorMXBean {
    public static final GAV CLASSPATH_KIECONTAINER_RELEASEID = new GAV("classpath", "classpath", "0.0.0");

    String getContainerId();

    /**
     * The RelaseId configured while creating the KieContainer.
     * <p />
     * If the KieContainer has been created from Classpath instead, the hardcoded value {@link #CLASSPATH_KIECONTAINER_RELEASEID} will be returned. 
     * @return
     */
    GAV getConfiguredReleaseId();

    /**
     * The RelaseId configured while creating the KieContainer.
     * <p />
     * If the KieContainer has been created from Classpath instead, the hardcoded value {@link #CLASSPATH_KIECONTAINER_RELEASEID} will be returned. 
     * @return
     */
    String getConfiguredReleaseIdStr();

    /**
     * The actual resolved ReleaseId.
     * <p />
     * If the KieContainer has been created from Classpath instead, the hardcoded value {@link #CLASSPATH_KIECONTAINER_RELEASEID} will be returned. 
     * @return
     */
    GAV getResolvedReleaseId();

    /**
     * The actual resolved ReleaseId.
     * <p />
     * If the KieContainer has been created from Classpath instead, the hardcoded value {@link #CLASSPATH_KIECONTAINER_RELEASEID} will be returned. 
     * @return
     */
    String getResolvedReleaseIdStr();
}
