package org.drools.testcoverage.functional.model;

import java.io.IOException;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;

public class RulesWithInTest {

    @Test
    public void testRecreateKieBaseNewContainer() throws IOException {
        final KieServices kieServices = KieServices.get();
        final ReleaseId releaseId = createKJar(kieServices);

        kieServices.newKieContainer(releaseId).newKieBase(kieServices.newKieBaseConfiguration());
        kieServices.newKieContainer(releaseId).newKieBase(kieServices.newKieBaseConfiguration());
    }

    @Test
    public void testRecreateKieBaseReuseContainer() throws IOException {
        final KieServices kieServices = KieServices.get();
        final ReleaseId releaseId = createKJar(kieServices);

        final KieContainer kieContainer = kieServices.newKieContainer(releaseId);
        kieContainer.newKieBase(kieServices.newKieBaseConfiguration());
        kieContainer.newKieBase(kieServices.newKieBaseConfiguration());
    }

    private ReleaseId createKJar(final KieServices kieServices) throws IOException {
        final String drl = "package org.drools.testcoverage.functional; \n"
                + "rule \"testRule\" \n"
                + "when \n"
                + "    String(this == \"test\") \n"
                + "then \n"
                + "end\n";
        final Resource drlResource = kieServices.getResources().newByteArrayResource(drl.getBytes());
        drlResource.setResourceType(ResourceType.DRL);
        drlResource.setTargetPath("org/drools/testcoverage/functional/model/testFile.drl");

        return BuildtimeUtil.createKJarFromResources(true, drlResource);
    }
}
