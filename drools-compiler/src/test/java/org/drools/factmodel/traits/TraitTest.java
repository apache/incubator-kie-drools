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

package org.drools.factmodel.traits;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.drools.CommonTestMethodBase;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.Person;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.command.Command;
import org.drools.command.CommandFactory;
import org.drools.common.AbstractRuleBase;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemoryEntryPoint;
import org.drools.common.ObjectTypeConfigurationRegistry;
import org.drools.definition.type.FactType;
import org.drools.event.rule.AfterActivationFiredEvent;
import org.drools.event.rule.AgendaEventListener;
import org.drools.event.rule.DebugAgendaEventListener;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ByteArrayResource;
import org.drools.io.impl.ClassPathResource;
import org.drools.reteoo.ObjectTypeConf;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.runtime.ClassObjectFilter;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.QueryResultsRow;
import org.drools.util.CodedHierarchyImpl;
import org.drools.util.HierarchyEncoder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.is;

public class TraitTest extends CommonTestMethodBase {

    private static long t0;

    @BeforeClass
    public static void init() {
        t0 = new Date().getTime();
    }

    @AfterClass
    public static void finish() {
        System.out.println( "TIME : " + ( new Date().getTime() - t0 ) );
    }


    private StatefulKnowledgeSession getSession( String... ruleFiles ) {
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        for (String file : ruleFiles) {
            knowledgeBuilder.add( ResourceFactory.newClassPathResource( file ),
                                  ResourceType.DRL );
        }
        if (knowledgeBuilder.hasErrors()) {
            throw new RuntimeException( knowledgeBuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( knowledgeBuilder.getKnowledgePackages() );

        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
        return session;
    }

    private StatefulKnowledgeSession getSessionFromString( String drl ) {
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add( ResourceFactory.newByteArrayResource( drl.getBytes() ),
                ResourceType.DRL );
        if (knowledgeBuilder.hasErrors()) {
            throw new RuntimeException( knowledgeBuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( knowledgeBuilder.getKnowledgePackages() );

        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
        return session;
    }

    private KnowledgeBase getKnowledgeBaseFromString( String drl ) {
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add( ResourceFactory.newByteArrayResource( drl.getBytes() ),
                ResourceType.DRL );
        if (knowledgeBuilder.hasErrors()) {
            throw new RuntimeException( knowledgeBuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( knowledgeBuilder.getKnowledgePackages() );

        return kbase;
    }

    public void traitWrapGetAndSet( TraitFactory.VirtualPropertyMode mode ) {
        String source = "org/drools/factmodel/traits/testTraitDon.drl";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Resource res = ResourceFactory.newClassPathResource( source );
        assertNotNull( res );
        kbuilder.add( res,
                      ResourceType.DRL );
        if (kbuilder.hasErrors()) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kb = KnowledgeBaseFactory.newKnowledgeBase();
            TraitFactory.setMode( mode, kb );
        kb.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        TraitFactory tFactory = ((AbstractRuleBase) ((KnowledgeBaseImpl) kb).getRuleBase()).getConfiguration().getComponentFactory().getTraitFactory();

        try {
            FactType impClass = kb.getFactType( "org.test",
                                                "Imp" );
            TraitableBean imp = (TraitableBean) impClass.newInstance();
            Class trait = kb.getFactType( "org.test",
                                          "Student" ).getFactClass();

            TraitProxy proxy = (TraitProxy) tFactory.getProxy( imp,
                                                               trait );

            Map<String, Object> virtualFields = imp._getDynamicProperties();
            Map<String, Object> wrapper = proxy.getFields();

            wrapper.put( "name",
                         "john" );

            wrapper.put( "virtualField",
                         "xyz" );

            wrapper.entrySet();
            assertEquals( 4,
                          wrapper.size() );
            assertEquals( 2,
                          virtualFields.size() );

            assertEquals( "john",
                          wrapper.get( "name" ) );
            assertEquals( "xyz",
                          wrapper.get( "virtualField" ) );

            assertEquals( "john",
                          impClass.get( imp,
                                        "name" ) );

        } catch (Exception e) {
            e.printStackTrace();
            fail( e.getMessage() );
        }

    }

    @Test
    public void testTraitWrapper_GetAndSetTriple() {
        traitWrapGetAndSet( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test
    public void testTraitWrapper_GetAndSetMap() {
        traitWrapGetAndSet( TraitFactory.VirtualPropertyMode.MAP );
    }












    public void traitShed( TraitFactory.VirtualPropertyMode mode ) {
        String source = "org/drools/factmodel/traits/testTraitShed.drl";

        StatefulKnowledgeSession ks = getSession( source );
        TraitFactory.setMode( mode, ks.getKnowledgeBase() );


        List info = new ArrayList();
        ks.setGlobal( "list",
                      info );

        assertTrue( info.isEmpty() );

        ks.fireAllRules();

        assertTrue( info.contains( "Student" ) );
        assertEquals( 1,
                      info.size() );

        ks.insert( "hire" );
        ks.fireAllRules();

        Collection c = ks.getObjects();

        assertTrue( info.contains( "Worker" ) );
        assertEquals( 2,
                      info.size() );

        ks.insert( "check" );
        ks.fireAllRules();

        assertEquals( 4,
                      info.size() );
        assertTrue( info.contains( "Conflict" ) );
        assertTrue( info.contains( "Nothing" ) );

    }


    @Test
    public void testTraitShedTriple() {
        traitShed( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test
    public void testTraitShedMap() {
        traitShed( TraitFactory.VirtualPropertyMode.MAP );
    }









    public void traitDon( TraitFactory.VirtualPropertyMode mode ) {
        String source = "org/drools/factmodel/traits/testTraitDon.drl";

        StatefulKnowledgeSession ks = getSession( source );
        TraitFactory.setMode( mode, ks.getKnowledgeBase() );

        List info = new ArrayList();
        ks.setGlobal( "list",
                      info );

        ks.fireAllRules();

        Collection<Object> wm = ks.getObjects();

        ks.insert( "go" );
        ks.fireAllRules();

        Assert.assertTrue( info.contains( "DON" ) );
        Assert.assertTrue( info.contains( "SHED" ) );

        Iterator it = wm.iterator();
        Object x = it.next();
        if ( x instanceof String ) {
            x = it.next();
        }

        System.out.println( x.getClass() );
        System.out.println( x.getClass().getSuperclass() );
        System.out.println( Arrays.asList(x.getClass().getInterfaces() ));
    }

    @Test
    public void testTraitDonTriple() {
        traitDon( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test
    public void testTraitDonMap() {
        traitDon( TraitFactory.VirtualPropertyMode.MAP );
    }





    public void mixin( TraitFactory.VirtualPropertyMode mode ) {
        String source = "org/drools/factmodel/traits/testTraitMixin.drl";

        StatefulKnowledgeSession ks = getSession( source );
        TraitFactory.setMode( mode, ks.getKnowledgeBase() );

        List info = new ArrayList();
        ks.setGlobal( "list",
                      info );

        ks.fireAllRules();

        assertTrue( info.contains( "27" ) );

    }

    @Test
    public void testTraitMixinTriple() {
        mixin( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test
    public void testTraitMxinMap() {
        mixin( TraitFactory.VirtualPropertyMode.MAP );
    }







    public void traitMethodsWithObjects( TraitFactory.VirtualPropertyMode mode ) {
        String source = "org/drools/factmodel/traits/testTraitWrapping.drl";

        StatefulKnowledgeSession ks = getSession( source );
        TraitFactory.setMode( mode, ks.getKnowledgeBase() );

        List errors = new ArrayList();
        ks.setGlobal( "list",
                      errors );

        ks.fireAllRules();

        if (!errors.isEmpty()) {
            System.err.println( errors.toString() );
        }
        Assert.assertTrue( errors.isEmpty() );

    }


    @Test
    public void testTraitObjMethodsTriple() {
        traitMethodsWithObjects( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test
    public void testTraitObjMethodsMap() {
        traitMethodsWithObjects( TraitFactory.VirtualPropertyMode.MAP );
    }





    public void traitMethodsWithPrimitives( TraitFactory.VirtualPropertyMode mode ) {
        String source = "org/drools/factmodel/traits/testTraitWrappingPrimitives.drl";

        StatefulKnowledgeSession ks = getSession( source );
        TraitFactory.setMode( mode, ks.getKnowledgeBase() );

        List errors = new ArrayList();
        ks.setGlobal( "list",
                      errors );

        ks.fireAllRules();

        if (!errors.isEmpty()) {
            System.err.println( errors );
        }
        Assert.assertTrue( errors.isEmpty() );

    }


    @Test
    public void testTraitPrimMethodsTriple() {
        traitMethodsWithPrimitives( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test
    public void testTraitPrimMethodsMap() {
        traitMethodsWithPrimitives( TraitFactory.VirtualPropertyMode.MAP );
    }








    public void traitProxy( TraitFactory.VirtualPropertyMode mode ) {

        String source = "org/drools/factmodel/traits/testTraitDon.drl";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Resource res = ResourceFactory.newClassPathResource( source );
        assertNotNull( res );
        kbuilder.add( res,
                      ResourceType.DRL );
        if (kbuilder.hasErrors()) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kb = KnowledgeBaseFactory.newKnowledgeBase();
        kb.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        TraitFactory.setMode( mode, kb );
        TraitFactory tFactory = ((AbstractRuleBase) ((KnowledgeBaseImpl) kb).getRuleBase()).getConfiguration().getComponentFactory().getTraitFactory();

        try {
            FactType impClass = kb.getFactType( "org.test",
                                                "Imp" );
            TraitableBean imp = (TraitableBean) impClass.newInstance();
            impClass.set( imp,
                          "name",
                          "aaa" );

            Class trait = kb.getFactType( "org.test",
                                          "Student" ).getFactClass();
            Class trait2 = kb.getFactType( "org.test",
                                           "Role" ).getFactClass();

            assertNotNull( trait );
            TraitProxy proxy = (TraitProxy) tFactory.getProxy( imp,
                                                                   trait );
            proxy.getFields().put( "field",
                                   "xyz" );
            //            proxy.getFields().put("name", "aaa");

            assertNotNull( proxy );

            TraitProxy proxy2 = (TraitProxy) tFactory.getProxy( imp,
                                                                    trait );
            assertSame( proxy2,
                        proxy );

            TraitProxy proxy3 = (TraitProxy) tFactory.getProxy( imp,
                                                                    trait2 );
            assertNotNull( proxy3 );
            assertEquals( "xyz",
                          proxy3.getFields().get( "field" ) );
            assertEquals( "aaa",
                          proxy3.getFields().get( "name" ) );

            TraitableBean imp2 = (TraitableBean) impClass.newInstance();
            impClass.set( imp2,
                          "name",
                          "aaa" );
            TraitProxy proxy4 = (TraitProxy) tFactory.getProxy( imp2,
                                                                    trait );
            //            proxy4.getFields().put("name", "aaa");
            proxy4.getFields().put( "field",
                                    "xyz" );

            Assert.assertEquals( proxy2,
                                 proxy4 );

        } catch (InstantiationException e) {
            e.printStackTrace();
            fail( e.getMessage() );
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            fail( e.getMessage() );
        } catch (LogicalTypeInconsistencyException e) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }


    @Test
    public void testTraitProxyTriple() {
        traitProxy( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test
    public void testTraitProxyMap() {
        traitProxy( TraitFactory.VirtualPropertyMode.MAP );
    }









    public void wrapperSize( TraitFactory.VirtualPropertyMode mode ) {
        String source = "org/drools/factmodel/traits/testTraitDon.drl";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Resource res = ResourceFactory.newClassPathResource( source );
        assertNotNull( res );
        kbuilder.add( res,
                      ResourceType.DRL );
        if (kbuilder.hasErrors()) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kb = KnowledgeBaseFactory.newKnowledgeBase();
        kb.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        TraitFactory.setMode( mode, kb );
        TraitFactory tFactory = ((AbstractRuleBase) ((KnowledgeBaseImpl) kb).getRuleBase()).getConfiguration().getComponentFactory().getTraitFactory();


        try {
            FactType impClass = kb.getFactType( "org.test",
                                                "Imp" );
            TraitableBean imp = (TraitableBean) impClass.newInstance();
            FactType traitClass = kb.getFactType( "org.test",
                                                  "Student" );
            Class trait = traitClass.getFactClass();
            TraitProxy proxy = (TraitProxy) tFactory.getProxy( imp,
                                                                   trait );

            Map<String, Object> virtualFields = imp._getDynamicProperties();
            Map<String, Object> wrapper = proxy.getFields();
            assertEquals( 3,
                          wrapper.size() );
            assertEquals( 1,
                          virtualFields.size() );

            impClass.set(imp,
                    "name",
                    "john");
            assertEquals( 3,
                          wrapper.size() );
            assertEquals( 1,
                          virtualFields.size() );

            proxy.getFields().put( "school",
                                   "skol" );
            assertEquals( 3,
                          wrapper.size() );
            assertEquals( 1,
                          virtualFields.size() );

            proxy.getFields().put( "surname",
                                   "xxx" );
            assertEquals( 4,
                          wrapper.size() );
            assertEquals( 2,
                          virtualFields.size() );

            //            FactType indClass = kb.getFactType("org.test","Entity");
            //            TraitableBean ind = (TraitableBean) indClass.newInstance();
            TraitableBean ind = new Entity();

            TraitProxy proxy2 = (TraitProxy) tFactory.getProxy( ind,
                                                                    trait );

            Map virtualFields2 = ind._getDynamicProperties();
            Map wrapper2 = proxy2.getFields();
            assertEquals( 3,
                          wrapper2.size() );
            assertEquals( 3,
                          virtualFields2.size() );

            traitClass.set( proxy2,
                            "name",
                            "john" );
            assertEquals( 3,
                          wrapper2.size() );
            assertEquals( 3,
                          virtualFields2.size() );

            proxy2.getFields().put( "school",
                                    "skol" );
            assertEquals( 3,
                          wrapper2.size() );
            assertEquals( 3,
                          virtualFields2.size() );

            proxy2.getFields().put( "surname",
                                    "xxx" );
            assertEquals( 4,
                          wrapper2.size() );
            assertEquals( 4,
                          virtualFields2.size() );

            FactType traitClass2 = kb.getFactType( "org.test",
                                                   "Role" );
            Class trait2 = traitClass2.getFactClass();
            //            TraitableBean ind2 = (TraitableBean) indClass.newInstance();
            TraitableBean ind2 = new Entity();

            TraitProxy proxy99 = (TraitProxy) tFactory.getProxy( ind2,
                                                                     trait2 );

            proxy99.getFields().put( "surname",
                                     "xxx" );
            proxy99.getFields().put( "name",
                                     "xyz" );
            proxy99.getFields().put( "school",
                                     "skol" );

            assertEquals( 3,
                          proxy99.getFields().size() );

            TraitProxy proxy100 = (TraitProxy) tFactory.getProxy( ind2,
                                                                      trait );

            assertEquals( 4,
                          proxy100.getFields().size() );

        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }

    }


    @Test
    public void testTraitWrapperSizeTriple() {
        wrapperSize( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test
    public void testTraitWrapperSizeMap() {
        wrapperSize( TraitFactory.VirtualPropertyMode.MAP );
    }







    public void wrapperEmpty( TraitFactory.VirtualPropertyMode mode ) {
        String source = "org/drools/factmodel/traits/testTraitDon.drl";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Resource res = ResourceFactory.newClassPathResource( source );
        assertNotNull( res );
        kbuilder.add( res,
                      ResourceType.DRL );
        if (kbuilder.hasErrors()) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kb = KnowledgeBaseFactory.newKnowledgeBase();
        kb.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        TraitFactory.setMode( mode, kb );

        TraitFactory tFactory = ((AbstractRuleBase) ((KnowledgeBaseImpl) kb).getRuleBase()).getConfiguration().getComponentFactory().getTraitFactory();

        try {
            FactType impClass = kb.getFactType( "org.test",
                                                "Imp" );
            TraitableBean imp = (TraitableBean) impClass.newInstance();

            FactType studentClass = kb.getFactType( "org.test",
                                                    "Student" );
            Class trait = studentClass.getFactClass();
            TraitProxy proxy = (TraitProxy) tFactory.getProxy( imp,
                                                               trait );

            Map<String, Object> virtualFields = imp._getDynamicProperties();
            Map<String, Object> wrapper = proxy.getFields();
            assertFalse( wrapper.isEmpty() );

            studentClass.set( proxy,
                              "name",
                              "john" );
            assertFalse( wrapper.isEmpty() );
            studentClass.set( proxy,
                              "name",
                              null );
            assertFalse( wrapper.isEmpty() );

            studentClass.set( proxy,
                              "age",
                              32 );
            assertFalse( wrapper.isEmpty() );

            studentClass.set( proxy,
                              "age",
                              null );
            assertFalse( wrapper.isEmpty() );

            //            FactType indClass = kb.getFactType("org.test","Entity");
            TraitableBean ind = new Entity();

            FactType RoleClass = kb.getFactType( "org.test",
                                                 "Role" );
            Class trait2 = RoleClass.getFactClass();
            TraitProxy proxy2 = (TraitProxy) tFactory.getProxy( ind,
                                                                trait2 );

            Map<String, Object> wrapper2 = proxy2.getFields();
            assertTrue( wrapper2.isEmpty() );

            proxy2.getFields().put( "name",
                                    "john" );
            assertFalse( wrapper2.isEmpty() );

            proxy2.getFields().put( "name",
                                    null );
            assertFalse( wrapper2.isEmpty() );

        } catch (Exception e) {
            e.printStackTrace();
            fail( e.getMessage() );
        }

    }

    @Test
    public void testTraitWrapperEmptyTriple() {
        wrapperEmpty( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test
    public void testTraitWrapperEmptyMap() {
        wrapperEmpty( TraitFactory.VirtualPropertyMode.MAP );
    }









    public void wrapperContainsKey( TraitFactory.VirtualPropertyMode mode ) {
        String source = "org/drools/factmodel/traits/testTraitDon.drl";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Resource res = ResourceFactory.newClassPathResource( source );
        assertNotNull( res );
        kbuilder.add( res,
                      ResourceType.DRL );
        if (kbuilder.hasErrors()) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kb = KnowledgeBaseFactory.newKnowledgeBase();
        kb.addKnowledgePackages( kbuilder.getKnowledgePackages() );


        TraitFactory.setMode( mode, kb );
        TraitFactory tFactory = ((AbstractRuleBase) ((KnowledgeBaseImpl) kb).getRuleBase()).getConfiguration().getComponentFactory().getTraitFactory();

        try {
            FactType impClass = kb.getFactType( "org.test",
                                                "Imp" );
            TraitableBean imp = (TraitableBean) impClass.newInstance();
            impClass.set( imp,
                          "name",
                          "john" );

            FactType traitClass = kb.getFactType( "org.test",
                                                  "Student" );
            Class trait = traitClass.getFactClass();
            TraitProxy proxy = (TraitProxy) tFactory.getProxy( imp,
                                                                   trait );

            Map<String, Object> virtualFields = imp._getDynamicProperties();
            Map<String, Object> wrapper = proxy.getFields();

            assertTrue( wrapper.containsKey( "name" ) );
            assertTrue( wrapper.containsKey( "school" ) );
            assertTrue( wrapper.containsKey( "age" ) );
            assertFalse( wrapper.containsKey( "surname" ) );

            proxy.getFields().put( "school",
                                   "skol" );
            proxy.getFields().put( "surname",
                                   "xxx" );
            assertTrue( wrapper.containsKey( "surname" ) );

            //            FactType indClass = kb.getFactType("org.test","Entity");
            TraitableBean ind = new Entity();

            TraitProxy proxy2 = (TraitProxy) tFactory.getProxy( ind,
                                                                trait );

            Map virtualFields2 = ind._getDynamicProperties();
            Map wrapper2 = proxy2.getFields();
            assertTrue( wrapper2.containsKey( "name" ) );
            assertTrue( wrapper2.containsKey( "school" ) );
            assertTrue( wrapper2.containsKey( "age" ) );
            assertFalse( wrapper2.containsKey( "surname" ) );

            traitClass.set( proxy2,
                            "name",
                            "john" );
            proxy2.getFields().put( "school",
                                    "skol" );
            proxy2.getFields().put( "surname",
                                    "xxx" );
            assertTrue( wrapper2.containsKey( "surname" ) );

            FactType traitClass2 = kb.getFactType( "org.test",
                                                   "Role" );
            Class trait2 = traitClass2.getFactClass();
            TraitableBean ind2 = new Entity();

            TraitProxy proxy99 = (TraitProxy) tFactory.getProxy( ind2,
                                                                 trait2 );
            Map<String, Object> wrapper99 = proxy99.getFields();

            assertFalse( wrapper99.containsKey( "name" ) );
            assertFalse( wrapper99.containsKey( "school" ) );
            assertFalse( wrapper99.containsKey( "age" ) );
            assertFalse( wrapper99.containsKey( "surname" ) );

            proxy99.getFields().put( "surname",
                                     "xxx" );
            proxy99.getFields().put( "name",
                                     "xyz" );
            proxy99.getFields().put( "school",
                                     "skol" );

            assertTrue( wrapper99.containsKey( "name" ) );
            assertTrue( wrapper99.containsKey( "school" ) );
            assertFalse( wrapper99.containsKey( "age" ) );
            assertTrue( wrapper99.containsKey( "surname" ) );
            assertEquals( 3,
                          proxy99.getFields().size() );

            TraitableBean ind0 = new Entity();

            TraitProxy proxy100 = (TraitProxy) tFactory.getProxy( ind0,
                                                                  trait2 );
            Map<String, Object> wrapper100 = proxy100.getFields();
            assertFalse( wrapper100.containsKey( "name" ) );
            assertFalse( wrapper100.containsKey( "school" ) );
            assertFalse( wrapper100.containsKey( "age" ) );
            assertFalse( wrapper100.containsKey( "surname" ) );

            TraitProxy proxy101 = (TraitProxy) tFactory.getProxy( ind0,
                                                                  trait );
            // object gains properties by virtue of another trait
            // so new props are accessible even using the old proxy
            assertTrue( wrapper100.containsKey( "name" ) );
            assertTrue( wrapper100.containsKey( "school" ) );
            assertTrue( wrapper100.containsKey( "age" ) );
            assertFalse( wrapper100.containsKey( "surname" ) );

        } catch (Exception e) {
            e.printStackTrace();
            fail( e.getMessage() );
        }

    }

    @Test
    public void testTraitContainskeyTriple() {
        wrapperContainsKey( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test
    public void testTraitContainskeyMap() {
        wrapperContainsKey( TraitFactory.VirtualPropertyMode.MAP );
    }







    public void wrapperKeySetAndValues( TraitFactory.VirtualPropertyMode mode ) {
        String source = "org/drools/factmodel/traits/testTraitDon.drl";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Resource res = ResourceFactory.newClassPathResource( source );
        assertNotNull( res );
        kbuilder.add( res,
                      ResourceType.DRL );
        if (kbuilder.hasErrors()) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kb = KnowledgeBaseFactory.newKnowledgeBase();
        kb.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        TraitFactory.setMode( mode, kb );

        TraitFactory tFactory = ((AbstractRuleBase) ((KnowledgeBaseImpl) kb).getRuleBase()).getConfiguration().getComponentFactory().getTraitFactory();

        try {
            FactType impClass = kb.getFactType( "org.test",
                                                "Imp" );
            TraitableBean imp = (TraitableBean) impClass.newInstance();
            FactType traitClass = kb.getFactType( "org.test",
                                                  "Student" );
            Class trait = traitClass.getFactClass();
            TraitProxy proxy = (TraitProxy) tFactory.getProxy( imp,
                                                               trait );

            impClass.set( imp,
                          "name",
                          "john" );
            proxy.getFields().put( "surname",
                                   "xxx" );
            proxy.getFields().put( "name2",
                                   "john" );
            proxy.getFields().put( "nfield",
                                   null );

            Set set = new HashSet();
            set.add( "name" );
            set.add( "surname" );
            set.add( "age" );
            set.add( "school" );
            set.add( "name2" );
            set.add( "nfield" );

            assertEquals( 6,
                          proxy.getFields().keySet().size() );
            assertEquals( set,
                          proxy.getFields().keySet() );

            Collection col1 = proxy.getFields().values();
            Collection col2 = Arrays.asList( "john",
                                             null,
                                             0,
                                             "xxx",
                                             "john",
                                             null );

            Comparator comp = new Comparator() {

                public int compare( Object o1, Object o2 ) {
                    if (o1 == null && o2 != null) {
                        return 1;
                    }
                    if (o1 != null && o2 == null) {
                        return -1;
                    }
                    if (o1 == null && o2 == null) {
                        return 0;
                    }
                    return o1.toString().compareTo( o2.toString() );
                }
            };

            Collections.sort( (List) col1,
                              comp );
            Collections.sort( (List) col2,
                              comp );
            assertEquals( col1,
                          col2 );

            assertTrue( proxy.getFields().containsValue( null ) );
            assertTrue( proxy.getFields().containsValue( "john" ) );
            assertTrue( proxy.getFields().containsValue( 0 ) );
            assertTrue( proxy.getFields().containsValue( "xxx" ) );
            assertFalse( proxy.getFields().containsValue( "randomString" ) );
            assertFalse( proxy.getFields().containsValue( -96 ) );

        } catch (Exception e) {
            e.printStackTrace();
            fail( e.getMessage() );
        }

    }

    @Test
    public void testTraitWrapperKSVTriple() {
        wrapperKeySetAndValues( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test
    public void testTraitWrapperKSVMap() {
        wrapperKeySetAndValues( TraitFactory.VirtualPropertyMode.MAP );
    }







    public void wrapperClearAndRemove( TraitFactory.VirtualPropertyMode mode ) {
        String source = "org/drools/factmodel/traits/testTraitDon.drl";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Resource res = ResourceFactory.newClassPathResource( source );
        assertNotNull( res );
        kbuilder.add( res,
                      ResourceType.DRL );
        if (kbuilder.hasErrors()) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kb = KnowledgeBaseFactory.newKnowledgeBase();
        kb.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        TraitFactory.setMode( mode, kb );
        TraitFactory tFactory = ((AbstractRuleBase) ((KnowledgeBaseImpl) kb).getRuleBase()).getConfiguration().getComponentFactory().getTraitFactory();

        try {
            FactType impClass = kb.getFactType( "org.test",
                                                "Imp" );
            TraitableBean imp = (TraitableBean) impClass.newInstance();
            impClass.set( imp,
                          "name",
                          "john" );
            FactType traitClass = kb.getFactType( "org.test",
                                                  "Student" );
            Class trait = traitClass.getFactClass();
            TraitProxy proxy = (TraitProxy) tFactory.getProxy( imp,
                                                               trait );

            proxy.getFields().put( "surname",
                                   "xxx" );
            proxy.getFields().put( "name2",
                                   "john" );
            proxy.getFields().put( "nfield",
                                   null );

            Set set = new HashSet();
            set.add( "name" );
            set.add( "surname" );
            set.add( "age" );
            set.add( "school" );
            set.add( "name2" );
            set.add( "nfield" );

            assertEquals( 6,
                          proxy.getFields().keySet().size() );
            assertEquals( set,
                          proxy.getFields().keySet() );

            proxy.getFields().clear();

            Map<String, Object> fields = proxy.getFields();
            assertEquals( 3,
                          fields.size() );
            assertTrue( fields.containsKey( "age" ) );
            assertTrue( fields.containsKey( "school" ) );
            assertTrue( fields.containsKey( "name" ) );

            assertEquals( 0,
                          fields.get( "age" ) );
            assertNull( fields.get( "school" ) );
            assertNotNull( fields.get( "name" ) );

            proxy.getFields().put( "surname",
                                   "xxx" );
            proxy.getFields().put( "name2",
                                   "john" );
            proxy.getFields().put( "nfield",
                                   null );
            proxy.getFields().put( "age",
                                   24 );

            assertEquals( "john",
                          proxy.getFields().get( "name" ) );
            assertEquals( "xxx",
                          proxy.getFields().get( "surname" ) );
            assertEquals( "john",
                          proxy.getFields().get( "name2" ) );
            assertEquals( null,
                          proxy.getFields().get( "nfield" ) );
            assertEquals( 24,
                          proxy.getFields().get( "age" ) );
            assertEquals( null,
                          proxy.getFields().get( "school" ) );

            proxy.getFields().remove( "surname" );
            proxy.getFields().remove( "name2" );
            proxy.getFields().remove( "age" );
            proxy.getFields().remove( "school" );
            proxy.getFields().remove( "nfield" );
            assertEquals( 3,
                          proxy.getFields().size() );

            assertEquals( 0,
                          proxy.getFields().get( "age" ) );
            assertEquals( null,
                          proxy.getFields().get( "school" ) );
            assertEquals( "john",
                          proxy.getFields().get( "name" ) );

            assertEquals( null,
                          proxy.getFields().get( "nfield" ) );
            assertFalse( proxy.getFields().containsKey( "nfield" ) );

            assertEquals( null,
                          proxy.getFields().get( "name2" ) );
            assertFalse( proxy.getFields().containsKey( "name2" ) );

            assertEquals( null,
                          proxy.getFields().get( "surname" ) );
            assertFalse( proxy.getFields().containsKey( "surname" ) );

        } catch (Exception e) {
            e.printStackTrace();
            fail( e.getMessage() );
        }

    }

    @Test
    public void testTraitWrapperClearTriples() {
        wrapperClearAndRemove( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test
    public void testTraitWrapperClearMap() {
        wrapperClearAndRemove( TraitFactory.VirtualPropertyMode.MAP );
    }







    public void isA( TraitFactory.VirtualPropertyMode mode ) {
        String source = "org/drools/factmodel/traits/testTraitIsA.drl";

        StatefulKnowledgeSession ks = getSession( source );
        TraitFactory.setMode( mode, ks.getKnowledgeBase() );

        List info = new ArrayList();
        ks.setGlobal( "list",
                      info );

        ks.fireAllRules();


        int num = 10;

        System.out.println( info );
        assertEquals( num,
                      info.size() );
        for (int j = 0; j < num; j++) {
            assertTrue( info.contains( "" + j ) );
        }

    }

    @Test
    public void testISATriple() {
        isA( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test
    public void testISAMap() {
        isA( TraitFactory.VirtualPropertyMode.MAP );
    }



    @Test
    public void testIsAEvaluator( ) {
        String source = "package org.test;\n" +
                "\n" +
                "import org.drools.factmodel.traits.Traitable;\n" +
                "import org.drools.factmodel.traits.Entity;\n" +
                "import org.drools.factmodel.traits.Thing;\n" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +
                "\n" +
                "declare Imp\n" +
                "    @Traitable\n" +
                "    name    : String        @key\n" +
                "end\n" +
                "\n" +
                "declare trait Person\n" +
                "    name    : String \n" +
                "    age     : int   \n" +
                "end\n" +
                "  \n" +
                "declare trait Worker\n" +
                "    job     : String\n" +
                "end\n" +
                " \n" +
                "\n" +
                " \n" +
                " \n" +
                "rule \"Init\"\n" +
                "when\n" +
                "then\n" +
                "    Imp core = new Imp( \"joe\" );\n" +
                "    insert( core );\n" +
                "    don( core, Person.class );\n" +
                "    don( core, Worker.class );\n" +
                "\n" +
                "    Imp core2 = new Imp( \"adam\" );\n" +
                "    insert( core2 );\n" +
                "    don( core2, Worker.class );\n" +
                "end\n" +
                "\n" +
                "rule \"Mod\"\n" +
                "when\n" +
                "    $p : Person( name == \"joe\" )\n" +
                "then\n" +
                "    modify ($p) { setName( \"john\" ); }\n" +
                "end\n" +
                "\n" +
                "rule \"Worker Students v6\"\n" +
                "when\n" +
                "    $x2 := Person( name == \"john\" )\n" +
                "    $x1 := Worker( core != $x2.core, this not isA $x2 )\n" +
                "then\n" +
                "    list.add( \"ok\" );\n" +
                "end\n" +
                "\n" +
                "\n";

        StatefulKnowledgeSession ks = getSessionFromString( source );
        TraitFactory.setMode( TraitFactory.VirtualPropertyMode.TRIPLES, ks.getKnowledgeBase() );

        List info = new ArrayList();
        ks.setGlobal( "list",
                info );

        ks.fireAllRules();

        System.out.println( info );
        assertTrue( info.contains( "ok" ) );
    }






    public void overrideType( TraitFactory.VirtualPropertyMode mode ) {
        String source = "org/drools/factmodel/traits/testTraitOverride.drl";

        StatefulKnowledgeSession ks = getSession( source );
        TraitFactory.setMode( mode, ks.getKnowledgeBase() );

        List info = new ArrayList();
        ks.setGlobal( "list",
                      info );

        ks.fireAllRules();

        Collection wm = ks.getObjects();

        assertTrue( info.contains( "OK" ) );

    }

    @Test
    public void testOverrideTriple() {
        overrideType( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test
    public void testOverrideMap() {
        overrideType( TraitFactory.VirtualPropertyMode.MAP );
    }









    public void traitLegacy( TraitFactory.VirtualPropertyMode mode ) {
        String source = "org/drools/factmodel/traits/testTraitLegacyTrait.drl";

        StatefulKnowledgeSession ks = getSession( source );
        TraitFactory.setMode( mode, ks.getKnowledgeBase() );


        List info = new ArrayList();
        ks.setGlobal( "list",
                      info );

        ks.fireAllRules();

        System.err.println( " -------------- " + ks.getObjects().size() + " ---------------- " );
        for (Object o : ks.getObjects()) {
            System.err.println( "\t\t" + o );
        }
        System.err.println( " --------------  ---------------- " );
        System.err.println( info );
        System.err.println( " --------------  ---------------- " );

        assertEquals( 5,
                      info.size() );
        assertTrue( info.contains( "OK" ) );
        assertTrue( info.contains( "OK2" ) );
        assertTrue( info.contains( "OK3" ) );
        assertTrue( info.contains( "OK4" ) );
        assertTrue( info.contains( "OK5" ) );

    }

    @Test
    public void testLegacyTriple() {
        traitLegacy( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test
    public void testLegacyMap() {
        traitLegacy( TraitFactory.VirtualPropertyMode.MAP );
    }








    public void traitCollections( TraitFactory.VirtualPropertyMode mode ) {
        String source = "org/drools/factmodel/traits/testTraitCollections.drl";

        StatefulKnowledgeSession ks = getSession( source );
        TraitFactory.setMode( mode, ks.getKnowledgeBase() );


        List info = new ArrayList();
        ks.setGlobal( "list",
                      info );

        ks.fireAllRules();

        System.err.println( " -------------- " + ks.getObjects().size() + " ---------------- " );
        for (Object o : ks.getObjects()) {
            System.err.println( "\t\t" + o );
        }
        System.err.println( " --------------  ---------------- " );
        System.err.println( info );
        System.err.println( " --------------  ---------------- " );

        assertEquals( 1,
                      info.size() );
        assertTrue( info.contains( "OK" ) );

    }


    @Test
    public void testCollectionsTriple() {
        traitCollections( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test
    public void testCollectionsMap() {
        traitCollections( TraitFactory.VirtualPropertyMode.MAP );
    }







    public void traitCore( TraitFactory.VirtualPropertyMode mode ) {
        String source = "org/drools/factmodel/traits/testTraitLegacyCore.drl";

        StatefulKnowledgeSession ks = getSession( source );
        TraitFactory.setMode( mode, ks.getKnowledgeBase() );

        List info = new ArrayList();
        ks.setGlobal( "list",
                      info );

        ks.fireAllRules();

        System.err.println( " -------------- " + ks.getObjects().size() + " ---------------- " );
        for (Object o : ks.getObjects()) {
            System.err.println( "\t\t" + o );
        }
        System.err.println( " --------------  ---------------- " );
        System.err.println( info );
        System.err.println( " --------------  ---------------- " );

        assertTrue( info.contains( "OK" ) );
        assertTrue( info.contains( "OK2" ) );
        assertEquals( 2,
                      info.size() );

    }


    @Test
    public void testCoreTriple() {
        traitCore( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test
    public void testCoreMap() {
        traitCore( TraitFactory.VirtualPropertyMode.MAP );
    }







    public void traitWithEquality( TraitFactory.VirtualPropertyMode mode ) {
        String source = "org/drools/factmodel/traits/testTraitWithEquality.drl";

        StatefulKnowledgeSession ks = getSession( source );
        TraitFactory.setMode( mode, ks.getKnowledgeBase() );

        List info = new ArrayList();
        ks.setGlobal( "list",
                      info );

        ks.fireAllRules();

        Assert.assertTrue( info.contains( "DON" ) );
        Assert.assertTrue( info.contains( "EQUAL" ) );

    }

    @Test
    public void testEqTriple() {
        traitWithEquality( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test
    public void testEqMap() {
        traitWithEquality( TraitFactory.VirtualPropertyMode.MAP );
    }







    public void traitDeclared( TraitFactory.VirtualPropertyMode mode ) {

        List<Integer> trueTraits = new ArrayList<Integer>();
        List<Integer> untrueTraits = new ArrayList<Integer>();

        StatefulKnowledgeSession ks = getSession( "org/drools/factmodel/traits/testDeclaredFactTrait.drl" );
        TraitFactory.setMode( mode, ks.getKnowledgeBase() );

        ks.setGlobal( "trueTraits",
                           trueTraits );
        ks.setGlobal( "untrueTraits",
                           untrueTraits );

        ks.fireAllRules();
        ks.dispose();

        assertTrue( trueTraits.contains( 1 ) );
        assertFalse( trueTraits.contains( 2 ) );
        assertTrue( untrueTraits.contains( 2 ) );
        assertFalse( untrueTraits.contains( 1 ) );
    }

    @Test
    public void testDeclaredTriple() {
        traitDeclared( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test
    public void testDeclaredMap() {
        traitDeclared( TraitFactory.VirtualPropertyMode.MAP );
    }







    public void traitPojo( TraitFactory.VirtualPropertyMode mode ) {

        List<Integer> trueTraits = new ArrayList<Integer>();
        List<Integer> untrueTraits = new ArrayList<Integer>();

        StatefulKnowledgeSession session = getSession( "org/drools/factmodel/traits/testPojoFactTrait.drl" );
        TraitFactory.setMode( mode, session.getKnowledgeBase() );

        session.setGlobal( "trueTraits",
                           trueTraits );
        session.setGlobal( "untrueTraits",
                           untrueTraits );

        session.fireAllRules();
        session.dispose();

        assertTrue( trueTraits.contains( 1 ) );
        assertFalse( trueTraits.contains( 2 ) );
        assertTrue( untrueTraits.contains( 2 ) );
        assertFalse( untrueTraits.contains( 1 ) );
    }

    @Test
    public void testPojoTriple() {
        traitPojo( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test
    public void testPojoMap() {
        traitPojo( TraitFactory.VirtualPropertyMode.MAP );
    }







    public void isAOperator( TraitFactory.VirtualPropertyMode mode ) {
        String source = "org/drools/factmodel/traits/testTraitIsA2.drl";
        StatefulKnowledgeSession ksession = getSession( source );
        TraitFactory.setMode( mode, ksession.getKnowledgeBase() );


        AgendaEventListener ael = mock( AgendaEventListener.class );
        ksession.addEventListener( ael );

        Person student = new Person( "student", 18 );
        ksession.insert( student );

        ksession.fireAllRules();

        ArgumentCaptor<AfterActivationFiredEvent> cap = ArgumentCaptor.forClass( AfterActivationFiredEvent.class );
        verify( ael,
                times( 3 ) ).afterActivationFired( cap.capture() );

        List<AfterActivationFiredEvent> values = cap.getAllValues();

        assertThat( values.get( 0 ).getActivation().getRule().getName(),
                    is( "create student" ) );
        assertThat( values.get( 1 ).getActivation().getRule().getName(),
                    is( "print student" ) );
        assertThat( values.get( 2 ).getActivation().getRule().getName(),
                    is( "print school" ) );

    }

    @Test
    public void testISA2Triple() {
        isAOperator( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test
    public void testISA2Map() {
        isAOperator( TraitFactory.VirtualPropertyMode.MAP );
    }





    protected void manyTraits( TraitFactory.VirtualPropertyMode mode ) {
        String source = "" +
                "import org.drools.Message;" +
                "" +
                "global java.util.List list; \n" +
                "" +
                "declare Message\n" +
                "      @Traitable\n" +
                "    end\n" +
                "\n" +
                "    declare trait NiceMessage\n" +
                "       message : String\n" +
                "    end\n" +
                "" +
                "rule \"Nice\"\n" +
                "when\n" +
                "  $n : NiceMessage( $m : message )\n" +
                "then\n" +
                "  System.out.println( $m );\n" +
                "end" +
                "\n" +
                "    rule load\n" +
                "        when\n" +
                "\n" +
                "        then\n" +
                "            Message message = new Message();\n" +
                "            message.setMessage(\"Hello World\");\n" +
                "            insert(message);\n" +
                "            don( message, NiceMessage.class );\n" +
                "\n" +
                "            Message unreadMessage = new Message();\n" +
                "            unreadMessage.setMessage(\"unread\");\n" +
                "            insert(unreadMessage);\n" +
                "            don( unreadMessage, NiceMessage.class );\n" +
                "\n" +
                "            Message oldMessage = new Message();\n" +
                "            oldMessage.setMessage(\"old\");\n" +
                "            insert(oldMessage);\n" +
                "            don( oldMessage, NiceMessage.class );" +

                "            list.add(\"OK\");\n" +
                "    end";
        StatefulKnowledgeSession ksession = getSessionFromString( source );
        TraitFactory.setMode( mode, ksession.getKnowledgeBase() );


        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        Person student = new Person( "student", 18 );
        ksession.insert( student );

        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertTrue( list.contains( "OK" ) );

    }

    @Test
    public void testManyTraitsTriples() {
        manyTraits( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test
    public void testManyTraitsMap() {
        manyTraits( TraitFactory.VirtualPropertyMode.MAP );
    }







    public void traitManyTimes( TraitFactory.VirtualPropertyMode mode ) {

        StatefulKnowledgeSession ksession = getSession( "org/drools/factmodel/traits/testTraitDonMultiple.drl" );
        TraitFactory.setMode( mode, ksession.getKnowledgeBase() );

        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        ksession.fireAllRules();

        for ( Object o : ksession.getObjects() ) {
            System.err.println( o );
        }
        Collection x = ksession.getObjects();
        assertEquals( 3, ksession.getObjects().size() );

        assertEquals( 5, list.size() );
        assertEquals( 0, list.get( 0 ) );
        assertTrue( list.contains( 1 ) );
        assertTrue( list.contains( 2 ) );
        assertTrue( list.contains( 3 ) );
        assertTrue( list.contains( 4 ) );


    }


    @Test
    public void testManyTriple() {
        traitManyTimes( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test
    public void testManyMap() {
        traitManyTimes( TraitFactory.VirtualPropertyMode.MAP );
    }






    // BZ #748752
    public void traitsInBatchExecution( TraitFactory.VirtualPropertyMode mode ) {
        String str = "package org.jboss.qa.brms.traits\n" +
                "import org.drools.Person;\n" +
                "import org.drools.factmodel.traits.Traitable;\n" +
                "" +
                "global java.util.List list;" +
                "" +
                "declare Person \n" +
                "  @Traitable \n" +
                "end \n" +
                "" +
                "declare trait Student\n" +
                "  school : String\n" +
                "end\n" +
                "\n" +
                "rule \"create student\" \n" +
                "  when\n" +
                "    $student : Person( age < 26 )\n" +
                "  then\n" +
                "    Student s = don( $student, Student.class );\n" +
                "    s.setSchool(\"Masaryk University\");\n" +
                "end\n" +
                "\n" +
                "rule \"print student\"\n" +
                "  when\n" +
                "    student : Person( this isA Student )\n" +
                "  then" +
                "    list.add( 1 );\n" +
                "    System.out.println(\"Person is a student: \" + student);\n" +
                "end\n" +
                "\n" +
                "rule \"print school\"\n" +
                "  when\n" +
                "    Student( $school : school )\n" +
                "  then\n" +
                "    list.add( 2 );\n" +
                "    System.out.println(\"Student is attending \" + $school);\n" +
                "end";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ByteArrayResource( str.getBytes() ), ResourceType.DRL );

        if (kbuilder.hasErrors()) {
            throw new RuntimeException(kbuilder.getErrors().toString());
        }

        List list = new ArrayList();

        KnowledgeBase kbase = kbuilder.newKnowledgeBase();
        TraitFactory.setMode( mode, kbase );

        StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession();


        ksession.setGlobal( "list", list );

        List<Command<?>> commands = new ArrayList<Command<?>>();
        Person student = new Person("student", 18);
        commands.add(CommandFactory.newInsert(student));

        System.out.println("Starting execution...");
        commands.add(CommandFactory.newFireAllRules());
        ksession.execute(CommandFactory.newBatchExecution(commands));

        System.out.println("Finished...");

        assertEquals( 2, list.size() );
        assertTrue( list.contains( 1 ) );
        assertTrue( list.contains( 2 ) );
    }

    @Test
    public void testBatchTriple() {
        traitsInBatchExecution( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test
    public void testBatchMap() {
        traitsInBatchExecution( TraitFactory.VirtualPropertyMode.MAP );
    }







    public void manyTraitsStateless( TraitFactory.VirtualPropertyMode mode ) {
        String source = "" +
                "import org.drools.Message;" +
                "" +
                "global java.util.List list; \n" +
                "" +
                "declare Message\n" +
                "      @Traitable\n" +
                "    end\n" +
                "\n" +
                "    declare trait NiceMessage\n" +
                "       message : String\n" +
                "    end\n" +
                "" +
                "rule \"Nice\"\n" +
                "when\n" +
                "  $n : NiceMessage( $m : message )\n" +
                "then\n" +
                "  System.out.println( $m );\n" +
                "end" +
                "\n" +
                "    rule load\n" +
                "        when\n" +
                "\n" +
                "        then\n" +
                "            Message message = new Message();\n" +
                "            message.setMessage(\"Hello World\");\n" +
                "            insert(message);\n" +
                "            don( message, NiceMessage.class );\n" +
                "\n" +
                "            Message unreadMessage = new Message();\n" +
                "            unreadMessage.setMessage(\"unread\");\n" +
                "            insert(unreadMessage);\n" +
                "            don( unreadMessage, NiceMessage.class );\n" +
                "\n" +
                "            Message oldMessage = new Message();\n" +
                "            oldMessage.setMessage(\"old\");\n" +
                "            insert(oldMessage);\n" +
                "            don( oldMessage, NiceMessage.class );" +

                "            list.add(\"OK\");\n" +
                "    end";
        KnowledgeBase kb = getKnowledgeBaseFromString( source );
        TraitFactory.setMode( mode, kb );

        StatelessKnowledgeSession ksession = kb.newStatelessKnowledgeSession();

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.execute( CommandFactory.newFireAllRules() );

        assertEquals( 1, list.size() );
        assertTrue( list.contains( "OK" ) );

    }

    @Test
    public void testManyStatelessTriple() {
        manyTraitsStateless( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test
    public void testManyStatelessMap() {
        manyTraitsStateless( TraitFactory.VirtualPropertyMode.MAP );
    }





    public void aliasing( TraitFactory.VirtualPropertyMode mode ) {
        String drl = "package org.drools.traits\n" +
                "import org.drools.factmodel.traits.Traitable;\n" +
                "import org.drools.factmodel.traits.Alias;\n" +
                "" +
                "global java.util.List list;" +
                "" +
                "declare Person \n" +
                "  @Traitable \n" +
                "  name      : String  @key \n" +
                "  workPlace : String \n" +
                "  address   : String \n" +
                "end \n" +
                "" +
                "declare Student\n" +
                "  @kind(trait)" +
                // this alias fails, should revert to the hard field
                "  name      : String @Alias(\"nox1\") \n" +
                // this alias works
                "  school    : String  @Alias(\"workPlace\") \n" +
                // this alias fails, should revert to the soft field
                "  grade     : int @Alias(\"nox2\") \n" +
                // this is actually a soft field, because both mapping and aliasing should fail
                "  rank      : int @Alias(\"address\") \n" +
                "end \n" +
                "\n" +
                "rule \"create student\" \n" +
                "  when\n" +
                "  then\n" +
                "    Person p = new Person( \"davide\", \"UniBoh\", \"Floor84\" ); \n" +
                "    Student s = don( p, Student.class );\n" +
                "end\n" +
                "\n" +
                "rule \"print school\"\n" +
                "  when\n" +
                "    $student : Student( $school : school == \"UniBoh\",  $f : fields, fields[ \"school\" ] == \"UniBoh\" )\n" +
                "  then \n " +
                "    $student.setRank( 99 ); \n" +
                "    System.out.println( $student ); \n" +
                "    $f.put( \"school\", \"Skool\" ); \n" +

                "    list.add( $school );\n" +
                "    list.add( $f.get( \"school\" ) );\n" +
                "    list.add( $student.getSchool() );\n" +
                "    list.add( $f.keySet() );\n" +
                "    list.add( $f.entrySet() );\n" +
                "    list.add( $f.values() );\n" +
                "    list.add( $f.containsKey( \"school\" ) );\n" +
                "    list.add( $student.getRank() );\n" +
                "    list.add( $f.get( \"address\" ) );\n" +
                "end";

        StatefulKnowledgeSession ksession = getSessionFromString( drl );
        TraitFactory.setMode( mode, ksession.getKnowledgeBase() );

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        assertEquals( 9, list.size() );
        assertTrue( list.contains( "UniBoh" ) );
        assertTrue( list.contains( "Skool" ) );
        assertTrue( ( (Collection) list.get(3) ).containsAll( Arrays.asList( "workPlace", "name", "grade" ) ) );
        assertTrue( ( (Collection) list.get(5) ).containsAll( Arrays.asList( "davide", "Skool", 0 ) ) );
        assertTrue( list.contains( true ) );
        assertTrue( list.contains( "Floor84" ) );
        assertTrue( list.contains( 99 ) );

    }

    @Test
    public void testAliasingTriples() {
        aliasing( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test
    public void testAliasingMap() {
        aliasing( TraitFactory.VirtualPropertyMode.MAP );
    }







    public void traitLogicalRemoval( TraitFactory.VirtualPropertyMode mode ) {
        String drl = "package org.drools.trait.test;\n" +
                "\n" +
                "import org.drools.factmodel.traits.Traitable;\n" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +
                "declare trait Student\n" +
                "  age  : int\n" +
                "  name : String\n" +
                "end\n" +
                "\n" +
                "declare trait Worker\n" +
                "  wage  : int\n" +
                "  name : String\n" +
                "end\n" +
                "declare Person\n" +
                "  @Traitable\n" +
                "  name : String \n" +
                "end\n" +
                "\n" +
                "\n" +
                "rule \"Don Logical\"\n" +
                "when\n" +
                "  $s : String( this == \"trigger\" )\n" +
                "then\n" +
                "  Person p = new Person( \"john\" );\n" +
                "  insertLogical( p ); \n" +
                "  don( p, Student.class, true );\n" +
                "end\n" +
                " " +
                "rule \"Don Logical 2\"\n" +
                "when\n" +
                "  $s : String( this == \"trigger2\" )\n" +
                "  $p : Person( name == \"john\" )" +
                "then\n" +
                "  don( $p, Worker.class, true );\n" +
                "end";


        StatefulKnowledgeSession ksession = getSessionFromString(drl);
        TraitFactory.setMode( mode, ksession.getKnowledgeBase() );

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        FactHandle h = ksession.insert( "trigger" );
        ksession.fireAllRules();
        assertEquals( 4, ksession.getObjects().size() );

        ksession.retract( h );
        ksession.fireAllRules();

        assertEquals( 0, ksession.getObjects().size() );

        FactHandle h1 = ksession.insert( "trigger" );
        FactHandle h2 = ksession.insert( "trigger2" );
        ksession.fireAllRules();

        assertEquals( 6, ksession.getObjects().size() );

        ksession.retract( h2 );
        ksession.fireAllRules();

        assertEquals( 4, ksession.getObjects().size() );

        ksession.retract( h1 );
        ksession.fireAllRules();

        assertEquals( 0, ksession.getObjects().size() );

    }

    @Test
    public void testLogicalRemovalTriples() {
        traitLogicalRemoval( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test
    public void testLogicalRemovalMap() {
        traitLogicalRemoval( TraitFactory.VirtualPropertyMode.MAP );
    }



    @Test
    public void testTMSConsistencyWithNonTraitableBeans() {

        String s1 = "package org.drools.test;\n" +
                "import org.drools.Person; \n" +
                "import org.drools.factmodel.traits.Traitable; \n" +
                "" +
                "declare Person @Traitable end \n" +
                "" +
                "rule \"Init\"\n" +
                "when\n" +
                "then\n" +
                "  insertLogical( new Person( \"x\", 18 ) );\n" +
                "end\n" +
                "\n" +
                "declare trait Student\n" +
                "  age  : int\n" +
                "  name : String\n" +
                "end\n" +
                "\n" +
                "rule \"Trait\"\n" +
                "when\n" +
                "    $p : Person( )\n" +
                "then\n" +
                "    don( $p, Student.class, true );\n" +
                "end\n";



        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ByteArrayResource( s1.getBytes() ), ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.fireAllRules();

        FactHandle personHandle = ksession.getFactHandles( new ClassObjectFilter( Person.class ) ).iterator().next();
        InternalFactHandle h = ((InternalFactHandle) personHandle);
        ObjectTypeConfigurationRegistry reg = ((InternalWorkingMemoryEntryPoint) h.getEntryPoint()).getObjectTypeConfigurationRegistry();
        ObjectTypeConf conf = reg.getObjectTypeConf( ((InternalWorkingMemoryEntryPoint) h.getEntryPoint()).getEntryPoint(), ((InternalFactHandle) personHandle).getObject() );
        assertTrue( conf.isTMSEnabled() );

        ksession.dispose();
    }


    @Test
    public void testInternalComponentsMap(  ) {
          String source = "org/drools/factmodel/traits/testTraitDon.drl";

          KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
          Resource res = ResourceFactory.newClassPathResource( source );
          assertNotNull( res );
          kbuilder.add( res,
                        ResourceType.DRL );
          if (kbuilder.hasErrors()) {
              fail( kbuilder.getErrors().toString() );
          }
          KnowledgeBase kb = KnowledgeBaseFactory.newKnowledgeBase();
          kb.addKnowledgePackages( kbuilder.getKnowledgePackages() );

          TraitFactory.setMode( TraitFactory.VirtualPropertyMode.MAP, kb );
          TraitFactory tFactory = ((AbstractRuleBase) ((KnowledgeBaseImpl) kb).getRuleBase()).getConfiguration().getComponentFactory().getTraitFactory();


          try {
              FactType impClass = kb.getFactType( "org.test",
                                                  "Imp" );
              TraitableBean imp = (TraitableBean) impClass.newInstance();
              FactType traitClass = kb.getFactType( "org.test",
                                                    "Student" );
              Class trait = traitClass.getFactClass();
              TraitProxy proxy = (TraitProxy) tFactory.getProxy( imp,
                                                                     trait );
              Object proxyFields = proxy.getFields();
              Object coreTraits = imp._getTraitMap();
              Object coreProperties = imp._getDynamicProperties();

              assertTrue( proxy.getObject() instanceof TraitableBean );

              assertNotNull( proxyFields );
              assertNotNull( coreTraits );
              assertNotNull( coreProperties );

              assertTrue( proxyFields instanceof MapWrapper );
              assertTrue( coreTraits instanceof TraitTypeMap);
              assertTrue( coreProperties instanceof HashMap);


              StudentProxy2 sp2 = new StudentProxy2( new Imp2(), null );
              System.out.println( sp2.toString() );

          } catch ( Exception e ) {
              e.printStackTrace();
              fail( e.getMessage() );
          }
    }


    @Test
    public void testInternalComponentsTriple(  ) {
        String source = "org/drools/factmodel/traits/testTraitDon.drl";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Resource res = ResourceFactory.newClassPathResource( source );
        assertNotNull( res );
        kbuilder.add( res,
                ResourceType.DRL );
        if (kbuilder.hasErrors()) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kb = KnowledgeBaseFactory.newKnowledgeBase();
        kb.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        TraitFactory.setMode( TraitFactory.VirtualPropertyMode.TRIPLES, kb );
        TraitFactory tFactory = ((AbstractRuleBase) ((KnowledgeBaseImpl) kb).getRuleBase()).getConfiguration().getComponentFactory().getTraitFactory();


        try {
            FactType impClass = kb.getFactType( "org.test",
                    "Imp" );
            TraitableBean imp = (TraitableBean) impClass.newInstance();
            FactType traitClass = kb.getFactType( "org.test",
                    "Student" );
            Class trait = traitClass.getFactClass();
            TraitProxy proxy = (TraitProxy) tFactory.getProxy( imp,
                    trait );
            Object proxyFields = proxy.getFields();
            Object coreTraits = imp._getTraitMap();
            Object coreProperties = imp._getDynamicProperties();

            assertTrue( proxy.getObject() instanceof TraitableBean );

            assertNotNull( proxyFields );
            assertNotNull( coreTraits );
            assertNotNull( coreProperties );

            assertEquals(proxyFields.getClass().getName(), "org.test.Student.org.test.Imp_ProxyWrapper");

            assertTrue(proxyFields instanceof TripleBasedStruct);
            assertTrue( coreTraits instanceof TraitTypeMap);
            assertTrue( coreProperties instanceof TripleBasedBean );


        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );

        }
    }



    public static class TBean {
        private String fld;
        public String getFld() { return fld; }
        public void setFld( String fld ) { this.fld = fld; }
        public TBean( String fld ) { this.fld = fld; }
    }



    public void traitsLegacyWrapperCoherence( TraitFactory.VirtualPropertyMode mode ) {
        String str = "package org.drools.trait.test; \n" +
                "global java.util.List list; \n" +
                "import org.drools.factmodel.traits.Traitable;\n" +
                "import org.drools.factmodel.traits.TraitTest.TBean;\n" +
                "" +                "" +
                "declare TBean \n" +
                "@Traitable \n" +
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

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        TraitFactory.setMode( mode, ksession.getKnowledgeBase() );
        List<?> list = new ArrayList<Object>();

        ksession.setGlobal("list",
                list);

        ksession.fireAllRules();


        Collection yOld = ksession.getObjects();
        assertEquals( 3, yOld.size() );

        TraitableBean coreOld = null;
        for ( Object o : yOld ) {
            if ( o instanceof TraitableBean ) {
                coreOld = (TraitableBean) o;
                break;
            }
        }
        assertNotNull( coreOld );

        assertSame( TBean.class, coreOld.getClass().getSuperclass() );

        assertEquals( "abc", ((TBean) coreOld).getFld() );
        assertEquals( 1, coreOld._getDynamicProperties().size() );
        assertEquals( 2, coreOld._getTraitMap().size() );
    }


    @Test
    public void testTraitsBeanWrapperDataStructuresTriples() {
        traitsLegacyWrapperCoherence( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test
    public void testTraitsBeanWrapperDataStructuresMap() {
        traitsLegacyWrapperCoherence( TraitFactory.VirtualPropertyMode.MAP );
    }








    public void traitRedundancy( TraitFactory.VirtualPropertyMode mode ) {
        String str = "package org.drools.factmodel.traits; \n" +
                "global java.util.List list; \n" +
                "" +
                "declare trait IStudent end \n" +
                "" +
                "declare org.drools.factmodel.traits.IPerson @typesafe(false) end \n" +
                "" +
                "rule \"Students\" \n" +
                "salience -10" +
                "when \n" +
                "   $s : IStudent() \n" +
                "then \n" +
                "   System.out.println( \"Student in \" + $s ); \n" +
                "end \n" +
                "" +
                "rule \"Don\" \n" +
                "no-loop  \n" +
                "when \n" +
                "  $p : IPerson( age < 30 ) \n" +
                "then \n" +
                "   System.out.println( \"Candidate student \" + $p ); \n" +
                "   don( $p, IStudent.class );\n" +
                "end \n" +
                "" +
                "rule \"Check\" \n" +
                "no-loop \n" +
                "when \n" +
                "  $p : IPerson( this isA IStudent ) \n" +
                "then \n" +
                "   System.out.println( \"Known student \" + $p ); " +
                "   modify ($p) { setAge( 37 ); } \n" +
                "   shed( $p, IStudent.class );\n" +
                "end \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                ResourceType.DRL );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        TraitFactory.setMode( mode, ksession.getKnowledgeBase() );
        List<?> list = new ArrayList<Object>();

        ksession.setGlobal("list",
                list);

        ksession.insert( new StudentImpl( "skool", "john", 27 ) );


        assertEquals( 3, ksession.fireAllRules() );

        for ( Object o : ksession.getObjects() ) {
            System.err.println( o );
        }

    }


    @Test
    public void testTraitRedundancyTriples() {
        traitRedundancy(TraitFactory.VirtualPropertyMode.TRIPLES);
    }

    @Test
    public void testTraitRedundancyMap() {
        traitRedundancy(TraitFactory.VirtualPropertyMode.MAP);
    }




    public void traitSimpleTypes( TraitFactory.VirtualPropertyMode mode ) {

        String s1 = "package org.drools.factmodel.traits;\n" +
                "\n" +
                "import org.drools.factmodel.traits.Traitable;\n" +
                "" +
                "declare trait PassMark\n" +
                "end\n" +
                "\n" +
                "declare ExamMark \n" +
                "@Traitable\n" +
                "value : long \n" +
                "end\n" +
                "" +
                "rule \"testTraitFieldTypePrimitive\"\n" +
                "when\n" +
                "    $mark : ExamMark()\n" +
                "then\n" +
                "    don($mark, PassMark.class);\n" +
                "end\n" +
                "" +
                "rule \"Init\" when then insert( new ExamMark() ); end \n";



        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ByteArrayResource( s1.getBytes() ), ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        TraitFactory.setMode( mode, kbase );

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.fireAllRules();


    }

    @Test
    public void testTraitWithSimpleTypesTriples() {
        traitSimpleTypes( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test
    public void testTraitWithSimpleTypesMap() {
        traitSimpleTypes( TraitFactory.VirtualPropertyMode.MAP );
    }



    @Test
    public void testTraitEncoding() {
        String s1 = "package org.drools.factmodel.traits;\n" +
                "declare trait A end\n" +
                "declare trait B extends A end\n" +
                "declare trait C extends A end\n" +
                "declare trait D extends A end\n" +
                "declare trait E extends B end\n" +
                "declare trait F extends C end\n" +
                "declare trait G extends D end\n" +
                "declare trait H extends D end\n" +
                "declare trait I extends E end\n" +
                "declare trait J extends F end\n" +
                "declare trait K extends G, H end\n" +
                "declare trait L extends G, H end\n" +
                "declare trait M extends I, J end\n" +
                "declare trait N extends K, L end\n" +
                "" +
                "rule \"donOneThing\"\n" +
                "when\n" +
                "    $x : Entity()\n" +
                "then\n" +
                "    don( $x, A.class );\n" +
                "end\n" +
                "" +
                "rule \"donManyThing\"\n" +
                "when\n" +
                "    String( this == \"y\" ) \n" +
                "    $x : Entity()\n" +
                "then\n" +
                "    don( $x, B.class );\n" +
                "    don( $x, D.class );\n" +
                "    don( $x, F.class );\n" +
                "    don( $x, E.class );\n" +
                "    don( $x, I.class );\n" +
                "    don( $x, K.class );\n" +
                "    don( $x, J.class );\n" +
                "    don( $x, C.class );\n" +
                "    don( $x, H.class );\n" +
                "    don( $x, G.class );\n" +
                "    don( $x, L.class );\n" +
                "    don( $x, M.class );\n" +
                "    don( $x, N.class );\n" +
                "end\n"
                ;

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ByteArrayResource( s1.getBytes() ), ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        TraitFactory.setMode( TraitFactory.VirtualPropertyMode.MAP, kbase ); // not relevant

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        TraitRegistry tr = ((ReteooRuleBase) ((KnowledgeBaseImpl) kbase).getRuleBase()).getConfiguration().getComponentFactory().getTraitRegistry();
        System.out.println( tr.getHierarchy() );


        Entity ent = new Entity( "x" );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.insert( ent );
        ksession.fireAllRules();

        assertEquals( 1, ent.getMostSpecificTraits().size() );

        ksession.insert( "y" );
        ksession.fireAllRules();

        System.out.println( ent.getMostSpecificTraits() );
        assertEquals( 2, ent.getMostSpecificTraits().size() );

    }


    @Test
    public void testTraitModifyCore() {
        String s1 = "package test;\n" +
                "import org.drools.factmodel.traits.*;\n" +
                "" +
                "declare trait Student name : String end\n" +
                "declare trait Worker name : String end\n" +
                "declare trait StudentWorker extends Student, Worker name : String end\n" +
                "declare trait Assistant extends Student, Worker name : String end\n" +
                "declare Person @Traitable name : String end\n" +
                "" +
                "rule \"Init\" \n" +
                "when \n" +
                "then \n" +
                "  Person p = new Person( \"john\" ); \n" +
                "  insert( p ); \n" +
                "end \n" +
                "" +
                "rule \"Don\" \n" +
                "no-loop\n " +
                "when \n" +
                "  $p : Person( name == \"john\" ) \n" +
                "then \n" +
                "  System.out.println( $p ); \n" +
                "" +
                "  System.out.println( \" ----------------------------------------------------------------------------------- Don student\" ); \n" +
                "  don( $p, Student.class ); \n" +
                "  System.out.println( \" ----------------------------------------------------------------------------------- Don worker\" ); \n" +
                "  don( $p, Worker.class ); \n" +
                "  System.out.println( \" ----------------------------------------------------------------------------------- Don studentworker\" ); \n" +
                "  don( $p, StudentWorker.class ); \n" +
                "  System.out.println( \" ----------------------------------------------------------------------------------- Don assistant\" ); \n" +
                "  don( $p, Assistant.class ); \n" +
                "end \n" +
                "" +
                "rule \"Log S\" \n" +
                "when \n" +
                "  $t : Student() \n" +
                "then \n" +
                "  System.out.println( \"Student >> \" +  $t ); \n" +
                "end \n" +
                "rule \"Log W\" \n" +
                "when \n" +
                "  $t : Worker() \n" +
                "then \n" +
                "  System.out.println( \"Worker >> \" + $t ); \n" +
                "end \n" +
                "rule \"Log SW\" \n" +
                "when \n" +
                "  $t : StudentWorker() \n" +
                "then \n" +
                "  System.out.println( \"StudentWorker >> \" + $t ); \n" +
                "end \n" +
                "rule \"Log RA\" \n" +
                "when \n" +
                "  $t : Assistant() \n" +
                "then \n" +
                "  System.out.println( \"Assistant >> \" + $t ); \n" +
                "end \n" +
                "" +
                "rule \"Mod\" \n" +
                "salience -10 \n" +
                "when \n" +
                "  $p : Person( name == \"john\" ) \n" +
                "then \n" +
                "   System.out.println( \"-----------------------------\" );\n" +
                "   modify ( $p ) { setName( \"alan\" ); } " +
                "end \n" +
                "";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ByteArrayResource( s1.getBytes() ), ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        TraitFactory.setMode( TraitFactory.VirtualPropertyMode.MAP, kbase ); // not relevant

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        int k = ksession.fireAllRules();

        assertEquals( 13, k );

    }




    @Test
    public void testTraitModifyCore2() {
        String s1 = "package test;\n" +
                "import org.drools.factmodel.traits.*;\n" +
                "" +
                "declare trait Student @propertyReactive name : String end\n" +
                "declare trait Worker @propertyReactive name : String end\n" +
                "declare trait StudentWorker extends Student, Worker @propertyReactive name : String end\n" +
                "declare trait StudentWorker2 extends StudentWorker @propertyReactive name : String end\n" +
                "declare trait Assistant extends Student, Worker @propertyReactive name : String end\n" +
                "declare Person @Traitable @propertyReactive name : String end\n" +
                "" +
                "rule \"Init\" \n" +
                "when \n" +
                "then \n" +
                "  Person p = new Person( \"john\" ); \n" +
                "  insert( p ); \n" +
                "end \n" +
                "" +
                "rule \"Don\" \n" +
                "when \n" +
                "  $p : Person( name == \"john\" ) \n" +
                "then \n" +
                "  System.out.println( \">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> DON WORKER \" + $p  ); \n" +
                "  don( $p, Worker.class ); \n" +
                "  System.out.println( \">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> DON STUDWORKER \" + $p ); \n" +
                "  don( $p, StudentWorker2.class ); \n" +
                "  System.out.println( \">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> DON ASSISTANT \" + $p ); \n" +
                "  don( $p, Assistant.class ); \n" +
                "end \n" +
                "" +
                "rule \"Log S\" \n" +
                "when \n" +
                "  $t : Student() @watch( name ) \n" +
                "then \n" +
                "  System.out.println( \"@@Student >> \" +  $t ); \n" +
                "end \n" +
                "rule \"Log W\" \n" +
                "when \n" +
                "  $t : Worker() @watch( name ) \n" +
                "then \n" +
                "  System.out.println( \"@@Worker >> \" + $t ); \n" +
                "end \n" +
                "rule \"Log SW\" \n" +
                "when \n" +
                "  $t : StudentWorker() @watch( name ) \n" +
                "then \n" +
                "  System.out.println( \"@@StudentWorker >> \" + $t ); \n" +
                "end \n" +
                "rule \"Log RA\" \n" +
                "when \n" +
                "  $t : Assistant() @watch( name ) \n" +
                "then \n" +
                "  System.out.println( \"@@Assistant >> \" + $t ); \n" +
                "end \n" +
                "rule \"Log Px\" \n" +
                "salience -1 \n" +
                "when \n" +
                "  $p : Person() @watch( name ) \n" +
                "then \n" +
                "  System.out.println( \"Poor Core Person >> \" + $p ); \n" +
                "end \n" +
                "" +
                "rule \"Mod\" \n" +
                "salience -10 \n" +
                "when \n" +
                "  String( this == \"go\" ) \n" +
                "  $p : Student( name == \"john\" ) \n" +
                "then \n" +
                "  System.out.println( \" ------------------------------------------------------------------------------ \" + $p ); \n" +
                "  modify ( $p ) { setName( \"alan\" ); } " +
                "end \n" +
                "";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ByteArrayResource( s1.getBytes() ), ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        TraitFactory.setMode( TraitFactory.VirtualPropertyMode.MAP, kbase ); // not relevant

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        int k = ksession.fireAllRules();

        assertEquals( 7, k );

        ksession.insert( "go" );
        k = ksession.fireAllRules();

        assertEquals( 6, k );

    }

    @Test
    public void testTraitModifyCore2a() {
        String s1 = "package test;\n" +
                    "import org.drools.factmodel.traits.*;\n" +
                    "global java.util.List list; \n" +
                    "" +
                    "declare trait Student @propertyReactive name : String end\n" +
                    "declare trait Worker @propertyReactive name : String end\n" +
                    "declare trait StudentWorker extends Student, Worker @propertyReactive name : String end\n" +
                    "declare trait Assistant extends Student, Worker @propertyReactive name : String end\n" +
                    "declare Person @Traitable @propertyReactive name : String end\n" +
                    "" +
                    "rule \"Init\" \n" +
                    "when \n" +
                    "then \n" +
                    "  Person p = new Person( \"john\" ); \n" +
                    "  insert( p ); \n" +
                    "end \n" +
                    "" +
                    "rule \"Don\" \n" +
                    "when \n" +
                    "  $p : Person( name == \"john\" ) \n" +
                    "then \n" +
                    "  System.out.println( \">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> DON WORKER \" + $p  ); \n" +
                    "  don( $p, Worker.class ); \n" +
                    "  System.out.println( \">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> DON STUDWORKER \" + $p ); \n" +
                    "  don( $p, StudentWorker.class ); \n" +
                    "end \n" +
                    "" +
                    "rule \"Log W\" \n" +
                    "when \n" +
                    "  $t : Worker( this isA StudentWorker ) @watch( name ) \n" +
                    "then \n" +
                    "  System.out.println( \"@@Worker >> \" + $t ); \n" +
                    "  list.add( true ); \n" +
                    "end \n" +
                    "rule \"Log SW\" \n" +
                    "when \n" +
                    "  $t : StudentWorker() @watch( name ) \n" +
                    "then \n" +
                    "  System.out.println( \"@@StudentWorker >> \" + $t ); \n" +
                    "end \n" +
                    "";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ByteArrayResource( s1.getBytes() ), ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        TraitFactory.setMode( TraitFactory.VirtualPropertyMode.MAP, kbase ); // not relevant
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ArrayList list = new ArrayList(  );
        ksession.setGlobal( "list", list );
        int k = ksession.fireAllRules();
        assertTrue( list.contains( true ) );
        assertEquals( 1, list.size() );
    }




    @Test
    public void testTraitModifyCore3() {
        String s1 = "package test;\n" +
                "import org.drools.factmodel.traits.*;\n" +
                "global java.util.List list; \n" +
                "" +
                "declare trait A id : int end\n" +
                "declare trait B extends A end\n" +
                "declare trait C extends A end\n" +
                "declare trait D extends A end\n" +
                "declare trait E extends B end\n" +
                "declare trait F extends C end\n" +
                "declare trait G extends D end\n" +
                "declare trait H extends D end\n" +
                "declare trait I extends E end\n" +
                "declare trait J extends F end\n" +
                "declare trait K extends G, H end\n" +
                "declare trait L extends G, H end\n" +
                "declare trait M extends I, J end\n" +
                "declare trait N extends K, L end\n" +
                "" +
                "declare Core @Traitable id : int = 0 end \n" +
                "" +
                "rule \"Init\" when \n" +
                "then \n" +
                "   insert( new Core() );" +
                "end \n" +
                "" +
                "rule \"donManyThing\"\n" +
                "when\n" +
                "    $x : Core( id == 0 )\n" +
                "then\n" +
                "    don( $x, A.class );\n" +
                "    don( $x, B.class );\n" +
                "    don( $x, D.class );\n" +
                "    don( $x, F.class );\n" +
                "    don( $x, E.class );\n" +
                "    don( $x, I.class );\n" +
                "    don( $x, K.class );\n" +
                "    don( $x, J.class );\n" +
                "    don( $x, C.class );\n" +
                "    don( $x, H.class );\n" +
                "    don( $x, G.class );\n" +
                "    don( $x, L.class );\n" +
                "    don( $x, M.class );\n" +
                "    don( $x, N.class );\n" +
                "end\n" +
                "\n" +
                "\n" +
                "\n" +
                "rule \"Log A\" when $x : A( id == 1 ) then System.out.println( \"A >> \" +  $x ); list.add( 1 ); end \n" +
                "rule \"Log B\" when $x : B( id == 1 ) then System.out.println( \"B >> \" +  $x ); list.add( 2 ); end \n" +
                "rule \"Log C\" when $x : C( id == 1 ) then System.out.println( \"C >> \" +  $x ); list.add( 3 ); end \n" +
                "rule \"Log D\" when $x : D( id == 1 ) then System.out.println( \"D >> \" +  $x ); list.add( 4 ); end \n" +
                "rule \"Log E\" when $x : E( id == 1 ) then System.out.println( \"E >> \" +  $x ); list.add( 5 ); end \n" +
                "rule \"Log F\" when $x : F( id == 1 ) then System.out.println( \"F >> \" +  $x ); list.add( 6 ); end \n" +
                "rule \"Log G\" when $x : G( id == 1 ) then System.out.println( \"G >> \" +  $x ); list.add( 7 ); end \n" +
                "rule \"Log H\" when $x : H( id == 1 ) then System.out.println( \"H >> \" +  $x ); list.add( 8 ); end \n" +
                "rule \"Log I\" when $x : I( id == 1 ) then System.out.println( \"I >> \" +  $x ); list.add( 9 ); end \n" +
                "rule \"Log J\" when $x : J( id == 1 ) then System.out.println( \"J >> \" +  $x ); list.add( 10 ); end \n" +
                "rule \"Log K\" when $x : K( id == 1 ) then System.out.println( \"K >> \" +  $x ); list.add( 11 ); end \n" +
                "rule \"Log L\" when $x : L( id == 1 ) then System.out.println( \"L >> \" +  $x ); list.add( 12 ); end \n" +
                "rule \"Log M\" when $x : M( id == 1 ) then System.out.println( \"M >> \" +  $x ); list.add( 13 ); end \n" +
                "rule \"Log N\" when $x : N( id == 1 ) then System.out.println( \"N >> \" +  $x ); list.add( 14 ); end \n" +
                "" +
                "rule \"Log Core\" when $x : Core( $id : id ) then System.out.println( \"Core >>>>>> \" +  $x ); end \n" +
                "" +
                "rule \"Mod\" \n" +
                "salience -10 \n" +
                "when \n" +
                "  String( this == \"go\" ) \n" +
                "  $x : Core( id == 0 ) \n" +
                "then \n" +
                "  System.out.println( \" ------------------------------------------------------------------------------ \" ); \n" +
                "  modify ( $x ) { setId( 1 ); }" +
                "end \n" +
                "";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ByteArrayResource( s1.getBytes() ), ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        TraitFactory.setMode( TraitFactory.VirtualPropertyMode.MAP, kbase ); // not relevant

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        List list = new ArrayList();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        ksession.insert( "go" );
        ksession.fireAllRules();

        assertEquals( 14, list.size() );
        for ( int j = 1; j <= 14; j++ ) {
            assertTrue( list.contains( j ) );
        }


    }







    @Test
    public void testTraitModifyCoreWithPropertyReactivity() {
        String s1 = "package test;\n" +
                "import org.drools.factmodel.traits.*;\n" +
                "global java.util.List list;\n" +
                "" +
                "declare trait Student @propertyReactive " +
                "   name : String " +
                "   age : int " +
                "   grades : double " +
                "   school : String " +
                "   aaa : boolean " +
                "end\n" +
                "declare trait Worker @propertyReactive " +
                "   name : String " +
                "   wage : double " +
                "end\n" +
                "declare trait StudentWorker extends Student, Worker @propertyReactive " +
                "   hours : int " +
                "end\n" +
                "declare trait Assistant extends Student, Worker @propertyReactive " +
                "   address : String " +
                "end\n" +
                "declare Person @propertyReactive @Traitable " +
                "   wage : double " +
                "   name : String " +
                "   age : int  " +
                "end\n" +
                "" +
                "rule \"Init\" \n" +
                "when \n" +
                "then \n" +
                "  Person p = new Person( 109.99, \"john\", 18 ); \n" +
                "  insert( p ); \n" +
                "end \n" +
                "" +
                "rule \"Don\" \n" +
                "when \n" +
                "  $p : Person( name == \"john\" ) \n" +
                "then \n" +
                "  System.out.println( $p ); \n" +
                "  don( $p, StudentWorker.class ); \n" +
                "  don( $p, Assistant.class ); \n" +
                "end \n" +
                "" +
                "rule \"Log S\" \n" +
                "when \n" +
                "  $t : Student( age == 44 ) \n" +
                "then \n" +
                "  list.add( 1 );\n " +
                "  System.out.println( \"Student >> \" +  $t ); \n" +
                "end \n" +
                "rule \"Log W\" \n" +
                "when \n" +
                "  $t : Worker( name == \"alan\" ) \n" +
                "then \n" +
                "  list.add( 2 );\n " +
                "  System.out.println( \"Worker >> \" + $t ); \n" +
                "end \n" +
                "rule \"Log SW\" \n" +
                "when \n" +
                "  $t : StudentWorker( age == 44 ) \n" +
                "then \n" +
                "  list.add( 3 );\n " +
                "  System.out.println( \"StudentWorker >> \" + $t ); \n" +
                "end \n" +
                "rule \"Log Pers\" \n" +
                "when \n" +
                "  $t : Person( age == 44 ) \n" +
                "then \n" +
                "  list.add( 4 );\n " +
                "  System.out.println( \"Person >> \" + $t ); \n" +
                "end \n" +
                "" +
                "rule \"Mod\" \n" +
                "salience -10 \n" +
                "when \n" +
                "  String( this == \"go\" ) \n" +
                "  $p : Student( name == \"john\" ) \n" +
                "then \n" +
                "  System.out.println( \" ------------------------------------------------------------------------------ \" + $p ); \n" +
                "  modify ( $p ) { setSchool( \"myschool\" ), setAge( 44 ), setName( \"alan\" ); } " +
                "end \n" +
                "";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ByteArrayResource( s1.getBytes() ), ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        TraitFactory.setMode( TraitFactory.VirtualPropertyMode.MAP, kbase ); // not relevant

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        List<Integer> list = new ArrayList<Integer>();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.setGlobal( "list", list );
        int k = ksession.fireAllRules();

        ksession.insert( "go" );
        k = ksession.fireAllRules();

        assertEquals( 5, k );

        assertEquals( 4, list.size() );
        assertTrue( list.contains( 1 ) );
        assertTrue( list.contains( 2 ) );
        assertTrue( list.contains( 3 ) );
        assertTrue( list.contains( 4 ) );

    }










    @Test
    public void testTraitActualTypeCodeWithEntities() {
        testTraitActualTypeCodeWithEntities( "ent", TraitFactory.VirtualPropertyMode.MAP );
    }

    @Test
    public void testTraitActualTypeCodeWithCoreMap() {
        testTraitActualTypeCodeWithEntities( "kor", TraitFactory.VirtualPropertyMode.MAP );
    }

    @Test
    public void testTraitActualTypeCodeWithCoreTriples() {
        testTraitActualTypeCodeWithEntities( "kor", TraitFactory.VirtualPropertyMode.TRIPLES );
    }


    void testTraitActualTypeCodeWithEntities( String trig, TraitFactory.VirtualPropertyMode mode ) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ClassPathResource( "org/drools/factmodel/traits/testComplexDonShed.drl" ), ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        TraitFactory.setMode( mode, kbase ); // not relevant

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        HierarchyEncoder hier = ((ReteooRuleBase) ((KnowledgeBaseImpl) ksession.getKnowledgeBase()).getRuleBase()).getConfiguration().getComponentFactory().getTraitRegistry().getHierarchy();
        System.out.println( hier );

        ksession.insert( trig );
        ksession.fireAllRules();

        TraitableBean ent = (TraitableBean) ksession.getGlobal( "core" );

        assertEquals( CodedHierarchyImpl.stringToBitSet( "1" ), ent.getCurrentTypeCode( ) );

        ksession.insert( "b" );
        ksession.fireAllRules();
        assertEquals( CodedHierarchyImpl.stringToBitSet( "11" ), ent.getCurrentTypeCode( ) );

        ksession.insert( "c" );
        ksession.fireAllRules();
        assertEquals( CodedHierarchyImpl.stringToBitSet( "1011" ), ent.getCurrentTypeCode( ) );

        ksession.insert( "e" );
        ksession.fireAllRules();
        assertEquals( CodedHierarchyImpl.stringToBitSet( "11011" ), ent.getCurrentTypeCode( ) );

        ksession.insert( "-c" );
        ksession.fireAllRules();
        assertEquals( CodedHierarchyImpl.stringToBitSet( "11" ), ent.getCurrentTypeCode( ) );

        ksession.insert( "dg" );
        ksession.fireAllRules();
        assertEquals( CodedHierarchyImpl.stringToBitSet( "111111" ), ent.getCurrentTypeCode( ) );

        ksession.insert( "-f" );
        ksession.fireAllRules();
        assertEquals( CodedHierarchyImpl.stringToBitSet( "111" ), ent.getCurrentTypeCode( ) );

    }


    public static interface IntfParent {}

    @Test
    public void testTraitEncodeExtendingNonTrait() {

        String s1 = "package test;\n" +
                "import org.drools.factmodel.traits.TraitTest.IntfParent;\n" +
                "" +
                "declare IntfParent end\n" +
                "" +
                "declare trait TChild extends IntfParent end \n" +
                "";

        String s2 = "package test; declare trait SomeThing end \n";


        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ByteArrayResource( s2.getBytes() ), ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        TraitFactory.setMode( TraitFactory.VirtualPropertyMode.MAP, kbase ); // not relevant

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KnowledgeBuilder kbuilder2 = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder2.add( new ByteArrayResource( s1.getBytes() ), ResourceType.DRL );
        if ( kbuilder2.hasErrors() ) {
            fail( kbuilder2.getErrors().toString() );
        }

        kbase.addKnowledgePackages( kbuilder2.getKnowledgePackages() );

    }





    public void isAWithBackChaining( TraitFactory.VirtualPropertyMode mode ) {

        String source = "org/drools/factmodel/traits/testTraitIsAWithBC.drl";
        StatefulKnowledgeSession ksession = getSession( source );
        TraitFactory.setMode( mode, ksession.getKnowledgeBase() );

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        ksession.insert( "Como" );

        ksession.fireAllRules();

        assertTrue( list.contains( "Italy" ) );
    }

    @Test
    public void isAWithBackChainingTriples() {
        isAWithBackChaining( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test
    public void isAWithBackChainingMap() {
        isAWithBackChaining( TraitFactory.VirtualPropertyMode.MAP );
    }



    @Test
    public void donMapTest() {
        String source = "package org.drools.traits.test; \n" +
                "import java.util.*\n;" +
                "import org.drools.factmodel.traits.Traitable;\n" +
                "" +
                "declare org.drools.factmodel.MapCore end \n" +
                "" +
                "global List list; \n" +
                "" +
                "declare HashMap @Traitable end \n" +
                "" +
                "declare trait PersonMap" +
                "@propertyReactive \n" +
                "   name : String \n" +
                "   age  : int \n" +
                "   height : Double \n" +
                "end\n" +
                "" +
                "" +
                "rule Don \n" +
                "when \n" +
                "  $m : Map( this[ \"age\"] == 18 ) " +
                "then \n" +
                "   don( $m, PersonMap.class );\n" +
                "end \n" +
                "" +
                "rule Log \n" +
                "when \n" +
                "   $p : PersonMap( name == \"john\", age > 10 ) \n" +
                "then \n" +
                "   System.out.println( $p ); \n" +
                "   modify ( $p ) { \n" +
                "       setHeight( 184.0 ); \n" +
                "   }" +
                "   System.out.println( $p ); " +
                "end \n";

        StatefulKnowledgeSession ksession = loadKnowledgeBaseFromString( source ).newStatefulKnowledgeSession();
        TraitFactory.setMode( TraitFactory.VirtualPropertyMode.MAP, ksession.getKnowledgeBase() );

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        Map map = new HashMap();
        map.put( "name", "john" );
        map.put( "age", 18 );

        ksession.insert( map );
        ksession.fireAllRules();

        assertTrue( map.containsKey( "height" ) );
        assertEquals( map.get( "height"), 184.0 );

    }




    @Test
    public void testIsAEvaluatorOnClassification( ) {
        String source = "package t.x \n" +
                "\n" +
                "global java.util.List list; \n" +
                "import org.drools.factmodel.traits.Thing\n" +
                "import org.drools.factmodel.traits.Entity\n" +
                "\n" +
                "declare t.x.D\n" +
                "    @propertyReactive\n" +
                "    @kind( TRAIT )\n" +
                "\n" +
                "end\n" +
                "" +
                "declare t.x.E\n" +
                "    @propertyReactive\n" +
                "    @kind( TRAIT )\n" +
                "\n" +
                "end\n" +
                "" +
                "rule Init when\n" +
                "then\n" +
                "   Entity o = new Entity();\n" +
                "   insert(o);\n" +
                "   don( o, D.class ); \n" +
                "end\n" +
                "" +
                "rule Don when\n" +
                " $o : Entity() \n" +
                "then \n" +
                "end \n" +
                "" +
                "rule \"Rule 0 >> http://t/x#D\"\n" +
                "when\n" +
                "   $t : org.drools.factmodel.traits.Thing( $c : core, top == true, this not isA t.x.E.class, this isA t.x.D.class ) " +
                "then\n" +
                "   list.add( \"E\" ); \n" +
                "   don( $t, E.class, true ); \n" +
                "end\n" +
                "" +
                "rule React \n" +
                "when E() then \n" +
                "   list.add( \"X\" ); \n" +
                "end \n"
                ;

        StatefulKnowledgeSession ks = getSessionFromString( source );
        TraitFactory.setMode( TraitFactory.VirtualPropertyMode.TRIPLES, ks.getKnowledgeBase() );

        List list = new ArrayList();
        ks.setGlobal( "list", list );
        ks.fireAllRules();

        assertEquals( 2, list.size() );
        assertTrue( list.contains( "E" ) );
        assertTrue( list.contains( "X" ) );

//        for ( Object o : ks.getObjects() ) {
//            System.out.println( o );
//        }
    }



    @Test
    public void testShedWithTMS( ) {
        String source = "package t.x \n" +
                "\n" +
                "global java.util.List list; \n" +
                "import org.drools.factmodel.traits.Thing\n" +
                "import org.drools.factmodel.traits.Entity\n" +
                "\n" +
                "declare t.x.D\n" +
                "    @propertyReactive\n" +
                "    @kind( TRAIT )\n" +
                "\n" +
                "end\n" +
                "" +
                "declare t.x.E\n" +
                "    @propertyReactive\n" +
                "    @kind( TRAIT )\n" +
                "\n" +
                "end\n" +
                "" +
                "rule Init when\n" +
                "then\n" +
                "   Entity o = new Entity();\n" +
                "   insert(o);\n" +
                "   don( o, D.class ); \n" +
                "end\n" +
                "" +
                "rule Don when\n" +
                " $o : Entity() \n" +
                "then \n" +
                "end \n" +
                "" +
                "rule \"Rule 0 >> http://t/x#D\"\n" +
                "when\n" +
                "   $t : org.drools.factmodel.traits.Thing( $c : core, top == true, this not isA t.x.E.class, this isA t.x.D.class ) " +
                "then\n" +
                "   list.add( \"E\" ); \n" +
                "   don( $t, E.class, true ); \n" +
                "end\n" +
                "" +
                "rule React \n" +
                "when $x : E() then \n" +
                "   list.add( \"X\" ); \n" +
                "end \n" +
                "" +
                "rule Shed \n" +
                "when \n" +
                "   $s : String() \n" +
                "   $d : Entity() \n" +
                "then \n" +
                "   retract( $s ); \n" +
                "   shed( $d, D.class );\n" +
                "end \n" +
                ""
                ;

        StatefulKnowledgeSession ks = getSessionFromString( source );
        TraitFactory.setMode( TraitFactory.VirtualPropertyMode.TRIPLES, ks.getKnowledgeBase() );

        List list = new ArrayList();
        ks.setGlobal( "list", list );
        ks.fireAllRules();

        assertEquals( 2, list.size() );
        assertTrue( list.contains( "E" ) );
        assertTrue( list.contains( "X" ) );

        ks.insert( "shed" );
        ks.fireAllRules();

        for ( Object o : ks.getObjects() ) {
            System.out.println( o );
        }
        assertEquals( 2, ks.getObjects().size() );

    }



    @Test
    public void testTraitInitializationTriples() {
        testTraitInitialization( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test
    public void testTraitInitializationMap() {
        testTraitInitialization( TraitFactory.VirtualPropertyMode.MAP );
    }

    public void testTraitInitialization( TraitFactory.VirtualPropertyMode mode ) {
        String source = "package t.x \n" +
                "import java.util.*; \n" +
                "import org.drools.factmodel.traits.Thing \n" +
                "import org.drools.factmodel.traits.Traitable \n" +
                "\n" +
                "global java.util.List list; \n" +
                "\n" +
                "declare trait Foo\n" +
                "   hardList : List = new ArrayList() \n" +
                "   softList : List = new ArrayList() \n" +
                "   moreList : List = new ArrayList() \n" +
                "   otraList : List = new ArrayList() \n" +
                "   primFld  : int = 3 \n" +
                "   primDbl  : double = 0.421 \n" +
                "\n" +
                "end\n" +
                "" +
                "declare Bar\n" +
                "   @Traitable()\n" +
                "   hardList : List \n" +
                "   moreList : List = Arrays.asList( 1, 2, 3 ) \n" +
                "\n" +
                "end\n" +
                "" +
                "rule Init when\n" +
                "then\n" +
                "   Bar o = new Bar();\n" +
                "   insert(o);\n" +
                "   Thing t = don( o, Thing.class ); \n" +
                "   t.getFields().put( \"otraList\", Arrays.asList( 42 ) ); \n" +
                "   don( o, Foo.class ); \n" +
                "end\n" +
                "" +
                "rule Don when\n" +
                "   $x : Foo( $h : hardList, $s : softList, $o : otraList, $m : moreList, $i : primFld, $d : primDbl ) \n" +
                "then \n" +
                "   list.add( $h ); \n" +
                "   list.add( $s ); \n" +
                "   list.add( $o ); \n" +
                "   list.add( $m ); \n" +
                "   list.add( $i ); \n" +
                "   list.add( $d ); \n" +
                "   System.out.println( $x ); \n" +
                "end\n" +
                ""
                ;

        StatefulKnowledgeSession ks = getSessionFromString( source );
        TraitFactory.setMode( mode, ks.getKnowledgeBase() );

        List list = new ArrayList();
        ks.setGlobal( "list", list );
        ks.fireAllRules();

        assertEquals( 6, list.size() );
        assertFalse( list.contains( null ) );

        List hard = (List) list.get( 0 );
        List soft = (List) list.get( 1 );
        List otra = (List) list.get( 2 );
        List more = (List) list.get( 3 );

        assertTrue( hard.isEmpty() );
        assertTrue( soft.isEmpty() );
        assertEquals( more, Arrays.asList( 1, 2, 3 ) );
        assertEquals( otra, Arrays.asList( 42 ) );

        assertTrue( list.contains( 3 ) );
        assertTrue( list.contains( 0.421 ) );
    }




    @Test
    public void testUnTraitedBeanTriples() {
        unTraitedBean( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test
    public void testUnTraitedBeanMap() {
        unTraitedBean( TraitFactory.VirtualPropertyMode.MAP );
    }


    public void unTraitedBean( TraitFactory.VirtualPropertyMode mode ) {
        String source = "package t.x \n" +
                        "import java.util.*; \n" +
                        "import org.drools.factmodel.traits.Thing \n" +
                        "import org.drools.factmodel.traits.Traitable \n" +
                        "\n" +
                        "global java.util.List list; \n" +
                        "\n" +
                        "" +
                        "declare trait Foo end\n" +
                        "" +
                        "declare Bar\n" +
                        "   @Traitable\n" +
                        "end\n" +
                        "declare Bar2\n" +
                        "end\n" +
                        "" +
                        "rule Init when\n" +
                        "then\n" +
                        "   Bar o = new Bar();\n" +
                        "   insert(o);\n" +
                        "   Bar2 o2 = new Bar2();\n" +
                        "   insert(o2);\n" +
                        "end\n" +
                        "" +
                        "rule Check when\n" +
                        "   $x : Bar( this not isA Foo ) \n" +
                        "then \n" +
                        "   System.out.println( $x ); \n" +
                        "end\n" +
                        "rule Check2 when\n" +
                        "   $x : Bar2( this not isA Foo ) \n" +
                        "then \n" +
                        "   System.out.println( $x ); \n" +
                        "end\n" +
                        "";


        StatefulKnowledgeSession ks = getSessionFromString( source );
        TraitFactory.setMode( mode, ks.getKnowledgeBase() );

        List list = new ArrayList();
        ks.setGlobal( "list", list );
        ks.fireAllRules();

    }






    @Test
    public void testIsAOptimization(  ) {
        String source = "package t.x \n" +
                        "import java.util.*; \n" +
                        "import org.drools.factmodel.traits.Thing \n" +
                        "import org.drools.factmodel.traits.Traitable \n" +
                        "\n" +
                        "global java.util.List list; \n" +
                        "\n" +
                        "" +
                        "declare trait A end\n" +
                        "declare trait B extends A end\n" +
                        "declare trait C extends B end\n" +
                        "declare trait D extends A end\n" +
                        "declare trait E extends C, D end\n" +
                        "declare trait F extends E end\n" +
                        "" +
                        "declare Kore\n" +
                        "   @Traitable\n" +
                        "end\n" +
                        "" +
                        "rule Init when\n" +
                        "then\n" +
                        "   Kore k = new Kore();\n" +
                        "   don( k, E.class ); \n" +
                        "end\n" +
                        "" +
                        "rule Check_1 when\n" +
                        "   $x : Kore( this isA [ B, D ]  ) \n" +
                        "then \n" +
                        "   list.add( \" B+D \" ); \n" +
                        "end\n" +
                        "" +
                        "rule Check_2 when\n" +
                        "   $x : Kore( this isA [ A ]  ) \n" +
                        "then \n" +
                        "   list.add( \" A \" ); \n" +
                        "end\n" +

                        "rule Check_3 when\n" +
                        "   $x : Kore( this not isA [ F ]  ) \n" +
                        "then \n" +
                        "   list.add( \" F \" ); \n" +
                        "end\n" +
                        "";


        StatefulKnowledgeSession ks = getSessionFromString( source );

        List list = new ArrayList();
        ks.setGlobal( "list", list );
        ks.fireAllRules();

        assertEquals( 3, list.size() );

    }



    @Test
    public void testShadowAliasingTriples() {
        shadowAlias( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test
    public void testShadowAliasingMap() {
        shadowAlias( TraitFactory.VirtualPropertyMode.MAP );
    }

    public void shadowAlias( TraitFactory.VirtualPropertyMode mode ) {

        KnowledgeBuilder kbuilderImpl = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilderImpl.add( ResourceFactory.newClassPathResource ( "org/drools/factmodel/traits/testTraitedAliasing.drl" ), ResourceType.DRL );
        if ( kbuilderImpl.hasErrors() ) {
            fail( kbuilderImpl.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilderImpl.getKnowledgePackages() );

        TraitFactory.setMode( TraitFactory.VirtualPropertyMode.TRIPLES, kbase );

        StatefulKnowledgeSession ks = kbase.newStatefulKnowledgeSession();

        ArrayList list = new ArrayList(  );
        ks.setGlobal( "list", list );

        ks.fireAllRules();
        System.out.println( list );
    }






    @Test
    public void testTypeRefractionOnInsert(  ) {
        String source = "package t.x \n" +
                        "import java.util.*; \n" +
                        "import org.drools.factmodel.traits.Thing \n" +
                        "import org.drools.factmodel.traits.Traitable \n" +
                        "\n" +
                        "global java.util.List list; \n" +
                        "\n" +
                        "" +
                        "declare trait A @propertyReactive end\n" +
                        "declare trait B extends A @propertyReactive end\n" +
                        "declare trait C extends B @propertyReactive end\n" +
                        "declare trait D extends A @propertyReactive end\n" +
                        "declare trait E extends C, D @propertyReactive end\n" +
                        "declare trait F extends E @propertyReactive end\n" +
                        "" +
                        "declare Kore\n" +
                        "   @Traitable\n" +
                        "end\n" +
                        "" +
                        "rule Init when\n" +
                        "then\n" +
                        "   Kore k = new Kore();\n" +
                        "   System.out.println( \"-----------------------------------------------------------------------\" ); \n " +
                        "   don( k, B.class ); \n" +

                        "   System.out.println( \"-----------------------------------------------------------------------\" ); \n " +
                        "   don( k, C.class ); \n" +

                        "   System.out.println( \"-----------------------------------------------------------------------\" ); \n " +
                        "   don( k, D.class ); \n" +

                        "   System.out.println( \"-----------------------------------------------------------------------\" ); \n " +
                        "   don( k, E.class ); \n" +

                        "   System.out.println( \"-----------------------------------------------------------------------\" ); \n " +
                        "   don( k, A.class ); \n" +

                        "   System.out.println( \"-----------------------------------------------------------------------\" ); \n " +
                        "   don( k, F.class ); \n" +
                        "end\n" +
                        "" +
                        "rule Check_1 when\n" +
                        "   $x : A( ) \n" +
                        "then \n" +
                        "   list.add( $x ); \n" +
                        "   System.out.println( \" A by \" + $x ); \n" +
                        "end\n" +
                        "";


        StatefulKnowledgeSession ks = getSessionFromString( source );

        List list = new ArrayList();
        ks.setGlobal( "list", list );
        ks.fireAllRules();

        assertEquals( 1, list.size() );

    }


    @Test
    public void testTypeRefractionOnQuery(  ) {
        String source = "declare BaseObject\n" +
                        "@Traitable\n" +
                        "id : String @key\n" +
                        "end\n" +
                        "\n" +
                        "declare trait A\n" +
                        "id : String @key\n" +
                        "end\n" +
                        "\n" +
                        "declare trait B extends A\n" +
                        "end\n" +
                        "\n" +
                        "declare trait C extends A\n" +
                        "end\n" +
                        "\n" +
                        "rule \"init\"\n" +
                        "when\n" +
                        "then\n" +
                        "BaseObject $obj = new BaseObject(\"testid123\");\n" +
                        "insert ($obj);\n" +
                        "don($obj, B.class, true);\n" +
                        "don($obj, C.class, true);\n" +
                        "end\n" +
                        "\n" +
                        "query \"QueryTraitA\"\n" +
                        "a : A()\n" +
                        "end";


        StatefulKnowledgeSession ks = getSessionFromString( source );

        ks.fireAllRules();

        QueryResults res = ks.getQueryResults( "QueryTraitA" );

        assertEquals( 1, res.size() );

    }

    @Test
    public void testTypeRefractionOnQuery2(  ) {
        String source = "package t.x \n" +
                        "import java.util.*; \n" +
                        "import org.drools.factmodel.traits.Thing \n" +
                        "import org.drools.factmodel.traits.Traitable \n" +
                        "\n" +
                        "global java.util.List list; \n" +
                        "\n" +
                        "" +
                        "declare trait A end\n" +
                        "declare trait B extends A end\n" +
                        "declare trait C extends B end\n" +
                        "declare trait D extends A end\n" +
                        "declare trait E extends C, D end\n" +
                        "declare trait F extends E end\n" +
                        "declare trait G extends A end\n" +
                        "" +
                        "declare Kore\n" +
                        "   @Traitable\n" +
                        "end\n" +
                        "" +
                        "rule Init when\n" +
                        "then\n" +
                        "   Kore k = new Kore();\n" +
                        "   don( k, C.class ); \n" +
                        "   don( k, D.class ); \n" +
                        "   don( k, E.class ); \n" +
                        "   don( k, B.class ); \n" +
                        "   don( k, A.class ); \n" +
                        "   don( k, F.class ); \n" +
                        "   don( k, G.class ); \n" +
                        "   shed( k, B.class ); \n" +
                        "end\n" +
                        "" +
                        "rule RuleA\n" +
                        "when \n" +
                        "   $x : A(  ) \n" +
                        "then \n" +
                        "   System.out.println( $x ); \n " +
                        "end\n" +
                        " \n" +
                        "query queryA1\n" +
                        "   $x := A(  ) \n" +
                        "end\n" +
                        "";


        StatefulKnowledgeSession ks = getSessionFromString( source );

        List list = new ArrayList();
        ks.setGlobal( "list", list );
        ks.fireAllRules();

        QueryResults res;
        res = ks.getQueryResults( "queryA1" );
        assertEquals( 1, res.size() );
    }

    @Test
    public void testTypeRefractionOnQueryWithIsA(  ) {
        String source = "package t.x \n" +
                        "import java.util.*; \n" +
                        "import org.drools.factmodel.traits.Thing \n" +
                        "import org.drools.factmodel.traits.Traitable \n" +
                        "\n" +
                        "global java.util.List list; \n" +
                        "\n" +
                        "" +
                        "declare trait A @propertyReactive end\n" +
                        "declare trait B extends A @propertyReactive end\n" +
                        "declare trait C extends B @propertyReactive end\n" +
                        "declare trait D extends A @propertyReactive end\n" +
                        "declare trait E extends C, D @propertyReactive end\n" +
                        "declare trait F extends E @propertyReactive end\n" +
                        "" +
                        "declare Kore\n" +
                        "   @Traitable\n" +
                        "end\n" +
                        "" +
                        "rule Init when\n" +
                        "then\n" +
                        "   Kore k = new Kore();\n" +
                        "   don( k, C.class ); \n" +
                        "   don( k, D.class ); \n" +
                        "   don( k, E.class ); \n" +
                        "   don( k, B.class ); \n" +
                        "   don( k, A.class ); \n" +
                        "   don( k, F.class ); \n" +
                        "   shed( k, B.class ); \n" +
                        "end\n" +
                        "" +
                        " \n" +
                        "query queryA\n" +
                        "   $x := Kore( this isA A ) \n" +
                        "end\n" +
                        "";


        StatefulKnowledgeSession ks = getSessionFromString( source );

        List list = new ArrayList();
        ks.setGlobal( "list", list );
        ks.fireAllRules();

        QueryResults res = ks.getQueryResults( "queryA" );
        Iterator<QueryResultsRow> iter = res.iterator();
        Object a = iter.next().get( "$x" );
        assertFalse( iter.hasNext() );

        assertEquals( 1, res.size() );

    }





    @Test
    public void testCoreUpdate4(  ) {
        String source = "package t.x \n" +
                        "import java.util.*; \n" +
                        "import org.drools.factmodel.traits.Thing \n" +
                        "import org.drools.factmodel.traits.Traitable \n" +
                        "\n" +
                        "global java.util.List list; \n" +
                        "\n" +
                        "" +
                        "declare trait A " +
                        "   age : int \n" +
                        "end\n" +
                        "" +
                        "declare Kore\n" +
                        "   @Traitable\n" +
                        "   @propertyReactive" +
                        "   age : int\n" +
                        "end\n" +
                        "" +
                        "rule Init \n" +
                        "when\n" +
                        "then\n" +
                        "   Kore k = new Kore( 44 );\n" +
                        "   insert( k ); \n" +
                        "end\n" +
                        "" +
                        "" +
                        "rule Don \n" +
                        "no-loop \n" +
                        "when\n" +
                        "   $x : Kore() \n" +
                        "then \n" +
                        "   System.out.println( \"Donning\" ); \n" +
                        "   don( $x, A.class ); \n" +
                        "end\n" +
                        "rule React \n" +
                        "salience 1" +
                        "when\n" +
                        "   $x : Kore( this isA A.class ) \n" +
                        "then \n" +
                        "   System.out.println( \"XXXXXXXXXXXXXXXXXXXXXX \" + $x ); \n" +
                        "   list.add( $x ); \n" +
                        "end\n" +
                        "";
        StatefulKnowledgeSession ks = getSessionFromString( source );

        List list = new ArrayList();
        ks.setGlobal( "list", list );
        ks.fireAllRules();

        for ( Object o : ks.getObjects() ) {
            System.err.println( o );
        }
        assertEquals( 1, list.size() );
    }


    @Test
    public void testMapCore2(  ) {
        String source = "package org.drools.factmodel.traits.test;\n" +
                        "\n" +
                        "import java.util.*;\n" +
                        "import org.drools.factmodel.traits.Traitable;\n" +
                        "" +
                        "global List list;\n " +
                        "" +
                        "declare HashMap @Traitable end \n" +
                        "\n" +
                        "declare org.drools.factmodel.MapCore \n" +
                        "end\n" +
                        "\n" +
                        "global List list; \n" +
                        "\n" +
                        "declare trait PersonMap\n" +
                        "@propertyReactive  \n" +
                        "   name : String  \n" +
                        "   age  : int  \n" +
                        "   height : Double  \n" +
                        "end\n" +
                        "\n" +
                        "declare trait StudentMap\n" +
                        "@propertyReactive\n" +
                        "   ID : String\n" +
                        "   GPA : Double = 3.0\n" +
                        "end\n" +
                        "\n" +
                        "rule Don  \n" +
                        "when  \n" +
                        "  $m : Map( this[ \"age\"] == 18, this[ \"ID\" ] != \"100\" )\n" +
                        "then  \n" +
                        "   don( $m, PersonMap.class );\n" +
                        "   System.out.println( \"done: PersonMap\" );\n" +
                        "\n" +
                        "end\n" +
                        "\n" +
                        "rule Log  \n" +
                        "when  \n" +
                        "   $p : PersonMap( name == \"john\", age > 10 )\n" +
                        "then  \n" +
                        "   modify ( $p ) {  \n" +
                        "       setHeight( 184.0 );  \n" +
                        "   }\n" +
                        "   System.out.println(\"Log: \" +  $p );\n" +
                        "end\n" +
                        "" +
                        "" +
                        "rule Don2\n" +
                        "salience -1\n" +
                        "when\n" +
                        "   $m : Map( this[ \"age\"] == 18, this[ \"ID\" ] != \"100\" ) " +
                        "then\n" +
                        "   don( $m, StudentMap.class );\n" +
                        "   System.out.println( \"done2: StudentMap\" );\n" +
                        "end\n" +
                        "" +
                        "" +
                        "rule Log2\n" +
                        "salience -2\n" +
                        "no-loop\n" +
                        "when\n" +
                        "   $p : StudentMap( $h : fields[ \"height\" ], GPA >= 3.0 ) " +
                        "then\n" +
                        "   modify ( $p ) {\n" +
                        "       setGPA( 4.0 ),\n" +
                        "       setID( \"100\" );\n" +
                        "   }\n" +
                        "   System.out.println(\"Log2: \" + $p );\n" +
                        "end\n" +
                        "" +
                        "" +
                        "\n" +
                        "rule Shed1\n" +
                        "salience -5// it seams that the order of shed must be the same as applying don\n" +
                        "when\n" +
                        "    $m : PersonMap()\n" +
                        "then\n" +
                        "   shed( $m, PersonMap.class );\n" +
                        "   System.out.println( \"shed: PersonMap\" );\n" +
                        "end\n" +
                        "\n" +
                        "rule Shed2\n" +
                        "salience -9\n" +
                        "when\n" +
                        "    $m : StudentMap()\n" +
                        "then\n" +
                        "   shed( $m, StudentMap.class );\n" +
                        "   System.out.println( \"shed: StudentMap\" );\n" +
                        "end\n" +
                        "" +
                        "rule Last  \n" +
                        "salience -99 \n" +
                        "when  \n" +
                        "  $m : Map( this not isA StudentMap.class )\n" +
                        "then  \n" +
                        "   System.out.println( \"Final\" );\n" +
                        "   $m.put( \"final\", true );" +
                        "\n" +
                        "end\n" +
                        "\n" +
                        "\n";

        StatefulKnowledgeSession ks = getSessionFromString( source );
        TraitFactory.setMode( TraitFactory.VirtualPropertyMode.MAP, ks.getKnowledgeBase() );

        List list = new ArrayList();
        ks.setGlobal( "list", list );

        Map<String,Object> map = new HashMap<String, Object>(  );
        map.put( "name", "john" );
        map.put( "age", 18 );
        ks.insert( map );

        ks.fireAllRules();


        for ( Object o : ks.getObjects() ) {
            System.err.println( o );
        }

        assertEquals( "100", map.get( "ID" ) );
        assertEquals( 184.0, map.get( "height" ) );
        assertEquals( 4.0, map.get( "GPA" ) );
        assertEquals( true, map.get( "final" ) );

    }


    @Test
    public void traitLogicalSupportAndRetract() {
        String drl = "package org.drools.trait.test;\n" +
                     "\n" +
                     "import org.drools.factmodel.traits.Traitable;\n" +
                     "\n" +
                     "global java.util.List list;\n" +
                     "\n" +
                     "declare trait Student\n" +
                     "  age  : int\n" +
                     "  name : String\n" +
                     "end\n" +
                     "\n" +
                     "declare Person\n" +
                     "  @Traitable\n" +
                     "  name : String\n" +
                     "end\n" +
                     "\n" +
                     "rule Init when then insert( new Person( \"john\" ) ); end \n" +
                     "" +
                     "rule \"Don Logical\"\n" +
                     "when\n" +
                     "  $s : String( this == \"trigger1\" )\n" +
                     "  $p : Person() \n" +
                     "then\n" +
                     "  don( $p, Student.class, true );\n" +
                     "end\n" +
                     "" +
                     "rule \"Don Logical2\"\n" +
                     "when\n" +
                     "  $s : String( this == \"trigger2\" )\n" +
                     "  $p : Person() \n" +
                     "then\n" +
                     "  don( $p, Student.class, true );\n" +
                     "end\n" +
                     "" +
                     "rule \"Undon \"\n" +
                     "when\n" +
                     "  $s : String( this == \"trigger3\" )\n" +
                     "  $p : Person() \n" +
                     "then\n" +
                     "  shed( $p, org.drools.factmodel.traits.Thing.class ); " +
                     "  retract( $s ); \n" +
                     "end\n" +
                     " " +
                     "rule \"Don Logical3\"\n" +
                     "when\n" +
                     "  $s : String( this == \"trigger4\" )\n" +
                     "  $p : Person() \n" +
                     "then\n" +
                     "  don( $p, Student.class, true );" +
                     "end\n" +
                     " " +
                     "rule \"Undon 2\"\n" +
                     "when\n" +
                     "  $s : String( this == \"trigger5\" )\n" +
                     "  $p : Person() \n" +
                     "then\n" +
                     "  retract( $s ); \n" +
                     "  retract( $p ); \n" +
                     "end\n" +
                     "";


        StatefulKnowledgeSession ksession = getSessionFromString(drl);
        TraitFactory.setMode( TraitFactory.VirtualPropertyMode.MAP, ksession.getKnowledgeBase() );

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        FactHandle h1 = ksession.insert( "trigger1" );
        FactHandle h2 = ksession.insert( "trigger2" );
        ksession.fireAllRules();

        for ( Object o : ksession.getObjects() ) {
            System.err.println( o );
        }
        System.err.println( "---------------------------------" );

        assertEquals( 5, ksession.getObjects().size() );

        ksession.retract( h1 );
        ksession.fireAllRules();

        for ( Object o : ksession.getObjects() ) {
            System.err.println( o );
        }
        System.err.println( "---------------------------------" );

        assertEquals( 4, ksession.getObjects().size() );

        ksession.retract( h2 );
        ksession.fireAllRules();

        for ( Object o : ksession.getObjects() ) {
            System.err.println( o );
        }
        System.err.println( "---------------------------------" );

        assertEquals( 2, ksession.getObjects().size() );

        ksession.insert( "trigger3" );
        ksession.fireAllRules();

        for ( Object o : ksession.getObjects() ) {
            System.err.println( o );
        }
        System.err.println( "---------------------------------" );

        assertEquals( 1, ksession.getObjects().size() );

        ksession.insert( "trigger4" );
        ksession.fireAllRules();

        for ( Object o : ksession.getObjects() ) {
            System.err.println( o );
        }
        System.err.println( "---------------------------------" );

        assertEquals( 4, ksession.getObjects().size() );

        ksession.insert( "trigger5" );
        ksession.fireAllRules();

        for ( Object o : ksession.getObjects() ) {
            System.err.println( o );
        }
        System.err.println( "---------------------------------" );

        assertEquals( 1, ksession.getObjects().size() );
    }




    @Test
    public void testShedThing() {
        String s1 = "package test;\n" +
                    "import org.drools.factmodel.traits.*;\n" +
                    "global java.util.List list; \n" +
                    "" +
                    "declare trait A id : int end\n" +
                    "declare trait B extends A end\n" +
                    "declare trait C extends A end\n" +
                    "declare trait D extends A end\n" +
                    "declare trait E extends B end\n" +
                    "" +
                    "declare Core @Traitable id : int = 0 end \n" +
                    "" +
                    "rule \"Init\" when \n" +
                    "then \n" +
                    "   insert( new Core() );" +
                    "end \n" +
                    "" +
                    "rule \"donManyThing\"\n" +
                    "when\n" +
                    "    $x : Core( id == 0 )\n" +
                    "then\n" +
                    "    don( $x, A.class );\n" +
                    "    don( $x, B.class );\n" +
                    "    don( $x, C.class );\n" +
                    "    don( $x, D.class );\n" +
                    "    don( $x, E.class );\n" +
                    "end\n" +
                    "\n" +
                    "\n" +
                    "" +
                    "rule \"Mod\" \n" +
                    "salience -10 \n" +
                    "when \n" +
                    "  $g : String( this == \"go\" ) \n" +
                    "  $x : Core( id == 0 ) \n" +
                    "then \n" +
                    "  shed( $x, Thing.class ); " +
                    "  retract( $g ); \n\n" +
                    "end \n" +
                    "";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ByteArrayResource( s1.getBytes() ), ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        TraitFactory.setMode( TraitFactory.VirtualPropertyMode.MAP, kbase ); // not relevant

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        List list = new ArrayList();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        ksession.insert( "go" );
        ksession.fireAllRules();

        for ( Object o : ksession.getObjects() ) {
            System.out.println( o );
        }

        assertEquals( 1, ksession.getObjects().size() );
    }


    @Test
    public void testRetractThings() {
        String s1 = "package test;\n" +
                    "import org.drools.factmodel.traits.*;\n" +
                    "global java.util.List list; \n" +
                    "" +
                    "declare trait A id : int end\n" +
                    "declare trait B extends A end\n" +
                    "declare trait C extends A end\n" +
                    "declare trait D extends A end\n" +
                    "declare trait E extends B end\n" +
                    "" +
                    "declare Core @Traitable id : int = 0 end \n" +
                    "" +
                    "rule \"Init\" when \n" +
                    "then \n" +
                    "   insert( new Core() );" +
                    "end \n" +
                    "" +
                    "rule \"donManyThing\"\n" +
                    "when\n" +
                    "    $x : Core( id == 0 )\n" +
                    "then\n" +
                    "    don( $x, A.class );\n" +
                    "    don( $x, B.class );\n" +
                    "    don( $x, C.class );\n" +
                    "    don( $x, D.class );\n" +
                    "    don( $x, E.class );\n" +
                    "end\n" +
                    "\n" +
                    "\n" +
                    "" +
                    "rule \"Mod\" \n" +
                    "salience -10 \n" +
                    "when \n" +
                    "  $g : String( this == \"go\" ) \n" +
                    "  $x : Core( id == 0 ) \n" +
                    "then \n" +
                    "  retract( $x ); \n\n" +
                    "  retract( $g ); \n\n" +
                    "end \n" +
                    "";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ByteArrayResource( s1.getBytes() ), ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        TraitFactory.setMode( TraitFactory.VirtualPropertyMode.MAP, kbase ); // not relevant

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        List list = new ArrayList();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        ksession.insert( "go" );
        ksession.fireAllRules();

        for ( Object o : ksession.getObjects() ) {
            System.out.println( o );
        }

        assertEquals( 0, ksession.getObjects().size() );
    }

    @Test
    public void traitLogicalRemovalSimple( ) {
        String drl = "package org.drools.compiler.trait.test;\n" +
                     "\n" +
                     "import org.drools.factmodel.traits.Traitable;\n" +
                     "\n" +
                     "global java.util.List list;\n" +
                     "\n" +
                     "declare trait Student\n" +
                     " age : int\n" +
                     " name : String\n" +
                     "end\n" +
                     "declare trait Worker\n" +
                     " wage : int\n" +
                     "end\n" +
                     "" +
                     "declare trait Scholar extends Student\n" +
                     "end\n" +
                     "\n" +
                     "declare Person\n" +
                     " @Traitable\n" +
                     " name : String\n" +
                     "end\n" +
                     "\n" +
                     "\n" +
                     "rule \"Don Logical\"\n" +
                     "when\n" +
                     " $s : String( this == \"trigger\" )\n" +
                     "then\n" +
                     " Person p = new Person( \"john\" );\n" +
                     " insert( p ); \n" +
                     " don( p, Student.class, true );\n" +
                     " don( p, Worker.class );\n" +
                     " don( p, Scholar.class );\n" +
                     "end";


        StatefulKnowledgeSession ksession = getSessionFromString(drl);
        TraitFactory.setMode( TraitFactory.VirtualPropertyMode.MAP, ksession.getKnowledgeBase() );

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        FactHandle h = ksession.insert( "trigger" );
        ksession.fireAllRules();
        assertEquals( 6, ksession.getObjects().size() );

        ksession.retract( h );
        ksession.fireAllRules();

        for ( Object o : ksession.getObjects() ) {
            // lose the string and the Student proxy
            System.out.println( o );
        }
        assertEquals( 4, ksession.getObjects().size() );

    }



    @Traitable
    public static class TraitableFoo {

        private String id;

        public TraitableFoo( String id, int x, Object k ) {
            setId( id );
        }

        public String getId() {
            return id;
        }

        public void setId( String id ) {
            this.id = id;
        }
    }

    @Traitable
    public static class XYZ extends TraitableFoo {

        public XYZ() {
            super( null, 0, null );
        }

    }


    @Test
    public void traitDonLegacyClassWithoutEmptyConstructor( ) {
        String drl = "package org.drools.compiler.trait.test;\n" +
                     "\n" +
                     "import org.drools.factmodel.traits.TraitTest.TraitableFoo;\n" +
                     "import org.drools.factmodel.traits.Traitable;\n" +
                     "\n" +
                     "" +
                     "declare trait Bar\n" +
                     "end\n" +
                     "\n" +
                     "rule \"Don\"\n" +
                     "no-loop \n" +
                     "when\n" +
                     " $f : TraitableFoo( )\n" +
                     "then\n" +
                     "  Bar b = don( $f, Bar.class );\n" +
                     "end";


        StatefulKnowledgeSession ksession = getSessionFromString(drl);
        TraitFactory.setMode( TraitFactory.VirtualPropertyMode.MAP, ksession.getKnowledgeBase() );
        ksession.addEventListener( new DebugAgendaEventListener(  ) );

        ksession.insert( new TraitableFoo( "xx", 0, null ) );
        ksession.fireAllRules();

        for ( Object o : ksession.getObjects() ) {
            System.out.println( o );
        }

        assertEquals( 3, ksession.getObjects().size() );
    }




    @Test
    public void testMapCoreManyTraits(  ) {
        String source = "package org.drools.test;\n" +
                        "\n" +
                        "import java.util.*;\n" +
                        "import org.drools.factmodel.traits.Traitable;\n" +
                        "" +
                        "global List list;\n " +
                        "\n" +
                        "declare HashMap @Traitable end \n" +
                        "" +
                        "declare org.drools.factmodel.MapCore \n" +
                        "end\n" +
                        "\n" +
                        "global List list; \n" +
                        "\n" +
                        "declare trait PersonMap\n" +
                        "@propertyReactive  \n" +
                        "   name : String  \n" +
                        "   age  : int  \n" +
                        "   height : Double  \n" +
                        "end\n" +
                        "\n" +
                        "declare trait StudentMap\n" +
                        "@propertyReactive\n" +
                        "   ID : String\n" +
                        "   GPA : Double = 3.0\n" +
                        "end\n" +
                        "\n" +
                        "rule Don  \n" +
                        "no-loop \n" +
                        "when  \n" +
                        "  $m : Map( this[ \"age\"] == 18 )\n" +
                        "then  \n" +
                        "   Object obj1 = don( $m, PersonMap.class );\n" +
                        "   Object obj2 = don( obj1, StudentMap.class );\n" +
                        "   System.out.println( \"done: PersonMap\" );\n" +
                        "\n" +
                        "end\n" +
                        "\n";

        StatefulKnowledgeSession ks = getSessionFromString( source );
        TraitFactory.setMode( TraitFactory.VirtualPropertyMode.MAP, ks.getKnowledgeBase() );

        List list = new ArrayList();
        ks.setGlobal( "list", list );

        Map<String,Object> map = new HashMap<String, Object>(  );
        map.put( "name", "john" );
        map.put( "age", 18 );
        ks.insert( map );

        ks.fireAllRules();

        for ( Object o : ks.getObjects() ) {
            System.err.println( o );
        }

        assertEquals( 3.0, map.get( "GPA" ) );
    }


    @Test
    public void testRetractCoreObjectChained(  ) {
        String source = "package org.drools.test;\n" +
                        "import java.util.List; \n" +
                        "import org.drools.factmodel.traits.Thing \n" +
                        "import org.drools.factmodel.traits.Traitable \n" +
                        "\n" +
                        "global java.util.List list; \n" +
                        "\n" +
                        "" +
                        "declare trait A " +
                        "   age : int \n" +
                        "end\n" +
                        "" +
                        "declare Kore\n" +
                        "   @Traitable\n" +
                        "   age : int\n" +
                        "end\n" +
                        "" +
                        "rule Init \n" +
                        "when\n" +
                        "   $s : String() \n" +
                        "then\n" +
                        "   Kore k = new Kore( 44 );\n" +
                        "   insertLogical( k ); \n" +
                        "end\n" +
                        "" +
                        "" +
                        "rule Don \n" +
                        "no-loop \n" +
                        "when\n" +
                        "   $x : Kore() \n" +
                        "then \n" +
                        "   System.out.println( \"Donning\" ); \n" +
                        "   don( $x, A.class ); \n" +
                        "end\n" +
                        "" +
                        "" +
                        "rule Retract \n" +
                        "salience -99 \n" +
                        "when \n" +
                        "   $x : String() \n" +
                        "then \n" +
                        "   System.out.println( \"Retracting\" ); \n" +
                        "   retract( $x ); \n" +
                        "end \n" +
                        "\n";

        StatefulKnowledgeSession ks = getSessionFromString( source );
        TraitFactory.setMode( TraitFactory.VirtualPropertyMode.MAP, ks.getKnowledgeBase() );

        List list = new ArrayList();
        ks.setGlobal( "list", list );

        ks.insert( "go" );

        ks.fireAllRules();

        for ( Object o : ks.getObjects() ) {
            System.out.println( o );
        }

        assertEquals( 0, ks.getObjects().size() );

        ks.dispose();
    }


    @Test
    public void testUpdateLegacyClass(  ) {
        String source = "package org.drools.text;\n" +
                        "\n" +
                        "global java.util.List list;\n" +
                        "\n" +
                        "import org.drools.Person;\n" +
                        "import org.drools.factmodel.traits.Traitable;\n" +
                        "\n" +
                        "declare Person @Traitable end \n" +
                        "" +
                        "declare trait Student\n" +
                        "  name : String\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Init\"\n" +
                        "salience 10 \n" +
                        "when\n" +
                        "  $p : Person( this not isA Student )\n" +
                        "then\n" +
                        "  System.out.println( \"Don person\" ); \n" +
                        "  don( $p, Student.class );\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Go\"\n" +
                        "when\n" +
                        "  $s : String( this == \"X\" )\n" +
                        "  $p : Person()\n" +
                        "then\n" +
                        "  System.out.println( \"Change name\" ); \n" +
                        "  retract( $s ); \n" +
                        "  modify( $p ) { setName( $s ); }\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Mod\"\n" +
                        "when\n" +
                        "  Student( name == \"X\" )\n" +
                        "then\n" +
                        "  System.out.println( \"Update detected\" );\n" +
                        "  list.add( 0 );\n" +
                        "end";

        StatefulKnowledgeSession ks = getSessionFromString( source );
        TraitFactory.setMode( TraitFactory.VirtualPropertyMode.MAP, ks.getKnowledgeBase() );

        List list = new ArrayList();
        ks.setGlobal( "list", list );

        ks.insert( new Person( "john", 32 ) );
        ks.insert( "X" );

        ks.fireAllRules();

        assertTrue( list.contains( 0 ) );
        assertEquals( 1, list.size() );

        ks.dispose();
    }



    @Test
    @Ignore
    public void testPropertyClash(  ) {
        String source = "package org.drools.text;\n" +
                        "\n" +
                        "global java.util.List list;\n" +
                        "\n" +
                        "import org.drools.factmodel.traits.Traitable;\n" +
                        "\n" +
                        "declare Person @Traitable @propertyReactive \n" +
                        "end \n" +
                        "" +
                        "declare Person1 extends Person @Traitable \n" +
                        "   id : String \n" +
                        "end \n" +
                        "" +
                        "declare Person2 extends Person @Traitable \n" +
                        "   id : int \n" +
                        "end \n" +
                        "" +
                        "declare trait Student\n" +
                        "   @propertyReactive \n" +
                        "   id : String = \"a\" \n" +
                        "end\n" +
                        "declare trait Worker\n" +
                        "   @propertyReactive \n" +
                        "   id : int = 3 \n" +
                        "end\n" +
                        "" +
                        "rule \"Init\" when then \n" +
                        "   insert( new Person() ); \n" +
                        "   insert( new Person1() ); \n" +
                        "   insert( new Person2() ); \n" +
                        "end \n" +
                        "" +
                        "\n" +
                        "rule \"Don\"\n" +
                        "when\n" +
                        "   $p : Person() \n" +
                        "then\n" +
                        "  System.out.println( \"Don person\" ); \n"
                        +
                        "  Student $s = (Student) don( $p, Student.class );\n" +
                        "  modify ( $s ) { setId( \"xyz\" ); } " +
                        "  " +
                        "  Worker $w = don( $p, Worker.class );\n" +
                        "  modify ( $w ) { setId( 99 ); } " +
                        "end\n" +
                        "\n" +
                        "rule \"Stud\"\n" +
                        "when\n" +
                        "  $s : Student( id == \"xyz\" )\n" +
                        "then\n" +
                        "  System.out.println( \">>>>>>>>>> Student\" + $s ); \n" +
                        "end\n" +
                        "\n" +
                        "rule \"Mod\"\n" +
                        "when\n" +
                        "  $w : Worker( id == 99 )\n" +
                        "then\n" +
                        "  System.out.println( \">>>>>>>>>> Worker\" + $w );\n" +
                        "end";

        StatefulKnowledgeSession ks = getSessionFromString( source );
        TraitFactory.setMode( TraitFactory.VirtualPropertyMode.MAP, ks.getKnowledgeBase() );

        List list = new ArrayList();
        ks.setGlobal( "list", list );

        HashMap map;
        ks.fireAllRules();

        ks.dispose();
    }


    @Test
    public void testMultipleModifications() {
        String drl = "package org.drools.traits.test;\n" +
                     "\n" +
                     "import org.drools.factmodel.traits.Traitable;\n" +
                     "" +
                     "global java.util.List list;" +
                     "\n" +
                     "declare Person\n" +
                     "@Traitable\n" +
                     "@propertyReactive\n" +
                     "    ssn : String\n" +
                     "    pob : String\n" +
                     "    isStudent : boolean\n" +
                     "    hasAssistantship : boolean\n" +
                     "end\n" +
                     "\n" +
                     "declare trait Student\n" +
                     "@propertyReactive\n" +
                     "    studyingCountry : String\n" +
                     "    hasAssistantship : boolean\n" +
                     "end\n" +
                     "\n" +
                     "declare trait Worker\n" +
                     "@propertyReactive\n" +
                     "    pob : String\n" +
                     "    workingCountry : String\n" +
                     "end\n" +
                     "\n" +
                     "declare trait USCitizen\n" +
                     "@propertyReactive\n" +
                     "    pob : String = \"US\"\n" +
                     "end\n" +
                     "\n" +
                     "declare trait ITCitizen\n" +
                     "@propertyReactive\n" +
                     "    pob : String = \"IT\"\n" +
                     "end\n" +
                     "\n" +
                     "declare trait IRCitizen\n" +
                     "@propertyReactive\n" +
                     "    pob : String = \"IR\"\n" +
                     "end\n" +
                     "\n" +
                     "rule \"init\"\n" +
                     "when\n" +
                     "then\n" +
                     "    insert( new Person(\"1234\",\"IR\",true,true) );\n" +
                     "end\n" +
                     "\n" +
                     "rule \"check for being student\"\n" +
                     "when\n" +
                     "    $p : Person( $ssn : ssn, $pob : pob,  isStudent == true )\n" +
                     "then\n" +
                     "    Student st = (Student) don( $p , Student.class );\n" +
                     "    modify( st ){\n" +
                     "        setStudyingCountry( \"US\" );\n" +
                     "    }\n" +
                     "end\n" +
                     "\n" +
                     "rule \"check for IR\"\n" +
                     "when\n" +
                     "    $p : Person( pob == \"IR\" )\n" +
                     "then\n" +
                     "    don( $p , IRCitizen.class );\n" +
                     "end\n" +
                     "\n" +
                     "rule \"check for being US citizen\"\n" +
                     "when\n" +
                     "    $s : Student( studyingCountry == \"US\" )\n" +
                     "then\n" +
                     "    don( $s , USCitizen.class );\n" +
                     "end\n" +
                     "\n" +
                     "rule \"check for being worker\"\n" +
                     "when\n" +
                     "    $p : Student( hasAssistantship == true, $sc : studyingCountry  )\n" +
                     "then\n" +
                     "    Worker wr = (Worker) don( $p , Worker.class );\n" +
                     "    modify( wr ){\n" +
                     "        setWorkingCountry( $sc );\n" +
                     "    }\n" +
                     "\n" +
                     "end\n" +
                     "\n" +
                     "rule \"Join Full\"\n" +
                     "salience -1\n" +
                     "when\n" +
                     "    Student( )      // $sc := studyingCountry )\n" +
                     "    USCitizen( )\n" +
                     "    IRCitizen( )      // $pob := pob )\n" +
                     "    Worker( )       // pob == $pob , workingCountry == $sc )\n" +
                     "then\n" +
                     "    list.add( 1 ); " +
                     "end\n" +
                     "\n" +
                     "\n";

        StatefulKnowledgeSession ks = getSessionFromString( drl );
        TraitFactory.setMode( TraitFactory.VirtualPropertyMode.MAP, ks.getKnowledgeBase() );

        List list = new ArrayList();
        ks.setGlobal( "list", list );

        HashMap map;
        ks.fireAllRules();

        assertTrue( list.contains( 1 ) );
        assertEquals( 1, list.size() );

        ks.dispose();

    }


    @Test
    public void testPropagation() {
        String drl = "package org.drools.test;\n" +
                     "import org.drools.factmodel.traits.*; \n" +
                     "\n" +
                     "global java.util.List list; \n" +
                     "" +
                     "declare X @Traitable end \n" +
                     "" +
                     "declare trait A @propertyReactive end\n" +
                     "declare trait B extends A @propertyReactive end\n" +
                     "declare trait C extends B @propertyReactive end \n" +
                     "declare trait D extends C @propertyReactive end\n" +
                     "declare trait E extends B,C @propertyReactive end\n" +
                     "declare trait F extends E @propertyReactive end\n" +
                     "declare trait G extends B @propertyReactive end\n" +
                     "declare trait H extends G @propertyReactive end\n" +
                     "declare trait I extends E,H @propertyReactive end\n" +
                     "declare trait J extends I @propertyReactive end\n" +
                     "" +
                     "rule Init when then X x = new X(); insert( x ); don( x, F.class); end \n"+
                     "rule Go when String( this == \"go\" ) $x : X() then don( $x, H.class); end \n" +
                     "rule Go2 when String( this == \"go2\" ) $x : X() then don( $x, D.class); end \n" +
                     "";

        for ( int j = 'A'; j <= 'J'; j ++ ) {
            String x = "" + (char) j;
            drl += "rule \"Log " + x + "\" when " + x + "() then System.out.println( \"@@ " + x + " detected \" ); list.add( \"" + x + "\" ); end \n";

            drl += "rule \"Log II" + x + "\" salience -1 when " + x + "( ";
            drl += "this isA H";
            drl += " ) then System.out.println( \"@@ as H >> " + x + " detected \" ); list.add( \"H" + x + "\" ); end \n";
        }

        StatefulKnowledgeSession ks = getSessionFromString( drl );
        TraitFactory.setMode( TraitFactory.VirtualPropertyMode.MAP, ks.getKnowledgeBase() );

        List list = new ArrayList();
        ks.setGlobal( "list", list );

        ks.fireAllRules();

        assertTrue( list.contains( "A" ) );
        assertTrue( list.contains( "B" ) );
        assertTrue( list.contains( "C" ) );
        assertTrue( list.contains( "E" ) );
        assertTrue( list.contains( "F" ) );
        assertEquals( 5, list.size() );

        list.clear();

        System.out.println( "---------------------------------------" );

        ks.insert( "go" );
        ks.fireAllRules();

        assertTrue( list.contains( "H" ) );
        assertTrue( list.contains( "G" ) );
        assertTrue( list.contains( "HA" ) );
        assertTrue( list.contains( "HB" ) );
        assertTrue( list.contains( "HC" ) );
        assertTrue( list.contains( "HE" ) );
        assertTrue( list.contains( "HF" ) );
        assertTrue( list.contains( "HG" ) );
        assertTrue( list.contains( "HH" ) );
        assertEquals( 9, list.size() );
        list.clear();

        System.out.println( "---------------------------------------" );

        ks.insert( "go2" );
        ks.fireAllRules();

        assertTrue( list.contains( "D" ) );
        assertTrue( list.contains( "HA" ) );
        assertTrue( list.contains( "HB" ) );
        assertTrue( list.contains( "HC" ) );
        assertTrue( list.contains( "HE" ) );
        assertTrue( list.contains( "HF" ) );
        assertTrue( list.contains( "HG" ) );
        assertTrue( list.contains( "HH" ) );
        assertTrue( list.contains( "HH" ) );
        assertTrue( list.contains( "HD" ) );
        assertEquals( 9, list.size() );

        ks.dispose();

    }




}
