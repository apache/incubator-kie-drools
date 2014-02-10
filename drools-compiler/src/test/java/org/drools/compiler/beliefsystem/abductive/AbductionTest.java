package org.drools.compiler.beliefsystem.abductive;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.core.BeliefSystemType;
import org.drools.core.FactHandle;
import org.drools.core.SessionConfiguration;
import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.beliefsystem.abductive.Abducible;
import org.drools.core.common.EqualityKey;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.factmodel.traits.Thing;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.conf.DeclarativeAgendaOption;
import org.kie.api.definition.rule.Query;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.api.runtime.rule.Variable;
import org.kie.internal.KnowledgeBaseFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbductionTest extends CommonTestMethodBase {

    protected KieSession getSessionFromString( String drlString ) {
        return getSessionFromString( drlString, null );
    }

    protected KieSession getSessionFromString( String drlString, KieBaseConfiguration kbConf ) {
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write( ks.getResources()
                           .newByteArrayResource( drlString.getBytes() )
                           .setSourcePath( "drl1.drl" )
                           .setResourceType( ResourceType.DRL ) );
        KieBuilder kieBuilder = ks.newKieBuilder( kfs );
        kieBuilder.buildAll();

        Results res = kieBuilder.getResults();
        if ( res.hasMessages( Message.Level.ERROR ) ) {
            fail( res.getMessages( Message.Level.ERROR ).toString() );
        }

        KieSessionConfiguration ksConf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ((SessionConfiguration) ksConf).setBeliefSystemType( BeliefSystemType.DEFEASIBLE );

        KieContainer kc = ks.newKieContainer( kieBuilder.getKieModule().getReleaseId() );
        return kbConf != null ? kc.newKieBase( kbConf ).newKieSession( ksConf, null ) : kc.newKieSession( ksConf);
    }


    @Test
    public void testAbductiveLogicWithConstructorArgs() {
        String droolsSource =
                "package org.drools.tms.test; \n" +
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
    public void testBindNonAbductiveQueryError() {
        String droolsSource =
                "package org.drools.tms.test; \n" +
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
                "package org.drools.tms.test; \n" +
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
                "" +
                "" +
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

        FactType foo = session.getKieBase().getFactType( "org.drools.tms.test", "Foo" );
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
                "package org.drools.tms.test; \n" +
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
                "package org.drools.tms.test; \n" +
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
        assertEquals( EqualityKey.STATED, f11.getEqualityKey().getStatus() );
        assertSame( b, b11 );

        Bean b42 = (Bean) map.get( 42 );
        InternalFactHandle f42 = ( InternalFactHandle ) session.getFactHandle( b42 );
        assertEquals( EqualityKey.JUSTIFIED, f42.getEqualityKey().getStatus() );

    }




    @Test
    public void testAbductiveLogicUnlinking() {
        String droolsSource =
                "package org.drools.tms.test; \n" +
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
                "package org.drools.tms.test; \n" +
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

        KieSession session = getSessionFromString( droolsSource );
        List list = new ArrayList();
        session.setGlobal( "list", list );

        session.fireAllRules();

        for ( Object o : session.getObjects() ) {
            System.out.println( ">> " + o );
        }
        System.err.println( list );
        assertEquals( 1, list.size() );
        assertTrue( list.contains( null ) );
    }

    @Test
    public void testQueryTwice() {
        String droolsSource =
                "package org.drools.tms.test; \n" +
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
                "package org.drools.tms.test; \n" +
                "" +
                "import " + Abducible.class.getName() + "; \n" +
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

                "rule Main_1\n" +
                "when \n" +
                "   wetGrass() \n" +
                "   ( " +
                "     Sprinkler() do[sprk] \n" +
                "     or \n" +
                "     Rain() do[rain] \n" +
                "     or \n" +
                "     Rain() from entry-point 'neg' do[norn] \n" +
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

        KieSession session = getSessionFromString( droolsSource );
        List list = new ArrayList();
        session.setGlobal( "list", list );

        session.fireAllRules();

        assertEquals( 2, list.size() );
        assertTrue( list.contains( "sprinkler" ) );
        assertTrue( list.contains( "not rain" ) );

        assertEquals( 3, session.getObjects().size() );
    }

    @Test
    public void testAbductiveFactory() {
        String droolsSource =
                "package org.drools.tms.test; \n" +
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
    public void testNeeds() {
        // revisiting OPSJ's version of a fragment of the famous monkey&bananas AI problem
        String droolsSource =
                "package org.drools.tms.test; \n" +
                "" +
                "import " + Thing.class.getPackage().getName() + ".*;" +
                "import " + Abducible.class.getName() + "; \n" +
                "global java.util.List list; \n" +

                "declare Goal \n" +
                "   entity : Thing \n" +
                "   property : String \n" +
                "   value : Object \n" +
                "end \n" +

                "query check( Thing $thing, String $prop, Object $val ) " +
                "   Thing( this == $thing, fields[ $prop ] == $val ) " +
                "   or" +
                "   ( " +
                "     need( $thing, $prop, $val ; ) " +
                "     and" +
                "     Thing( this == $thing, fields[ $prop ] == $val ) " +
                "   ) " +
                "end \n " +

                "query need( Thing $thing, String $prop, Object $val ) " +
                "   @Abductive( target=Goal.class ) \n" +
                "   Thing( this == $thing, fields[ $prop ] != $val ) " +
                "end \n "+

                "rule HandleGoal " +
                "when " +
                "   $g : Goal( $m : entity, $prop : property, $val : value ) " +
                "then " +
                "   System.out.println( 'Satisfy ' + $g ); \n" +
                "   modify ( $m ) { getFields().put( $prop, $val ); } \n" +
                "end " +

                "declare trait Monkey\n" +
                "   position : Integer = 1 \n " +
                "end \n" +

                "rule Main\n" +
                "when \n" +
                "then \n" +
                "   System.out.println( 'Don MONKEY ' ); " +
                "   Entity e = new Entity(); \n" +
                "   Monkey monkey = don( e, Monkey.class );" +
                "end \n" +

                "rule MoveAround " +
                "when " +
                "   $m : Monkey( $pos : position ) " +
                "   ?check( $m, \"position\", 4 ; ) " +
                "then " +
                "   System.out.println( 'Monkey madness' + $m ); " +
                "   list.add( $m.getPosition() ); " +
                "end " +

                "";

        /////////////////////////////////////

        KieSession session = getSessionFromString( droolsSource );
        List list = new ArrayList();
        session.setGlobal( "list", list );

        session.fireAllRules();

        for ( Object o : session.getObjects() ) {
            System.out.println( ">>> " + o );
        }

        assertEquals( Arrays.asList( 4 ), list );
    }


    @Test
    public void testQueryAPIs() {
        String droolsSource =
                "package org.drools.tms.test; \n" +
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

        Query q1 = session.getKieBase().getQuery( "org.drools.tms.test", "foo" );
        Query q2 = session.getKieBase().getQuery( "org.drools.tms.test", "bar" );

        assertNotNull( q1 );
        assertNotNull( q2 );

        QueryResults q10res = session.getQueryResults( "foo", "foo" );
        QueryResults q11res = session.getQueryResults( "foo", "foo", Variable.v );
        QueryResults q20res = session.getQueryResults( "bar", "foo", Variable.v );

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
                "package org.drools.tms.test; \n" +
                "" +

                "declare entry-point \"neg\" end " +

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

        for ( Object o : session.getObjects() ) {
            System.out.println( ">>> " + o );
            if ( o.getClass().equals( session.getKieBase().getFactType( "org.drools.tms.test", "CitizenUS" ) ) ) {
                InternalFactHandle h = (InternalFactHandle) session.getFactHandle( o );
                BeliefSet bs = h.getEqualityKey().getBeliefSet();
                assertTrue( bs.isPositive() );
                assertEquals( 2, bs.size() );
            }
        }


    }

    @Test
    @Ignore
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
                "package org.drools.tms.test; \n" +
                "import org.kie.api.runtime.rule.Match;\n" +
                "" +
                "declare entry-point \"neg\" end " +

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
    @Ignore
    public void testBacktracking() {
        String droolsSource =
                "package org.drools.tms.test; \n" +
                "import org.kie.api.runtime.rule.Match;\n" +
                "" +
                "declare entry-point \"neg\" end " +

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


}