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

package org.drools.compiler.factmodel.traits;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
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

import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.drools.core.common.AbstractRuleBase;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.common.ObjectTypeConfigurationRegistry;
import org.drools.core.factmodel.traits.Entity;
import org.drools.core.factmodel.traits.LogicalTypeInconsistencyException;
import org.drools.core.factmodel.traits.MapWrapper;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.factmodel.traits.TraitFactory;
import org.drools.core.factmodel.traits.TraitProxy;
import org.drools.core.factmodel.traits.TraitableBean;
import org.drools.core.factmodel.traits.TripleBasedBean;
import org.drools.core.factmodel.traits.TripleBasedStruct;
import org.drools.core.factmodel.traits.VetoableTypedMap;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.io.impl.ByteArrayResource;
import org.drools.core.reteoo.ObjectTypeConf;
import org.junit.*;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.api.command.Command;
import org.kie.internal.command.CommandFactory;
import org.kie.api.definition.type.FactType;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.runtime.StatelessKnowledgeSession;
import org.kie.api.io.Resource;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.rule.FactHandle;
import org.mockito.ArgumentCaptor;

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
        return loadKnowledgeBase( ruleFiles ).newStatefulKnowledgeSession();
//        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
//        for (String file : ruleFiles) {
//            knowledgeBuilder.add( ResourceFactory.newClassPathResource( file ),
//                                  ResourceType.DRL );
//        }
//        if (knowledgeBuilder.hasErrors()) {
//            throw new RuntimeException( knowledgeBuilder.getErrors().toString() );
//        }
//
//        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
//        kbase.addKnowledgePackages( knowledgeBuilder.getKnowledgePackages() );
//
//        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
//        return session;
    }

    private StatefulKnowledgeSession getSessionFromString( String drl ) {
        return loadKnowledgeBaseFromString( drl ).newStatefulKnowledgeSession();
//        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
//        knowledgeBuilder.add( ResourceFactory.newByteArrayResource( drl.getBytes() ),
//                ResourceType.DRL );
//        if (knowledgeBuilder.hasErrors()) {
//            throw new RuntimeException( knowledgeBuilder.getErrors().toString() );
//        }
//
//        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
//        kbase.addKnowledgePackages( knowledgeBuilder.getKnowledgePackages() );
//
//        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
//        return session;
    }
//
//    private KnowledgeBase getKnowledgeBaseFromString( String drl ) {
//        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
//        knowledgeBuilder.add( ResourceFactory.newByteArrayResource( drl.getBytes() ),
//                ResourceType.DRL );
//        if (knowledgeBuilder.hasErrors()) {
//            throw new RuntimeException( knowledgeBuilder.getErrors().toString() );
//        }
//
//        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
//        kbase.addKnowledgePackages( knowledgeBuilder.getKnowledgePackages() );
//
//        return kbase;
//    }

    public void traitWrapGetAndSet( TraitFactory.VirtualPropertyMode mode ) {
        String source = "org/drools/compiler/factmodel/traits/testTraitDon.drl";

//        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
//        Resource res = ResourceFactory.newClassPathResource( source );
//        assertNotNull( res );
//        kbuilder.add( res,
//                      ResourceType.DRL );
//        if (kbuilder.hasErrors()) {
//            fail( kbuilder.getErrors().toString() );
//        }
//        KnowledgeBase kb = KnowledgeBaseFactory.newKnowledgeBase();
//            TraitFactory.setMode( mode, kb );
//        kb.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KnowledgeBase kb = loadKnowledgeBase(source);

        TraitFactory tFactory = ((AbstractRuleBase) ((KnowledgeBaseImpl) kb).getRuleBase()).getConfiguration().getComponentFactory().getTraitFactory();

        try {
            FactType impClass = kb.getFactType( "org.drools.compiler.trait.test",
                                                "Imp" );
            TraitableBean imp = (TraitableBean) impClass.newInstance();
            Class trait = kb.getFactType( "org.drools.compiler.trait.test",
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

    @Test(timeout = 10000)
    public void testTraitWrapper_GetAndSetTriple() {
        traitWrapGetAndSet( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test(timeout = 10000)
    public void testTraitWrapper_GetAndSetMap() {
        traitWrapGetAndSet( TraitFactory.VirtualPropertyMode.MAP );
    }

    public void traitShed( TraitFactory.VirtualPropertyMode mode ) {
        String source = "org/drools/compiler/factmodel/traits/testTraitShed.drl";

        StatefulKnowledgeSession ks = getSession( source );
        TraitFactory.setMode( mode, ks.getKieBase() );


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


    @Test(timeout = 10000)
    public void testTraitShedTriple() {
        traitShed( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test(timeout = 10000)
    public void testTraitShedMap() {
        traitShed( TraitFactory.VirtualPropertyMode.MAP );
    }

    public void traitDon( TraitFactory.VirtualPropertyMode mode ) {
        String source = "org/drools/compiler/factmodel/traits/testTraitDon.drl";

        StatefulKnowledgeSession ks = getSession( source );
        TraitFactory.setMode( mode, ks.getKieBase() );

        List info = new ArrayList();
        ks.setGlobal( "list",
                      info );

        ks.fireAllRules();

        Collection<? extends Object> wm = ks.getObjects();

        ks.insert( "die" );
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

    @Test(timeout = 10000)
    public void testTraitDonTriple() {
        traitDon( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test(timeout = 10000)
    public void testTraitDonMap() {
        traitDon( TraitFactory.VirtualPropertyMode.MAP );
    }





    public void mixin( TraitFactory.VirtualPropertyMode mode ) {
        String source = "org/drools/compiler/factmodel/traits/testTraitMixin.drl";

        StatefulKnowledgeSession ks = getSession( source );
        TraitFactory.setMode( mode, ks.getKieBase() );

        List info = new ArrayList();
        ks.setGlobal( "list",
                      info );

        ks.fireAllRules();

        assertTrue( info.contains( "27" ) );

    }

    @Test(timeout = 10000)
    public void testTraitMixinTriple() {
        mixin( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test(timeout = 10000)
    public void testTraitMxinMap() {
        mixin( TraitFactory.VirtualPropertyMode.MAP );
    }







    public void traitMethodsWithObjects( TraitFactory.VirtualPropertyMode mode ) {
        String source = "org/drools/compiler/factmodel/traits/testTraitWrapping.drl";

        StatefulKnowledgeSession ks = getSession( source );
        TraitFactory.setMode( mode, ks.getKieBase() );

        List errors = new ArrayList();
        ks.setGlobal( "list",
                      errors );

        ks.fireAllRules();

        if (!errors.isEmpty()) {
            System.err.println( errors.toString() );
        }
        Assert.assertTrue( errors.isEmpty() );

    }


    @Test(timeout = 10000)
    public void testTraitObjMethodsTriple() {
        traitMethodsWithObjects( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test(timeout = 10000)
    public void testTraitObjMethodsMap() {
        traitMethodsWithObjects( TraitFactory.VirtualPropertyMode.MAP );
    }





    public void traitMethodsWithPrimitives( TraitFactory.VirtualPropertyMode mode ) {
        String source = "org/drools/compiler/factmodel/traits/testTraitWrappingPrimitives.drl";

        StatefulKnowledgeSession ks = getSession( source );
        TraitFactory.setMode( mode, ks.getKieBase() );

        List errors = new ArrayList();
        ks.setGlobal( "list",
                      errors );

        ks.fireAllRules();

        if (!errors.isEmpty()) {
            System.err.println( errors );
        }
        Assert.assertTrue( errors.isEmpty() );

    }


    @Test(timeout = 10000)
    public void testTraitPrimMethodsTriple() {
        traitMethodsWithPrimitives( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test(timeout = 10000)
    public void testTraitPrimMethodsMap() {
        traitMethodsWithPrimitives( TraitFactory.VirtualPropertyMode.MAP );
    }








    public void traitProxy( TraitFactory.VirtualPropertyMode mode ) {

        String source = "org/drools/compiler/factmodel/traits/testTraitDon.drl";

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
            FactType impClass = kb.getFactType( "org.drools.compiler.trait.test",
                                                "Imp" );
            TraitableBean imp = (TraitableBean) impClass.newInstance();
            impClass.set( imp,
                          "name",
                          "aaa" );

            Class trait = kb.getFactType( "org.drools.compiler.trait.test",
                                          "Student" ).getFactClass();
            Class trait2 = kb.getFactType( "org.drools.compiler.trait.test",
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


    @Test(timeout = 10000)
    public void testTraitProxyTriple() {
        traitProxy( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test(timeout = 10000)
    public void testTraitProxyMap() {
        traitProxy( TraitFactory.VirtualPropertyMode.MAP );
    }









    public void wrapperSize( TraitFactory.VirtualPropertyMode mode ) {
        String source = "org/drools/compiler/factmodel/traits/testTraitDon.drl";

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
            FactType impClass = kb.getFactType( "org.drools.compiler.trait.test",
                                                "Imp" );
            TraitableBean imp = (TraitableBean) impClass.newInstance();
            FactType traitClass = kb.getFactType( "org.drools.compiler.trait.test",
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

            //            FactType indClass = kb.getFactType("org.drools.compiler.test","Entity");
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

            FactType traitClass2 = kb.getFactType( "org.drools.compiler.trait.test",
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


    @Test(timeout = 10000)
    public void testTraitWrapperSizeTriple() {
        wrapperSize( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test(timeout = 10000)
    public void testTraitWrapperSizeMap() {
        wrapperSize( TraitFactory.VirtualPropertyMode.MAP );
    }







    public void wrapperEmpty( TraitFactory.VirtualPropertyMode mode ) {
        String source = "org/drools/compiler/factmodel/traits/testTraitDon.drl";

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
            FactType impClass = kb.getFactType( "org.drools.compiler.trait.test",
                                                "Imp" );
            TraitableBean imp = (TraitableBean) impClass.newInstance();

            FactType studentClass = kb.getFactType( "org.drools.compiler.trait.test",
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

            //            FactType indClass = kb.getFactType("org.drools.compiler.test","Entity");
            TraitableBean ind = new Entity();

            FactType RoleClass = kb.getFactType( "org.drools.compiler.trait.test",
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

    @Test(timeout = 10000)
    public void testTraitWrapperEmptyTriple() {
        wrapperEmpty( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test(timeout = 10000)
    public void testTraitWrapperEmptyMap() {
        wrapperEmpty( TraitFactory.VirtualPropertyMode.MAP );
    }









    public void wrapperContainsKey( TraitFactory.VirtualPropertyMode mode ) {
        String source = "org/drools/compiler/factmodel/traits/testTraitDon.drl";

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
            FactType impClass = kb.getFactType( "org.drools.compiler.trait.test",
                                                "Imp" );
            TraitableBean imp = (TraitableBean) impClass.newInstance();
            impClass.set( imp,
                          "name",
                          "john" );

            FactType traitClass = kb.getFactType( "org.drools.compiler.trait.test",
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

            //            FactType indClass = kb.getFactType("org.drools.compiler.test","Entity");
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

            FactType traitClass2 = kb.getFactType( "org.drools.compiler.trait.test",
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

    @Test(timeout = 10000)
    public void testTraitContainskeyTriple() {
        wrapperContainsKey( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test(timeout = 10000)
    public void testTraitContainskeyMap() {
        wrapperContainsKey( TraitFactory.VirtualPropertyMode.MAP );
    }







    public void wrapperKeySetAndValues( TraitFactory.VirtualPropertyMode mode ) {
        String source = "org/drools/compiler/factmodel/traits/testTraitDon.drl";

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
            FactType impClass = kb.getFactType( "org.drools.compiler.trait.test",
                                                "Imp" );
            TraitableBean imp = (TraitableBean) impClass.newInstance();
            FactType traitClass = kb.getFactType( "org.drools.compiler.trait.test",
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

    @Test(timeout = 10000)
    public void testTraitWrapperKSVTriple() {
        wrapperKeySetAndValues( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test(timeout = 10000)
    public void testTraitWrapperKSVMap() {
        wrapperKeySetAndValues( TraitFactory.VirtualPropertyMode.MAP );
    }







    public void wrapperClearAndRemove( TraitFactory.VirtualPropertyMode mode ) {
        String source = "org/drools/compiler/factmodel/traits/testTraitDon.drl";

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
            FactType impClass = kb.getFactType( "org.drools.compiler.trait.test",
                                                "Imp" );
            TraitableBean imp = (TraitableBean) impClass.newInstance();
            impClass.set( imp,
                          "name",
                          "john" );
            FactType traitClass = kb.getFactType( "org.drools.compiler.trait.test",
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

    @Test(timeout = 10000)
    public void testTraitWrapperClearTriples() {
        wrapperClearAndRemove( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test(timeout = 10000)
    public void testTraitWrapperClearMap() {
        wrapperClearAndRemove( TraitFactory.VirtualPropertyMode.MAP );
    }







    public void isA( TraitFactory.VirtualPropertyMode mode ) {
        String source = "org/drools/compiler/factmodel/traits/testTraitIsA.drl";

        StatefulKnowledgeSession ks = getSession( source );
        TraitFactory.setMode( mode, ks.getKieBase() );

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

    @Test(timeout = 10000)
    @Ignore("problem during phreak work")
    public void testISATriple() {
        isA( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test(timeout = 10000)
    @Ignore("problem during phreak work")
    public void testISAMap() {
        isA( TraitFactory.VirtualPropertyMode.MAP );
    }

    public void overrideType( TraitFactory.VirtualPropertyMode mode ) {
        String source = "org/drools/compiler/factmodel/traits/testTraitOverride.drl";

        StatefulKnowledgeSession ks = getSession( source );
        TraitFactory.setMode( mode, ks.getKieBase() );

        List info = new ArrayList();
        ks.setGlobal( "list",
                      info );

        ks.fireAllRules();

        Collection wm = ks.getObjects();

        assertTrue( info.contains( "OK" ) );

    }

    @Test(timeout = 10000)
    public void testOverrideTriple() {
        overrideType( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test(timeout = 10000)
    public void testOverrideMap() {
        overrideType( TraitFactory.VirtualPropertyMode.MAP );
    }

    public void traitLegacy( TraitFactory.VirtualPropertyMode mode ) {
        String source = "org/drools/compiler/factmodel/traits/testTraitLegacyTrait.drl";

        StatefulKnowledgeSession ks = getSession( source );
        TraitFactory.setMode( mode, ks.getKieBase() );


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

    @Test(timeout = 10000)
    public void testLegacyTriple() {
        traitLegacy( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test(timeout = 10000)
    public void testLegacyMap() {
        traitLegacy( TraitFactory.VirtualPropertyMode.MAP );
    }

    public void traitCollections( TraitFactory.VirtualPropertyMode mode ) {
        String source = "org/drools/compiler/factmodel/traits/testTraitCollections.drl";

        StatefulKnowledgeSession ks = getSession( source );
        TraitFactory.setMode( mode, ks.getKieBase() );


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


    @Test(timeout = 10000)
    public void testCollectionsTriple() {
        traitCollections( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test(timeout = 10000)
    public void testCollectionsMap() {
        traitCollections( TraitFactory.VirtualPropertyMode.MAP );
    }







    public void traitCore( TraitFactory.VirtualPropertyMode mode ) {
        String source = "org/drools/compiler/factmodel/traits/testTraitLegacyCore.drl";

        StatefulKnowledgeSession ks = getSession( source );
        TraitFactory.setMode( mode, ks.getKieBase() );

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


    @Test(timeout = 10000)
    public void testCoreTriple() {
        traitCore( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test(timeout = 10000)
    public void testCoreMap() {
        traitCore( TraitFactory.VirtualPropertyMode.MAP );
    }

    public void traitWithEquality( TraitFactory.VirtualPropertyMode mode ) {
        String source = "org/drools/compiler/factmodel/traits/testTraitWithEquality.drl";

        StatefulKnowledgeSession ks = getSession( source );
        TraitFactory.setMode( mode, ks.getKieBase() );
        
        List info = new ArrayList();
        ks.setGlobal( "list",
                      info );

        ks.fireAllRules();

        Assert.assertTrue( info.contains( "DON" ) );
        Assert.assertTrue( info.contains( "EQUAL" ) );

    }

    @Test(timeout = 10000)
    public void testEqTriple() {
        traitWithEquality( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test(timeout = 10000)
    public void testEqMap() {
        traitWithEquality( TraitFactory.VirtualPropertyMode.MAP );
    }







    public void traitDeclared( TraitFactory.VirtualPropertyMode mode ) {

        List<Integer> trueTraits = new ArrayList<Integer>();
        List<Integer> untrueTraits = new ArrayList<Integer>();

        StatefulKnowledgeSession ks = getSession("org/drools/compiler/factmodel/traits/testDeclaredFactTrait.drl");
        TraitFactory.setMode( mode, ks.getKieBase() );
        
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

    @Test(timeout = 10000)
    public void testDeclaredTriple() {
        traitDeclared( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test(timeout = 10000)
    public void testDeclaredMap() {
        traitDeclared( TraitFactory.VirtualPropertyMode.MAP );
    }

    public void traitPojo( TraitFactory.VirtualPropertyMode mode ) {

        List<Integer> trueTraits = new ArrayList<Integer>();
        List<Integer> untrueTraits = new ArrayList<Integer>();

        StatefulKnowledgeSession session = getSession("org/drools/compiler/factmodel/traits/testPojoFactTrait.drl");
        TraitFactory.setMode( mode, session.getKieBase() );
        
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

    @Test(timeout = 10000)
    public void testPojoTriple() {
        traitPojo( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test(timeout = 10000)
    public void testPojoMap() {
        traitPojo( TraitFactory.VirtualPropertyMode.MAP );
    }







    public void isAOperator( TraitFactory.VirtualPropertyMode mode ) {
        String source = "org/drools/compiler/factmodel/traits/testTraitIsA2.drl";
        StatefulKnowledgeSession ksession = getSession( source );
        TraitFactory.setMode( mode, ksession.getKieBase() );
        
        
        AgendaEventListener ael = mock( AgendaEventListener.class );
        ksession.addEventListener( ael );

        Person student = new Person( "student", 18 );
        ksession.insert( student );

        ksession.fireAllRules();

        ArgumentCaptor<AfterMatchFiredEvent> cap = ArgumentCaptor.forClass( AfterMatchFiredEvent.class );
        verify( ael,
                times( 3 ) ).afterMatchFired(cap.capture());

        List<AfterMatchFiredEvent> values = cap.getAllValues();

        assertThat( values.get( 0 ).getMatch().getRule().getName(),
                    is( "create student" ) );
        assertThat( values.get( 1 ).getMatch().getRule().getName(),
                    is( "print student" ) );
        assertThat( values.get( 2 ).getMatch().getRule().getName(),
                    is( "print school" ) );

    }

    @Test(timeout = 10000)
    public void testISA2Triple() {
        isAOperator( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test(timeout = 10000)
    public void testISA2Map() {
        isAOperator( TraitFactory.VirtualPropertyMode.MAP );
    }





    protected void manyTraits( TraitFactory.VirtualPropertyMode mode ) {
        String source = "" +
                "import org.drools.compiler.Message;" +
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
        TraitFactory.setMode( mode, ksession.getKieBase() );
        

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        Person student = new Person( "student", 18 );
        ksession.insert( student );

        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertTrue( list.contains( "OK" ) );

    }

    @Test(timeout = 10000)
    public void testManyTraitsTriples() {
        manyTraits( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test(timeout = 10000)
    public void testManyTraitsMap() {
        manyTraits( TraitFactory.VirtualPropertyMode.MAP );
    }







    public void traitManyTimes( TraitFactory.VirtualPropertyMode mode ) {

        StatefulKnowledgeSession ksession = getSession("org/drools/compiler/factmodel/traits/testTraitDonMultiple.drl");
        TraitFactory.setMode( mode, ksession.getKieBase() );
        

        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        ksession.fireAllRules();

        System.out.println( "list" + list );
        assertEquals( 1, list.size() );
        assertEquals( 0, list.get( 0 ) );
        assertFalse( list.contains( 1 ) );


    }


    @Test(timeout = 10000)
    public void testManyTriple() {
        traitManyTimes( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test(timeout = 10000)
    public void testManyMap() {
        traitManyTimes( TraitFactory.VirtualPropertyMode.MAP );
    }






    // BZ #748752
    public void traitsInBatchExecution( TraitFactory.VirtualPropertyMode mode ) {
        String str = "package org.jboss.qa.brms.traits\n" +
                "import org.drools.compiler.Person;\n" +
                "import org.drools.core.factmodel.traits.Traitable;\n" +
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

    @Test(timeout = 10000)
    public void testBatchTriple() {
        traitsInBatchExecution( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test(timeout = 10000)
    public void testBatchMap() {
        traitsInBatchExecution( TraitFactory.VirtualPropertyMode.MAP );
    }







    public void manyTraitsStateless( TraitFactory.VirtualPropertyMode mode ) {
        String source = "" +
                "import org.drools.compiler.Message;" +
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
        KnowledgeBase kb = loadKnowledgeBaseFromString( source );
        TraitFactory.setMode( mode, kb );

        StatelessKnowledgeSession ksession = kb.newStatelessKnowledgeSession();

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.execute( CommandFactory.newFireAllRules() );

        assertEquals( 1, list.size() );
        assertTrue( list.contains( "OK" ) );

    }

    @Test(timeout = 10000)
    public void testManyStatelessTriple() {
        manyTraitsStateless( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test(timeout = 10000)
    public void testManyStatelessMap() {
        manyTraitsStateless( TraitFactory.VirtualPropertyMode.MAP );
    }





    public void aliasing( TraitFactory.VirtualPropertyMode mode ) {
        String drl = "package org.drools.traits\n" +
                "import org.drools.core.factmodel.traits.Traitable;\n" +
                "import org.drools.core.factmodel.traits.Alias;\n" +
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
        TraitFactory.setMode( mode, ksession.getKieBase() );

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

    @Test(timeout = 10000)
    public void testAliasingTriples() {
        aliasing( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test(timeout = 10000)
    public void testAliasingMap() {
        aliasing( TraitFactory.VirtualPropertyMode.MAP );
    }







    public void traitLogicalRemoval( TraitFactory.VirtualPropertyMode mode ) {
        String drl = "package org.drools.trait.test;\n" +
                "\n" +
                "import org.drools.core.factmodel.traits.Traitable;\n" +
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
        TraitFactory.setMode( mode, ksession.getKieBase() );

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        FactHandle h = ksession.insert( "trigger" );
        ksession.fireAllRules();
        assertEquals( 4, ksession.getObjects().size() );

        ksession.retract( h );
        ksession.fireAllRules();

        Collection col = ksession.getObjects();
        
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

    @Test(timeout = 10000)
    public void testLogicalRemovalTriples() {
        traitLogicalRemoval( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test(timeout = 10000)
    public void testLogicalRemovalMap() {
        traitLogicalRemoval( TraitFactory.VirtualPropertyMode.MAP );
    }



    @Test(timeout = 10000)
    public void testTMSConsistencyWithNonTraitableBeans() {

        String s1 = "package org.drools.compiler.test;\n" +
                "import org.drools.compiler.Person; \n" +
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



    public void traitWard( TraitFactory.VirtualPropertyMode mode ) {

        String s1 = "package org.drools.compiler.test;\n" +
                "import org.drools.compiler.Person; \n" +
                "import org.drools.core.factmodel.traits.Thing; \n" +
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
                "    Thing t = ward( $p, Student.class );\n" +
                "    Student s = (Student) don( t, Student.class );\n" +
                "end\n";



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

        for ( Object o : ksession.getObjects( new ClassObjectFilter( Thing.class ) ) ) {
            Thing t = (Thing) o;
            Object core = t.getCore();
            assertTrue( core instanceof Person );
            assertTrue( core instanceof TraitableBean );
            assertEquals( 1, ( (TraitableBean) core).getTraitMap().size() );
            assertTrue( ( (TraitableBean) core ).hasTrait( Thing.class.getName() ) );
        }

    }

    @Test(timeout = 10000)
    public void testTraitWardTriple() {
        traitWard( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test(timeout = 10000)
    public void testTraitWardMap() {
        traitWard( TraitFactory.VirtualPropertyMode.MAP );
    }



    public void traitGrant( TraitFactory.VirtualPropertyMode mode ) {

        String s1 = "package org.drools.compiler.test;\n" +
                "import org.drools.compiler.Person; \n" +
                "import org.drools.core.factmodel.traits.Thing; \n" +
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
                "no-loop\n" +
                "when\n" +
                "    $p : Person( )\n" +
                "then\n" +
                "    Thing t = ward( $p, Student.class );\n" +
                "    Student s = (Student) don( t, Student.class );\n" +
                "       grant( t, Student.class ); \n" +
                "   System.out.println( \"HERE WE GO \"); \n" +
                "    s = (Student) don( t, Student.class );\n" +
                "end\n";



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

        for ( Object o : ksession.getObjects( new ClassObjectFilter( Thing.class ) ) ) {
            Thing t = (Thing) o;
            Object core = t.getCore();
            assertTrue( core instanceof Person );
            assertTrue( core instanceof TraitableBean );
            assertEquals( 2, ( (TraitableBean) core).getTraitMap().size() );
            assertTrue( ( (TraitableBean) core ).hasTrait( Thing.class.getName() ) );
        }

    }

    @Test(timeout = 10000)
    public void testTraitGrantTriple() {
        traitGrant( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test(timeout = 10000)
    public void testTraitGrantMap() {
        traitGrant( TraitFactory.VirtualPropertyMode.MAP );
    }


    @Test(timeout = 10000)
    public void testInternalComponentsMap(  ) {
          String source = "org/drools/compiler/factmodel/traits/testTraitDon.drl";

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
              FactType impClass = kb.getFactType( "org.drools.compiler.trait.test",
                                                  "Imp" );
              TraitableBean imp = (TraitableBean) impClass.newInstance();
              FactType traitClass = kb.getFactType( "org.drools.compiler.trait.test",
                                                    "Student" );
              Class trait = traitClass.getFactClass();
              TraitProxy proxy = (TraitProxy) tFactory.getProxy( imp,
                                                                     trait );
              Object proxyFields = proxy.getFields();
              Object coreTraits = imp.getTraitMap();
              Object coreProperties = imp.getDynamicProperties();

              assertTrue( proxy.getObject() instanceof TraitableBean );

              assertNotNull( proxyFields );
              assertNotNull( coreTraits );
              assertNotNull( coreProperties );

              assertTrue( proxyFields instanceof MapWrapper);
              assertTrue( coreTraits instanceof VetoableTypedMap);
              assertTrue( coreProperties instanceof HashMap);


              StudentProxy2 sp2 = new StudentProxy2( new Imp2(), null );
              System.out.println( sp2.toString() );

          } catch ( Exception e ) {
              e.printStackTrace();
              fail( e.getMessage() );
          }
    }


    @Test(timeout = 10000)
    public void testInternalComponentsTriple(  ) {
        String source = "org/drools/compiler/factmodel/traits/testTraitDon.drl";

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
            FactType impClass = kb.getFactType( "org.drools.compiler.trait.test",
                    "Imp" );
            TraitableBean imp = (TraitableBean) impClass.newInstance();
            FactType traitClass = kb.getFactType( "org.drools.compiler.trait.test",
                    "Student" );
            Class trait = traitClass.getFactClass();
            TraitProxy proxy = (TraitProxy) tFactory.getProxy( imp,
                    trait );
            Object proxyFields = proxy.getFields();
            Object coreTraits = imp.getTraitMap();
            Object coreProperties = imp.getDynamicProperties();

            assertTrue( proxy.getObject() instanceof TraitableBean );

            assertNotNull( proxyFields );
            assertNotNull( coreTraits );
            assertNotNull( coreProperties );

            assertEquals("org.drools.compiler.trait.test.StudentorgdroolscompilertraittestImpProxyWrapper", proxyFields.getClass().getName());

            assertTrue(proxyFields instanceof TripleBasedStruct);
            assertTrue( coreTraits instanceof VetoableTypedMap );
            assertTrue( coreProperties instanceof TripleBasedBean);


        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );

        }
    }



    public void traitWardOnHierarchy( TraitFactory.VirtualPropertyMode mode ) {

        String s1 = "package org.drools.compiler.test;\n" +
                "import org.drools.compiler.Person; \n" +
                "import org.drools.core.factmodel.traits.Thing; \n" +
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
                "declare trait UniversityStudent extends Student\n" +
                "  uni  : String\n" +
                "end\n" +
                "declare trait PhDStudent extends UniversityStudent\n" +
                "  year : int\n" +
                "end\n" +
                "\n" +
                "rule \"Trait\"\n" +
                "no-loop\n" +
                "when\n" +
                "    $p : Person( )\n" +
                "then\n" +
                "    Thing t = ward( $p, UniversityStudent.class );\n" +
                "    grant( t, PhDStudent.class );\n" +
                "    Student s = (Student) don( t, Student.class );\n" +
                "    UniversityStudent u = (UniversityStudent) don( t, UniversityStudent.class );\n" +
                "    PhDStudent p = (PhDStudent) don( t, PhDStudent.class );\n" +
                "end\n";



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

        for ( Object o : ksession.getObjects( new ClassObjectFilter( Thing.class ) ) ) {
            Thing t = (Thing) o;
            Object core = t.getCore();
            assertTrue( core instanceof Person );
            assertTrue( core instanceof TraitableBean );
            assertEquals( 3, ( (TraitableBean) core).getTraitMap().size() );
            assertTrue( ( (TraitableBean) core ).hasTrait( Thing.class.getName() ) );
        }

    }

    @Test(timeout = 10000)
    public void testTraitWardOnHierarchyTriple() {
        traitWardOnHierarchy( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test(timeout = 10000)
    public void testTraitWardOnHierarchyMap() {
        traitWardOnHierarchy( TraitFactory.VirtualPropertyMode.MAP );
    }







    public void traitsLegacyWrapperCoherence( TraitFactory.VirtualPropertyMode mode ) {
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
        TraitFactory.setMode( mode, ksession.getKieBase() );
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


        FactType tBeanType = kbase.getFactType( "org.drools.trait.test", "TBean" );
        assertNotNull( tBeanType );

        assertSame( tBeanType.getFactClass(), coreOld.getClass().getSuperclass() );

        assertEquals( "abc", tBeanType.get( coreOld, "fld" ) );
        assertEquals( 1, coreOld.getDynamicProperties().size() );
        assertEquals( 2, coreOld.getTraitMap().size() );
    }


    @Test(timeout = 10000)
    public void testTraitsBeanWrapperDataStructuresTriples() {
        traitsLegacyWrapperCoherence( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test(timeout = 10000)
    public void testTraitsBeanWrapperDataStructuresMap() {
        traitsLegacyWrapperCoherence( TraitFactory.VirtualPropertyMode.MAP );
    }








    public void traitRedundancy( TraitFactory.VirtualPropertyMode mode ) {
        String str = "package org.drools.compiler.factmodel.traits; \n" +
                "global java.util.List list; \n" +
                "" +
                "declare trait IStudent end \n" +
                "" +
                "declare IPerson @typesafe(false) end \n" +
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
        TraitFactory.setMode( mode, kbase );
        List<?> list = new ArrayList<Object>();

        ksession.setGlobal("list",
                list);

        ksession.insert( new StudentImpl( "skool", "john", 27 ) );


        assertEquals( 3, ksession.fireAllRules() );

        for ( Object o : ksession.getObjects() ) {
            System.err.println( o );
        }

    }


    @Test(timeout = 10000)
    public void testTraitRedundancyTriples() {
        traitRedundancy(TraitFactory.VirtualPropertyMode.TRIPLES);
    }

    @Test(timeout = 10000)
    public void testTraitRedundancyMap() {
        traitRedundancy(TraitFactory.VirtualPropertyMode.MAP);
    }




    public void traitSimpleTypes( TraitFactory.VirtualPropertyMode mode ) {

        String s1 = "package org.drools.core.factmodel.traits;\n" +
                "\n" +
                "declare trait PassMark\n" +
                "end\n" +
                "\n" +
                "declare ExamMark \n" +
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

    @Test(timeout = 10000)
    public void testTraitWithSimpleTypesTriples() {
        traitSimpleTypes( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test(timeout = 10000)
    public void testTraitWithSimpleTypesMap() {
        traitSimpleTypes( TraitFactory.VirtualPropertyMode.MAP );
    }





}
