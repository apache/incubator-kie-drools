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

package org.drools.mvel.compiler.beliefsystem.abductive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.core.BeliefSystemType;
import org.drools.core.QueryResultsImpl;
import org.drools.core.SessionConfiguration;
import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.beliefsystem.abductive.Abducible;
import org.drools.core.beliefsystem.defeasible.Defeasible;
import org.drools.core.common.EqualityKey;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.runtime.rule.impl.FlatQueryResults;
import org.drools.mvel.CommonTestMethodBase;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.conf.DeclarativeAgendaOption;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.definition.rule.Query;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.api.runtime.rule.Variable;
import org.kie.internal.utils.KieHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AbductionTest extends CommonTestMethodBase {

    protected KieSession getSessionFromString( String drlString ) {
        return getSessionFromString( drlString, null );
    }

    protected KieSession getSessionFromString( String drlString, KieBaseConfiguration kbConf ) {
        KieHelper kieHelper = new KieHelper();
        kieHelper.addContent( drlString, ResourceType.DRL );

        Results res = kieHelper.verify();
        if ( res.hasMessages( Message.Level.ERROR ) ) {
            fail( res.getMessages( Message.Level.ERROR ).toString() );
        }

        if ( kbConf == null ) {
            kbConf = KieServices.Factory.get().newKieBaseConfiguration();
        }
        kbConf.setOption( EqualityBehaviorOption.EQUALITY );
        KieBase kieBase = kieHelper.build( kbConf );


        KieSessionConfiguration ksConf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ((SessionConfiguration) ksConf).setBeliefSystemType( BeliefSystemType.DEFEASIBLE );
        return kieBase.newKieSession( ksConf, null );
    }


    @Test
    public void testAbductiveLogicWithConstructorArgs() {
        String droolsSource =
                "package org.drools.abductive.test; \n" +
                "" +
                "import " + Abducible.class.getName() + "; \n" +
                "global java.util.List list; \n" +
                "" +
                "declare Foo \n" +
                "   @Abducible \n" +
                "   id : Integer @key \n" +
                "   name : String @key \n" +
                "   value : double \n" +
                "   flag : boolean \n" +
                "end \n" +

                "query foo() \n" +
                "   @Abductive( target=Foo.class ) \n" +
                "end \n" +

                "query foo2( Integer $i, String $name ) \n" +
                "   @Abductive( target=Foo.class ) \n" +
                "   $i := Integer() from new Integer( 4 ) \n" +
                "   $name := String() " +
                "end \n" +

                "query foo3( Integer $i, String $name, double $val, boolean $bool ) \n" +
                "   @Abductive( target=Foo.class ) \n" +
                "end \n" +

                "rule Init " +
                "   salience 9999 " +
                "when " +
                "then " +
                "   System.out.println( 'Foo zero is in' ); \n" +
                "   insert( new Foo() ); \n" +
                "end " +

                "rule R1 " +
                "when " +
                "   $fx : foo() " +
                "then " +
                "   list.add( 1 ); " +
                "end \n" +
                "" +
                "rule R2 " +
                "when " +
                "   foo2( 4, $n ; ) " +
                "then " +
                "   list.add( 2 ); " +
                "end \n" +
                "" +
                "rule R3 " +
                "when " +
                "   foo3( 42, \"test2\", $dbl, $bool ; ) " +
                "then " +
                "   list.add( 3 ); " +
                "end \n" +
                "" +
                "";
        /////////////////////////////////////

        KieSession session = getSessionFromString( droolsSource );
        List list = new ArrayList();
        session.setGlobal( "list", list );

        session.insert( "john" );

        session.fireAllRules();

        for ( Object o : session.getObjects() ) {
            System.out.println( ">> " + o );
        }
        System.err.println( list );
        assertEquals( Arrays.asList( 1, 2, 3 ), list );
    }


    @Test
    public void testAbductiveLogicWithSelectiveConstructorArgs() {
        String droolsSource =
                "package org.drools.abductive.test; \n" +
                "" +
                "import " + Abducible.class.getName() + "; \n" +
                "global java.util.List list; \n" +
                "" +
                "declare Foo \n" +
                "   @Abducible \n" +
                "   id : String @key \n" +
                "   name : String @key \n" +
                "   value : double \n" +
                "   flag : boolean \n" +
                "end \n" +

                "query foo( String $name, double $val, String $x ) \n" +
                "   @Abductive( target=Foo.class, args={ $x, $name } ) \n" +
                "end \n" +

                "rule R3 " +
                "when " +
                "   $f := foo( \"name_test\", 99.0, \"id_0\" ; ) " +
                "then " +
                "   list.add( $f ); " +
                "end \n" +
                "" +
                "";
        /////////////////////////////////////

        KieSession session = getSessionFromString( droolsSource );
        List list = new ArrayList();
        session.setGlobal( "list", list );

        session.fireAllRules();

        FactType type = session.getKieBase().getFactType( "org.drools.abductive.test", "Foo" );
        for ( Object o : session.getObjects() ) {
            if ( type.getFactClass().isInstance( o ) ) {
                assertEquals( "id_0", type.get( o, "id" ) );
                assertEquals( "name_test", type.get( o, "name" ) );
                assertEquals( 0.0, type.get( o, "value" ) );
            }
        }
        System.err.println( list );
    }

    @Test
    public void testAbductiveLogicWithNonExistingArgsMapping() {
        String droolsSource =
                "package org.drools.abductive.test; \n" +
                "" +
                "import " + Abducible.class.getName() + "; \n" +
                "global java.util.List list; \n" +
                "" +
                "declare Foo \n" +
                "   @Abducible \n" +
                "   id : String @key \n" +
                "   name : String @key \n" +
                "end \n" +

                "query foo( String $name ) \n" +
                "   @Abductive( target=Foo.class, args={ $missing, $name } ) \n" +
                "end \n" +

                "";
        /////////////////////////////////////

        KieHelper kieHelper = new KieHelper();
        kieHelper.addContent( droolsSource, ResourceType.DRL );

        Results res = kieHelper.verify();
        assertEquals( 1, res.getMessages( Message.Level.ERROR ).size() );
    }

    @Test
    public void testAbductiveLogicWithWrongTypeArgsMapping() {
        String droolsSource =
                "package org.drools.abductive.test; \n" +
                "" +
                "import " + Abducible.class.getName() + "; \n" +
                "global java.util.List list; \n" +
                "" +
                "declare Foo \n" +
                "   @Abducible \n" +
                "   id : String @key \n" +
                "   name : String @key \n" +
                "end \n" +

                "query foo( String $name, int $x ) \n" +
                "   @Abductive( target=Foo.class, args={ $x, $name } ) \n" +
                "end \n" +

                "rule R0 " +
                "when " +
                "   $f := foo( \"name_test\", 99 ; ) " +
                "then " +
                "   list.add( $f ); " +
                "end \n" +

                "";
        /////////////////////////////////////

        KieHelper kieHelper = new KieHelper();
        kieHelper.addContent( droolsSource, ResourceType.DRL );

        Results res = kieHelper.verify();
        assertEquals( 1, res.getMessages( Message.Level.ERROR ).size() );
    }

    @Test
    public void testBindNonAbductiveQueryError() {
        String droolsSource =
                "package org.drools.abductive.test; \n" +
                "" +                "" +
                "query foo() \n" +
                "end \n" +
                "rule R1 " +
                "when " +
                "   $x : foo( ) " +
                "then " +
                "end \n" +
                "";
        /////////////////////////////////////

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write( ks.getResources()
                           .newByteArrayResource( droolsSource.getBytes() )
                           .setSourcePath( "drl1.drl" )
                           .setResourceType( ResourceType.DRL ) );
        KieBuilder kieBuilder = ks.newKieBuilder( kfs );
        kieBuilder.buildAll();

        Results res = kieBuilder.getResults();
        assertTrue( res.hasMessages( Message.Level.ERROR ) );

    }




    @Test
    public void testAbducedReturnBinding() {
        String droolsSource =
                "package org.drools.abductive.test; \n" +
                "" +
                "import " + Abducible.class.getName() + "; \n" +
                "global java.util.Map map; \n" +
                "" +
                "declare Foo \n" +
                "   @Abducible \n" +
                "   id : Integer @key \n" +
                "end \n" +

                "query foo( Integer $i ) \n" +
                "   @Abductive( target=Foo.class ) \n" +
                "   $i := Integer() " +
                "end \n" +

                "rule R1 " +
                "when " +
                "   $x : foo( $v ; ) " +
                "then " +
                "   map.put( $v, $x ); " +
                "end \n" +

                "";
        /////////////////////////////////////

        KieSession session = getSessionFromString( droolsSource );
        Map map = new HashMap();
        session.setGlobal( "map", map );

        session.insert( 3 );
        session.insert( 42 );
        session.insert( 11 );

        session.fireAllRules();

        System.out.println( map );
        assertTrue( map.keySet().containsAll( Arrays.asList( 3, 42, 11 ) ) );

        FactType foo = session.getKieBase().getFactType( "org.drools.abductive.test", "Foo" );
        for ( Object k : map.keySet() ) {
            Object val = map.get( k );
            assertSame( foo.getFactClass(), val.getClass() );
            assertEquals( k, foo.get( val, "id" ) );
        }

    }


    @Abducible
    public static class Bean {
        private Integer id;

        public Bean() { id = 0; }

        public Bean( Integer id ) {
            this.id = id;
        }

        public Integer getId() {
            return id;
        }

        public void setId( Integer id ) {
            this.id = id;
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;

            Bean bean = (Bean) o;

            if ( id != bean.id ) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return id;
        }

        @Override
        public String toString() {
            return "Bean{" +
                   "id=" + id +
                   '}';
        }
    }


    @Test
    public void testAbducedKnownClass() {
        String droolsSource =
                "package org.drools.abductive.test; \n" +
                "" +
                "import " + Bean.class.getCanonicalName() + ";" +
                "global java.util.Map map; \n" +
                "" +

                "query foo( Integer $i ) \n" +
                "   @Abductive( target=Bean.class ) \n" +
                "   $i := Integer() " +
                "end \n" +

                "rule R1 " +
                "when " +
                "   $x : foo( $v ; ) " +
                "then " +
                "   map.put( $v, $x ); " +
                "end \n" +
                "" +
                "";
        /////////////////////////////////////

        KieSession session = getSessionFromString( droolsSource );
        Map map = new HashMap();
        session.setGlobal( "map", map );

        session.insert( 42 );

        session.fireAllRules();

        System.out.println( map );
        assertTrue( map.containsKey( 42 ) );
        assertEquals( new Bean( 42 ), map.get( 42 ) );
    }




    @Test
    public void testAbducedWithStatus() {
        String droolsSource =
                "package org.drools.abductive.test; \n" +
                "" +
                "import " + Bean.class.getCanonicalName() + ";" +
                "global java.util.Map map; \n" +
                "" +

                "query foo( Integer $i ) \n" +
                "   @Abductive( target=Bean.class ) \n" +
                "   $i := Integer() " +
                "end \n" +

                "rule R1 " +
                "when " +
                "   $x : foo( $v ; ) " +
                "then " +
                "   map.put( $v, $x ); " +
                "end \n" +
                "" +
                "";
        /////////////////////////////////////

        KieSession session = getSessionFromString( droolsSource );
        Map map = new HashMap();
        session.setGlobal( "map", map );

        session.insert( 42 );
        session.insert( 11 );
        Bean b = new Bean( 11 );
        session.insert( b );

        session.fireAllRules();

        System.out.println( map );
        assertTrue( map.keySet().containsAll( Arrays.asList( 11, 42 ) ) );
        assertEquals( 2, map.size() );

        Bean b11 = (Bean) map.get( 11 );
        InternalFactHandle f11 = (( InternalFactHandle ) session.getFactHandle( b11 ));
        assertSame( b, b11 );

        Bean b42 = (Bean) map.get( 42 );
        InternalFactHandle f42 = ( InternalFactHandle ) session.getFactHandle( b42 );
        assertEquals( EqualityKey.JUSTIFIED, f42.getEqualityKey().getStatus() );

    }




    @Test
    public void testAbductiveLogicUnlinking() {
        String droolsSource =
                "package org.drools.abductive.test; \n" +
                "" +
                "import " + Abducible.class.getName() + "; \n" +
                "global java.util.List list; \n" +
                "" +
                "declare Foo \n" +
                "   @Abducible \n" +
                "   id : Integer @key \n" +
                "end \n" +

                "query foo( Integer $i ) \n" +
                "   @Abductive( target=Foo.class ) \n" +
                "end \n" +

                "rule R1 " +
                "when " +
                "   foo( 42 ; ) " +
                "   Foo( 42 ; ) " +
                "then " +
                "   list.add( 1 ); " +
                "end \n" +
                "" +
                "rule R2 " +
                "when " +
                "   foo( 24 ; ) " +
                "   String() " +
                "   Foo( 24 ; ) " +
                "then " +
                "   list.add( 2 ); " +
                "end \n" +
                "" +
                "";
        /////////////////////////////////////

        KieSession session = getSessionFromString( droolsSource );
        List list = new ArrayList();
        session.setGlobal( "list", list );

        session.fireAllRules();
        session.insert( "test" );
        session.fireAllRules();

        for ( Object o : session.getObjects() ) {
            System.out.println( ">> " + o );
        }
        System.err.println( list );
        assertEquals( Arrays.asList( 1, 2 ), list );
    }


    @Test
    public void testAbductiveLogicNoConstructorFoundError() {
        String droolsSource =
                "package org.drools.abductive.test; \n" +
                "" +
                "import " + Abducible.class.getName() + "; \n" +
                "global java.util.List list; \n" +
                "" +
                "declare Foo \n" +
                "   @Abducible \n" +
                "   id : Integer @key \n" +
                "end \n" +

                "query foo( String $x ) \n" +
                    // Foo does not have a String constructor
                "   @Abductive( target=Foo.class ) \n" +
                "end \n" +

                "rule R1 " +
                "when " +
                "   $x : foo( \"x\" ; ) " +
                "then " +
                "   list.add( $x ); " +
                "end \n" +
                "" +
                "";
        /////////////////////////////////////

        KieHelper kieHelper = new KieHelper();
        kieHelper.addContent( droolsSource, ResourceType.DRL );

        Results res = kieHelper.verify();
        assertEquals( 1, res.getMessages( Message.Level.ERROR ).size() );
    }

    @Test
    public void testQueryTwice() {
        String droolsSource =
                "package org.drools.abductive.test; \n" +
                "" +
                "import " + Abducible.class.getName() + "; \n" +
                "global java.util.List list; \n" +
                "" +
                "declare Foo \n" +
                "   @Abducible \n" +
                "   id : String @key \n" +
                "end \n" +

                "query foo1( String $x ) \n" +
                "   @Abductive( target=Foo.class ) \n" +
                "end \n" +

                "query foo2( String $x ) \n" +
                "   @Abductive( target=Foo.class ) \n" +
                "end \n" +

                "rule R1 " +
                "when " +
                "   $x := ?foo1( \"x\" ; ) " +
                "   $x := ?foo2( \"x\" ; ) " +
                "then " +
                "   System.out.println( 'aaaa' ); " +
                "   list.add( $x ); " +
                "end \n" +
                "" +
                "";
        /////////////////////////////////////

        KieSession session = getSessionFromString( droolsSource );
        List list = new ArrayList();
        session.setGlobal( "list", list );

        session.fireAllRules();

        for ( Object o : session.getObjects() ) {
            System.out.println( ">> " + o );
        }
        System.err.println( list );
        assertEquals( 1, list.size() );
    }



    @Test
    public void testAbductiveLogicSprinklerAndRainExample() {
        // Sprinkler & Rain, abductive version
        String droolsSource =
                "package org.drools.abductive.test; \n" +
                "" +
                "import " + Abducible.class.getName() + "; \n" +
                "import " + Defeasible.class.getName() + "; \n" +
                "global java.util.List list; \n" +
                "" +
                "declare Sunny id : int @key end \n" +
                "declare WetGrass @Abducible id : int @key end \n" +
                "declare Rain @Abducible id : int @key end \n" +
                "declare Sprinkler @Abducible id : int @key end \n" +

                "query wetGrass() \n" +
                "   @Abductive( target=WetGrass.class ) \n" +
                "   rain() or sprinkler() \n" +
                "end \n" +

                "query rain() \n" +
                "   @Abductive( target=Rain.class ) \n" +
                "   @Defeasible " +
                "end \n" +

                "query sprinkler() \n" +
                "   @Abductive( target=Sprinkler.class ) \n" +
                "   @Defeasible " +
                "end \n" +

                "rule SunIntegrityConstraint \n" +
                "@Direct \n" +
                "when \n" +
                "   Sunny()" +
                "then \n" +
                "   insertLogical( new Rain(), 'neg' ); \n" +
                "end \n" +

                "rule Facts \n" +
                "when \n" +
                "then \n" +
                " insert( new Sunny( 0 ) ); \n" +
                "end \n" +

                "rule Raaain \n" +
                "when " +
                "   $r : Rain( _.neg ) " +
                "then \n" +
                "   list.add( 'no_rain_check' ); \n" +
                "end \n" +

                "rule Main_1\n" +
                "when \n" +
                "   wetGrass() \n" +
                "   ( " +
                "     Sprinkler() do[sprk] \n" +
                "     or \n" +
                "     Rain() do[rain] \n" +
                "     or \n" +
                "     Rain( _.neg ) do[norn] \n" +
                "   ) \n" +
                "then \n" +
                "then[sprk] \n" +
                "   list.add( 'sprinkler' ); \n" +
                "   System.out.println( \"The grass is wet because the sprinkler is on \" ); \n" +
                "then[rain] \n" +
                "   list.add( 'rain' ); \n " +
                "   System.out.println( \"The grass is wet because it's raining! \" ); \n" +
                "then[norn] \n" +
                "   list.add( 'not rain' ); \n" +
                "   System.out.println( \"The grass can't be wet due to rain, it's sunny today!!! \" ); \n" +
                "end \n" +

                "";
        /////////////////////////////////////

        KieSession session;
        try {
            System.setProperty("drools.negatable", "on");
            session = getSessionFromString( droolsSource );
        } finally {
            System.setProperty("drools.negatable", "off");
        }

        List list = new ArrayList();
        session.setGlobal( "list", list );

        session.fireAllRules();
        System.out.println( list );

        assertEquals( 3, list.size() );
        assertTrue( list.contains( "sprinkler" ) );
        assertTrue( list.contains( "not rain" ) );
        assertTrue( list.contains( "no_rain_check" ) );

        assertEquals( 3, session.getObjects().size() );

        int i = 0;
        Iterator it = session.getObjects().iterator();
        while (it.hasNext()) {
            i++;
            it.next();
        }
        assertEquals( 3, i );
    }

    @Test
    public void testAbductiveFactory() {
        String droolsSource =
                "package org.drools.abductive.test; \n" +
                "" +
                "import " + Abducible.class.getName() + "; \n" +
                "global java.util.List list; \n" +
                "" +
                "declare Root id : String @key end \n" +
                "declare TypeA extends Root @Abducible end \n" +
                "declare TypeB extends Root @Abducible end \n" +

                "query factory( String $type, String $arg, Root $out ) \n" +
                "   ( String( this == \"A\" ) from $type " +
                "     and" +
                "     $out := typeA( $arg ; ) " +
                "   ) " +
                "   or " +
                "   ( String( this == \"B\" ) from $type " +
                "     and " +
                "     $out := typeB( $arg ; ) " +
                "   ) " +
                "end \n" +

                "query typeA( String $x ) \n" +
                "   @Abductive( target=TypeA.class ) \n" +
                "end \n" +

                "query typeB( String $x ) \n" +
                "   @Abductive( target=TypeB.class ) \n" +
                "end \n" +

                "rule Main\n" +
                "when \n" +
                "   $s : String() " +
                "   factory( $s, \"foo\", $x ; ) " +
                "   Root( id == \"foo\" ) from $x " +
                "then \n" +
                "   System.out.println( \">>>>>\" + $x ); \n" +
                "   list.add( $x ); \n" +
                "end \n" +

                "";
        /////////////////////////////////////

        KieSession session = getSessionFromString( droolsSource );
        List list = new ArrayList();
        session.setGlobal( "list", list );

        session.insert( "A" );

        session.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( "TypeA", list.get( 0 ).getClass().getSimpleName() );
    }


    @Test
    public void testQueryAPIs() {
        String droolsSource =
                "package org.drools.abductive.test; \n" +
                "import " + Abducible.class.getName() + "; \n" +

                "" +

                "declare Foo " +
                "   @Abducible " +
                "   id : String " +
                "end " +

                "query foo( String $s ) " +
                "   @Abductive( target=Foo.class ) \n" +
                "end \n "+

                "query bar( String $s, Foo $foo ) " +
                "   $foo := Foo() " +
                "end \n "+

                "rule MoveAround " +
                "when " +
                "   $s : String() " +
                "   $f : foo( $s ; ) " +
                "   bar( $s, $f ; ) " +
                "then " +
                "   delete( $s ); " +
                "   System.out.println( 'Foo ' + $f ); " +
                "end " +

                "";

        /////////////////////////////////////

        KieSession session = getSessionFromString( droolsSource );

        session.insert( "faa" );
        session.fireAllRules();

        for ( Object o : session.getObjects() ) {
            System.out.println( ">>> " + o );
        }
        assertEquals( 1, session.getObjects().size() );

        Query q1 = session.getKieBase().getQuery( "org.drools.abductive.test", "foo" );
        Query q2 = session.getKieBase().getQuery( "org.drools.abductive.test", "bar" );

        assertNotNull( q1 );
        assertNotNull( q2 );

        QueryResults q10res = new FlatQueryResults((QueryResultsImpl) session.getQueryResults( "foo", "foo", null ));
        QueryResults q11res = new FlatQueryResults((QueryResultsImpl) session.getQueryResults( "foo", "foo", Variable.v ));
        QueryResults q20res = new FlatQueryResults((QueryResultsImpl) session.getQueryResults( "bar", "foo", Variable.v ));

        assertEquals( 1, q10res.size() );
        assertEquals( 1, q11res.size() );
        assertEquals( 1, q20res.size() );

        QueryResultsRow row10 = q10res.iterator().next();
        QueryResultsRow row11 = q11res.iterator().next();
        QueryResultsRow row20 = q20res.iterator().next();

        assertEquals( "foo", row10.get( "$s" ) );
        assertEquals( "foo", row11.get( "$s" ) );
        assertEquals( "foo", row20.get( "$s" ) );

        Object foo = row20.get( "$foo" );
        assertSame( foo, session.getObjects().iterator().next() );

        // the implicit return argument, the abduced/retrieved fact, is hidden
        assertNull( row11.get( "" ) );

    }



    @Test
    public void testCitizenshipExample() {
        // from wikipedia, abductive reasoning example
        String droolsSource =
                "package org.drools.abductive.test; \n" +
                "" +
                "declare CitizenUS " +
                "   name : String @key " +
                "end " +

                "declare Parent " +
                "   parent : String @key " +
                "   child : String @key " +
                "end " +

                "declare BornUS @Abducible name : String @key end " +
                "declare BornOutsideUS @Abducible name : String @key end " +
                "declare ResidentUS @Abducible name : String @key end " +
                "declare NaturalizedUS @Abducible name : String @key end " +
                "declare RegisteredUS @Abducible name : String @key end " +

                "query extractCitizen( CitizenUS $cit ) " +
                "   $cit := CitizenUS() " +
                "end " +

                "query citizen( String $name ) " +
                "   @Abductive( target=CitizenUS.class ) " +
                "   bornUS( $name ; ) " +
                "   or " +
                "   ( bornOutsideUS( $name ; ) and residentUS( $name ; ) and naturalizedUS( $name ; ) ) " +
                "   or " +
                "   ( bornOutsideUS( $name ; ) and Parent( $parent, $name ; ) and CitizenUS( $parent ; ) and registeredUS( $name ; ) ) " +
                "end " +

                "query bornUS( String $name ) @Abductive( target=BornUS.class ) end " +
                "query bornOutsideUS( String $name ) @Abductive( target=BornOutsideUS.class ) end " +
                "query residentUS( String $name ) @Abductive( target=ResidentUS.class ) end " +
                "query naturalizedUS( String $name ) @Abductive( target=NaturalizedUS.class ) end " +
                "query registeredUS( String $name ) @Abductive( target=RegisteredUS.class ) end " +

                "rule Facts " +
                "when " +
                "then " +
                "   insert( new CitizenUS( 'Mary' ) ); " +
                "   insert( new Parent( 'Mary', 'John' ) ); " +
                "   insertLogical( new ResidentUS( 'John' ), 'neg' ); " +
                "end " +

                "rule CheckCitizen " +
                "when " +
                "   $cit : ?citizen( 'John' ; ) " +
                "then " +
                "   System.out.println( 'John is a citizen ' + $cit ); " +
                "end " +

                "";

        /////////////////////////////////////

        KieSession session = getSessionFromString( droolsSource );

        session.fireAllRules();

        FactType type = session.getKieBase().getFactType( "org.drools.abductive.test", "CitizenUS" );

        for ( Object o : session.getObjects() ) {
            System.out.println( ">>> " + o );
            if ( o.getClass().equals( type.getFactClass() ) ) {
                InternalFactHandle h = (InternalFactHandle) session.getFactHandle( o );
                String name = (String) type.get( o, "name" );
                if ( "Mary".equals( name ) ) {
                    assertNull( h.getEqualityKey().getBeliefSet() );
                } else if ( "John".equals( name ) ) {
                    BeliefSet bs = h.getEqualityKey().getBeliefSet();
                    assertTrue( bs.isPositive() );
                    assertEquals( 2, bs.size() );
                }
            }
        }


    }

    @Test
    @Ignore( "Not implemented yet" )
    public void testGenesExplanationBackTracking() {
        // from wikipedia, abductive reasoning example

        /*
            feed(lactose):-make(permease),make(galactosidase).
            make(Enzyme):-code(Gene,Enzyme),express(Gene).
            express(lac(X)):-amount(glucose,low),amount(lactose,hi).
            express(lac(X)):-amount(glucose,medium),amount(lactose,medium).
            code(lac(y),permease).
            code(lac(z),galactosidase).

            temperature(low):-amount(glucose,low).
         */

        String droolsSource =
                "package org.drools.abductive.test; \n" +
                "import org.kie.api.runtime.rule.Match;\n" +
                "" +

                "declare Amount " +
                "   enz : String @key " +
                "   level : String @key " +
                "end " +

                "query feed( String $enz ) " +
                "   $enz := String() from 'lactose' " +
                "   ?make( 'permease' ; ) " +
                "   ?make( 'galactosidase' ; ) " +
                "end " +

                "query make( String $enz ) " +
                "   ?code( $gen, $enz ; ) and ?express( $gen ; ) " +
                "end " +

                "query code( String $gen, String $enz ) " +
                "   ( $gen := String() from 'lacY' and $enz := String() from 'permease' ) " +
                "   or " +
                "   ( $gen := String() from 'lacZ' and $enz := String() from 'galactosidase' ) " +
                "end " +

                "query express( String $gen ) " +
                "  ( ?amount( 'glucose', 'low' ; ) and ?amount( 'lactose', 'hi' ; ) ) " +
                "  or " +
                "  ( ?amount( 'glucose', 'medium' ; ) and ?amount( 'lactose', 'medium' ; ) ) " +
                "end " +

                "query amount( String $enz, String $lev ) @Abductive( target=Amount.class ) end " +


                "rule Check " +
                "when " +
                "   ?feed( 'lactose' ; ) " +
                "then " +
                "   System.out.println( 'YES' ); " +
                "end " +

                "rule Match " +
                "when " +
                "   $m : Match( rule.name != 'Match' ) " +
                "then" +
                "   System.out.println( $m ); " +
                "end  " +

                "";

        /////////////////////////////////////

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( DeclarativeAgendaOption.ENABLED );

        KieSession session = getSessionFromString( droolsSource, kconf );

        session.fireAllRules();

        for ( Object o : session.getObjects() ) {
            System.out.println( ">>> " + o );
        }


    }




    @Test
    @Ignore( "Not implemented yet" )
    public void testBacktracking() {
        String droolsSource =
                "package org.drools.abductive.test; \n" +
                "import org.kie.api.runtime.rule.Match;\n" +
                "" +

                "declare Foo " +
                "@Abducible " +
                "   id : Integer @key " +
                "end " +

                "query bar( Integer $id ) " +
                "   @Abductive( target=Foo.class, backtracking=true ) " +
                "   $id := Integer() " +
                "end " +

                "rule Check " +
                "when " +
                "   bar( $i ; ) " +
                "then " +
                "   System.out.println( 'YES ' + $i ); " +
                "end " +

                "rule Check2 " +
                "when " +
                "   bar( $i ; ) " +
                "then " +
                "   System.out.println( 'HAH ' + $i ); " +
                "end " +

                "rule Init " +
                "when " +
                "then" +
                "   insert( new Integer( 1 ) ); " +
                "   insert( new Integer( 2 ) ); " +
                "end  " +

                "";

        /////////////////////////////////////

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( DeclarativeAgendaOption.ENABLED );

        KieSession session = getSessionFromString( droolsSource, kconf );

        session.fireAllRules();

        for ( Object o : session.getObjects() ) {
            System.out.println( ">>> " + o );
        }


    }


    @Test
    public void testCheckForItemsExample() {
        String droolsSource =
                "package org.drools.abductive.test; " +
                "import " + Abducible.class.getName() + "; " +
                "global java.util.List list; " +

                "declare Fruit id : int @key end " +

                "declare Apple extends Fruit end " +
                "declare Orange extends Fruit end " +
                "declare Banana extends Fruit end " +

                "declare Goal " +
                "   type : Class @key " +
                "end " +

                "query need( Class $type ) " +
                "   @Abductive( target = Goal.class ) " +
                "   not Fruit( $type.getName() == this.getClass().getName() ) " +
                "end " +

                "query check( Class $type ) " +
                "   ?need( $type ; ) " +
                "   or " +
                "   Fruit( $type.getName() == this.getClass().getName() ) " +
                "end " +

                "query checkRecipe() " +
                "   check( Apple.class ; ) " +
                "   check( Orange.class ; ) " +
                "   check( Banana.class ; ) " +
                "end " +

                "rule Fridge " +
                "   @Direct " +
                "when " +
                "then " +
                "   insert( new Banana( 1 ) ); " +
                //"   insert( new Apple( 2 ) ); " +
                "   insert( new Orange( 1 ) ); " +
                "end " +

                "rule Reminder " +
                "when " +
                "   accumulate( $f : Fruit() ," +
                "       $c : count( $f );" +
                "       $c == 2 " +         // works also with 1, or no fruit at all
                "   ) " +
                "   ?checkRecipe() " +
                "then " +
                "   System.out.println( 'You have ' + $c + ' ingredients ' ); " +
                "end " +

                "rule Suggest " +
                "when " +
                "   $g : Goal( $type ; ) " +
                "then " +
                "   System.out.println( 'You are missing ' + $type ); " +
                "   list.add( $type.getSimpleName() ); " +
                "end " +

                "rule FruitSalad " +
                "when " +
                "   Apple() " +
                "   Banana() " +
                "   Orange() " +
                "then " +
                "   System.out.println( 'Enjoy the salad' ); " +
                "end ";

        /////////////////////////////////////

        KieSession session = getSessionFromString( droolsSource );
        List list = new ArrayList(  );
        session.setGlobal( "list", list );

        session.fireAllRules();

        for ( Object o : session.getObjects() ) {
            System.out.println( ">>> " + o );
        }

        assertEquals( Arrays.asList( "Apple" ), list );
    }

}