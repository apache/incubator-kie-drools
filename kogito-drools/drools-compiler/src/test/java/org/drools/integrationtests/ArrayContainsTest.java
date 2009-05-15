package org.drools.integrationtests;

import java.util.ArrayList;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.Primitives;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

import junit.framework.TestCase;

public class ArrayContainsTest extends TestCase {
    public void testContainsBooleanArray() throws Exception {
        String str = "";
        str += "package org.drools;\n";

        str += "global java.util.List list;\n";
        str += "global Boolean bGlobal;\n";

        str += "rule \"contains in array\"\n";
        str += "     salience 10\n";
        str += "     when\n";
        str += "         Primitives( $b : booleanPrimitive == true ) \n";
        str += "         Primitives( booleanPrimitive == false, primitiveBooleanArray contains bGlobal,  primitiveBooleanArray contains $b )\n";
        str += "     then\n";
        str += "        list.add( \"ok1\" );\n";
        str += "end\n";
        
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        
        if ( kbuilder.hasErrors() ) {
            fail( "kbuilder has errors\n:" + kbuilder.getErrors() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        kbase = SerializationHelper.serializeObject( kbase );
        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        final List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );  
        
        ksession.setGlobal( "bGlobal",
                            true );             
        
        final Primitives p1 = new Primitives();
        p1.setPrimitiveBooleanArray( new boolean[]{true, false} );
        FactHandle p1h = ksession.insert( p1 );
        
        final Primitives p2 = new Primitives();
        p2.setBooleanPrimitive( true );
        ksession.insert( p2 );                 

        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );
        
        ksession.retract( p1h );
        
        ksession.insert( p1 );
        
        ksession.fireAllRules();

        assertEquals( 2,
                      list.size() );        
    }      
    
    public void testContainsByteArray() throws Exception {
        String str = "";
        str += "package org.drools;\n";

        str += "global java.util.List list;\n";
        str += "global Byte bGlobal;\n";

        str += "rule \"contains in array\"\n";
        str += "     salience 10\n";
        str += "     when\n";
        str += "         Primitives( $b : bytePrimitive == 1 ) \n";
        str += "         Primitives( bytePrimitive != 1, primitiveByteArray contains bGlobal,  primitiveByteArray contains $b )\n";
        str += "     then\n";
        str += "        list.add( \"ok1\" );\n";
        str += "end\n";
        
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        
        if ( kbuilder.hasErrors() ) {
            fail( "kbuilder has errors\n:" + kbuilder.getErrors() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        kbase = SerializationHelper.serializeObject( kbase );
        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        final List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );  
        
        ksession.setGlobal( "bGlobal",
                            (byte) 1 );             
        
        final Primitives p1 = new Primitives();
        p1.setPrimitiveByteArray( new byte[]{1, 2, 3} );
        FactHandle p1h = ksession.insert( p1 );
        
        final Primitives p2 = new Primitives();
        p2.setBytePrimitive( (byte) 1 );
        ksession.insert( p2 );                 

        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );
        
        ksession.retract( p1h );
        
        ksession.insert( p1 );
        
        ksession.fireAllRules();

        assertEquals( 2,
                      list.size() );        
    }       
    
    public void testContainsShortArray() throws Exception {
        String str = "";
        str += "package org.drools;\n";

        str += "global java.util.List list;\n";
        str += "global Short sGlobal;\n";

        str += "rule \"contains in array\"\n";
        str += "     salience 10\n";
        str += "     when\n";
        str += "         Primitives( $s : shortPrimitive == 1 ) \n";
        str += "         Primitives( shortPrimitive != 1, primitiveShortArray contains sGlobal,  primitiveShortArray contains $s )\n";
        str += "     then\n";
        str += "        list.add( \"ok1\" );\n";
        str += "end\n";
        
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        
        if ( kbuilder.hasErrors() ) {
            fail( "kbuilder has errors\n:" + kbuilder.getErrors() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        kbase = SerializationHelper.serializeObject( kbase );
        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        final List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );  
        
        ksession.setGlobal( "sGlobal",
                            (short) 1 );             
        
        final Primitives p1 = new Primitives();
        p1.setPrimitiveShortArray( new short[]{1, 2, 3} );
        FactHandle p1h = ksession.insert( p1 );
        
        final Primitives p2 = new Primitives();
        p2.setShortPrimitive( (short) 1 );
        ksession.insert( p2 );                 

        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );
        
        ksession.retract( p1h );
        
        ksession.insert( p1 );
        
        ksession.fireAllRules();

        assertEquals( 2,
                      list.size() );        
    }         
    
    public void testContainsCharArray() throws Exception {
        String str = "";
        str += "package org.drools;\n";

        str += "global java.util.List list;\n";
        str += "global Character cGlobal;\n";

        str += "rule \"contains in array\"\n";
        str += "     salience 10\n";
        str += "     when\n";
        str += "         Primitives( $c : charPrimitive == 'c' ) \n";
        str += "         Primitives( charPrimitive != 'c', primitiveCharArray contains cGlobal,  primitiveCharArray contains $c )\n";
        str += "     then\n";
        str += "        list.add( \"ok1\" );\n";
        str += "end\n";
        
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        
        if ( kbuilder.hasErrors() ) {
            fail( "kbuilder has errors\n:" + kbuilder.getErrors() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        kbase = SerializationHelper.serializeObject( kbase );
        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        final List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );  
        
        ksession.setGlobal( "cGlobal",
                            'c' );             
        
        final Primitives p1 = new Primitives();
        p1.setPrimitiveCharArray( new char[]{ 'a', 'b', 'c'} );
        FactHandle p1h = ksession.insert( p1 );
        
        final Primitives p2 = new Primitives();
        p2.setCharPrimitive( 'c' );
        ksession.insert( p2 );                 

        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );
        
        ksession.retract( p1h );
        
        ksession.insert( p1 );
        
        ksession.fireAllRules();

        assertEquals( 2,
                      list.size() );        
    }     
    
    public void testContainsIntArray() throws Exception {
        String str = "";
        str += "package org.drools;\n";

        str += "global java.util.List list;\n";
        str += "global Integer iGlobal;\n";

        str += "rule \"contains in array\"\n";
        str += "     salience 10\n";
        str += "     when\n";
        str += "         Primitives( $i : intPrimitive == 10 ) \n";
        str += "         Primitives( intPrimitive != 10, primitiveIntArray contains iGlobal,  primitiveIntArray contains $i )\n";
        str += "     then\n";
        str += "        list.add( \"ok1\" );\n";
        str += "end\n";
        
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        
        if ( kbuilder.hasErrors() ) {
            fail( "kbuilder has errors\n:" + kbuilder.getErrors() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        kbase = SerializationHelper.serializeObject( kbase );
        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        final List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );  
        
        ksession.setGlobal( "iGlobal",
                            10 );             
        
        final Primitives p1 = new Primitives();
        p1.setPrimitiveIntArray( new int[]{ 5, 10, 20} );
        FactHandle p1h = ksession.insert( p1 );
        
        final Primitives p2 = new Primitives();
        p2.setIntPrimitive( 10 );
        ksession.insert( p2 );                 

        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );
        
        ksession.retract( p1h );
        
        ksession.insert( p1 );
        
        ksession.fireAllRules();

        assertEquals( 2,
                      list.size() );        
    } 
    
    public void testContainsLongArray() throws Exception {
        String str = "";
        str += "package org.drools;\n";

        str += "global java.util.List list;\n";
        str += "global Long lGlobal;\n";

        str += "rule \"contains in array\"\n";
        str += "     salience 10\n";
        str += "     when\n";
        str += "         Primitives( $l : longPrimitive == 10 ) \n";
        str += "         Primitives( longPrimitive != 10, primitiveLongArray contains lGlobal,  primitiveLongArray contains $l )\n";
        str += "     then\n";
        str += "        list.add( \"ok1\" );\n";
        str += "end\n";
        
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        
        if ( kbuilder.hasErrors() ) {
            fail( "kbuilder has errors\n:" + kbuilder.getErrors() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        kbase = SerializationHelper.serializeObject( kbase );
        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        final List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );  
        
        ksession.setGlobal( "lGlobal",
                            10l );             
        
        final Primitives p1 = new Primitives();
        p1.setPrimitiveLongArray( new long[]{ 5, 10, 20} );
        FactHandle p1h = ksession.insert( p1 );
        
        final Primitives p2 = new Primitives();
        p2.setLongPrimitive( 10 );
        ksession.insert( p2 );                 

        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );
        
        ksession.retract( p1h );
        
        ksession.insert( p1 );
        
        ksession.fireAllRules();

        assertEquals( 2,
                      list.size() );        
    }     
    
    public void testContainsFloatArray() throws Exception {
        String str = "";
        str += "package org.drools;\n";

        str += "global java.util.List list;\n";
        str += "global Float fGlobal;\n";

        str += "rule \"contains in array\"\n";
        str += "     salience 10\n";
        str += "     when\n";
        str += "         Primitives( $f : floatPrimitive == 10 ) \n";
        str += "         Primitives( floatPrimitive != 10, primitiveFloatArray contains fGlobal,  primitiveFloatArray contains $f )\n";
        str += "     then\n";
        str += "        list.add( \"ok1\" );\n";
        str += "end\n";
        
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        
        if ( kbuilder.hasErrors() ) {
            fail( "kbuilder has errors\n:" + kbuilder.getErrors() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        kbase = SerializationHelper.serializeObject( kbase );
        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        final List list = new ArrayList();
        ksession.setGlobal( "list",
                                 list );  
        
        ksession.setGlobal( "fGlobal",
                            10.0f );             
        
        final Primitives p1 = new Primitives();
        p1.setPrimitiveFloatArray( new float[]{ 5f, 10f, 20f} );
        FactHandle p1h = ksession.insert( p1 );
        
        final Primitives p2 = new Primitives();
        p2.setFloatPrimitive( 10f );
        ksession.insert( p2 );                 

        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );
        
        ksession.retract( p1h );
        
        ksession.insert( p1 );
        
        ksession.fireAllRules();

        assertEquals( 2,
                      list.size() );        
    }     
    
    public void testContainsDoubleArray() throws Exception {
        String str = "";
        str += "package org.drools;\n";

        str += "global java.util.List list;\n";
        str += "global Double dGlobal;\n";

        str += "rule \"contains in array\"\n";
        str += "     salience 10\n";
        str += "     when\n";
        str += "         Primitives( $d : doublePrimitive == 10 ) \n";
        str += "         Primitives( doublePrimitive != 10, primitiveDoubleArray contains dGlobal,  primitiveDoubleArray contains $d )\n";
        str += "     then\n";
        str += "        list.add( \"ok1\" );\n";
        str += "end\n";
        
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        
        if ( kbuilder.hasErrors() ) {
            fail( "kbuilder has errors\n:" + kbuilder.getErrors() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        kbase = SerializationHelper.serializeObject( kbase );
        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        final List list = new ArrayList();
        ksession.setGlobal( "list",
                                 list );  
        
        ksession.setGlobal( "dGlobal",
                            10.0d );             
        
        final Primitives p1 = new Primitives();
        p1.setPrimitiveDoubleArray( new double[]{ 5, 10, 20} );
        FactHandle p1h = ksession.insert( p1 );
        
        final Primitives p2 = new Primitives();
        p2.setDoublePrimitive( 10 );
        ksession.insert( p2 );                 

        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );
        
        ksession.retract( p1h );
        
        ksession.insert( p1 );
        
        ksession.fireAllRules();

        assertEquals( 2,
                      list.size() );        
    }    
         
}
