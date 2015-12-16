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

package org.drools.compiler.rule.builder.dialect.java;

import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.junit.Test;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.io.ResourceType;

public class AsmGeneratorTest extends CommonTestMethodBase {
     
    @Test
    public void testPatterDeclarations() {
        String s = 
            "package org.kie.test\n" +
            "global java.util.List list\n" +        
            "rule r1 when\n" +
            "    s1 : String( this == 's1' )\n" +
            "    s2 : String( this == 's2' )\n" +
            "    s3 : String( this == 's3' )\n" +
            "    s4 : String( this == 's4' )\n" +
            "    s5 : String( this == 's5' )\n" +
            "then\n" +
            "    // s5 is missed out on purpose to make sure we only resolved required declarations\n" +
            "   list.add( s1 + s2 + s3 + s5 ); \n" +
            "end\n";
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(s.getBytes()), ResourceType.DRL );
        
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        List list = new ArrayList();
        ksession.setGlobal( "list", list );        
        
        ksession.insert( "s1" );
        ksession.insert( "s2" );
        ksession.insert( "s3" );
        ksession.insert( "s4" );
        ksession.insert( "s5" );
        
        ksession.fireAllRules();     
        assertEquals( 1, list.size() );
        assertEquals( "s1s2s3s5", list.get( 0 ));        
    }
    
    @Test
    public void testAllGeneratedConstructs() {
        String s = 
            "package org.kie.test\n" +
            "import org.drools.compiler.Person\n" +
            "global java.util.List list\n" +
            "rule r1 when\n" +
            "    s1 : String( this == 's1' )\n" +
            "    p1 : Person( $name1 : name, name == s1 )\n" +
            "    eval( p1.getName().equals( s1 ) ) \n" +
            "    s2 : String( this == 's2' )\n" +
            "    p2 : Person( $name2 : name, name == s2, eval( p2.getName().equals( s2 ) && " +
            "                                                  ! $name2.equals( $name1 )  ) )\n" +
            "    s3 : String( this == 's3' )\n" +
            "    not String( this == 's5')\n " +
            "    p3 : Person( $name3 : name, name == s3, name == ( new String( $name1.charAt(0) + \"3\" ) ) )\n" +            
            "then\n" +
            "    // *2 are missed out on purpose to make sure we only resolved required declarations\n" +
            "    list.add( s1 + p1 + $name1 + s3 + p3 + $name3 ); \n" +
            "end\n";
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( s.getBytes() ), ResourceType.DRL );        
        
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        
        ksession.insert( "s1" );
        ksession.insert( new Person( "s1" ) );
        ksession.insert( "s2" );
        ksession.insert( new Person( "s2" ) );
        ksession.insert( "s3" );
        ksession.insert( new Person( "s3" ) );
        
        ksession.fireAllRules();         
        
        assertEquals( 1, list.size() );
        assertEquals( "s1[Person name='s1 age='0' likes='']s1s3[Person name='s3 age='0' likes='']s3", list.get( 0 ));
    }    
    
    @Test
    public void testOr() {
        String s = 
            "package org.kie.test\n" +
            "import org.drools.compiler.Person\n" +
            "import org.drools.compiler.Cheese\n" +
            "global java.util.List list\n" +
            "rule r1 when\n" +
            "    s1 : String( this == 's1' )\n" +
            "    Cheese( $type : type == \"stilton\", $price : price ) or\n" + 
            "    ( Cheese( $type : type == \"brie\", $price : price ) and Person( name == \"bob\", likes == $type ) )\n" +            
            "then\n" +
            "    // *2 are missed out on purpose to make sure we only resolved required declarations\n" +
            "    list.add( \"test3\"+$type +\":\"+ new Integer( $price ) ); \n" +
            "end\n";
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( s.getBytes() ), ResourceType.DRL );        
        
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        
        ksession.insert( "s1" );
        ksession.insert( new Person( "bob", "brie" ) );
        ksession.insert( new Cheese( "stilton") );
        ksession.insert( new Cheese( "brie") );
        ksession.insert( new Person( "s2" ) );
        
        ksession.fireAllRules();         
        
        assertEquals( 2, list.size() );
        assertTrue( list.contains("test3brie:0"));
        assertTrue( list.contains("test3stilton:0"));
    }      

}
