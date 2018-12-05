package org.kie.dmn.core.fluent;

import org.drools.core.command.impl.ContextImpl;
import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.impl.DMNRuntimeImpl;
import org.kie.internal.command.RegistryContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class ExecuteDMNCommandTest {

    @Test
    public void execute() {
        RegistryContext registryContext = new ContextImpl();
        ExecuteDMNCommand executeDMNCommand = new ExecuteDMNCommand();

        try {
            executeDMNCommand.execute(registryContext);
            fail();
        } catch (IllegalStateException ignored) {

        }

        registryContext.register(DMNModel.class, new DMNModelImpl(null));

        try {
            executeDMNCommand.execute(registryContext);
            fail();
        } catch (IllegalStateException ignored) {

        }

        DMNContext dmnContext = DMNFactory.newContext();
        dmnContext.set("example", 10);

        registryContext.register(DMNRuntime.class, new DMNRuntimeImpl(null));
        registryContext.register(DMNContext.class, dmnContext);

        DMNResult result = executeDMNCommand.execute(registryContext);
        assertNotNull(result);
        DMNContext newDmnContext = registryContext.lookup(DMNContext.class);
        assertEquals(1, dmnContext.getAll().size());
        assertEquals(0, newDmnContext.getAll().size());
    }
}