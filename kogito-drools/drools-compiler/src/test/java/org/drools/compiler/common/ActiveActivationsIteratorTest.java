package org.drools.compiler.common;

import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.core.common.ActiveActivationIterator;
import org.drools.core.common.AgendaItem;
import org.drools.core.common.InternalAgenda;
import org.drools.core.util.Iterator;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.junit.Test;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.rule.Match;

public class ActiveActivationsIteratorTest extends CommonTestMethodBase {

    @Test
    public void testActiveActivationsIteratorTest() {
        String str = "package org.kie.test \n" +
                     "\n" +
                     "rule rule0 @Eager(true) agenda-group 'a1' salience ( Integer.parseInt('1'+$s) ) when\n" +
                     "    $s : String( this != 'xx' )\n" +
                     "then\n" +
                     "end\n" +
                     "rule rule1 @Eager(true) agenda-group 'a2' salience ( Integer.parseInt('1'+$s)) when\n" +
                     "    $s : String( this != 'xx' )\n" +
                     "    eval( Integer.parseInt( $s ) <= 2 ) \n" +
                     "then\n" +
                     "end\n" +
                     "rule rule2 @Eager(true) agenda-group 'a3' salience ( Integer.parseInt('1'+$s)) when\n" +
                     "    $s : String( this != 'xx' )\n" +
                     "    eval( Integer.parseInt( $s ) <= 2 ) \n" +
                     "then\n" +
                     "    kcontext.getKieRuntime().halt();\n" +
                     "end\n" +
                     "rule rule3 @Eager(true) ruleflow-group 'r1' salience ( Integer.parseInt('1'+$s)) when\n" +
                     "    $s : String( this != 'xx' )\n" +
                     "    eval( Integer.parseInt( $s ) > 2 ) \n" +
                     "then\n" +
                     "end\n" +
                     "rule rule4 @Eager(true) ruleflow-group 'r1' salience ( Integer.parseInt('1'+$s) ) when\n" +
                     "    $s : String( this != 'xx' )\n" +
                     "    eval( Integer.parseInt( $s ) > 2 ) \n" +
                     "    eval( Integer.parseInt( $s ) > 3 ) \n" +
                     "then\n" +
                     "end\n" +
                     "rule rule6 @Eager(true) when\n" +
                     "     java.util.Map()\n" +
                     "then\n" +
                     "end\n" +
                     "\n" +
                     "rule rule7 @Eager(true) when\n" +
                     "    $s : String( this != 'xx' )\n" +
                     "then\n" +
                     "end\n" +
                     "\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(str.getBytes()),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        for ( int i = 0; i < 3; i++ ) {
            ksession.insert( new String( "" + i ) );
        }

        ((InternalAgenda)ksession.getAgenda()).unstageActivations();

        ((InternalAgenda) ksession.getAgenda()).evaluateEagerList();


        Iterator it = ActiveActivationIterator.iterator(ksession);
        List list = new ArrayList();
        for ( AgendaItem act = (AgendaItem) it.next(); act != null; act = (AgendaItem) it.next() ) {
            list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s" ) + ":" + act.isQueued() );
        }
        assertContains( new String[]{"rule7:2:true", "rule7:0:true", "rule7:1:true", "rule0:2:true", "rule0:0:true", "rule0:1:true", "rule1:2:true", "rule1:0:true", "rule1:1:true", "rule2:2:true", "rule2:0:true", "rule2:1:true"},
                        list );

        ksession.fireAllRules();

        it = ActiveActivationIterator.iterator( ksession );

        list = new ArrayList();
        for ( AgendaItem act = (AgendaItem) it.next(); act != null; act = (AgendaItem) it.next() ) {
            list.add( act.getRule().getName() + ":" + act.getDeclarationValue( "$s" ) + ":" + act.isQueued() );
        }
        assertContains( new String[]{"rule0:2:true", "rule0:0:true", "rule0:1:true", "rule1:2:true", "rule1:0:true", "rule1:1:true", "rule2:2:true", "rule2:0:true", "rule2:1:true"},
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
