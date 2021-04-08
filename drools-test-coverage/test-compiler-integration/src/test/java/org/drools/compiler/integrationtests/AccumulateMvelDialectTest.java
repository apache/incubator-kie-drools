/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.ObjectSink;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.RightInputAdapterNode;
import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieSessionTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.conf.PropertySpecificOption;

import static java.util.Arrays.asList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Parameterized.class)
public class AccumulateMvelDialectTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public AccumulateMvelDialectTest( final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    // See https://issues.jboss.org/browse/DROOLS-2733
    @Test(timeout = 10000)
    public void testMVELAccumulate() {
        final KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources("accumulate-test", kieBaseTestConfiguration,
                                                                           "org/drools/compiler/integrationtests/test_AccumulateMVEL.drl");
        final KieSession wm = kbase.newKieSession();
        try {
            final List<?> results = new ArrayList<>();
            wm.setGlobal("results",
                         results);

            wm.insert(new Person("Bob",
                                 "stilton",
                                 20));
            wm.insert(new Person("Mark",
                                 "provolone"));
            wm.insert(new Cheese("stilton",
                                 10));
            wm.insert(new Cheese("brie",
                                 5));
            wm.insert(new Cheese("provolone",
                                 150));

            wm.fireAllRules();

            assertEquals(165, results.get(0));
            assertEquals(10, results.get(1));
            assertEquals(150, results.get(2));
            assertEquals(10, results.get(3));
            assertEquals(210, results.get(4));
        } finally {
            wm.dispose();
        }
    }

    // See https://issues.jboss.org/browse/DROOLS-2733
    @Test(timeout = 10000)
    public void testMVELAccumulate2WM() {

        final KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources("accumulate-test", kieBaseTestConfiguration,
                                                                           "org/drools/compiler/integrationtests/test_AccumulateMVEL.drl");
        final KieSession wm1 = kbase.newKieSession();
        try {
            final List<?> results1 = new ArrayList<>();

            wm1.setGlobal("results",
                          results1);

            final List<?> results2 = new ArrayList<>();
            final KieSession wm2 = kbase.newKieSession();
            try {
                wm2.setGlobal("results",
                              results2);

                wm1.insert(new Person("Bob",
                                      "stilton",
                                      20));
                wm1.insert(new Person("Mark",
                                      "provolone"));

                wm2.insert(new Person("Bob",
                                      "stilton",
                                      20));
                wm2.insert(new Person("Mark",
                                      "provolone"));

                wm1.insert(new Cheese("stilton",
                                      10));
                wm1.insert(new Cheese("brie",
                                      5));
                wm2.insert(new Cheese("stilton",
                                      10));
                wm1.insert(new Cheese("provolone",
                                      150));
                wm2.insert(new Cheese("brie",
                                      5));
                wm2.insert(new Cheese("provolone",
                                      150));
                wm1.fireAllRules();

                wm2.fireAllRules();
            } finally {
                wm2.dispose();
            }

            assertEquals(165, results1.get(0));
            assertEquals(10, results1.get(1));
            assertEquals(150, results1.get(2));
            assertEquals(10, results1.get(3));
            assertEquals(210, results1.get(4));

            assertEquals(165, results2.get(0));
            assertEquals(10, results2.get(1));
            assertEquals(150, results2.get(2));
            assertEquals(10, results2.get(3));
            assertEquals(210, results2.get(4));
        } finally {
            wm1.dispose();
        }
    }


    @Test
    public void testAccFunctionOpaqueJoins() {
        // DROOLS-661
        testAccFunctionOpaqueJoins(PropertySpecificOption.ALLOWED);
    }

    @Test
    public void testAccFunctionOpaqueJoinsWithPropertyReactivity() {
        // DROOLS-1445
        testAccFunctionOpaqueJoins(PropertySpecificOption.ALWAYS);
    }

    // This is unsupported as the declared type Data is loosely typed
    private void testAccFunctionOpaqueJoins(final PropertySpecificOption propertySpecificOption) {
        final String drl = "package org.test; " +
                "import java.util.*; " +
                "global List list; " +
                "global List list2; " +

                "declare Tick " +
                "  tick : int " +
                "end " +

                "declare Data " +
                "  values : List " +
                "  bias : int = 0 " +
                "end " +

                "rule Init " +
                "when " +
                "then " +
                "  insert( new Data( Arrays.asList( 1, 2, 3 ), 1 ) ); " +
                "  insert( new Data( Arrays.asList( 4, 5, 6 ), 2 ) ); " +
                "  insert( new Tick( 0 ) );" +
                "end " +

                "rule Update " +
                "  no-loop " +
                "when " +
                "  $i : Integer() " +
                "  $t : Tick() " +
                "then " +
                "  System.out.println( 'Set tick to ' + $i ); " +
                "  modify( $t ) { " +
                "      setTick( $i ); " +
                "  } " +
                "end " +

                "rule M " +
                "  dialect 'mvel' " +
                "when " +
                "    Tick( $index : tick ) " +
                "    accumulate ( $data : Data( $bias : bias )," +
                "                 $tot : sum( ((Integer) $data.values[ $index ]) + $bias ) ) " +
                "then " +
                "    System.out.println( $tot + ' for J ' + $index ); " +
                "    list.add( $tot.intValue() ); " +
                "end " +

                "rule J " +
                "when " +
                "    Tick( $index : tick ) " +
                "    accumulate ( $data : Data( $bias : bias )," +
                "                 $tot : sum( ((Integer)$data.getValues().get( $index )) + $bias ) ) " +
                "then " +
                "    System.out.println( $tot + ' for M ' + $index ); " +
                "    list2.add( $tot.intValue() ); " +
                "end ";

        final ReleaseId releaseId1 = KieServices.get().newReleaseId("org.kie", "accumulate-test", "1");
        final Map<String, String> kieModuleConfigurationProperties = new HashMap<>();
        kieModuleConfigurationProperties.put(PropertySpecificOption.PROPERTY_NAME, propertySpecificOption.toString());

        final KieModule kieModule = KieUtil.getKieModuleFromDrls(releaseId1,
                                                                 kieBaseTestConfiguration,
                                                                 KieSessionTestConfiguration.STATEFUL_REALTIME,
                                                                 kieModuleConfigurationProperties,
                                                                 drl);
        final KieContainer kieContainer = KieServices.get().newKieContainer(kieModule.getReleaseId());
        final KieBase kbase = kieContainer.getKieBase();
        final KieSession ks = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ks.setGlobal("list", list);
            final List list2 = new ArrayList();
            ks.setGlobal("list2", list2);

            // init data
            ks.fireAllRules();
            assertEquals(Collections.singletonList(8), list);
            assertEquals(Collections.singletonList(8), list2);

            ks.insert(1);
            ks.fireAllRules();
            assertEquals(asList(8, 10), list);
            assertEquals(asList(8, 10), list2);

            ks.insert(2);
            ks.fireAllRules();
            assertEquals(asList(8, 10, 12), list);
            assertEquals(asList(8, 10, 12), list2);
        } finally {
            ks.dispose();
        }
    }

    @Test
    public void testAccumulateWithSameSubnetwork() {
        final String drl = "package org.drools.compiler.test;\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.List list; \n" +
                "rule r1 salience 100 \n" +
                "    when\n" +
                "        $person      : Person( name == 'Alice', $likes : likes )\n" +
                "        $total       : Number() from accumulate( $p : Person(likes != $likes, $l : likes) and $c : Cheese( type == $l ),\n" +
                "                                                min($c.getPrice()) )\n" +
                "    then\n" +
                "        list.add( 'r1' + ':' + $total);\n" +
                "end\n" +
                "rule r2 \n" +
                "    when\n" +
                "        $person      : Person( name == 'Alice', $likes : likes )\n" +
                "        $total       : Number() from accumulate( $p : Person(likes != $likes, $l : likes) and $c : Cheese( type == $l ),\n" +
                "                                                max($c.getPrice()) )\n" +
                "    then\n" +
                "        list.add( 'r2' + ':' + $total);\n" +
                "end\n" +

                "";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration, drl);
        final KieSession wm = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            wm.setGlobal("list", list);

            // Check the network formation, to ensure the RiaNode is shared.
            final ObjectTypeNode cheeseOtn = KieUtil.getObjectTypeNode(kbase, Cheese.class);
            assertNotNull(cheeseOtn);
            final ObjectSink[] oSinks = cheeseOtn.getObjectSinkPropagator().getSinks();
            assertEquals(1, oSinks.length);

            final JoinNode cheeseJoin = (JoinNode) oSinks[0];
            final LeftTupleSink[] ltSinks = cheeseJoin.getSinkPropagator().getSinks();

            assertEquals(1, ltSinks.length);
            final RightInputAdapterNode rian = (RightInputAdapterNode) ltSinks[0];
            assertEquals(2, rian.getObjectSinkPropagator().size());   //  RiaNode is shared, if this has two outputs

            wm.insert(new Cheese("stilton", 10));
            wm.insert(new Person("Alice", "brie"));
            wm.insert(new Person("Bob", "stilton"));

            wm.fireAllRules();

            assertEquals(2, list.size());
            assertEquals("r1:10", list.get(0));
            assertEquals("r2:10", list.get(1));
        } finally {
            wm.dispose();
        }
    }
}
