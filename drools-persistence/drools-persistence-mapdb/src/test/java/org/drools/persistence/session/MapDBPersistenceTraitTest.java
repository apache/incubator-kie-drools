/*
 * Copyright 2011 Red Hat Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.persistence.session;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.drools.core.factmodel.traits.TraitFactory;
import org.drools.core.factmodel.traits.TraitableBean;
import org.drools.core.factmodel.traits.VirtualPropertyMode;
import org.drools.persistence.mapdb.util.MapDBPersistenceUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message.Level;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.api.runtime.rule.Variable;
import org.kie.internal.io.ResourceFactory;

public class MapDBPersistenceTraitTest {

    private Map<String, Object> context;
    private Environment env;

    @Before
    public void setUp() throws Exception {
        context = MapDBPersistenceUtil.setupMapDB();
        env = MapDBPersistenceUtil.createEnvironment(context);
    }

    @After
    public void tearDown() throws Exception {
        MapDBPersistenceUtil.cleanUp(context);
    }

    @Test
    public void testTripleBasedTraitsWithJPA() {
        String str = "package org.drools.trait.test; \n" +
                "global java.util.List list; \n" +
                "" +
                "declare TBean \n" +
                "  @propertyReactive \n" +
                "  @Traitable \n" +
                "  fld : String \n" +
                "end \n " +
                "" +
                "declare trait Mask \n" +
                "  @propertyReactive \n" +
                "  fld : String \n" +
                "  xyz : int  \n" +
                "end \n" +
                "\n " +
                "declare trait Cloak \n" +
                "  @propertyReactive \n" +
                "  fld : String \n" +
                "  ijk : String  \n" +
                "end \n" +
                "" +
                "rule Init \n" +
                "when \n" +
                "then \n" +
                "  insert( new TBean(\"abc\") ); \n" +
                "end \n" +
                "" +
                "rule Don \n" +
                "no-loop \n" +
                "when \n" +
                "  $b : TBean( ) \n" +
                "then \n" +
                "  Mask m = don( $b, Mask.class ); \n" +
                "  modify (m) { setXyz( 10 ); } \n" +
                "  list.add( m ); \n" +
                "  System.out.println( \"Don result : \" + m ); \n " +
                "end \n" +
                "\n" +
                "rule Don2 \n" +
                "no-loop \n" +
                "when \n" +
                "  $b : TBean( ) \n" +
                "then \n" +
                "  Cloak c = don( $b, Cloak.class ); \n" +
                "  modify (c) { setIjk( \"ijklmn\" ); } \n" +
                "  list.add( c ); \n" +
                "  System.out.println( \"Don result : \" + c ); \n " +
                "end \n" +
                "";


        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        kfs.write("src/main/resources/r1.drl", ResourceFactory.newByteArrayResource(str.getBytes()));
        KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder(kfs);
        kbuilder.buildAll();
        if (kbuilder.getResults().hasMessages(Level.ERROR)) {
        	fail (kbuilder.getResults().toString());
        }
        KieBase kbase = KieServices.Factory.get().newKieContainer(kbuilder.getKieModule().getReleaseId()).getKieBase();
        KieSession ksession = KieServices.Factory.get().getStoreServices().newKieSession(kbase,  null, env);
        //StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<?> list = new ArrayList<Object>();

        ksession.setGlobal("list",
                list);

        ksession.fireAllRules();

        assertEquals( 2,
                list.size() );
        long id = ksession.getIdentifier();


        KieSession ksession2 = KieServices.Factory.get().getStoreServices().loadKieSession(id, kbase,  null, env);

        ksession2.fireAllRules();


        Collection<?> x = ksession2.getObjects();
        assertEquals( 3, x.size() );

        TraitableBean<?, ?> core = null;
        for ( Object o : x ) {
            if ( o instanceof TraitableBean ) {
                core = (TraitableBean<?, ?>) o;
                break;
            }
        }
        assertNotNull( core );
        assertEquals( 2, core._getDynamicProperties().size() );
        assertNotNull( core.getTrait( "org.drools.trait.test.Mask" ) );
        assertNotNull( core.getTrait( "org.drools.trait.test.Cloak" ) );

    }



    @Test
    public void testMapBasedTraitsWithJPA() {
        String str = "package org.drools.trait.test; \n" +
                "global java.util.List list; \n" +
                "" +
                "declare TBean2 \n" +
                "  @propertyReactive \n" +
                "  @Traitable \n" +
                "  fld : String \n" +
                "end \n " +
                "" +
                "declare trait Mask2 \n" +
                "  @propertyReactive \n" +
                "  fld : String \n" +
                "  xyz : int  \n" +
                "end \n" +
                "\n " +
                "declare trait Cloak2 \n" +
                "  @propertyReactive \n" +
                "  fld : String \n" +
                "  ijk : String  \n" +
                "end \n" +
                "" +
                "rule Init \n" +
                "when \n" +
                "then \n" +
                "  insert( new TBean2(\"abc\") ); \n" +
                "end \n" +
                "" +
                "rule Don \n" +
                "no-loop \n" +
                "when \n" +
                "  $b : TBean2( ) \n" +
                "then \n" +
                "  Mask2 m = don( $b, Mask2.class ); \n" +
                "  modify (m) { setXyz( 10 ); } \n" +
                "  list.add( m ); \n" +
                "  System.out.println( \"Don result : \" + m ); \n " +
                "end \n" +
                "\n" +
                "rule Don2 \n" +
                "no-loop \n" +
                "when \n" +
                "  $b : TBean2( ) \n" +
                "then \n" +
                "  Cloak2 c = don( $b, Cloak2.class ); \n" +
                "  modify (c) { setIjk( \"ijklmn\" ); } \n" +
                "  list.add( c ); \n" +
                "  System.out.println( \"Don result : \" + c ); \n " +
                "end \n" +
                "";

        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        kfs.write("src/main/resources/r1.drl", ResourceFactory.newByteArrayResource(str.getBytes()));
        KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder(kfs);
        kbuilder.buildAll();
        if (kbuilder.getResults().hasMessages(Level.ERROR)) {
        	fail (kbuilder.getResults().toString());
        }
        KieBase kbase = KieServices.Factory.get().newKieContainer(kbuilder.getKieModule().getReleaseId()).getKieBase();
        TraitFactory.setMode( VirtualPropertyMode.MAP, kbase );

        KieSession ksession = KieServices.Factory.get().getStoreServices().newKieSession( kbase, null, env );
        List<?> list = new ArrayList<Object>();

        ksession.setGlobal("list", list);

        ksession.fireAllRules();

        assertEquals( 2, list.size() );
        long id = ksession.getIdentifier();

        KieSession ksession2 = KieServices.Factory.get().getStoreServices().loadKieSession( id, kbase, null, env );
        ksession2.fireAllRules();

        Collection<?> x = ksession2.getObjects();
        assertEquals( 3, x.size() );

        TraitableBean<?, ?> core = null;
        for ( Object o : x ) {
            if ( o instanceof TraitableBean ) {
                core = (TraitableBean<?, ?>) o;
                break;
            }
        }
        assertNotNull( core );
        assertEquals( 2, core._getDynamicProperties().size() );
        assertNotNull( core.getTrait( "org.drools.trait.test.Mask2" ) );
        assertNotNull( core.getTrait( "org.drools.trait.test.Cloak2" ) );
    }

    public void traitsLegacyWrapperWithJPA( VirtualPropertyMode mode ) {
        String str = "package org.drools.trait.test; \n" +
                "global java.util.List list; \n" +
                "import org.drools.core.factmodel.traits.*; \n" +
                "" +                "" +
                "declare TBean \n" +
                "@Traitable \n" +
                "  fld : String \n" +
                "end \n " +
                "" +
                "declare trait Mask \n" +
                "  @propertyReactive \n" +
                "  fld : String \n" +
                "  xyz : int  \n" +
                "end \n" +
                "\n " +
                "rule Init \n" +
                "when \n" +
                "then \n" +
                "  insert( new TBean(\"abc\") ); \n" +
                "end \n" +
                "" +
                "rule Don \n" +
                "no-loop \n" +
                "when \n" +
                "  $b : TBean( ) \n" +
                "then \n" +
                "  System.out.println( \"Din Don Dan: \"  ); \n " +
                "  Mask m = don( $b, Mask.class ); \n" +
                "  modify (m) { setXyz( 10 ); } \n" +
                "  list.add( m ); \n" +
                "  System.out.println( \"Don result : \" + m ); \n " +
                "end \n" +
                "\n" +
                "";

        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        kfs.write("src/main/resources/r1.drl", ResourceFactory.newByteArrayResource(str.getBytes()));
        KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder(kfs);
        kbuilder.buildAll();
        if (kbuilder.getResults().hasMessages(Level.ERROR)) {
        	fail (kbuilder.getResults().toString());
        }
        KieBase kbase = KieServices.Factory.get().newKieContainer(kbuilder.getKieModule().getReleaseId()).getKieBase();

        KieSession ksession = KieServices.Factory.get().getStoreServices().newKieSession( kbase, null, env );
        TraitFactory.setMode( mode, ksession.getKieBase() );
        List<?> list = new ArrayList<Object>();

        ksession.setGlobal("list", list);
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        long id = ksession.getIdentifier();

        Collection<?> yOld = ksession.getObjects();
        TraitableBean<?, ?> coreOld = null;
        for ( Object o : yOld ) {
            if ( o instanceof TraitableBean ) {
                coreOld = (TraitableBean<?, ?>) o;
                break;
            }
        }
        assertNotNull( coreOld );
        KieSession ksession2 = KieServices.Factory.get().getStoreServices().loadKieSession( id, kbase, null, env );
        ksession2.fireAllRules();

        Collection<?> y = ksession2.getObjects();
        assertEquals( 2, y.size() );

        TraitableBean<?, ?> core = null;
        for ( Object o : y ) {
            if ( o instanceof TraitableBean ) {
                core = (TraitableBean<?, ?>) o;
                break;
            }
        }
        assertNotNull( core );
        assertEquals( 1, core._getDynamicProperties().size() );
        assertNotNull( core.getTrait( "org.drools.trait.test.Mask" ) );

    }


    @Test
    public void testTraitsOnLegacyJPATriple() {
        traitsLegacyWrapperWithJPA( VirtualPropertyMode.TRIPLES );
    }

    @Test
    public void testTraitsOnLegacyJPAMap() {
        traitsLegacyWrapperWithJPA( VirtualPropertyMode.MAP );
    }


    @Test
    public void testTraitWithJPAOnFreshKieBase() {
        //DROOLS-904
        String str = "package org.drools.trait.test; " +
                     "global java.util.List list; " +

                     "declare TBean2  " +
                     "  @propertyReactive  " +
                     "  @Traitable  " +
                     "end   " +

                     "declare trait Mask " +
                     "  @propertyReactive  " +
                     "end  " +

                     "query getTraits( Mask $m ) " +
                     "  $m := Mask() " +
                     "end " +

                     "rule Init when then don( new TBean2(), Mask.class ); end " +

                     "rule Trig when String() then don( new TBean2(), Mask.class ); end " +

                     "rule Main when $m : Mask() then list.add( $m ); end ";

        List<?> list = new ArrayList<>();
        
        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        kfs.write("src/main/resources/r1.drl", ResourceFactory.newByteArrayResource(str.getBytes()));
        KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder(kfs);
        kbuilder.buildAll();
        if (kbuilder.getResults().hasMessages(Level.ERROR)) {
        	fail (kbuilder.getResults().toString());
        }
        KieBase kbase = KieServices.Factory.get().newKieContainer(kbuilder.getKieModule().getReleaseId()).getKieBase();

        KieSession ksession = KieServices.Factory.get().getStoreServices().newKieSession( kbase, null, env );
        ksession.setGlobal( "list", list );
        ksession.fireAllRules();

        long id = ksession.getIdentifier();


        KieBase kbase2 = KieServices.Factory.get().newKieContainer(kbuilder.getKieModule().getReleaseId()).newKieBase(null);
        TraitFactory.setMode( VirtualPropertyMode.MAP, kbase );

        KieSession ksession2 = KieServices.Factory.get().getStoreServices().loadKieSession( id, kbase2, null, env );
        ksession.setGlobal( "list", list );
        ksession2.insert( "go" );
        ksession2.fireAllRules();

        assertEquals( 2, list.size() );

        Class<?> oldProxyClass = list.get( 0 ).getClass();
        Class<?> newProxyClass = list.get( 1 ).getClass();
        assertNotSame( oldProxyClass, newProxyClass );

        QueryResults qry = ksession2.getQueryResults( "getTraits", Variable.v );
        assertEquals( 2, qry.size() );
        java.util.Iterator<QueryResultsRow> iter = qry.iterator();
        int j = 0;
        while ( iter.hasNext() ) {
            QueryResultsRow row = iter.next();
            Object entry = row.get( "$m" );
            assertNotNull( entry );
            assertSame( newProxyClass, entry.getClass() );
            j++;
        }
        assertEquals( 2, j );

        for ( Object o : ksession2.getObjects() ) {
            if ( o.getClass().getName().contains( "Mask" ) ) {
                assertSame( newProxyClass, o.getClass() );
            }
        }
    }

}
