/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.integrationtests.drl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.drools.core.base.MapGlobalResolver;
import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class GlobalTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public GlobalTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testReturnValueAndGlobal() {

        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "\n" +
                "global java.lang.String cheeseType;\n" +
                "global java.util.List   matchingList;\n" +
                "global java.util.List   nonMatchingList;\n" +
                "\n" +
                "\n" +
                "rule \"Match type\"\n" +
                "    when\n" +
                "        $cheese : Cheese( type == (cheeseType) )\n" +
                "    then\n" +
                "        matchingList.add( $cheese );\n" +
                "end\n" +
                "\n" +
                "rule \"Non matching type\"\n" +
                "    when\n" +
                "        $cheese : Cheese( type != (cheeseType) )\n" +
                "    then\n" +
                "        nonMatchingList.add( $cheese );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("global-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List matchlist = new ArrayList();
            ksession.setGlobal("matchingList",
                               matchlist);

            final List nonmatchlist = new ArrayList();
            ksession.setGlobal("nonMatchingList",
                               nonmatchlist);

            ksession.setGlobal("cheeseType",
                               "stilton");

            final Cheese stilton1 = new Cheese("stilton",
                                               5);
            final Cheese stilton2 = new Cheese("stilton",
                                               7);
            final Cheese brie = new Cheese("brie",
                                           4);
            ksession.insert(stilton1);
            ksession.insert(stilton2);
            ksession.insert(brie);

            ksession.fireAllRules();

            assertEquals(2,
                         matchlist.size());
            assertEquals(1,
                         nonmatchlist.size());
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testGlobalAccess() {

        final String drl = "import org.drools.core.base.MapGlobalResolver;\n" +
                "global java.lang.String myGlobal;\n" +
                "global String unused; \n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("global-test", kieBaseTestConfiguration, drl);

        final String sample = "default string";
        final KieSession session1 = kbase.newKieSession();
        try {
            // Testing 1.
            session1.setGlobal("myGlobal", "Testing 1");
            session1.insert(sample);
            session1.fireAllRules();
            final Map.Entry[] entries1 = ((MapGlobalResolver) session1.getGlobals()).getGlobals();
            assertEquals(1, entries1.length);
            assertEquals(entries1[0].getValue(), "Testing 1");
            assertEquals(1, session1.getGlobals().getGlobalKeys().size());
            assertTrue(session1.getGlobals().getGlobalKeys().contains("myGlobal"));
        } finally {
            session1.dispose();
        }

        // Testing 2.
        final StatelessKieSession session2 = kbase.newStatelessKieSession();
        session2.setGlobal("myGlobal", "Testing 2");
        session2.execute(sample);
        final Map.Entry[] entries2 = ((MapGlobalResolver) session2.getGlobals()).getGlobals();
        assertEquals(1, entries2.length);
        assertEquals(entries2[0].getValue(), "Testing 2");
        assertEquals(1, session2.getGlobals().getGlobalKeys().size());
        assertTrue(session2.getGlobals().getGlobalKeys().contains("myGlobal"));
    }

    @Test
    public void testEvalNullGlobal() {
        // RHBPMS-4649
        final String drl =
                "import " + Cheese.class.getCanonicalName() + "\n" +
                        "global Boolean b\n" +
                        "rule R when\n" +
                        "  eval(b)\n" +
                        "then\n" +
                        "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("global-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            ksession.setGlobal("b", null);
            assertEquals(0, ksession.fireAllRules());
        } finally {
            ksession.dispose();
        }
    }
}
