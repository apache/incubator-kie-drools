package org.drools.integrationtests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.builder.conf.DeclarativeAgendaOption;
import org.drools.common.AgendaItem;
import org.drools.event.rule.ActivationCancelledEvent;
import org.drools.event.rule.ActivationCreatedEvent;
import org.drools.event.rule.AfterActivationFiredEvent;
import org.drools.event.rule.AgendaEventListener;
import org.drools.event.rule.AgendaGroupPoppedEvent;
import org.drools.event.rule.AgendaGroupPushedEvent;
import org.drools.event.rule.BeforeActivationFiredEvent;
import org.drools.event.rule.RuleFlowGroupActivatedEvent;
import org.drools.event.rule.RuleFlowGroupDeactivatedEvent;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.Activation;
import org.drools.runtime.rule.FactHandle;
import org.junit.Test;

public class DeclarativeAgendaTest {

    @Test
    public void testBasicBlockOnAnnotation() {
        String str = "";
        str += "package org.domain.test \n";
        str += "import " + Activation.class.getName() + "\n";
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
        str += "     $i : Activation( department == 'sales' ) \n";
        str += "then \n";
        str += "    list.add( $i.rule.name + ':' + $s  ); \n";
        str += "    kcontext.blockActivation( $i ); \n";
        str += "end \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( DeclarativeAgendaOption.ENABLED );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( kconf );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
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

    @Test
    public void testApplyBlockerFirst() {
        StatefulKnowledgeSession ksession = getStatefulKnowledgeSession();

        List list = new ArrayList();
        ksession.setGlobal( "list",
                list );
        FactHandle go2 = ksession.insert( "go2" );
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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
        str += "import " + Activation.class.getName() + "\n";
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
        str += "     $i : Activation( department == 'sales' ) \n";
        str += "then \n";
        str += "    list.add( $i.rule.name + ':' + $s  ); \n";
        str += "    kcontext.blockActivation( $i ); \n";
        str += "end \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( DeclarativeAgendaOption.ENABLED );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( kconf );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        return ksession;
    }

    @Test
    public void testMultipleBlockers() {
        String str = "";
        str += "package org.domain.test \n";
        str += "import " + Activation.class.getName() + "\n";
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
        str += "     $i : Activation( department == 'sales' ) \n";
        str += "then \n";
        str += "    list.add( $i.rule.name + ':' + $s  ); \n";
        str += "    kcontext.blockActivation( $i ); \n";
        str += "end \n";

        str += "rule blockerAllSalesRules2 @activationListener('direct') \n";
        str += "when \n";
        str += "     $s : String( this == 'go2' ) \n";
        str += "     $i : Activation( department == 'sales' ) \n";
        str += "then \n";
        str += "    list.add( $i.rule.name + ':' + $s  ); \n";
        str += "    kcontext.blockActivation( $i ); \n";
        str += "end \n";

        str += "rule blockerAllSalesRules3 @activationListener('direct') \n";
        str += "when \n";
        str += "     $s : String( this == 'go3' ) \n";
        str += "     $i : Activation( department == 'sales' ) \n";
        str += "then \n";
        str += "    list.add( $i.rule.name + ':' + $s  ); \n";
        str += "    kcontext.blockActivation( $i ); \n";
        str += "end \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( DeclarativeAgendaOption.ENABLED );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( kconf );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
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

    @Test
    public void testMultipleBlockersWithUnblockAll() {
        // This test is a bit wierd as it recurses. Maybe unblockAll is not feasible...
        String str = "";
        str += "package org.domain.test \n";
        str += "import " + Activation.class.getName() + "\n";
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
        str += "     $i : Activation( department == 'sales' ) \n";
        str += "then \n";
        str += "    list.add( kcontext.rule.name + ':' + $i.rule.name + ':' + $s  ); \n";
        str += "    kcontext.blockActivation( $i ); \n";
        str += "end \n";

        str += "rule blockerAllSalesRules2 @activationListener('direct') \n";
        str += "when \n";
        str += "     $s : String( this == 'go2' ) \n";
        str += "     $i : Activation( department == 'sales' ) \n";
        str += "then \n";
        str += "    list.add( kcontext.rule.name + ':' + $i.rule.name + ':' + $s  ); \n";
        str += "    kcontext.blockActivation( $i ); \n";
        str += "end \n";

        str += "rule blockerAllSalesRules3 @activationListener('direct') \n";
        str += "when \n";
        str += "     $s : String( this == 'go3' ) \n";
        str += "     $i : Activation( department == 'sales' ) \n";
        str += "then \n";
        str += "    list.add( kcontext.rule.name + ':' + $i.rule.name + ':' + $s  ); \n";
        str += "    kcontext.blockActivation( $i ); \n";
        str += "end \n";

        str += "rule unblockAll @activationListener('direct') \n";
        str += "when \n";
        str += "     $s : String( this == 'go4' ) \n";
        str += "     $i : Activation( department == 'sales', active == true ) \n";
        str += "then \n";
        str += "    list.add( kcontext.rule.name + ':' + $i.rule.name + ':' + $s  ); \n";
        str += "    kcontext.unblockAllActivations( $i ); \n";
        str += "end \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( DeclarativeAgendaOption.ENABLED );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( kconf );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
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
        assertEquals( 5,
                list.size() );

        assertTrue( list.contains( "unblockAll:rule0:go4" ) );
        assertTrue( list.contains( "rule0:go0" ) );
        assertTrue( list.contains( "blockerAllSalesRules1:rule0:go1" ) );
        assertTrue( list.contains( "blockerAllSalesRules2:rule0:go2" ) );
        assertTrue( list.contains( "blockerAllSalesRules3:rule0:go3" ) );
    }

    @Test
    public void testIterativeUpdate() {
        String str = "";
        str += "package org.domain.test \n";
        str += "import " + Activation.class.getName() + "\n";
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
        str += "     $i : Activation( rule.name == $l[0] ) \n";
        str += "then \n";
        //str += "   System.out.println( kcontext.rule.name  + ':' + $i ); \n";
        str += "    list.add( 'block:' + $i.rule.name  ); \n";
        str += "    kcontext.blockActivation( $i ); \n";
        str += "end \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( DeclarativeAgendaOption.ENABLED );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( kconf );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
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
        ksession.update( rule0,
                "rule0" );
        ksession.update( rule1,
                "rule1" );
        ksession.update( rule2,
                "rule2" );

        l.add( "rule0" );
        FactHandle lh = ksession.insert( l );

        ksession.fireAllRules();

        assertEquals( 3,
                list.size() );
        assertTrue( list.contains( "block:rule0" ) );
        assertTrue( list.contains( "rule1" ) );
        assertTrue( list.contains( "rule2" ) );

        list.clear();

        ksession.update( rule0,
                "rule0" );
        ksession.update( rule1,
                "rule1" );
        ksession.update( rule2,
                "rule2" );
        assertEquals( 1,
                list.size() );
        assertTrue( list.contains( "block:rule0" ) );

        list.clear();

        l.set( 0,
                "rule1" );
        ksession.update( lh,
                l );
        ksession.fireAllRules();

        assertEquals( 3,
                list.size() );
        assertTrue( list.contains( "rule0" ) );
        assertTrue( list.contains( "block:rule1" ) );
        assertTrue( list.contains( "rule2" ) );

        list.clear();

        ksession.update( rule0,
                "rule0" );
        ksession.update( rule1,
                "rule1" );
        ksession.update( rule2,
                "rule2" );
        assertEquals( 1,
                list.size() );
        assertTrue( list.contains( "block:rule1" ) );

        list.clear();

        l.set( 0,
                "rule2" );
        ksession.update( lh,
                l );
        ksession.fireAllRules();

        assertEquals( 3,
                list.size() );
        assertTrue( list.contains( "rule0" ) );
        assertTrue( list.contains( "rule1" ) );
        assertTrue( list.contains( "block:rule2" ) );
    }

    @Test
    public void testCancelActivation() {
        String str = "";
        str += "package org.domain.test \n";
        str += "import " + Activation.class.getName() + "\n";
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
        str += "     $i : Activation( department == 'sales' ) \n";
        str += "then \n";
        str += "    kcontext.cancelActivation( $i ); \n";
        str += "end \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( DeclarativeAgendaOption.ENABLED );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( kconf );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        final List cancelled = new ArrayList();

        ksession.addEventListener( new AgendaEventListener() {

            public void beforeActivationFired(BeforeActivationFiredEvent event) {
            }

            public void agendaGroupPushed(AgendaGroupPushedEvent event) {
            }

            public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
            }

            public void afterActivationFired(AfterActivationFiredEvent event) {
            }

            public void activationCreated(ActivationCreatedEvent event) {
            }

            public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
            }

            public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
            }

            public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
            }

            public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
            }

            public void activationCancelled(ActivationCancelledEvent event) {
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
                ((ActivationCancelledEvent) cancelled.get( 0 )).getActivation().getRule().getName() );
        ksession.dispose();
    }

    @Test
    public void testActiveInActiveChanges() {
        String str = "";
        str += "package org.domain.test \n";
        str += "import " + Activation.class.getName() + "\n";
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
        str += "     $active : Number( this == 1 ) from accumulate( $a : Activation( department == 'sales', active == true ), count( $a ) )\n";
        str += "     $inActive : Number( this == 2 ) from  accumulate( $a : Activation( department == 'sales', active == false ), count( $a ) )\n";
        str += "then \n";
        str += "    list.add( $active + ':' + $inActive  ); \n";
        str += "    kcontext.halt( ); \n";
        str += "end \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( DeclarativeAgendaOption.ENABLED );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( kconf );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        ksession.setGlobal( "list",
                list );
        ksession.insert( "go1" );
        FactHandle go2 = ksession.insert( "go2" );
        ksession.fireAllRules();

        assertEquals( 3,
                list.size() );
        assertTrue( list.contains( "1:2" ) );
        assertTrue( list.contains( "rule2:go1" ) );
        assertTrue( list.contains( "rule3:go1" ) );

        ksession.dispose();
    }

    @Test
    public void testCancelMultipleActivations() {
        String str = "package org.domain.test\n" +
                "import " + Activation.class.getName() + "\n" +
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
                "    $i : Activation( department == 'sales' )\n" +
                "then\n" +
                "    kcontext.cancelActivation($i);\n" +
                "end";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( DeclarativeAgendaOption.ENABLED );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( kconf );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.insert("fireRules");
        ksession.fireAllRules();
        System.out.println(list);
        assertEquals(0, list.size());

        ksession.dispose();
    }

    @Test
    public void testCancelActivationOnInsertAndUpdate() {
        String str = "package org.domain.test\n" +
                "import " + Activation.class.getName() + "\n" +
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
                "    $i : Activation( department == 'sales', category == 'special' )\n" +
                "then\n" +
                "    kcontext.cancelActivation($i);\n" +
                "end";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( DeclarativeAgendaOption.ENABLED );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( kconf );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

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
}
