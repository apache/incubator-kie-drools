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


import static org.drools.persistence.util.PersistenceUtil.DROOLS_PERSISTENCE_UNIT_NAME;
import static org.drools.persistence.util.PersistenceUtil.createEnvironment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.drools.factmodel.traits.TraitFactory;
import org.drools.factmodel.traits.TraitableBean;
import org.drools.persistence.util.PersistenceUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.io.ResourceFactory;
import org.kie.io.ResourceType;
import org.kie.persistence.jpa.JPAKnowledgeService;
import org.kie.runtime.Environment;
import org.kie.runtime.StatefulKnowledgeSession;

public class JpaPersistenceTraitTest {

    private HashMap<String, Object> context;
    private Environment env;

    @Before
    public void setUp() throws Exception {
        context = PersistenceUtil.setupWithPoolingDataSource(DROOLS_PERSISTENCE_UNIT_NAME);
        env = createEnvironment(context);
    }

    @After
    public void tearDown() throws Exception {
        PersistenceUtil.tearDown(context);
    }



    @Test
    public void testTripleBasedTraitsWithJPA() {
        String str = "package org.drools.trait.test; \n" +
                "global java.util.List list; \n" +
                "" +
                "declare TBean \n" +
                "  @Traitable \n" +
                "  fld : String \n" +
                "end \n " +
                "" +
                "declare trait Mask \n" +
                "  fld : String \n" +
                "  xyz : int  \n" +
                "end \n" +
                "\n " +
                "declare trait Cloak \n" +
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



        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(str.getBytes()),
                ResourceType.DRL );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
        List<?> list = new ArrayList<Object>();

        ksession.setGlobal("list",
                list);

        ksession.fireAllRules();

        assertEquals( 2,
                list.size() );
        int id = ksession.getId();


        StatefulKnowledgeSession ksession2 = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );

        ksession2.fireAllRules();


        Collection x = ksession2.getObjects();
        assertEquals( 4, x.size() );

        TraitableBean core = null;
        for ( Object o : x ) {
            if ( o instanceof TraitableBean ) {
                core = (TraitableBean) o;
                break;
            }
        }
        assertNotNull( core );
        assertEquals(2, core.getDynamicProperties().size());
        assertNotNull( core.getTrait( "org.drools.factmodel.traits.Thing" ) );
        assertNotNull( core.getTrait( "org.drools.trait.test.Mask" ) );
        assertNotNull( core.getTrait( "org.drools.trait.test.Cloak" ) );

    }



    @Test
    public void testMapBasedTraitsWithJPA() {
        String str = "package org.drools.trait.test; \n" +
                "global java.util.List list; \n" +
                "" +
                "declare TBean2 \n" +
                "  @Traitable \n" +
                "  fld : String \n" +
                "end \n " +
                "" +
                "declare trait Mask2 \n" +
                "  fld : String \n" +
                "  xyz : int  \n" +
                "end \n" +
                "\n " +
                "declare trait Cloak2 \n" +
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


        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                ResourceType.DRL );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        TraitFactory.setMode( TraitFactory.VirtualPropertyMode.MAP, kbase );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
        List<?> list = new ArrayList<Object>();

        ksession.setGlobal("list",
                list);

        ksession.fireAllRules();

        assertEquals( 2,
                list.size() );
        int id = ksession.getId();


        StatefulKnowledgeSession ksession2 = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );

        ksession2.fireAllRules();


        Collection x = ksession2.getObjects();
        assertEquals( 4, x.size() );

        TraitableBean core = null;
        for ( Object o : x ) {
            if ( o instanceof TraitableBean ) {
                core = (TraitableBean) o;
                break;
            }
        }
        assertNotNull( core );
        assertEquals( 2, core.getDynamicProperties().size() );
        assertNotNull( core.getTrait( "org.drools.factmodel.traits.Thing" ) );
        assertNotNull( core.getTrait( "org.drools.trait.test.Mask2" ) );
        assertNotNull( core.getTrait( "org.drools.trait.test.Cloak2" ) );

    }




    public void traitsLegacyWrapperWithJPA( TraitFactory.VirtualPropertyMode mode ) {
        String str = "package org.drools.trait.test; \n" +
                "global java.util.List list; \n" +
                "" +                "" +
                "declare TBean \n" +
                "  fld : String \n" +
                "end \n " +
                "" +
                "declare trait Mask \n" +
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

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                ResourceType.DRL );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
        TraitFactory.setMode( mode, ksession.getKieBase() );
        List<?> list = new ArrayList<Object>();

        ksession.setGlobal("list",
                list);

        ksession.fireAllRules();

        assertEquals( 1,
                list.size() );
        int id = ksession.getId();


        Collection yOld = ksession.getObjects();
        TraitableBean coreOld = null;
        for ( Object o : yOld ) {
            if ( o instanceof TraitableBean ) {
                coreOld = (TraitableBean) o;
                break;
            }
        }
        assertNotNull( coreOld );


        StatefulKnowledgeSession ksession2 = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );

        ksession2.fireAllRules();


        Collection y = ksession2.getObjects();
        assertEquals( 3, y.size() );

        TraitableBean core = null;
        for ( Object o : y ) {
            if ( o instanceof TraitableBean ) {
                core = (TraitableBean) o;
                break;
            }
        }
        assertNotNull( core );
        assertEquals( 1, core.getDynamicProperties().size() );
        assertNotNull( core.getTrait( "org.drools.factmodel.traits.Thing" ) );
        assertNotNull( core.getTrait( "org.drools.trait.test.Mask" ) );

    }


    @Test
    public void testTraitsOnLegacyJPATriple() {
        traitsLegacyWrapperWithJPA( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test
    public void testTraitsOnLegacyJPAMap() {
        traitsLegacyWrapperWithJPA( TraitFactory.VirtualPropertyMode.MAP );
    }

}
