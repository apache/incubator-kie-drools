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

package org.drools.compiler.common;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.core.common.ActiveActivationIterator;
import org.drools.core.common.AgendaItem;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.util.Iterator;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import java.util.ArrayList;
import java.util.List;

public class ActiveActivationsIteratorTest extends CommonTestMethodBase {

    @Test
    public void testActiveActivationsIteratorTest() {
        String str = "package org.kie.test \n" +
                     "\n" +
                     "rule rule0 @Propagation(EAGER) agenda-group 'a1' salience ( Integer.parseInt('1'+$s) ) when\n" +
                     "    $s : String( this != 'xx' )\n" +
                     "then\n" +
                     "end\n" +
                     "rule rule1 @Propagation(EAGER) agenda-group 'a2' salience ( Integer.parseInt('1'+$s)) when\n" +
                     "    $s : String( this != 'xx' )\n" +
                     "    eval( Integer.parseInt( $s ) <= 2 ) \n" +
                     "then\n" +
                     "end\n" +
                     "rule rule2 @Propagation(EAGER) agenda-group 'a3' salience ( Integer.parseInt('1'+$s)) when\n" +
                     "    $s : String( this != 'xx' )\n" +
                     "    eval( Integer.parseInt( $s ) <= 2 ) \n" +
                     "then\n" +
                     "    kcontext.getKieRuntime().halt();\n" +
                     "end\n" +
                     "rule rule3 @Propagation(EAGER) ruleflow-group 'r1' salience ( Integer.parseInt('1'+$s)) when\n" +
                     "    $s : String( this != 'xx' )\n" +
                     "    eval( Integer.parseInt( $s ) > 2 ) \n" +
                     "then\n" +
                     "end\n" +
                     "rule rule4 @Propagation(EAGER) ruleflow-group 'r1' salience ( Integer.parseInt('1'+$s) ) when\n" +
                     "    $s : String( this != 'xx' )\n" +
                     "    eval( Integer.parseInt( $s ) > 2 ) \n" +
                     "    eval( Integer.parseInt( $s ) > 3 ) \n" +
                     "then\n" +
                     "end\n" +
                     "rule rule6 @Propagation(EAGER) when\n" +
                     "     java.util.Map()\n" +
                     "then\n" +
                     "end\n" +
                     "\n" +
                     "rule rule7 @Propagation(EAGER) when\n" +
                     "    $s : String( this != 'xx' )\n" +
                     "then\n" +
                     "end\n" +
                     "\n";

        KieSession ksession = new KieHelper().addContent(str, ResourceType.DRL)
                                             .build()
                                             .newKieSession();

        for ( int i = 0; i < 3; i++ ) {
            ksession.insert( new String( "" + i ) );
        }

        ((InternalAgenda)ksession.getAgenda()).unstageActivations();

        ((InternalWorkingMemory) ksession).flushPropagations();
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
