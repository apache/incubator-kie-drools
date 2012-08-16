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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
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
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ByteArrayResource;
import org.drools.reteoo.ObjectTypeConf;
import org.drools.runtime.ClassObjectFilter;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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

    @Before
    public void reset() {
        TraitRegistry.reset();
        TraitFactory.reset();
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

            Map<String, Object> virtualFields = imp.getDynamicProperties();
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

        ks.insert( "die" );
        ks.fireAllRules();

        Assert.assertTrue( info.contains( "DON" ) );
        Assert.assertTrue( info.contains( "SHED" ) );

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

            Map<String, Object> virtualFields = imp.getDynamicProperties();
            Map<String, Object> wrapper = proxy.getFields();
            assertEquals( 3,
                          wrapper.size() );
            assertEquals( 1,
                          virtualFields.size() );

            impClass.set( imp,
                          "name",
                          "john" );
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

            Map virtualFields2 = ind.getDynamicProperties();
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

        } catch (Exception e) {
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

            Map<String, Object> virtualFields = imp.getDynamicProperties();
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

            Map<String, Object> virtualFields = imp.getDynamicProperties();
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

            Map virtualFields2 = ind.getDynamicProperties();
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

        assertEquals( 1, list.size() );
        assertEquals( 0, list.get( 0 ) );
        assertFalse( list.contains( 1 ) );


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
                "  name : String\n" +
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






}
