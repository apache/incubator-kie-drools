package org.drools.modelcompiler.assembler;

import java.util.List;

import org.drools.modelcompiler.ExecutableModelProject;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.io.ResourceFactory;

import static org.drools.modelcompiler.KJARUtils.getPom;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AssemblerTest {

    @Test
    public void checkAssemblerRunsBeforeRules() {
        // we pick an arbitrary resource that does not have a local implementation
        // file path is also non-existent and invalid because it is not really used in the TestAssembler
        Resource fake = ResourceFactory.newFileResource("FAKE.dmn");
        fake.setResourceType(ResourceType.DMN);

        // the test assembler runs _before_ rules execution and leaves a result in the list
        KieServices ks = KieServices.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "kjar-test-1.0", "1.0");
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write(fake);
        kfs.writePomXML(getPom(releaseId));
        KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll(ExecutableModelProject.class);

        List<Message> results = kieBuilder.getResults().getMessages();
        assertEquals(TestAssembler.BEFORE_RULES.getMessage(), results.get(0).getText());
        assertEquals(TestAssembler.AFTER_RULES.getMessage(), results.get(1).getText());

        // create the container to build the generated Package
        KieContainer kieContainer = ks.newKieContainer(releaseId);
        try {
            // the generated package contains a broken Function.
            // Compilation will fail.
            // we use this to verify that it's been actually picked up
            KieBase kieBase = kieContainer.getKieBase();
            fail("The PackageDescr has not been picked up by drools");
        } catch (Throwable ex) {
            // all good
            assertTrue(ex.getCause() instanceof ClassNotFoundException);
        }
    }
}