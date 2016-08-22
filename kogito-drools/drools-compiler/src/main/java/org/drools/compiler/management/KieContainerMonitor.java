package org.drools.compiler.management;

import org.drools.compiler.kie.builder.impl.InternalKieContainer;
import org.kie.api.builder.ReleaseId;
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
		return kieContainer.getConfiguredReleaseId().toString();
	}

	@Override
	public String getResolvedReleaseIdStr() {
		return kieContainer.getResolvedReleaseId().toString();
	}

    @Override
    public GAV getConfiguredReleaseId() {
        return GAV.from(kieContainer.getConfiguredReleaseId());
    }

    @Override
    public GAV getResolvedReleaseId() {
        return GAV.from(kieContainer.getResolvedReleaseId());
    }
}
