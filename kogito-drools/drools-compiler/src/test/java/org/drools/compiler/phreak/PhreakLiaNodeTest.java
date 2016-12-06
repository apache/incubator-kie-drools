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

package org.drools.compiler.phreak;

import org.drools.core.common.InternalFactHandle;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

public class PhreakLiaNodeTest {

    @Test
    public void test() {
        String str = "package org.drools.compiler.test\n" +
                "\n" +
                "import " + A.class.getCanonicalName() + "\n" +
                "import " + B.class.getCanonicalName() + "\n" +
                "\n" +
                "rule r1 \n" +
                "    when \n" +
                "        $a : A( object == 1 )\n" +
                "    then \n" +
                "        System.out.println( $a ); \n" +
                "end \n" +
                "rule r2 \n" +
                "    when \n" +
                "        $a : A( object == 2 )\n" +
                "    then \n" +
                "        System.out.println( $a ); \n" +
                "end \n " +
                "rule r3 \n" +
                "    when \n" +
                "        $a : A( object == 2 )\n" +
                "        $b : B( )\n" +
                "    then \n" +
                "        System.out.println( $a ); \n" +
                "end \n " +                
                "rule r4 \n" +
                "    when \n" +
                "        $a : A( object == 3 )\n" +
                "    then \n" +
                "        System.out.println( $a ); \n" +
                "end \n";
                
        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        builder.add( ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL);

        if ( builder.hasErrors() ) {
            throw new RuntimeException(builder.getErrors().toString());
        }
        
        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addKnowledgePackages(builder.getKnowledgePackages());

        StatefulKnowledgeSession ksession = knowledgeBase.newStatefulKnowledgeSession();

        InternalFactHandle fhB = ( InternalFactHandle ) ksession.insert( B.b(1) );
        
        InternalFactHandle fhA = ( InternalFactHandle ) ksession.insert( A.a(1) );
        ksession.fireAllRules();        
        System.out.println( "---1---" );
        
//        ksession.update( fhA, a(1) );
//        ksession.fireAllRules();
//        System.out.println( "---2---" );
//
        ksession.update( fhA, A.a(2) );
        ksession.fireAllRules(); 
        System.out.println( "---3---" );

        ksession.update( fhA, A.a(2) );
        ksession.fireAllRules();
        System.out.println( "---4---" );
        
        ksession.update( fhA, A.a(3) );
        ksession.fireAllRules(); 
        System.out.println( "---5---" );
        
        ksession.update( fhB, B.b(1) );

        ksession.update( fhA, A.a(3) );
        ksession.fireAllRules();  
        
//        ksession.update( fhA, a(1) );
//        ksession.fireAllRules();        
//
//        ksession.update( fhA, a(1) );
//        ksession.fireAllRules();          
        
        ksession.dispose();        
    }
    
    @Test
    public void test2() {
        String str = "package org.drools.compiler.test\n" +
                "\n" +
                "import " + A.class.getCanonicalName() + "\n" +
                "import " + B.class.getCanonicalName() + "\n" +
                "\n" +
                "rule r1 \n" +
                "    when \n" +
                "        $a : A( object == 1 )\n" +
                "    then \n" +
                "        System.out.println( $a ); \n" +
                "end \n" +
                "rule r2 \n" +
                "    when \n" +
                "        $a : A( object == 2 )\n" +
                "    then \n" +
                "        System.out.println( $a ); \n" +
                "end \n " +
                "rule r3 \n" +
                "    when \n" +
                "        $a : A( object == 2 )\n" +
                "        $b : B( )\n" +
                "    then \n" +
                "        System.out.println( $a + \" : \" + $b  );"
                + "      modify($a) { setObject(3) };  \n" +
                "end \n " +                
                "rule r4 \n" +
                "    when \n" +
                "        $a : A( object == 3 )\n" +
                "    then \n" +
                "        System.out.println( $a ); \n" +
                "end \n";
                
        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        builder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL);

        if ( builder.hasErrors() ) {
            throw new RuntimeException(builder.getErrors().toString());
        }
        
        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addKnowledgePackages(builder.getKnowledgePackages());

        StatefulKnowledgeSession ksession = knowledgeBase.newStatefulKnowledgeSession();

        InternalFactHandle fhB = ( InternalFactHandle ) ksession.insert( B.b(1) );
        
        InternalFactHandle fhA = ( InternalFactHandle ) ksession.insert( A.a(1) );
        ksession.fireAllRules();        
        System.out.println( "---1---" );
        
//        ksession.update( fhA, a(1) );
//        ksession.fireAllRules();
//        System.out.println( "---2---" );
//
        
        InternalFactHandle fhB2 = ( InternalFactHandle ) ksession.insert( B.b(2) );
        InternalFactHandle fhB3 = ( InternalFactHandle ) ksession.insert( B.b(3) );
        
        ksession.update( fhA, A.a(2) );
        ksession.fireAllRules(); 
        System.out.println( "---3---" );

//        ksession.update( fhA, a(2) );
//        ksession.fireAllRules();
//        System.out.println( "---4---" );
//        
//        ksession.update( fhA, a(3) );
//        ksession.fireAllRules(); 
//        System.out.println( "---5---" );
//        
//        ksession.update( fhB, b(1) );
//
//        ksession.update( fhA, a(3) );
//        ksession.fireAllRules();  
        
//        ksession.update( fhA, a(1) );
//        ksession.fireAllRules();        
//
//        ksession.update( fhA, a(1) );
//        ksession.fireAllRules();          
        
        ksession.dispose();        
    }    

}
