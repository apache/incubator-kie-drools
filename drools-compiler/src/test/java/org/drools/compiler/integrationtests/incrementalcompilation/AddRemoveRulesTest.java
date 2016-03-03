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


import org.drools.core.common.InternalFactHandle;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.SubnetworkTuple;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.definition.rule.Rule;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.runtime.StatelessKnowledgeSession;
import org.kie.internal.utils.KieHelper;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.drools.compiler.integrationtests.incrementalcompilation.IncrementalCompilationTest.rulestoMap;
import static org.junit.Assert.*;

public class AddRemoveRulesTest extends AbstractAddRemoveRulesTest {

    public String ruleNormal1 = "rule 'rn1' "+
            "when "+
            "$c : Counter(id==1)"+
            "then "+
            "System.out.println('RN1 fired!!!'); \n"+
            " end ";

    public String ruleNormal2 = "rule 'rn2' "+
            "when "+
            "$c : Counter(id==1)"+
            "then "+
            "System.out.println('RN2 fired!!!'); \n"+
            " end ";

    public String ruleNormal3 = "rule 'rn3' "+
            "when "+
            "$c : Counter(id==1)"+
            "then "+
            "System.out.println('RN3 + fired!!!'); \n"+
            " end ";


    String rule = "rule 'test' "+
            "when "+
            "$c : Counter(id==1)"+
            "eval(Integer.parseInt(\"5\")==$c.getId()) \n"+
            "eval(Integer.parseInt(\"10\")>5) "+
            "then "+
            "System.out.println('TEST 1 fired!!!');"+
            "end ";

    public String rule2 = "rule 'test2' "+
            "when "+
            "$c : Counter(id==2)"+
            "eval(Integer.parseInt(\"10\")==$c.getId()) \n"+
            "eval(Integer.parseInt(\"20\")>10) "+
            "then "+
            "System.out.println('TEST 2 fired!!!'); \n"+
            " end ";

    public String rule3 = "rule 'test3' "+
            "when "+
            "$c : Counter(id==3)"+
            "eval(Integer.parseInt(\"15\")==$c.getId()) \n"+
            "eval(Integer.parseInt(\"30\")>20) "+
            "then "+
            "System.out.println('TEST 2 fired!!!'); \n"+
            " end ";

    public String rule4 = "rule 'test4' "+
            "when "+
            "$c : Counter(id==4)"+
            "eval(Integer.parseInt(\"20\")==$c.getId()) \n"+
            "eval(Integer.parseInt(\"40\")>30) "+
            "then "+
            "System.out.println('TEST 2 fired!!!'); \n"+
            " end ";

    public String rule5 = "rule 'test5' "+
            "when "+
            "$c : Counter(id==5)"+
            "eval(Integer.parseInt(\"25\")==$c.getId()) \n"+
            "eval(Integer.parseInt(\"50\")>40) "+
            "then "+
            "System.out.println('TEST 2 fired!!!'); \n"+
            " end ";

    public String rule6 = "rule 'test6' "+
            "when "+
            "$c : Counter(id==6)"+
            "eval(Integer.parseInt(\"30\")==$c.getId()) \n"+
            "eval(Integer.parseInt(\"60\")>50) "+
            "then "+
            "System.out.println('TEST 2 fired!!!'); \n"+
            " end ";



    private KnowledgeBase base = KnowledgeBaseFactory.newKnowledgeBase();

    public String getPrefix() {
        return "package " + PKG_NAME_TEST + " \n"+
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

    private boolean loadRule(final String rule)  {
        String prefix = getPrefix();
        prefix += rule;

        final KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        builder.add( ResourceFactory.newReaderResource( new StringReader( prefix ) ), ResourceType.DRL);
        final Collection<KnowledgePackage> pkgs = this.buildKnowledge(builder);
        this.addKnowledgeToBase(pkgs);

        return true;
    }

    public boolean addRuleToEngine(final String rule)  {
        this.loadRule(rule);
        return true;
    }

    public boolean deleteRule(final String name) {
        this.base.removeRule(PKG_NAME_TEST, name);
        return true;
    }

    private Collection<KnowledgePackage> buildKnowledge(final KnowledgeBuilder builder)  {
        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }
        return builder.getKnowledgePackages();
    }

    private void addKnowledgeToBase(final Collection<KnowledgePackage> pkgs) {
        this.base.addKnowledgePackages( pkgs );
    }

    @Test
    public void test() throws Exception {
        final KieSession knowledgeSession = base.newKieSession();
        knowledgeSession.fireAllRules();

        addRuleToEngine(ruleNormal1);
        addRuleToEngine(ruleNormal2);
        addRuleToEngine(ruleNormal3);

        addRuleToEngine(rule);
        addRuleToEngine(rule2);
        addRuleToEngine(rule3);
        addRuleToEngine(rule4);
        addRuleToEngine(rule5);
        addRuleToEngine(rule6);

        assertTrue(getRulesCount(base) == 9);

        System.out.println("Primary remove");
        deleteRule("test6");

        assertTrue(getRulesCount(base) == 8);

        addRuleToEngine(rule6);

        assertTrue(getRulesCount(base) == 9);

        System.out.println("Secondary remove");
        deleteRule("test6");

        assertTrue(getRulesCount(base) == 8);
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


        final KnowledgeBuilder kbuilder = createKnowledgeBuilder(null, drl);
        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

        // Create kSession and initialize it
        final StatefulKnowledgeSession kSession = kbase.newStatefulKnowledgeSession();
        kSession.fireAllRules();

        kSession.getKieBase().addKnowledgePackages( kbuilder.getKnowledgePackages() );

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

        final KnowledgeBuilder kbuilder = createKnowledgeBuilder(null, drl);
        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        // Create kSession and initialize it
        final StatefulKnowledgeSession kSession = kbase.newStatefulKnowledgeSession();
        final FactHandle fh = kSession.insert(new Float( 0.0f ) );
        kSession.fireAllRules();

        kSession.getKieBase().addKnowledgePackages( kbuilder.getKnowledgePackages() );
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

        final KnowledgeBuilder kbuilder = createKnowledgeBuilder(null, drl);
        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        // Create kSession and initialize it
        final StatefulKnowledgeSession kSession = kbase.newStatefulKnowledgeSession();
        kSession.fireAllRules();

        kSession.getKieBase().removeRule( "org.drools.test", "Two" );
        kSession.fireAllRules();
    }

    private String simpleRuleInTestPackage = "package org.drools.test; \n" +
            "global java.util.List list; \n" +
            "rule \"Later\" " +
            "when " +
            "   $s : String( ) " +
            "then " +
            "   System.out.println( \"ok\" ); " +
            "   list.add( \"ok\" ); \n" +
            "end ";

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
        final StatefulKnowledgeSession knowledgeSession = buildSessionInSteps(drl, simpleRuleInTestPackage);
        final List list = new ArrayList();
        knowledgeSession.setGlobal("list", list);

        knowledgeSession.insert("go");
        knowledgeSession.fireAllRules();
        assertEquals(Arrays.asList("ok"), list);
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
        final KnowledgeBuilder kbuilder = createKnowledgeBuilder(null, rule);
        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        kbase.removeKnowledgePackage(packageName);
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
        final StatefulKnowledgeSession session = buildSessionInSteps( rule1, rule2 );
        session.getKieBase().removeKnowledgePackage(packageName);
        session.fireAllRules();
        final Map<String, Object> fact = new HashMap<String, Object>();
        fact.put("name", "Michael");
        session.insert(fact);
        session.fireAllRules();
    }

    @Test @Ignore("DROOLS-1031")
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
        final StatefulKnowledgeSession session = buildSessionInSteps( rule1, rule2);
        session.fireAllRules();
        final Map<String, Object> fact = new HashMap<String, Object>();
        fact.put("name", "Michael");
        fact.put("test", 1);
        session.insert(fact);

        session.getKieBase().removeRule(packageName, "parentRule");
        assertTrue(session.fireAllRules() == 0);
    }

    @Test @Ignore("DROOLS-1031")
    public void testRuleWithExtendsModifyParent() {
        final String packageName = "test_same_condition_pk" ;
        final String rule1 = "package " + packageName + ";" +
                "import java.util.Map; \n" +
                "rule \"parentRule\" \n" +
                "when \n" +
                " Map(this['name'] == 'Michael') \n" +
                "then \n" +
                "System.out.println('Parent rule!'); \n"+
                "end";

        final String rule1modified = "package " + packageName + ";" +
                "import java.util.Map; \n" +
                "rule \"parentRule\" \n" +
                "when \n" +
                " Map(this['name'] == 'Jerry') \n" +
                "then \n" +
                "System.out.println('Parent rule modified!'); \n"+
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
        final StatefulKnowledgeSession session = buildSessionInSteps( rule1, rule2);
        session.fireAllRules();

        final KnowledgeBuilder kbuilder2 = createKnowledgeBuilder(session.getKieBase(), rule1modified);
        session.getKieBase().addKnowledgePackages(kbuilder2.getKnowledgePackages());

        final Map<String, Object> fact2 = new HashMap<String, Object>();
        fact2.put("name", "Michael");
        fact2.put("test", 1);
        session.insert(fact2);

        assertTrue(session.fireAllRules() == 0);
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

        final KnowledgeBuilder kbuilder = createKnowledgeBuilder(null, rule1);
        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        kbase.removeKnowledgePackage(packageName);
        final StatelessKnowledgeSession session = kbase.newStatelessKnowledgeSession();
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
                "System.out.println('test rule 1'); \n"+
                "end";

        final String rule2 = "package " + packageName2 + ";" +
                "import java.util.Map; \n" +
                "rule 'rule2' \n" +
                "when \n" +
                " Map(this['type'] == 'Goods' ) \n" +
                " Map(this['x'] == 'y'  ) \n" +
                " Map(this['type'] == 'Juice'  ) \n" +
                "then \n" +
                "System.out.println('test  rule 2'); \n"+
                "end";

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("type", "Goods");
        map.put("kind", "Stuff");
        map.put("x", "y");

        final KieBase kbase = new KieHelper()
                .addContent(rule1, ResourceType.DRL)
                .addContent(rule2, ResourceType.DRL)
                .build();

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

        final StatelessKnowledgeSession statelessSession = base.newStatelessKnowledgeSession();

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

        final Map<String, Object> fact = new HashMap<String, Object>();
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
        final String rule2Name = rule1Name;

        final String rule1 = "package " + packageName + ";" +
                "rule " + rule1Name + " \n" +
                "when \n" +
                " String( ) \n" +
                "then \n" +
                " System.out.println('test in rule1'); \n"+
                "end";

        final String rule2 = "package " + packageName2 + ";" +
                "rule " + rule2Name + " \n" +
                "when \n" +
                " Long( ) \n" +
                "then \n" +
                " System.out.println('test in rule2'); \n"+
                "end";

        final StatefulKnowledgeSession session = buildSessionInSteps( rule1, rule2 );
        session.getKieBase().removeKnowledgePackage(packageName);
        session.getKieBase().removeKnowledgePackage(packageName2);
        session.insert(new String());
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


        final StatefulKnowledgeSession session = buildSessionInSteps( rule1 );

        session.setGlobal( "globalInt", new AtomicInteger(0) );
        session.insert( 1 );
        session.insert( "1" );

        session.fireAllRules();
        session.getKieBase().removeKnowledgePackage(packageName);
    }

    @Test
    public void testRemoveExistsPopulatedByInitialFact() {
        final String rule1 = "package " + PKG_NAME_TEST + ";" +
                "global java.util.List list\n" +
                "rule R1 when\n" +
                "    exists( Integer() and Integer() )\n" +
                "then\n" +
                " list.add('R1'); \n" +
                "end\n";

        final String rule2 = "package " + PKG_NAME_TEST + ";" +
                "global java.util.List list\n" +
                "rule R2 \n" +
                "when \n" +
                "    exists( Integer() and Integer() )\n" +
                "    String()" +
                "then \n" +
                " list.add('R2'); \n" +
                "end";

        final List<TestOperation> testPlan =
                AddRemoveTestBuilder.createInsertFactsRemoveFireTestPlan(
                        rule1, rule2, AddRemoveTestBuilder.getDefaultFacts());
        runAddRemoveTest(testPlan, null);

    }

    @Test
    public void testAddSplitInSubnetwork() {
        final String rule1 = "package " + PKG_NAME_TEST + ";" +
                "global java.util.List list\n" +
                "rule R1 when\n" +
                "    exists( Integer() and Integer() )\n" +
                "then\n" +
                " list.add('R1'); \n" +
                "end\n";

        final String rule2 = "package " + PKG_NAME_TEST + ";" +
                "global java.util.List list\n" +
                "rule R2 \n" +
                "when \n" +
                "    exists( Integer() and String() )\n" +
                "    String()" +
                "then \n" +
                " list.add('R2'); \n" +
                "end";

        final List<TestOperation> testPlan =
                AddRemoveTestBuilder.createInsertFactsRemoveFireTestPlan(
                        rule1, rule2, AddRemoveTestBuilder.getDefaultFacts());
        runAddRemoveTest(testPlan, null);
    }

    @Test
    public void testRemoveWithSplitStartAtLianAndFollowedBySubNetworkWithSharing() {
        final String rule1 = "package " + PKG_NAME_TEST + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + RULE1_NAME + " \n" +
                "when\n" +
                "    $s : String()\n" +
                "    Integer()\n" +
                "    exists Integer() from globalInt.get()\n" +
                "then\n" +
                " list.add('" + RULE1_NAME + "'); \n" +
                "end\n";

        final String rule2 = "package " + PKG_NAME_TEST + ";" +
                "global java.util.List list\n" +
                "rule " + RULE2_NAME + " \n" +
                "when \n" +
                "    $s1 : String()\n" +
                "    $s2 : String()\n" +
                "then \n" +
                " list.add('" + RULE2_NAME + "'); \n" +
                "end";

        testRemoveWithSplitStartBasicTestSet(rule1, rule2, RULE1_NAME, RULE2_NAME);
    }

    @Test
    public void testRemoveWithSplitStartBeforeJoinAndFollowedBySubNetworkWithSharing() {
        //  moved the split start to after the Integer
        final String rule1 = "package " + PKG_NAME_TEST + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + RULE1_NAME + " \n" +
                "when\n" +
                "    $s : String()\n" +
                "    Integer()\n" +
                "    exists Integer() from globalInt.get()\n" +
                "then\n" +
                " list.add('" + RULE1_NAME + "'); \n" +
                "end\n";

        final String rule2 = "package " + PKG_NAME_TEST + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + RULE2_NAME + " \n" +
                "when \n" +
                "    $s : String()\n" +
                "    exists Integer() from globalInt.get()\n" +
                "    Integer()\n" +
                "    String()\n" +
                "then \n" +
                " list.add('" + RULE2_NAME + "'); \n" +
                "end";

        testRemoveWithSplitStartBasicTestSet(rule1, rule2, RULE1_NAME, RULE2_NAME);
    }

    @Test
    public void testRemoveWithSplitStartAtJoinAndFollowedBySubNetworkWithSharing() {
        //  moved the split start to after the Integer
        final String rule1 = "package " + PKG_NAME_TEST + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + RULE1_NAME + " \n" +
                "when\n" +
                "    $s : String()\n" +
                "    Integer()\n" +
                "    exists Integer() from globalInt.get()\n" +
                "then\n" +
                " list.add('" + RULE1_NAME + "'); \n" +
                "end\n";

        final String rule2 = "package " + PKG_NAME_TEST + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + RULE2_NAME + " \n" +
                "when \n" +
                "    $s : String()\n" +
                "    Integer()\n" +
                "    String()\n" +
                "then \n" +
                " list.add('" + RULE2_NAME + "'); \n" +
                "end";

        testRemoveWithSplitStartBasicTestSet(rule1, rule2, RULE1_NAME, RULE2_NAME);
    }

    @Test
    public void testRemoveWithSplitStartSameRules() {
        final String rule1 = "package " + PKG_NAME_TEST + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + RULE1_NAME + " \n" +
                "when\n" +
                "    $s : String()\n" +
                "    Integer()\n" +
                "    exists( Integer() and Integer() )\n" +
                "then\n" +
                " list.add('" + RULE1_NAME + "'); \n" +
                "end\n";

        final String rule2 = "package " + PKG_NAME_TEST + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + RULE2_NAME + " \n" +
                "when \n" +
                "    $s : String()\n" +
                "    Integer()\n" +
                "    exists( Integer() and Integer() )\n" +
                "then \n" +
                " list.add('" + RULE2_NAME + "'); \n" +
                "end";

        testRemoveWithSplitStartBasicTestSet(rule1, rule2, RULE1_NAME, RULE2_NAME);
    }

    @Test
    public void testRemoveWithSplitStartDoubledExistsConstraint() {
        final String rule1 = "package " + PKG_NAME_TEST + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + RULE1_NAME + " \n" +
                "when\n" +
                "    $s : String()\n" +
                "    Integer()\n" +
                "    exists( Integer() and Integer() )\n" +
                "    exists( Integer() and Integer() )\n" +
                "then\n" +
                " list.add('" + RULE1_NAME + "'); \n" +
                "end\n";

        final String rule2 = "package " + PKG_NAME_TEST + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + RULE2_NAME + " \n" +
                "when \n" +
                "    $s : String()\n" +
                "    Integer()\n" +
                "    exists( Integer() and Integer() )\n" +
                "then \n" +
                " list.add('" + RULE2_NAME + "'); \n" +
                "end";

        testRemoveWithSplitStartBasicTestSet(rule1, rule2, RULE1_NAME, RULE2_NAME);
    }

    @Test
    public void testRemoveWithSplitStartDoubledIntegerConstraint() {
        final String rule1 = "package " + PKG_NAME_TEST + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + RULE1_NAME + " \n" +
                "when\n" +
                "    $s : String()\n" +
                "    Integer()\n" +
                "    exists( Integer() and Integer() )\n" +
                "then\n" +
                " list.add('" + RULE1_NAME + "'); \n" +
                "end\n";

        final String rule2 = "package " + PKG_NAME_TEST + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + RULE2_NAME + " \n" +
                "when \n" +
                "    $s : String()\n" +
                "    Integer()\n" +
                "    Integer()\n" +
                "    exists( Integer() and Integer() )\n" +
                "then \n" +
                " list.add('" + RULE2_NAME + "'); \n" +
                "end";

        testRemoveWithSplitStartBasicTestSet(rule1, rule2, RULE1_NAME, RULE2_NAME);
    }

    @Test
    public void testRemoveWithSplitStartAfterSubnetwork() {
        final String rule1 = "package " + PKG_NAME_TEST + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + RULE1_NAME + " \n" +
                "when\n" +
                "    $s : String()\n" +
                "    Integer()\n" +
                "    exists( Integer() and Integer() )\n" +
                "then\n" +
                " list.add('" + RULE1_NAME + "'); \n" +
                "end\n";

        final String rule2 = "package " + PKG_NAME_TEST + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + RULE2_NAME + " \n" +
                "when \n" +
                "    $s : String()\n" +
                "    Integer()\n" +
                "    exists( Integer() and Integer() )\n" +
                "    String()\n" +
                "then \n" +
                " list.add('" + RULE2_NAME + "'); \n" +
                "end";

        testRemoveWithSplitStartBasicTestSet(rule1, rule2, RULE1_NAME, RULE2_NAME);
    }

    @Test
    public void testRemoveWithSplitStartAfterSubnetwork3Rules() {
        final String rule1 = "package " + PKG_NAME_TEST + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + RULE1_NAME + " \n" +
                "when\n" +
                "    $s : String()\n" +
                "    Integer()\n" +
                "    exists( Integer() and Integer() )\n" +
                "then\n" +
                " list.add('" + RULE1_NAME + "'); \n" +
                "end\n";

        final String rule2 = "package " + PKG_NAME_TEST + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + RULE2_NAME + " \n" +
                "when \n" +
                "    $s : String()\n" +
                "    Integer()\n" +
                "    exists( Integer() and Integer() )\n" +
                "    String()\n" +
                "then \n" +
                " list.add('" + RULE2_NAME + "'); \n" +
                "end";

        final String rule3 = "package " + PKG_NAME_TEST + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + RULE3_NAME + " \n" +
                "when \n" +
                "    $s : String()\n" +
                "    Integer()\n" +
                "    exists( Integer() and exists(Integer() and Integer()))\n" +
                "    String()\n" +
                "then \n" +
                " list.add('" + RULE3_NAME + "'); \n" +
                "end";

        AddRemoveTestBuilder builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rule1, rule2, rule3})
                .addOperation(TestOperationType.INSERT_FACTS, new Object[] {1, 2, "1"})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{RULE1_NAME, RULE2_NAME, RULE3_NAME})
                .addOperation(TestOperationType.REMOVE_RULES, new String[]{RULE1_NAME, RULE2_NAME, RULE3_NAME})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{});

        final Map<String, Object> additionalGlobals = new HashMap<String, Object>();
        additionalGlobals.put("globalInt", new AtomicInteger(0));

        runAddRemoveTest(builder.build(), additionalGlobals);
    }

    @Test
    public void testRemoveWithSplitStartAfterSubnetwork3RulesAddOneAfterAnother() {
        final String rule1 = "package " + PKG_NAME_TEST + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + RULE1_NAME + " \n" +
                "when\n" +
                "    $s : String()\n" +
                "    Integer()\n" +
                "    exists( Integer() and Integer() )\n" +
                "then\n" +
                " list.add('" + RULE1_NAME + "'); \n" +
                "end\n";

        final String rule2 = "package " + PKG_NAME_TEST + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + RULE2_NAME + " \n" +
                "when \n" +
                "    $s : String()\n" +
                "    Integer()\n" +
                "    exists( Integer() and Integer() )\n" +
                "    String()\n" +
                "then \n" +
                " list.add('" + RULE2_NAME + "'); \n" +
                "end";

        final String rule3 = "package " + PKG_NAME_TEST + ";" +
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "global java.util.List list\n" +
                "rule " + RULE3_NAME + " \n" +
                "when \n" +
                "    $s : String()\n" +
                "    Integer()\n" +
                "    exists( Integer() and exists(Integer() and Integer()))\n" +
                "    String()\n" +
                "then \n" +
                " list.add('" + RULE3_NAME + "'); \n" +
                "end";

        AddRemoveTestBuilder builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rule1})
                .addOperation(TestOperationType.INSERT_FACTS, new Object[] {1, 1, "1"})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{RULE1_NAME})
                .addOperation(TestOperationType.ADD_RULES, new String[]{rule2})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{RULE2_NAME})
                .addOperation(TestOperationType.ADD_RULES, new String[]{rule3})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{RULE3_NAME})
                .addOperation(TestOperationType.REMOVE_RULES, new String[]{RULE1_NAME, RULE2_NAME, RULE3_NAME})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{});

        final Map<String, Object> additionalGlobals = new HashMap<String, Object>();
        additionalGlobals.put("globalInt", new AtomicInteger(0));

        runAddRemoveTest(builder.build(), additionalGlobals);
    }

    @Test
    public void testRemoveWithSplitStartAfterSubnetwork3RulesReaddRule() {
        final String rule1 = "package " + PKG_NAME_TEST + ";" +
                             "global java.util.List list\n" +
                             "rule " + RULE1_NAME + " \n" +
                             "when\n" +
                             "    $s : String()\n" +
                             "    Integer()\n" +
                             "    exists( Integer() and Integer() )\n" +
                             "then\n" +
                             " list.add('" + RULE1_NAME + "'); \n" +
                             "end\n";

        final String rule2 = "package " + PKG_NAME_TEST + ";" +
                             "global java.util.List list\n" +
                             "rule " + RULE2_NAME + " \n" +
                             "when \n" +
                             "    $s : String()\n" +
                             "    Integer()\n" +
                             "    exists( Integer() and Integer() )\n" +
                             "    String()\n" +
                             "then \n" +
                             " list.add('" + RULE2_NAME + "'); \n" +
                             "end";

        final String rule3 = "package " + PKG_NAME_TEST + ";" +
                             "global java.util.List list\n" +
                             "rule " + RULE3_NAME + " \n" +
                             "when \n" +
                             "    $s : String()\n" +
                             "    Integer()\n" +
                             "    exists( Integer() and exists(Integer() and Integer()))\n" +
                             "    String()\n" +
                             "then \n" +
                             " list.add('" + RULE3_NAME + "'); \n" +
                             "end";

        AddRemoveTestBuilder builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rule1, rule2, rule3})
               .addOperation(TestOperationType.INSERT_FACTS, new Object[] {1, 2, "1"})
               .addOperation(TestOperationType.REMOVE_RULES, new String[]{RULE2_NAME})
               .addOperation(TestOperationType.FIRE_RULES)
               .addOperation(TestOperationType.CHECK_RESULTS, new String[]{RULE1_NAME, RULE3_NAME})
               .addOperation(TestOperationType.ADD_RULES, new String[]{rule2})
               .addOperation(TestOperationType.FIRE_RULES)
               .addOperation(TestOperationType.CHECK_RESULTS, new String[]{RULE2_NAME});

        runAddRemoveTest(builder.build(), new HashMap<String, Object>());
    }

    String[] getRules1Pattern() {
        final String rule1 = "package " + PKG_NAME_TEST + ";" +
                             "global java.util.List list\n" +
                             "rule " + RULE1_NAME + " \n" +
                             "when\n" +
                             "    $i : Integer(this>1)\n" +
                             "then\n" +
                             " list.add('" + RULE1_NAME + "'); \n" +
                             "end\n";

        final String rule2 = "package " + PKG_NAME_TEST + ";" +
                             "global java.util.List list\n" +
                             "rule " + RULE2_NAME + " \n" +
                             "when \n" +
                             "    $i : Integer(this>2)\n" +
                             "then \n" +
                             " list.add('" + RULE2_NAME + "'); \n" +
                             "end";

        return new String[] { rule1, rule2 };
    }

    String[] getRules2Pattern() {
        final String rule1 = "package " + PKG_NAME_TEST + ";" +
                             "global java.util.List list\n" +
                             "rule " + RULE1_NAME + " \n" +
                             "when\n" +
                             "    $i1 : Integer(this>1)\n" +
                             "    $i2 : Integer(this>1)\n" +
                             "then\n" +
                             " list.add('" + RULE1_NAME + "'); \n" +
                             "end\n";

        final String rule2 = "package " + PKG_NAME_TEST + ";" +
                             "global java.util.List list\n" +
                             "rule " + RULE2_NAME + " \n" +
                             "when \n" +
                             "    $i1 : Integer(this>1)\n" +
                             "    $i2 : Integer(this>2)\n" +
                             "then \n" +
                             " list.add('" + RULE2_NAME + "'); \n" +
                             "end";

        return new String[] { rule1, rule2 };
    }

    @Test
    public void testRemoveRuleChangeFHFirstLeftTuple() {
        String[] rules = getRules1Pattern();

        AddRemoveTestBuilder builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rules[0], rules[1]})
               .addOperation(TestOperationType.INSERT_FACTS, new Object[] {3})
               .addOperation(TestOperationType.FIRE_RULES)
               .addOperation(TestOperationType.CHECK_RESULTS, new String[]{RULE1_NAME, RULE2_NAME})
               .addOperation(TestOperationType.REMOVE_RULES, new String[]{RULE1_NAME})
               .addOperation(TestOperationType.FIRE_RULES);

        final Map<String, Object> additionalGlobals = new HashMap<String, Object>();

        StatefulKnowledgeSession ksession = runAddRemoveTest(builder.build(), additionalGlobals);
        InternalFactHandle  fh1 = (InternalFactHandle) ksession.getFactHandle(3);
        assertNotNull( fh1.getFirstLeftTuple() );
    }

    @Test
    public void testRemoveRuleChangeFHLastLeftTuple() {
        String[] rules = getRules1Pattern();

        AddRemoveTestBuilder builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rules[0], rules[1]})
               .addOperation(TestOperationType.INSERT_FACTS, new Object[] {3})
               .addOperation(TestOperationType.FIRE_RULES)
               .addOperation(TestOperationType.CHECK_RESULTS, new String[]{RULE1_NAME, RULE2_NAME})
               .addOperation(TestOperationType.REMOVE_RULES, new String[]{RULE2_NAME})
               .addOperation(TestOperationType.FIRE_RULES);

        final Map<String, Object> additionalGlobals = new HashMap<String, Object>();

        StatefulKnowledgeSession ksession = runAddRemoveTest(builder.build(), additionalGlobals);
        InternalFactHandle  fh1 = (InternalFactHandle) ksession.getFactHandle(3);
        assertNotNull( fh1.getLastLeftTuple() );
    }

    @Test
    public void testRemoveRightTupleThatWasFirst() {
        String[] rules = getRules2Pattern();

        AddRemoveTestBuilder builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rules[0], rules[1]})
               .addOperation(TestOperationType.INSERT_FACTS, new Object[] {3})
               .addOperation(TestOperationType.FIRE_RULES)
               .addOperation(TestOperationType.CHECK_RESULTS, new String[]{RULE1_NAME, RULE2_NAME})
               .addOperation(TestOperationType.REMOVE_RULES, new String[]{RULE1_NAME})
               .addOperation(TestOperationType.FIRE_RULES);

        final Map<String, Object> additionalGlobals = new HashMap<String, Object>();

        StatefulKnowledgeSession ksession = runAddRemoveTest(builder.build(), additionalGlobals);
        Map<String, Rule>        rulesMap = rulestoMap(ksession.getKieBase());

        InternalFactHandle  fh1 = (InternalFactHandle) ksession.getFactHandle(3);
        assertNotNull( fh1.getFirstRightTuple() );
        assertSame( fh1.getFirstRightTuple() , fh1.getLastRightTuple() );
        assertEquals( 1, fh1.getFirstRightTuple().getTupleSink().getAssociatedRuleSize() );
        assertTrue( fh1.getFirstRightTuple().getTupleSink().isAssociatedWith(rulesMap.get(RULE2_NAME)));
    }

    @Test
    public void testRemoveRightTupleThatWasLast() {
        String[] rules = getRules2Pattern();

        AddRemoveTestBuilder builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rules[0], rules[1]})
               .addOperation(TestOperationType.INSERT_FACTS, new Object[] {3})
               .addOperation(TestOperationType.FIRE_RULES)
               .addOperation(TestOperationType.CHECK_RESULTS, new String[]{RULE1_NAME, RULE2_NAME})
               .addOperation(TestOperationType.REMOVE_RULES, new String[]{RULE2_NAME})
               .addOperation(TestOperationType.FIRE_RULES);

        final Map<String, Object> additionalGlobals = new HashMap<String, Object>();

        StatefulKnowledgeSession ksession = runAddRemoveTest(builder.build(), additionalGlobals);
        Map<String, Rule>        rulesMap = rulestoMap(ksession.getKieBase());

        InternalFactHandle  fh1 = (InternalFactHandle) ksession.getFactHandle(3);
        assertNotNull( fh1.getFirstRightTuple() );
        assertSame( fh1.getFirstRightTuple() , fh1.getLastRightTuple() );
        assertEquals( 1, fh1.getFirstRightTuple().getTupleSink().getAssociatedRuleSize() );
        assertTrue( fh1.getFirstRightTuple().getTupleSink().isAssociatedWith(rulesMap.get(RULE1_NAME)));
    }


    String[] getRules3Pattern() {
        final String rule1 = "package " + PKG_NAME_TEST + ";" +
                             "global java.util.List list\n" +
                             "rule " + RULE1_NAME + " \n" +
                             "when\n" +
                             "    $i1 : Integer(this>1)\n" +
                             "    $i2 : Integer(this>1)\n" +
                             "    $i3 : Integer(this>0)\n" +
                             "then\n" +
                             " list.add('" + RULE1_NAME + "'); \n" +
                             "end\n";

        final String rule2 = "package " + PKG_NAME_TEST + ";" +
                             "global java.util.List list\n" +
                             "rule " + RULE2_NAME + " \n" +
                             "when \n" +
                             "    $i1 : Integer(this>1)\n" +
                             "    $i2 : Integer(this>1)\n" +
                             "    $i3 : Integer(this>1)\n" +
                             "then \n" +
                             " list.add('" + RULE2_NAME + "'); \n" +
                             "end";

        final String rule3 = "package " + PKG_NAME_TEST + ";" +
                             "global java.util.List list\n" +
                             "rule " + RULE3_NAME + " \n" +
                             "when \n" +
                             "    $i1 : Integer(this>1)\n" +
                             "    $i2 : Integer(this>1)\n" +
                             "    $i3 : Integer(this>2)\n" +
                             "then \n" +
                             " list.add('" + RULE3_NAME + "'); \n" +
                             "end";

        return new String[] { rule1, rule2, rule3 };
    }

    @Test
    public void testRemoveChildLeftTupleThatWasFirst() {
        String[] rules = getRules3Pattern();

        AddRemoveTestBuilder builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rules[0], rules[1]})
               .addOperation(TestOperationType.INSERT_FACTS, new Object[] {3})
               .addOperation(TestOperationType.FIRE_RULES)
               .addOperation(TestOperationType.CHECK_RESULTS, new String[]{RULE1_NAME, RULE2_NAME})
               .addOperation(TestOperationType.REMOVE_RULES, new String[]{RULE1_NAME})
               .addOperation(TestOperationType.FIRE_RULES);

        final Map<String, Object> additionalGlobals = new HashMap<String, Object>();

        StatefulKnowledgeSession ksession = runAddRemoveTest(builder.build(), additionalGlobals);
        Map<String, Rule>        rulesMap = rulestoMap(ksession.getKieBase());

        InternalFactHandle  fh1 = (InternalFactHandle) ksession.getFactHandle(3);
        LeftTuple lt = fh1.getFirstLeftTuple().getFirstChild().getFirstChild();
        assertSame(lt, fh1.getFirstLeftTuple().getFirstChild().getLastChild());
        assertNull( lt.getPeer() );
        assertEquals( 1, lt.getTupleSink().getAssociatedRuleSize() );
        assertTrue( lt.getTupleSink().isAssociatedWith(rulesMap.get(RULE2_NAME)));
    }

    @Test
    public void testRemoveChildLeftTupleThatWasLast() {
        String[] rules = getRules3Pattern();

        AddRemoveTestBuilder builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rules[0], rules[1]})
               .addOperation(TestOperationType.INSERT_FACTS, new Object[] {3})
               .addOperation(TestOperationType.FIRE_RULES)
               .addOperation(TestOperationType.CHECK_RESULTS, new String[]{RULE1_NAME, RULE2_NAME})
               .addOperation(TestOperationType.REMOVE_RULES, new String[]{RULE2_NAME})
               .addOperation(TestOperationType.FIRE_RULES);

        final Map<String, Object> additionalGlobals = new HashMap<String, Object>();

        StatefulKnowledgeSession ksession = runAddRemoveTest(builder.build(), additionalGlobals);
        Map<String, Rule>        rulesMap = rulestoMap(ksession.getKieBase());

        InternalFactHandle  fh1 = (InternalFactHandle) ksession.getFactHandle(3);
        LeftTuple lt = fh1.getFirstLeftTuple().getFirstChild().getFirstChild();
        assertSame(lt, fh1.getFirstLeftTuple().getFirstChild().getLastChild());

        assertNull( lt.getPeer() );
        assertEquals( 1, lt.getTupleSink().getAssociatedRuleSize() );
        assertTrue( lt.getTupleSink().isAssociatedWith(rulesMap.get(RULE1_NAME)));
    }

    @Test
    public void testRemoveChildLeftTupleThatWasMiddle() {
        String[] rules = getRules3Pattern();

        AddRemoveTestBuilder builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rules[0], rules[1], rules[2]})
               .addOperation(TestOperationType.INSERT_FACTS, new Object[] {3})
               .addOperation(TestOperationType.FIRE_RULES)
               .addOperation(TestOperationType.CHECK_RESULTS, new String[]{RULE1_NAME, RULE2_NAME, RULE3_NAME})
               .addOperation(TestOperationType.REMOVE_RULES, new String[]{RULE2_NAME})
               .addOperation(TestOperationType.FIRE_RULES);

        final Map<String, Object> additionalGlobals = new HashMap<String, Object>();

        StatefulKnowledgeSession ksession = runAddRemoveTest(builder.build(), additionalGlobals);
        Map<String, Rule>        rulesMap = rulestoMap(ksession.getKieBase());

        InternalFactHandle  fh1 = (InternalFactHandle) ksession.getFactHandle(3);
        LeftTuple lt = fh1.getFirstLeftTuple().getFirstChild();
        assertSame(lt, fh1.getFirstLeftTuple().getLastChild());
        assertEquals( 1, lt.getTupleSink().getAssociatedRuleSize() );
        assertTrue( lt.getTupleSink().isAssociatedWith(rulesMap.get(RULE1_NAME)));

        LeftTuple peer = lt.getPeer();
        assertEquals( 1, peer.getTupleSink().getAssociatedRuleSize() );
        assertTrue( peer.getTupleSink().isAssociatedWith(rulesMap.get(RULE3_NAME)));
    }

    @Test
    public void testRemoveChildLeftTupleThatWasFirstWithMultipleData() {
        String[] rules = getRules3Pattern();

        AddRemoveTestBuilder builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rules[0], rules[1]})
               .addOperation(TestOperationType.INSERT_FACTS, new Object[] {3, 4, 5})
               .addOperation(TestOperationType.FIRE_RULES)
               .addOperation(TestOperationType.CHECK_RESULTS, new String[]{RULE1_NAME, RULE2_NAME})
               .addOperation(TestOperationType.REMOVE_RULES, new String[]{RULE1_NAME})
               .addOperation(TestOperationType.FIRE_RULES);

        final Map<String, Object> additionalGlobals = new HashMap<String, Object>();

        StatefulKnowledgeSession ksession = runAddRemoveTest(builder.build(), additionalGlobals);
        Map<String, Rule>        rulesMap = rulestoMap(ksession.getKieBase());

        InternalFactHandle  fh1 = (InternalFactHandle) ksession.getFactHandle(3);
        InternalFactHandle  fh2 = (InternalFactHandle) ksession.getFactHandle(4);
        InternalFactHandle  fh3 = (InternalFactHandle) ksession.getFactHandle(5);
        LeftTuple lt1 = fh1.getFirstLeftTuple();

        LeftTuple lt1_1 = lt1.getFirstChild();
        LeftTuple lt1_2 = lt1_1.getHandleNext();
        LeftTuple lt1_3= lt1_2.getHandleNext();
        assertNotNull( lt1_1 );
        assertNotNull( lt1_2 );
        assertNotNull( lt1_3 );
        assertSame(lt1_3, lt1.getLastChild());

        assertSame(lt1_2, lt1_3.getHandlePrevious() );
        assertSame(lt1_1, lt1_2.getHandlePrevious() );

        assertEquals( 1, lt1_1.getTupleSink().getAssociatedRuleSize() );
        assertTrue( lt1_1.getTupleSink().isAssociatedWith(rulesMap.get(RULE2_NAME)));
        assertNull( lt1_1.getPeer() );

        assertEquals( 1, lt1_2.getTupleSink().getAssociatedRuleSize() );
        assertTrue( lt1_2.getTupleSink().isAssociatedWith(rulesMap.get(RULE2_NAME)));
        assertNull( lt1_2.getPeer() );

        assertEquals( 1, lt1_3.getTupleSink().getAssociatedRuleSize() );
        assertTrue( lt1_3.getTupleSink().isAssociatedWith(rulesMap.get(RULE2_NAME)));
        assertNull( lt1_3.getPeer() );


        RightTuple rt1 = fh3.getFirstRightTuple();
        LeftTuple rt1_1 = rt1.getLastChild();
        assertSame( lt1_1, rt1_1);

        LeftTuple rt1_2 = rt1_1.getRightParentPrevious();
        LeftTuple rt1_3 = rt1_2.getRightParentPrevious();

        assertNotNull( rt1_1 );
        assertNotNull( rt1_2 );
        assertNotNull( rt1_3 );

        assertSame(rt1_2, rt1_3.getRightParentNext() );
        assertSame(rt1_1, rt1_2.getRightParentNext() );

        assertEquals( 1, rt1_1.getTupleSink().getAssociatedRuleSize() );
        assertTrue( rt1_1.getTupleSink().isAssociatedWith(rulesMap.get(RULE2_NAME)));
        assertNull( rt1_1.getPeer() );

        assertEquals( 1, rt1_2.getTupleSink().getAssociatedRuleSize() );
        assertTrue( rt1_2.getTupleSink().isAssociatedWith(rulesMap.get(RULE2_NAME)));
        assertNull( rt1_2.getPeer() );

        assertEquals( 1, rt1_3.getTupleSink().getAssociatedRuleSize() );
        assertTrue( rt1_3.getTupleSink().isAssociatedWith(rulesMap.get(RULE2_NAME)));
        assertNull( rt1_3.getPeer() );
    }

    @Test
    public void testRemoveChildLeftTupleThatWasLastWithMultipleData() {
        String[] rules = getRules3Pattern();

        AddRemoveTestBuilder builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rules[0], rules[1]})
               .addOperation(TestOperationType.INSERT_FACTS, new Object[] {3, 4, 5})
               .addOperation(TestOperationType.FIRE_RULES)
               .addOperation(TestOperationType.CHECK_RESULTS, new String[]{RULE1_NAME, RULE2_NAME})
               .addOperation(TestOperationType.REMOVE_RULES, new String[]{RULE2_NAME})
               .addOperation(TestOperationType.FIRE_RULES);

        final Map<String, Object> additionalGlobals = new HashMap<String, Object>();

        StatefulKnowledgeSession ksession = runAddRemoveTest(builder.build(), additionalGlobals);
        Map<String, Rule>        rulesMap = rulestoMap(ksession.getKieBase());

        InternalFactHandle  fh1 = (InternalFactHandle) ksession.getFactHandle(3);
        InternalFactHandle  fh2 = (InternalFactHandle) ksession.getFactHandle(4);
        InternalFactHandle  fh3 = (InternalFactHandle) ksession.getFactHandle(5);
        LeftTuple lt1 = fh1.getFirstLeftTuple();

        LeftTuple lt1_1 = lt1.getFirstChild();
        LeftTuple lt1_2 = lt1_1.getHandleNext();
        LeftTuple lt1_3= lt1_2.getHandleNext();
        assertNotNull( lt1_1 );
        assertNotNull( lt1_2 );
        assertNotNull( lt1_3 );
        assertSame(lt1_3, lt1.getLastChild());

        assertSame(lt1_2, lt1_3.getHandlePrevious() );
        assertSame(lt1_1, lt1_2.getHandlePrevious() );

        assertEquals( 1, lt1_1.getTupleSink().getAssociatedRuleSize() );
        assertTrue( lt1_1.getTupleSink().isAssociatedWith(rulesMap.get(RULE1_NAME)));
        assertNull( lt1_1.getPeer() );

        assertEquals( 1, lt1_2.getTupleSink().getAssociatedRuleSize() );
        assertTrue( lt1_2.getTupleSink().isAssociatedWith(rulesMap.get(RULE1_NAME)));
        assertNull( lt1_2.getPeer() );

        assertEquals( 1, lt1_3.getTupleSink().getAssociatedRuleSize() );
        assertTrue( lt1_3.getTupleSink().isAssociatedWith(rulesMap.get(RULE1_NAME)));
        assertNull( lt1_3.getPeer() );


        RightTuple rt1 = fh3.getFirstRightTuple();
        LeftTuple rt1_1 = rt1.getLastChild();
        assertSame( lt1_1, rt1_1);

        LeftTuple rt1_2 = rt1_1.getRightParentPrevious();
        LeftTuple rt1_3 = rt1_2.getRightParentPrevious();

        assertNotNull( rt1_1 );
        assertNotNull( rt1_2 );
        assertNotNull( rt1_3 );

        assertSame(rt1_2, rt1_3.getRightParentNext() );
        assertSame(rt1_1, rt1_2.getRightParentNext() );

        assertEquals( 1, rt1_1.getTupleSink().getAssociatedRuleSize() );
        assertTrue( rt1_1.getTupleSink().isAssociatedWith(rulesMap.get(RULE1_NAME)));
        assertNull( rt1_1.getPeer() );

        assertEquals( 1, rt1_2.getTupleSink().getAssociatedRuleSize() );
        assertTrue( rt1_2.getTupleSink().isAssociatedWith(rulesMap.get(RULE1_NAME)));
        assertNull( rt1_2.getPeer() );

        assertEquals( 1, rt1_3.getTupleSink().getAssociatedRuleSize() );
        assertTrue( rt1_3.getTupleSink().isAssociatedWith(rulesMap.get(RULE1_NAME)));
        assertNull( rt1_3.getPeer() );
    }

    private void testRemoveWithSplitStartBasicTestSet(final String rule1, final String rule2,
            final String rule1Name, final String rule2Name) {

        final Map<String, Object> additionalGlobals = new HashMap<String, Object>();
        additionalGlobals.put("globalInt", new AtomicInteger(0));

        runAddRemoveTests(rule1, rule2, rule1Name, rule2Name, new Object[] {1, 2, "1"}, additionalGlobals);
    }

    @Test
    public void testMergeRTN() {
        final String rule1 = "package " + PKG_NAME_TEST + ";" +
                             "global java.util.List list\n" +
                             "rule " + RULE1_NAME + " \n" +
                             "when\n" +
                             "    Integer()\n" +
                             "    Integer()\n" +
                             "    Integer()\n" +
                             "then\n" +
                             " list.add('" + RULE1_NAME + "'); \n" +
                             "end\n";

        final String rule2 = "package " + PKG_NAME_TEST + ";" +
                             "global java.util.List list\n" +
                             "rule " + RULE2_NAME + " \n" +
                             "when \n" +
                             "    Integer()\n" +
                             "    exists( Integer() and Integer() )\n" +
                             "    Integer()\n" +
                             "then \n" +
                             " list.add('" + RULE2_NAME + "'); \n" +
                             "end";

        AddRemoveTestBuilder builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rule1, rule2})
               .addOperation(TestOperationType.INSERT_FACTS, new Object[] {1, 2, 3})
               .addOperation(TestOperationType.REMOVE_RULES, new String[]{RULE2_NAME})
               .addOperation(TestOperationType.FIRE_RULES)
               .addOperation(TestOperationType.CHECK_RESULTS, new String[]{RULE1_NAME})
               .addOperation(TestOperationType.REMOVE_RULES, new String[]{RULE1_NAME})
               .addOperation(TestOperationType.FIRE_RULES)
               .addOperation(TestOperationType.CHECK_RESULTS, new String[]{});

        runAddRemoveTest(builder.build(), new HashMap<String, Object>());
    }

    @Test
    public void testSubSubNetwork() {
        final String rule1 = "package " + PKG_NAME_TEST + ";" +
                             "global java.util.List list\n" +
                             "rule " + RULE1_NAME + " \n" +
                             "when\n" +
                             "    Integer()\n" +
                             "    Integer()\n" +
                             "    Integer()\n" +
                             "then\n" +
                             " list.add('" + RULE1_NAME + "'); \n" +
                             "end\n";

        final String rule2 = "package " + PKG_NAME_TEST + ";" +
                             "global java.util.List list\n" +
                             "rule " + RULE2_NAME + " \n" +
                             "when \n" +
                             "    Integer()\n" +
                             "    exists( Integer() and Integer() )\n" +
                             "    exists(Integer() and exists(Integer() and Integer()))\n" +
                             "then \n" +
                             " list.add('" + RULE2_NAME + "'); \n" +
                             "end";

        AddRemoveTestBuilder builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rule1, rule2})
               .addOperation(TestOperationType.INSERT_FACTS, new Object[] {1})
               .addOperation(TestOperationType.REMOVE_RULES, new String[]{RULE2_NAME})
               .addOperation(TestOperationType.FIRE_RULES)
               .addOperation(TestOperationType.CHECK_RESULTS, new String[]{RULE1_NAME})
               .addOperation(TestOperationType.REMOVE_RULES, new String[]{RULE1_NAME})
               .addOperation(TestOperationType.FIRE_RULES)
               .addOperation(TestOperationType.CHECK_RESULTS, new String[]{})
        ;

        runAddRemoveTest(builder.build(), new HashMap<String, Object>());
    }

    @Test
    public void testSubSubNetwork2() {
        final String rule1 = "package " + PKG_NAME_TEST + ";" +
                             "global java.util.List list\n" +
                             "rule " + RULE1_NAME + " \n" +
                             "when\n" +
                             "    Integer()\n" +
                             "    Integer()\n" +
                             "then\n" +
                             " list.add('" + RULE1_NAME + "'); \n" +
                             "end\n";

        final String rule2 = "package " + PKG_NAME_TEST + ";" +
                             "global java.util.List list\n" +
                             "rule " + RULE2_NAME + " \n" +
                             "when \n" +
                             "    Integer()\n" +
                             "    exists( Integer() and Integer() )\n" +
                             "    exists(Integer() and exists(Integer() and Integer()))\n" +
                             "then \n" +
                             " list.add('" + RULE2_NAME + "'); \n" +
                             "end";

        AddRemoveTestBuilder builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rule1, rule2})
               .addOperation(TestOperationType.INSERT_FACTS, new Object[] {1})
               .addOperation(TestOperationType.REMOVE_RULES, new String[]{RULE1_NAME, RULE2_NAME})
               .addOperation(TestOperationType.ADD_RULES, new String[]{rule1})
               .addOperation(TestOperationType.FIRE_RULES)
               .addOperation(TestOperationType.CHECK_RESULTS, new String[]{RULE1_NAME})
        ;

        runAddRemoveTest(builder.build(), new HashMap<String, Object>());
    }

    @Test
    public void testSubSubNetwork3() {
        final String rule1 = "package " + PKG_NAME_TEST + ";" +
                             "global java.util.List list\n" +
                             "rule " + RULE1_NAME + " \n" +
                             "when\n" +
                             "  exists(Integer() and Integer()) \n" +
                             "  exists(Integer() and Integer()) \n" +
                             "  Integer() \n\n" +
                             "then\n" +
                             " list.add('" + RULE1_NAME + "'); \n" +
                             "end\n";

        final String rule2 = "package " + PKG_NAME_TEST + ";" +
                             "global java.util.List list\n" +
                             "rule " + RULE2_NAME + " \n" +
                             "when \n" +
                             "  exists(Integer() and Integer()) \n" +
                             "  Integer() \n" +
                             "  exists(Integer() and exists(Integer() and Integer())) \n" +
                             "then \n" +
                             " list.add('" + RULE2_NAME + "'); \n" +
                             "end";

        AddRemoveTestBuilder builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rule1, rule2})
               .addOperation(TestOperationType.INSERT_FACTS, new Object[] {1})
               .addOperation(TestOperationType.FIRE_RULES)
               .addOperation(TestOperationType.CHECK_RESULTS, new String[]{RULE1_NAME, RULE2_NAME})
               .addOperation(TestOperationType.REMOVE_RULES, new String[]{RULE1_NAME, RULE2_NAME})
               .addOperation(TestOperationType.FIRE_RULES)
               .addOperation(TestOperationType.CHECK_RESULTS, new String[]{})
        ;

        runAddRemoveTest(builder.build(), new HashMap<String, Object>());
    }

    @Test
    public void testSubSubNetwork4() {
        final String rule1 = "package " + PKG_NAME_TEST + ";" +
                             "global java.util.List list\n" +
                             "rule " + RULE1_NAME + " \n" +
                             "when\n" +
                             "  exists(Integer() and Integer()) \n" +
                             "  Integer() \n" +
                             "  Integer() \n" +
                             "then\n" +
                             " list.add('" + RULE1_NAME + "'); \n" +
                             "end\n";

        final String rule2 = "package " + PKG_NAME_TEST + ";" +
                             "global java.util.List list\n" +
                             "rule " + RULE2_NAME + " \n" +
                             "when \n" +
                             "  exists(Integer() and Integer()) \n" +
                             "  Integer() \n" +
                             "  exists(Integer() and exists(Integer() and Integer())) \n" +
                             "then \n" +
                             " list.add('" + RULE2_NAME + "'); \n" +
                             "end";

        AddRemoveTestBuilder builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rule1, rule2})
               .addOperation(TestOperationType.INSERT_FACTS, new Object[] {1})
               .addOperation(TestOperationType.REMOVE_RULES, new String[]{RULE1_NAME})
               .addOperation(TestOperationType.FIRE_RULES)
               .addOperation(TestOperationType.CHECK_RESULTS, new String[]{RULE2_NAME})
        ;

        runAddRemoveTest(builder.build(), new HashMap<String, Object>());
    }

    @Test
    public void testSubNetworkWithNot() {
        final String rule1 = "package " + PKG_NAME_TEST + ";" +
                             "global java.util.List list\n" +
                             "rule " + RULE1_NAME + " \n" +
                             "when\n" +
                             "  exists(Integer() and Integer()) \n" +
                             "  not(Double() and Double()) \n" +
                             "  Integer() \n" +
                             "then\n" +
                             " list.add('" + RULE1_NAME + "'); \n" +
                             "end\n";

        final String rule2 = "package " + PKG_NAME_TEST + ";" +
                             "global java.util.List list\n" +
                             "rule " + RULE2_NAME + " \n" +
                             "when \n" +
                             "  exists(Integer() and Integer()) \n" +
                             "  not(Double() and Double()) \n" +
                             "then \n" +
                             " list.add('" + RULE2_NAME + "'); \n" +
                             "end";

        AddRemoveTestBuilder builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rule1, rule2})
               .addOperation(TestOperationType.INSERT_FACTS, new Object[] {1})
               .addOperation(TestOperationType.REMOVE_RULES, new String[]{RULE2_NAME})
               .addOperation(TestOperationType.FIRE_RULES)
               .addOperation(TestOperationType.CHECK_RESULTS, new String[]{RULE1_NAME})
        ;

        runAddRemoveTest(builder.build(), new HashMap<String, Object>());
    }

    @Test
    public void testSubNetworkWithNot2() {
        final String rule1 = "package " + PKG_NAME_TEST + ";" +
                             "global java.util.List list\n" +
                             "rule " + RULE1_NAME + " \n" +
                             "when\n" +
                             "  exists(Integer() and Integer()) \n" +
                             "  not(Double() and Double()) \n" +
                             "  Integer() \n" +
                             "then\n" +
                             " list.add('" + RULE1_NAME + "'); \n" +
                             "end\n";

        final String rule2 = "package " + PKG_NAME_TEST + ";" +
                             "global java.util.List list\n" +
                             "rule " + RULE2_NAME + " \n" +
                             "when \n" +
                             "  exists(Integer() and Integer()) \n" +
                             "  not(Double() and Double()) \n" +
                             "  exists(Integer() and Integer()) \n" +
                             "then \n" +
                             " list.add('" + RULE2_NAME + "'); \n" +
                             "end";

        AddRemoveTestBuilder builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rule1, rule2})
               .addOperation(TestOperationType.INSERT_FACTS, new Object[] {1})
               .addOperation(TestOperationType.REMOVE_RULES, new String[]{RULE2_NAME})
               .addOperation(TestOperationType.FIRE_RULES)
               .addOperation(TestOperationType.CHECK_RESULTS, new String[]{RULE1_NAME})
        ;

        runAddRemoveTest(builder.build(), new HashMap<String, Object>());
    }

    @Test
    public void testSubNetworkWithNot3() {
        final String rule1 = "package " + PKG_NAME_TEST + ";" +
                             "global java.util.List list\n" +
                             "rule " + RULE1_NAME + " \n" +
                             "when\n" +
                             "  exists(Integer()) \n" +
                             "  not(Double() and Double()) \n" +
                             "  Integer() \n" +
                             "then\n" +
                             " list.add('" + RULE1_NAME + "'); \n" +
                             "end\n";

        final String rule2 = "package " + PKG_NAME_TEST + ";" +
                             "global java.util.List list\n" +
                             "rule " + RULE2_NAME + " \n" +
                             "when \n" +
                             "  exists(Integer()) \n" +
                             "  not(Double() and Double()) \n" +
                             "then \n" +
                             " list.add('" + RULE2_NAME + "'); \n" +
                             "end";

        AddRemoveTestBuilder builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rule1, rule2})
               .addOperation(TestOperationType.INSERT_FACTS, new Object[] {1})
               .addOperation(TestOperationType.FIRE_RULES)
               .addOperation(TestOperationType.CHECK_RESULTS, new String[]{RULE1_NAME, RULE2_NAME})
        ;

        runAddRemoveTest(builder.build(), new HashMap<String, Object>());
    }

    @Test
    public void testSubNetworkWithNot4() {
        final String rule1 = "package " + PKG_NAME_TEST + ";" +
                             "global java.util.List list\n" +
                             "rule " + RULE1_NAME + " \n" +
                             "when\n" +
                             "  exists(Integer() and Integer()) \n" +
                             "  exists(Integer() and exists(Integer() and Integer())) \n" +
                             "  Integer() \n" +
                             "  not(Double() and Double()) \n" +
                             "then\n" +
                             " list.add('" + RULE1_NAME + "'); \n" +
                             "end\n";

        final String rule2 = "package " + PKG_NAME_TEST + ";" +
                             "global java.util.List list\n" +
                             "rule " + RULE2_NAME + " \n" +
                             "when \n" +
                             "  exists(Integer() and Integer()) \n" +
                             "  Integer() \n" +
                             "  not(Double() and Double()) \n" +
                             "  exists(Integer() and Integer()) \n" +
                             "then \n" +
                             " list.add('" + RULE2_NAME + "'); \n" +
                             "end";

        AddRemoveTestBuilder builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rule1, rule2})
               .addOperation(TestOperationType.INSERT_FACTS, new Object[] {1})
               .addOperation(TestOperationType.REMOVE_RULES, new String[]{RULE1_NAME})
               .addOperation(TestOperationType.FIRE_RULES)
               .addOperation(TestOperationType.CHECK_RESULTS, new String[]{RULE2_NAME})
        ;

        runAddRemoveTest(builder.build(), new HashMap<String, Object>());
    }

    @Test
    public void testInsertFireRemoveWith2Nots() {
        final String rule1 = " package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list\n" +
                " rule " + RULE1_NAME + " \n" +
                " when \n" +
                "   Integer() \n" +
                "   Integer() \n" +
                " then\n" +
                "   list.add('" + RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = " package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list\n" +
                " rule " + RULE2_NAME + " \n" +
                " when \n" +
                "   Integer() \n" +
                "   Integer() \n" +
                "   not(not(Integer() and Integer())) \n" +
                " then\n" +
                "   list.add('" + RULE2_NAME + "'); \n" +
                " end";

        final AddRemoveTestBuilder builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rule2, rule1})
                .addOperation(TestOperationType.INSERT_FACTS, new Object[] {1})
                .addOperation(TestOperationType.REMOVE_RULES, new String[]{RULE2_NAME})
                .addOperation(TestOperationType.REMOVE_RULES, new String[]{RULE1_NAME})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{});

        runAddRemoveTest(builder.build(), new HashMap<String, Object>());

    }

    @Test
    public void testSubSubNetwork5() {
        final String rule1 = "package " + PKG_NAME_TEST + ";" +
                             "global java.util.List list\n" +
                             "rule " + RULE1_NAME + " \n" +
                             "when\n" +
                             "  Integer() \n" +
                             "  Integer() \n" +
                             "  exists(Integer() and Integer()) \n" +
                             "then\n" +
                             " list.add('" + RULE1_NAME + "'); \n" +
                             "end\n";

        final String rule2 = "package " + PKG_NAME_TEST + ";" +
                             "global java.util.List list\n" +
                             "rule " + RULE2_NAME + " \n" +
                             "when \n" +
                             "  Integer() \n" +
                             "  exists(Integer() and Integer()) \n" +
                             "then \n" +
                             " list.add('" + RULE2_NAME + "'); \n" +
                             "end";

        AddRemoveTestBuilder builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rule1, rule2})
               .addOperation(TestOperationType.INSERT_FACTS, new Object[] {1})
               .addOperation(TestOperationType.REMOVE_RULES, new String[]{RULE1_NAME})
               .addOperation(TestOperationType.FIRE_RULES)
               .addOperation(TestOperationType.CHECK_RESULTS, new String[]{RULE2_NAME})
        ;

        runAddRemoveTest(builder.build(), new HashMap<String, Object>());
    }

    @Test
    public void testInsertRemoveFireWith2Nots() {
        final String rule1 = " package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list\n" +
                " rule " + RULE1_NAME + " \n" +
                " when \n" +
                "   exists(Integer() and Integer()) \n" +
                "   Integer() \n" +
                "   not(exists(Integer() and Integer())) \n" +
                " then\n" +
                "   list.add('" + RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = " package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list\n" +
                " rule " + RULE2_NAME + " \n" +
                " when \n" +
                "   exists(Integer() and Integer()) \n" +
                "   not(exists(Integer() and Integer())) \n" +
                " then\n" +
                "   list.add('" + RULE2_NAME + "'); \n" +
                " end";

        final AddRemoveTestBuilder builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rule1, rule2});

        runAddRemoveTest(builder.build(), new HashMap<String, Object>());
    }

    @Test
    public void testSharedRian() {

        final String rule1 = " package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list\n" +
                " rule " + RULE1_NAME + " \n" +
                " when \n" +
                "   Integer() \n" +
                "   not(Integer() and Integer()) \n" +
                " then\n" +
                "   list.add('" + RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = " package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list\n" +
                " rule " + RULE2_NAME + " \n" +
                " when \n" +
                "   Integer() \n" +
                "   exists(Integer() and Integer()) \n" +
                " then\n" +
                "   list.add('" + RULE2_NAME + "'); \n" +
                " end";

        final AddRemoveTestBuilder builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rule1})
                .addOperation(TestOperationType.INSERT_FACTS, new Object[]{1})
                .addOperation(TestOperationType.ADD_RULES, new String[]{rule2})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{RULE2_NAME});

        runAddRemoveTest(builder.build(), new HashMap<String, Object>());
    }

    @Test
    public void testSharedRianWithFire() {

        final String rule1 = " package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list\n" +
                " rule " + RULE1_NAME + " \n" +
                " when \n" +
                "   Integer() \n" +
                "   not(Integer() and Integer()) \n" +
                " then\n" +
                "   list.add('" + RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = " package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list\n" +
                " rule " + RULE2_NAME + " \n" +
                " when \n" +
                "   Integer() \n" +
                "   exists(Integer() and Integer()) \n" +
                " then\n" +
                "   list.add('" + RULE2_NAME + "'); \n" +
                " end";

        final AddRemoveTestBuilder builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rule1})
                .addOperation(TestOperationType.INSERT_FACTS, new Object[]{1})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.ADD_RULES, new String[]{rule2})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{RULE2_NAME});

        runAddRemoveTest(builder.build(), new HashMap<String, Object>());
    }

    @Test
    public void testSharedRian2() {

        final String rule1 = " package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list\n" +
                " rule " + RULE1_NAME + " \n" +
                " when \n" +
                "   Integer() \n" +
                "   Integer() \n" +
                "   not(not(Integer() and Integer())) \n" +
                " then\n" +
                "   list.add('" + RULE1_NAME + "'); \n" +
                " end";

        final String rule2 = " package " + PKG_NAME_TEST + ";\n" +
                " global java.util.List list\n" +
                " rule " + RULE2_NAME + " \n" +
                " when \n" +
                "   Integer() \n" +
                "   exists(Integer() and exists(Integer() and Integer())) \n" +
                " then\n" +
                "   list.add('" + RULE2_NAME + "'); \n" +
                " end";

        final AddRemoveTestBuilder builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rule1})
               .addOperation(TestOperationType.INSERT_FACTS, new Object[]{1})
               .addOperation(TestOperationType.FIRE_RULES)
               .addOperation(TestOperationType.CHECK_RESULTS, new String[]{RULE1_NAME})
               .addOperation(TestOperationType.ADD_RULES, new String[]{rule2})
               .addOperation(TestOperationType.FIRE_RULES)
               .addOperation(TestOperationType.CHECK_RESULTS, new String[]{RULE2_NAME});

        runAddRemoveTest(builder.build(), new HashMap<String, Object>());
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

        KieSession session = base.newKieSession();

        this.addRuleToEngine(rule1);
        InternalFactHandle fh = (InternalFactHandle)session.insert( 1 );
        session.fireAllRules();

        this.addRuleToEngine(rule2);

        SubnetworkTuple tuple = (SubnetworkTuple)fh.getFirstLeftTuple().getFirstChild().getFirstChild();
        assertNotNull( tuple.getPeer() );

        this.deleteRule(rule2Name);
        assertNull( tuple.getPeer() );
    }
}
