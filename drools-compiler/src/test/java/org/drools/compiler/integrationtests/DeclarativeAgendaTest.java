package org.drools.compiler.integrationtests;

import org.drools.compiler.CommonTestMethodBase;
import org.junit.Test;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.DeclarativeAgendaOption;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Match;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeclarativeAgendaTest extends CommonTestMethodBase {
    
    @Test(timeout=10000)
    public void testSimpleBlockingUsingForall() {
        String str = "";
        str += "package org.domain.test \n";
        str += "import " + Match.class.getName() + "\n";
        str += "global java.util.List list \n";
        str += "dialect 'mvel' \n";
        str += "rule rule1 @department(sales) salience -100 \n";
        str += "when \n";
        str += "     $s : String( this == 'go1' ) \n";
        str += "then \n";
        str += "    list.add( kcontext.rule.name + ':' + $s ); \n";
        str += "end \n";
        str += "rule rule2 salience 200\n";
        str += "when \n";        
        str += "     $s : String( this == 'go1' ) \n";
        str += "     exists  Match( department == 'sales' ) \n";  
        str += "     forall ( $a : Match( department == 'sales' ) Match( this == $a, active == false ) ) \n";
        str += "then \n";
        str += "    list.add( kcontext.rule.name + ':' + $s ); \n";
        str += "end \n";

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( DeclarativeAgendaOption.ENABLED );
        KnowledgeBase kbase = loadKnowledgeBaseFromString( kconf, str );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );
        ksession.insert( "go1" );
        ksession.fireAllRules();

        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains("rule1:go1") );
        assertTrue( list.contains("rule2:go1") );

        ksession.dispose();
    }

    @Test(timeout=10000)
    public void testBasicBlockOnAnnotation() {
        String str = "";
        str += "package org.domain.test \n";
        str += "import " + Match.class.getName() + "\n";
        str += "global java.util.List list \n";
        str += "dialect 'mvel' \n";
        str += "rule rule1 @department(sales) \n";
        str += "when \n";
        str += "     $s : String( this == 'go1' ) \n";
        str += "then \n";
        str += "    list.add( kcontext.rule.name + ':' + $s ); \n";
        str += "end \n";
        str += "rule rule2 @department(sales) \n";
        str += "when \n";
        str += "     $s : String( this == 'go1' ) \n";
        str += "then \n";
        str += "    list.add( kcontext.rule.name + ':' + $s ); \n";
        str += "end \n";
        str += "rule rule3 @department(sales) \n";
        str += "when \n";
        str += "     $s : String( this == 'go1' ) \n";
        str += "then \n";
        str += "    list.add( kcontext.rule.name + ':' + $s ); \n";
        str += "end \n";
        str += "rule blockerAllSalesRules @activationListener('direct') \n";
        str += "when \n";
        str += "     $s : String( this == 'go2' ) \n";
        str += "     $i : Match( department == 'sales' ) \n";
        str += "then \n";
        str += "    list.add( $i.rule.name + ':' + $s  ); \n";
        str += "    kcontext.blockMatch( $i ); \n";
        str += "end \n";

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( DeclarativeAgendaOption.ENABLED );
        KnowledgeBase kbase = loadKnowledgeBaseFromString( kconf, str );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );
        ksession.insert( "go1" );
        FactHandle go2 = ksession.insert( "go2" );        
        ksession.fireAllRules();
        
        assertEquals( 3,
                      list.size() );
        assertTrue( list.contains( "rule1:go2" ) );
        assertTrue( list.contains( "rule2:go2" ) );
        assertTrue( list.contains( "rule3:go2" ) );

        list.clear();
        ksession.retract( go2 );
        ksession.fireAllRules();

        assertEquals( 3,
                      list.size() );
        assertTrue( list.contains( "rule1:go1" ) );
        assertTrue( list.contains( "rule2:go1" ) );
        assertTrue( list.contains( "rule3:go1" ) );

        ksession.dispose();
    }

    @Test//(timeout=10000)
    public void testApplyBlockerFirst() {
        StatefulKnowledgeSession ksession = getStatefulKnowledgeSession();

        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );
        FactHandle go2 = ksession.insert( "go2" );
        //((InternalWorkingMemory) ksession).flushPropagations();
        FactHandle go1 = ksession.insert( "go1" );
        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "rule1:go2" ) );

        list.clear();

        ksession.retract( go2 );
        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "rule1:go1" ) );
    }

    @Test(timeout=10000)
    public void testApplyBlockerFirstWithFireAllRulesInbetween() {
        StatefulKnowledgeSession ksession = getStatefulKnowledgeSession();

        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );
        FactHandle go2 = ksession.insert( "go2" );
        ksession.fireAllRules();
        assertEquals( 0,
                      list.size() );

        FactHandle go1 = ksession.insert( "go1" );
        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "rule1:go2" ) );

        list.clear();

        ksession.retract( go2 );
        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "rule1:go1" ) );
    }

    @Test//(timeout=10000)
    public void testApplyBlockerSecond() {
        StatefulKnowledgeSession ksession = getStatefulKnowledgeSession();

        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );
        FactHandle go1 = ksession.insert( "go1" );
        FactHandle go2 = ksession.insert( "go2" );
        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "rule1:go2" ) );

        list.clear();

        ksession.retract( go2 );
        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "rule1:go1" ) );
    }

    @Test(timeout=10000)
    public void testApplyBlockerSecondWithUpdate() {
        StatefulKnowledgeSession ksession = getStatefulKnowledgeSession();

        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );
        FactHandle go1 = ksession.insert( "go1" );
        FactHandle go2 = ksession.insert( "go2" );
        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "rule1:go2" ) );

        list.clear();

        ksession.update( go2,
                         "go2" );
        ksession.fireAllRules();
        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "rule1:go2" ) );

        list.clear();

        ksession.retract( go2 );
        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "rule1:go1" ) );
    }

    @Test(timeout=10000)
    public void testApplyBlockerSecondAfterUpdate() {
        StatefulKnowledgeSession ksession = getStatefulKnowledgeSession();

        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );
        FactHandle go1 = ksession.insert( "go1" );
        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "rule1:go1" ) );

        list.clear();

        FactHandle go2 = ksession.insert( "go2" );
        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "rule1:go2" ) );

        list.clear();

        ksession.update( go1,
                         "go1" );
        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "rule1:go2" ) );

        list.clear();

        ksession.retract( go2 );
        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "rule1:go1" ) );
    }

    public StatefulKnowledgeSession getStatefulKnowledgeSession() {
        String str = "";
        str += "package org.domain.test \n";
        str += "import " + Match.class.getName() + "\n";
        str += "global java.util.List list \n";
        str += "dialect 'mvel' \n";

        str += "rule rule1 @department(sales) \n";
        str += "when \n";
        str += "     $s : String( this == 'go1' ) \n";
        str += "then \n";
        str += "    list.add( kcontext.rule.name + ':' + $s ); \n";
        str += "end \n";

        str += "rule blockerAllSalesRules @activationListener('direct') \n";
        str += "when \n";
        str += "     $s : String( this == 'go2' ) \n";
        str += "     $i : Match( department == 'sales' ) \n";
        str += "then \n";
        str += "    list.add( $i.rule.name + ':' + $s  ); \n";
        str += "    kcontext.blockMatch( $i ); \n";
        str += "end \n";

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( DeclarativeAgendaOption.ENABLED );
        KnowledgeBase kbase = loadKnowledgeBaseFromString( kconf, str );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        return ksession;
    }

    @Test(timeout=10000)
    public void testMultipleBlockers() {
        String str = "";
        str += "package org.domain.test \n";
        str += "import " + Match.class.getName() + "\n";
        str += "global java.util.List list \n";
        str += "dialect 'mvel' \n";

        str += "rule rule0 @department(sales) \n";
        str += "when \n";
        str += "     $s : String( this == 'go0' ) \n";
        str += "then \n";
        str += "    list.add( kcontext.rule.name + ':' + $s ); \n";
        str += "end \n";

        str += "rule blockerAllSalesRules1 @activationListener('direct') \n";
        str += "when \n";
        str += "     $s : String( this == 'go1' ) \n";
        str += "     $i : Match( department == 'sales' ) \n";
        str += "then \n";
        str += "    list.add( $i.rule.name + ':' + $s  ); \n";
        str += "    kcontext.blockMatch( $i ); \n";
        str += "end \n";

        str += "rule blockerAllSalesRules2 @activationListener('direct') \n";
        str += "when \n";
        str += "     $s : String( this == 'go2' ) \n";
        str += "     $i : Match( department == 'sales' ) \n";
        str += "then \n";
        str += "    list.add( $i.rule.name + ':' + $s  ); \n";
        str += "    kcontext.blockMatch( $i ); \n";
        str += "end \n";

        str += "rule blockerAllSalesRules3 @activationListener('direct') \n";
        str += "when \n";
        str += "     $s : String( this == 'go3' ) \n";
        str += "     $i : Match( department == 'sales' ) \n";
        str += "then \n";
        str += "    list.add( $i.rule.name + ':' + $s  ); \n";
        str += "    kcontext.blockMatch( $i ); \n";
        str += "end \n";

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( DeclarativeAgendaOption.ENABLED );
        KnowledgeBase kbase = loadKnowledgeBaseFromString( kconf, str );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );
        FactHandle go0 = ksession.insert( "go0" );
        FactHandle go1 = ksession.insert( "go1" );
        FactHandle go2 = ksession.insert( "go2" );
        FactHandle go3 = ksession.insert( "go3" );

        ksession.fireAllRules();
        assertEquals( 3,
                      list.size() );
        assertTrue( list.contains( "rule0:go1" ) );
        assertTrue( list.contains( "rule0:go2" ) );
        assertTrue( list.contains( "rule0:go3" ) );

        list.clear();

        ksession.retract( go3 );
        ksession.fireAllRules();
        assertEquals( 0,
                      list.size() );

        ksession.retract( go2 );
        ksession.fireAllRules();
        assertEquals( 0,
                      list.size() );

        ksession.retract( go1 );
        ksession.fireAllRules();
        assertEquals( 1,
                      list.size() );

        assertTrue( list.contains( "rule0:go0" ) );
        ksession.dispose();
    }

    @Test(timeout=10000)
    public void testMultipleBlockersWithUnblockAll() {
        // This test is a bit wierd as it recurses. Maybe unblockAll is not feasible...
        String str = "";
        str += "package org.domain.test \n";
        str += "import " + Match.class.getName() + "\n";
        str += "global java.util.List list \n";
        str += "dialect 'mvel' \n";

        str += "rule rule0 @Propagation(EAGER) @department(sales) \n";
        str += "when \n";
        str += "     $s : String( this == 'go0' ) \n";
        str += "then \n";
        str += "    list.add( kcontext.rule.name + ':' + $s ); \n";
        str += "end \n";

        str += "rule blockerAllSalesRules1 @activationListener('direct') \n";
        str += "when \n";
        str += "     $s : String( this == 'go1' ) \n";
        str += "     $i : Match( department == 'sales' ) \n";
        str += "then \n";
        str += "    list.add( kcontext.rule.name + ':' + $i.rule.name + ':' + $s  ); \n";
        str += "    kcontext.blockMatch( $i ); \n";
        str += "end \n";

        str += "rule blockerAllSalesRules2 @activationListener('direct') \n";
        str += "when \n";
        str += "     $s : String( this == 'go2' ) \n";
        str += "     $i : Match( department == 'sales' ) \n";
        str += "then \n";
        str += "    list.add( kcontext.rule.name + ':' + $i.rule.name + ':' + $s  ); \n";
        str += "    kcontext.blockMatch( $i ); \n";
        str += "end \n";

        str += "rule blockerAllSalesRules3 @activationListener('direct') \n";
        str += "when \n";
        str += "     $s : String( this == 'go3' ) \n";
        str += "     $i : Match( department == 'sales' ) \n";
        str += "then \n";
        str += "    list.add( kcontext.rule.name + ':' + $i.rule.name + ':' + $s  ); \n";
        str += "    kcontext.blockMatch( $i ); \n";
        str += "end \n";

        str += "rule unblockAll @activationListener('direct') \n";
        str += "when \n";
        str += "     $s : String( this == 'go4' ) \n";
        str += "     $i : Match( department == 'sales', active == true ) \n";
        str += "then \n";
        str += "    list.add( kcontext.rule.name + ':' + $i.rule.name + ':' + $s  ); \n";
        str += "    kcontext.unblockAllMatches( $i ); \n";
        str += "end \n";

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( DeclarativeAgendaOption.ENABLED );
        KnowledgeBase kbase = loadKnowledgeBaseFromString( kconf, str );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );
        FactHandle go0 = ksession.insert( "go0" );
        FactHandle go1 = ksession.insert( "go1" );
        FactHandle go2 = ksession.insert( "go2" );
        FactHandle go3 = ksession.insert( "go3" );

        ksession.fireAllRules();
        assertEquals( 3,
                      list.size() );
        System.out.println( list );
        assertTrue( list.contains( "blockerAllSalesRules1:rule0:go1" ) );
        assertTrue( list.contains( "blockerAllSalesRules2:rule0:go2" ) );
        assertTrue( list.contains( "blockerAllSalesRules3:rule0:go3" ) );

        list.clear();

        FactHandle go4 = ksession.insert( "go4" );
        ksession.fireAllRules();
        System.out.println( list );
        assertEquals( 5,
                      list.size() );

        assertTrue( list.contains( "unblockAll:rule0:go4" ) );
        assertTrue( list.contains( "rule0:go0" ) );
        assertTrue( list.contains( "blockerAllSalesRules1:rule0:go1" ) );
        assertTrue( list.contains( "blockerAllSalesRules2:rule0:go2" ) );
        assertTrue( list.contains( "blockerAllSalesRules3:rule0:go3" ) );
    }

    @Test(timeout=10000)
    public void testIterativeUpdate() {
        String str = "";
        str += "package org.domain.test \n";
        str += "import " + Match.class.getName() + "\n";
        str += "global java.util.List list \n";
        str += "dialect 'mvel' \n";

        str += "rule rule0 \n";
        str += "when \n";
        str += "     $s : String( this == 'rule0' ) \n";
        str += "then \n";
        str += "    list.add( kcontext.rule.name ); \n";
        str += "end \n";

        str += "rule rule1 \n";
        str += "when \n";
        str += "     $s : String( this == 'rule1' ) \n";
        str += "then \n";
        str += "    list.add( kcontext.rule.name ); \n";
        str += "end \n";

        str += "rule rule2 \n";
        str += "when \n";
        str += "     $s : String( this == 'rule2' ) \n";
        str += "then \n";
        str += "    list.add( kcontext.rule.name ); \n";
        str += "end \n";

        str += "rule blockerAllSalesRules1 @activationListener('direct') \n";
        str += "when \n";
        str += "     $l : List( ) \n";
        str += "     $i : Match( rule.name == $l[0] ) \n";
        str += "then \n";
        //str += "   System.out.println( kcontext.rule.name  + ':' + $i ); \n";
        str += "    list.add( 'block:' + $i.rule.name  ); \n";
        str += "    kcontext.blockMatch( $i ); \n";
        str += "end \n";

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( DeclarativeAgendaOption.ENABLED );
        KnowledgeBase kbase = loadKnowledgeBaseFromString( kconf, str );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );

        FactHandle rule0 = ksession.insert( "rule0" );
        FactHandle rule1 = ksession.insert( "rule1" );
        FactHandle rule2 = ksession.insert( "rule2" );

        ksession.fireAllRules();
        assertEquals( 3,
                      list.size() );
        assertTrue( list.contains( "rule0" ) );
        assertTrue( list.contains( "rule1" ) );
        assertTrue( list.contains( "rule2" ) );

        list.clear();

        ArrayList l = new ArrayList();
        l.add( "rule0" );
        FactHandle lh = ksession.insert( l );
        
        ksession.update( rule0,
                         "rule0" );
        ksession.update( rule1,
                         "rule1" );
        ksession.update( rule2,
                         "rule2" );

        ksession.fireAllRules();

        assertEquals( 4,
                      list.size() );
        assertTrue( list.contains( "block:rule0" ) );
        assertTrue( list.contains( "rule1" ) );
        assertTrue( list.contains( "rule2" ) );
        assertFalse( list.contains( "rule0" ) );

        list.clear();

        ksession.update( rule0,
                         "rule0" );
        ksession.update( rule1,
                         "rule1" );
        ksession.update( rule2,
                         "rule2" );
        
        ksession.fireAllRules();
        
        assertEquals( 3,
                      list.size() );
        assertTrue( list.contains( "block:rule0" ) );
        assertTrue( list.contains( "rule1" ) );
        assertTrue( list.contains( "rule2" ) );
        assertFalse( list.contains( "rule0" ) );

        list.clear();
        
        l.set( 0,
               "rule1" );
        ksession.update( lh,
                         l );
                
        ksession.fireAllRules();

        System.out.println( list );
        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( "rule0" ) );
        assertTrue( list.contains( "block:rule1" ) );

        list.clear();

        ksession.update( rule0,
                         "rule0" );
        ksession.update( rule1,
                         "rule1" );
        ksession.update( rule2,
                         "rule2" );
        
        ksession.fireAllRules();
        
        assertEquals( 3,
                      list.size() );
        assertTrue( list.contains( "block:rule1" ) );
        assertTrue( list.contains( "rule0" ) );
        assertTrue( list.contains( "rule2" ) );
        assertFalse( list.contains( "rule1" ) );

        list.clear();

        l.set( 0,
               "rule2" );
        ksession.update( lh,
                         l );
        ksession.fireAllRules();

        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( "rule1" ) );
        assertTrue( list.contains( "block:rule2" ) );
    }

    @Test(timeout=10000)
    public void testCancelActivation() {
        String str = "";
        str += "package org.domain.test \n";
        str += "import " + Match.class.getName() + "\n";
        str += "global java.util.List list \n";
        str += "dialect 'mvel' \n";
        str += "rule rule1 @department(sales) \n";
        str += "when \n";
        str += "     $s : String( this == 'go1' ) \n";
        str += "then \n";
        str += "    list.add( kcontext.rule.name + ':' + $s ); \n";
        str += "end \n";
        str += "rule blockerAllSalesRules @activationListener('direct') \n";
        str += "when \n";
        str += "     $s : String( this == 'go2' ) \n";
        str += "     $i : Match( department == 'sales' ) \n";
        str += "then \n";
        str += "    kcontext.cancelMatch( $i ); \n";
        str += "end \n";

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( DeclarativeAgendaOption.ENABLED );
        KnowledgeBase kbase = loadKnowledgeBaseFromString( kconf, str );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        final List cancelled = new ArrayList();

        ksession.addEventListener( new AgendaEventListener() {

            public void beforeMatchFired(BeforeMatchFiredEvent event) {
            }

            public void agendaGroupPushed(AgendaGroupPushedEvent event) {
            }

            public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
            }

            public void afterMatchFired(AfterMatchFiredEvent event) {
            }

            public void matchCreated(MatchCreatedEvent event) {
            }

            public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
            }

            public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
            }

            public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
            }

            public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
            }
            
            public void matchCancelled(MatchCancelledEvent event) {
                cancelled.add( event );
            }            
        } );

        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );
        ksession.insert( "go1" );
        FactHandle go2 = ksession.insert( "go2" );
        ksession.fireAllRules();
        assertEquals( 0,
                      list.size() );

        assertEquals( 1,
                      cancelled.size() );
        assertEquals( "rule1",
                      ((MatchCancelledEvent) cancelled.get( 0 )).getMatch().getRule().getName() );
        ksession.dispose();
    }

    @Test(timeout=10000)
    public void testActiveInActiveChanges() {
        String str = "";
        str += "package org.domain.test \n";
        str += "import " + Match.class.getName() + "\n";
        str += "global java.util.List list \n";
        str += "dialect 'mvel' \n";
        str += "rule rule1 @department(sales) \n";
        str += "when \n";
        str += "     $s : String( this == 'go1' ) \n";
        str += "then \n";
        str += "    list.add( kcontext.rule.name + ':' + $s ); \n";
        str += "end \n";
        str += "rule rule2 @department(sales) \n";
        str += "when \n";
        str += "     $s : String( this == 'go1' ) \n";
        str += "then \n";
        str += "    list.add( kcontext.rule.name + ':' + $s ); \n";
        str += "end \n";
        str += "rule rule3 @department(sales) \n";
        str += "when \n";
        str += "     $s : String( this == 'go1' ) \n";
        str += "then \n";
        str += "    list.add( kcontext.rule.name + ':' + $s ); \n";
        str += "end \n";
        str += "rule countActivateInActive @activationListener('direct') \n";
        str += "when \n";
        str += "     $s : String( this == 'go2' ) \n";
        str += "     $active : Number( this == 1 ) from accumulate( $a : Match( department == 'sales', active == true ), count( $a ) )\n";
        str += "     $inActive : Number( this == 2 ) from  accumulate( $a : Match( department == 'sales', active == false ), count( $a ) )\n";
        str += "then \n";
        str += "    list.add( $active + ':' + $inActive  ); \n";
        str += "    kcontext.halt( ); \n";
        str += "end \n";

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( DeclarativeAgendaOption.ENABLED );
        KnowledgeBase kbase = loadKnowledgeBaseFromString( kconf, str );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );
        ksession.insert( "go1" );
        FactHandle go2 = ksession.insert( "go2" );
        ksession.fireAllRules();

        assertEquals( 3,
                      list.size() );
        System.out.println( list );
        assertTrue( list.contains( "1:2" ) );
        assertTrue( list.contains( "rule1:go1" ) );
        assertTrue( list.contains( "rule2:go1" ) );

        ksession.dispose();
    }

    @Test(timeout=10000)
    public void testCancelMultipleActivations() {
        String str = "package org.domain.test\n" +
                "import " + Match.class.getName() + "\n" +
                "global java.util.List list\n" +
                "rule sales1 @department('sales')\n" +
                "when\n" +
                "    String( this == 'fireRules' )\n" +
                "then\n" +
                "    list.add(\"sales1\");\n" +
                "end\n" +
                "\n" +
                "rule sales2 @department('sales') \n" +
                "when\n" +
                "    String( this == 'fireRules' )\n" +
                "then\n" +
                "    list.add(\"sales2\");\n" +
                "end\n" +
                "\n" +
                "rule salesCancel @activationListener('direct')\n" +
                "when\n" +
                "    $i : Match( department == 'sales' )\n" +
                "then\n" +
                "    kcontext.cancelMatch($i);\n" +
                "end";

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( DeclarativeAgendaOption.ENABLED );
        KnowledgeBase kbase = loadKnowledgeBaseFromString( kconf, str );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.insert("fireRules");
        ksession.fireAllRules();
        System.out.println(list);
        assertEquals(0, list.size());

        ksession.dispose();
    }

    @Test(timeout=10000)
    public void testCancelActivationOnInsertAndUpdate() {
        String str = "package org.domain.test\n" +
                "import " + Match.class.getName() + "\n" +
                "global java.util.List list\n" +
                "rule sales1 @department('sales') @category('special')\n" +
                "salience 10\n" +
                "when\n" +
                "    String( this == 'fireRules' )\n" +
                "then\n" +
                "    list.add(\"sales1\");\n" +
                "end\n" +
                "\n" +
                "rule sales2 @department('sales') \n" +
                "when\n" +
                "    String( this == 'fireRules' )\n" +
                "then\n" +
                "    list.add(\"sales2\");\n" +
                "end\n" +
                "\n" +
                "rule salesCancel @activationListener('direct')\n" +
                "when\n" +
                "    String(this == 'fireCancelRule')\n" +
                "    $i : Match( department == 'sales', category == 'special' )\n" +
                "then\n" +
                "    kcontext.cancelMatch($i);\n" +
                "end";

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( DeclarativeAgendaOption.ENABLED );
        KnowledgeBase kbase = loadKnowledgeBaseFromString( kconf, str );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        FactHandle fireRules = ksession.insert("fireRules");
        FactHandle fireCancelRule = ksession.insert("fireCancelRule");
        ksession.fireAllRules();
        assertEquals(1, list.size());

        ksession.update(fireRules, "fireRules");
        ksession.update(fireCancelRule, "fireCancelRule");
        ksession.fireAllRules();
        
        assertEquals(2, list.size());

        ksession.dispose();
    }

    @Test
    public void testFiredRuleDoNotRefireAfterUnblock() {
        // BZ-1038076
        String drl =
                "package org.drools.compiler.integrationtests\n" +
                "\n" +
                "import org.kie.api.runtime.rule.Match\n" +
                "import java.util.List\n" +
                "\n" +
                "global List list\n" +
                "\n" +
                "rule startAgenda\n" +
                "salience 100\n" +
                "when\n" +
                "    String( this == 'startAgenda' )\n" +
                "then\n" +
                "    drools.getKnowledgeRuntime().getAgenda().getAgendaGroup(\"agenda\").setFocus();\n" +
                "    list.add(kcontext.getRule().getName());\n" +
                "end\n" +
                "\n" +
                "rule sales @department('sales')\n" +
                "agenda-group \"agenda\"\n" +
                "when\n" +
                "    $s : String( this == 'fireRules' )\n" +
                "then\n" +
                "    list.add(kcontext.getRule().getName());\n" +
                "end\n" +
                "\n" +
                "rule salesBlocker salience 10\n" +
                "when\n" +
                "    $s : String( this == 'fireBlockerRule' )\n" +
                "    $i : Match( department == 'sales' )\n" +
                "then\n" +
                "    kcontext.blockMatch( $i );\n" +
                "    list.add(kcontext.getRule().getName());\n" +
                "end\n";

        final KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        KieModuleModel kmodule = ks.newKieModuleModel();

        KieBaseModel baseModel = kmodule.newKieBaseModel("defaultKBase")
                                        .setDefault(true)
                                        .setDeclarativeAgenda(DeclarativeAgendaOption.ENABLED);
        baseModel.newKieSessionModel("defaultKSession")
                 .setDefault(true);

        kfs.writeKModuleXML(kmodule.toXML());
        kfs.write("src/main/resources/block_rule.drl", drl);
        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        assertEquals( 0, kieBuilder.getResults().getMessages( org.kie.api.builder.Message.Level.ERROR ).size() );

        KieSession ksession = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).newKieSession();

        List<String> list = new ArrayList<String>();

        ksession.setGlobal("list", list);

        // first run
        ksession.insert("startAgenda");
        ksession.insert("fireRules");
        FactHandle fireBlockerRule = ksession.insert("fireBlockerRule");
        ksession.fireAllRules();
        String[] expected = { "startAgenda", "sales", "salesBlocker" };

        assertEquals(expected.length, list.size());
        for (int i = 0; i < list.size(); i++) {
            assertEquals(expected[i], list.get(i));
        }

        // second run
        ksession.delete(fireBlockerRule);
        list.clear();
        ksession.fireAllRules();
        assertEquals(0, list.size());

        ksession.dispose();
        list.clear();
    }

    @Test
    public void testExplicitUndercutWithDeclarativeAgenda() {

        String drl = "package org.drools.test;\n" +
                     "\n" +
                     "import org.kie.api.runtime.rule.Match;\n" +
                     "\n" +
                     "global java.util.List list;\n" +
                     "\n" +
                     "declare Foo\n" +
                     "    type  : String\n" +
                     "    value : double\n" +
                     "end\n" +
                     "\n" +
                     "declare Bar\n" +
                     "    type  : String\n" +
                     "    total : double\n" +
                     "end\n" +
                     "\n" +
                     "\n" +
                     "rule \"Init\"\n" +
                     "when\n" +
                     "then\n" +
                     "    insert( new Foo( \"first\", 10 ) );\n" +
                     "    insert( new Foo( \"first\", 11 ) );\n" +
                     "    insert( new Foo( \"second\", 20 ) );\n" +
                     "    insert( new Foo( \"second\", 22 ) );\n" +
                     "    insert( new Foo( \"third\", 30 ) );\n" +
                     "    insert( new Foo( \"third\", 40 ) );\n" +
                     "end\n" +
                     "\n" +
                     "rule \"Accumulate\"\n" +
                     "salience 100\n" +
                     "dialect \"mvel\"\n" +
                     "  when\n" +
                     "    $type : String() from [ \"first\", \"second\", \"third\" ]\n" +
                     "    accumulate ( Foo( type == $type, $value : value ),\n" +
                     "                 $total : sum( $value );\n" +
                     "                 $total > 0 )\n" +
                     "  then\n" +
                     "    insert(new Bar($type, $total));\n" +
                     "end\n" +
                     "\n" +
                     "rule \"handle all Bars of type first\"\n" +
                     "@Undercuts( others )\n" +
                     "  when\n" +
                     "    $bar : Bar( type == 'first', $total : total )\n" +
                     "  then\n" +
                     "    System.out.println( \"First bars \" + $total );\n" +
                     "    list.add( $total );\n" +
                     "end\n" +
                     "\n" +
                     "rule \"handle all Bars of type second\"\n" +
                     "@Undercuts( others )\n" +
                     "  when\n" +
                     "    $bar : Bar( type == 'second', $total : total )\n" +
                     "  then\n" +
                     "    System.out.println( \"Second bars \" + $total );\n" +
                     "    list.add( $total );\n" +
                     "end\n" +
                     "\n" +
                     "rule \"others\"\n" +
                     "  when\n" +
                     "    $bar : Bar( $total : total )\n" +
                     "  then\n" +
                     "    System.out.println( \"Other bars \" + $total );\n" +
                     "    list.add( $total );\n" +
                     "end\n" +
                     "\n" +
                     "\n" +
                     "rule \"Undercut\"\n" +
                     "@Direct \n" +
                     "when\n" +
                     "    $m : Match( $handles : factHandles )\n" +
                     "    $v : Match( rule.name == $m.Undercuts, factHandles == $handles )\n" +
                     "then\n" +
                     "    System.out.println( \"Activation of rule \" + $m.getRule().getName() + \" overrides \" + $v.getRule().getName() + \" for tuple \" + $handles );\n" +
                     "    kcontext.cancelMatch( $v );\n" +
                     "end\n" +
                     "\n" +
                     "\n";

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( DeclarativeAgendaOption.ENABLED );
        KnowledgeBase kbase = loadKnowledgeBaseFromString( kconf, drl );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        assertEquals( Arrays.asList( 21.0, 42.0, 70.0 ), list );

        ksession.dispose();
    }

}
