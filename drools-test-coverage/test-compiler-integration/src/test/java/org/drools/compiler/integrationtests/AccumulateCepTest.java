/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.integrationtests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.drools.compiler.integrationtests.incrementalcompilation.TestUtil;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.time.SessionPseudoClock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(Parameterized.class)
public class AccumulateCepTest {

    public static final String TEST_MANY_SLIDING_WINDOWS_DRL = "package com.sample;\n" +
                "\n" +
                "global java.util.List list; \n" +
                "" +
                "declare Fakt\n" +
                "  @role( event ) \n" +
                "  id : int \n" +
                "end\n" +
                " \n" +
                "rule Init \n" +
                "when \n" +
                "  $i : Integer() \n" +
                "then \n" +
                "  insert( new Fakt( $i ) ); \n" +
                "end\n" +
                "" +
                "rule \"Test\"\n" +
                "when\n" +
                "   accumulate ( $d : Fakt( id > 10 ) over window:length(2), $tot1 : count( $d ) ) \n" +
                "   accumulate ( $d : Fakt( id < 50 ) over window:length(5), $tot2 : count( $d ) ) \n" +
                "then\n" +
                "  list.clear();\n " +
                "  list.add( $tot1.intValue() ); \n" +
                "  list.add( $tot2.intValue() ); \n" +
                "end\n" +
                "\n";

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public AccumulateCepTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseStreamConfigurations(true);
    }

    @Test
    public void testAccumulatesExpireVsCancel() throws Exception {
        // JBRULES-3201
        final String drl = "package com.sample;\n" +
                "\n" +
                "global java.util.List list; \n" +
                "" +
                "declare FactTest\n" +
                " @role( event ) \n" +
                "end\n" +
                " \n" +
                "rule \"A500 test\"\n" +
                "when\n" +
                " accumulate (\n" +
                " $d : FactTest() over window:time(1m), $tot : count($d); $tot > 0 )\n" +
                "then\n" +
                " System.out.println( $tot ); \n" +
                " list.add( $tot.intValue() ); \n " +
                "end\n" +
                "\n";

        final InternalKnowledgeBase kbase = (InternalKnowledgeBase) KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration);
        kbase.addPackages(TestUtil.createKnowledgeBuilder(null, drl).getKnowledgePackages());

        final KieSessionConfiguration ksConf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksConf.setOption(ClockTypeOption.PSEUDO);
        final KieSession ksession = kbase.newKieSession(ksConf, null);
        try {
            final ArrayList list = new ArrayList();
            ksession.setGlobal("list", list);

            final FactType ft = kbase.getFactType("com.sample", "FactTest");

            ksession.insert(ft.newInstance());
            ksession.fireAllRules();
            ksession.insert(ft.newInstance());
            ksession.fireAllRules();
            ksession.insert(ft.newInstance());
            ksession.fireAllRules();

            final SessionPseudoClock clock = ksession.getSessionClock();
            clock.advanceTime(1, TimeUnit.MINUTES);

            ksession.fireAllRules();

            assertFalse(list.contains(0));
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testManySlidingWindows() {

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                         TEST_MANY_SLIDING_WINDOWS_DRL);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal("list", list);

            // Intentionally new Integer() here, we need different instances, but some of them equal
            // - if using direct value or Integer.valueOf, we get JVM cached instances, so we will get the same one for
            // the same number
            ksession.insert( new Integer( 20 ) );
            ksession.fireAllRules();
            assertEquals(asList(1, 1), list);

            ksession.insert(new Integer(20));
            ksession.fireAllRules();

            assertEquals(asList(2, 2), list);

            ksession.insert(new Integer(20));
            ksession.fireAllRules();
            assertEquals(asList(2, 3), list);

            ksession.insert(new Integer(2));
            ksession.fireAllRules();
            assertEquals(asList(2, 4), list);

            ksession.insert(new Integer(2));
            ksession.fireAllRules();
            assertEquals(asList(2, 5), list);

            ksession.insert(new Integer(2));
            ksession.fireAllRules();
            assertEquals(asList(2, 5), list);
        } finally {
            ksession.dispose();
        }
    }
}
