package org.kie.dmn.core.fluent;

import java.util.UUID;

import org.drools.core.command.impl.ContextImpl;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.util.KieHelper;
import org.kie.internal.command.RegistryContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SetDMNActiveModelCommandTest {

    RegistryContext registryContext;
    DMNRuntime dmnRuntime;
    Resource resource;

    @Before
    public void init() {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "dmn-test-" + UUID.randomUUID(), "1.0");
        resource = ks.getResources().newClassPathResource("org/kie/dmn/core/typecheck_in_DT.dmn", this.getClass());
        KieContainer kieContainer = KieHelper.getKieContainer(releaseId, resource);

        registryContext = new ContextImpl();

        dmnRuntime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
    }

    @Test
    public void execute() {
        String namespace = "http://www.trisotech.com/definitions/_99ccd4df-41ac-43c3-a563-d58f43149829";
        String modelName = "typecheck in DT";
        SetDMNActiveModelCommand setDMNActiveModelCommand = new SetDMNActiveModelCommand(namespace, modelName);

        try {
            setDMNActiveModelCommand.execute(registryContext);
            fail();
        } catch (IllegalStateException ignored) {

        }
        registryContext.register(DMNRuntime.class, dmnRuntime);

        DMNModel dmnModel = setDMNActiveModelCommand.execute(registryContext);
        assertEquals(namespace, dmnModel.getNamespace());
        assertEquals(modelName, dmnModel.getName());
    }

    @Test
    public void executeWithResource() {
        SetDMNActiveModelCommand setDMNActiveModelCommand = new SetDMNActiveModelCommand(resource);

        try {
            setDMNActiveModelCommand.execute(registryContext);
            fail();
        } catch (IllegalStateException ignored) {

        }
        registryContext.register(DMNRuntime.class, dmnRuntime);

        DMNModel dmnModel = setDMNActiveModelCommand.execute(registryContext);
        assertEquals(resource.getSourcePath(), dmnModel.getResource().getSourcePath());
    }
}