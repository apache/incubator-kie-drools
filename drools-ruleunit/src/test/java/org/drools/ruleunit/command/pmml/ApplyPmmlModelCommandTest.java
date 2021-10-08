package org.drools.ruleunit.command.pmml;

import org.drools.core.command.impl.ContextImpl;
import org.drools.core.command.runtime.pmml.ApplyPmmlModelCommand;
import org.junit.Test;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.runtime.Context;
import org.kie.internal.command.RegistryContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ApplyPmmlModelCommandTest {

    @Test(expected = IllegalStateException.class)
    public void testMissingRequestDataWithTrusty() {
        ApplyPmmlModelCommand cmd = new ApplyPmmlModelCommandTester();
        RegistryContext ctx = new ContextImpl();
        cmd.execute(ctx);
    }

    @Test
    public void testHasRequestDataAndSourceWithNew() {
        ApplyPmmlModelCommand cmd = new ApplyPmmlModelCommandTester();
        PMMLRequestData data = new PMMLRequestData("123", "Sample Score");
        data.addRequestParam("age", 33.0);
        data.addRequestParam("occupation", "SKYDIVER");
        data.addRequestParam("residenceState", "KN");
        data.addRequestParam("validLicense", true);
        data.setSource("SOURCE");
        cmd.setRequestData(data);
        cmd.setPackageName("org.drools.scorecards.example");
        RegistryContext ctx = new ContextImpl();
        PMML4Result resultHolder = cmd.execute(ctx);

        assertNotNull(resultHolder);
        String invoked = (String) resultHolder.getResultVariables().get("TYPE");
        assertEquals("TRUSTY", invoked);
    }

    @Test
    public void testIsMining() {
        ApplyPmmlModelCommand cmd = new ApplyPmmlModelCommand();
        assertNull(cmd.getHasMining());
        assertFalse(cmd.isMining());
        cmd.setHasMining(false);
        assertFalse(cmd.isMining());
        cmd.setHasMining(true);
        assertTrue(cmd.isMining());
    }

    private class ApplyPmmlModelCommandTester extends ApplyPmmlModelCommand {

        public ApplyPmmlModelCommandTester() {
            super();
        }

        @Override
        public PMML4Result execute(Context context) {
            PMML4Result toReturn = super.execute(context);
            toReturn.addResultVariable("TYPE", "TRUSTY");
            return toReturn;
        }
    }
}
