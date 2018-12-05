package org.kie.dmn.core.fluent;

import java.util.List;

import org.drools.core.command.impl.ContextImpl;
import org.junit.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.core.impl.DMNContextImpl;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.internal.command.RegistryContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class GetDMNMessagesCommandTest {

    @Test
    public void execute() {
        RegistryContext registryContext = new ContextImpl();
        GetDMNMessagesCommand getDMNMessagesCommand = new GetDMNMessagesCommand();

        try {
            getDMNMessagesCommand.execute(registryContext);
            fail();
        } catch (IllegalStateException ignored) {

        }
        DMNResultImpl dmnResult = new DMNResultImpl(null);
        dmnResult.setContext(new DMNContextImpl());

        registryContext.register(DMNResult.class, dmnResult);

        List<DMNMessage> result = getDMNMessagesCommand.execute(registryContext);
        assertEquals(0, result.size());
    }
}