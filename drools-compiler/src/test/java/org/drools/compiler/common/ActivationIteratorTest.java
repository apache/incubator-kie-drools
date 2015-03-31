package org.drools.compiler.common;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.core.common.ActivationIterator;
import org.drools.core.common.AgendaItem;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.util.Iterator;
import org.junit.Test;
import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.runtime.conf.ForceEagerActivationFilter;
import org.kie.internal.runtime.conf.ForceEagerActivationOption;
import org.kie.internal.utils.KieHelper;

import java.util.ArrayList;
import java.util.List;

public class ActivationIteratorTest extends CommonTestMethodBase {

    @Test
    public void testSingleLian() {
        String str = "package org.kie.test \n" +
                     "\n" +
                     "rule rule1 @Propagation(EAGER) when\n" +
                     "    $s : String( this != 'xx' )\n" +
                     "then\n" +
                     "end\n" +
                     "rule rule6 @Propagation(EAGER) when\n" +
                     "     java.util.Map()\n" +
                     "then\n" +
                     "end\n" +
                     "\n";

        KieSession ksession = new KieHelper().addContent(str, ResourceType.DRL)
                                             .build()
                                             .newKieSession();

        for ( int i = 0; i < 5; i++ ) {
            ksession.insert( new String( "" + i ) );
        }

        evaluateEagerList(ksession);

        Iterator it = ActivationIterator.iterator( ksession );
        List list = new ArrayList();
        for ( AgendaItem act = (AgendaItem) it.next(); act != null; act = (AgendaItem) it.next() ) {
            list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s" ) + ":" + act.isQueued() );
        }
        assertContains( new String[]{"rule1:0:true", "rule1:1:true", "rule1:2:true", "rule1:3:true", "rule1:4:true"},
                        list );

        ksession.fireAllRules();

        it = ActivationIterator.iterator( ksession );
        list = new ArrayList();
        for ( AgendaItem act = (AgendaItem) it.next(); act != null; act = (AgendaItem) it.next() ) {
            list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s" ) + ":" + act.isQueued() );
        }
        assertContains( new String[]{"rule1:0:false", "rule1:1:false", "rule1:2:false", "rule1:3:false", "rule1:4:false"},
                        list );
    }

    private void evaluateEagerList(KieSession ksession) {
        ((InternalWorkingMemory) ksession).flushPropagations();
        ((InternalAgenda) ksession.getAgenda()).evaluateEagerList();
    }

    @Test
    public void testLianPlusEvaln() {
        String str = "package org.kie.test \n" +
                     "\n" +
                     "rule rule1 @Propagation(EAGER) when\n" +
                     "    $s : String( this != 'xx' )\n" +
                     "    eval( 1 == 1 ) \n" +
                     "then\n" +
                     "end\n" +
                     "rule rule6 @Propagation(EAGER) when\n" +
                     "     java.util.Map()\n" +
                     "then\n" +
                     "end\n" +
                     "\n";

        KieSession ksession = new KieHelper().addContent(str, ResourceType.DRL)
                                             .build()
                                             .newKieSession();

        for ( int i = 0; i < 5; i++ ) {
            ksession.insert( new String( "" + i ) );
        }

        evaluateEagerList(ksession);

        Iterator it = ActivationIterator.iterator( ksession );
        List list = new ArrayList();
        for ( AgendaItem act = (AgendaItem) it.next(); act != null; act = (AgendaItem) it.next() ) {
            list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s" ) + ":" + act.isQueued() );
        }
        assertContains( new String[]{"rule1:0:true", "rule1:1:true", "rule1:2:true", "rule1:3:true", "rule1:4:true"},
                        list );

        ksession.fireAllRules();

        it = ActivationIterator.iterator( ksession );
        list = new ArrayList();
        for ( AgendaItem act = (AgendaItem) it.next(); act != null; act = (AgendaItem) it.next() ) {
            list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s" ) + ":" + act.isQueued() );
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
        String str = "package org.kie.test \n" +
                     "\n" +
                     "rule rule0 @Propagation(EAGER) when\n" +
                     "    $s : String( this != 'xx' )\n" +
                     "then\n" +
                     "end\n" +
                     "rule rule1 @Propagation(EAGER) when\n" +
                     "    $s : String( this != 'xx' )\n" +
                     "    eval( Integer.parseInt( $s ) <= 2 ) \n" +
                     "then\n" +
                     "end\n" +
                     "rule rule2 @Propagation(EAGER) when\n" +
                     "    $s : String( this != 'xx' )\n" +
                     "    eval( Integer.parseInt( $s ) <= 2 ) \n" +
                     "then\n" +
                     "end\n" +
                     "rule rule3 @Propagation(EAGER) when\n" +
                     "    $s : String( this != 'xx' )\n" +
                     "    eval( Integer.parseInt( $s ) > 2 ) \n" +
                     "then\n" +
                     "end\n" +
                     "rule rule4 @Propagation(EAGER) when\n" +
                     "    $s : String( this != 'xx' )\n" +
                     "    eval( Integer.parseInt( $s ) > 2 ) \n" +
                     "    eval( Integer.parseInt( $s ) > 3 ) \n" +
                     "then\n" +
                     "end\n" +
                     "rule rule5 @Propagation(EAGER) when\n" +
                     "    $s : String( this != 'xx' )\n" +
                     "    eval( Integer.parseInt( $s ) > 2 ) \n" +
                     "    eval( Integer.parseInt( $s ) > 3 ) \n" +
                     "then\n" +
                     "end\n" +
                     "rule rule6 @Propagation(EAGER) when\n" +
                     "     java.util.Map()\n" +
                     "then\n" +
                     "end\n" +
                     "\n";

        KieSession ksession = new KieHelper().addContent(str, ResourceType.DRL)
                                             .build()
                                             .newKieSession();

        for ( int i = 0; i < 5; i++ ) {
            ksession.insert( new String( "" + i ) );
        }

        evaluateEagerList(ksession);

        Iterator it = ActivationIterator.iterator( ksession );

        List list = new ArrayList();
        for ( AgendaItem act = (AgendaItem) it.next(); act != null; act = (AgendaItem) it.next() ) {
            list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s" ) + ":" + act.isQueued() );
        }
        assertContains( new String[]{"rule0:0:true", "rule0:1:true", "rule0:2:true", "rule0:3:true", "rule0:4:true",
                                "rule1:0:true", "rule1:1:true", "rule1:2:true", "rule2:0:true", "rule2:1:true", "rule2:2:true",
                                "rule3:3:true", "rule3:4:true",
                                "rule4:4:true", "rule5:4:true"},
                        list );

        ksession.fireAllRules();

        it = ActivationIterator.iterator( ksession );
        list = new ArrayList();
        for ( AgendaItem act = (AgendaItem) it.next(); act != null; act = (AgendaItem) it.next() ) {
            list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s" ) + ":" + act.isQueued() );
        }
        assertContains( new String[]{"rule0:0:false", "rule0:1:false", "rule0:2:false", "rule0:3:false", "rule0:4:false",
                                "rule1:0:false", "rule1:1:false", "rule1:2:false", "rule2:0:false", "rule2:1:false", "rule2:2:false",
                                "rule3:3:false", "rule3:4:false",
                                "rule4:4:false", "rule5:4:false"},
                        list );
    }

    @Test
    public void testLianPlusEvalnWithSharingWithMixedDormantAndActive() {
        // Rule 0 single LiaNode
        // Rule 1 and 2 are shared
        // Rule 3 shares the LIANode with 1 and 2
        // Rule 4 Shares the eval with 3
        String str = "package org.kie.test \n" +
                     "\n" +
                     "rule rule2 salience ( Integer.parseInt('1'+$s)) when\n" +
                     "    $s : String( this != 'xx' )\n" +
                     "    eval( Integer.parseInt( $s ) <= 2 ) \n" +
                     "then\n" +
                     "    kcontext.getKieRuntime().halt();\n" +
                     "end\n" +
                     "rule rule0 salience ( Integer.parseInt('1'+$s) ) when\n" +
                     "    $s : String( this != 'xx' )\n" +
                     "then\n" +
                     "end\n" +
                     "rule rule1 salience ( Integer.parseInt('1'+$s)) when\n" +
                     "    $s : String( this != 'xx' )\n" +
                     "    eval( Integer.parseInt( $s ) <= 2 ) \n" +
                     "then\n" +
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

        KieSession ksession = new KieHelper().addContent(str, ResourceType.DRL)
                                             .build()
                                             .newKieSession();

        for ( int i = 0; i < 5; i++ ) {
            ksession.insert( new String( "" + i ) );
        }
        ksession.fireAllRules();

        Iterator it = ActivationIterator.iterator( ksession );

        List list = new ArrayList();
        for ( AgendaItem act = (AgendaItem) it.next(); act != null; act = (AgendaItem) it.next() ) {
            list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s" ) + ":" + act.isQueued() );
        }
        assertContains( new String[]{"rule0:0:true", "rule0:1:true", "rule0:2:true", "rule0:3:false", "rule0:4:false",
                                "rule1:0:true", "rule1:1:true", "rule1:2:true", "rule2:0:true", "rule2:1:true", "rule2:2:false",
                                "rule3:3:false", "rule3:4:false",
                                "rule3:4:false"},
                        list );
    }

    @Test
    public void testSingleJoinNode() {
        String str = "package org.kie.test \n" +
                     "\n" +
                     "rule rule1  @Propagation(EAGER)  when\n" +
                     "    $s1 : String( )\n" +
                     "    $s2 : String( )\n" +
                     "then\n" +
                     "end\n";

        KieSession ksession = new KieHelper().addContent(str, ResourceType.DRL)
                                             .build()
                                             .newKieSession();

        for ( int i = 0; i < 2; i++ ) {
            ksession.insert( new String( "" + i ) );
        }
        evaluateEagerList(ksession);

        Iterator it = ActivationIterator.iterator( ksession );
        List list = new ArrayList();
        for ( AgendaItem act = (AgendaItem) it.next(); act != null; act = (AgendaItem) it.next() ) {
            list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s1" ) + ":" + act.getDeclarationValue( "$s2" ) + ":" + act.isQueued() );
        }
        assertContains( new String[]{"rule1:0:1:true", "rule1:1:0:true", "rule1:1:1:true", "rule1:0:0:true"},
                        list );


        ksession.fireAllRules();
        it = ActivationIterator.iterator( ksession );
        list = new ArrayList();
        for ( AgendaItem act = (AgendaItem) it.next(); act != null; act = (AgendaItem) it.next() ) {
            list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s1" ) + ":" + act.getDeclarationValue( "$s2" ) + ":" + act.isQueued() );
        }
        assertContains( new String[]{"rule1:0:1:false", "rule1:1:0:false", "rule1:1:1:false", "rule1:0:0:false"},
                        list );
    }

    @Test
    public void testSingleJoinNodePlusEvaln() {
        String str = "package org.kie.test \n" +
                     "\n" +
                     "rule rule1 @Propagation(EAGER) when\n" +
                     "    $s1 : String( )\n" +
                     "    $s2 : String( )\n" +
                     "    eval( 1 == 1 ) \n" +
                     "then\n" +
                     "end\n" +
                     "\n";

        KieSession ksession = new KieHelper().addContent(str, ResourceType.DRL)
                                             .build()
                                             .newKieSession();

        for ( int i = 0; i < 2; i++ ) {
            ksession.insert( new String( "" + i ) );
        }
        evaluateEagerList(ksession);

        Iterator it = ActivationIterator.iterator( ksession );
        List list = new ArrayList();
        for ( AgendaItem act = (AgendaItem) it.next(); act != null; act = (AgendaItem) it.next() ) {
            list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s1" ) + ":" + act.getDeclarationValue( "$s2" ) + ":" + act.isQueued() );
        }
        assertContains( new String[]{"rule1:0:1:true", "rule1:1:0:true", "rule1:1:1:true", "rule1:0:0:true"},
                        list );

        ksession.fireAllRules();

        it = ActivationIterator.iterator( ksession );
        list = new ArrayList();
        for ( AgendaItem act = (AgendaItem) it.next(); act != null; act = (AgendaItem) it.next() ) {
            list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s1" ) + ":" + act.getDeclarationValue( "$s2" ) + ":" + act.isQueued() );
        }
        assertContains( new String[]{"rule1:0:1:false", "rule1:1:0:false", "rule1:1:1:false", "rule1:0:0:false"},
                        list );
    }

    @Test
    public void testSingleJoinNodePlusEvalnWithSharing() {
        String str = "package org.kie.test \n" +
                     "\n" +
                     "rule rule1  @Propagation(EAGER)  when\n" +
                     "    $s1 : String( )\n" +
                     "    $s2 : String( )\n" +
                     "    eval( 1 == 1 ) \n" +
                     "then\n" +
                     "end\n" +
                     "rule rule2  @Propagation(EAGER)  when\n" +
                     "    $s1 : String( )\n" +
                     "then\n" +
                     "end\n" +
                     "rule rule3  @Propagation(EAGER)  when\n" +
                     "    $s1 : String( )\n" +
                     "    $s2 : String( )\n" +
                     "    $s3 : String( )\n" +
                     "    eval( 1 == 1 ) \n" +
                     "then\n" +
                     "end\n" +
                     "\n";

        KieSession ksession = new KieHelper().addContent(str, ResourceType.DRL)
                                             .build()
                                             .newKieSession();

        for ( int i = 0; i < 2; i++ ) {
            ksession.insert( new String( "" + i ) );
        }
        evaluateEagerList(ksession);

        Iterator it = ActivationIterator.iterator( ksession );
        List list = new ArrayList();
        for ( AgendaItem act = (AgendaItem) it.next(); act != null; act = (AgendaItem) it.next() ) {
            if ( act.getRule().getName().equals( "rule3" ) ) {
                list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s1" ) + ":" + act.getDeclarationValue( "$s2" ) + ":" + act.getDeclarationValue( "$s3" ) + ":" + act.isQueued() );
            } else if ( act.getRule().getName().equals( "rule1" ) ) {
                list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s1" ) + ":" + act.getDeclarationValue( "$s2" ) + ":" + act.isQueued() );
            } else if ( act.getRule().getName().equals( "rule2" ) ) {
                list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s1" ) + ":" + act.isQueued() );
            }
        }

        assertContains( new String[]{"rule1:0:0:true", "rule1:0:1:true", "rule1:1:0:true", "rule1:1:1:true",
                                "rule2:1:true", "rule2:0:true",
                                "rule3:0:0:0:true", "rule3:0:0:1:true", "rule3:1:0:0:true", "rule3:1:0:1:true", "rule3:0:1:0:true", "rule3:0:1:1:true", "rule3:1:1:0:true", "rule3:1:1:1:true"},
                        list );

        ksession.fireAllRules();

        it = ActivationIterator.iterator( ksession );
        list = new ArrayList();
        for ( AgendaItem act = (AgendaItem) it.next(); act != null; act = (AgendaItem) it.next() ) {
            if ( act.getRule().getName().equals( "rule3" ) ) {
                list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s1" ) + ":" + act.getDeclarationValue( "$s2" ) + ":" + act.getDeclarationValue( "$s3" ) + ":" + act.isQueued() );
            } else if ( act.getRule().getName().equals( "rule1" ) ) {
                list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s1" ) + ":" + act.getDeclarationValue( "$s2" ) + ":" + act.isQueued() );
            } else if ( act.getRule().getName().equals( "rule2" ) ) {
                list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s1" ) + ":" + act.isQueued() );
            }
        }
        assertContains( new String[]{"rule1:0:0:false", "rule1:0:1:false", "rule1:1:0:false", "rule1:1:1:false",
                                "rule2:1:false", "rule2:0:false",
                                "rule3:0:0:0:false", "rule3:0:0:1:false", "rule3:1:0:0:false", "rule3:1:0:1:false", "rule3:0:1:0:false", "rule3:0:1:1:false", "rule3:1:1:0:false", "rule3:1:1:1:false"},
                        list );
    }

    @Test
    public void testSingleJoinNodePlusEvalnWithSharingWithMixedDormantAndActive() {
        String str = "package org.kie.test \n" +
                     "\n" +
                     "rule rule1  salience ( Integer.parseInt( '1'+$s1+'0'+$s2 ) ) when\n" +
                     "    $s1 : String( )\n" +
                     "    $s2 : String( )\n" +
                     "    eval( 1 == 1 ) \n" +
                     "then\n" +
                     "end\n" +
                     "rule rule2 salience (1020 + Integer.parseInt( $s1 ) ) when\n" +
                     "    $s1 : String( )\n" +
                     "then\n" +
                     "    kcontext.getKieRuntime().halt();\n" +
                     "end\n" +
                     "rule rule3  salience ( Integer.parseInt( '1'+$s1+'1'+$s2  ) ) when\n" +
                     "    $s1 : String( )\n" +
                     "    $s2 : String( )\n" +
                     "    $s3 : String( )\n" +
                     "    eval( 1 == 1 ) \n" +
                     "then\n" +
                     "end\n" +
                     "\n";

        KieSession ksession = new KieHelper().addContent(str, ResourceType.DRL)
                                             .build()
                                             .newKieSession();

        for ( int i = 0; i < 2; i++ ) {
            ksession.insert( new String( "" + i ) );
        }

        ksession.fireAllRules();

        Iterator it = ActivationIterator.iterator( ksession );
        List list = new ArrayList();
        for ( AgendaItem act = (AgendaItem) it.next(); act != null; act = (AgendaItem) it.next() ) {
            if ( act.getRule().getName().equals( "rule3" ) ) {
                list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s1" ) + ":" + act.getDeclarationValue( "$s2" ) + ":" + act.getDeclarationValue( "$s3" ) + ":" + act.isQueued() );
            } else if ( act.getRule().getName().equals( "rule1" ) ) {
                list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s1" ) + ":" + act.getDeclarationValue( "$s2" ) + ":" + act.isQueued() );
            } else if ( act.getRule().getName().equals( "rule2" ) ) {
                list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s1" ) + ":" + act.isQueued() );
            }
        }

        assertContains( new String[]{"rule1:0:0:true", "rule1:0:1:true", "rule1:1:0:false", "rule1:1:1:false",
                                "rule2:1:false", "rule2:0:true",
                                "rule3:0:0:0:true", "rule3:0:0:1:true", "rule3:1:0:0:false", "rule3:1:0:1:false", "rule3:0:1:0:true", "rule3:0:1:1:true", "rule3:1:1:0:false", "rule3:1:1:1:false"},
                        list );
    }

    @Test
    public void testNotSharingWithMixedDormantAndActive() {
        String str = "package org.kie.test \n" +
                     "\n" +
                     "rule rule1  @Propagation(EAGER)  salience 10 when\n" +
                     "    not String( this == '1' )\n" +
                     "then\n" +
                     "end\n" +
                     "rule rule2   @Propagation(EAGER)  salience ( Integer.parseInt( $s1+'1' ) ) when\n" +
                     "    not String( this == '1' )\n" +
                     "    $s1 : String( )\n" +
                     "    eval( 1 == 1 ) \n" +
                     "then\n" +
                     "end\n" +
                     "rule rule3  @Propagation(EAGER)  salience ( Integer.parseInt( $s1+'2' ) ) when\n" +
                     "    $s1 : String( )\n" +
                     "    not String( this == '1' )\n" +
                     "    eval( 1 == 1 ) \n" +
                     "    eval( 1 == 1 ) \n" +
                     "then\n" +
                     "    kcontext.getKieRuntime().halt();\n" +
                     "end\n" +
                     "\n";

        KieSession ksession = new KieHelper().addContent(str, ResourceType.DRL)
                                             .build()
                                             .newKieSession();

        ksession.insert( "0" );
        ksession.insert( "2" );

        ksession.fireAllRules();

        Iterator it = ActivationIterator.iterator( ksession );
        List list = new ArrayList();
        for ( AgendaItem act = (AgendaItem) it.next(); act != null; act = (AgendaItem) it.next() ) {
            if ( act.getRule().getName().equals( "rule3" ) ) {
                list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s1" ) + ":" + act.isQueued() );
            } else if ( act.getRule().getName().equals( "rule1" ) ) {
                list.add( act.getRule().getName() + ":" + act.isQueued() );
            } else if ( act.getRule().getName().equals( "rule2" ) ) {
                list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s1" ) + ":" + act.isQueued() );
            }
        }

        assertContains( new String[]{"rule1:true", "rule2:0:true", "rule2:2:true", "rule3:0:true", "rule3:2:false"},
                        list );
    }

    @Test
    public void testExistsSharingWithMixedDormantAndActive() {
        String str = "package org.kie.test \n" +
                     "\n" +
                     "rule rule3  @Propagation(EAGER)  salience ( Integer.parseInt( $s1+'1' ) ) when\n" +
                     "    $s1 : String( )\n" +
                     "    exists String( this == '1' )\n" +
                     "    eval( 1 == 1 ) \n" +
                     "    eval( 1 == 1 ) \n" +
                     "then\n" +
                     "    kcontext.getKieRuntime().halt();\n" +
                     "end\n" +
                     "rule rule1  @Propagation(EAGER)  salience 100 when\n" +
                     "    exists String( this == '1' )\n" +
                     "then\n" +
                     "end\n" +
                     "rule rule2   @Propagation(EAGER)  salience ( Integer.parseInt( $s1+'1' ) ) when\n" +
                     "    exists String( this == '1' )\n" +
                     "    $s1 : String( )\n" +
                     "    eval( 1 == 1 ) \n" +
                     "then\n" +
                     "end\n" +
                     "\n";

        KieSession ksession = new KieHelper().addContent(str, ResourceType.DRL)
                                             .build()
                                             .newKieSession();

        ksession.insert( "0" );
        ksession.insert( "1" );
        ksession.insert( "2" );

        ksession.fireAllRules();

        Iterator it = ActivationIterator.iterator( ksession );
        List list = new ArrayList();
        for ( AgendaItem act = (AgendaItem) it.next(); act != null; act = (AgendaItem) it.next() ) {
            if ( act.getRule().getName().equals( "rule3" ) ) {
                list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s1" ) + ":" + act.isQueued() );
            } else if ( act.getRule().getName().equals( "rule1" ) ) {
                list.add( act.getRule().getName() + ":" + act.isQueued() );
            } else if ( act.getRule().getName().equals( "rule2" ) ) {
                list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s1" ) + ":" + act.isQueued() );
            }
        }

        assertContains( new String[]{"rule1:false", "rule2:0:true", "rule2:1:true", "rule2:2:true", "rule3:2:false"},
                        list );
    }

    @Test
    public void testFromnSharingWithMixedDormantAndActive() {
        String str = "package org.kie.test \n" +
                     "global java.util.List list \n" +
                     "\n" +
                     "rule rule3 salience ( Integer.parseInt( $s1+'1' ) ) when\n" +
                     "    $s1 : String( ) from list  \n" +
                     "    eval( 1 == 1 ) \n" +
                     "    eval( 1 == 1 ) \n" +
                     "then\n" +
                     "    kcontext.getKieRuntime().halt();\n" +
                     "end\n" +
                     "rule rule1 salience ( Integer.parseInt( $s1+'1' ) )  when\n" +
                     "    $s1 : String( this == '1' )  from list\n" +
                     "then\n" +
                     "end\n" +
                     "rule rule2  salience ( Integer.parseInt( $s1+'1' ) ) when\n" +
                     "    $s1 : String( )  from list \n" +
                     "    eval( 1 == 1 ) \n" +
                     "then\n" +
                     "end\n" +
                     "\n";

        KieSession ksession = new KieHelper().addContent(str, ResourceType.DRL)
                                             .build()
                                             .newKieSession();

        List list = new ArrayList();
        list.add( "0" );
        list.add( "1" );
        list.add( "2" );

        ksession.setGlobal( "list",
                            list );

        ksession.fireAllRules();

        Iterator it = ActivationIterator.iterator( ksession );
        list = new ArrayList();
        for ( AgendaItem act = (AgendaItem) it.next(); act != null; act = (AgendaItem) it.next() ) {
            list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s1" ) + ":" + act.isQueued() );
        }

        assertContains(new String[]{"rule1:1:true", "rule2:0:true", "rule2:1:true", "rule2:2:true", "rule3:0:true", "rule3:1:true", "rule3:2:false"},
                       list);
    }

    @Test
    public void testAccnSharingWithMixedDormantAndActive() {
        String str = "package org.kie.test \n" +
                     "\n" +
                     "rule rule1 @Propagation(EAGER) when\n" +
                     "    $s1 : Double() from accumulate( $i : Integer(), sum ( $i ) )    " +
                     "then\n" +
                     "end\n" +
                     "rule rule2 @Propagation(EAGER) when\n" +
                     "    $s1 : Double() from accumulate( $i : Integer(), sum ( $i ) )    " +
                     "    eval( 1 == 1 ) \n" +
                     "then\n" +
                     "end\n" +
                     "rule rule3  salience 10 when\n" +
                     "    eval( 1 == 1 ) \n" +
                     "    $s1 : Double() from accumulate( $i : Integer(), sum ( $i ) )    " +
                     "    eval( 1 == 1 ) \n" +
                     "then\n" +
                     "    kcontext.getKieRuntime().halt();\n" +
                     "end\n" +
                     "\n";

        KieSession ksession = new KieHelper().addContent(str, ResourceType.DRL)
                                             .build()
                                             .newKieSession();

        ksession.insert( new Integer( 1 ) );
        ksession.insert( new Integer( 2 ) );
        ksession.insert( new Integer( 3 ) );

        ksession.fireAllRules();

        Iterator it = ActivationIterator.iterator( ksession );
        List list = new ArrayList();
        list = new ArrayList();
        for ( AgendaItem act = (AgendaItem) it.next(); act != null; act = (AgendaItem) it.next() ) {
            list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s1" ) + ":" + act.isQueued() );
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

    @Test(timeout=10000)
    public void testEagerEvaluation() throws Exception {
        String str =
                "package org.simple \n" +
                "rule xxx @Propagation(EAGER) \n" +
                "when \n" +
                "  $s : String()\n" +
                "then \n" +
                "end  \n" +
                "rule yyy @Propagation(EAGER) \n" +
                "when \n" +
                "  $s : String()\n" +
                "then \n" +
                "end  \n";

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ForceEagerActivationOption.YES );

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        KieSession ksession = createKnowledgeSession(kbase, conf);

        final List list = new ArrayList();

        AgendaEventListener agendaEventListener = new DefaultAgendaEventListener() {
            public void matchCreated(org.kie.api.event.rule.MatchCreatedEvent event) {
                list.add("activated");
            }
        };
        ksession.addEventListener(agendaEventListener);

        ksession.insert("test");
        ((InternalWorkingMemory) ksession).flushPropagations();

        assertEquals(2, list.size());
    }

    @Test(timeout=10000)
    public void testFilteredEagerEvaluation() throws Exception {
        String str =
                "package org.simple \n" +
                "rule xxx @Propagation(EAGER) \n" +
                "when \n" +
                "  $s : String()\n" +
                "then \n" +
                "end  \n" +
                "rule yyy @Propagation(EAGER) \n" +
                "when \n" +
                "  $s : String()\n" +
                "then \n" +
                "end  \n";

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( new ForceEagerActivationOption.FILTERED( new ForceEagerActivationFilter() {
            @Override
            public boolean accept(Rule rule) {
                return rule.getName().equals("xxx");
            }
        }));

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        KieSession ksession = createKnowledgeSession(kbase, conf);

        final List list = new ArrayList();

        AgendaEventListener agendaEventListener = new DefaultAgendaEventListener() {
            public void matchCreated(org.kie.api.event.rule.MatchCreatedEvent event) {
                list.add("activated");
            }
        };
        ksession.addEventListener(agendaEventListener);

        ksession.insert("test");
        ((InternalWorkingMemory) ksession).flushPropagations();

        assertEquals(1, list.size());
    }
}
