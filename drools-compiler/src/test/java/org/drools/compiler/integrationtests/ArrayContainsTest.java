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

package org.drools.compiler.integrationtests;

import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Primitives;
import org.junit.Test;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.runtime.rule.FactHandle;

public class ArrayContainsTest extends CommonTestMethodBase {

    @Test
    public void testEqualsOnIntArray() throws Exception {
        String str = "";
        str += "package org.drools.compiler;\n";
        str += "global java.util.List list;\n";
        str += "rule \"contains in array\"\n";
        str += "     salience 10\n";
        str += "     when\n";
        str += "         Primitives( primitiveIntArray[0] == 1 )\n";
        str += "     then\n";
        str += "        list.add( \"ok1\" );\n";
        str += "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

        kbase = SerializationHelper.serializeObject( kbase );
        final StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal( "list",
                list );

        final Primitives p1 = new Primitives();
        p1.setPrimitiveIntArray( new int[] { 1, 2 } );
        FactHandle p1h = ksession.insert( p1 );

        ksession.fireAllRules();

        assertEquals( 1,
                list.size() );
    }

    @Test
    public void testContainsBooleanArray() throws Exception {
        String str = "";
        str += "package org.drools.compiler;\n";

        str += "global java.util.List list;\n";
        str += "global Boolean bGlobal;\n";
        str += "global Object bArrayGlobal;\n";

        str += "rule \"contains in array\"\n";
        str += "     salience 10\n";
        str += "     when\n";
        str += "         Primitives( $b : booleanPrimitive == true, $array1 :  primitiveBooleanArray ) \n";
        str += "         Primitives( booleanPrimitive == false, $array2 : primitiveBooleanArray contains bGlobal,  primitiveBooleanArray contains $b, ";
        str += "                     booleanPrimitive memberOf $array2, booleanPrimitive memberOf $array1, booleanPrimitive memberOf bArrayGlobal )\n";
        str += "     then\n";
        str += "        list.add( \"ok1\" );\n";
        str += "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

        kbase = SerializationHelper.serializeObject( kbase );
        final StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );
        ksession.setGlobal( "bGlobal",
                            true );
        ksession.setGlobal( "bArrayGlobal",
                            new boolean[]{true, false} );
        
        final Primitives p1 = new Primitives();
        p1.setPrimitiveBooleanArray( new boolean[]{true, false} );
        p1.setBooleanPrimitive( false );
        FactHandle p1h = ksession.insert( p1 );
        
        final Primitives p2 = new Primitives();
        p2.setPrimitiveBooleanArray( new boolean[]{true, false} );
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
    
    @Test
    public void testNotContainsBooleanArray() throws Exception {
        String str = "";
        str += "package org.drools.compiler;\n";

        str += "global java.util.List list;\n";
        str += "global Boolean bGlobal;\n";
        str += "global Object bArrayGlobal;\n";

        str += "rule \"contains in array\"\n";
        str += "     salience 10\n";
        str += "     when\n";
        str += "         Primitives( $b : booleanPrimitive, intPrimitive == 10, $array1 :  primitiveBooleanArray ) \n";
        str += "         Primitives( booleanPrimitive == false, intPrimitive != 10, $array2 : primitiveBooleanArray not contains bGlobal,  primitiveBooleanArray not contains $b, ";
        str += "                     booleanPrimitive not memberOf $array2, booleanPrimitive not memberOf $array1, booleanPrimitive not memberOf bArrayGlobal )\n";
        str += "     then\n";
        str += "        list.add( \"ok1\" );\n";
        str += "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

        kbase = SerializationHelper.serializeObject( kbase );
        final StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );
        ksession.setGlobal( "bGlobal",
                            false );
        ksession.setGlobal( "bArrayGlobal",
                            new boolean[]{true, true} );
        
        final Primitives p1 = new Primitives();
        p1.setIntPrimitive( 10 );
        p1.setPrimitiveBooleanArray( new boolean[]{true, true} );
        p1.setBooleanPrimitive( false );
        FactHandle p1h = ksession.insert( p1 );
        
        final Primitives p2 = new Primitives();
        p2.setPrimitiveBooleanArray( new boolean[]{true,true} );
        p2.setBooleanPrimitive( false );
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
    
    @Test
    public void testContainsByteArray() throws Exception {
        String str = "";
        str += "package org.drools.compiler;\n";

        str += "global java.util.List list;\n";
        str += "global Byte bGlobal;\n";
        str += "global Object bArrayGlobal;\n";

        str += "rule \"contains in array\"\n";
        str += "     salience 10\n";
        str += "     when\n";
        str += "         Primitives( $b : bytePrimitive == 1, $array1 :  primitiveByteArray ) \n";
        str += "         Primitives( bytePrimitive != 1, $array2 : primitiveByteArray contains bGlobal,  primitiveByteArray contains $b, ";
        str += "                     bytePrimitive memberOf $array2, bytePrimitive memberOf $array1, bytePrimitive memberOf bArrayGlobal )\n";
        str += "     then\n";
        str += "        list.add( \"ok1\" );\n";
        str += "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

        kbase = SerializationHelper.serializeObject( kbase );
        final StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );
        ksession.setGlobal( "bGlobal",
                            (byte) 1 );
        ksession.setGlobal( "bArrayGlobal",
                            new byte[]{1, 2, 3} );
        
        final Primitives p1 = new Primitives();
        p1.setPrimitiveByteArray( new byte[]{1, 2, 3} );
        p1.setBytePrimitive( (byte) 2 );
        FactHandle p1h = ksession.insert( p1 );
        
        final Primitives p2 = new Primitives();
        p2.setPrimitiveByteArray( new byte[]{1, 2, 3} );
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
    
    @Test
    public void testNotContainsByteArray() throws Exception {
        String str = "";
        str += "package org.drools.compiler;\n";

        str += "global java.util.List list;\n";
        str += "global Byte bGlobal;\n";
        str += "global Object bArrayGlobal;\n";

        str += "rule \"contains in array\"\n";
        str += "     salience 10\n";
        str += "     when\n";
        str += "         Primitives( $b : bytePrimitive == 1, $array1 :  primitiveByteArray ) \n";
        str += "         Primitives( bytePrimitive != 1, $array2 : primitiveByteArray not contains bGlobal,  primitiveByteArray not contains $b, ";
        str += "                     bytePrimitive not memberOf $array2, bytePrimitive not memberOf $array1, bytePrimitive not memberOf bArrayGlobal )\n";
        str += "     then\n";
        str += "        list.add( \"ok1\" );\n";
        str += "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

        kbase = SerializationHelper.serializeObject( kbase );
        final StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );
        ksession.setGlobal( "bGlobal",
                            (byte) 1 );
        ksession.setGlobal( "bArrayGlobal",
                            new byte[]{4, 5, 6} );
        
        final Primitives p1 = new Primitives();
        p1.setPrimitiveByteArray( new byte[]{4, 5, 6} );
        p1.setBytePrimitive( (byte) 2 );
        FactHandle p1h = ksession.insert( p1 );
        
        final Primitives p2 = new Primitives();
        p2.setPrimitiveByteArray( new byte[]{4, 5, 6} );
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
    
    @Test
    public void testContainsShortArray() throws Exception {
        String str = "";
        str += "package org.drools.compiler;\n";

        str += "global java.util.List list;\n";
        str += "global Short sGlobal;\n";
        str += "global Object sArrayGlobal;\n";

        str += "rule \"contains in array\"\n";
        str += "     salience 10\n";
        str += "     when\n";
        str += "         Primitives( $s : shortPrimitive == 1, $array1 :  primitiveShortArray ) \n";
        str += "         Primitives( shortPrimitive != 1, $array2 : primitiveShortArray contains sGlobal,  primitiveShortArray contains $s, ";
        str += "                     shortPrimitive memberOf $array2, shortPrimitive memberOf $array1, shortPrimitive memberOf sArrayGlobal )\n";
        str += "     then\n";
        str += "        list.add( \"ok1\" );\n";
        str += "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

        kbase = SerializationHelper.serializeObject( kbase );
        final StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );
        ksession.setGlobal( "sGlobal",
                            (short) 1 );
        ksession.setGlobal( "sArrayGlobal",
                            new short[]{1, 2, 3} );
        
        final Primitives p1 = new Primitives();
        p1.setPrimitiveShortArray( new short[]{1, 2, 3} );
        p1.setShortPrimitive( (short) 2 );
        FactHandle p1h = ksession.insert( p1 );
        
        final Primitives p2 = new Primitives();
        p2.setPrimitiveShortArray( new short[]{1, 2, 3} );
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
    
    @Test
    public void testNotContainsShortArray() throws Exception {
        String str = "";
        str += "package org.drools.compiler;\n";

        str += "global java.util.List list;\n";
        str += "global Short sGlobal;\n";
        str += "global Object sArrayGlobal;\n";

        str += "rule \"contains in array\"\n";
        str += "     salience 10\n";
        str += "     when\n";
        str += "         Primitives( $s : shortPrimitive == 1, $array1 :  primitiveShortArray ) \n";
        str += "         Primitives( shortPrimitive != 1, $array2 : primitiveShortArray not contains sGlobal,  primitiveShortArray not contains $s, ";
        str += "                     shortPrimitive not memberOf $array2, shortPrimitive not memberOf $array1, shortPrimitive not memberOf sArrayGlobal )\n";
        str += "     then\n";
        str += "        list.add( \"ok1\" );\n";
        str += "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

        kbase = SerializationHelper.serializeObject( kbase );
        final StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );
        ksession.setGlobal( "sGlobal",
                            (short) 1 );
        ksession.setGlobal( "sArrayGlobal",
                            new short[]{4, 5, 6} );
        
        final Primitives p1 = new Primitives();
        p1.setPrimitiveShortArray( new short[]{4, 5, 6} );
        p1.setShortPrimitive( (short) 2 );
        FactHandle p1h = ksession.insert( p1 );
        
        final Primitives p2 = new Primitives();
        p2.setPrimitiveShortArray( new short[]{4, 5, 6} );
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
    
    @Test
    public void testContainsCharArray() throws Exception {
        String str = "";
        str += "package org.drools.compiler;\n";

        str += "global java.util.List list;\n";
        str += "global Character cGlobal;\n";
        str += "global Object cArrayGlobal;\n";

        str += "rule \"contains in array\"\n";
        str += "     salience 10\n";
        str += "     when\n";
        str += "         Primitives( $c : charPrimitive == 'c', $array1 :  primitiveCharArray ) \n";
        str += "         Primitives( charPrimitive != 'c', $array2 : primitiveCharArray contains cGlobal,  primitiveCharArray contains $c, ";
        str += "                     charPrimitive memberOf $array2, charPrimitive memberOf $array1, charPrimitive memberOf cArrayGlobal )\n";
        str += "     then\n";
        str += "        list.add( \"ok1\" );\n";
        str += "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

        kbase = SerializationHelper.serializeObject( kbase );
        final StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );
        ksession.setGlobal( "cGlobal",
                            'c'  );
        ksession.setGlobal( "cArrayGlobal",
                            new char[]{ 'a', 'b', 'c'} );
        
        final Primitives p1 = new Primitives();
        p1.setPrimitiveCharArray( new char[]{ 'a', 'b', 'c'} );
        p1.setCharPrimitive( 'a' );
        FactHandle p1h = ksession.insert( p1 );
        
        final Primitives p2 = new Primitives();
        p2.setPrimitiveCharArray( new char[]{ 'a', 'b', 'c'} );
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
    
    @Test
    public void testNotContainsCharArray() throws Exception {
        String str = "";
        str += "package org.drools.compiler;\n";

        str += "global java.util.List list;\n";
        str += "global Character cGlobal;\n";
        str += "global Object cArrayGlobal;\n";

        str += "rule \"contains in array\"\n";
        str += "     salience 10\n";
        str += "     when\n";
        str += "         Primitives( $c : charPrimitive == 'c', $array1 :  primitiveCharArray ) \n";
        str += "         Primitives( charPrimitive != 'c', $array2 : primitiveCharArray not contains cGlobal,  primitiveCharArray not contains $c, ";
        str += "                     charPrimitive not memberOf $array2, charPrimitive not memberOf $array1, charPrimitive not memberOf cArrayGlobal )\n";
        str += "     then\n";
        str += "        list.add( \"ok1\" );\n";
        str += "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

        kbase = SerializationHelper.serializeObject( kbase );
        final StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );
        ksession.setGlobal( "cGlobal",
                            'c'  );
        ksession.setGlobal( "cArrayGlobal",
                            new char[]{ 'd', 'e', 'f'} );
        
        final Primitives p1 = new Primitives();
        p1.setPrimitiveCharArray( new char[]{ 'd', 'e', 'f'} );
        p1.setCharPrimitive( 'a' );
        FactHandle p1h = ksession.insert( p1 );
        
        final Primitives p2 = new Primitives();
        p2.setPrimitiveCharArray( new char[]{ 'd', 'e', 'f'} );
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
    
    @Test
    public void testContainsIntArray() throws Exception {
        String str = "";
        str += "package org.drools.compiler;\n";

        str += "global java.util.List list;\n";
        str += "global Integer iGlobal;\n";
        str += "global Object iArrayGlobal;\n";

        str += "rule \"contains in array\"\n";
        str += "     salience 10\n";
        str += "     when\n";
        str += "         Primitives( $i : intPrimitive == 10, $array1 :  primitiveIntArray ) \n";
        str += "         Primitives( intPrimitive != 10, $array2 : primitiveIntArray contains iGlobal,  primitiveIntArray contains $i, ";
        str += "                     intPrimitive memberOf $array2, intPrimitive memberOf $array1, intPrimitive memberOf iArrayGlobal )\n";
        str += "     then\n";
        str += "        list.add( \"ok1\" );\n";
        str += "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

        kbase = SerializationHelper.serializeObject( kbase );
        final StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );
        
        ksession.setGlobal( "iGlobal",
                            10 );
        ksession.setGlobal( "iArrayGlobal",
                            new int[]{ 5, 10, 20} );
        
        final Primitives p1 = new Primitives();
        p1.setPrimitiveIntArray( new int[]{ 5, 10, 20} );
        p1.setIntPrimitive( 5 );
        FactHandle p1h = ksession.insert( p1 );
        
        final Primitives p2 = new Primitives();
        p2.setPrimitiveIntArray( new int[]{ 5, 10, 20} );
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
    
    @Test
    public void testNotContainsIntArray() throws Exception {
        String str = "";
        str += "package org.drools.compiler;\n";

        str += "global java.util.List list;\n";
        str += "global Integer iGlobal;\n";
        str += "global Object iArrayGlobal;\n";

        str += "rule \"contains in array\"\n";
        str += "     salience 10\n";
        str += "     when\n";
        str += "         Primitives( $i : intPrimitive == 10, $array1 :  primitiveIntArray ) \n";
        str += "         Primitives( intPrimitive != 10, $array2 : primitiveIntArray not contains iGlobal,  primitiveIntArray not contains $i, ";
        str += "                     intPrimitive not memberOf $array2, intPrimitive not memberOf $array1, intPrimitive not memberOf iArrayGlobal )\n";
        str += "     then\n";
        str += "        list.add( \"ok1\" );\n";
        str += "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

        kbase = SerializationHelper.serializeObject( kbase );
        final StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );
        
        ksession.setGlobal( "iGlobal",
                            10 );
        ksession.setGlobal( "iArrayGlobal",
                            new int[]{ 40, 50, 60} );
        
        final Primitives p1 = new Primitives();
        p1.setPrimitiveIntArray( new int[]{ 40, 50, 60} );
        p1.setIntPrimitive( 5 );
        FactHandle p1h = ksession.insert( p1 );
        
        final Primitives p2 = new Primitives();
        p2.setPrimitiveIntArray( new int[]{ 40, 50, 60} );
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
    
    @Test
    public void testContainsLongArray() throws Exception {
        String str = "";
        str += "package org.drools.compiler;\n";

        str += "global java.util.List list;\n";
        str += "global Long lGlobal;\n";
        str += "global Object lArrayGlobal;\n";

        str += "rule \"contains in array\"\n";
        str += "     salience 10\n";
        str += "     when\n";
        str += "         Primitives( $l : longPrimitive == 10, $array1 :  primitiveLongArray ) \n";
        str += "         Primitives( longPrimitive != 10, $array2 : primitiveLongArray contains lGlobal,  primitiveLongArray contains $l, ";
        str += "                     longPrimitive memberOf $array2, longPrimitive memberOf $array1, longPrimitive memberOf lArrayGlobal )\n";
        str += "     then\n";
        str += "        list.add( \"ok1\" );\n";
        str += "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

        kbase = SerializationHelper.serializeObject( kbase );
        final StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );
        
        ksession.setGlobal( "lGlobal",
                            10l );
        ksession.setGlobal( "lArrayGlobal",
                            new long[]{ 5, 10, 20}  );
        
        final Primitives p1 = new Primitives();
        p1.setPrimitiveLongArray( new long[]{ 5, 10, 20} );
        p1.setLongPrimitive( 5 );
        FactHandle p1h = ksession.insert( p1 );
        
        final Primitives p2 = new Primitives();
        p2.setLongPrimitive( 10 );
        p2.setPrimitiveLongArray( new long[]{ 5, 10, 20} );
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
    
    @Test
    public void testNotContainsLongArray() throws Exception {
        String str = "";
        str += "package org.drools.compiler;\n";

        str += "global java.util.List list;\n";
        str += "global Long lGlobal;\n";
        str += "global Object lArrayGlobal;\n";

        str += "rule \"contains in array\"\n";
        str += "     salience 10\n";
        str += "     when\n";
        str += "         Primitives( $l : longPrimitive == 10, $array1 :  primitiveLongArray ) \n";
        str += "         Primitives( longPrimitive != 10, $array2 : primitiveLongArray not contains lGlobal,  primitiveLongArray not contains $l, ";
        str += "                     longPrimitive not memberOf $array2, longPrimitive not memberOf $array1, longPrimitive not memberOf lArrayGlobal )\n";
        str += "     then\n";
        str += "        list.add( \"ok1\" );\n";
        str += "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

        kbase = SerializationHelper.serializeObject( kbase );
        final StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );
        
        ksession.setGlobal( "lGlobal",
                            10l );
        ksession.setGlobal( "lArrayGlobal",
                            new long[]{ 40, 50, 60}  );
        
        final Primitives p1 = new Primitives();
        p1.setPrimitiveLongArray( new long[]{ 40, 50, 60} );
        p1.setLongPrimitive( 5 );
        FactHandle p1h = ksession.insert( p1 );
        
        final Primitives p2 = new Primitives();
        p2.setLongPrimitive( 10 );
        p2.setPrimitiveLongArray( new long[]{ 40, 50, 60} );
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
    
    @Test
    public void testContainsFloatArray() throws Exception {
        String str = "";
        str += "package org.drools.compiler;\n";

        str += "global java.util.List list;\n";
        str += "global Float fGlobal;\n";
        str += "global Object fArrayGlobal;\n";

        str += "rule \"contains in array\"\n";
        str += "     salience 10\n";
        str += "     when\n";
        str += "         Primitives( $f : floatPrimitive == 10, $array1 :  primitiveFloatArray ) \n";
        str += "         Primitives( floatPrimitive != 10, $array2 : primitiveFloatArray contains fGlobal,  primitiveFloatArray contains $f, ";
        str += "                     floatPrimitive memberOf $array2, floatPrimitive memberOf $array1, floatPrimitive memberOf fArrayGlobal )\n";
        str += "     then\n";
        str += "        list.add( \"ok1\" );\n";
        str += "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

        kbase = SerializationHelper.serializeObject( kbase );
        final StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal( "list",
                                 list );
        
        ksession.setGlobal( "fGlobal",
                            10.0f );
        ksession.setGlobal( "fArrayGlobal",
                            new float[]{ 5, 10, 20} );
        
        final Primitives p1 = new Primitives();
        p1.setFloatPrimitive( 5 );
        p1.setPrimitiveFloatArray( new float[]{ 5, 10, 20} );
        FactHandle p1h = ksession.insert( p1 );
        
        final Primitives p2 = new Primitives();
        p2.setFloatPrimitive( 10 );
        p2.setPrimitiveFloatArray( new float[]{ 5, 10, 20} );
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
    
    @Test
    public void testNotContainsFloatArray() throws Exception {
        String str = "";
        str += "package org.drools.compiler;\n";

        str += "global java.util.List list;\n";
        str += "global Float fGlobal;\n";
        str += "global Object fArrayGlobal;\n";

        str += "rule \"contains in array\"\n";
        str += "     salience 10\n";
        str += "     when\n";
        str += "         Primitives( $f : floatPrimitive == 10, $array1 :  primitiveFloatArray ) \n";
        str += "         Primitives( floatPrimitive != 10, $array2 : primitiveFloatArray not contains fGlobal,  primitiveFloatArray not contains $f, ";
        str += "                     floatPrimitive not memberOf $array2, floatPrimitive not memberOf $array1, floatPrimitive not memberOf fArrayGlobal )\n";
        str += "     then\n";
        str += "        list.add( \"ok1\" );\n";
        str += "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

        kbase = SerializationHelper.serializeObject( kbase );
        final StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal( "list",
                                 list );
        
        ksession.setGlobal( "fGlobal",
                            10.0f );
        ksession.setGlobal( "fArrayGlobal",
                            new float[]{ 40, 50, 60} );
        
        final Primitives p1 = new Primitives();
        p1.setFloatPrimitive( 5 );
        p1.setPrimitiveFloatArray( new float[]{ 40, 50, 60} );
        FactHandle p1h = ksession.insert( p1 );
        
        final Primitives p2 = new Primitives();
        p2.setFloatPrimitive( 10 );
        p2.setPrimitiveFloatArray( new float[]{ 40, 50, 60} );
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
       
    
    @Test
    public void testContainsDoubleArray() throws Exception {
        String str = "";
        str += "package org.drools.compiler;\n";

        str += "global java.util.List list;\n";
        str += "global Double dGlobal;\n";
        str += "global Object dArrayGlobal;\n";

        str += "rule \"contains in array\"\n";
        str += "     salience 10\n";
        str += "     when\n";
        str += "         Primitives( $d : doublePrimitive == 10, $array1 :  primitiveDoubleArray ) \n";
        str += "         Primitives( doublePrimitive != 10, $array2 : primitiveDoubleArray contains dGlobal,  primitiveDoubleArray contains $d, ";
        str += "                     doublePrimitive memberOf $array2, doublePrimitive memberOf $array1, doublePrimitive memberOf dArrayGlobal )\n";
        str += "     then\n";
        str += "        list.add( \"ok1\" );\n";
        str += "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

        kbase = SerializationHelper.serializeObject( kbase );
        final StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal( "list",
                                 list );
        
        ksession.setGlobal( "dGlobal",
                            10.0d );
        
        ksession.setGlobal( "dArrayGlobal",
                            new double[]{ 5, 10, 20} );
        
        final Primitives p1 = new Primitives();
        p1.setPrimitiveDoubleArray( new double[]{ 5, 10, 20} );
        p1.setDoublePrimitive( 5 );
        FactHandle p1h = ksession.insert( p1 );
        
        final Primitives p2 = new Primitives();
        p2.setDoublePrimitive( 10 );
        p2.setPrimitiveDoubleArray( new double[]{ 5, 10, 20} );
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
    
    @Test
    public void testNotContainsDoubleArray() throws Exception {
        String str = "";
        str += "package org.drools.compiler;\n";

        str += "global java.util.List list;\n";
        str += "global Double dGlobal;\n";
        str += "global Object dArrayGlobal;\n";

        str += "rule \"contains in array\"\n";
        str += "     salience 10\n";
        str += "     when\n";
        str += "         Primitives( $d : doublePrimitive == 10, $array1 :  primitiveDoubleArray ) \n";
        str += "         Primitives( doublePrimitive != 10, $array2 : primitiveDoubleArray not contains dGlobal,  primitiveDoubleArray not contains $d, ";
        str += "                     doublePrimitive not memberOf $array2, doublePrimitive not memberOf $array1, doublePrimitive not memberOf dArrayGlobal )\n";
        str += "     then\n";
        str += "        list.add( \"ok1\" );\n";
        str += "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

        kbase = SerializationHelper.serializeObject( kbase );
        final StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal( "list",
                                 list );
        
        ksession.setGlobal( "dGlobal",
                            10.0d );
        
        ksession.setGlobal( "dArrayGlobal",
                            new double[]{ 40, 50, 60} );
        
        final Primitives p1 = new Primitives();
        p1.setPrimitiveDoubleArray( new double[]{ 40, 50, 60} );
        p1.setDoublePrimitive( 5 );
        FactHandle p1h = ksession.insert( p1 );
        
        final Primitives p2 = new Primitives();
        p2.setDoublePrimitive( 10 );
        p2.setPrimitiveDoubleArray( new double[]{ 40, 50, 60} );
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
