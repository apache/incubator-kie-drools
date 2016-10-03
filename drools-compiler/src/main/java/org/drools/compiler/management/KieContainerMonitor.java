package org.drools.compiler.management;

import org.drools.compiler.kie.builder.impl.InternalKieContainer;
import org.kie.api.management.KieContainerMonitorMXBean;
import org.kie.api.management.GAV;

public class KieContainerMonitor implements KieContainerMonitorMXBean {
    private InternalKieContainer kieContainer;

	public KieContainerMonitor(InternalKieContainer kieContainer) {
		this.kieContainer = kieContainer;
	}

	@Override
	public String getContainerId() {
		return kieContainer.getContainerId();
	}

	@Override
	public String getConfiguredReleaseIdStr() {
		return ( kieContainer.getConfiguredReleaseId() != null )
		        ? kieContainer.getConfiguredReleaseId().toString()
		        : KieContainerMonitorMXBean.CLASSPATH_KIECONTAINER_RELEASEID.toString() ;
	}

	@Override
	public String getResolvedReleaseIdStr() {
		return ( kieContainer.getResolvedReleaseId() != null )
		        ? kieContainer.getResolvedReleaseId().toString()
		        : KieContainerMonitorMXBean.CLASSPATH_KIECONTAINER_RELEASEID.toString() ;
	}

    @Override
    public GAV getConfiguredReleaseId() {
        return ( kieContainer.getConfiguredReleaseId() != null )
                ? GAV.from(kieContainer.getConfiguredReleaseId())
                : KieContainerMonitorMXBean.CLASSPATH_KIECONTAINER_RELEASEID ;
    }

    @Override
    public GAV getResolvedReleaseId() {
        return ( kieContainer.getResolvedReleaseId() != null )
                ? GAV.from(kieContainer.getResolvedReleaseId())
                : KieContainerMonitorMXBean.CLASSPATH_KIECONTAINER_RELEASEID ;
    }
}
