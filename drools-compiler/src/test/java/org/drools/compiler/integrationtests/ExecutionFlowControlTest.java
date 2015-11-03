/*
 * Copyright 2015 JBoss Inc
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

import static org.junit.Assert.fail;

import org.drools.compiler.Cell;
import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.FactA;
import org.drools.compiler.Father;
import org.drools.compiler.Foo;
import org.drools.compiler.Message;
import org.drools.compiler.Neighbor;
import org.drools.compiler.Person;
import org.drools.compiler.PersonInterface;
import org.drools.compiler.Pet;
import org.drools.compiler.TotalHolder;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.spi.AgendaGroup;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.RuleEngineOption;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import com.answers.kie.layoutmode.LayoutModeInput;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ExecutionFlowControlTest extends CommonTestMethodBase {
  public static class TrackingAgendaEventListener extends
      DefaultAgendaEventListener {
    private List<String> rulesFiredList = new ArrayList<String>();
    
    @Override
    public void beforeMatchFired(BeforeMatchFiredEvent event) {}
    
    @Override
    public void afterMatchFired(AfterMatchFiredEvent event) {
      Rule rule = event.getMatch().getRule();
      rulesFiredList.add(rule.getName());
    }
    
    /**
     * @return the rulesFiredList
     */
    public List<String> getRulesFiredList() {
      return rulesFiredList;
    }
    
    public void reset() {
      rulesFiredList.clear();
    }
  }
  @Test
  public void testActivationGroupIssue() throws Exception {
//    KieBase kbase = loadKnowledgeBase(ResourceType.DRL, null, null,
//        "test_ActivationGroupIssue.drl");
//        String [] rules={"test_ActivationGroupIssue.drl","test_salienceIntegerRule.drl"};
//        KieBase kbase = loadKnowledgeBase(rules);
//        KieSession ksession = kbase.newKieSession();
    
    String prefix="package com.answers.kie.layoutmode\n"
        //"package org.drools.compiler.test\n"
        +"import com.answers.kie.layoutmode.LayoutModeInput;\n"
        +"import com.answers.kie.layoutmode.VisitorClass;\n"
        +"global java.util.List list;\n";
    String rule1 = prefix
        +"rule \"Global_VisitorClassification_type_crawler\"\n"
        +"        ruleflow-group \"ruleflow-group-visitor-classification\"\n"
        +"        activation-group \"activation-group-visitor-classification-root\"\n"
        +"        salience 2\n"
        +"        dialect \"mvel\"\n"
        +"        when\n"
        +"                LayoutModeInput( user_agent matches \".*Googlebot.*\" )\n"
        +"                not (VisitorClass( classLevel == \"type_crawler\" )) \n"
        +"        then\n"
        +"                list.add( \"Global_VisitorClassification_type_crawler\" );\n"
        +"                VisitorClass fact0 = new VisitorClass();\n"
        +"                fact0.setClassLevel( \"type_crawler\" );\n"
        +"                insert( fact0 );\n"
        +"end\n";
    
    String rule2 = prefix
        +"rule \"LMI_default_null\"\n"
        +"        salience -1\n"
        +"        activation-group \"visitor_classification\"\n"
        +"        ruleflow-group \"ruleflow-group-visitor-classification\"\n"
        +"        dialect \"mvel\"\n"
        +"        when\n"
        +"                exists (LayoutModeInput( ))\n" 
        +"        then\n"
        +"                list.add( \"LMI_default_null\" );\n"        
        +"end\n";
    
    String rule3 = prefix
        +"rule \"VisitorClassification_lowperf_10004\"\n"
        +"        salience 10004\n"
        +"        activation-group \"visitor_classification\"\n"
        +"        ruleflow-group \"ruleflow-group-visitor-classification\"\n"
        +"        dialect \"mvel\"\n"
        +"        when\n"
        +"                LayoutModeInput( city == \"mountain view\" )\n"
        +"        then\n"
        +"                list.add( \"VisitorClassification_lowperf_10004\" );\n"
        +"                VisitorClass fact0 = new VisitorClass();\n"
        +"                fact0.setClassLevel( \"low_perf\" );\n"
        +"                insert( fact0 );\n"
        +"end\n";
    
    String rule4 = prefix
        +"rule \"VisitorClassification_type_4_7000\"\n"
        +"        salience 7000\n"
        +"        activation-group \"visitor_classification\"\n"
        +"        ruleflow-group \"ruleflow-group-visitor-classification\"\n"
        +"        dialect \"mvel\"\n"
        +"        when\n"
        +"                lmi : LayoutModeInput( user_agent matches \".*Safari.*\" )\n"
        +"                LayoutModeInput(source == null)\n"
        +"        then\n"
        +"                list.add( \"VisitorClassification_type_4_7000\" );\n"
        +"                VisitorClass fact0 = new VisitorClass();\n"
        +"                fact0.setClassLevel( \"type_4\" );\n"
        +"                insert( fact0 );\n"
        +"end\n";
    
    String [] drlContentStrings={rule1,rule2,rule3,rule4};
    System.out.println(Arrays.toString(drlContentStrings));
    
    KieBaseConfiguration kBaseConfig=KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
    KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
    for (String drlContentString : drlContentStrings) {
      Resource resource = ResourceFactory.newByteArrayResource(drlContentString
          .getBytes());
      kbuilder.add(resource, ResourceType.RDRL);
    }

    if (kbuilder.hasErrors()) {
      fail(kbuilder.getErrors().toString());
    }
    if (kBaseConfig == null) {
      kBaseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
    }
    kBaseConfig.setOption(phreak);
    KnowledgeBase kbase = kBaseConfig == null ? KnowledgeBaseFactory.newKnowledgeBase() : KnowledgeBaseFactory.newKnowledgeBase(kBaseConfig);
    System.out.println("getKnowledgePackages "+kbuilder.getKnowledgePackages());
    kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
    
    //KieBase kbase = loadKnowledgeBaseFromString(rule1);
    KieSession ksession = kbase.newKieSession();
    final List list = new ArrayList();
    ksession.setGlobal("list", list);
    TrackingAgendaEventListener listener=new TrackingAgendaEventListener();
    ksession.addEventListener(listener);
    
        
        final LayoutModeInput input = new LayoutModeInput();
        input.setCity("mountain view");
        final String ua = "Mozilla/5.0 (iPhone; CPU iPhone OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A5376e Safari/8536.25 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)";
        input.setUser_agent(ua);
        
        int loop = 10;
        
        for (int i = 0; i < loop; i++) {
          ksession.insert(input);
          ((InternalAgenda)ksession.getAgenda()).activateRuleFlowGroup("ruleflow-group-visitor-classification");
          int fired=ksession.fireAllRules();
          System.out.println("i="+i);
          System.out.println("fired rules "+fired);
          System.out.println(listener.getRulesFiredList());
          System.out.println(list.size());
          System.out.println(list);
          for (Object obj : ksession.getObjects()) {
            ksession.delete(ksession.getFactHandle(obj));
          }
          listener.reset();
          list.clear();
        }
//      assertEquals( "Three rules should have been fired", 3, list.size() );
//      assertEquals( "Rule 4 should have been fired first", "Rule 4",
//                    list.get( 0 ) );
//      assertEquals( "Rule 2 should have been fired second", "Rule 2",
//                    list.get( 1 ) );
//      assertEquals( "Rule 3 should have been fired third", "Rule 3",
//                    list.get( 2 ) );
  }
    @Test(timeout = 10000)
    public void testSalienceIntegerAndLoadOrder() throws Exception {
        KieBase kbase = loadKnowledgeBase("test_salienceIntegerRule.drl");
        KieSession ksession = kbase.newKieSession();
        final List list = new ArrayList();
        ksession.setGlobal( "list", list );

        final PersonInterface person = new Person( "Edson", "cheese" );
        ksession.insert( person );

        ksession.fireAllRules();

        assertEquals( "Three rules should have been fired", 3, list.size() );
        assertEquals( "Rule 4 should have been fired first", "Rule 4",
                      list.get( 0 ) );
        assertEquals( "Rule 2 should have been fired second", "Rule 2",
                      list.get( 1 ) );
        assertEquals( "Rule 3 should have been fired third", "Rule 3",
                      list.get( 2 ) );
    }

    @Test
    public void testSalienceExpression() throws Exception {
        KieBase kbase = loadKnowledgeBase("test_salienceExpressionRule.drl");
        KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal( "list", list );

        final PersonInterface person10 = new Person( "bob", "cheese", 10 );
        ksession.insert( person10 );

        final PersonInterface person20 = new Person( "mic", "cheese", 20 );
        ksession.insert( person20 );

        ksession.fireAllRules();

        assertEquals( "Two rules should have been fired", 2, list.size() );
        assertEquals( "Rule 3 should have been fired first", "Rule 3",
                      list.get( 0 ) );
        assertEquals( "Rule 2 should have been fired second", "Rule 2",
                      list.get( 1 ) );
    }
    
    @Test
    public void testSalienceExpressionWithOr() throws Exception {
        String text = "package org.kie.test\n"
                      + "global java.util.List list\n"
                      + "import " + FactA.class.getCanonicalName() + "\n"
                      + "import " + Foo.class.getCanonicalName() + "\n"
                      + "import " + Pet.class.getCanonicalName() + "\n"
                      + "rule r1 salience (f1.field2)\n"
                      + "when\n"                      
                      + "    foo: Foo()\n" 
                      + "    ( Pet()  and f1 : FactA( field1 == 'f1') ) or \n"
                      + "    f1 : FactA(field1 == 'f2') \n"                      
                      + "then\n"
                      + "    list.add( f1 );\n"
                      + "    foo.setId( 'xxx' );\n"
                      + "end\n" + "\n";

        KieBase kbase = loadKnowledgeBaseFromString(text);
        KieSession ksession = kbase.newKieSession();
        List list = new ArrayList();
        ksession.setGlobal( "list", list );        
        ksession.insert ( new Foo(null, null) );
        ksession.insert ( new Pet(null) );
        
        FactA fact1 = new FactA();
        fact1.setField1( "f1" );
        fact1.setField2( 10 );
        
        FactA fact2 = new FactA();
        fact2.setField1( "f1" );
        fact2.setField2( 30 );
        
        FactA fact3 = new FactA();
        fact3.setField1( "f2" );
        fact3.setField2( 20 );
        
        ksession.insert( fact1 );
        ksession.insert( fact2 );
        ksession.insert( fact3 );
        
        ksession.fireAllRules();
        System.out.println( list );
        
        assertEquals( 3, list.size() );
        assertEquals( fact2, list.get( 0 ) );
        assertEquals( fact3, list.get( 1 ) );
        assertEquals( fact1, list.get( 2 ) );     
    }

    @Test
    public void testSalienceMinInteger() throws Exception {
        String text = "package org.kie.test\n"
                      + "global java.util.List list\n"
                      + "rule a\n"
                      + "when\n"
                      + "then\n"
                      + "    list.add( \"a\" );\n" + "end\n" + "\n"
                      + "rule b\n"
                      + "   salience ( Integer.MIN_VALUE )\n" + "when\n"
                      + "then\n"
                      + "    list.add( \"b\" );\n" + "end\n" + "\n"
                      + "rule c\n"
                      + "when\n"
                      + "then\n"
                      + "    list.add( \"c\" );\n"
                      + "end\n";

        KieBase kbase = loadKnowledgeBaseFromString(text);
        KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal( "list", list );
        ksession.fireAllRules();

        assertEquals( "b", list.get( 2 ) );
    }

    @Test
    public void testLoadOrderConflictResolver() throws Exception {
        String text = "package org.kie.test\n"
                      + "global java.util.List list\n"
                      + "rule a\n"
                      + "when\n"
                      + "      s : String( this == 'a') \n"
                      + "then\n"
                      + "    list.add( s );\n"
                      + "end\n" + "\n"

                      + "rule b\n"
                      + "when\n"
                      + "      s : String( this == 'b') \n"
                      + "then\n"
                      + "    list.add( s );\n"
                      + "end\n" + "\n"

                      + "rule c\n"
                      + "when\n"
                      + "      s : String( this == 'c') \n"
                      + "then\n"
                      + "    list.add( s );\n"
                      + "    insert( new String(\"a\") ); \n"
                      + "    insert( new String(\"b\") ); \n"
                      + "end\n" + "\n"

                      + "rule d\n"
                      + "when\n"
                      + "    s : String( this == 'd') \n"
                      + "then\n"
                      + "    list.add( s );\n"
                      + "    insert( new String(\"b\") ); \n"
                      + "    insert( new String(\"a\") ); \n"
                      + "end\n" + "\n"
                      + "rule e\n"
                      + "when\n"
                      + "    s : String( this == 'e') \n"
                      + "then\n"
                      + "    list.add( s );\n"
                      + "end\n" + "\n";

        KieBase kbase = loadKnowledgeBaseFromString(text);
        KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal( "list", list );
        ksession.insert( "a" );
        ksession.insert( "b" );
        ksession.insert( "c" );
        ksession.insert( "d" );
        ksession.insert( "e" );
        ksession.fireAllRules();

        assertEquals( 9, list.size() );
        assertEquals( "a", list.get( 0 ) );
        assertEquals( "b", list.get( 1 ) );
        assertEquals( "c", list.get( 2 ) );
        assertEquals( "a", list.get( 3 ) );
        assertEquals( "b", list.get( 4 ) );
        assertEquals( "d", list.get( 5 ) );
        assertEquals( "a", list.get( 6 ) );
        assertEquals( "b", list.get( 7 ) );
        assertEquals( "e", list.get( 8 ) );
    }


    
    @Test
    public void testEnabledExpressionWithOr() throws Exception {
        String text = "package org.kie.test\n"
                      + "global java.util.List list\n"
                      + "import " + FactA.class.getCanonicalName() + "\n"
                      + "import " + Foo.class.getCanonicalName() + "\n"
                      + "import " + Pet.class.getCanonicalName() + "\n"
                      + "rule r1 salience(f1.field2) enabled(f1.field2 >= 20)\n"
                      + "when\n"                      
                      + "    foo: Foo()\n" 
                      + "    ( Pet()  and f1 : FactA( field1 == 'f1') ) or \n"
                      + "    f1 : FactA(field1 == 'f2') \n"                      
                      + "then\n"
                      + "    list.add( f1 );\n"
                      + "    foo.setId( 'xxx' );\n"
                      + "end\n" + "\n";

        KieBase kbase = loadKnowledgeBaseFromString(text);
        KieSession ksession = kbase.newKieSession();
        List list = new ArrayList();
        ksession.setGlobal( "list", list );        
        ksession.insert ( new Foo(null, null) );
        ksession.insert ( new Pet(null) );
        
        FactA fact1 = new FactA();
        fact1.setField1( "f1" );
        fact1.setField2( 10 );
        
        FactA fact2 = new FactA();
        fact2.setField1( "f1" );
        fact2.setField2( 30 );
        
        FactA fact3 = new FactA();
        fact3.setField1( "f2" );
        fact3.setField2( 20 );
        
        ksession.insert( fact1 );
        ksession.insert( fact2 );
        ksession.insert( fact3 );
        
        ksession.fireAllRules();
        
        assertEquals( 2, list.size() );
        assertEquals( fact2, list.get( 0 ) );
        assertEquals( fact3, list.get( 1 ) );   
    }    

    @Test
    public void testNoLoop() throws Exception {
        KieBase kbase = loadKnowledgeBase("no-loop.drl");
        KieSession ksession = kbase.newKieSession();;

        final List list = new ArrayList();
        ksession.setGlobal( "list", list );

        final Cheese brie = new Cheese( "brie", 12 );
        ksession.insert( brie );

        ksession.fireAllRules();

        assertEquals( "Should not loop  and thus size should be 1", 1,
                      list.size() );

    }

    @Test
    public void testNoLoopWithModify() throws Exception {
        KieBase kbase = loadKnowledgeBase("no-loop_with_modify.drl");
        KieSession ksession =  kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal( "list", list );

        final Cheese brie = new Cheese( "brie", 12 );
        ksession.insert( brie );

        ksession.fireAllRules();

        assertEquals( "Should not loop  and thus size should be 1", 1,
                      list.size() );
        assertEquals( 50, brie.getPrice() );

    }

    @Test
    public void testLockOnActive() throws Exception {
        KieBase kbase = loadKnowledgeBase("test_LockOnActive.drl");
        KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal( "list", list );

        // AgendaGroup "group1" is not active, so should receive activation
        final Cheese brie12 = new Cheese( "brie", 12 );
        ksession.insert( brie12 );
        ((InternalWorkingMemory)ksession).flushPropagations();
        InternalAgenda agenda = ((InternalAgenda) ksession.getAgenda());
        final AgendaGroup group1 = agenda.getAgendaGroup( "group1" );
        assertEquals( 1, group1.size() );

        ksession.getAgenda().getAgendaGroup("group1").setFocus( );
        // AgendaqGroup "group1" is now active, so should not receive activations
        final Cheese brie10 = new Cheese( "brie", 10 );
        ksession.insert( brie10 );
        assertEquals( 1, group1.size() );

        final Cheese cheddar20 = new Cheese( "cheddar", 20 );
        ksession.insert( cheddar20 );
        final AgendaGroup group2 = agenda.getAgendaGroup( "group1" );
        assertEquals( 1, group2.size() );

        agenda.setFocus(group2);
        final Cheese cheddar17 = new Cheese( "cheddar", 17 );
        ksession.insert( cheddar17 );
        assertEquals( 1, group2.size() );
    }

    @Test
    public void testLockOnActiveForMain() {
        String str = "";
        str += "package org.kie \n";
        str += "global java.util.List list \n";
        str += "rule rule1 \n";
        str += "    lock-on-active true \n";
        str += "when \n";
        str += "    $str : String() \n";
        str += "then \n";
        str += "    list.add( $str ); \n";
        str += "end \n";

        KieBase kbase = loadKnowledgeBaseFromString(str);
        KieSession ksession = kbase.newKieSession();

        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        ksession.insert( "hello1" );
        ksession.insert( "hello2" );
        ksession.insert( "hello3" );

        ksession.fireAllRules();
        assertEquals( 3, list.size() );

        ksession.insert( "hello4" );
        ksession.insert( "hello5" );
        ksession.insert( "hello6" );

        ksession.fireAllRules();
        assertEquals( 6, list.size() );
    }

    @Test
    public void testLockOnActiveForMainWithHalt() {
        String str = "";
        str += "package org.kie \n";
        str += "global java.util.List list \n";
        str += "rule rule1 \n";
        str += "    lock-on-active true \n";
        str += "when \n";
        str += "    $str : String() \n";
        str += "then \n";
        str += "    list.add( $str ); \n";
        str += "    if ( list.size() == 2 ) {\n";
        str += "        drools.halt();\n";
        str += "    }";
        str += "end \n";
        KieBase kbase = loadKnowledgeBaseFromString(str);
        KieSession ksession = kbase.newKieSession();
        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        ksession.insert( "hello1" );
        ksession.insert( "hello2" );
        ksession.insert( "hello3" );

        ksession.fireAllRules();
        assertEquals( 2, list.size() );

        // because we have halted, the next 3 will be ignored, but it will still
        // fire the remaing 3rd activation from previous asserts
        ksession.insert( "hello4" );
        ksession.insert( "hello5" );
        ksession.insert( "hello6" );

        ksession.fireAllRules();
        assertEquals( 3, list.size() );
    }

    @Test
    public void testLockOnActiveWithModify() throws Exception {
        KieBase kbase = loadKnowledgeBase("test_LockOnActiveWithUpdate.drl");
        KieSession ksession =  kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal( "list", list );

        final Cheese brie = new Cheese( "brie", 13 );

        final Person bob = new Person( "bob" );
        bob.setCheese( brie );

        final Person mic = new Person( "mic" );
        mic.setCheese( brie );

        final Person mark = new Person( "mark" );
        mark.setCheese( brie );

        final FactHandle brieHandle = ( FactHandle ) ksession.insert( brie );
        ksession.insert( bob );
        ksession.insert( mic );
        ksession.insert( mark );

        InternalWorkingMemory wm = ( InternalWorkingMemory )ksession;
        wm.flushPropagations();

        final InternalAgenda agenda = (InternalAgenda) ksession.getAgenda();
        final AgendaGroup group1 = agenda.getAgendaGroup( "group1" );
        if ( phreak == RuleEngineOption.RETEOO ) {
            agenda.setFocus( group1 );
            assertEquals( 3, group1.size() );
            agenda.fireNextItem( null, 0, 0 );
            assertEquals( 2, group1.size() );
            ksession.update( brieHandle, brie );
            assertEquals( 2, group1.size() );

            AgendaGroup group2 = agenda.getAgendaGroup( "group2" );
            assertEquals( 3, group2.size() );
            agenda.setFocus( group2 );

            agenda.activateRuleFlowGroup( "ruleflow2" );
            agenda.fireNextItem( null, 0, 0 );
            assertEquals( 2, group2.size() );
            ksession.update( brieHandle, brie );
            assertEquals( 2, group2.size() );
        } else {
            agenda.setFocus( group1 );
            assertEquals( 1, group1.size() );
            RuleAgendaItem ruleItem1 = (RuleAgendaItem) group1.getActivations()[0];
            ruleItem1.getRuleExecutor().evaluateNetwork(wm);
            assertEquals(3, ruleItem1.getRuleExecutor().getLeftTupleList().size());

            agenda.fireNextItem( null, 0, 0 );
            assertEquals( 1, group1.size() );
            assertEquals( 2, ruleItem1.getRuleExecutor().getLeftTupleList().size() );

            ksession.update( brieHandle, brie );
            assertEquals( 1, group1.size() );
            ruleItem1.getRuleExecutor().evaluateNetwork(wm);
            assertEquals(2, ruleItem1.getRuleExecutor().getLeftTupleList().size());

            AgendaGroup group2 = agenda.getAgendaGroup( "group2" );
            agenda.setFocus( group2);
            assertEquals( 1, group2.size() );
            RuleAgendaItem ruleItem2 = (RuleAgendaItem) group2.getActivations()[0];
            ruleItem2.getRuleExecutor().evaluateNetwork(wm);
            assertEquals(3, ruleItem2.getRuleExecutor().getLeftTupleList().size());

            agenda.fireNextItem( null, 0, 0 );
            assertEquals( 1, group2.size() );
            assertEquals( 2, ruleItem2.getRuleExecutor().getLeftTupleList().size() );

            ksession.update( brieHandle, brie );
            assertEquals( 1, group2.size() );
            assertEquals( 2, ruleItem2.getRuleExecutor().getLeftTupleList().size() );
        }
    }

    @Test
    public void testLockOnActiveWithModify2() throws Exception {
        KieBase kbase = loadKnowledgeBase("test_LockOnActiveWithModify.drl");
        KieSession ksession = kbase.newKieSession();

        // populating working memory
        final int size = 3;

        Cell[][] cells = new Cell[size][];
        FactHandle[][] handles = new FactHandle[size][];
        for ( int row = 0; row < size; row++ ) {
            cells[row] = new Cell[size];
            handles[row] = new FactHandle[size];
            for ( int col = 0; col < size; col++ ) {
                cells[row][col] = new Cell( Cell.DEAD, row, col );
                handles[row][col] = (FactHandle) ksession.insert( cells[row][col] );
                if ( row >= 1 && col >= 1 ) {
                    // northwest
                    ksession.insert( new Neighbor( cells[row - 1][col - 1],
                                                  cells[row][col] ) );
                    ksession.insert( new Neighbor( cells[row][col],
                                                  cells[row - 1][col - 1] ) );
                }
                if ( row >= 1 ) {
                    // north
                    ksession.insert( new Neighbor( cells[row - 1][col],
                                                  cells[row][col] ) );
                    ksession.insert( new Neighbor( cells[row][col],
                                                  cells[row - 1][col] ) );
                }
                if ( row >= 1 && col < (size - 1) ) {
                    // northeast
                    ksession.insert( new Neighbor( cells[row - 1][col + 1],
                                                  cells[row][col] ) );
                    ksession.insert( new Neighbor( cells[row][col],
                                                  cells[row - 1][col + 1] ) );
                }
                if ( col >= 1 ) {
                    // west
                    ksession.insert( new Neighbor( cells[row][col - 1],
                                                  cells[row][col] ) );
                    ksession.insert( new Neighbor( cells[row][col],
                                                  cells[row][col - 1] ) );
                }
            }
        }

        ksession.getAgenda().getAgendaGroup("calculate").clear();

         // now, start playing
        int fired = ksession.fireAllRules( 100 );
        assertEquals( 0, fired );

        ksession.getAgenda().getAgendaGroup("calculate").setFocus();
        fired = ksession.fireAllRules( 100 );
        // logger.writeToDisk();
        assertEquals( 0, fired );
        assertEquals("MAIN", ((InternalAgenda) ksession.getAgenda()).getFocusName());

        // on the fifth day God created the birds and sea creatures
        cells[0][0].setState( Cell.LIVE );
        ksession.update( handles[0][0], cells[0][0] );
        ksession.getAgenda().getAgendaGroup("birth").setFocus();
        ksession.getAgenda().getAgendaGroup("calculate").setFocus();
        fired = ksession.fireAllRules( 100 );

        // logger.writeToDisk();
        int[][] expected = new int[][]{{0, 1, 0}, {1, 1, 0}, {0, 0, 0}};
        assertEqualsMatrix( size, cells, expected );
        assertEquals( "MAIN", ((InternalAgenda)ksession.getAgenda()).getFocusName() );

        // on the sixth day God created the animals that walk over the land and
        // the Man
        cells[1][1].setState( Cell.LIVE );
        ksession.update( handles[1][1], cells[1][1] );
        ksession.getAgenda().getAgendaGroup("calculate").setFocus();
        ksession.fireAllRules( 100 );
        // logger.writeToDisk();

        expected = new int[][]{{1, 2, 1}, {2, 1, 1}, {1, 1, 1}};
        assertEqualsMatrix( size, cells, expected );
        assertEquals( "MAIN", ((InternalAgenda)ksession.getAgenda()).getFocusName()  );

        ksession.getAgenda().getAgendaGroup("birth").setFocus();
        ksession.fireAllRules( 100 );
        expected = new int[][]{{1, 2, 1}, {2, 1, 1}, {1, 1, 1}};
        assertEqualsMatrix( size, cells, expected );
        assertEquals( "MAIN", ((InternalAgenda)ksession.getAgenda()).getFocusName()  );

        System.out.println( "--------" );
        ksession.getAgenda().getAgendaGroup("calculate").setFocus();
        ksession.fireAllRules( 100 );
        // logger.writeToDisk();
        // printMatrix( cells );

        expected = new int[][]{{3, 3, 2}, {3, 3, 2}, {2, 2, 1}};
        assertEqualsMatrix( size, cells, expected );
        assertEquals( "MAIN", ((InternalAgenda)ksession.getAgenda()).getFocusName()  );
        System.out.println( "--------" );

        // on the seventh day, while God rested, man start killing them all
        cells[0][0].setState( Cell.DEAD );
        ksession.update( handles[0][0], cells[0][0] );
        ksession.getAgenda().getAgendaGroup("calculate").setFocus();
        ksession.fireAllRules( 100 );

        expected = new int[][]{{3, 2, 2}, {2, 2, 2}, {2, 2, 1}};
        assertEqualsMatrix( size, cells, expected );
        assertEquals( "MAIN", ((InternalAgenda)ksession.getAgenda()).getFocusName()  );

    }

    // private void printMatrix(Cell[][] cells) {
    // System.out.println("----------");
    // for( int row = 0; row < cells.length; row++) {
    // for( int col = 0; col < cells[row].length; col++ ) {
    // System.out.print( cells[row][col].getValue() +
    // ((cells[row][col].getState()==Cell.LIVE)?"L  ":".  ") );
    // }
    // System.out.println();
    // }
    // System.out.println("----------");
    // }

    private void assertEqualsMatrix(final int size,
                                    Cell[][] cells,
                                    int[][] expected) {
        for ( int row = 0; row < size; row++ ) {
            for ( int col = 0; col < size; col++ ) {
                assertEquals( "Wrong value at " + row + "," + col + ": ",
                              expected[row][col], cells[row][col].getValue() );
            }
        }
    }

    @Test
    public void testAgendaGroups() throws Exception {
        KieBase kbase = loadKnowledgeBase("test_AgendaGroups.drl");
        KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal( "list", list );

        final Cheese brie = new Cheese( "brie", 12 );
        ksession.insert( brie );

        ksession.fireAllRules();

        assertEquals( 7, list.size() );

        assertEquals( "group3", list.get( 0 ) );
        assertEquals( "group4", list.get( 1 ) );
        assertEquals( "group3", list.get( 2 ) );
        assertEquals( "MAIN", list.get( 3 ) );
        assertEquals( "group1", list.get( 4 ) );
        assertEquals( "group1", list.get( 5 ) );
        assertEquals( "MAIN", list.get( 6 ) );

        ksession.getAgenda().getAgendaGroup( "group2" ).setFocus();
        ksession.fireAllRules();

        assertEquals( 8, list.size() );
        assertEquals( "group2", list.get( 7 ) );

        if ( CommonTestMethodBase.phreak == RuleEngineOption.RETEOO ) {
            // clear only works for Rete, as while Phreak can be eager, it'll still result in rule firing

            // clear main only the auto focus related ones should fire
            list.clear();
            ksession.insert( new Cheese( "cheddar" ) );
            ksession.getAgenda().getAgendaGroup( "MAIN" ).clear();
            ksession.fireAllRules();
            assertEquals( 3, list.size() );
            assertEquals( "group3", list.get( 0 ) );
            assertEquals( "group4", list.get( 1 ) );
            assertEquals( "group3", list.get( 2 ) );
        }

    }

    @Test
    public void testActivationGroups() throws Exception {
        KieBase kbase = loadKnowledgeBase("test_ActivationGroups.drl");
        KieSession ksession = kbase.newKieSession();


        final List list = new ArrayList();
        ksession.setGlobal( "list", list );

        final Cheese brie = new Cheese( "brie", 12 );
        ksession.insert( brie );

        ksession.fireAllRules();

        assertEquals( 2, list.size() );
        assertEquals( "rule0", list.get( 0 ) );
        assertEquals( "rule2", list.get( 1 ) );
    }
    
    @Test 
    public void testUnMatchListenerForChainedPlanningEntities() {
        String str =""+
                "package org.drools.compiler.integrationtests;\n" +
                "\n" +
                "import org.drools.compiler.Father;\n" +
                "import org.drools.compiler.TotalHolder;\n" +
                "\n" +
                "import org.drools.core.common.AgendaItem;\n" +
                "import org.kie.internal.event.rule.ActivationUnMatchListener;\n" +
                "import org.kie.api.runtime.rule.RuleRuntime;\n" +
                "import org.kie.api.runtime.rule.Match;\n" +
                "\n" +
                "global TotalHolder totalHolder;\n" +
                "\n" +
                "rule \"sumWeightOfFather\"\n" +
                "when\n" +
                "    $h: Father(father != null, $wf : weightOfFather)\n" +
                "then\n" +
                "    totalHolder.add($wf);\n" +
                "    final TotalHolder finalTotalHolder = totalHolder;\n" +
                "    final int finalWf = $wf;\n" +
                "    AgendaItem agendaItem = (AgendaItem) kcontext.getMatch();" +
                "    if (agendaItem.getActivationUnMatchListener() != null) {\n" +
                "        RuleRuntime session = null; // Should not be used by the undoListener anyway\n" +
                "        agendaItem.getActivationUnMatchListener().unMatch(session, agendaItem);\n" +
                "    }" +
                "    agendaItem.setActivationUnMatchListener(new ActivationUnMatchListener() {" +
                "            public void unMatch(RuleRuntime session, Match match) {" +
                "                finalTotalHolder.subtract(finalWf);" +
                "            }" +
                "    });" +
                "end";

        KieBase kbase = loadKnowledgeBaseFromString(str);
        KieSession ksession = kbase.newKieSession();

        ksession.setGlobal("totalHolder", new TotalHolder());
        Father abraham = new Father("abraham", null, 100);
        Father homer = new Father("homer", null, 20);
        Father bart = new Father("bart", null, 3);

        FactHandle abrahamHandle = ksession.insert(abraham);
        FactHandle bartHandle = ksession.insert(bart);
        ksession.fireAllRules();
        assertEquals(0, ((TotalHolder) ksession.getGlobal("totalHolder")).getTotal());

        bart.setFather(abraham);
        ksession.update(bartHandle, bart);
        ksession.fireAllRules();
        assertEquals(100, ((TotalHolder) ksession.getGlobal("totalHolder")).getTotal());

        bart.setFather(null);
        ksession.update(bartHandle, bart);
        ksession.fireAllRules();
        assertEquals(0, ((TotalHolder) ksession.getGlobal("totalHolder")).getTotal());

        bart.setFather(abraham);
        ksession.update(bartHandle, bart);
        ksession.fireAllRules();
        assertEquals(100, ((TotalHolder) ksession.getGlobal("totalHolder")).getTotal());

        FactHandle homerHandle = ksession.insert(homer);
        homer.setFather(abraham);
        ksession.update(homerHandle, homer);
        bart.setFather(homer);
        ksession.update(bartHandle, bart);
        ksession.fireAllRules();
        assertEquals(120, ((TotalHolder) ksession.getGlobal("totalHolder")).getTotal());
    }    

    public static class Holder {
        private Integer val;
        private String  outcome;

        public Holder(Integer val) {
            this.val = val;
        }

        public void setValue(Integer val) {
            this.val = val;
        }

        public Integer getValue() {
            return this.val;
        }

        public void setOutcome(String outcome) {
            this.outcome = outcome;
        }

        public String getOutcome() {
            return this.outcome;
        }
    }

    @Test
    // JBRULES-2398
    public void
    testActivationGroupWithTroubledSyntax() {
    String str = "package BROKEN_TEST;\n" + "import "
                 + Holder.class.getCanonicalName() + ";\n"
                 + "rule \"_12\"\n"
                 + "    \n"
                 + "    salience 3\n"
                 + "    activation-group \"BROKEN\"\n"
                 + "    when\n"
                 + "        $a : Holder(value in (0))\n"
                 + "    then\n"
                 + "        System.out.println(\"setting 0\");\n"
                 + "        $a.setOutcome(\"setting 0\");\n"
                 + "end\n" + "\n"
                 + "rule \"_13\"\n"
                 + "    \n"
                 + "    salience 2\n"
                 + "    activation-group \"BROKEN\"\n"
                 + "    when\n"
                 + "        $a : Holder(value in (1))\n"
                 + "    then\n"
                 + "        System.out.println(\"setting 1\");\n"
                 + "        $a.setOutcome(\"setting 1\");\n"
                 + "end\n" + "\n"
                 + "rule \"_22\"\n"
                 + "    \n" + "    salience 1\n"
                 + "    activation-group \"BROKEN\"\n"
                 + "    when\n"
                 + "        $a : Holder(value == null)\n"
                 + "    then\n"
                 + "        System.out.println(\"setting null\");\n"
                 + "        $a.setOutcome(\"setting null\");\n"
                 + "end\n" + "\n"
                 + "";

        KieBase kbase = loadKnowledgeBaseFromString(str);
        KieSession ksession = kbase.newKieSession();

        Holder inrec = new Holder( 1 );
        System.out.println( "Holds: " + inrec.getValue() );
        ksession.insert( inrec );
        ksession.fireAllRules();
        Assert.assertEquals( 1, ksession.getFactHandles().size() );
        Assert.assertEquals( "setting 1", inrec.getOutcome() );

        ksession.dispose();
        ksession = kbase.newKieSession();
        inrec = new Holder( null );
        System.out.println( "Holds: " + inrec.getValue() );
        ksession.insert( inrec );
        ksession.fireAllRules();
        Assert.assertEquals( 1, ksession.getFactHandles().size() );
        Assert.assertEquals( "setting null", inrec.getOutcome() );

        ksession.dispose();
        ksession = kbase.newKieSession();
        inrec = new Holder( 0 );
        System.out.println( "Holds: " + inrec.getValue() );
        ksession.insert( inrec );
        ksession.fireAllRules(); // appropriate rule is not fired!
        Assert.assertEquals( 1, ksession.getFactHandles().size() );
        Assert.assertEquals( "setting 0", inrec.getOutcome() );
    }

    @Test
    public void testInsertRetractNoloop() throws Exception {
        // read in the source
        KieBase kbase = loadKnowledgeBase("test_Insert_Retract_Noloop.drl");
        KieSession ksession = kbase.newKieSession();

        ksession.insert( new Cheese( "stilton", 15 ) );

        ksession.fireAllRules();
        assertEquals(0, ksession.getObjects().size());
    }

    @Test
    public void testUpdateNoLoop() throws Exception {
        // JBRULES-780, throws a NullPointer or infinite loop if there is an
        // issue
        KieBase kbase = loadKnowledgeBase("test_UpdateNoloop.drl");
        KieSession ksession = kbase.newKieSession();

        Cheese cheese = new Cheese( "stilton", 15 );
        ksession.insert( cheese  );

        ksession.fireAllRules();

        assertEquals( 14, cheese.getPrice() );
    }

    @Test
    public void testUpdateActivationCreationNoLoop() throws Exception {
        // JBRULES-787, no-loop blocks all dependant tuples for update
        KieBase kbase = loadKnowledgeBase("test_UpdateActivationCreationNoLoop.drl");
        KieSession ksession = kbase.newKieSession();

        final List created = new ArrayList();
        final List cancelled = new ArrayList();
        final AgendaEventListener l = new DefaultAgendaEventListener() {
            @Override
            public void matchCreated(MatchCreatedEvent event) {
                created.add( event );
            }

            @Override
            public void matchCancelled(MatchCancelledEvent event) {
                cancelled.add( event );
            }
        };

        ksession.addEventListener( l );

        final Cheese stilton = new Cheese( "stilton", 15 );
        final FactHandle stiltonHandle = ksession.insert( stilton );

        final Person p1 = new Person( "p1" );
        p1.setCheese( stilton );
        ksession.insert( p1 );

        final Person p2 = new Person( "p2" );
        p2.setCheese( stilton );
        ksession.insert( p2 );

        final Person p3 = new Person( "p3" );
        p3.setCheese( stilton );
        ksession.insert( p3 );

        ksession.fireAllRules();

        assertEquals( 3, created.size() );
        assertEquals( 0, cancelled.size() );

        // simulate a modify inside a consequence
        ksession.update( stiltonHandle, stilton );

        // with true modify, no reactivations should be triggered
        assertEquals( 3, created.size() );
        assertEquals( 0, cancelled.size() );
    }

    @Test
    public void testRuleFlowGroup() throws Exception {
        KieBase kbase = loadKnowledgeBase("ruleflowgroup.drl");
        KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.insert( "Test" );
        ksession.fireAllRules();
        assertEquals( 0, list.size() );

        ((InternalAgenda)ksession.getAgenda()).activateRuleFlowGroup("Group1");
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
    }

    @Test
    public void testRuleFlowGroupDeactivate() throws Exception {
        // need to make eager, for cancel to work, (mdp)
        KieBase kbase = loadKnowledgeBase("ruleflowgroup2.drl");
        KieSession ksession = kbase.newKieSession();


        final List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.insert( "Test" );
        ksession.fireAllRules();
        assertEquals( 0, list.size() );
        assertEquals(2, ((InternalAgenda) ksession.getAgenda()).getRuleFlowGroup("Group1").size());

        ((InternalAgenda)ksession.getAgenda()).activateRuleFlowGroup("Group1");
        ksession.fireAllRules();

        assertEquals( 0, list.size() );
    }

    @Test(timeout=10000)
    public void testRuleFlowGroupInActiveMode() throws Exception {
        KieBase kbase = loadKnowledgeBase("ruleflowgroup.drl");
        final KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal( "list",
                                 list );
        
        final AtomicBoolean fired = new AtomicBoolean(false);
        ksession.addEventListener(new org.kie.api.event.rule.DefaultAgendaEventListener() {
            @Override
            public void afterMatchFired(AfterMatchFiredEvent event) {
                synchronized( fired ) {
                    fired.set(true);
                    fired.notifyAll();
                }
            }
        });

        new Thread(new Runnable() {
            public void run() {
                ksession.fireUntilHalt();
            }
        }).start();

        ksession.insert( "Test" );
        assertEquals( 0,
                      list.size() );

        ((InternalAgenda) ksession.getAgenda()).activateRuleFlowGroup("Group1");
        
        synchronized( fired ) {
            if( !fired.get() ) {
                fired.wait();
            }
        }

        assertEquals( 1,
                      list.size() );

        ksession.halt();
    }

    @Test
    public void testDateEffective() throws Exception {
        // read in the source
        KieBase kbase = loadKnowledgeBase("test_EffectiveDate.drl");
        KieSession ksession = kbase.newKieSession();


        final List list = new ArrayList();
        ksession.setGlobal( "list", list );

        // go !
        final Message message = new Message( "hola" );
        ksession.insert( message );
        ksession.fireAllRules();
        assertFalse( message.isFired() );
    }

    @Test
    public void testNullPointerOnModifyWithLockOnActive() {
        // JBRULES-3234

        String str = "package org.kie.test \n"
                     + "import org.drools.compiler.Person; \n"
                     + "rule 'Rule 1' agenda-group 'g1' lock-on-active	when \n"
                     + "		$p : Person( age != 35 ) \n"
                     + "	then \n"
                     + "		modify( $p ) { setAge( 35 ) };	\n"
                     + "end \n"
                     + "rule 'Rule 2' agenda-group 'g1' no-loop when \n"
                     + "		$p:  Person( age == 35) \n"
                     + "	then \n"
                     + "		modify( $p ) { setAge( 36 ) }; \n"
                     + "end \n";

        KieBase kbase = loadKnowledgeBaseFromString(str);
        KieSession ksession = kbase.newKieSession();

        Person p = new Person( "darth", 36 );
        FactHandle fh = (FactHandle) ksession.insert( p );

        ksession.getAgenda().getAgendaGroup( "g1" ).setFocus();

        ksession.fireAllRules();

        ksession.update( fh, p ); // normally NPE thrown here, for BUG
        
        assertEquals( 36, p.getAge() );
    }


    @Test
    public void testAgendaGroupGivewaySequence() {
        // BZ-999360
        String str =
                "global java.util.List ruleList\n" +
                "\n" +

                "rule r1 agenda-group \"g1\" salience 20\n when" +
                "    String( this == 'r1' )\n" +
                "then\n" +
                "    ruleList.add(1);\n" +
                "end\n" +

                "rule r2 agenda-group \"g1\" salience 15\n when" +
                "    String( this == 'r2' )\n" +
                "then\n" +
                "    ruleList.add(2);\n" +
                "    kcontext.getKnowledgeRuntime().getAgenda().getAgendaGroup(\"g2\").setFocus();\n" +
                "end\n" +

                "rule r3 agenda-group \"g1\" salience 10\n when" +
                "    String( this == 'r3' )\n" +
                "then\n" +
                "    ruleList.add(3);\n" +
                "end\n" +

                "rule r4 agenda-group \"g2\" salience 5\n when" +
                "    String( this == 'r4' )\n" +
                "then\n" +
                "    ruleList.add(4);\n" +
                "end\n";

        KieBase kbase = loadKnowledgeBaseFromString(str);
        KieSession ksession = kbase.newKieSession();

        ArrayList<String> ruleList = new ArrayList<String>();
        ksession.setGlobal("ruleList", ruleList);

        ksession.insert(new String("r1"));
        ksession.insert(new String("r1"));
        ksession.insert(new String("r2"));
        ksession.insert(new String("r2"));
        ksession.insert(new String("r3"));
        ksession.insert(new String("r3"));
        ksession.insert(new String("r4"));
        ksession.insert(new String("r4"));
        ksession.getAgenda().getAgendaGroup("g1").setFocus();

        assertEquals( 8, ksession.fireAllRules() );


        assertEquals( 8, ruleList.size() );
        assertEquals( 1, ruleList.get(0));
        assertEquals( 1, ruleList.get(1));
        assertEquals( 2, ruleList.get(2));

        assertEquals( 4, ruleList.get(3));
        assertEquals( 4, ruleList.get(4));

        assertEquals( 2, ruleList.get(5));

        assertEquals( 3, ruleList.get(6));
        assertEquals( 3, ruleList.get(7));
    }
}
