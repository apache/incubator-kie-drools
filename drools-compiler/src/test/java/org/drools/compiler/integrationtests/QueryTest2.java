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

package org.drools.compiler.integrationtests;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Order;
import org.drools.compiler.OrderItem;
import org.junit.Test;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.io.ResourceType;
import org.kie.internal.runtime.StatefulKnowledgeSession;


public class QueryTest2 extends CommonTestMethodBase {
    
    @Test
    public void testEvalRewrite() throws Exception {
        String str = "" +
        "package org.drools.compiler;\n" +
        "global java.util.List results;\n" +
        "rule \"eval rewrite\"\n" +
        "    when\n" +
        "        $o1 : OrderItem( order.number == 11, $seq : seq == 1 )\n" +
        //"        $o2 : OrderItem( order.number == $o1.order.number, seq != $seq )\n" +
        "        $o2 : Order( items[(Integer) 1] == $o1 ) \n" +
        "    then\n" +
        "        System.out.println( $o1 + \":\" + $o2 );\n" +
        "end        \n";
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL );
        
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        
        final Order order1 = new Order( 11,
                                        "Bob" );
        final OrderItem item11 = new OrderItem( order1,
                                                1 );
        final OrderItem item12 = new OrderItem( order1,
                                                2 );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.insert( order1 );
        ksession.insert( item11 );
        ksession.insert( item12 );
        
        ksession.fireAllRules();
        

    }
}
