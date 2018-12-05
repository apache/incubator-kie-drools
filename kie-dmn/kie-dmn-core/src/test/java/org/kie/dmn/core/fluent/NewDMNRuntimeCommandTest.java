package org.kie.dmn.core.fluent;

import org.drools.core.command.impl.ContextImpl;
import org.junit.Test;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.internal.command.RegistryContext;
import org.kie.internal.utils.KieHelper;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class NewDMNRuntimeCommandTest {

    @Test
    public void execute() {
        RegistryContext registryContext = new ContextImpl();
        NewDMNRuntimeCommand newDMNRuntimeCommand = new NewDMNRuntimeCommand();

        try {
            newDMNRuntimeCommand.execute(registryContext);
            fail();
        } catch (IllegalStateException ignored) {

        }

        registryContext.register(KieContainer.class, new KieHelper().getKieContainer());

        newDMNRuntimeCommand.execute(registryContext);

        assertNotNull(registryContext.lookup(DMNRuntime.class));
    }
}