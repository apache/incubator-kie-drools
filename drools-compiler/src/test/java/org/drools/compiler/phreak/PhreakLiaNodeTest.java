package org.drools.compiler.phreak;

import org.drools.common.InternalFactHandle;
import org.junit.Test;
import org.kie.KieBaseConfiguration;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.conf.LRUnlinkingOption;
import org.kie.io.ResourceFactory;
import org.kie.io.ResourceType;
import org.kie.runtime.StatefulKnowledgeSession;

public class PhreakLiaNodeTest {

    @Test
    public void test() {
        String str = "package org.drools.test\n" +
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

        builder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL);

        if ( builder.hasErrors() ) {
            throw new RuntimeException(builder.getErrors().toString());
        }
        
        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( LRUnlinkingOption.ENABLED );
        
        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase(kconf);        
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
        String str = "package org.drools.test\n" +
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
        
        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( LRUnlinkingOption.ENABLED );
        
        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase(kconf);        
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
