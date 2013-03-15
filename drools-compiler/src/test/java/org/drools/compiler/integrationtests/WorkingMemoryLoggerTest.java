package org.drools.compiler.integrationtests;

import java.io.InputStreamReader;
import java.io.Reader;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.core.audit.WorkingMemoryFileLogger;
import org.drools.compiler.compiler.PackageBuilder;
import org.junit.Ignore;
import org.junit.Test;

public class WorkingMemoryLoggerTest {

    private static final Reader DRL = new InputStreamReader(
            WorkingMemoryLoggerTest.class.getResourceAsStream("empty.drl"));

    private static final String LOG = "session";
    @Test
    @Ignore
    public void testOutOfMemory() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl(DRL);
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage(builder.getPackage());
        for (int i = 0; i < 10000; i++) {
            //System.out.println(i);
            StatefulSession session = ruleBase.newStatefulSession();
            WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger(session);
            session.fireAllRules();
            session.dispose();
        }
    }

}
