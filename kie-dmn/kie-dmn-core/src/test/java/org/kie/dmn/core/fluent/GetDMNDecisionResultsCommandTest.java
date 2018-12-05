package org.kie.dmn.core.fluent;

import java.util.List;

import org.drools.core.command.impl.ContextImpl;
import org.junit.Test;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.core.impl.DMNContextImpl;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.internal.command.RegistryContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class GetDMNDecisionResultsCommandTest {

    @Test
    public void execute() {
        RegistryContext registryContext = new ContextImpl();
        GetDMNDecisionResultsCommand getDMNDecisionResultsCommand = new GetDMNDecisionResultsCommand();

        try {
            getDMNDecisionResultsCommand.execute(registryContext);
            fail();
        } catch (IllegalStateException ignored) {

        }
        DMNResultImpl dmnResult = new DMNResultImpl(null);
        dmnResult.setContext(new DMNContextImpl());

        registryContext.register(DMNResult.class, dmnResult);

        List<DMNDecisionResult> result = getDMNDecisionResultsCommand.execute(registryContext);
        assertEquals(0, result.size());
    }
}