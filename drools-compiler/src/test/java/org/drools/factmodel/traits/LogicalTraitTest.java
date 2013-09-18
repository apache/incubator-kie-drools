package org.drools.factmodel.traits;

/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.drools.ClassObjectFilter;
import org.drools.CommonTestMethodBase;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.common.InternalFactHandle;
import org.drools.definition.type.FactType;
import org.drools.integrationtests.SerializationHelper;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

@RunWith(Parameterized.class)
public class LogicalTraitTest extends CommonTestMethodBase {


    public TraitFactory.VirtualPropertyMode mode;

    @Parameterized.Parameters
    public static Collection modes() {
        return Arrays.asList( new TraitFactory.VirtualPropertyMode[][]
                                      {
                                              { TraitFactory.VirtualPropertyMode.MAP },
                                              { TraitFactory.VirtualPropertyMode.TRIPLES }
                                      } );
    }

    public LogicalTraitTest( TraitFactory.VirtualPropertyMode m ) {
        this.mode = m;
    }


    @Test
    public void testShadowAlias() {

        KnowledgeBuilder kbuilderImpl = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilderImpl.add( ResourceFactory.newClassPathResource ( "org/drools/factmodel/traits/testTraitedAliasing.drl" ), ResourceType.DRL );
        if ( kbuilderImpl.hasErrors() ) {
            fail( kbuilderImpl.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilderImpl.getKnowledgePackages() );

        TraitFactory.setMode( mode, kbase );

        StatefulKnowledgeSession ks = kbase.newStatefulKnowledgeSession();

        ArrayList list = new ArrayList(  );
        ks.setGlobal( "list", list );

        ks.fireAllRules();

        for ( Object o : ks.getObjects() ) {
            System.out.println( o );
        }


        try {
            ks = SerializationHelper.getSerialisedStatefulKnowledgeSession( ks, true );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }

        System.out.println( list );
        assertFalse( list.contains( false ) );
        assertEquals( 8, list.size() );
    }






    @Test
    public void testShadowAliasTraitOnClass() {

        String drl = "package org.drools.test; \n" +
                     "import org.drools.factmodel.traits.*; \n" +
                     "import org.drools.factmodel.traits.Trait; \n" +
                     "" +
                     "global java.util.List list; \n" +
                     "" +
                     "declare trait X \n" +
                     "  fld : T \n" +
                     "end \n" +
                     "" +
                     "declare Y \n" +
                     "@Traitable( logical = true ) \n" +
                     "  fld : K \n" +
                     "end \n" +
                     "" +
                     "declare trait T @Trait( logical=true ) end \n" +
                     "declare K @Traitable() end \n" +
                     "" +
                     "rule Don \n" +
                     "when \n" +
                     "then \n" +
                     "  Y y = new Y( new K() ); \n" +
                     "  don( y, X.class ); \n" +
                     "end \n" +
                     "" +
                     "rule Check \n" +
                     "when \n" +
                     "  X( fld isA T ) \n" +
                     "then \n" +
                     "  list.add( \"ok\" );" +
                     "end \n";

        KnowledgeBuilder kbuilderImpl = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilderImpl.add( ResourceFactory.newByteArrayResource( drl.getBytes() ), ResourceType.DRL );
        if ( kbuilderImpl.hasErrors() ) {
            fail( kbuilderImpl.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilderImpl.getKnowledgePackages() );

        TraitFactory.setMode( mode, kbase );

        StatefulKnowledgeSession ks = kbase.newStatefulKnowledgeSession();
        ArrayList list = new ArrayList();
        ks.setGlobal( "list", list );

        ks.fireAllRules();
        for ( Object o : ks.getObjects() ) {
            System.out.println( o );
        }
        assertEquals( Arrays.asList( "ok" ), list );

        try {
            ks = SerializationHelper.getSerialisedStatefulKnowledgeSession( ks, true );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }

    }


    @Test
    public void testShadowAliasClassOnTrait() {

        String drl = "package org.drools.test; \n" +
                     "import org.drools.factmodel.traits.*; \n" +
                     "import org.drools.factmodel.traits.Trait; \n" +
                     "" +
                     "global java.util.List list; \n" +
                     "" +
                     "declare trait X \n" +
                     "  fld : K \n" +
                     "end \n" +
                     "" +
                     "declare Y \n" +
                     "@Traitable( logical = true ) \n" +
                     "  fld : T \n" +
                     "end \n" +
                     "" +
                     "declare trait T @Trait( logical=true ) end \n" +
                     "declare K @Traitable() end \n" +
                     "" +
                     "rule Don \n" +
                     "when \n" +
                     "then \n" +
                     "  K k = new K(); \n" +
                     "  T t = don( k, T.class ); \n" +
                     "  Y y = new Y( t ); \n" +
                     "  don( y, X.class ); \n" +
                     "end \n" +
                     "" +
                     "rule Check \n" +
                     "salience 1 \n" +
                     "when \n" +
                     "  $x : Y( fld isA T ) \n" +
                     "then \n" +
                     "  System.err.println( $x.getFld() ); \n" +
                     "  list.add( \"ok1\" );" +
                     "end \n" +
                     "" +
                     "rule Check2 \n" +
                     "when \n" +
                     "  $x : X( fld isA T ) \n" +
                     "then \n" +
                     "  System.err.println( $x.getFld() ); \n" +
                     "  list.add( \"ok2\" );" +
                     "end \n" +
                     "" +
                     "";

        KnowledgeBuilder kbuilderImpl = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilderImpl.add( ResourceFactory.newByteArrayResource( drl.getBytes() ), ResourceType.DRL );
        if ( kbuilderImpl.hasErrors() ) {
            fail( kbuilderImpl.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilderImpl.getKnowledgePackages() );

        TraitFactory.setMode( mode, kbase );

        StatefulKnowledgeSession ks = kbase.newStatefulKnowledgeSession();
        ArrayList list = new ArrayList();
        ks.setGlobal( "list", list );

        ks.fireAllRules();
        for ( Object o : ks.getObjects() ) {
            System.out.println( o );
        }
        assertEquals( Arrays.asList( "ok1", "ok2" ), list );

        try {
            ks = SerializationHelper.getSerialisedStatefulKnowledgeSession( ks, true );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }

    }




    @Test
    public void testShadowAliasTraitOnTrait() {

        String drl = "package org.drools.test; \n" +
                     "import org.drools.factmodel.traits.*; \n" +
                     "import org.drools.factmodel.traits.Trait; \n" +
                     "" +
                     "global java.util.List list; \n" +
                     "" +
                     "declare trait X \n" +
                     "  fld : A \n" +
                     "end \n" +
                     "" +
                     "declare Y \n" +
                     "@Traitable( logical = true ) \n" +
                     "  fld : B \n" +
                     "end \n" +
                     "" +
                     "declare trait A @Trait( logical=true ) end \n" +
                     "declare trait B @Trait( logical=true ) end \n" +
                     "declare K @Traitable() end \n" +
                     "" +
                     "rule Don \n" +
                     "when \n" +
                     "then \n" +
                     "  K k = new K(); \n" +
                     "  B b = don( k, B.class ); \n" +
                     "  Y y = new Y( b ); \n" +
                     "  don( y, X.class ); \n" +
                     "end \n" +
                     "" +
                     "rule Check \n" +
                     "salience 1 \n" +
                     "when \n" +
                     "  $x : Y( fld isA B, fld isA A ) \n" +
                     "then \n" +
                     "  list.add( \"ok\" ); \n" +
                     "end \n" +
                     "" +
                     "" +
                     "";

        KnowledgeBuilder kbuilderImpl = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilderImpl.add( ResourceFactory.newByteArrayResource( drl.getBytes() ), ResourceType.DRL );
        if ( kbuilderImpl.hasErrors() ) {
            fail( kbuilderImpl.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilderImpl.getKnowledgePackages() );

        TraitFactory.setMode( mode, kbase );

        StatefulKnowledgeSession ks = kbase.newStatefulKnowledgeSession();
        ArrayList list = new ArrayList();
        ks.setGlobal( "list", list );

        ks.fireAllRules();
        for ( Object o : ks.getObjects() ) {
            System.out.println( o );
        }
        assertEquals( Arrays.asList( "ok" ), list );

        try {
            ks = SerializationHelper.getSerialisedStatefulKnowledgeSession( ks, true );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }

    }




    @Test
    public void initializationConflictManagement() {
        String drl = "package org.drools.test; \n" +
                     "" +
                     "global java.util.List list; \n" +
                     "global java.util.List list2; \n" +
                     "" +
                     "declare trait A name : String = \"1\" age : Integer = 18 end \n" +
                     "declare trait B name : String = \"5\" age : Integer = 50 end \n" +
                     "declare trait C extends A,B name : String = \"7\" age : Integer = 37 end \n" +
                     "" +
                     "declare X @Traitable( logical = true ) name : String end \n" +
                     "" +
                     "" +
                     "rule Init \n" +
                     "when \n" +
                     "then \n" +
                     "  X x = new X(); \n" +
                     "  A a = don( x, A.class ); \n" +
                            // default 1, from A
                     "      list.add( x.getName() ); \n" +
                     "      list2.add( a.getAge() ); \n" +
                     "  B b = don( x, B.class ); \n" +
                            // conflicting defaults A and B, nullify
                     "      list.add( x.getName() ); \n" +
                     "      list2.add( b.getAge() ); \n" +
                     "end \n" +
                     "" +
                     "rule Later \n" +
                     "no-loop \n" +
                     "when \n" +
                     "  $x : X() \n" +
                     "then \n" +
                     "  $x.setName( \"xyz\" ); \n" +
                            // set to "xyz"
                     "      list.add( $x.getName() ); \n" +
                     "  C c = don( $x, C.class ); \n" +
                            // keep "xyz" even if C has a default
                     "      list.add( $x.getName() ); \n" +
                     "      list2.add( c.getAge() ); \n" +
                     "  $x.setName( null ); \n" +
                     "  c.setAge( 99 ); \n" +
                            // now revert to default by current most specific type, C
                     "      list.add( $x.getName() ); \n" +
                     "      list2.add( c.getAge() ); \n" +
                     "  c.setName( \"aaa\" ); \n" +
                     "  c.setAge( null ); \n" +
                            // set to "aaa"
                     "      list.add( $x.getName() ); \n" +
                     "      list2.add( c.getAge() ); \n" +
                     "end \n" +
                     "" +
                     "";
        KnowledgeBase knowledgeBase = loadKnowledgeBaseFromString( drl );
        TraitFactory.setMode( mode, knowledgeBase );

        StatefulKnowledgeSession knowledgeSession = knowledgeBase.newStatefulKnowledgeSession();
        ArrayList list = new ArrayList();
        ArrayList list2 = new ArrayList();
        knowledgeSession.setGlobal( "list", list );
        knowledgeSession.setGlobal( "list2", list2 );

        knowledgeSession.fireAllRules();

        for ( Object o : knowledgeSession.getObjects() ) {
            System.out.println( o );
        }

        System.out.println( list );
        System.out.println( list2 );
        assertEquals( Arrays.asList( "1", null, "xyz", "xyz", "7", "aaa" ), list );
        assertEquals( Arrays.asList( 18, null, 37, 99, 37 ), list2 );

        try {
            knowledgeSession = SerializationHelper.getSerialisedStatefulKnowledgeSession( knowledgeSession, true );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }




    @Test
    public void testInitializationConflictManagementPrimitiveTypes() {
        String drl = "package org.drools.test; \n" +
                     "" +
                     "global java.util.List list; \n" +
                     "global java.util.List list2; \n" +
                     "" +
                     "declare trait A rate : double = 1.0 age : int = 18 end \n" +
                     "declare trait B rate : double = 5.0 age : int = 50 end \n" +
                     "declare trait C extends A,B rate : double = 7 age : int = 37 end \n" +
                     "" +
                     "declare X @Traitable( logical = true ) rate : double end \n" +
                     "" +
                     "" +
                     "rule Init \n" +
                     "when \n" +
                     "then \n" +
                     "  X x = new X(); \n" +
                     "  A a = don( x, A.class ); \n" +
                     // default 1, from A
                     "      list.add( x.getRate() ); \n" +
                     "      list2.add( a.getAge() ); \n" +
                     "  B b = don( x, B.class ); \n" +
                     // conflicting defaults A and B, nullify
                     "      list.add( x.getRate() ); \n" +
                     "      list2.add( b.getAge() ); \n" +
                     "end \n" +
                     "" +
                     "rule Later \n" +
                     "no-loop \n" +
                     "when \n" +
                     "  $x : X() \n" +
                     "then \n" +
                     "  $x.setRate( 16.3 ); \n" +
                     // set to "xyz"
                     "      list.add( $x.getRate() ); \n" +
                     "  C c = don( $x, C.class ); \n" +
                     // keep "xyz" even if C has a default
                     "      list.add( $x.getRate() ); \n" +
                     "      list2.add( c.getAge() ); \n" +
                     "  $x.setRate( 0.0 ); \n" +
                     "  c.setAge( 99 ); \n" +
                     "      list.add( $x.getRate() ); \n" +
                     "      list2.add( c.getAge() ); \n" +
                     "  c.setRate( -0.72 ); \n" +
                     "  c.setAge( 0 ); \n" +
                     // set to "aaa"
                     "      list.add( $x.getRate() ); \n" +
                     "      list2.add( c.getAge() ); \n" +
                     "end \n" +
                     "" +
                     "";
        KnowledgeBase knowledgeBase = loadKnowledgeBaseFromString( drl );
        TraitFactory.setMode( mode, knowledgeBase );

        StatefulKnowledgeSession knowledgeSession = knowledgeBase.newStatefulKnowledgeSession();
        ArrayList list = new ArrayList();
        ArrayList list2 = new ArrayList();
        knowledgeSession.setGlobal( "list", list );
        knowledgeSession.setGlobal( "list2", list2 );

        knowledgeSession.fireAllRules();

        for ( Object o : knowledgeSession.getObjects() ) {
            System.out.println( o );
        }

        System.out.println( list );
        System.out.println( list2 );
        assertEquals( Arrays.asList( 1.0, 0.0, 16.3, 16.3, 0.0, -0.72 ), list );
        assertEquals( Arrays.asList( 18, 0, 37, 99, 0 ), list2 );

        try {
            knowledgeSession = SerializationHelper.getSerialisedStatefulKnowledgeSession( knowledgeSession, true );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }

        knowledgeSession.dispose();
    }




    @Test
    public void testFieldTypeDonMap() {
        String drl = "package org.drools.test; \n" +
                     "" +
                     "global java.util.List list; \n" +
                     "global java.util.List list2; \n" +
                     "" +
                     "declare trait T " +
                     "  hardShort : short = 1 \n" +
                     "  hardChar : char = 1 \n" +
                     "  hardByte : byte = 1 \n" +
                     "  hardInt : int = 1 \n" +
                     "  hardLong : long = 1 \n" +
                     "  hardFloat : float = 1.0f \n" +
                     "  hardDouble : double = 1.0 \n" +
                     "  hardBoolean : boolean = true \n" +
                     "  hardString : String = \"x\" \n" +
                     "  softShort : short = 1 \n" +
                     "  softChar : char = 1 \n" +
                     "  softByte : byte = 1 \n" +
                     "  softInt : int = 1 \n" +
                     "  softLong : long = 1 \n" +
                     "  softFloat : float = 1.0f \n" +
                     "  softDouble : double = 1.0 \n" +
                     "  softBoolean : boolean = true \n" +
                     "  softString : String = \"x\" \n" +
                     "end \n" +
                     "" +
                     "declare X @Traitable( logical = true ) " +
                     "  hardShort : short  \n" +
                     "  hardChar : char  \n" +
                     "  hardByte : byte  \n" +
                     "  hardInt : int \n" +
                     "  hardLong : long  \n" +
                     "  hardFloat : float  \n" +
                     "  hardDouble : double  \n" +
                     "  hardBoolean : boolean  \n" +
                     "end \n" +
                     "" +
                     "" +
                     "rule Init \n" +
                     "when \n" +
                     "then \n" +
                     "  X x = new X(); \n" +
                     "  don( x, T.class ); \n" +
                     "end \n" +
                     "" +
                     "" +
                     "";
        KnowledgeBase knowledgeBase = loadKnowledgeBaseFromString( drl );
        TraitFactory.setMode( mode, knowledgeBase );

        StatefulKnowledgeSession knowledgeSession = knowledgeBase.newStatefulKnowledgeSession();

        knowledgeSession.fireAllRules();

        for ( Object o : knowledgeSession.getObjects() ) {
            System.out.println( o );
        }

        try {
            knowledgeSession = SerializationHelper.getSerialisedStatefulKnowledgeSession( knowledgeSession, true );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }


        knowledgeSession.dispose();

    }




    @Test
    public void testDataStructs() {
        String drl = "package org.drools.test; \n" +
                     "" +
                     "global java.util.List list; \n" +
                     "global java.util.List list2; \n" +
                     "" +
                     "declare trait T \n" +
                     "  hardString : String = \"x\" \n" +
                     "  hardString : String = \"x\" \n" +
                     "  softString : String = \"x\" \n" +
                     "  hardFloat : float = 5.9f \n" +
                     "end \n" +
                     "" +
                     "declare X @Traitable( logical = true ) \n" +
                     "  id : int @key \n" +
                     "  hardString : String  = \"a\" \n" +
                     "  hardInt    : int  = 12 \n" +
                     "  hardDouble : double  = 42.0 \n" +
                     "  hardFloat : float  = 2.3f \n" +
                     "end \n" +
                     "" +
                     "rule Init \n" +
                     "when \n" +
                     "then \n" +
                     "  X x = new X( 1 ); \n" +
                     "  x.setHardFloat( 8.42f ); \n" +
                     "  insert( x ); \n" +
                     "      x.setHardDouble( -11.2 ); \n" +
                     "  X x2 = new X( 2, \"b\", 13, 44.0, 16.5f ); \n" +
                     "      x2.setHardInt( -1 ); \n" +
                     "  insert( x2 ); \n" +
                     "  don( x, T.class ); \n" +
                     "  don( x2, T.class ); \n" +
                     "end \n" +
                     "";

        KnowledgeBase knowledgeBase = loadKnowledgeBaseFromString( drl );
        TraitFactory.setMode( mode, knowledgeBase );

        StatefulKnowledgeSession knowledgeSession = knowledgeBase.newStatefulKnowledgeSession();

        knowledgeSession.fireAllRules();

        FactType X = knowledgeBase.getFactType( "org.drools.test", "X" );

        for ( Object o : knowledgeSession.getObjects() ) {
            if ( X.getFactClass().isInstance( o ) ) {
                switch ( (Integer) X.get( o, "id" ) ) {
                    case 1 :
                        assertEquals( "a", X.get( o, "hardString" ) );
                        assertEquals( 12, X.get( o, "hardInt" ) );
                        assertEquals( -11.2, X.get( o, "hardDouble" ) );
                        assertEquals( 8.42f, X.get( o, "hardFloat" ) );
                        break;
                    case 2 :
                        assertEquals( "b", X.get( o, "hardString" ) );
                        assertEquals( -1, X.get( o, "hardInt" ) );
                        assertEquals( 44.0, X.get( o, "hardDouble" ) );
                        assertEquals( 16.5f, X.get( o, "hardFloat" ) );
                        break;
                    default:
                        fail( "Unexpected id " );
                }
            }
            System.out.println( o );
        }

        try {
            knowledgeSession = SerializationHelper.getSerialisedStatefulKnowledgeSession( knowledgeSession, true );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }


        knowledgeSession.dispose();

    }



    @Test
    public void shadowAliasSelf() {

        String drl = "package org.drools.test; \n" +
                     "import org.drools.factmodel.traits.*; \n" +
                     "import org.drools.factmodel.traits.Trait; \n" +
                     "" +
                     "global java.util.List list; \n" +
                     "" +
                     "declare trait VIP \n" +
                     "@Trait( logical=true ) \n" +
                     "  friend : VIP \n" +
                     "end \n" +
                     "" +
                     "declare Pers \n" +
                     "@Traitable( logical = true ) \n" +
                     "  friend : Pers \n" +
                     "end \n" +
                     "" +
                     "rule Don \n" +
                     "when \n" +
                     "then \n" +
                     "  Pers p = new Pers(); " +
                     "  p.setFriend( p ); \n\n" +
                     "  don( p, VIP.class ); \n" +
                     "end \n" +
                     "" +
                     "rule Check \n" +
                     "salience 1 \n" +
                     "when \n" +
                     "  $x : Pers( friend isA VIP ) \n" +
                     "then \n" +
                     "  list.add( \"ok1\" );" +
                     "end \n" +
                     "" +
                     "";

        KnowledgeBuilder kbuilderImpl = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilderImpl.add( ResourceFactory.newByteArrayResource( drl.getBytes() ), ResourceType.DRL );
        if ( kbuilderImpl.hasErrors() ) {
            fail( kbuilderImpl.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilderImpl.getKnowledgePackages() );

        TraitFactory.setMode( mode, kbase );

        StatefulKnowledgeSession ks = kbase.newStatefulKnowledgeSession();
        ArrayList list = new ArrayList();
        ks.setGlobal( "list", list );

        ks.fireAllRules();
        assertEquals( Arrays.asList( "ok1" ), list );

        try {
            ks = SerializationHelper.getSerialisedStatefulKnowledgeSession( ks, true );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }

    }


    @Test
    public void traitOnSet() {

        String drl = "package org.drools.test; \n" +
                     "import org.drools.factmodel.traits.*; \n" +
                     "import org.drools.factmodel.traits.Trait; \n" +
                     "" +
                     "global java.util.List list; \n" +
                     "" +
                     "declare trait T \n" +
                     "@Trait( logical=true ) \n" +
                     "  fld : S \n" +
                     "end \n" +
                     "" +
                     "declare trait U \n" +
                     "@Trait( logical=true ) \n" +
                     "  fld : R \n" +
                     "end \n" +
                     "" +
                     "declare trait S \n" +
                     "@Trait( logical=true ) \n" +
                     "end \n" +
                     "" +
                     "declare trait R \n" +
                     "@Trait( logical=true ) \n" +
                     "end \n" +
                     "" +
                     "declare K \n" +
                     "@propertyReactive \n" +
                     "@Traitable( logical = true ) \n" +
                     "  fld : K \n" +
                     "end \n" +
                     "" +
                     "rule Don \n" +
                     "when \n" +
                     "then \n" +
                     "  K k = new K(); " +
                     "  don( k, T.class ); \n" +
                     "  modify ( k ) { \n" +
                     "    setFld( new K() ); \n" +
                     "  } \n" +
                     "end \n" +
                     "" +
                     "rule Check \n" +
                     "salience 1 \n" +
                     "when \n" +
                     "  $x : K( fld isA S, fld not isA R ) \n" +
                     "then \n" +
                     "  list.add( \"ok1\" );" +
                     "end \n" +
                     "" +
                     "rule Check2 \n" +
                     "salience 1 \n" +
                     "when \n" +
                     "  String() \n" +
                     "  $x : K( fld isA S ) \n" +
                     "then \n" +
                     "  don( $x, U.class ); \n" +
                     "end \n" +
                     "" +
                     "";

        KnowledgeBuilder kbuilderImpl = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilderImpl.add( ResourceFactory.newByteArrayResource( drl.getBytes() ), ResourceType.DRL );
        if ( kbuilderImpl.hasErrors() ) {
            fail( kbuilderImpl.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilderImpl.getKnowledgePackages() );

        TraitFactory.setMode( mode, kbase );

        StatefulKnowledgeSession ks = kbase.newStatefulKnowledgeSession();

        ArrayList list = new ArrayList();
        ks.setGlobal( "list", list );

        ks.fireAllRules();

        System.out.println( "------------- ROUND TRIP -------------" );

        try {
            ks = SerializationHelper.getSerialisedStatefulKnowledgeSession( ks, true );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }

        ks.insert( "go" );
        ks.fireAllRules();

        for ( Object o : ks.getObjects() ) {
            System.out.println( o );
        }

        assertEquals( Arrays.asList( "ok1" ), list );
    }





    @Test
    public void testShadowAliasTraitOnClassLogicalRetract() {

        String drl = "package org.drools.test; \n" +
                     "import org.drools.factmodel.traits.*; \n" +
                     "import org.drools.factmodel.traits.Trait; \n" +
                     "" +
                     "global java.util.List list; \n" +
                     "" +
                     "declare trait X \n" +
                     "  fld : T \n" +
                     "  fld2 : Q \n" +
                     "end \n" +
                     "" +
                     "declare trait W \n" +
                     "  fld : T \n" +
                     "end \n" +
                     "declare trait V \n" +
                     "  fld2 : Q \n" +
                     "end \n" +
                     "" +
                     "declare Y \n" +
                     "@Traitable( logical = true ) \n" +
                     "  fld : K \n" +
                     "  fld2 : Object \n" +
                     "end \n" +
                     "" +
                     "declare trait T @Trait( logical=true ) end \n" +
                     "declare trait Q @Trait( logical=true ) end \n" +
                     "declare K @Traitable() end \n" +
                     "" +
                     "rule Don \n" +
                     "when \n" +
                     "  $s : String( this == \"go\" ) \n" +
                     "then \n" +
                     "  K k = new K(); \n" +
                     "  Y y = new Y( k, null ); \n" +
                     "  insert( y ); \n" +
                     "  insert( k ); \n" +
                     "" +
                     "  don( k, Q.class ); \n" +
                     "" +
                     "  don( y, X.class, true ); \n" +
                     "  don( y, W.class ); \n" +
                     "  don( y, V.class ); \n" +
                     "end \n" +
                     "" +
                     "rule Check \n" +
                     "when \n" +
                     "  $x : X( this isA V, fld isA T ) \n" +
                     "then \n" +
                     "  shed( $x, V.class ); \n" +
                     "  list.add( \"ok\" );" +
                     "end \n" +
                     "" +
                     "rule Check2 \n" +
                     "salience 10 \n" +
                     "when \n" +
                     "  String( this == \"go2\" ) \n" +
                     "  Q() \n" +
                     "  not X() not V() " +
                     "then \n" +
                     "  list.add( \"ok2\" );" +
                     "end \n" +
                     "" +
                     "rule Check3 \n" +
                     "salience 5 \n" +
                     "when \n" +
                     "  String( this == \"go2\" ) \n" +
                     "  K( this isA Q ) \n" +
                     "  not X() not V() " +
                     "then \n" +
                     "  list.add( \"ok3\" );" +
                     "end \n" +
                     "" +
                     "";

        KnowledgeBuilder kbuilderImpl = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilderImpl.add( ResourceFactory.newByteArrayResource( drl.getBytes() ), ResourceType.DRL );
        if ( kbuilderImpl.hasErrors() ) {
            fail( kbuilderImpl.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilderImpl.getKnowledgePackages() );

        TraitFactory.setMode( mode, kbase );

        StatefulKnowledgeSession ks = kbase.newStatefulKnowledgeSession();
        ArrayList list = new ArrayList();
        ks.setGlobal( "list", list );

        FactHandle handle = ks.insert( "go" );

        ks.fireAllRules();
        assertEquals( Arrays.asList( "ok" ), list );

        for ( Object o : ks.getObjects() ) {
            System.out.println( o  + " >> " + (( InternalFactHandle)ks.getFactHandle( o )).getEqualityKey() );
        }

        ks.retract( handle );
        ks.fireAllRules();

        for ( Object o : ks.getObjects( new ClassObjectFilter( ks.getKnowledgeBase().getFactType( "org.drools.test", "Y" ).getFactClass() ) ) ) {
            assertTrue( o instanceof TraitableBean );
            TraitableBean tb = (TraitableBean) o;

            TraitField fld = tb._getFieldTMS().getRegisteredTraitField( "fld" );
            Set<Class<?>> types = fld.getRangeTypes();
            assertEquals( 2, types.size() );

            TraitField fld2 = tb._getFieldTMS().getRegisteredTraitField( "fld2" );
            Set<Class<?>> types2 = fld2.getRangeTypes();
            assertEquals( 1, types2.size() );
        }

        try {
            ks = SerializationHelper.getSerialisedStatefulKnowledgeSession( ks, true );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }

        ks.setGlobal( "list", list );

        ks.insert( "go2" );
        ks.fireAllRules();

        System.out.println( list );

        assertEquals( Arrays.asList( "ok", "ok2", "ok3" ), list );

    }





    @Test
    public void testShadowAliasClassOnTraitLogicalRetract() {

        String drl = "package org.drools.test; \n" +
                     "import org.drools.factmodel.traits.*; \n" +
                     "import org.drools.factmodel.traits.Trait; \n" +
                     "" +
                     "global java.util.List list; \n" +
                     "" +
                     "declare trait X \n" +
                     "  fld : K \n" +
                     "  fld2 : K \n" +
                     "end \n" +
                     "" +
                     "declare trait W \n" +
                     "  fld : Q \n" +
                     "end \n" +
                     "declare trait V \n" +
                     "  fld2 : T \n" +
                     "end \n" +
                     "" +
                     "declare Y \n" +
                     "@Traitable( logical = true ) \n" +
                     "  fld : T \n" +
                     "  fld2 : Q \n" +
                     "end \n" +
                     "" +
                     "declare trait T @Trait( logical=true ) id : int end \n" +
                     "declare trait Q @Trait( logical=true ) id : int end \n" +
                     "declare K @Traitable() id : int end \n" +
                     "" +
                     "rule Don \n" +
                     "when \n" +
                     "  $s : String( this == \"go\" ) \n" +
                     "then \n" +
                     "  K k1 = new K( 1 ); \n" +
                     "  K k2 = new K( 2 ); \n" +
                     "  T t = don( k1, T.class ); \n" +
                     "  Q q = don( k2, Q.class ); \n" +

                     "  Y y = new Y( t, q ); \n" +
                     "  insert( y ); \n" +
                     "" +
                     "  don( y, X.class, true ); \n" +
                     "end \n" +
                     "" +
                     "rule Check \n" +
                     "when \n" +
                     "  String( this == \"go\" ) \n" +
                     "  $x : X( $f1 : fld, $f2 : fld2 ) \n" +
                     "then \n" +
                     "  list.add( $f1.getId() );" +
                     "  list.add( $f2.getId() );" +
                     "end \n" +
                     "" +
                     "rule Check2\n" +
                     "when \n" +
                     "  not String( this == \"go\" ) \n" +
                     "  $x : Y( $f1 : fld, $f2 : fld2 ) \n" +
                     "then \n" +
                     "  list.add( $f1.getId() );" +
                     "  list.add( $f2.getId() );" +
                     "end \n" +
                     "";

        KnowledgeBuilder kbuilderImpl = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilderImpl.add( ResourceFactory.newByteArrayResource( drl.getBytes() ), ResourceType.DRL );
        if ( kbuilderImpl.hasErrors() ) {
            fail( kbuilderImpl.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilderImpl.getKnowledgePackages() );

        TraitFactory.setMode( mode, kbase );

        StatefulKnowledgeSession ks = kbase.newStatefulKnowledgeSession();
        ArrayList list = new ArrayList();
        ks.setGlobal( "list", list );

        FactHandle handle = ks.insert( "go" );

        ks.fireAllRules();
        assertEquals( Arrays.asList( 1, 2 ), list );

        ks.retract( handle );
        ks.fireAllRules();

        for ( Object o : ks.getObjects( new ClassObjectFilter( ks.getKnowledgeBase().getFactType( "org.drools.test", "Y" ).getFactClass() ) ) ) {
            assertTrue( o instanceof TraitableBean );
        }

        try {
            ks = SerializationHelper.getSerialisedStatefulKnowledgeSession( ks, true );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }

        System.out.println( list );
        assertEquals( Arrays.asList( 1, 2, 1, 2 ), list );

    }





    @Test
    public void testSerial() {

        String drl = "package org.drools.test; \n" +
                     "import org.drools.factmodel.traits.*; \n" +
                     "import org.drools.factmodel.traits.Trait; \n" +
                     "" +
                     "global java.util.List list; \n" +
                     "" +
                     "declare trait X end \n" +
                     "declare trait Z end \n" +
                     "" +
                     "declare Y \n" +
                     "@Traitable( ) \n" +
                     "end \n" +
                     "" +
                     "" +
                     "rule Don \n" +
                     "when \n" +
                     "then \n" +
                     "  Y y = new Y( ); \n" +
                     "  don( y, X.class ); \n" +
                     "  don( y, Z.class ); \n" +
                     "end \n" +
                     "" +
                     "";

        KnowledgeBuilder kbuilderImpl = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilderImpl.add( ResourceFactory.newByteArrayResource( drl.getBytes() ), ResourceType.DRL );
        if ( kbuilderImpl.hasErrors() ) {
            fail( kbuilderImpl.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilderImpl.getKnowledgePackages() );

        TraitFactory.setMode( mode, kbase );

        StatefulKnowledgeSession ks = kbase.newStatefulKnowledgeSession();
        ArrayList list = new ArrayList();
        ks.setGlobal( "list", list );

        ks.fireAllRules();
        for ( Object o : ks.getObjects() ) {
            System.out.println( o );
        }

        try {
            ks = SerializationHelper.getSerialisedStatefulKnowledgeSession( ks, true );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }

        for ( Object o : ks.getObjects() ) {
            System.out.println( o );
        }


    }

}
