package org.kie.dmn.core.fluent;

import org.drools.core.command.impl.ContextImpl;
import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.internal.command.RegistryContext;

import static org.junit.Assert.assertTrue;

public class SetDMNInputCommandTest {

    @Test
    public void execute() {
        RegistryContext registryContext = new ContextImpl();
        String testVariable = "testVariable";
        SetDMNInputCommand setDMNInputCommand = new SetDMNInputCommand(testVariable, 10);

        setDMNInputCommand.execute(registryContext);

        DMNContext lookup = registryContext.lookup(DMNContext.class);
        assertTrue(lookup.isDefined(testVariable));
    }
}