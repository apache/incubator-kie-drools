/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.core.InitialFact;
import org.drools.core.base.ClassObjectType;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.reteoo.AccumulateNode;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.ExistsNode;
import org.drools.core.reteoo.FromNode;
import org.drools.core.reteoo.NotNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.QueryElementNode;
import org.drools.core.reteoo.RightInputAdapterNode;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieSessionTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.SerializationHelper;
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
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.LiveQuery;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.api.runtime.rule.Row;
import org.kie.api.runtime.rule.Variable;
import org.kie.api.runtime.rule.ViewChangedEventListener;
import org.kie.internal.builder.conf.PropertySpecificOption;

import static org.drools.compiler.integrationtests.BackwardChainingTest.assertContains;
import static org.drools.compiler.integrationtests.BackwardChainingTest.getFactHandle;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class BackwardChainingUnsupportedModelTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public BackwardChainingUnsupportedModelTest( final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(false);
    }

    @Test
    public void testQueryWithEvalAndTypeBoxingUnboxing() {
        final String drl = "package org.drools.test;\n" +
                "\n" +
                "global java.util.List list \n;" +
                "\n" +
                "query primitiveInt( int $a )\n" +
                " Integer( intValue == $a )\n" +
                " eval( $a == 178 )\n" +
                "end\n" +
                "\n" +
                "query boxedInteger( Integer $a )\n" +
                " Integer( this == $a )\n" +
                " eval( $a == 178 )\n" +
                "end\n" +
                "\n" +
                "query boxInteger( int $a )\n" +
                " Integer( this == $a )\n" +
                " eval( $a == 178 )\n" +
                "end\n" +
                "\n" +
                "query unboxInteger( Integer $a )\n" +
                " Integer( intValue == $a )\n" +
                " eval( $a == 178 )\n" +
                "end\n" +
                "\n" +
                "query cast( int $a )\n" +
                " Integer( longValue == $a )\n" +
                " eval( $a == 178 )\n" +
                "end\n" +
                "" +
                "query cast2( long $a )\n" +
                " Integer( intValue == $a )\n" +
                " eval( $a == 178 )\n" +
                "end\n" +
                "\n" +
                "rule Init when then insert( 178 ); end\n" +
                "\n" +
                "rule Check\n" +
                "when\n" +
                " String()\n" +
                " ?primitiveInt( 178 ; )\n" +
                " ?boxedInteger( $x ; )\n" +
                " ?boxInteger( $x ; )\n" +
                " ?unboxInteger( $y ; )\n" +
                " ?cast( $z ; )\n" +
                " ?cast2( $z ; )\n" +
                "then\n" +
                " list.add( $x ); \n" +
                " list.add( $y ); \n" +
                " list.add( $z ); \n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("backward-chaining-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final ArrayList list = new ArrayList();
            ksession.setGlobal("list", list);

            ksession.fireAllRules();
            assertTrue(list.isEmpty());

            ksession.insert("go");
            ksession.fireAllRules();

            assertEquals(Arrays.asList(178, 178, 178), list);
        } finally {
            ksession.dispose();
        }
    }
    @Test
    public void testNaniSearchsNoPropReactivity() throws IOException, ClassNotFoundException {
        testNaniSearchs(PropertySpecificOption.ALLOWED);
    }

    @Test
    public void testNaniSearchsWithPropReactivity() throws IOException, ClassNotFoundException {
        // DROOLS-1453
        testNaniSearchs(PropertySpecificOption.ALWAYS);
    }

    private void testNaniSearchs(final PropertySpecificOption propertySpecificOption) throws IOException, ClassNotFoundException {
        // http://www.amzi.com/AdventureInProlog/advtop.php

        final String drl = "" +
                "package org.drools.compiler.test  \n" +

                "import java.util.List\n" +
                "import java.util.ArrayList\n" +

                "import java.util.Map\n" +
                "import java.util.HashMap\n" +

                "global List list\n" +

                "dialect \"mvel\"\n" +

                "declare Room" +
                "    name : String\n" +
                "end\n" +
                "\n" +
                "declare Location\n" +
                "    thing : String \n" +
                "    location : String \n" +
                "end" +
                "\n" +
                "declare Door\n" +
                "   fromLocation : String\n" +
                "   toLocation : String\n" +
                "end" +
                "\n" +
                "declare Edible\n" +
                "   thing : String\n" +
                "end" +
                "\n" +
                "declare TastesYucky\n" +
                "   thing : String\n" +
                "end\n" +
                "\n" +
                "declare Here\n" +
                "   place : String \n" +
                "end\n" +
                "\n" +

                "query whereFood( String x, String y ) \n" +
                "    ( Location(x, y;) and\n" +
                "      Edible(x;) ) " +
                "     or \n " +
                "    ( Location(z, y;) and ?whereFood(x, z;) )\n" +
                "end\n" +

                "query connect( String x, String y ) \n" +
                "    Door(x, y;)\n" +
                "    or \n" +
                "    Door(y, x;)\n" +
                "end\n" +
                "\n" +
                "query isContainedIn( String x, String y ) \n" +
                "    Location(x, y;)\n" +
                "    or \n" +
                "    ( Location(z, y;) and ?isContainedIn(x, z;) )\n" +
                "end\n" +
                "\n" +
                "query look(String place, List things, List food, List exits ) \n" +
                "    Here(place;)\n" +
                "    things := List() from accumulate( Location(thing, place;),\n" +
                "                                      collectList( thing ) )\n" +
                "    food := List() from accumulate( ?whereFood(thing, place;) ," +
                "                                    collectList( thing ) )\n" +
                "    exits := List() from accumulate( ?connect(place, exit;),\n" +
                "                                    collectList( exit ) )\n" +
                "end\n" +
                "\n" +
                "rule reactiveLook when\n" +
                "    Here( place : place) \n" +
                "    ?look(place, things, food, exits;)\n" +
                "then\n" +
                "    Map map = new HashMap();" +
                "    list.add(map);" +
                "    map.put( 'place', place); " +
                "    map.put( 'things', things); " +
                "    map.put( 'food', food); " +
                "    map.put( 'exits', exits); " +
                "    System.out.println( \"You are in the \" + place);\n" +
                "    System.out.println( \"  You can see \" + things );\n" +
                "    System.out.println( \"  You can eat \" + food );\n" +
                "    System.out.println( \"  You can go to \" + exits );\n" +
                "end\n" +
                "\n" +
                "rule init when\n" +
                "then\n" +
                "        insert( new Room(\"kitchen\") );\n" +
                "        insert( new Room(\"office\") );\n" +
                "        insert( new Room(\"hall\") );\n" +
                "        insert( new Room(\"dining room\") );\n" +
                "        insert( new Room(\"cellar\") );\n" +
                "        \n" +
                "        insert( new Location(\"apple\", \"kitchen\") );\n" +

                "        insert( new Location(\"desk\", \"office\") );\n" +
                "        insert( new Location(\"apple\", \"desk\") );\n" +
                "        insert( new Location(\"flashlight\", \"desk\") );\n" +
                "        insert( new Location(\"envelope\", \"desk\") );\n" +
                "        insert( new Location(\"key\", \"envelope\") );\n" +

                "        insert( new Location(\"washing machine\", \"cellar\") );\n" +
                "        insert( new Location(\"nani\", \"washing machine\") );\n" +
                "        insert( new Location(\"broccoli\", \"kitchen\") );\n" +
                "        insert( new Location(\"crackers\", \"kitchen\") );\n" +
                "        insert( new Location(\"computer\", \"office\") );\n" +
                "        \n" +
                "        insert( new Door(\"office\", \"hall\") );\n" +
                "        insert( new Door(\"kitchen\", \"office\") );\n" +
                "        insert( new Door(\"hall\", \"dining room\") );\n" +
                "        insert( new Door(\"kitchen\", \"cellar\") );\n" +
                "        insert( new Door(\"dining room\", \"kitchen\") );\n" +
                "        \n" +
                "        insert( new Edible(\"apple\") );\n" +
                "        insert( new Edible(\"crackers\") );\n" +
                "        \n" +
                "        insert( new TastesYucky(\"broccoli\") );  " +
                "end\n" +
                "" +
                "rule go1 when\n" +
                "   String( this == 'go1' )\n" +
                "then\n" +
                "   insert( new Here(\"kitchen\") );\n" +
                "end\n" +
                "\n" +
                "rule go2 when\n" +
                "   String( this == 'go2' )\n" +
                "   $h : Here( place == \"kitchen\")" +
                "then\n" +
                "   modify( $h ) { place = \"office\" };\n" +
                "end\n" +
                "";

        final ReleaseId releaseId1 = KieServices.get().newReleaseId("org.kie", "backward-chaining-test", "1");
        final Map<String, String> kieModuleConfigurationProperties = new HashMap<>();
        kieModuleConfigurationProperties.put(PropertySpecificOption.PROPERTY_NAME, propertySpecificOption.toString());

        final KieModule kieModule = KieUtil.getKieModuleFromDrls(releaseId1,
                kieBaseTestConfiguration,
                KieSessionTestConfiguration.STATEFUL_REALTIME,
                kieModuleConfigurationProperties,
                drl);
        final KieContainer kieContainer = KieServices.get().newKieContainer(kieModule.getReleaseId());
        final KieBase kbase = kieContainer.getKieBase();

        KieSession ksession = kbase.newKieSession();
        try {
            final List<Map<String, Object>> list = new ArrayList<>();
            ksession.setGlobal("list", list);

            ksession.fireAllRules();

            ksession.insert("go1");
            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
            ksession.fireAllRules();

            Map<String, Object> map = list.get(0);
            assertEquals("kitchen", map.get("place"));
            List<String> items = (List<String>) map.get("things");
            assertEquals(3, items.size());
            assertContains(new String[]{"apple", "broccoli", "crackers"}, items);

            items = (List<String>) map.get("food");
            assertEquals(2, items.size());
            assertContains(new String[]{"apple", "crackers"}, items);

            items = (List<String>) map.get("exits");
            assertEquals(3, items.size());
            assertContains(new String[]{"office", "cellar", "dining room"}, items);

            ksession.insert("go2");
            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true, false);
            ksession.fireAllRules();

            map = list.get(1);
            assertEquals("office", map.get("place"));
            items = (List<String>) map.get("things");
            assertEquals(2, items.size());
            assertContains(new String[]{"computer", "desk",}, items);

            items = (List<String>) map.get("food");
            assertEquals(1, items.size());
            assertContains(new String[]{"apple"}, items); // notice the apple is on the desk in the office

            items = (List<String>) map.get("exits");
            assertEquals(2, items.size());
            assertContains(new String[]{"hall", "kitchen"}, items);

            QueryResults results = ksession.getQueryResults("isContainedIn", "key", "office");
            assertEquals(1, results.size());
            final QueryResultsRow result = results.iterator().next();
            assertEquals("key", result.get("x"));
            assertEquals("office", result.get("y"));

            results = ksession.getQueryResults("isContainedIn", "key", Variable.v);
            List<List<String>> l = new ArrayList<>();
            for (final QueryResultsRow r : results) {
                l.add(Arrays.asList((String) r.get("x"), (String) r.get("y")));
            }
            assertEquals(3, results.size());
            assertContains(Arrays.asList("key", "desk"), l);
            assertContains(Arrays.asList("key", "office"), l);
            assertContains(Arrays.asList("key", "envelope"), l);

            results = ksession.getQueryResults("isContainedIn", Variable.v, "office");
            l = new ArrayList<>();
            for (final QueryResultsRow r : results) {
                l.add(Arrays.asList((String) r.get("x"), (String) r.get("y")));
            }

            assertEquals(6, results.size());
            assertContains(Arrays.asList("desk", "office"), l);
            assertContains(Arrays.asList("computer", "office"), l);
            assertContains(Arrays.asList("apple", "office"), l);
            assertContains(Arrays.asList("envelope", "office"), l);
            assertContains(Arrays.asList("flashlight", "office"), l);
            assertContains(Arrays.asList("key", "office"), l);

            results = ksession.getQueryResults("isContainedIn", Variable.v, Variable.v);
            l = new ArrayList<>();
            for (final QueryResultsRow r : results) {
                l.add(Arrays.asList((String) r.get("x"), (String) r.get("y")));
            }
            assertEquals(17, results.size());
            assertContains(Arrays.asList("apple", "kitchen"), l);
            assertContains(Arrays.asList("apple", "desk"), l);
            assertContains(Arrays.asList("envelope", "desk"), l);
            assertContains(Arrays.asList("desk", "office"), l);
            assertContains(Arrays.asList("computer", "office"), l);
            assertContains(Arrays.asList("washing machine", "cellar"), l);
            assertContains(Arrays.asList("key", "envelope"), l);
            assertContains(Arrays.asList("broccoli", "kitchen"), l);
            assertContains(Arrays.asList("nani", "washing machine"), l);
            assertContains(Arrays.asList("crackers", "kitchen"), l);
            assertContains(Arrays.asList("flashlight", "desk"), l);
            assertContains(Arrays.asList("nani", "cellar"), l);
            assertContains(Arrays.asList("apple", "office"), l);
            assertContains(Arrays.asList("envelope", "office"), l);
            assertContains(Arrays.asList("flashlight", "office"), l);
            assertContains(Arrays.asList("key", "office"), l);
            assertContains(Arrays.asList("key", "desk"), l);
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testSubNetworksAndQueries() {
        final String drl = "" +
                "package org.drools.compiler.test  \n" +

                "import java.util.List\n" +
                "import java.util.ArrayList\n" +

                "import java.util.Map\n" +
                "import java.util.HashMap\n" +

                "global List list\n" +

                "dialect \"mvel\"\n" +
                "\n" +
                "declare Location\n" +
                "    thing : String \n" +
                "    location : String \n" +
                "end" +
                "\n" +
                "declare Edible\n" +
                "   thing : String\n" +
                "end" +
                "\n" +
                "query whereFood( String x, String y ) \n" +
                "    Location(x, y;) Edible(x;) \n" +
                "end\n" +
                "\n" +
                "query look(String place, List food ) \n" +
                "    $s : String() // just here to give a OTN lookup point\n" +
                "    food := List() from accumulate( whereFood(thing, place;) ," +
                "                                    collectList( thing ) )\n" +
                "    exists( whereFood(thing, place;) )\n" +
                "    not( whereFood(thing, place;) and\n " +
                "         String( this == $s ) from thing )\n" +
                "end\n" +
                "\n" +
                "rule init when\n" +
                "then\n" +
                "        \n" +
                "        insert( new Location(\"apple\", \"kitchen\") );\n" +
                "        insert( new Location(\"crackers\", \"kitchen\") );\n" +
                "        insert( new Location(\"broccoli\", \"kitchen\") );\n" +
                "        insert( new Location(\"computer\", \"office\") );\n" +

                "        insert( new Edible(\"apple\") );\n" +
                "        insert( new Edible(\"crackers\") );\n" +
                "end\n" +
                "";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("backward-chaining-test", kieBaseTestConfiguration, drl);

        // Get the accumulate node, so we can test it's memory later
        // now check beta memory was correctly cleared
        final List<ObjectTypeNode> nodes = ((KnowledgeBaseImpl ) kbase).getRete().getObjectTypeNodes();
        ObjectTypeNode node = null;
        for (final ObjectTypeNode n : nodes) {
            if (((ClassObjectType ) n.getObjectType()).getClassType() == String.class) {
                node = n;
                break;
            }
        }

        assertNotNull(node);
        final BetaNode stringBetaNode = (BetaNode) node.getObjectSinkPropagator().getSinks()[0];
        final QueryElementNode queryElementNode1 = (QueryElementNode) stringBetaNode.getSinkPropagator().getSinks()[0];
        final RightInputAdapterNode riaNode1 = (RightInputAdapterNode) queryElementNode1.getSinkPropagator().getSinks()[0];
        final AccumulateNode accNode = (AccumulateNode) riaNode1.getObjectSinkPropagator().getSinks()[0];

        final QueryElementNode queryElementNode2 = (QueryElementNode) accNode.getSinkPropagator().getSinks()[0];
        final RightInputAdapterNode riaNode2 = (RightInputAdapterNode) queryElementNode2.getSinkPropagator().getSinks()[0];
        final ExistsNode existsNode = (ExistsNode) riaNode2.getObjectSinkPropagator().getSinks()[0];

        final QueryElementNode queryElementNode3 = (QueryElementNode) existsNode.getSinkPropagator().getSinks()[0];
        final FromNode fromNode = (FromNode) queryElementNode3.getSinkPropagator().getSinks()[0];
        final RightInputAdapterNode riaNode3 = (RightInputAdapterNode) fromNode.getSinkPropagator().getSinks()[0];
        final NotNode notNode = (NotNode) riaNode3.getObjectSinkPropagator().getSinks()[0];

        final KieSession ksession = kbase.newKieSession();
        try {
            final InternalWorkingMemory wm = ((StatefulKnowledgeSessionImpl ) ksession);
            final AccumulateNode.AccumulateMemory accMemory = (AccumulateNode.AccumulateMemory ) wm.getNodeMemory(accNode);
            final BetaMemory existsMemory = (BetaMemory) wm.getNodeMemory(existsNode);
            final FromNode.FromMemory fromMemory = (FromNode.FromMemory ) wm.getNodeMemory(fromNode);
            final BetaMemory notMemory = (BetaMemory) wm.getNodeMemory(notNode);

            final List<Map<String, Object>> list = new ArrayList<>();
            ksession.setGlobal("list", list);
            final FactHandle fh = ksession.insert("bread");

            ksession.fireAllRules();

            final List food = new ArrayList();

            // Execute normal query and check no subnetwork tuples are left behind
            QueryResults results = ksession.getQueryResults("look", "kitchen", Variable.v);
            assertEquals(1, results.size());

            for (final org.kie.api.runtime.rule.QueryResultsRow row : results) {
                food.addAll((Collection) row.get("food"));
            }
            assertEquals(2, food.size());
            assertContains(new String[]{"crackers", "apple"}, food);

            assertEquals(0, accMemory.getBetaMemory().getRightTupleMemory().size());
            assertEquals(0, existsMemory.getRightTupleMemory().size());
            assertEquals(0, fromMemory.getBetaMemory().getLeftTupleMemory().size());
            assertEquals(0, notMemory.getRightTupleMemory().size());

            // Now execute an open query and ensure the memory is left populated
            food.clear();
            final List foodUpdated = new ArrayList();
            final LiveQuery query = ksession.openLiveQuery("look",
                    new Object[]{"kitchen", Variable.v},
                    new ViewChangedEventListener() {

                        public void rowUpdated(final Row row) {
                            foodUpdated.addAll((Collection) row.get("food"));
                        }

                        public void rowDeleted(final Row row) {
                        }

                        public void rowInserted(final Row row) {
                            food.addAll((Collection) row.get("food"));
                        }
                    });
            assertEquals(2, food.size());
            assertContains(new String[]{"crackers", "apple"}, food);

            assertEquals(2, accMemory.getBetaMemory().getRightTupleMemory().size());
            assertEquals(2, existsMemory.getRightTupleMemory().size());
            assertEquals(2, fromMemory.getBetaMemory().getLeftTupleMemory().size());
            assertEquals(0, notMemory.getRightTupleMemory().size());

            food.clear();
            // Now try again, make sure it only delete's it's own tuples
            results = ksession.getQueryResults("look", "kitchen", Variable.v);
            assertEquals(1, results.size());

            for (final org.kie.api.runtime.rule.QueryResultsRow row : results) {
                food.addAll((Collection) row.get("food"));
            }
            assertEquals(2, food.size());
            assertContains(new String[]{"crackers", "apple"}, food);

            assertEquals(2, accMemory.getBetaMemory().getRightTupleMemory().size());
            assertEquals(2, existsMemory.getRightTupleMemory().size());
            assertEquals(2, fromMemory.getBetaMemory().getLeftTupleMemory().size());
            assertEquals(0, notMemory.getRightTupleMemory().size());
            food.clear();

            // do an update and check it's  still memory size 2
            // however this time the food should be empty, as 'crackers' now blocks the not.
            ksession.update(fh, "crackers");
            ksession.fireAllRules();

            assertEquals(2, accMemory.getBetaMemory().getRightTupleMemory().size());
            assertEquals(2, existsMemory.getRightTupleMemory().size());
            assertEquals(2, fromMemory.getBetaMemory().getLeftTupleMemory().size());
            assertEquals(1, notMemory.getRightTupleMemory().size());

            assertEquals(0, foodUpdated.size());

            // do an update and check it's  still memory size 2
            // this time
            ksession.update(fh, "oranges");
            ksession.fireAllRules();

            assertEquals(2, accMemory.getBetaMemory().getRightTupleMemory().size());
            assertEquals(2, existsMemory.getRightTupleMemory().size());
            assertEquals(2, fromMemory.getBetaMemory().getLeftTupleMemory().size());
            assertEquals(0, notMemory.getRightTupleMemory().size());

            assertEquals(2, food.size());
            assertContains(new String[]{"crackers", "apple"}, food);

            // Close the open
            query.close();
            assertEquals(0, accMemory.getBetaMemory().getRightTupleMemory().size());
            assertEquals(0, existsMemory.getRightTupleMemory().size());
            assertEquals(0, fromMemory.getBetaMemory().getLeftTupleMemory().size());
            assertEquals(0, notMemory.getRightTupleMemory().size());
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testInsertionOrderTwo() {
        final StringBuilder drlBuilder = new StringBuilder("" +
                "package org.drools.compiler.test \n" +
                "import java.util.List \n" +
                "global List list \n" +
                "declare Thing \n" +
                "    thing : String @key \n" +
                "end \n" +
                "declare Edible extends Thing \n" +
                "end \n" +
                "declare Location extends Thing \n" +
                "    location : String  @key \n" +
                "end \n" +
                "declare Here \n" +
                "    place : String \n" +
                "end \n" +
                "rule kickOff \n" +
                "when \n" +
                "    Integer( $i: intValue ) \n" +
                "then \n" +
                "    switch( $i ){ \n");

        String[] facts = new String[]{"new Edible( 'peach' )", "new Location( 'peach', 'table' )", "new Here( 'table' )"};
        int f = 0;
        for (final String fact : facts) {
            for (final String fact1 : facts) {
                for (final String fact2 : facts) {
                    // use a Set to make sure we only include 3 unique values
                    final Set<String> set = new HashSet<>();
                    set.add(fact);
                    set.add(fact1);
                    set.add(fact2);
                    if (set.size() == 3) {
                        drlBuilder.append("    case ").append(f++).append(": \n")
                                .append("        insert( ").append(fact).append(" ); \n")
                                .append("        insert( ").append(fact1).append(" ); \n")
                                .append("        insert( ").append(fact2).append(" ); \n")
                                .append("        break; \n");
                    }
                }
            }
        }

        facts = new String[]{"new Edible( 'peach' )", "new Location( 'table', 'office' )", "new Location( 'peach', 'table' )", "new Here( 'office' )"};
        int h = f;
        for (final String fact : facts) {
            for (final String fact1 : facts) {
                for (final String fact3 : facts) {
                    for (final String fact2 : facts) {
                        // use a Set to make sure we only include 3 unique values
                        final Set<String> set = new HashSet<>();
                        set.add(fact);
                        set.add(fact1);
                        set.add(fact3);
                        set.add(fact2);
                        if (set.size() == 4) {
                            drlBuilder.append("    case ").append(h++).append(": \n")
                                    .append("        insert( ").append(fact).append(" ); \n")
                                    .append("        insert( ").append(fact1).append(" ); \n")
                                    .append("        insert( ").append(fact3).append(" ); \n")
                                    .append("        insert( ").append(fact2).append(" ); \n")
                                    .append("        break; \n");
                        }
                    }
                }
            }
        }

        drlBuilder.append("    } \n" + "end \n" + "\n"
                + "query whereFood( String x, String y ) \n" + "    ( Location(x, y;) and \n" + "    Edible(x;) ) \n "
                + "    or  \n" + "    ( Location(z, y;) and whereFood(x, z;) ) \n"
                + "end "
                + "query look(String place, List things, List food)  \n" + "    Here(place;) \n"
                + "    things := List() from accumulate( Location(thing, place;), \n"
                + "                                      collectList( thing ) ) \n"
                + "    food := List() from accumulate( whereFood(thing, place;), \n"
                + "                                    collectList( thing ) ) \n"
                + "end \n" + "rule reactiveLook \n" + "when \n" + "    Here( $place : place)  \n"
                + "    look($place, $things; $food := food) \n"
                + "then \n"
                + "    list.addAll( $things ); \n"
                + "    list.addAll( $food   ); \n"
                + "end \n" + "");

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("backward-chaining-test", kieBaseTestConfiguration, drlBuilder.toString());

        for (int i = 0; i < f; i++) {
            final KieSession ksession = kbase.newKieSession();
            try {
                final List<String> list = new ArrayList<>();
                ksession.setGlobal("list", list);
                ksession.fireAllRules();
                list.clear();
                InternalFactHandle fh = (InternalFactHandle) ksession.insert(i);
                ksession.fireAllRules();

                assertEquals(2, list.size());
                assertEquals("peach", list.get(0));
                assertEquals("peach", list.get(1));
                list.clear();

                final InternalFactHandle[] handles = ksession.getFactHandles().toArray(new InternalFactHandle[0]);
                for (int j = 0; j < handles.length; j++) {
                    if (handles[j].getObject() instanceof InitialFact || handles[j].getObject() instanceof Integer) {
                        continue;
                    }
                    handles[j] = getFactHandle(handles[j], ksession);
                    final Object o = handles[j].getObject();

                    // first retract + assert
                    ksession.delete(handles[j]);

                    handles[j] = (InternalFactHandle) ksession.insert(o);

                    ksession.fireAllRules();
                    assertEquals(2, list.size());
                    assertEquals("peach", list.get(0));
                    assertEquals("peach", list.get(1));
                    list.clear();

                    // now try update
                    // session was serialised so need to get factHandle
                    handles[j] = getFactHandle(handles[j], ksession);
                    ksession.update(handles[j], handles[j].getObject());

                    ksession.fireAllRules();
                    assertEquals(2, list.size());
                    assertEquals("peach", list.get(0));
                    assertEquals("peach", list.get(1));
                    list.clear();
                }

                fh = getFactHandle(fh, ksession);
                ksession.delete(fh);
            } finally {
                ksession.dispose();
            }
        }

        for (int i = f; i < h; i++) {
            final KieSession ksession = kbase.newKieSession();
            try {
                final List<String> list = new ArrayList<>();
                ksession.setGlobal("list", list);
                ksession.fireAllRules();
                list.clear();

                InternalFactHandle fh = (InternalFactHandle) ksession.insert(i);
                ksession.fireAllRules();
                assertEquals(2, list.size());
                assertEquals("table", list.get(0));
                assertEquals("peach", list.get(1));
                list.clear();

                final InternalFactHandle[] handles = ksession.getFactHandles().toArray(new InternalFactHandle[0]);
                for (int j = 0; j < handles.length; j++) {
                    if (handles[j].getObject() instanceof InitialFact || handles[j].getObject() instanceof Integer) {
                        continue;
                    }

                    handles[j] = getFactHandle(handles[j], ksession);
                    final Object o = handles[j].getObject();

                    ksession.delete(handles[j]);
                    handles[j] = (InternalFactHandle) ksession.insert(o);

                    ksession.fireAllRules();
                    assertEquals(2, list.size());
                    assertEquals("table", list.get(0));
                    assertEquals("peach", list.get(1));
                    list.clear();

                    // now try update
                    handles[j] = getFactHandle(handles[j], ksession);
                    ksession.update(handles[j], handles[j].getObject());

                    ksession.fireAllRules();
                    assertEquals(2, list.size());
                    assertEquals("table", list.get(0));
                    assertEquals("peach", list.get(1));
                    list.clear();
                }

                fh = getFactHandle(fh, ksession);
                ksession.delete(fh);
            } finally {
                ksession.dispose();
            }
        }
    }
}
