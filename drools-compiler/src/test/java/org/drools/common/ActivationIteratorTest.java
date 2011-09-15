package org.drools.common;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.Activation;
import org.junit.Test;

public class ActivationIteratorTest {
    @Test
    public void testSingleLian() {
        String str = "package org.drools.test \n" +
                     "\n" +
                     "rule rule1 when\n" +
                     "    $s : String( this != 'xx' )\n" +
                     "then\n" +
                     "end\n" +
                     "rule rule6 when\n" +
                     "     java.util.Map()\n" +
                     "then\n" +
                     "end\n" +
                     "\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        for ( int i = 0; i < 5; i++ ) {
            ksession.insert( new String( "" + i ) );
        }

        ActivationIterator it = ActivationIterator.iterator( ksession );
        List list = new ArrayList();
        for ( Activation act = (Activation) it.next(); act != null; act = (Activation) it.next() ) {
            list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s" ) + ":" + act.isActive() );
        }
        assertContains( new String[]{"rule1:0:true", "rule1:1:true", "rule1:2:true", "rule1:3:true", "rule1:4:true"},
                        list );

        ksession.fireAllRules();

        it = ActivationIterator.iterator( ksession );
        list = new ArrayList();
        for ( Activation act = (Activation) it.next(); act != null; act = (Activation) it.next() ) {
            list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s" ) + ":" + act.isActive() );
        }
        assertContains( new String[]{"rule1:0:false", "rule1:1:false", "rule1:2:false", "rule1:3:false", "rule1:4:false"},
                        list );
    }

    @Test
    public void testLianPlusEvaln() {
        String str = "package org.drools.test \n" +
                     "\n" +
                     "rule rule1 when\n" +
                     "    $s : String( this != 'xx' )\n" +
                     "    eval( 1 == 1 ) \n" +
                     "then\n" +
                     "end\n" +
                     "rule rule6 when\n" +
                     "     java.util.Map()\n" +
                     "then\n" +
                     "end\n" +
                     "\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        for ( int i = 0; i < 5; i++ ) {
            ksession.insert( new String( "" + i ) );
        }

        ActivationIterator it = ActivationIterator.iterator( ksession );
        List list = new ArrayList();
        for ( Activation act = (Activation) it.next(); act != null; act = (Activation) it.next() ) {
            list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s" ) + ":" + act.isActive() );
        }
        assertContains( new String[]{"rule1:0:true", "rule1:1:true", "rule1:2:true", "rule1:3:true", "rule1:4:true"},
                        list );

        ksession.fireAllRules();

        it = ActivationIterator.iterator( ksession );
        list = new ArrayList();
        for ( Activation act = (Activation) it.next(); act != null; act = (Activation) it.next() ) {
            list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s" ) + ":" + act.isActive() );
        }
        assertContains( new String[]{"rule1:0:false", "rule1:1:false", "rule1:2:false", "rule1:3:false", "rule1:4:false"},
                        list );
    }

    @Test
    public void testLianPlusEvalnWithSharing() {
        // Rule 0 single LiaNode
        // Rule 1 and 2 are shared
        // Rule 3 shares the LIANode with 1 and 2    
        // Rule 4 Shares the eval with 3
        String str = "package org.drools.test \n" +
                     "\n" +
                     "rule rule0 when\n" +
                     "    $s : String( this != 'xx' )\n" +
                     "then\n" +
                     "end\n" +
                     "rule rule1 when\n" +
                     "    $s : String( this != 'xx' )\n" +
                     "    eval( Integer.parseInt( $s ) <= 2 ) \n" +
                     "then\n" +
                     "end\n" +
                     "rule rule2 when\n" +
                     "    $s : String( this != 'xx' )\n" +
                     "    eval( Integer.parseInt( $s ) <= 2 ) \n" +
                     "then\n" +
                     "end\n" +
                     "rule rule3 when\n" +
                     "    $s : String( this != 'xx' )\n" +
                     "    eval( Integer.parseInt( $s ) > 2 ) \n" +
                     "then\n" +
                     "end\n" +
                     "rule rule4 when\n" +
                     "    $s : String( this != 'xx' )\n" +
                     "    eval( Integer.parseInt( $s ) > 2 ) \n" +
                     "    eval( Integer.parseInt( $s ) > 3 ) \n" +
                     "then\n" +
                     "end\n" +
                     "rule rule5 when\n" +
                     "    $s : String( this != 'xx' )\n" +
                     "    eval( Integer.parseInt( $s ) > 2 ) \n" +
                     "    eval( Integer.parseInt( $s ) > 3 ) \n" +
                     "then\n" +
                     "end\n" +
                     "rule rule6 when\n" +
                     "     java.util.Map()\n" +
                     "then\n" +
                     "end\n" +
                     "\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        for ( int i = 0; i < 5; i++ ) {
            ksession.insert( new String( "" + i ) );
        }

        ActivationIterator it = ActivationIterator.iterator( ksession );

        List list = new ArrayList();
        for ( Activation act = (Activation) it.next(); act != null; act = (Activation) it.next() ) {
            list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s" ) + ":" + act.isActive() );
        }
        assertContains( new String[]{"rule0:0:true", "rule0:1:true", "rule0:2:true", "rule0:3:true", "rule0:4:true",
                                "rule1:0:true", "rule1:1:true", "rule1:2:true", "rule2:0:true", "rule2:1:true", "rule2:2:true",
                                "rule3:3:true", "rule3:4:true",
                                "rule3:4:true"},
                        list );

        ksession.fireAllRules();

        it = ActivationIterator.iterator( ksession );
        list = new ArrayList();
        for ( Activation act = (Activation) it.next(); act != null; act = (Activation) it.next() ) {
            list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s" ) + ":" + act.isActive() );
        }
        assertContains( new String[]{"rule0:0:false", "rule0:1:false", "rule0:2:false", "rule0:3:false", "rule0:4:false",
                                "rule1:0:false", "rule1:1:false", "rule1:2:false", "rule2:0:false", "rule2:1:false", "rule2:2:false",
                                "rule3:3:false", "rule3:4:false",
                                "rule3:4:false"},
                        list );
    }

    @Test
    public void testLianPlusEvalnWithSharingWithMixedDormantAndActive() {
        // Rule 0 single LiaNode
        // Rule 1 and 2 are shared
        // Rule 3 shares the LIANode with 1 and 2    
        // Rule 4 Shares the eval with 3
        String str = "package org.drools.test \n" +
                     "\n" +
                     "rule rule0 salience ( Integer.parseInt('1'+$s) ) when\n" +
                     "    $s : String( this != 'xx' )\n" +
                     "then\n" +
                     "end\n" +
                     "rule rule1 salience ( Integer.parseInt('1'+$s)) when\n" +
                     "    $s : String( this != 'xx' )\n" +
                     "    eval( Integer.parseInt( $s ) <= 2 ) \n" +
                     "then\n" +
                     "end\n" +
                     "rule rule2 salience ( Integer.parseInt('1'+$s)) when\n" +
                     "    $s : String( this != 'xx' )\n" +
                     "    eval( Integer.parseInt( $s ) <= 2 ) \n" +
                     "then\n" +
                     "    kcontext.getKnowledgeRuntime().halt();\n" +
                     "end\n" +
                     "rule rule3 salience ( Integer.parseInt('1'+$s)) when\n" +
                     "    $s : String( this != 'xx' )\n" +
                     "    eval( Integer.parseInt( $s ) > 2 ) \n" +
                     "then\n" +
                     "end\n" +
                     "rule rule4 salience ( Integer.parseInt('1'+$s) ) when\n" +
                     "    $s : String( this != 'xx' )\n" +
                     "    eval( Integer.parseInt( $s ) > 2 ) \n" +
                     "    eval( Integer.parseInt( $s ) > 3 ) \n" +
                     "then\n" +
                     "end\n" +
                     "\n" +
                     "rule rule6 when\n" +
                     "     java.util.Map()\n" +
                     "then\n" +
                     "end\n" +
                     "\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        for ( int i = 0; i < 5; i++ ) {
            ksession.insert( new String( "" + i ) );
        }
        ksession.fireAllRules();

        ActivationIterator it = ActivationIterator.iterator( ksession );

        List list = new ArrayList();
        for ( Activation act = (Activation) it.next(); act != null; act = (Activation) it.next() ) {
            list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s" ) + ":" + act.isActive() );
        }
        assertContains( new String[]{"rule0:0:true", "rule0:1:true", "rule0:2:true", "rule0:3:false", "rule0:4:false",
                                "rule1:0:true", "rule1:1:true", "rule1:2:true", "rule2:0:true", "rule2:1:true", "rule2:2:false",
                                "rule3:3:false", "rule3:4:false",
                                "rule3:4:false"},
                        list );
    }

    @Test
    public void testSingleJoinNode() {
        String str = "package org.drools.test \n" +
                     "\n" +
                     "rule rule1 when\n" +
                     "    $s1 : String( )\n" +
                     "    $s2 : String( )\n" +
                     "then\n" +
                     "end\n" +
                     "\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        for ( int i = 0; i < 2; i++ ) {
            ksession.insert( new String( "" + i ) );
        }

        ActivationIterator it = ActivationIterator.iterator( ksession );
        List list = new ArrayList();
        for ( Activation act = (Activation) it.next(); act != null; act = (Activation) it.next() ) {
            list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s1" ) + ":" + act.getDeclarationValue( "$s2" ) + ":" + act.isActive() );
        }
        assertContains( new String[]{"rule1:0:1:true", "rule1:1:0:true", "rule1:1:1:true", "rule1:0:0:true"},
                        list );

        ksession.fireAllRules();

        it = ActivationIterator.iterator( ksession );
        list = new ArrayList();
        for ( Activation act = (Activation) it.next(); act != null; act = (Activation) it.next() ) {
            list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s1" ) + ":" + act.getDeclarationValue( "$s2" ) + ":" + act.isActive() );
        }
        assertContains( new String[]{"rule1:0:1:false", "rule1:1:0:false", "rule1:1:1:false", "rule1:0:0:false"},
                        list );
    }

    @Test
    public void testSingleJoinNodePlusEvaln() {
        String str = "package org.drools.test \n" +
                     "\n" +
                     "rule rule1 when\n" +
                     "    $s1 : String( )\n" +
                     "    $s2 : String( )\n" +
                     "    eval( 1 == 1 ) \n" +
                     "then\n" +
                     "end\n" +
                     "\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        for ( int i = 0; i < 2; i++ ) {
            ksession.insert( new String( "" + i ) );
        }

        ActivationIterator it = ActivationIterator.iterator( ksession );
        List list = new ArrayList();
        for ( Activation act = (Activation) it.next(); act != null; act = (Activation) it.next() ) {
            list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s1" ) + ":" + act.getDeclarationValue( "$s2" ) + ":" + act.isActive() );
        }
        assertContains( new String[]{"rule1:0:1:true", "rule1:1:0:true", "rule1:1:1:true", "rule1:0:0:true"},
                        list );

        ksession.fireAllRules();

        it = ActivationIterator.iterator( ksession );
        list = new ArrayList();
        for ( Activation act = (Activation) it.next(); act != null; act = (Activation) it.next() ) {
            list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s1" ) + ":" + act.getDeclarationValue( "$s2" ) + ":" + act.isActive() );
        }
        assertContains( new String[]{"rule1:0:1:false", "rule1:1:0:false", "rule1:1:1:false", "rule1:0:0:false"},
                        list );
    }

    @Test
    public void testSingleJoinNodePlusEvalnWithSharing() {
        String str = "package org.drools.test \n" +
                     "\n" +
                     "rule rule1 when\n" +
                     "    $s1 : String( )\n" +
                     "    $s2 : String( )\n" +
                     "    eval( 1 == 1 ) \n" +
                     "then\n" +
                     "end\n" +
                     "rule rule2 when\n" +
                     "    $s1 : String( )\n" +
                     "then\n" +
                     "end\n" +
                     "rule rule3 when\n" +
                     "    $s1 : String( )\n" +
                     "    $s2 : String( )\n" +
                     "    $s3 : String( )\n" +
                     "    eval( 1 == 1 ) \n" +
                     "then\n" +
                     "end\n" +
                     "\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        for ( int i = 0; i < 2; i++ ) {
            ksession.insert( new String( "" + i ) );
        }

        ActivationIterator it = ActivationIterator.iterator( ksession );
        List list = new ArrayList();
        for ( Activation act = (Activation) it.next(); act != null; act = (Activation) it.next() ) {
            if ( act.getRule().getName().equals( "rule3" ) ) {
                list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s1" ) + ":" + act.getDeclarationValue( "$s2" ) + ":" + act.getDeclarationValue( "$s3" ) + ":" + act.isActive() );
            } else if ( act.getRule().getName().equals( "rule1" ) ) {
                list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s1" ) + ":" + act.getDeclarationValue( "$s2" ) + ":" + act.isActive() );
            } else if ( act.getRule().getName().equals( "rule2" ) ) {
                list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s1" ) + ":" + act.isActive() );
            }
        }

        assertContains( new String[]{"rule1:0:0:true", "rule1:0:1:true", "rule1:1:0:true", "rule1:1:1:true",
                                "rule2:1:true", "rule2:0:true",
                                "rule3:0:0:0:true", "rule3:0:0:1:true", "rule3:1:0:0:true", "rule3:1:0:1:true", "rule3:0:1:0:true", "rule3:0:1:1:true", "rule3:1:1:0:true", "rule3:1:1:1:true"},
                        list );

        ksession.fireAllRules();

        it = ActivationIterator.iterator( ksession );
        list = new ArrayList();
        for ( Activation act = (Activation) it.next(); act != null; act = (Activation) it.next() ) {
            if ( act.getRule().getName().equals( "rule3" ) ) {
                list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s1" ) + ":" + act.getDeclarationValue( "$s2" ) + ":" + act.getDeclarationValue( "$s3" ) + ":" + act.isActive() );
            } else if ( act.getRule().getName().equals( "rule1" ) ) {
                list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s1" ) + ":" + act.getDeclarationValue( "$s2" ) + ":" + act.isActive() );
            } else if ( act.getRule().getName().equals( "rule2" ) ) {
                list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s1" ) + ":" + act.isActive() );
            }
        }
        assertContains( new String[]{"rule1:0:0:false", "rule1:0:1:false", "rule1:1:0:false", "rule1:1:1:false",
                                "rule2:1:false", "rule2:0:false",
                                "rule3:0:0:0:false", "rule3:0:0:1:false", "rule3:1:0:0:false", "rule3:1:0:1:false", "rule3:0:1:0:false", "rule3:0:1:1:false", "rule3:1:1:0:false", "rule3:1:1:1:false"},
                        list );
    }

    @Test
    public void testSingleJoinNodePlusEvalnWithSharingWithMixedDormantAndActive() {
        String str = "package org.drools.test \n" +
                     "\n" +
                     "rule rule1  salience ( Integer.parseInt( '1'+$s1+'0'+$s2 ) ) when\n" +
                     "    $s1 : String( )\n" +
                     "    $s2 : String( )\n" +
                     "    eval( 1 == 1 ) \n" +
                     "then\n" +
                     "end\n" +
                     "rule rule2 salience 1020 when\n" +
                     "    $s1 : String( )\n" +
                     "then\n" +
                     "    kcontext.getKnowledgeRuntime().halt();\n" +
                     "end\n" +
                     "rule rule3  salience ( Integer.parseInt( '1'+$s1+'1'+$s2  ) ) when\n" +
                     "    $s1 : String( )\n" +
                     "    $s2 : String( )\n" +
                     "    $s3 : String( )\n" +
                     "    eval( 1 == 1 ) \n" +
                     "then\n" +
                     "end\n" +
                     "\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        for ( int i = 0; i < 2; i++ ) {
            ksession.insert( new String( "" + i ) );
        }

        ksession.fireAllRules();

        ActivationIterator it = ActivationIterator.iterator( ksession );
        List list = new ArrayList();
        for ( Activation act = (Activation) it.next(); act != null; act = (Activation) it.next() ) {
            if ( act.getRule().getName().equals( "rule3" ) ) {
                list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s1" ) + ":" + act.getDeclarationValue( "$s2" ) + ":" + act.getDeclarationValue( "$s3" ) + ":" + act.isActive() );
            } else if ( act.getRule().getName().equals( "rule1" ) ) {
                list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s1" ) + ":" + act.getDeclarationValue( "$s2" ) + ":" + act.isActive() );
            } else if ( act.getRule().getName().equals( "rule2" ) ) {
                list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s1" ) + ":" + act.isActive() );
            }
        }

        assertContains( new String[]{"rule1:0:0:true", "rule1:0:1:true", "rule1:1:0:false", "rule1:1:1:false",
                                "rule2:1:false", "rule2:0:true",
                                "rule3:0:0:0:true", "rule3:0:0:1:true", "rule3:1:0:0:false", "rule3:1:0:1:false", "rule3:0:1:0:true", "rule3:0:1:1:true", "rule3:1:1:0:false", "rule3:1:1:1:false"},
                        list );
    }

    @Test
    public void testNotSharingWithMixedDormantAndActive() {
        String str = "package org.drools.test \n" +
                     "\n" +
                     "rule rule1 salience 10 when\n" +
                     "    not String( this == '1' )\n" +
                     "then\n" +
                     "end\n" +
                     "rule rule2  salience ( Integer.parseInt( $s1+'1' ) ) when\n" +
                     "    not String( this == '1' )\n" +
                     "    $s1 : String( )\n" +
                     "    eval( 1 == 1 ) \n" +
                     "then\n" +
                     "end\n" +
                     "rule rule3 salience ( Integer.parseInt( $s1+'2' ) ) when\n" +
                     "    $s1 : String( )\n" +
                     "    not String( this == '1' )\n" +
                     "    eval( 1 == 1 ) \n" +
                     "    eval( 1 == 1 ) \n" +
                     "then\n" +
                     "    kcontext.getKnowledgeRuntime().halt();\n" +
                     "end\n" +
                     "\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.insert( "0" );
        ksession.insert( "2" );

        ksession.fireAllRules();

        ActivationIterator it = ActivationIterator.iterator( ksession );
        List list = new ArrayList();
        for ( Activation act = (Activation) it.next(); act != null; act = (Activation) it.next() ) {
            if ( act.getRule().getName().equals( "rule3" ) ) {
                list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s1" ) + ":" + act.isActive() );
            } else if ( act.getRule().getName().equals( "rule1" ) ) {
                list.add( act.getRule().getName() + ":" + act.isActive() );
            } else if ( act.getRule().getName().equals( "rule2" ) ) {
                list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s1" ) + ":" + act.isActive() );
            }
        }

        assertContains( new String[]{"rule1:true", "rule2:0:true", "rule2:2:true", "rule3:0:true", "rule3:2:false"},
                        list );
    }

    @Test
    public void testExistsSharingWithMixedDormantAndActive() {
        String str = "package org.drools.test \n" +
                     "\n" +
                     "rule rule1 salience 100 when\n" +
                     "    exists String( this == '1' )\n" +
                     "then\n" +
                     "end\n" +
                     "rule rule2  salience ( Integer.parseInt( $s1+'1' ) ) when\n" +
                     "    exists String( this == '1' )\n" +
                     "    $s1 : String( )\n" +
                     "    eval( 1 == 1 ) \n" +
                     "then\n" +
                     "end\n" +
                     "rule rule3 salience ( Integer.parseInt( $s1+'1' ) ) when\n" +
                     "    $s1 : String( )\n" +
                     "    exists String( this == '1' )\n" +
                     "    eval( 1 == 1 ) \n" +
                     "    eval( 1 == 1 ) \n" +
                     "then\n" +
                     "    kcontext.getKnowledgeRuntime().halt();\n" +
                     "end\n" +
                     "\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.insert( "0" );
        ksession.insert( "1" );
        ksession.insert( "2" );

        ksession.fireAllRules();

        ActivationIterator it = ActivationIterator.iterator( ksession );
        List list = new ArrayList();
        for ( Activation act = (Activation) it.next(); act != null; act = (Activation) it.next() ) {
            if ( act.getRule().getName().equals( "rule3" ) ) {
                list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s1" ) + ":" + act.isActive() );
            } else if ( act.getRule().getName().equals( "rule1" ) ) {
                list.add( act.getRule().getName() + ":" + act.isActive() );
            } else if ( act.getRule().getName().equals( "rule2" ) ) {
                list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s1" ) + ":" + act.isActive() );
            }
        }

        assertContains( new String[]{"rule1:false", "rule2:0:true", "rule2:1:true", "rule2:2:true", "rule3:2:false"},
                        list );
    }

    @Test
    public void testFromnSharingWithMixedDormantAndActive() {
        String str = "package org.drools.test \n" +
                     "global java.util.List list \n" +
                     "\n" +
                     "rule rule1 salience ( Integer.parseInt( $s1+'1' ) )  when\n" +
                     "    $s1 : String( this == '1' )  from list\n" +
                     "then\n" +
                     "end\n" +
                     "rule rule2  salience ( Integer.parseInt( $s1+'1' ) ) when\n" +
                     "    $s1 : String( )  from list \n" +
                     "    eval( 1 == 1 ) \n" +
                     "then\n" +
                     "end\n" +
                     "rule rule3 salience ( Integer.parseInt( $s1+'1' ) ) when\n" +
                     "    $s1 : String( ) from list  \n" +
                     "    eval( 1 == 1 ) \n" +
                     "    eval( 1 == 1 ) \n" +
                     "then\n" +
                     "    kcontext.getKnowledgeRuntime().halt();\n" +
                     "end\n" +
                     "\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        list.add( "0" );
        list.add( "1" );
        list.add( "2" );

        ksession.setGlobal( "list",
                            list );

        ksession.fireAllRules();

        ActivationIterator it = ActivationIterator.iterator( ksession );
        list = new ArrayList();
        for ( Activation act = (Activation) it.next(); act != null; act = (Activation) it.next() ) {
            list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s1" ) + ":" + act.isActive() );
        }

        assertContains( new String[]{"rule1:1:true", "rule2:0:true", "rule2:1:true", "rule2:2:true", "rule3:0:true", "rule3:1:true", "rule3:2:false"},
                        list );
    }

    @Test
    public void testAccnSharingWithMixedDormantAndActive() {
        String str = "package org.drools.test \n" +
                     "\n" +
                     "rule rule1 when\n" +
                     "    $s1 : Double() from accumulate( $i : Integer(), sum ( $i ) )    " +
                     "then\n" +
                     "end\n" +
                     "rule rule2  when\n" +
                     "    $s1 : Double() from accumulate( $i : Integer(), sum ( $i ) )    " +
                     "    eval( 1 == 1 ) \n" +
                     "then\n" +
                     "end\n" +
                     "rule rule3  salience 10 when\n" +
                     "    eval( 1 == 1 ) \n" +
                     "    $s1 : Double() from accumulate( $i : Integer(), sum ( $i ) )    " +
                     "    eval( 1 == 1 ) \n" +
                     "then\n" +
                     "    kcontext.getKnowledgeRuntime().halt();\n" +
                     "end\n" +
                     "\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.insert( new Integer( 1 ) );
        ksession.insert( new Integer( 2 ) );
        ksession.insert( new Integer( 3 ) );

        ksession.fireAllRules();

        ActivationIterator it = ActivationIterator.iterator( ksession );
        List list = new ArrayList();
        list = new ArrayList();
        for ( Activation act = (Activation) it.next(); act != null; act = (Activation) it.next() ) {
            list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s1" ) + ":" + act.isActive() );
        }

        assertContains( new String[]{"rule1:6.0:true", "rule2:6.0:true", "rule3:6.0:false"},
                        list );
    }

    public void assertContains(Object[] objects,
                               List list) {
        for ( Object object : objects ) {
            if ( !list.contains( object ) ) {
                fail( "does not contain:" + object );
            }
        }
    }
}
