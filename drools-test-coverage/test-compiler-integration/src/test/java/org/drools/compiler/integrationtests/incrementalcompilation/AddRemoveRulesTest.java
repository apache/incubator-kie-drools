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

package org.drools.compiler.integrationtests.incrementalcompilation;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.assertj.core.api.Assertions;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.SubnetworkTuple;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import static org.drools.core.util.DroolsTestUtil.rulestoMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class AddRemoveRulesTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public AddRemoveRulesTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    private InternalKnowledgeBase base = KnowledgeBaseFactory.newKnowledgeBase();

    private String getPrefix() {
        return "package " + TestUtil.RULES_PACKAGE_NAME + " \n"+
                "import java.util.Map;\n"+
                "import java.util.HashMap;\n"+
                "import org.slf4j.Logger;\n"+
                "import java.util.Date;\n"+

                "declare Counter \n"+
                "@role(event)\n"+
                " id : int \n"+
                "\n"+
                "end\n\n";
    }

    @Before
    public void createEmptyKnowledgeBase() {
        final KieServices kieServices = KieServices.get();
        final ReleaseId releaseId = kieServices.newReleaseId("org.kie", "test-add-remove-rules", "1.0");
        KieUtil.getKieModuleFromDrls(releaseId, kieBaseTestConfiguration);
        final KieContainer kieContainer = kieServices.newKieContainer(releaseId);
        base = (InternalKnowledgeBase) kieContainer.getKieBase();
    }

    private void loadRule(final String rule)  {
        String prefix = getPrefix();
        prefix += rule;

        final KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        builder.add( ResourceFactory.newReaderResource( new StringReader( prefix ) ), ResourceType.DRL);
        final Collection<KiePackage> pkgs = this.buildKnowledge(builder);
        this.addKnowledgeToBase(pkgs);
    }

    private void addRuleToEngine(final String rule)  {
        this.loadRule(rule);
    }

    private void deleteRule(final String name) {
        this.base.removeRule(TestUtil.RULES_PACKAGE_NAME, name);
    }

    private Collection<KiePackage> buildKnowledge(final KnowledgeBuilder builder)  {
        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }
        return builder.getKnowledgePackages();
    }

    private void addKnowledgeToBase(final Collection<KiePackage> pkgs) {
        this.base.addPackages( pkgs );
    }

    @Test
    public void test() {
        final KieSession knowledgeSession = base.newKieSession();
        knowledgeSession.fireAllRules();

        final String ruleNormal1 = "rule 'rn1' " +
                "when " +
                "$c : Counter(id==1)" +
                "then " +
                "System.out.println('RN1 fired!!!'); \n" +
                " end ";
        addRuleToEngine(ruleNormal1);

        final String ruleNormal2 = "rule 'rn2' " +
                "when " +
                "$c : Counter(id==1)" +
                "then " +
                "System.out.println('RN2 fired!!!'); \n" +
                " end ";
        addRuleToEngine(ruleNormal2);

        final String ruleNormal3 = "rule 'rn3' " +
                "when " +
                "$c : Counter(id==1)" +
                "then " +
                "System.out.println('RN3 + fired!!!'); \n" +
                " end ";
        addRuleToEngine(ruleNormal3);

        final String rule = "rule 'test' " +
                "when " +
                "$c : Counter(id==1)" +
                "eval(Integer.parseInt(\"5\")==$c.getId()) \n" +
                "eval(Integer.parseInt(\"10\")>5) " +
                "then " +
                "System.out.println('TEST 1 fired!!!');" +
                "end ";
        addRuleToEngine(rule);

        final String rule2 = "rule 'test2' " +
                "when " +
                "$c : Counter(id==2)" +
                "eval(Integer.parseInt(\"10\")==$c.getId()) \n" +
                "eval(Integer.parseInt(\"20\")>10) " +
                "then " +
                "System.out.println('TEST 2 fired!!!'); \n" +
                " end ";
        addRuleToEngine(rule2);

        final String rule3 = "rule 'test3' " +
                "when " +
                "$c : Counter(id==3)" +
                "eval(Integer.parseInt(\"15\")==$c.getId()) \n" +
                "eval(Integer.parseInt(\"30\")>20) " +
                "then " +
                "System.out.println('TEST 2 fired!!!'); \n" +
                " end ";
        addRuleToEngine(rule3);

        final String rule4 = "rule 'test4' " +
                "when " +
                "$c : Counter(id==4)" +
                "eval(Integer.parseInt(\"20\")==$c.getId()) \n" +
                "eval(Integer.parseInt(\"40\")>30) " +
                "then " +
                "System.out.println('TEST 2 fired!!!'); \n" +
                " end ";
        addRuleToEngine(rule4);

        final String rule5 = "rule 'test5' " +
                "when " +
                "$c : Counter(id==5)" +
                "eval(Integer.parseInt(\"25\")==$c.getId()) \n" +
                "eval(Integer.parseInt(\"50\")>40) " +
                "then " +
                "System.out.println('TEST 2 fired!!!'); \n" +
                " end ";
        addRuleToEngine(rule5);

        final String rule6 = "rule 'test6' " +
                "when " +
                "$c : Counter(id==6)" +
                "eval(Integer.parseInt(\"30\")==$c.getId()) \n" +
                "eval(Integer.parseInt(\"60\")>50) " +
                "then " +
                "System.out.println('TEST 2 fired!!!'); \n" +
                " end ";
        addRuleToEngine(rule6);

        assertEquals(9, TestUtil.getRulesCount(base));

        deleteRule("test6");
        assertEquals(8, TestUtil.getRulesCount(base));

        addRuleToEngine(rule6);
        assertEquals(9, TestUtil.getRulesCount(base));

        deleteRule("test6");
        assertEquals(8, TestUtil.getRulesCount(base));
    }

    @Test
    public void testAddRemoveFromKB() {
        // DROOLS-328
        final String drl = "\n" +
                "rule A\n" +
                "  when\n" +
                "    Double() from entry-point \"AAA\"\n" +
                "  then\n" +
                "  end\n" +
                "\n" +
                "rule B\n" +
                "  when\n" +
                "    Boolean()\n" +
                "    Float()\n" +
                "  then\n" +
                "  end\n" +
                "\n" +
                "\n" +
                "rule C\n" +
                "  when\n" +
                "  then\n" +
                "    insertLogical( new Float( 0.0f ) );\n" +
                "  end\n" +
                "\n" +
                "\n" +
                "rule D\n" +
                "  when\n" +
                "    Byte( )\n" +
                "    String( )\n" +
                "  then\n" +
                "  end\n" +
                "\n" +
                "\n" +
                "rule E\n" +
                "  when\n" +
                "    Float()\n" +
                "  then\n" +
                "    insertLogical( \"foo\" );\n" +
                "  end\n" +
                "";


        final KnowledgeBuilder kbuilder = TestUtil.createKnowledgeBuilder(null, drl);
        base.addPackages(kbuilder.getKnowledgePackages());

        // Create kSession and initialize it
        final KieSession kSession = base.newKieSession();
        kSession.fireAllRules();

        ((InternalKnowledgeBase) kSession.getKieBase()).addPackages( kbuilder.getKnowledgePackages() );
    }

    @Test
    public void testAddRemoveDeletingFact() {
        // DROOLS-328
        final String drl = "\n" +
                "rule B\n" +
                "  when\n" +
                "    Boolean()\n" +
                "    Float()\n" +
                "  then\n" +
                "  end\n" +
                "\n" +
                "";

        final KnowledgeBuilder kbuilder = TestUtil.createKnowledgeBuilder(null, drl);
        base.addPackages( kbuilder.getKnowledgePackages() );

        // Create kSession and initialize it
        final KieSession kSession = base.newKieSession();
        final FactHandle fh = kSession.insert(0.0f);
        kSession.fireAllRules();

        ((InternalKnowledgeBase)kSession.getKieBase()).addPackages( kbuilder.getKnowledgePackages() );
        kSession.delete(fh);
    }

    @Test
    public void testAddRemoveWithPartialSharing() {
        final String drl = "package org.drools.test; \n" +
                "\n" +
                "declare A end \n" +
                "declare B end \n" +
                "declare C end \n" +
                "declare D end \n" +
                "" +
                "rule Init \n" +
                "  when\n" +
                "  then\n" +
                "    insert( new A() ); \n" +
                "    insert( new B() ); \n" +
                "  end\n" +
                "" +
                "rule One\n" +
                "  when\n" +
                "    A()\n" +
                "    B()\n" +
                "    C()\n" +
                "  then\n" +
                "  end\n" +
                "\n" +
                "rule Two\n" +
                "  when\n" +
                "    A()\n" +
                "    B()\n" +
                "    D()\n" +
                "  then\n" +
                "  end\n" +
                "\n" +
                "";

        final KnowledgeBuilder kbuilder = TestUtil.createKnowledgeBuilder(null, drl);
        base.addPackages( kbuilder.getKnowledgePackages() );

        // Create kSession and initialize it
        final KieSession kSession = base.newKieSession();
        kSession.fireAllRules();

        kSession.getKieBase().removeRule( "org.drools.test", "Two" );
        kSession.fireAllRules();
    }

    @Test
    public void testAddRemoveWithReloadInSamePackage_4Rules() {
        final String drl = "package org.drools.test;\n" +

                "declare Fakt enabled : boolean end \n" +

                "rule Build1\n" +
                "when\n" +
                "    Fakt( enabled == true )\n" +
                "then\n" +
                "end\n" +

                "rule Build2\n" +
                "when\n" +
                "    Fakt( enabled == true )\n" +
                "then\n" +
                "end\n" +

                "rule Mark \n" +
                "salience 9999\n" +
                "when\n" +
                "then\n" +
                "    insertLogical( new Fakt( true ) );\n" +
                "end\n" +

                "rule Build3 \n" +
                "when\n" +
                "    Fakt( enabled == true ) \n" +
                "then\n" +
                "end\n" +

                "rule Build4 \n" +
                "when\n" +
                "    Fakt( enabled == true )\n" +
                "then\n" +
                "end\n" +

                "";

        testAddRemoveWithReloadInSamePackage(drl);
    }

    @Test
    public void testAddRemoveWithReloadInSamePackage_3Rules() {
        final String drl = "package org.drools.test;\n" +

                "declare Fakt enabled : boolean end \n" +

                "rule Build1\n" +
                "when\n" +
                "    Fakt( enabled == true )\n" +
                "then\n" +
                "end\n" +

                "rule Build2\n" +
                "when\n" +
                "    Fakt( enabled == true )\n" +
                "then\n" +
                "end\n" +

                "rule Mark \n" +
                "salience 9999\n" +
                "when\n" +
                "then\n" +
                "    insertLogical( new Fakt( true ) );\n" +
                "end\n" +

                "rule Build3 \n" +
                "when\n" +
                "    Fakt( enabled == true ) \n" +
                "then\n" +
                "end\n" +

                "";

        testAddRemoveWithReloadInSamePackage(drl);
    }


    @Test
    public void testAddRemoveWithReloadInSamePackage_EntryPoints() {
        final String drl = "package org.drools.test; \n" +

                "rule \"Input_X\"\n" +
                "when\n" +
                "    Double() from entry-point \"A\"\n" +
                "    not String( )\n" +
                "then\n" +
                "end\n" +

                "rule \"Input_Y\"\n" +
                "when\n" +
                "    Double() from entry-point \"A\"\n" +
                "    not Float( )\n" +
                "then\n" +
                "end\n" +

                "rule \"OverrideInput_Temp\"\n" +
                "when\n" +
                "    Double() from entry-point \"A\"\n" +
                "    Float( )\n" +
                "then\n" +
                "end\n" +

                "rule \"Zero\"\n" +
                "when\n" +
                "then\n" +
                "end\n" +

                "";

        testAddRemoveWithReloadInSamePackage(drl);
    }

    @Test
    public void testAddRemoveWithReloadInSamePackage_EntryPointsVariety() {
        final String drl = "package org.drools.test; \n" +

                "rule \"Input_X\"\n" +
                "when\n" +
                "    Double() \n" +
                "    not String( )\n" +
                "then\n" +
                "end\n" +

                "rule \"Input_Y\"\n" +
                "when\n" +
                "    Double() from entry-point \"A\"\n" +
                "    not Float( )\n" +
                "then\n" +
                "end\n" +

                "rule \"OverrideInput_Temp\"\n" +
                "when\n" +
                "    Double() from entry-point \"A\"\n" +
                "    Float( )\n" +
                "then\n" +
                "end\n" +

                "rule \"Zero\"\n" +
                "when\n" +
                "then\n" +
                "end\n" +

                "";

        testAddRemoveWithReloadInSamePackage(drl);
    }

    private void testAddRemoveWithReloadInSamePackage(final String drl) {
        final String simpleRuleInTestPackage = "package org.drools.test; \n" +
                "global java.util.List list; \n" +
                "rule \"Later\" " +
                "when " +
                "   $s : String( ) " +
                "then " +
                "   System.out.println( \"ok\" ); " +
                "   list.add( \"ok\" ); \n" +
                "end ";
        final KieSession knowledgeSession = TestUtil.buildSessionInSteps(base, drl, simpleRuleInTestPackage);
        final List list = new ArrayList();
        knowledgeSession.setGlobal("list", list);

        knowledgeSession.insert("go");
        knowledgeSession.fireAllRules();
        assertEquals(Collections.singletonList("ok"), list);
    }

    @Test
    public void testRemoveWithDuplicatedCondition() {
        final String packageName = "test_same_condition_pk" ;
        final String rule = "package " + packageName + ";" +
                "rule 'test_same_condition' \n" +
                "when \n" +
                " String(this == \"1\") \n" +
                " String(this == \"1\") \n" +
                "then \n" +
                "System.out.println('test same condition rule'); \n"+
                "end";
        final KnowledgeBuilder kbuilder = TestUtil.createKnowledgeBuilder(null, rule);
        base.addPackages( kbuilder.getKnowledgePackages() );
        base.removeKiePackage(packageName);
    }

    @Test
    public void testFireAfterRemoveDuplicatedConditionInDifferentPackages() {
        final String packageName = "test_same_condition_pk" ;
        final String packageName2 = "test_same_condition_pk_2" ;
        final String rule1 = "package " + packageName + ";" +
                "import java.util.Map; \n" +
                "rule 'test_same_condition' \n" +
                "when \n" +
                " Map(this['name'] == 'Michael') \n" +
                "then \n" +
                "System.out.println('test same condition rule'); \n"+
                "end";
        final String rule2 = "package " + packageName2 + ";" +
                "import java.util.Map; \n" +
                "rule 'test_same_condition_2' \n" +
                "when \n" +
                " Map(this['name'] == 'Michael') \n" +
                " Map(this['test'] == '1') \n" +
                "then \n" +
                "System.out.println('test same condition rule 2'); \n"+
                "end";
        final KieSession session = TestUtil.buildSessionInSteps(base, rule1, rule2 );
        session.getKieBase().removeKiePackage(packageName);
        session.fireAllRules();
        final Map<String, Object> fact = new HashMap<>();
        fact.put("name", "Michael");
        session.insert(fact);
        session.fireAllRules();
    }

    @Test
    public void testAddRemoveWithExtends() {
        final String packageName = "test_same_condition_pk" ;
        final String rule1 = "package " + packageName + ";" +
                "import java.util.Map; \n" +
                "rule \"parentRule\" \n" +
                "when \n" +
                " Map(this['name'] == 'Michael') \n" +
                "then \n" +
                "System.out.println('Parent rule!'); \n"+
                "end";
        final String rule2 = "package " + packageName + ";" +
                "import java.util.Map; \n" +
                "rule \"childRule\" \n" +
                "     extends \"parentRule\"\n" +
                "when \n" +
                " Map(this['test'] == '1') \n" +
                "then \n" +
                "System.out.println('Child rule!'); \n"+
                "end";

        final KieSession session = TestUtil.buildSessionInSteps(base, true, rule1, rule2);
        session.fireAllRules();
        final Map<String, Object> fact = new HashMap<>();
        fact.put("name", "Michael");
        fact.put("test", 1);
        session.insert(fact);

        try {
            session.getKieBase().removeRule(packageName, "parentRule");
            fail("A parent rule cannot be removed if one of its children is still there");
        } catch (final Exception e) {
            // OK
        }
    }

    @Test
    public void testRemoveHasSameConElement() {
        // DROOLS-891
        final String packageName = "test";
        final String rule1 = "package " + packageName + ";" +
                "import java.util.Map; \n" +
                "rule 'rule1' \n" +
                "when \n" +
                " Map(this['type'] == 'Goods' && this['brand'] == 'a') \n" +
                " Map(this['type'] == 'Goods' && this['category'] == 'b') \n" +
                "then \n" +
                "System.out.println('test rule 1'); \n"+
                "end";

        final KnowledgeBuilder kbuilder = TestUtil.createKnowledgeBuilder(null, rule1);
        base.addPackages( kbuilder.getKnowledgePackages() );
        base.removeKiePackage(packageName);
        final StatelessKieSession session = base.newStatelessKieSession();
        session.execute(new HashMap());
    }

    @Test
    public void testFireAfterRemoveWithSameCondition() {
        // DROOLS-893
        final String packageName = "pk1";
        final String packageName2 = "pk2";
        final String rule1 = "package " + packageName + ";" +
                "import java.util.Map; \n" +
                "rule 'rule1' \n" +
                "when \n" +
                " Map(this['type'] == 'Goods' ) \n" +
                " Map(this['x'] == 'y'  ) \n" +
                " Map(this['type'] == 'Juice'  ) \n" +
                " Map(this['kind'] == 'Stuff'  ) \n" +
                "then \n" +
                "end";

        final String rule2 = "package " + packageName2 + ";" +
                "import java.util.Map; \n" +
                "rule 'rule2' \n" +
                "when \n" +
                " Map(this['type'] == 'Goods' ) \n" +
                " Map(this['x'] == 'y'  ) \n" +
                " Map(this['type'] == 'Juice'  ) \n" +
                "then \n" +
                "end";

        final KieServices kieServices = KieServices.get();
        final ReleaseId releaseId = kieServices.newReleaseId("org.kie", "test-fire-after-remove-with-same-condition", "1.0");
        KieUtil.getKieModuleFromDrls(releaseId, kieBaseTestConfiguration, rule1, rule2);
        final KieContainer kieContainer = kieServices.newKieContainer(releaseId);
        final KieBase kbase = kieContainer.getKieBase();

        final Map<String, Object> map = new HashMap<>();
        map.put("type", "Goods");
        map.put("kind", "Stuff");
        map.put("x", "y");

        KieSession ksession = kbase.newKieSession();
        ksession.insert( map );
        ksession.fireAllRules();

        kbase.removeKiePackage( packageName2);

        ksession = kbase.newKieSession();
        ksession.insert(map);
        ksession.fireAllRules();
    }

    @Test
    public void testSameEval() {
        // DROOLS-893
        final String rule1Name = "rule1";
        final String rule2Name = "rule2";

        final String rule1 = "rule " + rule1Name + " \n " +
                "when \n" +
                " eval(true) \n" +
                "then \n" +
                "System.out.println('test rule 1'); \n"+
                "end";

        final String rule2 = "rule " + rule2Name + " \n " +
                "when \n" +
                "  eval(true) \n" +
                "then \n" +
                "System.out.println('test rule 2'); \n"+
                "end";

        final StatelessKieSession statelessSession = base.newStatelessKieSession();

        this.addRuleToEngine(rule1);
        statelessSession.execute(new Object());

        this.addRuleToEngine(rule2);
        statelessSession.execute(new Object());
    }

    @Test
    public void testFireAfterRemoveRule() {
        // DROOLS-893
        final String rule1Name = "rule1";
        final String rule2Name = "rule2";

        final String rule1 =  "rule " + rule1Name + " \n" +
                "when \n" +
                " Map(  this['type'] == 'Goods'  )" +
                " and " +
                " Map(  this['type'] == 'Cinema'  )" +
                "then \n" +
                " System.out.println('test in rule1'); \n"+
                "end";

        final String rule2 =  "rule " + rule2Name + " \n" +
                "when \n" +
                " Map(  this['type'] == 'Goods'  )" +
                " and " +
                " Map(  this['type'] == 'Cinema'  )" +
                "then \n" +
                " System.out.println('test in rule2'); \n"+
                "end";

        final Map<String, Object> fact = new HashMap<>();
        fact.put("type", "Cinema");

        final StatelessKieSession session = base.newStatelessKieSession();

        this.addRuleToEngine(rule1);
        session.execute(fact);

        this.addRuleToEngine(rule2);
        session.execute(fact);

        this.deleteRule(rule1Name);

        session.execute(fact);
    }

    @Test
    public void testRemoveWithSameRuleNameInDiffPackage() {
        final String packageName = "pk1";
        final String packageName2 = "pk2";
        final String rule1Name = "rule1";

        final String rule1 = "package " + packageName + ";" +
                "rule " + rule1Name + " \n" +
                "when \n" +
                " String( ) \n" +
                "then \n" +
                " System.out.println('test in rule1'); \n"+
                "end";

        final String rule2 = "package " + packageName2 + ";" +
                "rule " + rule1Name + " \n" +
                "when \n" +
                " Long( ) \n" +
                "then \n" +
                " System.out.println('test in rule2'); \n"+
                "end";

        final KieSession session = TestUtil.buildSessionInSteps( base, rule1, rule2 );
        session.getKieBase().removeKiePackage(packageName);
        session.getKieBase().removeKiePackage(packageName2);
        session.insert("");
        session.fireAllRules();
    }

    @Test
    public void testRemoveWithSplitStartAtLianAndFollowedBySubNetworkNoSharing() {
        final String packageName = "pk1";

        final String rule1 = "package " + packageName + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "rule R1 when\n" +
                "    $s : String()\n" +
                "    Integer()\n" +
                "    exists Integer() from globalInt.get()\n" +
                "then\n" +
                "end\n";


        final KieSession session = TestUtil.buildSessionInSteps(base, rule1 );

        session.setGlobal( "globalInt", new AtomicInteger(0) );
        session.insert( 1 );
        session.insert( "1" );

        session.fireAllRules();
        session.getKieBase().removeKiePackage(packageName);
    }

    @Test
    public void testRemoveExistsPopulatedByInitialFact() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                "global java.util.List list\n" +
                "rule R1 when\n" +
                "    exists( Integer() and Integer() )\n" +
                "then\n" +
                " list.add('R1'); \n" +
                "end\n";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                "global java.util.List list\n" +
                "rule R2 \n" +
                "when \n" +
                "    exists( Integer() and Integer() )\n" +
                "    String()" +
                "then \n" +
                " list.add('R2'); \n" +
                "end";

        AddRemoveTestCases.insertFactsRemoveFire(base, rule1, rule2, null, TestUtil.getDefaultFacts());
    }

    @Test
    public void testAddSplitInSubnetwork() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                "global java.util.List list\n" +
                "rule R1 when\n" +
                "    exists( Integer() and Integer() )\n" +
                "then\n" +
                " list.add('R1'); \n" +
                "end\n";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                "global java.util.List list\n" +
                "rule R2 \n" +
                "when \n" +
                "    exists( Integer() and String() )\n" +
                "    String()" +
                "then \n" +
                " list.add('R2'); \n" +
                "end";

        AddRemoveTestCases.insertFactsRemoveFire(base, rule1, rule2, null, TestUtil.getDefaultFacts());
    }

    @Test
    public void testRemoveWithSplitStartAtLianAndFollowedBySubNetworkWithSharing() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + TestUtil.RULE1_NAME + " \n" +
                "when\n" +
                "    $s : String()\n" +
                "    Integer()\n" +
                "    exists Integer() from globalInt.get()\n" +
                "then\n" +
                " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                "end\n";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                "global java.util.List list\n" +
                "rule " + TestUtil.RULE2_NAME + " \n" +
                "when \n" +
                "    $s1 : String()\n" +
                "    $s2 : String()\n" +
                "then \n" +
                " list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                "end";

        testRemoveWithSplitStartBasicTestSet(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME);
    }

    @Test
    public void testRemoveWithSplitStartBeforeJoinAndFollowedBySubNetworkWithSharing() {
        //  moved the split start to after the Integer
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + TestUtil.RULE1_NAME + " \n" +
                "when\n" +
                "    $s : String()\n" +
                "    Integer()\n" +
                "    exists Integer() from globalInt.get()\n" +
                "then\n" +
                " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                "end\n";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + TestUtil.RULE2_NAME + " \n" +
                "when \n" +
                "    $s : String()\n" +
                "    exists Integer() from globalInt.get()\n" +
                "    Integer()\n" +
                "    String()\n" +
                "then \n" +
                " list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                "end";

        testRemoveWithSplitStartBasicTestSet(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME);
    }

    @Test
    public void testRemoveWithSplitStartAtJoinAndFollowedBySubNetworkWithSharing() {
        //  moved the split start to after the Integer
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + TestUtil.RULE1_NAME + " \n" +
                "when\n" +
                "    $s : String()\n" +
                "    Integer()\n" +
                "    exists Integer() from globalInt.get()\n" +
                "then\n" +
                " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                "end\n";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + TestUtil.RULE2_NAME + " \n" +
                "when \n" +
                "    $s : String()\n" +
                "    Integer()\n" +
                "    String()\n" +
                "then \n" +
                " list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                "end";

        testRemoveWithSplitStartBasicTestSet(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME);
    }

    @Test
    public void testRemoveWithSplitStartSameRules() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + TestUtil.RULE1_NAME + " \n" +
                "when\n" +
                "    $s : String()\n" +
                "    Integer()\n" +
                "    exists( Integer() and Integer() )\n" +
                "then\n" +
                " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                "end\n";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + TestUtil.RULE2_NAME + " \n" +
                "when \n" +
                "    $s : String()\n" +
                "    Integer()\n" +
                "    exists( Integer() and Integer() )\n" +
                "then \n" +
                " list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                "end";

        testRemoveWithSplitStartBasicTestSet(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME);
    }

    @Test
    public void testRemoveWithSplitStartDoubledExistsConstraint() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + TestUtil.RULE1_NAME + " \n" +
                "when\n" +
                "    $s : String()\n" +
                "    Integer()\n" +
                "    exists( Integer() and Integer() )\n" +
                "    exists( Integer() and Integer() )\n" +
                "then\n" +
                " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                "end\n";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + TestUtil.RULE2_NAME + " \n" +
                "when \n" +
                "    $s : String()\n" +
                "    Integer()\n" +
                "    exists( Integer() and Integer() )\n" +
                "then \n" +
                " list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                "end";

        testRemoveWithSplitStartBasicTestSet(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME);
    }

    @Test
    public void testRemoveWithSplitStartDoubledIntegerConstraint() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + TestUtil.RULE1_NAME + " \n" +
                "when\n" +
                "    $s : String()\n" +
                "    Integer()\n" +
                "    exists( Integer() and Integer() )\n" +
                "then\n" +
                " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                "end\n";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + TestUtil.RULE2_NAME + " \n" +
                "when \n" +
                "    $s : String()\n" +
                "    Integer()\n" +
                "    Integer()\n" +
                "    exists( Integer() and Integer() )\n" +
                "then \n" +
                " list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                "end";

        testRemoveWithSplitStartBasicTestSet(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME);
    }

    @Test
    public void testRemoveWithSplitStartAfterSubnetwork() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + TestUtil.RULE1_NAME + " \n" +
                "when\n" +
                "    $s : String()\n" +
                "    Integer()\n" +
                "    exists( Integer() and Integer() )\n" +
                "then\n" +
                " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                "end\n";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + TestUtil.RULE2_NAME + " \n" +
                "when \n" +
                "    $s : String()\n" +
                "    Integer()\n" +
                "    exists( Integer() and Integer() )\n" +
                "    String()\n" +
                "then \n" +
                " list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                "end";

        testRemoveWithSplitStartBasicTestSet(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME);
    }

    @Test
    public void testRemoveWithSplitStartAfterSubnetwork3Rules() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + TestUtil.RULE1_NAME + " \n" +
                "when\n" +
                "    $s : String()\n" +
                "    Integer()\n" +
                "    exists( Integer() and Integer() )\n" +
                "then\n" +
                " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                "end\n";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + TestUtil.RULE2_NAME + " \n" +
                "when \n" +
                "    $s : String()\n" +
                "    Integer()\n" +
                "    exists( Integer() and Integer() )\n" +
                "    String()\n" +
                "then \n" +
                " list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                "end";

        final String rule3 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + TestUtil.RULE3_NAME + " \n" +
                "when \n" +
                "    $s : String()\n" +
                "    Integer()\n" +
                "    exists( Integer() and exists(Integer() and Integer()))\n" +
                "    String()\n" +
                "then \n" +
                " list.add('" + TestUtil.RULE3_NAME + "'); \n" +
                "end";

        final KieSession kieSession = TestUtil.buildSessionInSteps(base, rule1, rule2, rule3);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            kieSession.setGlobal("globalInt", new AtomicInteger(0));
            TestUtil.insertFacts(kieSession, 1, 2, "1");
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, TestUtil.RULE3_NAME);
            resultsList.clear();
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, TestUtil.RULE3_NAME);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testRemoveWithSplitStartAfterSubnetwork3RulesAddOneAfterAnother() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + TestUtil.RULE1_NAME + " \n" +
                "when\n" +
                "    $s : String()\n" +
                "    Integer()\n" +
                "    exists( Integer() and Integer() )\n" +
                "then\n" +
                " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                "end\n";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + TestUtil.RULE2_NAME + " \n" +
                "when \n" +
                "    $s : String()\n" +
                "    Integer()\n" +
                "    exists( Integer() and Integer() )\n" +
                "    String()\n" +
                "then \n" +
                " list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                "end";

        final String rule3 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + TestUtil.RULE3_NAME + " \n" +
                "when \n" +
                "    $s : String()\n" +
                "    Integer()\n" +
                "    exists( Integer() and exists(Integer() and Integer()))\n" +
                "    String()\n" +
                "then \n" +
                " list.add('" + TestUtil.RULE3_NAME + "'); \n" +
                "end";

        final KieSession kieSession = TestUtil.buildSessionInSteps(base, rule1);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            kieSession.setGlobal("globalInt", new AtomicInteger(0));
            TestUtil.insertFacts(kieSession, 1, 1, "1");
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE1_NAME);
            resultsList.clear();

            TestUtil.addRules(kieSession, rule2);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE2_NAME);
            resultsList.clear();

            TestUtil.addRules(kieSession, rule3);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE3_NAME);
            resultsList.clear();

            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, TestUtil.RULE3_NAME);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testRemoveWithSplitStartAfterSubnetwork3RulesReaddRule() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                             "global java.util.List list\n" +
                             "rule " + TestUtil.RULE1_NAME + " \n" +
                             "when\n" +
                             "    $s : String()\n" +
                             "    Integer()\n" +
                             "    exists( Integer() and Integer() )\n" +
                             "then\n" +
                             " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                             "end\n";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                             "global java.util.List list\n" +
                             "rule " + TestUtil.RULE2_NAME + " \n" +
                             "when \n" +
                             "    $s : String()\n" +
                             "    Integer()\n" +
                             "    exists( Integer() and Integer() )\n" +
                             "    String()\n" +
                             "then \n" +
                             " list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                             "end";

        final String rule3 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                             "global java.util.List list\n" +
                             "rule " + TestUtil.RULE3_NAME + " \n" +
                             "when \n" +
                             "    $s : String()\n" +
                             "    Integer()\n" +
                             "    exists( Integer() and exists(Integer() and Integer()))\n" +
                             "    String()\n" +
                             "then \n" +
                             " list.add('" + TestUtil.RULE3_NAME + "'); \n" +
                             "end";

        final KieSession kieSession = TestUtil.buildSessionInSteps(base, rule1, rule2, rule3);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            TestUtil.insertFacts(kieSession, 1, 2, "1");
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, TestUtil.RULE2_NAME);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE1_NAME, TestUtil.RULE3_NAME);
            resultsList.clear();
            TestUtil.addRules(kieSession, rule2);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE2_NAME);
        } finally {
            kieSession.dispose();
        }
    }

    private String[] getRules1Pattern() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                             "global java.util.List list\n" +
                             "rule " + TestUtil.RULE1_NAME + " \n" +
                             "when\n" +
                             "    $i : Integer(this>1)\n" +
                             "then\n" +
                             " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                             "end\n";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                             "global java.util.List list\n" +
                             "rule " + TestUtil.RULE2_NAME + " \n" +
                             "when \n" +
                             "    $i : Integer(this>2)\n" +
                             "then \n" +
                             " list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                             "end";

        return new String[] { rule1, rule2 };
    }

    private String[] getRules2Pattern() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                             "global java.util.List list\n" +
                             "rule " + TestUtil.RULE1_NAME + " \n" +
                             "when\n" +
                             "    $i1 : Integer(this>1)\n" +
                             "    $i2 : Integer(this>1)\n" +
                             "then\n" +
                             " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                             "end\n";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                             "global java.util.List list\n" +
                             "rule " + TestUtil.RULE2_NAME + " \n" +
                             "when \n" +
                             "    $i1 : Integer(this>1)\n" +
                             "    $i2 : Integer(this>2)\n" +
                             "then \n" +
                             " list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                             "end";

        return new String[] { rule1, rule2 };
    }

    @Test
    public void testRemoveRuleChangeFHFirstLeftTuple() {
        final String[] rules = getRules1Pattern();

        final KieSession kieSession = TestUtil.buildSessionInSteps(base, rules);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            TestUtil.insertFacts(kieSession, 3);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE1_NAME, TestUtil.RULE2_NAME);
            resultsList.clear();
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, TestUtil.RULE1_NAME);
            kieSession.fireAllRules();

            final InternalFactHandle  fh1 = (InternalFactHandle) kieSession.getFactHandle(3);
            assertNotNull( fh1.getFirstLeftTuple() );
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testRemoveRuleChangeFHLastLeftTuple() {
        final String[] rules = getRules1Pattern();

        final KieSession kieSession = TestUtil.buildSessionInSteps(base, rules);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            TestUtil.insertFacts(kieSession, 3);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE1_NAME, TestUtil.RULE2_NAME);
            resultsList.clear();
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, TestUtil.RULE2_NAME);
            kieSession.fireAllRules();

            final InternalFactHandle  fh1 = (InternalFactHandle) kieSession.getFactHandle(3);
            assertNotNull( fh1.getFirstLeftTuple() );
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testRemoveRightTupleThatWasFirst() {
        final String[] rules = getRules2Pattern();

        final KieSession kieSession = TestUtil.buildSessionInSteps(base, rules);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            TestUtil.insertFacts(kieSession, 3);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE1_NAME, TestUtil.RULE2_NAME);
            resultsList.clear();
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, TestUtil.RULE1_NAME);
            kieSession.fireAllRules();

            final Map<String, Rule> rulesMap = rulestoMap(kieSession.getKieBase());
            final InternalFactHandle  fh1 = (InternalFactHandle) kieSession.getFactHandle(3);
            assertNotNull( fh1.getFirstRightTuple() );
            assertEquals( 1, fh1.getFirstRightTuple().getTupleSink().getAssociatedRuleSize() );
            assertTrue( fh1.getFirstRightTuple().getTupleSink().isAssociatedWith(rulesMap.get(TestUtil.RULE2_NAME)));
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testRemoveRightTupleThatWasLast() {
        final String[] rules = getRules2Pattern();

        final KieSession kieSession = TestUtil.buildSessionInSteps(base, rules);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            TestUtil.insertFacts(kieSession, 3);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE1_NAME, TestUtil.RULE2_NAME);
            resultsList.clear();
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, TestUtil.RULE2_NAME);
            kieSession.fireAllRules();

            final Map<String, Rule> rulesMap = rulestoMap(kieSession.getKieBase());
            final InternalFactHandle  fh1 = (InternalFactHandle) kieSession.getFactHandle(3);
            assertNotNull( fh1.getFirstRightTuple() );
            assertEquals( 1, fh1.getFirstRightTuple().getTupleSink().getAssociatedRuleSize() );
            assertTrue( fh1.getFirstRightTuple().getTupleSink().isAssociatedWith(rulesMap.get(TestUtil.RULE1_NAME)));
        } finally {
            kieSession.dispose();
        }
    }


    private String[] getRules3Pattern() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                             "global java.util.List list\n" +
                             "rule " + TestUtil.RULE1_NAME + " \n" +
                             "when\n" +
                             "    $i1 : Integer(this>1)\n" +
                             "    $i2 : Integer(this>1)\n" +
                             "    $i3 : Integer(this>0)\n" +
                             "then\n" +
                             " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                             "end\n";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                             "global java.util.List list\n" +
                             "rule " + TestUtil.RULE2_NAME + " \n" +
                             "when \n" +
                             "    $i1 : Integer(this>1)\n" +
                             "    $i2 : Integer(this>1)\n" +
                             "    $i3 : Integer(this>1)\n" +
                             "then \n" +
                             " list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                             "end";

        final String rule3 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                             "global java.util.List list\n" +
                             "rule " + TestUtil.RULE3_NAME + " \n" +
                             "when \n" +
                             "    $i1 : Integer(this>1)\n" +
                             "    $i2 : Integer(this>1)\n" +
                             "    $i3 : Integer(this>2)\n" +
                             "then \n" +
                             " list.add('" + TestUtil.RULE3_NAME + "'); \n" +
                             "end";

        return new String[] { rule1, rule2, rule3 };
    }

    @Test
    public void testRemoveChildLeftTupleThatWasFirst() {
        final String[] rules = getRules3Pattern();

        final KieSession kieSession = TestUtil.buildSessionInSteps(base, rules[0], rules[1]);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            TestUtil.insertFacts(kieSession, 3);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE1_NAME, TestUtil.RULE2_NAME);
            resultsList.clear();
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, TestUtil.RULE1_NAME);
            kieSession.fireAllRules();

            final Map<String, Rule> rulesMap = rulestoMap(kieSession.getKieBase());
            final InternalFactHandle  fh1 = (InternalFactHandle) kieSession.getFactHandle(3);
            final LeftTuple lt = fh1.getFirstLeftTuple().getFirstChild().getFirstChild();
            assertSame(lt, fh1.getFirstLeftTuple().getFirstChild().getLastChild());
            assertNull( lt.getPeer() );
            assertEquals( 1, lt.getTupleSink().getAssociatedRuleSize() );
            assertTrue( lt.getTupleSink().isAssociatedWith(rulesMap.get(TestUtil.RULE2_NAME)));
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testRemoveChildLeftTupleThatWasLast() {
        final String[] rules = getRules3Pattern();

        final KieSession kieSession = TestUtil.buildSessionInSteps(base, rules[0], rules[1]);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            TestUtil.insertFacts(kieSession, 3);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE1_NAME, TestUtil.RULE2_NAME);
            resultsList.clear();
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, TestUtil.RULE2_NAME);
            kieSession.fireAllRules();

            final Map<String, Rule> rulesMap = rulestoMap(kieSession.getKieBase());
            final InternalFactHandle  fh1 = (InternalFactHandle) kieSession.getFactHandle(3);
            final LeftTuple lt = fh1.getFirstLeftTuple().getFirstChild().getFirstChild();
            assertSame(lt, fh1.getFirstLeftTuple().getFirstChild().getLastChild());
            assertNull( lt.getPeer() );
            assertEquals( 1, lt.getTupleSink().getAssociatedRuleSize() );
            assertTrue( lt.getTupleSink().isAssociatedWith(rulesMap.get(TestUtil.RULE1_NAME)));
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testRemoveChildLeftTupleThatWasMiddle() {
        final String[] rules = getRules3Pattern();

        final KieSession kieSession = TestUtil.buildSessionInSteps(base, rules);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            TestUtil.insertFacts(kieSession, 3);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, TestUtil.RULE3_NAME);
            resultsList.clear();
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, TestUtil.RULE2_NAME);
            kieSession.fireAllRules();

            final Map<String, Rule> rulesMap = rulestoMap(kieSession.getKieBase());
            final InternalFactHandle  fh1 = (InternalFactHandle) kieSession.getFactHandle(3);
            final LeftTuple lt = fh1.getFirstLeftTuple().getFirstChild();
            assertSame(lt, fh1.getFirstLeftTuple().getLastChild());
            assertEquals( 1, lt.getTupleSink().getAssociatedRuleSize() );
            assertTrue( lt.getTupleSink().isAssociatedWith(rulesMap.get(TestUtil.RULE1_NAME)));

            final LeftTuple peer = lt.getPeer();
            assertEquals( 1, peer.getTupleSink().getAssociatedRuleSize() );
            assertTrue( peer.getTupleSink().isAssociatedWith(rulesMap.get(TestUtil.RULE3_NAME)));
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testRemoveChildLeftTupleThatWasFirstWithMultipleData() {
        final String[] rules = getRules3Pattern();

        final KieSession kieSession = TestUtil.buildSessionInSteps(base, rules[0], rules[1]);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            TestUtil.insertFacts(kieSession, 3, 4, 5);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE1_NAME, TestUtil.RULE2_NAME);
            resultsList.clear();
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, TestUtil.RULE1_NAME);
            kieSession.fireAllRules();

            final Map<String, Rule>        rulesMap = rulestoMap(kieSession.getKieBase());

            final InternalFactHandle  fh1 = (InternalFactHandle) kieSession.getFactHandle(3);
            final InternalFactHandle  fh3 = (InternalFactHandle) kieSession.getFactHandle(5);
            final LeftTuple lt1 = fh1.getFirstLeftTuple();

            final LeftTuple lt1_1 = lt1.getFirstChild();
            final LeftTuple lt1_2 = lt1_1.getHandleNext();
            final LeftTuple lt1_3= lt1_2.getHandleNext();
            assertNotNull( lt1_1 );
            assertNotNull( lt1_2 );
            assertNotNull( lt1_3 );
            assertSame(lt1_3, lt1.getLastChild());

            assertSame(lt1_2, lt1_3.getHandlePrevious() );
            assertSame(lt1_1, lt1_2.getHandlePrevious() );

            assertEquals( 1, lt1_1.getTupleSink().getAssociatedRuleSize() );
            assertTrue( lt1_1.getTupleSink().isAssociatedWith(rulesMap.get(TestUtil.RULE2_NAME)));
            assertNull( lt1_1.getPeer() );

            assertEquals( 1, lt1_2.getTupleSink().getAssociatedRuleSize() );
            assertTrue( lt1_2.getTupleSink().isAssociatedWith(rulesMap.get(TestUtil.RULE2_NAME)));
            assertNull( lt1_2.getPeer() );

            assertEquals( 1, lt1_3.getTupleSink().getAssociatedRuleSize() );
            assertTrue( lt1_3.getTupleSink().isAssociatedWith(rulesMap.get(TestUtil.RULE2_NAME)));
            assertNull( lt1_3.getPeer() );


            final RightTuple rt1 = fh3.getFirstRightTuple();
            final LeftTuple rt1_1 = rt1.getLastChild();
            assertSame( lt1_1, rt1_1);

            final LeftTuple rt1_2 = rt1_1.getRightParentPrevious();
            final LeftTuple rt1_3 = rt1_2.getRightParentPrevious();

            assertNotNull( rt1_1 );
            assertNotNull( rt1_2 );
            assertNotNull( rt1_3 );

            assertSame(rt1_2, rt1_3.getRightParentNext() );
            assertSame(rt1_1, rt1_2.getRightParentNext() );

            assertEquals( 1, rt1_1.getTupleSink().getAssociatedRuleSize() );
            assertTrue( rt1_1.getTupleSink().isAssociatedWith(rulesMap.get(TestUtil.RULE2_NAME)));
            assertNull( rt1_1.getPeer() );

            assertEquals( 1, rt1_2.getTupleSink().getAssociatedRuleSize() );
            assertTrue( rt1_2.getTupleSink().isAssociatedWith(rulesMap.get(TestUtil.RULE2_NAME)));
            assertNull( rt1_2.getPeer() );

            assertEquals( 1, rt1_3.getTupleSink().getAssociatedRuleSize() );
            assertTrue( rt1_3.getTupleSink().isAssociatedWith(rulesMap.get(TestUtil.RULE2_NAME)));
            assertNull( rt1_3.getPeer() );
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testRemoveChildLeftTupleThatWasLastWithMultipleData() {
        final String[] rules = getRules3Pattern();

        final KieSession kieSession = TestUtil.buildSessionInSteps(base, rules[0], rules[1]);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            TestUtil.insertFacts(kieSession, 3, 4, 5);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE1_NAME, TestUtil.RULE2_NAME);
            resultsList.clear();
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, TestUtil.RULE2_NAME);
            kieSession.fireAllRules();

            final Map<String, Rule> rulesMap = rulestoMap(kieSession.getKieBase());

            final InternalFactHandle  fh1 = (InternalFactHandle) kieSession.getFactHandle(3);
            final InternalFactHandle  fh3 = (InternalFactHandle) kieSession.getFactHandle(5);
            final LeftTuple lt1 = fh1.getFirstLeftTuple();

            final LeftTuple lt1_1 = lt1.getFirstChild();
            final LeftTuple lt1_2 = lt1_1.getHandleNext();
            final LeftTuple lt1_3= lt1_2.getHandleNext();
            assertNotNull( lt1_1 );
            assertNotNull( lt1_2 );
            assertNotNull( lt1_3 );
            assertSame(lt1_3, lt1.getLastChild());

            assertSame(lt1_2, lt1_3.getHandlePrevious() );
            assertSame(lt1_1, lt1_2.getHandlePrevious() );

            assertEquals( 1, lt1_1.getTupleSink().getAssociatedRuleSize() );
            assertTrue( lt1_1.getTupleSink().isAssociatedWith(rulesMap.get(TestUtil.RULE1_NAME)));
            assertNull( lt1_1.getPeer() );

            assertEquals( 1, lt1_2.getTupleSink().getAssociatedRuleSize() );
            assertTrue( lt1_2.getTupleSink().isAssociatedWith(rulesMap.get(TestUtil.RULE1_NAME)));
            assertNull( lt1_2.getPeer() );

            assertEquals( 1, lt1_3.getTupleSink().getAssociatedRuleSize() );
            assertTrue( lt1_3.getTupleSink().isAssociatedWith(rulesMap.get(TestUtil.RULE1_NAME)));
            assertNull( lt1_3.getPeer() );


            final RightTuple rt1 = fh3.getFirstRightTuple();
            final LeftTuple rt1_1 = rt1.getLastChild();
            assertSame( lt1_1, rt1_1);

            final LeftTuple rt1_2 = rt1_1.getRightParentPrevious();
            final LeftTuple rt1_3 = rt1_2.getRightParentPrevious();

            assertNotNull( rt1_1 );
            assertNotNull( rt1_2 );
            assertNotNull( rt1_3 );

            assertSame(rt1_2, rt1_3.getRightParentNext() );
            assertSame(rt1_1, rt1_2.getRightParentNext() );

            assertEquals( 1, rt1_1.getTupleSink().getAssociatedRuleSize() );
            assertTrue( rt1_1.getTupleSink().isAssociatedWith(rulesMap.get(TestUtil.RULE1_NAME)));
            assertNull( rt1_1.getPeer() );

            assertEquals( 1, rt1_2.getTupleSink().getAssociatedRuleSize() );
            assertTrue( rt1_2.getTupleSink().isAssociatedWith(rulesMap.get(TestUtil.RULE1_NAME)));
            assertNull( rt1_2.getPeer() );

            assertEquals( 1, rt1_3.getTupleSink().getAssociatedRuleSize() );
            assertTrue( rt1_3.getTupleSink().isAssociatedWith(rulesMap.get(TestUtil.RULE1_NAME)));
            assertNull( rt1_3.getPeer() );
        } finally {
            kieSession.dispose();
        }
    }

    private void testRemoveWithSplitStartBasicTestSet(final String rule1, final String rule2,
                                                      final String rule1Name, final String rule2Name) {
        final Map<String, Object> additionalGlobals = new HashMap<>();
        additionalGlobals.put("globalInt", new AtomicInteger(0));

        AddRemoveTestCases.runAllTestCases(base, rule1, rule2, rule1Name, rule2Name, additionalGlobals,1, 2, "1");
        AddRemoveTestCases.runAllTestCases(base, rule2, rule1, rule2Name, rule1Name, additionalGlobals,1, 2, "1");
    }

    @Test
    public void testMergeRTN() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                             "global java.util.List list\n" +
                             "rule " + TestUtil.RULE1_NAME + " \n" +
                             "when\n" +
                             "    Integer()\n" +
                             "    Integer()\n" +
                             "    Integer()\n" +
                             "then\n" +
                             " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                             "end\n";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                             "global java.util.List list\n" +
                             "rule " + TestUtil.RULE2_NAME + " \n" +
                             "when \n" +
                             "    Integer()\n" +
                             "    exists( Integer() and Integer() )\n" +
                             "    Integer()\n" +
                             "then \n" +
                             " list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                             "end";

        final KieSession kieSession = TestUtil.buildSessionInSteps(base, rule1, rule2);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            TestUtil.insertFacts(kieSession, 1, 2, 3);
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, TestUtil.RULE2_NAME);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE1_NAME);
            resultsList.clear();
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, TestUtil.RULE1_NAME);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testSubSubNetwork() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                             "global java.util.List list\n" +
                             "rule " + TestUtil.RULE1_NAME + " \n" +
                             "when\n" +
                             "    Integer()\n" +
                             "    Integer()\n" +
                             "    Integer()\n" +
                             "then\n" +
                             " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                             "end\n";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                             "global java.util.List list\n" +
                             "rule " + TestUtil.RULE2_NAME + " \n" +
                             "when \n" +
                             "    Integer()\n" +
                             "    exists( Integer() and Integer() )\n" +
                             "    exists(Integer() and exists(Integer() and Integer()))\n" +
                             "then \n" +
                             " list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                             "end";

        final KieSession kieSession = TestUtil.buildSessionInSteps(base, rule1, rule2);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            TestUtil.insertFacts(kieSession, 1);
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, TestUtil.RULE2_NAME);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE1_NAME);
            resultsList.clear();
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, TestUtil.RULE1_NAME);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testSubSubNetwork2() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                             "global java.util.List list\n" +
                             "rule " + TestUtil.RULE1_NAME + " \n" +
                             "when\n" +
                             "    Integer()\n" +
                             "    Integer()\n" +
                             "then\n" +
                             " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                             "end\n";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                             "global java.util.List list\n" +
                             "rule " + TestUtil.RULE2_NAME + " \n" +
                             "when \n" +
                             "    Integer()\n" +
                             "    exists( Integer() and Integer() )\n" +
                             "    exists(Integer() and exists(Integer() and Integer()))\n" +
                             "then \n" +
                             " list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                             "end";

        final KieSession kieSession = TestUtil.buildSessionInSteps(base, rule1, rule2);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            TestUtil.insertFacts(kieSession, 1);
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME);
            TestUtil.addRules(kieSession, rule1);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE1_NAME);
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testSubSubNetwork3() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                             "global java.util.List list\n" +
                             "rule " + TestUtil.RULE1_NAME + " \n" +
                             "when\n" +
                             "  exists(Integer() and Integer()) \n" +
                             "  exists(Integer() and Integer()) \n" +
                             "  Integer() \n\n" +
                             "then\n" +
                             " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                             "end\n";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                             "global java.util.List list\n" +
                             "rule " + TestUtil.RULE2_NAME + " \n" +
                             "when \n" +
                             "  exists(Integer() and Integer()) \n" +
                             "  Integer() \n" +
                             "  exists(Integer() and exists(Integer() and Integer())) \n" +
                             "then \n" +
                             " list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                             "end";

        final KieSession kieSession = TestUtil.buildSessionInSteps(base, rule1, rule2);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            TestUtil.insertFacts(kieSession, 1);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE1_NAME, TestUtil.RULE2_NAME);
            resultsList.clear();
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testSubSubNetwork4() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                             "global java.util.List list\n" +
                             "rule " + TestUtil.RULE1_NAME + " \n" +
                             "when\n" +
                             "  exists(Integer() and Integer()) \n" +
                             "  Integer() \n" +
                             "  Integer() \n" +
                             "then\n" +
                             " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                             "end\n";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                             "global java.util.List list\n" +
                             "rule " + TestUtil.RULE2_NAME + " \n" +
                             "when \n" +
                             "  exists(Integer() and Integer()) \n" +
                             "  Integer() \n" +
                             "  exists(Integer() and exists(Integer() and Integer())) \n" +
                             "then \n" +
                             " list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                             "end";

        final KieSession kieSession = TestUtil.buildSessionInSteps(base, rule1, rule2);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            TestUtil.insertFacts(kieSession, 1);
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, TestUtil.RULE1_NAME);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE2_NAME);
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testSubNetworkWithNot() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                             "global java.util.List list\n" +
                             "rule " + TestUtil.RULE1_NAME + " \n" +
                             "when\n" +
                             "  exists(Integer() and Integer()) \n" +
                             "  not(Double() and Double()) \n" +
                             "  Integer() \n" +
                             "then\n" +
                             " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                             "end\n";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                             "global java.util.List list\n" +
                             "rule " + TestUtil.RULE2_NAME + " \n" +
                             "when \n" +
                             "  exists(Integer() and Integer()) \n" +
                             "  not(Double() and Double()) \n" +
                             "then \n" +
                             " list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                             "end";

        final KieSession kieSession = TestUtil.buildSessionInSteps(base, rule1, rule2);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            TestUtil.insertFacts(kieSession, 1);
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, TestUtil.RULE2_NAME);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE1_NAME);
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testSubNetworkWithNot2() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                             "global java.util.List list\n" +
                             "rule " + TestUtil.RULE1_NAME + " \n" +
                             "when\n" +
                             "  exists(Integer() and Integer()) \n" +
                             "  not(Double() and Double()) \n" +
                             "  Integer() \n" +
                             "then\n" +
                             " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                             "end\n";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                             "global java.util.List list\n" +
                             "rule " + TestUtil.RULE2_NAME + " \n" +
                             "when \n" +
                             "  exists(Integer() and Integer()) \n" +
                             "  not(Double() and Double()) \n" +
                             "  exists(Integer() and Integer()) \n" +
                             "then \n" +
                             " list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                             "end";

        final KieSession kieSession = TestUtil.buildSessionInSteps(base, rule1, rule2);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            TestUtil.insertFacts(kieSession, 1);
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, TestUtil.RULE2_NAME);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE1_NAME);
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testSubNetworkWithNot3() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                             "global java.util.List list\n" +
                             "rule " + TestUtil.RULE1_NAME + " \n" +
                             "when\n" +
                             "  exists(Integer()) \n" +
                             "  not(Double() and Double()) \n" +
                             "  Integer() \n" +
                             "then\n" +
                             " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                             "end\n";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                             "global java.util.List list\n" +
                             "rule " + TestUtil.RULE2_NAME + " \n" +
                             "when \n" +
                             "  exists(Integer()) \n" +
                             "  not(Double() and Double()) \n" +
                             "then \n" +
                             " list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                             "end";

        final KieSession kieSession = TestUtil.buildSessionInSteps(base, rule1, rule2);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            TestUtil.insertFacts(kieSession, 1);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE1_NAME, TestUtil.RULE2_NAME);
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testSubNetworkWithNot4() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                             "global java.util.List list\n" +
                             "rule " + TestUtil.RULE1_NAME + " \n" +
                             "when\n" +
                             "  exists(Integer() and Integer()) \n" +
                             "  exists(Integer() and exists(Integer() and Integer())) \n" +
                             "  Integer() \n" +
                             "  not(Double() and Double()) \n" +
                             "then\n" +
                             " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                             "end\n";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                             "global java.util.List list\n" +
                             "rule " + TestUtil.RULE2_NAME + " \n" +
                             "when \n" +
                             "  exists(Integer() and Integer()) \n" +
                             "  Integer() \n" +
                             "  not(Double() and Double()) \n" +
                             "  exists(Integer() and Integer()) \n" +
                             "then \n" +
                             " list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                             "end";

        final KieSession kieSession = TestUtil.buildSessionInSteps(base, rule1, rule2);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            TestUtil.insertFacts(kieSession, 1);
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, TestUtil.RULE1_NAME);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE2_NAME);
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testInsertFireRemoveWith2Nots() {
        final String rule1 = " package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List list\n" +
                " rule " + TestUtil.RULE1_NAME + " \n" +
                " when \n" +
                "   Integer() \n" +
                "   Integer() \n" +
                " then\n" +
                "   list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = " package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List list\n" +
                " rule " + TestUtil.RULE2_NAME + " \n" +
                " when \n" +
                "   Integer() \n" +
                "   Integer() \n" +
                "   not(not(Integer() and Integer())) \n" +
                " then\n" +
                "   list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                " end";

        final KieSession kieSession = TestUtil.buildSessionInSteps(base, rule2, rule1);
        try {
            final List resultsList = new ArrayList();
            kieSession.setGlobal("list", resultsList);
            TestUtil.insertFacts(kieSession, 1);
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, TestUtil.RULE2_NAME);
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, TestUtil.RULE1_NAME);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testSubSubNetwork5() {
        final String rule1 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                             "global java.util.List list\n" +
                             "rule " + TestUtil.RULE1_NAME + " \n" +
                             "when\n" +
                             "  Integer() \n" +
                             "  Integer() \n" +
                             "  exists(Integer() and Integer()) \n" +
                             "then\n" +
                             " list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                             "end\n";

        final String rule2 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
                             "global java.util.List list\n" +
                             "rule " + TestUtil.RULE2_NAME + " \n" +
                             "when \n" +
                             "  Integer() \n" +
                             "  exists(Integer() and Integer()) \n" +
                             "then \n" +
                             " list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                             "end";

        final KieSession kieSession = TestUtil.buildSessionInSteps(base, rule1, rule2);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            TestUtil.insertFacts(kieSession, 1);
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME,  TestUtil.RULE1_NAME);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE2_NAME);
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testInsertRemoveFireWith2Nots() {
        final String rule1 = " package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List list\n" +
                " rule " + TestUtil.RULE1_NAME + " \n" +
                " when \n" +
                "   exists(Integer() and Integer()) \n" +
                "   Integer() \n" +
                "   not(exists(Integer() and Integer())) \n" +
                " then\n" +
                "   list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = " package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List list\n" +
                " rule " + TestUtil.RULE2_NAME + " \n" +
                " when \n" +
                "   exists(Integer() and Integer()) \n" +
                "   not(exists(Integer() and Integer())) \n" +
                " then\n" +
                "   list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                " end";

        final KieSession kieSession = TestUtil.buildSessionInSteps(base, rule1, rule2);
        try {
            final List resultsList = new ArrayList();
            kieSession.setGlobal("list", resultsList);
            TestUtil.insertFacts(kieSession, 1);
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, TestUtil.RULE2_NAME);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testSharedRian() {

        final String rule1 = " package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List list\n" +
                " rule " + TestUtil.RULE1_NAME + " \n" +
                " when \n" +
                "   Integer() \n" +
                "   not(Integer() and Integer()) \n" +
                " then\n" +
                "   list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = " package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List list\n" +
                " rule " + TestUtil.RULE2_NAME + " \n" +
                " when \n" +
                "   Integer() \n" +
                "   exists(Integer() and Integer()) \n" +
                " then\n" +
                "   list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                " end";

        final KieSession kieSession = TestUtil.buildSessionInSteps(base, rule1);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            TestUtil.insertFacts(kieSession, 1);
            TestUtil.addRules(kieSession, rule2);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE2_NAME);
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testSharedRianWithFire() {

        final String rule1 = " package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List list\n" +
                " rule " + TestUtil.RULE1_NAME + " \n" +
                " when \n" +
                "   Integer() \n" +
                "   not(Integer() and Integer()) \n" +
                " then\n" +
                "   list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = " package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List list\n" +
                " rule " + TestUtil.RULE2_NAME + " \n" +
                " when \n" +
                "   Integer() \n" +
                "   exists(Integer() and Integer()) \n" +
                " then\n" +
                "   list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                " end";

        final KieSession kieSession = TestUtil.buildSessionInSteps(base, rule1);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            TestUtil.insertFacts(kieSession, 1);
            kieSession.fireAllRules();
            TestUtil.addRules(kieSession, rule2);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE2_NAME);
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testSharedRian2() {

        final String rule1 = " package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List list\n" +
                " rule " + TestUtil.RULE1_NAME + " \n" +
                " when \n" +
                "   Integer() \n" +
                "   Integer() \n" +
                "   not(not(Integer() and Integer())) \n" +
                " then\n" +
                "   list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = " package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List list\n" +
                " rule " + TestUtil.RULE2_NAME + " \n" +
                " when \n" +
                "   Integer() \n" +
                "   exists(Integer() and exists(Integer() and Integer())) \n" +
                " then\n" +
                "   list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                " end";

        final KieSession kieSession = TestUtil.buildSessionInSteps(base, rule1);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            TestUtil.insertFacts(kieSession, 1);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE1_NAME);
            resultsList.clear();
            TestUtil.addRules(kieSession, rule2);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE2_NAME);
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testRemoveRuleWithSharedRia() {
        final String rule1Name = "rule1";
        final String rule2Name = "rule2";
        final String rule1 = "rule " + rule1Name + " \n" +
                             "when \n" +
                             "   Integer() \n" +
                             "   not(Integer() and Integer()) \n" +
                             "then \n" +
                             "System.out.println('test rule 1'); \n"+
                             "end";

        final String rule2 = "rule " + rule2Name + " \n" +
                             "when \n" +
                             "   Integer() \n" +
                             "   exists(Integer() and Integer()) \n" +
                             "then \n" +
                             "System.out.println('test  rule 2'); \n"+
                             "end";

        final KieSession session = base.newKieSession();

        this.addRuleToEngine(rule1);
        final InternalFactHandle fh = (InternalFactHandle)session.insert(1 );
        session.fireAllRules();

        this.addRuleToEngine(rule2);

        final SubnetworkTuple tuple = (SubnetworkTuple)fh.getFirstLeftTuple().getFirstChild().getFirstChild();
        assertNotNull( tuple.getPeer() );

        this.deleteRule(rule2Name);
        assertNull( tuple.getPeer() );
    }

    @Test
    public void testAddRemoveFacts() {
        final String rule1 = " package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List list\n" +
                " rule " + TestUtil.RULE1_NAME + " \n" +
                " when \n" +
                "   Integer() \n" +
                "   not(not(Integer() and Integer())) \n" +
                " then\n" +
                "   list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = " package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                " global java.util.List list\n" +
                " rule " + TestUtil.RULE2_NAME + " \n" +
                " when \n" +
                "   Integer() \n" +
                "   exists(Integer() and Integer()) \n" +
                " then\n" +
                "   list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                " end";


        final KieSession kieSession = TestUtil.buildSessionInSteps(base, rule1, rule2);
        try {
            final List resultsList = new ArrayList();
            kieSession.setGlobal("list", resultsList);
            final List<FactHandle> sessionFacts = TestUtil.insertFacts(kieSession, 1);
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();
            TestUtil.removeFacts(kieSession, sessionFacts);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testReaddRulesSharedRianDoubleNots() {

        final String rule1 = " package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                             " global java.util.List list\n" +
                             " rule " + TestUtil.RULE1_NAME + " \n" +
                             " when \n" +
                             "   exists(Integer() and Integer()) \n" +
                             " then\n" +
                             "   list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                             " end";

        final String rule2 = " package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                             " global java.util.List list\n" +
                             " rule " + TestUtil.RULE2_NAME + " \n" +
                             " when \n" +
                             "   not(not(exists(Integer() and Integer()))) \n" +
                             " then\n" +
                             "   list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                             " end";

        final KieSession kieSession = TestUtil.buildSessionInSteps(base, rule1, rule2);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            TestUtil.insertFacts(kieSession, 1);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE1_NAME, TestUtil.RULE2_NAME);
            resultsList.clear();
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();
            TestUtil.addRules(kieSession, rule1, rule2);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE1_NAME, TestUtil.RULE2_NAME);
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testOr() {

        final String rule1 = " package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                             " global java.util.List list\n" +
                             " rule " + TestUtil.RULE1_NAME + " \n" +
                             " when \n" +
                             "   $k: Integer()\n" +
                             "   ( Integer(this != 1) or Integer(this == 1) )\n" +
                             " then\n" +
                             "   list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                             " end";

        final String rule2 = " package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                             " global java.util.List list\n" +
                             " rule " + TestUtil.RULE2_NAME + " \n" +
                             " when \n" +
                             "   Integer()\n" +
                             " then\n" +
                             "   list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                             " end";

        final KieSession kieSession = TestUtil.buildSessionInSteps(base, rule2);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            TestUtil.insertFacts(kieSession, 1);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE2_NAME);
            resultsList.clear();
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, TestUtil.RULE2_NAME);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();
            TestUtil.addRules(kieSession, rule1, rule2);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE1_NAME, TestUtil.RULE2_NAME);
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testOr2() {

        final String rule1 = " package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                             " global java.util.List list\n" +
                             " rule " + TestUtil.RULE1_NAME + " \n" +
                             " when \n" +
                             "   Integer()\n" +
                             " then\n" +
                             "   list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                             " end";

        final String rule2 = " package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                             " global java.util.List list\n" +
                             " rule " + TestUtil.RULE2_NAME + " \n" +
                             " when \n" +
                             "   $k: Integer()\n" +
                             "   ( Integer(this != 1) or Integer(this == 1) )\n" +
                             " then\n" +
                             "   list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                             " end";

        final KieSession kieSession = TestUtil.buildSessionInSteps(base, rule1, rule2);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            TestUtil.insertFacts(kieSession, 1);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE1_NAME, TestUtil.RULE2_NAME);
            resultsList.clear();
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, TestUtil.RULE2_NAME);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, TestUtil.RULE1_NAME);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();
            TestUtil.addRules(kieSession, rule1);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE1_NAME);
            resultsList.clear();
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, TestUtil.RULE1_NAME);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testEvals() {

        final String rule1 = " package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                             " global java.util.List list\n" +
                             " rule " + TestUtil.RULE1_NAME + " \n" +
                             " when \n" +
                             "   Integer()\n" +
                             " then\n" +
                             "   list.add('" + TestUtil.RULE1_NAME + "'); \n" +
                             " end";

        final String rule2 = " package " + TestUtil.RULES_PACKAGE_NAME + ";\n" +
                             " global java.util.List list\n" +
                             " rule " + TestUtil.RULE2_NAME + " \n" +
                             " when \n" +
                             "  $j: Integer() \n" +
                             "  eval($j == 1) \n" +
                             "  (eval(true) or eval($j == 1) ) \n" +
                             " then\n" +
                             "   list.add('" + TestUtil.RULE2_NAME + "'); \n" +
                             " end";

        final KieSession kieSession = TestUtil.buildSessionInSteps(base, rule1, rule2);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            TestUtil.insertFacts(kieSession, 1);
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, TestUtil.RULE2_NAME);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE1_NAME);
            resultsList.clear();
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, TestUtil.RULE1_NAME);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testPathMemoryInitialization() {

        final String rule1 = "package com.rules;global java.util.List list\n" +
                "rule R1 \n" +
                " when \n" +
                "  exists(Integer() and Integer()) \n" +
                " Integer() \n" +
                " Integer() \n" +
                " then\n" +
                " list.add('R1'); \n" +
                "end\n";

        final String rule2 = "package com.rules;global java.util.List list\n" +
                "rule R2 \n" +
                " when \n" +
                "  exists(Integer() and exists(Integer() and Integer())) \n" +
                " Integer() \n" +
                " exists(Integer() and Integer()) \n" +
                " then\n" +
                " list.add('R2'); \n" +
                "end";

        final List<String> globalList = new ArrayList<>();

        final KieSession kieSession = TestUtil.buildSessionInSteps(base, rule1, rule2);
        kieSession.setGlobal("list", globalList);
        kieSession.insert(1);
        kieSession.insert(2);
        kieSession.insert(3);
        kieSession.insert("1");

        final Map<Object, String> mapFact = new HashMap<>(1);
        mapFact.put(new Object(), "1");
        kieSession.insert(mapFact);

        kieSession.getKieBase().removeRule("com.rules", "R2");
        kieSession.fireAllRules();

        Assertions.assertThat(globalList).contains("R1");
        globalList.clear();

        kieSession.getKieBase().removeRule("com.rules", "R1");
        kieSession.fireAllRules();

        Assertions.assertThat(globalList).isEmpty();
    }

    @Test
    public void testBuildKieBaseIncrementally() {

        final String rule1 = "package com.rules;global java.util.List list\n" +
                "rule R1 \n" +
                " when \n" +
                "  exists(Integer() and Integer()) \n" +
                " Integer() \n" +
                " Integer() \n" +
                " then\n" +
                " list.add('R1'); \n" +
                "end\n";

        final String rule2 = "package com.rules;global java.util.List list\n" +
                "rule R2 \n" +
                " when \n" +
                "  exists(Integer() and exists(Integer() and Integer())) \n" +
                " Integer() \n" +
                " exists(Integer() and Integer()) \n" +
                " then\n" +
                " list.add('R2'); \n" +
                "end";

        final List<String> globalList = new ArrayList<>();

        final KieSession kieSession = TestUtil.buildSessionInSteps(base, rule1, rule2);
        kieSession.setGlobal("list", globalList);
        kieSession.insert(1);
        kieSession.insert(2);
        kieSession.insert(3);
        kieSession.insert("1");

        final Map<Object, String> mapFact = new HashMap<>(1);
        mapFact.put(new Object(), "1");
        kieSession.insert(mapFact);
        kieSession.fireAllRules();
        Assertions.assertThat(globalList).contains("R1", "R2");
    }

    @Test
    public void testBuildKieBaseIncrementally2() {

        final String rule1 = "package com.rules;global java.util.List list\n" +
                "rule R1 \n" +
                " when \n" +
                "  exists(Integer() and Integer()) \n" +
                " Integer() \n" +
                " Integer() \n" +
                " then\n" +
                " list.add('R1'); \n" +
                "end\n";

        final String rule2 = "package com.rules;global java.util.List list\n" +
                "rule R2 \n" +
                " when \n" +
                "  exists(Integer() and exists(Integer() and Integer())) \n" +
                " Integer() \n" +
                " exists(Integer() and Integer()) \n" +
                " then\n" +
                " list.add('R2'); \n" +
                "end";

        final List<String> globalList = new ArrayList<>();

        final KieSession kieSession = TestUtil.buildSessionInSteps(base, rule1, rule2);
        kieSession.setGlobal("list", globalList);

        kieSession.fireAllRules();
        Assertions.assertThat(globalList).isEmpty();

        kieSession.insert(1);
        kieSession.insert(2);
        kieSession.insert(3);
        kieSession.insert("1");

        final Map<Object, String> mapFact = new HashMap<>(1);
        mapFact.put(new Object(), "1");
        kieSession.insert(mapFact);

        kieSession.fireAllRules();
        Assertions.assertThat(globalList).contains("R1", "R2");
    }

    @Test
    public void testBuildKieBaseIncrementally3() {

        final String rule1 = "package com.rules;global java.util.List list\n" +
                "rule R1 \n" +
                " when \n" +
                "  exists(Integer()) \n" +
                " Integer() \n" +
                " then\n" +
                " list.add('R1'); \n" +
                "end\n";

        final String rule2 = "package com.rules;global java.util.List list\n" +
                "rule R2 \n" +
                " when \n" +
                "  exists(Integer()) \n" +
                " not(not(Integer() and Integer())) \n" +
                " then\n" +
                " list.add('R2'); \n" +
                "end";

        final List<String> globalList = new ArrayList<>();

        final KieSession kieSession = TestUtil.buildSessionInSteps(base, rule2, rule1);
        kieSession.setGlobal("list", globalList);

        Assertions.assertThat(globalList).isEmpty();

        kieSession.insert(1);
        kieSession.insert("1");

        kieSession.getKieBase().removeRule("com.rules", "R1");
        kieSession.fireAllRules();

        Assertions.assertThat(globalList).contains("R2");
        globalList.clear();

        kieSession.getKieBase().removeRule("com.rules", "R2");
        kieSession.fireAllRules();

        Assertions.assertThat(globalList).isEmpty();
    }
}
