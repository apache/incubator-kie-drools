package org.kie.api.management;

public interface KieContainerMonitorMXBean {

    String getContainerId();

    /**
     * The RelaseId configured while creating the Kiecontainer.
     * @return
     */
    GAV getConfiguredReleaseId();

    /**
     * The RelaseId configured while creating the Kiecontainer.
     * @return
     */
    String getConfiguredReleaseIdStr();

    /**
     * The actual resolved ReleaseId.
     * @return
     */
    GAV getResolvedReleaseId();

    /**
     * The actual resolved ReleaseId.
     * @return
     */
    String getResolvedReleaseIdStr();
}
