/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.drools.core.ObjectFilter;
import org.drools.core.base.ClassObjectType;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.common.ObjectTypeConfigurationRegistry;
import org.drools.core.factmodel.traits.Entity;
import org.drools.core.factmodel.traits.LogicalTypeInconsistencyException;
import org.drools.core.factmodel.traits.MapWrapper;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.factmodel.traits.Trait;
import org.drools.core.factmodel.traits.TraitFactory;
import org.drools.core.factmodel.traits.TraitProxy;
import org.drools.core.factmodel.traits.TraitRegistry;
import org.drools.core.factmodel.traits.TraitTypeMap;
import org.drools.core.factmodel.traits.Traitable;
import org.drools.core.factmodel.traits.TraitableBean;
import org.drools.core.factmodel.traits.TripleBasedBean;
import org.drools.core.factmodel.traits.TripleBasedStruct;
import org.drools.core.factmodel.traits.VirtualPropertyMode;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.io.impl.ByteArrayResource;
import org.drools.core.io.impl.ClassPathResource;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.RuleTerminalNodeLeftTuple;
import org.drools.core.rule.EntryPointId;
import org.drools.core.util.CodedHierarchyImpl;
import org.drools.core.util.HierarchyEncoder;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.command.Command;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.KieBaseOption;
import org.kie.api.definition.type.FactType;
import org.kie.api.definition.type.PropertyReactive;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.DebugAgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatelessKnowledgeSession;
import org.kie.internal.utils.KieHelper;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class TraitTest extends CommonTestMethodBase {

    private static long t0;


    public VirtualPropertyMode mode;

    @Parameterized.Parameters
    public static Collection modes() {
        return Arrays.asList( new VirtualPropertyMode[][]
                                      {
                                              { VirtualPropertyMode.MAP },
                                              { VirtualPropertyMode.TRIPLES }
                                      } );
    }

    public TraitTest( VirtualPropertyMode m ) {
        this.mode = m;
    }

    private KieSession getSession( String... ruleFiles ) {
        KieHelper kieHelper = new KieHelper();
        for (String file : ruleFiles) {
            kieHelper.kfs.write( new ClassPathResource( file ) );
        }
        return kieHelper.build().newKieSession();
    }

    private KieSession getSessionFromString( String drl ) {
        return new KieHelper().addContent( drl, ResourceType.DRL ).build().newKieSession();
    }

    private KieBase getKieBaseFromString( String drl, KieBaseOption... options ) {
        return new KieHelper().addContent( drl, ResourceType.DRL ).build(options);
    }


    @Test
    public void testRetract( ) {
        String drl = "package org.drools.compiler.trait.test; \n" +
                "import org.drools.core.factmodel.traits.Traitable; \n" +
                "" +
                "declare Foo @Traitable end\n" +
                "declare trait Bar end \n" +
                "" +
                "rule Init when then\n" +
                "  Foo foo = new Foo(); \n" +
                "  don( foo, Bar.class ); \n" +
                "end\n" +
                "rule Retract \n" +
                "when\n" +
                " $bar : Bar()\n" +
                "then\n" +
                "  delete( $bar ); \n" +
                "end\n";

        KieSession ks = getSessionFromString( drl );
        TraitFactory.setMode(mode, ks.getKieBase());

        assertEquals(2, ks.fireAllRules());

        for(Object o : ks.getObjects()) {
            System.out.println(o);
        }

        assertEquals(0, ks.getObjects().size());
    }


    @Test(timeout=10000)
    public void testTraitWrapGetAndSet() {
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
        TraitFactory.setMode( mode, kb );
        kb.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        TraitFactory tFactory = ((KnowledgeBaseImpl) kb).getConfiguration().getComponentFactory().getTraitFactory();

        try {
            FactType impClass = kb.getFactType( "org.drools.compiler.trait.test",
                                                "Imp" );
            TraitableBean imp = (TraitableBean) impClass.newInstance();
            Class trait = kb.getFactType( "org.drools.compiler.trait.test",
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



    @Test(timeout=10000)
    public void testTraitShed() {
        String source = "org/drools/compiler/factmodel/traits/testTraitShed.drl";

        KieSession ks = getSession( source );
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



    @Test(timeout=10000)
    public void testTraitDon() {
        String source = "org/drools/compiler/factmodel/traits/testTraitDon.drl";

        KieSession ks = getSession( source );
        TraitFactory.setMode( mode, ks.getKieBase() );

        List info = new ArrayList();
        ks.setGlobal( "list",
                      info );

        ks.fireAllRules();

        Collection<? extends Object> wm = ks.getObjects();

        ks.insert( "go" );
        ks.fireAllRules();

        assertTrue( info.contains( "DON" ) );
        assertTrue( info.contains( "SHED" ) );

        Iterator it = wm.iterator();
        Object x = it.next();
        if ( x instanceof String ) {
            x = it.next();
        }

        System.out.println( x.getClass() );
        System.out.println( x.getClass().getSuperclass() );
        System.out.println( Arrays.asList( x.getClass().getInterfaces() ));
    }





    @Test(timeout=10000)
    public void testMixin() {
        String source = "org/drools/compiler/factmodel/traits/testTraitMixin.drl";

        KieSession ks = getSession( source );
        TraitFactory.setMode( mode, ks.getKieBase() );

        List info = new ArrayList();
        ks.setGlobal( "list",
                      info );

        ks.fireAllRules();

        assertTrue( info.contains( "27" ) );
    }


    @Test(timeout=10000)
    public void traitMethodsWithObjects() {
        String source = "org/drools/compiler/factmodel/traits/testTraitWrapping.drl";

        KieSession ks = getSession( source );
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


    @Test(timeout=10000)
    public void traitMethodsWithPrimitives() {
        String source = "org/drools/compiler/factmodel/traits/testTraitWrappingPrimitives.drl";

        KieSession ks = getSession( source );
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


    @Test(timeout=10000)
    public void testTraitProxy() {

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
        TraitFactory tFactory = ((KnowledgeBaseImpl) kb).getConfiguration().getComponentFactory().getTraitFactory();

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
        } catch ( LogicalTypeInconsistencyException e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }


    @Test(timeout=10000)
    public void testWrapperSize() {
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
        TraitFactory tFactory = ((KnowledgeBaseImpl) kb).getConfiguration().getComponentFactory().getTraitFactory();


        try {
            FactType impClass = kb.getFactType( "org.drools.compiler.trait.test",
                                                "Imp" );
            TraitableBean imp = (TraitableBean) impClass.newInstance();
            FactType traitClass = kb.getFactType( "org.drools.compiler.trait.test",
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

            //            FactType indClass = kb.getFactType("org.drools.compiler.trait.test","Entity");
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

        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }

    }



    @Test(timeout=10000)
    public void testWrapperEmpty() {
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

        TraitFactory tFactory = ((KnowledgeBaseImpl) kb).getConfiguration().getComponentFactory().getTraitFactory();

        try {
            FactType impClass = kb.getFactType( "org.drools.compiler.trait.test",
                                                "Imp" );
            TraitableBean imp = (TraitableBean) impClass.newInstance();

            FactType studentClass = kb.getFactType( "org.drools.compiler.trait.test",
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

            //            FactType indClass = kb.getFactType("org.drools.compiler.trait.test","Entity");
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



    @Test(timeout=10000)
    public void testWrapperContainsKey() {
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
        TraitFactory tFactory = ((KnowledgeBaseImpl) kb).getConfiguration().getComponentFactory().getTraitFactory();

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

            //            FactType indClass = kb.getFactType("org.drools.compiler.trait.test","Entity");
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


    @Test(timeout=10000)
    public void testInternalComponents1(  ) {
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
        TraitFactory tFactory = ((KnowledgeBaseImpl) kb).getConfiguration().getComponentFactory().getTraitFactory();


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
            Object coreTraits = imp._getTraitMap();
            Object coreProperties = imp._getDynamicProperties();

            assertTrue( proxy.getObject() instanceof TraitableBean );

            assertNotNull( proxyFields );
            assertNotNull( coreTraits );
            assertNotNull( coreProperties );

            if ( mode == VirtualPropertyMode.MAP ) {
                assertTrue( proxyFields instanceof MapWrapper );
                assertTrue( coreTraits instanceof TraitTypeMap );
                assertTrue( coreProperties instanceof HashMap );
            } else {
                assertEquals( "org.drools.compiler.trait.test.Student.org.drools.compiler.trait.test.Imp_ProxyWrapper", proxyFields.getClass().getName() );

                assertTrue(proxyFields instanceof TripleBasedStruct );
                assertTrue( coreTraits instanceof TraitTypeMap);
                assertTrue( coreProperties instanceof TripleBasedBean );
            }


            StudentProxy2 sp2 = new StudentProxy2( new Imp2(), null );
            System.out.println( sp2.toString() );

        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }




    @Test(timeout=10000)
    public void testWrapperKeySetAndValues() {
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

        TraitFactory tFactory = ((KnowledgeBaseImpl) kb).getConfiguration().getComponentFactory().getTraitFactory();

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



    @Test(timeout=10000)
    public void testWrapperClearAndRemove() {
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
        TraitFactory tFactory = ((KnowledgeBaseImpl) kb).getConfiguration().getComponentFactory().getTraitFactory();

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





    @Test(timeout=10000)
    public void testIsAEvaluator( ) {
        String source = "package org.drools.compiler.trait.test;\n" +
                        "\n" +
                        "import org.drools.core.factmodel.traits.Traitable;\n" +
                        "import org.drools.core.factmodel.traits.Entity;\n" +
                        "import org.drools.core.factmodel.traits.Thing;\n" +
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

        KieSession ks = getSessionFromString( source );
        TraitFactory.setMode( mode, ks.getKieBase() );

        List info = new ArrayList();
        ks.setGlobal( "list",
                      info );

        ks.fireAllRules();

        System.out.println( info );
        assertTrue( info.contains( "ok" ) );
    }



    @Test(timeout=10000)
    public void testIsA() {
        String source = "org/drools/compiler/factmodel/traits/testTraitIsA.drl";

        KieSession ks = getSession( source );
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



    @Test(timeout=10000)
    public void testOverrideType() {
        String source = "org/drools/compiler/factmodel/traits/testTraitOverride.drl";

        KieSession ks = getSession( source );
        TraitFactory.setMode( mode, ks.getKieBase() );

        List info = new ArrayList();
        ks.setGlobal( "list",
                      info );

        try {
            ks.fireAllRules();
            fail( "An exception was expected since a trait can't override the type of a core class field with these settings " );
        } catch ( Throwable rde ) {
            assertTrue( rde.getCause() instanceof UnsupportedOperationException );
        }
    }




    @Test(timeout=10000)
    public void testOverrideType2( ) {
        String drl = "package org.drools.compiler.trait.test; \n" +
                     "import org.drools.core.factmodel.traits.Traitable; \n" +
                     "" +
                     "declare Foo @Traitable end\n" +
                     "declare trait Bar end \n" +
                     "" +
                     "declare trait Mask fld : Foo end \n" +
                     "declare Face @Traitable fld : Bar end \n" +
                     "" +
                     "rule Don when then\n" +
                     "  Face face = new Face(); \n" +
                     "  don( face, Mask.class ); \n" +
                     "end\n";

        KieSession ks = getSessionFromString( drl );
        TraitFactory.setMode( mode, ks.getKieBase() );

        try {
            ks.fireAllRules();
            fail( "An exception was expected since a trait can't override the type of a core class field with these settings " );
        } catch ( Throwable rde ) {
            assertTrue( rde.getCause() instanceof UnsupportedOperationException );
        }
    }



    @Test(timeout=10000)
    public void testOverrideType3( ) {
        String drl = "package org.drools.compiler.trait.test; \n" +
                     "import org.drools.core.factmodel.traits.Traitable; \n" +
                     "" +
                     "declare trait Foo end\n" +
                     "declare trait Bar end \n" +
                     "" +
                     "declare trait Mask fld : Foo end \n" +
                     "declare Face @Traitable fld : Bar end \n" +
                     "" +
                     "rule Don when then\n" +
                     "  Face face = new Face(); \n" +
                     "  don( face, Mask.class ); \n" +
                     "end\n";

        KieSession ks = getSessionFromString( drl );
        TraitFactory.setMode( mode, ks.getKieBase() );

        try {
            ks.fireAllRules();
            fail( "An exception was expected since a trait can't override the type of a core class field with these settings " );
        } catch ( Throwable rde ) {
            assertTrue( rde.getCause() instanceof UnsupportedOperationException );
        }
    }


    @Test(timeout=10000)
    public void testTraitLegacy() {
        String source = "org/drools/compiler/factmodel/traits/testTraitLegacyTrait.drl";

        KieSession ks = getSession( source );
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




    @Test(timeout=10000)
    public void testTraitCollections() {
        String source = "org/drools/compiler/factmodel/traits/testTraitCollections.drl";

        KieSession ks = getSession( source );
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




    @Test()
    public void testTraitCore() {
        String source = "org/drools/compiler/factmodel/traits/testTraitLegacyCore.drl";

        KieSession ks = getSession( source );
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




    @Test(timeout=10000)
    public void traitWithEquality() {
        String source = "org/drools/compiler/factmodel/traits/testTraitWithEquality.drl";

        KieSession ks = getSession( source );
        TraitFactory.setMode( mode, ks.getKieBase() );

        List info = new ArrayList();
        ks.setGlobal( "list",
                      info );

        ks.fireAllRules();

        Assert.assertTrue( info.contains( "DON" ) );
        Assert.assertTrue( info.contains( "EQUAL" ) );

    }



    @Test(timeout=10000)
    public void traitDeclared() {

        List<Integer> trueTraits = new ArrayList<Integer>();
        List<Integer> untrueTraits = new ArrayList<Integer>();

        KieSession ks = getSession( "org/drools/compiler/factmodel/traits/testDeclaredFactTrait.drl" );
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



    @Test(timeout=10000)
    public void traitPojo() {

        List<Integer> trueTraits = new ArrayList<Integer>();
        List<Integer> untrueTraits = new ArrayList<Integer>();

        KieSession session = getSession( "org/drools/compiler/factmodel/traits/testPojoFactTrait.drl" );
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




    @Test(timeout=10000)
    public void testIsAOperator() {
        String source = "org/drools/compiler/factmodel/traits/testTraitIsA2.drl";
        KieSession ksession = getSession( source );
        TraitFactory.setMode( mode, ksession.getKieBase() );


        AgendaEventListener ael = mock( AgendaEventListener.class );
        ksession.addEventListener( ael );

        Person student = new Person( "student", 18 );
        ksession.insert( student );

        ksession.fireAllRules();

        ArgumentCaptor<AfterMatchFiredEvent> cap = ArgumentCaptor.forClass( AfterMatchFiredEvent.class );
        verify( ael,
                times( 3 ) ).afterMatchFired( cap.capture() );

        List<AfterMatchFiredEvent> values = cap.getAllValues();

        assertThat( values.get( 0 ).getMatch().getRule().getName(),
                    is( "create student" ) );
        assertThat( values.get( 1 ).getMatch().getRule().getName(),
                    is( "print student" ) );
        assertThat( values.get( 2 ).getMatch().getRule().getName(),
                    is( "print school" ) );

    }



    @Test(timeout=10000)
    public void testManyTraits() {
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
        KieSession ksession = getSessionFromString( source );
        TraitFactory.setMode( mode, ksession.getKieBase() );


        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        Person student = new Person( "student", 18 );
        ksession.insert( student );

        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertTrue( list.contains( "OK" ) );

    }


    @Test(timeout=10000)
    public void traitManyTimes() {

        KieSession ksession = getSession( "org/drools/compiler/factmodel/traits/testTraitDonMultiple.drl" );
        TraitFactory.setMode( mode, ksession.getKieBase() );

        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        ksession.fireAllRules();

        for ( Object o : ksession.getObjects() ) {
            System.err.println( o );
        }
        Collection x = ksession.getObjects();
        assertEquals( 2, ksession.getObjects().size() );

        assertEquals( 5, list.size() );
        assertEquals( 0, list.get( 0 ) );
        assertTrue( list.contains( 1 ) );
        assertTrue( list.contains( 2 ) );
        assertTrue( list.contains( 3 ) );
        assertTrue( list.contains( 4 ) );


    }



    // BZ #748752
    @Test(timeout=10000)
    public void traitsInBatchExecution() {
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
        commands.add( CommandFactory.newInsert( student ));

        System.out.println("Starting execution...");
        commands.add(CommandFactory.newFireAllRules());
        ksession.execute(CommandFactory.newBatchExecution(commands));

        System.out.println("Finished...");

        assertEquals( 2, list.size() );
        assertTrue( list.contains( 1 ) );
        assertTrue( list.contains( 2 ) );
    }





    @Test(timeout=10000)
    public void testManyTraitsStateless() {
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
        KieBase kb = getKieBaseFromString( source );
        TraitFactory.setMode( mode, kb );

        KieSession ksession = kb.newKieSession();

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.execute( CommandFactory.newFireAllRules() );

        assertEquals( 1, list.size() );
        assertTrue( list.contains( "OK" ) );

    }



    @Test(timeout=10000)
    public void testAliasing() {
        String drl = "package org.drools.traits\n" +
                     "import org.drools.core.factmodel.traits.Traitable;\n" +
                     "import org.drools.core.factmodel.traits.Alias;\n" +
                     "" +
                     "global java.util.List list;" +
                     "" +
                     "declare Person \n" +
                     "  @Traitable \n" +
                     "  nomen     : String  @key @Alias(\"fld1\") \n" +
                     "  workPlace : String \n" +
                     "  address   : String \n" +
                     "  serviceYrs: int \n" +
                     "end \n" +
                     "" +
                     "declare trait Student\n" +
                     // this alias maps to the hard field
                     "  name      : String @Alias(\"fld1\") \n" +
                     // this alias works, binding school to workPlace
                     "  school    : String  @Alias(\"workPlace\") \n" +
                     // soft field, will use name 'level'
                     "  grade     : int @Alias(\"level\") \n" +
                     // this will try to bind rank to address
                     "  rank      : int @Alias(\"serviceYrs\") \n" +
                     "end \n" +
                     "\n" +
                     "rule \"create student\" \n" +
                     "  when\n" +
                     "  then\n" +
                     "    Person p = new Person( \"davide\", \"UniBoh\", \"Floor84\", 1 ); \n" +
                     "    Student s = don( p, Student.class );\n" +
                     "end\n" +
                     "\n" +
                     "rule \"print school\"\n" +
                     "  when\n" +
                     "    $student : Student( $school : school == \"UniBoh\",  $f : fields, fields[ \"workPlace\" ] == \"UniBoh\" )\n" +
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

        KieSession ksession = getSessionFromString( drl );
        TraitFactory.setMode( mode, ksession.getKieBase() );

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        assertEquals( 9, list.size() );
        assertTrue( list.contains( "UniBoh" ) );
        assertTrue( list.contains( "Skool" ) );
        assertTrue( ( (Collection) list.get(3) ).containsAll( Arrays.asList( "workPlace", "nomen", "level" ) ) );
        assertTrue( ( (Collection) list.get(5) ).containsAll( Arrays.asList( "davide", "Skool", 0 ) ) );
        assertTrue( list.contains( true ) );
        assertTrue( list.contains( "Floor84" ) );
        assertTrue( list.contains( 99 ) );

    }



    @Test(timeout=10000)
    public void testTraitLogicalRemoval() {
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


        KieSession ksession = getSessionFromString(drl);
        TraitFactory.setMode( mode, ksession.getKieBase() );

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        FactHandle h = ksession.insert( "trigger" );
        ksession.fireAllRules();
        assertEquals( 3, ksession.getObjects().size() );

        ksession.delete( h );
        ksession.fireAllRules();

        assertEquals( 0, ksession.getObjects().size() );

        FactHandle h1 = ksession.insert( "trigger" );
        FactHandle h2 = ksession.insert( "trigger2" );
        ksession.fireAllRules();

        assertEquals( 5, ksession.getObjects().size() );

        ksession.delete( h2 );
        ksession.fireAllRules();

        assertEquals( 3, ksession.getObjects().size() );

        ksession.delete( h1 );
        ksession.fireAllRules();

        assertEquals( 0, ksession.getObjects().size() );

    }


    @Test(timeout=10000)
    public void testTMSConsistencyWithNonTraitableBeans() {

        String s1 = "package org.drools.test;\n" +
                    "import org.drools.compiler.Person; \n" +
                    "import org.drools.core.factmodel.traits.Traitable; \n" +
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

        KieSession ksession = kbase.newKieSession();
        TraitFactory.setMode( mode, ksession.getKieBase() );

        ksession.fireAllRules();

        FactHandle personHandle = ksession.getFactHandles( new ClassObjectFilter( Person.class ) ).iterator().next();
        InternalFactHandle h = ((InternalFactHandle) personHandle);
        ObjectTypeConfigurationRegistry reg = h.getEntryPoint().getObjectTypeConfigurationRegistry();
        ObjectTypeConf conf = reg.getObjectTypeConf( h.getEntryPoint().getEntryPoint(), ((InternalFactHandle) personHandle).getObject() );
        assertTrue( conf.isTMSEnabled() );

        ksession.dispose();
    }






    public static class TBean {
        private String fld;
        public String getFld() { return fld; }
        public void setFld( String fld ) { this.fld = fld; }
        public TBean( String fld ) { this.fld = fld; }
    }



    @Test(timeout=10000)
    public void testTraitsLegacyWrapperCoherence() {
        String str = "package org.drools.trait.test; \n" +
                     "global java.util.List list; \n" +
                     "import org.drools.core.factmodel.traits.Traitable;\n" +
                     "import org.drools.compiler.factmodel.traits.TraitTest.TBean;\n" +
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

        KieSession ksession = kbase.newKieSession();
        TraitFactory.setMode( mode, ksession.getKieBase() );
        List<?> list = new ArrayList<Object>();

        ksession.setGlobal("list",
                           list);

        ksession.fireAllRules();


        Collection yOld = ksession.getObjects();
        assertEquals( 2, yOld.size() );

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
        assertEquals( 1, coreOld._getTraitMap().size() );
    }



    @Test(timeout=10000)
    public void testHasTypes() {

        String source = "org/drools/compiler/factmodel/traits/testTraitDon.drl";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Resource res = ResourceFactory.newClassPathResource( source );
        assertNotNull(res);
        kbuilder.add(res, ResourceType.DRL);
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kb = KnowledgeBaseFactory.newKnowledgeBase();
        kb.addKnowledgePackages(kbuilder.getKnowledgePackages());
        TraitFactory traitBuilder = ((KnowledgeBaseImpl) kb).getConfiguration().getComponentFactory().getTraitFactory();
        TraitFactory.setMode( mode, kb );

        try {
            FactType impClass = kb.getFactType("org.drools.compiler.trait.test","Imp");
            TraitableBean imp = (TraitableBean) impClass.newInstance();
            impClass.set(imp, "name", "aaabcd");

            Class trait = kb.getFactType("org.drools.compiler.trait.test","Student").getFactClass();
            Class trait2 = kb.getFactType("org.drools.compiler.trait.test","Role").getFactClass();

            assertNotNull( trait);

            TraitProxy proxy = (TraitProxy) traitBuilder.getProxy(imp, trait);
            Thing thing = traitBuilder.getProxy(imp, Thing.class);

            TraitableBean core = (TraitableBean) proxy.getObject();


            TraitProxy proxy2 = (TraitProxy) traitBuilder.getProxy(imp, trait);
            Thing thing2 = traitBuilder.getProxy(imp, Thing.class);

            assertSame(proxy,proxy2);
            assertSame(thing,thing2);

            assertEquals(2, core.getTraits().size());


        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }






    @Test(timeout=10000)
    public void testTraitRedundancy() {
        String str = "package org.drools.compiler.factmodel.traits; \n" +
                     "global java.util.List list; \n" +
                     "" +
                     "declare trait IStudent end \n" +
                     "" +
                     "declare org.drools.compiler.factmodel.traits.IPerson @typesafe(false) end \n" +
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

        KieSession ksession = kbase.newKieSession();
        TraitFactory.setMode( mode, ksession.getKieBase() );
        List<?> list = new ArrayList<Object>();

        ksession.setGlobal("list",
                           list);

        ksession.insert( new StudentImpl( "skool", "john", 27 ) );


        assertEquals( 3, ksession.fireAllRules() );

        for ( Object o : ksession.getObjects() ) {
            System.err.println( o );
        }

    }


    @Test(timeout=10000)
    public void traitSimpleTypes() {

        String s1 = "package org.drools.factmodel.traits;\n" +
                    "\n" +
                    "import org.drools.core.factmodel.traits.Traitable;\n" +
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

        KieSession ksession = kbase.newKieSession();
        ksession.fireAllRules();

        for ( Object o : ksession.getObjects() ) {
            if ( o instanceof TraitableBean ) {
                TraitableBean tb = (TraitableBean) o;
                assertEquals( 1, tb._getTraitMap().size() );
                BitSet bs = new BitSet();
                bs.set( 0 );
                assertEquals( bs, tb.getCurrentTypeCode() );
            }
            if ( o instanceof TraitProxy ) {
                TraitProxy tp = (TraitProxy) o;
                assertEquals( 0, tp.listAssignedOtnTypeCodes().size() );
            }
        }
    }



    @Test(timeout=10000)
    public void testTraitEncoding() {
        String s1 = "package org.drools.core.factmodel.traits;\n" +
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
        TraitFactory.setMode( mode, kbase );

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        TraitRegistry tr = ((KnowledgeBaseImpl) kbase).getConfiguration().getComponentFactory().getTraitRegistry();
        System.out.println( tr.getHierarchy() );


        Entity ent = new Entity( "x" );
        KieSession ksession = kbase.newKieSession();
        ksession.insert( ent );
        ksession.fireAllRules();

        assertEquals( 1, ent.getMostSpecificTraits().size() );

        ksession.insert( "y" );
        ksession.fireAllRules();

        System.out.println( ent.getMostSpecificTraits() );
        assertEquals( 2, ent.getMostSpecificTraits().size() );

    }



    @Test(timeout=10000)
    public void testTraitActualTypeCodeWithEntities() {
        testTraitActualTypeCodeWithEntities( "ent", mode );
    }

    @Test(timeout=10000)
    public void testTraitActualTypeCodeWithCoreMap() {
        testTraitActualTypeCodeWithEntities( "kor", mode );
    }


    void testTraitActualTypeCodeWithEntities( String trig, VirtualPropertyMode mode ) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ClassPathResource( "org/drools/compiler/factmodel/traits/testComplexDonShed.drl" ), ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        TraitFactory.setMode( mode, kbase );

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KieSession ksession = kbase.newKieSession();

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


    @Test(timeout=10000)
    public void testTraitModifyCore() {
        String s1 = "package test; " +
                    "import org.drools.core.factmodel.traits.*; " +
                    "" +
                    "global java.util.List list; " +
                    "" +
                    "declare trait Student @PropertyReactive name : String end " +
                    "declare trait Worker @PropertyReactive name : String end " +
                    "declare trait StudentWorker extends Student, Worker @PropertyReactive name : String end " +
                    "declare trait Assistant extends Student, Worker @PropertyReactive name : String end " +
                    "declare Person @Traitable name : String end " +
                    "" +
                    "rule \"Init\"  " +
                    "when  " +
                    "then  " +
                    "  Person p = new Person( \"john\" );  " +
                    "  insert( p );  " +
                    "end  " +
                    "" +
                    "rule \"Don\"  " +
                    "no-loop  " +
                    "when  " +
                    "  $p : Person( name == \"john\" )  " +
                    "then  " +
                    "  System.out.println( $p );  " +
                    "" +
                    "  System.out.println( \" ----------------------------------------------------------------------------------- Don student\" );  " +
                    "  don( $p, Student.class );  " +
                    "  System.out.println( \" ----------------------------------------------------------------------------------- Don worker\" );  " +
                    "  don( $p, Worker.class );  " +
                    "  System.out.println( \" ----------------------------------------------------------------------------------- Don studentworker\" );  " +
                    "  don( $p, StudentWorker.class );  " +
                    "  System.out.println( \" ----------------------------------------------------------------------------------- Don assistant\" );  " +
                    "  don( $p, Assistant.class );  " +
                    "end  " +
                    "" +
                    "rule \"Log S\"  " +
                    "when  " +
                    "  $t : Student() @Watch( name ) " +
                    "then  " +
                    "  System.out.println( \"Student >> \" +  $t ); " +
                    "  list.add( $t.getName() );  " +
                    "end  " +
                    "rule \"Log W\"  " +
                    "when  " +
                    "  $t : Worker() @Watch( name ) " +
                    "then  " +
                    "  System.out.println( \"Worker >> \" + $t );  " +
                    "  list.add( $t.getName() );  " +
                    "end  " +
                    "rule \"Log SW\"  " +
                    "when  " +
                    "  $t : StudentWorker() @Watch( name ) " +
                    "then  " +
                    "  System.out.println( \"StudentWorker >> \" + $t );  " +
                    "  list.add( $t.getName() );  " +
                    "end  " +
                    "rule \"Log RA\"  " +
                    "when  " +
                    "  $t : Assistant() @Watch( name ) " +
                    "then  " +
                    "  System.out.println( \"Assistant >> \" + $t );  " +
                    "  list.add( $t.getName() );  " +
                    "end  " +
                    "" +
                    "rule \"Mod\"  " +
                    "salience -10  " +
                    "when  " +
                    "  $p : Person( name == \"john\" ) " +
                    "then  " +
                    "   System.out.println( \"-----------------------------\" ); " +
                    "   modify ( $p ) { setName( \"alan\" ); } " +
                    "end  " +
                    "";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ByteArrayResource( s1.getBytes() ), ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        TraitFactory.setMode( mode, kbase );

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KieSession ksession = kbase.newKieSession();
        List list = new ArrayList();
        ksession.setGlobal( "list", list );


        int k = ksession.fireAllRules();

        assertEquals( Arrays.asList( "john", "john", "john", "john", "alan", "alan", "alan", "alan" ), list );
        assertEquals( 11, k );

    }




    @Test
    public void testTraitModifyCore2() {
        String s1 = "package test; " +
                    "import org.drools.core.factmodel.traits.*; " +
                    "" +
                    "declare trait Student @propertyReactive name : String end " +
                    "declare trait Worker @propertyReactive name : String end " +
                    "declare trait StudentWorker extends Student, Worker @propertyReactive name : String end " +
                    "declare trait StudentWorker2 extends StudentWorker @propertyReactive name : String end " +
                    "declare trait Assistant extends Student, Worker @propertyReactive name : String end " +
                    "declare Person @Traitable @propertyReactive name : String end " +
                    "" +
                    "rule \"Init\"  " +
                    "when  " +
                    "then  " +
                    "  Person p = new Person( \"john\" );  " +
                    "  insert( p );  " +
                    "end  " +
                    "" +
                    "rule \"Don\"  " +
                    "when  " +
                    "  $p : Person( name == \"john\" )  " +
                    "then  " +
                    "  System.out.println( \">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> DON WORKER \" + $p  );  " +
                    "  don( $p, Worker.class );  " +
                    "  System.out.println( \">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> DON STUDWORKER-2 \" + $p );  " +
                    "  don( $p, StudentWorker2.class );  " +
                    "  System.out.println( \">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> DON ASSISTANT \" + $p );  " +
                    "  don( $p, Assistant.class );  " +
                    "end  " +
                    "" +
                    "rule \"Log S\"  " +
                    "when  " +
                    "  $t : Student() @watch( name )  " +
                    "then  " +
                    "  System.err.println( \"@@Student >> \" +  $t );  " +
                    "end  " +
                    "rule \"Log W\"  " +
                    "when  " +
                    "  $t : Worker() @watch( name )  " +
                    "then  " +
                    "  System.err.println( \"@@Worker >> \" + $t );  " +
                    "end  " +
                    "rule \"Log SW\"  " +
                    "when  " +
                    "  $t : StudentWorker() @watch( name )  " +
                    "then  " +
                    "  System.err.println( \"@@StudentWorker >> \" + $t );  " +
                    "end  " +
                    "rule \"Log RA\"  " +
                    "when  " +
                    "  $t : Assistant() @watch( name )  " +
                    "then  " +
                    "  System.err.println( \"@@Assistant >> \" + $t );  " +
                    "end  " +
                    "rule \"Log Px\"  " +
                    "salience -1  " +
                    "when  " +
                    "  $p : Person() @watch( name )  " +
                    "then  " +
                    "  System.err.println( \"Poor Core Person >> \" + $p );  " +
                    "end  " +
                    "" +
                    "rule \"Mod\"  " +
                    "salience -10  " +
                    "when  " +
                    "  String( this == \"go\" )  " +
                    "  $p : Student( name == \"john\" )  " +
                    "then  " +
                    "  System.out.println( \" ------------------------------------------------------------------------------ \" + $p );  " +
                    "  modify ( $p ) { setName( \"alan\" ); } " +
                    "end  " +
                    "";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ByteArrayResource( s1.getBytes() ), ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        TraitFactory.setMode( mode, kbase ); // not relevant

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KieSession ksession = kbase.newKieSession();
        int k = ksession.fireAllRules();

        assertEquals( 7, k );

        ksession.insert( "go" );
        k = ksession.fireAllRules();

        assertEquals( 6, k );

    }

    @Test(timeout=10000)
    public void testTraitModifyCore2a() {
        String s1 = "package test;\n" +
                    "import org.drools.core.factmodel.traits.*;\n" +
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
        TraitFactory.setMode( mode, kbase ); // not relevant
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        KieSession ksession = kbase.newKieSession();

        ArrayList list = new ArrayList(  );
        ksession.setGlobal( "list", list );

        int k = ksession.fireAllRules();

        assertTrue( list.contains( true ) );
        assertEquals( 1, list.size() );
    }




    @Test(timeout=10000)
    public void testTraitModifyCore3() {
        String s1 = "package test;\n" +
                    "import org.drools.core.factmodel.traits.*;\n" +
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
        TraitFactory.setMode( mode, kbase ); // not relevant

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        List list = new ArrayList();
        KieSession ksession = kbase.newKieSession();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        ksession.insert( "go" );
        ksession.fireAllRules();

        assertEquals( 14, list.size() );
        for ( int j = 1; j <= 14; j++ ) {
            assertTrue( list.contains( j ) );
        }


    }







    @Test(timeout=10000)
    public void testTraitModifyCoreWithPropertyReactivity() {
        String s1 = "package test;\n" +
                    "import org.drools.core.factmodel.traits.*;\n" +
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
        TraitFactory.setMode( mode, kbase ); // not relevant

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        List<Integer> list = new ArrayList<Integer>();
        KieSession ksession = kbase.newKieSession();
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




    public static interface IntfParent {}

    @Test(timeout=10000)
    public void testTraitEncodeExtendingNonTrait() {

        String s1 = "package test;\n" +
                    "import org.drools.compiler.factmodel.traits.TraitTest.IntfParent;\n" +
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
        TraitFactory.setMode( mode, kbase );

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KnowledgeBuilder kbuilder2 = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder2.add( new ByteArrayResource( s1.getBytes() ), ResourceType.DRL );
        if ( kbuilder2.hasErrors() ) {
            fail( kbuilder2.getErrors().toString() );
        }

        kbase.addKnowledgePackages( kbuilder2.getKnowledgePackages() );

    }



    @Test(timeout=10000)
    public void isAWithBackChaining() {

        String source = "org/drools/compiler/factmodel/traits/testTraitIsAWithBC.drl";
        KieSession ksession = getSession( source );
        TraitFactory.setMode( mode, ksession.getKieBase() );

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        ksession.insert( "Como" );

        ksession.fireAllRules();

        assertTrue( list.contains( "Italy" ) );
    }




    @Test(timeout=10000)
    public void testIsAEvaluatorOnClassification( ) {
        String source = "package t.x \n" +
                        "\n" +
                        "global java.util.List list; \n" +
                        "import org.drools.core.factmodel.traits.Thing\n" +
                        "import org.drools.core.factmodel.traits.Entity\n" +
                        "\n" +
                        "declare trait t.x.D\n" +
                        "    @propertyReactive\n" +
                        "\n" +
                        "end\n" +
                        "" +
                        "declare trait t.x.E\n" +
                        "    @propertyReactive\n" +
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
                        "   $t : org.drools.core.factmodel.traits.Thing( $c : core, this not isA t.x.E.class, this isA t.x.D.class ) " +
                        "then\n" +
                        "   list.add( \"E\" ); \n" +
                        "   don( $t, E.class ); \n" +
                        "end\n" +
                        "" +
                        "rule React \n" +
                        "when E() then \n" +
                        "   list.add( \"X\" ); \n" +
                        "end \n"
                ;

        KieSession ks = getSessionFromString( source );
        TraitFactory.setMode( mode, ks.getKieBase() );

        List list = new ArrayList();
        ks.setGlobal( "list", list );
        ks.fireAllRules();

        assertEquals( 2, list.size() );
        assertTrue( list.contains( "E" ) );
        assertTrue( list.contains( "X" ) );

    }



    @Test(timeout=10000)
    public void testShedWithTMS( ) {
        String source = "package t.x \n" +
                        "\n" +
                        "global java.util.List list; \n" +
                        "import org.drools.core.factmodel.traits.Thing\n" +
                        "import org.drools.core.factmodel.traits.Entity\n" +
                        "\n" +
                        "declare trait t.x.D\n" +
                        "    @propertyReactive\n" +
                        "\n" +
                        "end\n" +
                        "" +
                        "declare trait t.x.E\n" +
                        "    @propertyReactive\n" +
                        "\n" +
                        "end\n" +
                        "" +
                        "rule Init when\n" +
                        "then\n" +
                        "   Entity o = new Entity();\n" +
                        "   insert(o);\n" +
                        "   don( o, Thing.class ); \n" +
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
                        "   $t : org.drools.core.factmodel.traits.Thing( $c : core, _isTop(), this not isA t.x.E.class, this isA t.x.D.class ) " +
                        "then\n" +
                        "   list.add( \"E\" ); \n" +
                        "   System.out.println( \"E due to \" + $t); \n" +
                        "   don( $t, E.class ); \n" +
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
                        "   delete( $s ); \n" +
                        "   shed( $d, D.class );\n" +
                        "end \n" +
                        ""
                ;

        KieSession ks = getSessionFromString( source );
        TraitFactory.setMode( mode, ks.getKieBase() );

        List list = new ArrayList();
        ks.setGlobal( "list", list );
        ks.fireAllRules();

        System.out.println( list );
        assertEquals( 2, list.size() );
        assertTrue( list.contains( "E" ) );
        assertTrue( list.contains( "X" ) );

        ks.insert( "shed" );
        ks.fireAllRules();

        for ( Object o : ks.getObjects() ) {
            System.out.println( o );
        }
        assertEquals( 3, ks.getObjects().size() );

    }



    @Test(timeout=10000)
    public void testTraitInitialization() {
        String source = "package t.x \n" +
                        "import java.util.*; \n" +
                        "import org.drools.core.factmodel.traits.Thing \n" +
                        "import org.drools.core.factmodel.traits.Traitable \n" +
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

        KieSession ks = getSessionFromString( source );
        TraitFactory.setMode( mode, ks.getKieBase() );

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




    @Test(timeout=10000)
    public void testUnTraitedBean() {
        String source = "package t.x \n" +
                        "import java.util.*; \n" +
                        "import org.drools.core.factmodel.traits.Thing \n" +
                        "import org.drools.core.factmodel.traits.Traitable \n" +
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


        KieSession ks = getSessionFromString( source );
        TraitFactory.setMode( mode, ks.getKieBase() );

        List list = new ArrayList();
        ks.setGlobal( "list", list );
        ks.fireAllRules();

    }



    @Test(timeout=10000)
    public void testIsAOptimization(  ) {
        String source = "package t.x \n" +
                        "import java.util.*; \n" +
                        "import org.drools.core.factmodel.traits.Thing \n" +
                        "import org.drools.core.factmodel.traits.Traitable \n" +
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


        KieSession ks = getSessionFromString( source );
        TraitFactory.setMode( mode, ks.getKieBase() );

        List list = new ArrayList();
        ks.setGlobal( "list", list );
        ks.fireAllRules();

        assertEquals( 3, list.size() );

    }



    @Test(timeout=10000)
    public void testTypeRefractionOnInsert(  ) {
        String source = "package t.x \n" +
                        "import java.util.*; \n" +
                        "import org.drools.core.factmodel.traits.Thing \n" +
                        "import org.drools.core.factmodel.traits.Traitable \n" +
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


        KieSession ks = getSessionFromString( source );
        TraitFactory.setMode( mode, ks.getKieBase() );

        List list = new ArrayList();
        ks.setGlobal( "list", list );
        ks.fireAllRules();

        assertEquals( 1, list.size() );

    }



    @Test(timeout=10000)
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


        KieSession ks = getSessionFromString( source );
        TraitFactory.setMode( mode, ks.getKieBase() );

        ks.fireAllRules();

        QueryResults res = ks.getQueryResults( "QueryTraitA" );

        assertEquals( 1, res.size() );

    }

    @Test
    public void testTypeRefractionOnQuery2(  ) {
        String source = "package t.x \n" +
                        "import java.util.*; \n" +
                        "import org.drools.core.factmodel.traits.Thing \n" +
                        "import org.drools.core.factmodel.traits.Traitable \n" +
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


        KieSession ks = getSessionFromString( source );
        TraitFactory.setMode( mode, ks.getKieBase() );

        List list = new ArrayList();
        ks.setGlobal( "list", list );
        ks.fireAllRules();

        QueryResults res;
        res = ks.getQueryResults( "queryA1" );
        assertEquals( 1, res.size() );

    }

    @Test
    public void testNodePartitioningByProxies(  ) {
        String source = "package t.x  " +
                        "import java.util.*;  " +
                        "import org.drools.core.factmodel.traits.Thing  " +
                        "import org.drools.core.factmodel.traits.Traitable  " +
                        " " +
                        "global java.util.List list;  " +
                        " " +
                        "" +
                        "declare trait A @PropertyReactive end " +
                        "declare trait B extends A @PropertyReactive end " +
                        "declare trait C extends B @PropertyReactive end " +
                        "declare trait D extends A @PropertyReactive end " +
                        "declare trait E extends C, D @PropertyReactive end " +
                        "declare trait F extends E @PropertyReactive end " +
                        "declare trait G extends A @PropertyReactive end " +
                        "" +
                        "declare Kore " +
                        "   @Traitable " +
                        "end " +
                        "" +
                        "rule Init when " +

                        "then " +
                        "   Kore k = new Kore(); " +
                        "   don( k, C.class );  " +
                        "   don( k, D.class );  " +
                        "   don( k, B.class );  " +
                        "   don( k, A.class );  " +
                        "   don( k, F.class );  " +
                        "   don( k, E.class );  " +
                        "   don( k, G.class );  " +
                        "end ";

        for ( char c = 'A'; c <= 'G'; c++ ) {
            String C = "" + c;
            source += "rule Rule" + C +
                      " when " + C + "() then list.add( '"+ C + "' ); end ";
        }

            source += "rule RuleAll " +
                        "when  " +
                        "   A() B() C() D() E() F() G() " +
                        "then  " +
                        "   list.add( 'Z' ); " +
                        "end " +
                        "";


        KieSession ks = getSessionFromString( source );
        TraitFactory.setMode( mode, ks.getKieBase() );

        List list = new ArrayList();
        ks.setGlobal( "list", list );
        ks.fireAllRules();

        System.out.println( list );
        assertEquals( Arrays.asList( 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'Z' ), list );


        for ( Object o : ks.getObjects( new ObjectFilter() {
            @Override
            public boolean accept( Object object ) {
                return object instanceof TraitableBean;
            }
        } ) ) {
            Set<BitSet> otns = checkOTNPartitioning( (TraitableBean) o, ks );
            assertEquals( 7, otns.size() );
        }

    }

    @Test
    public void testNodePartitioningByProxiesAfterShed(  ) {
        String source = "package t.x  " +
                        "import java.util.*;  " +
                        "import org.drools.core.factmodel.traits.Thing  " +
                        "import org.drools.core.factmodel.traits.Traitable  " +
                        " " +
                        "global java.util.List list;  " +
                        " " +
                        "" +
                        "declare trait A end " +
                        "declare trait B extends A end " +
                        "declare trait C extends B end " +
                        "declare trait D extends A end " +
                        "declare trait E extends C, D end " +
                        "declare trait F extends E end " +
                        "declare trait G extends A end " +
                        "" +
                        "declare Kore " +
                        "   @Traitable " +
                        "end " +
                        "" +
                        "rule Init when " +

                        "then " +
                        "   Kore k = new Kore(); " +
                        "   don( k, C.class );  " +
                        "   don( k, D.class );  " +
                        "   don( k, B.class );  " +
                        "   don( k, A.class );  " +
                        "   don( k, F.class );  " +
                        "   don( k, E.class );  " +
                        "   don( k, G.class );  " +
                        "   shed( k, B.class );  " +
                        "end ";

        for ( char c = 'A'; c <= 'G'; c++ ) {
            String C = "" + c;
            source += "rule Rule" + C +
                      " when " + C + "() then list.add( '"+ C + "' ); end ";
        }

            source += "rule RuleAll " +
                        "when  " +
                        "   A() D() G() " +
                        "then  " +
                        "   list.add( 'Z' ); " +
                        "end " +
                        "";


        KieSession ks = getSessionFromString( source );
        TraitFactory.setMode( mode, ks.getKieBase() );

        List list = new ArrayList();
        ks.setGlobal( "list", list );
        ks.fireAllRules();

        System.out.println( list );
        assertEquals( Arrays.asList( 'A', 'D', 'G', 'Z' ), list );


        for ( Object o : ks.getObjects( new ObjectFilter() {
            @Override
            public boolean accept( Object object ) {
                return object instanceof TraitableBean;
            }
        } ) ) {
            Set<BitSet> otns = checkOTNPartitioning( (TraitableBean) o, ks );
            assertEquals( 3, otns.size() );
        }

    }



    @Test(timeout=10000)
    public void testTypeRefractionOnQueryWithIsA(  ) {
        String source = "package t.x \n" +
                        "import java.util.*; \n" +
                        "import org.drools.core.factmodel.traits.Thing \n" +
                        "import org.drools.core.factmodel.traits.Traitable \n" +
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


        KieSession ks = getSessionFromString( source );
        TraitFactory.setMode( mode, ks.getKieBase() );

        List list = new ArrayList();
        ks.setGlobal( "list", list );
        ks.fireAllRules();

        QueryResults res = ks.getQueryResults( "queryA" );
        Iterator<QueryResultsRow> iter = res.iterator();
        Object a = iter.next().get( "$x" );
        assertFalse( iter.hasNext() );

        assertEquals( 1, res.size() );

    }



    @Test(timeout=10000)
    public void testCoreUpdate4(  ) {
        String source = "package t.x \n" +
                        "import java.util.*; \n" +
                        "import org.drools.core.factmodel.traits.Thing \n" +
                        "import org.drools.core.factmodel.traits.Traitable \n" +
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
        KieSession ks = getSessionFromString( source );
        TraitFactory.setMode( mode, ks.getKieBase() );

        List list = new ArrayList();
        ks.setGlobal( "list", list );
        ks.fireAllRules();

        for ( Object o : ks.getObjects() ) {
            System.err.println( o );
        }
        assertEquals( 1, list.size() );
    }



    @Test(timeout=10000)
    public void traitLogicalSupportAnddelete() {
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
                     "  shed( $p, org.drools.core.factmodel.traits.Thing.class ); " +
                     "  delete( $s ); \n" +
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
                     "  delete( $s ); \n" +
                     "  delete( $p ); \n" +
                     "end\n" +
                     "";


        KieSession ksession = getSessionFromString(drl);
        TraitFactory.setMode( mode, ksession.getKieBase() );

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        FactHandle h1 = ksession.insert( "trigger1" );
        FactHandle h2 = ksession.insert( "trigger2" );
        ksession.fireAllRules();

        for ( Object o : ksession.getObjects() ) {
            System.err.println( o );
        }
        System.err.println( "---------------------------------" );

        assertEquals( 4, ksession.getObjects().size() );

        ksession.delete( h1 );
        ksession.fireAllRules();

        for ( Object o : ksession.getObjects() ) {
            System.err.println( o );
        }
        System.err.println( "---------------------------------" );

        assertEquals( 3, ksession.getObjects().size() );

        ksession.delete( h2 );
        ksession.fireAllRules();

        for ( Object o : ksession.getObjects() ) {
            System.err.println( o );
        }
        System.err.println( "---------------------------------" );

        assertEquals( 1, ksession.getObjects().size() );

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

        assertEquals( 3, ksession.getObjects().size() );

        ksession.insert( "trigger5" );
        ksession.fireAllRules();

        for ( Object o : ksession.getObjects() ) {
            System.err.println( o );
        }
        System.err.println( "---------------------------------" );

        assertEquals( 1, ksession.getObjects().size() );
    }



    @Test(timeout=10000)
    public void testShedThing() {
        String s1 = "package test;\n" +
                    "import org.drools.core.factmodel.traits.*;\n" +
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
                    "  delete( $g ); \n\n" +
                    "end \n" +
                    "";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ByteArrayResource( s1.getBytes() ), ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        TraitFactory.setMode( mode, kbase ); // not relevant

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        List list = new ArrayList();
        KieSession ksession = kbase.newKieSession();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        ksession.insert( "go" );
        ksession.fireAllRules();

        for ( Object o : ksession.getObjects() ) {
            System.out.println( o );
        }

        assertEquals( 1, ksession.getObjects().size() );
    }


    @Test(timeout=10000)
    public void testdeleteThings() {
        String s1 = "package test;\n" +
                    "import org.drools.core.factmodel.traits.*;\n" +
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
                    "  delete( $x ); \n\n" +
                    "  delete( $g ); \n\n" +
                    "end \n" +
                    "";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ByteArrayResource( s1.getBytes() ), ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        TraitFactory.setMode( mode, kbase );

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        List list = new ArrayList();
        KieSession ksession = kbase.newKieSession();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        ksession.insert( "go" );
        ksession.fireAllRules();

        for ( Object o : ksession.getObjects() ) {
            System.out.println( o );
        }

        assertEquals( 0, ksession.getObjects().size() );
    }

    @Test(timeout=10000)
    public void traitLogicalRemovalSimple( ) {
        String drl = "package org.drools.compiler.trait.test;\n" +
                     "\n" +
                     "import org.drools.core.factmodel.traits.Traitable;\n" +
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


        KieSession ksession = getSessionFromString(drl);
        TraitFactory.setMode( mode, ksession.getKieBase() );

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        FactHandle h = ksession.insert( "trigger" );
        ksession.fireAllRules();
        assertEquals( 5, ksession.getObjects().size() );

        ksession.delete( h );
        ksession.fireAllRules();

        for ( Object o : ksession.getObjects() ) {
            // lose the string and the Student proxy
            System.out.println( o );
        }
        assertEquals( 3, ksession.getObjects().size() );

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


    @Test(timeout=10000)
    public void testTraitDonLegacyClassWithoutEmptyConstructor( ) {
        String drl = "package org.drools.compiler.trait.test;\n" +
                     "\n" +
                     "import org.drools.compiler.factmodel.traits.TraitTest.TraitableFoo;\n" +
                     "import org.drools.core.factmodel.traits.Traitable;\n" +
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


        KieSession ksession = getSessionFromString(drl);
        TraitFactory.setMode( mode, ksession.getKieBase() );
        ksession.addEventListener( new DebugAgendaEventListener(  ) );

        ksession.insert( new TraitableFoo( "xx", 0, null ) );
        ksession.fireAllRules();

        for ( Object o : ksession.getObjects() ) {
            System.out.println( o );
        }

        assertEquals( 2, ksession.getObjects().size() );
    }



    @Test(timeout=10000)
    public void testdeleteCoreObjectChained(  ) {
        String source = "package org.drools.test;\n" +
                        "import java.util.List; \n" +
                        "import org.drools.core.factmodel.traits.Thing \n" +
                        "import org.drools.core.factmodel.traits.Traitable \n" +
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
                        "rule delete \n" +
                        "salience -99 \n" +
                        "when \n" +
                        "   $x : String() \n" +
                        "then \n" +
                        "   System.out.println( \"deleteing\" ); \n" +
                        "   delete( $x ); \n" +
                        "end \n" +
                        "\n";

        KieSession ks = getSessionFromString( source );
        TraitFactory.setMode( mode, ks.getKieBase() );

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


    @Test(timeout=10000)
    public void testUpdateLegacyClass(  ) {
        String source = "package org.drools.text;\n" +
                        "\n" +
                        "global java.util.List list;\n" +
                        "\n" +
                        "import org.drools.compiler.Person;\n" +
                        "import org.drools.core.factmodel.traits.Traitable;\n" +
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
                        "  delete( $s ); \n" +
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

        KieSession ks = getSessionFromString( source );
        TraitFactory.setMode( mode, ks.getKieBase() );

        List list = new ArrayList();
        ks.setGlobal( "list", list );

        ks.insert( new Person( "john", 32 ) );
        ks.insert( "X" );

        ks.fireAllRules();

        assertTrue( list.contains( 0 ) );
        assertEquals( 1, list.size() );

        ks.dispose();
    }



    @Test(timeout=10000)
    public void testSoftPropertyClash() {
        String source = "package org.drools.text;\n" +
                        "\n" +
                        "global java.util.List list;\n" +
                        "\n" +
                        "import org.drools.core.factmodel.traits.Traitable;\n" +
                        "import org.drools.core.factmodel.traits.Alias;\n" +
                        "\n" +
                        "declare Person @Traitable @propertyReactive \n" +
                        "end \n" +
                        "" +
                        "declare trait Student\n" +
                        "   @propertyReactive \n" +
                        "   id : String = \"a\" \n" +
                        "   fld2 : int = 4 \n" +
                        "   fld3 : double = 4.0 \n" +
                        "   fld4 : String = \"hello\" \n" +
                        "   fldZ : String = \"hello\" @Alias( \"fld5\" )\n" +
                        "end\n" +
                        "declare trait Worker\n" +
                        "   @propertyReactive \n" +
                        "   id : int = 3 \n" +
                        "   fld2 : String = \"b\" \n " +
                        "   fld3 : int = 11 \n " +
                        "   fld4 : Class = Object.class \n " +
                        "   fldY : int = 42 @Alias( \"fld5\" )\n" +
                        "end\n" +
                        "" +
                        "rule \"Init\" when then \n" +
                        "   insert( new Person() ); \n" +
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
                        "  $s : Student( $sid : id == \"xyz\", $f2 : fld2, $f3 : fld3, $f4 : fld4, $f5 : fldZ )\n" +
                        "then\n" +
                        "  System.out.println( \">>>>>>>>>> Student\" + $s ); \n" +
                        "  list.add( $sid ); \n" +
                        "  list.add( $f2 ); \n" +
                        "  list.add( $f3 ); \n" +
                        "  list.add( $f4 ); \n" +
                        "  list.add( $f5 ); \n" +
                        "end\n" +
                        "\n" +
                        "rule \"Mod\"\n" +
                        "when\n" +
                        "  $w : Worker( $wid : id == 99, $f2 : fld2, $f3 : fld3, $f4 : fld4, $f5 : fldY )\n" +
                        "then\n" +
                        "  System.out.println( \">>>>>>>>>> Worker\" + $w );\n" +
                        "  list.add( $wid ); \n" +
                        "  list.add( $f2 ); \n" +
                        "  list.add( $f3 ); \n" +
                        "  list.add( $f4 ); \n" +
                        "  list.add( $f5 ); \n" +
                        "end";

        KieSession ks = getSessionFromString( source );
        TraitFactory.setMode( mode, ks.getKieBase() );

        List list = new ArrayList();
        ks.setGlobal( "list", list );

        ks.fireAllRules();

        assertEquals( 5, list.size() );
        assertEquals( Arrays.asList( 99, "b", 11, Object.class, 42 ), list );

        ks.dispose();
    }


    @Test
    public void testMultipleModifications() {
        String drl = "package org.drools.traits.test;\n" +
                     "\n" +
                     "import org.drools.core.factmodel.traits.Traitable;\n" +
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

        KieSession ks = getSessionFromString( drl );
        TraitFactory.setMode( mode, ks.getKieBase() );

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
                     "import org.drools.core.factmodel.traits.*; \n" +
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

        KieSession ks = getSessionFromString( drl );
        TraitFactory.setMode( mode, ks.getKieBase() );

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
        System.out.println( list );
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



    @Test(timeout=10000)
    public void testParentBlockers() {
        String drl = "package org.drools.test;\n" +
                     "import org.drools.core.factmodel.traits.*; \n" +
                     "\n" +
                     "global java.util.List list; \n" +
                     "" +
                     "declare X @Traitable end \n" +
                     "" +
                     "declare trait A @propertyReactive end\n" +
                     "declare trait B @propertyReactive end\n" +
                     "declare trait C extends A, B @propertyReactive end \n" +
                     "" +
                     "rule Init when then X x = new X(); insert( x ); don( x, A.class); don( x, B.class); end \n"+
                     "rule Go when String( this == \"go\" ) $x : X() then don( $x, C.class); end \n" +
                     "rule Go2 when String( this == \"go2\" ) $x : C() then System.out.println( 1000 ); end \n" +
                     "";


        KieSession ks = getSessionFromString( drl );
        TraitFactory.setMode( mode, ks.getKieBase() );

        List list = new ArrayList();
        ks.setGlobal( "list", list );

        ks.fireAllRules();

        ks.insert( "go" );
        ks.fireAllRules();

        ks.insert( "go2" );
        ks.fireAllRules();

        System.out.println( "---------------------------------------" );

        ks.dispose();

    }




    @Test(timeout=10000)
    public void testTraitLogicalTMS() {
        String drl = "package org.drools.test;\n" +
                     "import org.drools.core.factmodel.traits.*; \n" +
                     "\n" +
                     "global java.util.List list; \n" +
                     "" +
                     "declare X @Traitable end \n" +
                     "" +
                     "declare trait A @propertyReactive end\n" +
                     "declare trait B @propertyReactive end\n" +
                     "" +
                     "rule Init when then X x = new X(); insert( x ); end \n"+
                     "rule Go when String( this == \"go\" ) $x : X() then don( $x, A.class, true ); don( $x, B.class, true ); end \n" +
                     "rule Go2 when String( this == \"go2\" ) $x : X() then don( $x, A.class ); end \n" +
                     "rule Go3 when String( this == \"go3\" ) $x : A() not B() then list.add( 100 ); end \n" +
                     "";

        KieSession ks = getSessionFromString( drl );
        TraitFactory.setMode( mode, ks.getKieBase() );

        List list = new ArrayList();
        ks.setGlobal( "list", list );

        ks.fireAllRules();

        FactHandle handle = ks.insert( "go" );
        ks.fireAllRules();

        ks.insert( "go2" );
        ks.fireAllRules();

        ks.delete( handle );
        ks.fireAllRules();

        System.out.println( "---------------------------------------" );

        for ( Object o : ks.getObjects() ) {
            System.out.println( o );
        }

        ks.insert( "go3" );
        ks.fireAllRules();

        assertEquals( Arrays.asList( 100 ), list );

        ks.dispose();
    }


    @Test(timeout=10000)
    public void testTraitNoType() {
        String drl = "" +
                     "package org.drools.core.factmodel.traits.test;\n" +
                     "\n" +
                     "import org.drools.core.factmodel.traits.Thing;\n" +
                     "import org.drools.core.factmodel.traits.Traitable;\n" +
                     "import org.drools.core.factmodel.traits.Trait;\n" +
                     "import org.drools.core.factmodel.traits.Alias;\n" +
                     "import java.util.*;\n" +
                     "\n" +
                     "global java.util.List list;\n" +
                     "\n" +
                     "\n" +
                     "declare Parent\n" +
                     "@Traitable( logical = true )" +
                     "@propertyReactive\n" +
                     "end\n" +
                     "\n" +
                     "declare trait ChildTrait\n" +
                     "@propertyReactive\n" +
                     "    naam : String = \"kudak\"\n" +
                     "    id : int = 1020\n" +
                     "end\n" +
                     "\n" +
                     "rule \"don\"\n" +
                     "no-loop\n" +
                     "when\n" +
                     "then\n" +
                     "    Parent p = new Parent();" +
                     "    insert(p);\n" +
                     "    ChildTrait ct = don( p , ChildTrait.class );\n" +
                     "    list.add(\"correct1\");\n" +
                     "end\n" +
                     "\n" +
                     "rule \"check\"\n" +
                     "no-loop\n" +
                     "when\n" +
                     "    $c : ChildTrait($n : naam == \"kudak\", id == 1020 )\n" +
                     "    $p : Thing( core == $c.core, fields[\"naam\"] == $n )\n" +
                     "then\n" +
                     "    list.add(\"correct2\");\n" +
                     "end";

        KieSession ksession = loadKnowledgeBaseFromString(drl).newKieSession();
        TraitFactory.setMode( mode, ksession.getKieBase());

        List list = new ArrayList();
        ksession.setGlobal("list",list);
        ksession.fireAllRules();

        assertTrue(list.contains("correct1"));
        assertTrue(list.contains("correct2"));
    }




    @Test(timeout=10000)
    public void testTraitdeleteOrder() {
        String drl = "" +
                     "package org.drools.core.factmodel.traits.test;\n" +
                     "\n" +
                     "import org.drools.core.factmodel.traits.*;\n" +
                     "import java.util.*;\n" +
                     "\n" +
                     "declare trait A end \n" +
                     "declare trait B extends A end \n" +
                     "declare trait C end \n" +
                     "\n" +
                     "rule \"don\"\n" +
                     "when \n" +
                     "  $e : Entity() \n" +
                     "then\n" +
                     "  don( $e, A.class ); \n" +
                     "  don( $e, C.class ); \n" +
                     "  don( $e, B.class ); \n" +
                     "end\n" +
                     "";

        KieSession ksession = loadKnowledgeBaseFromString(drl).newKieSession();
        TraitFactory.setMode( mode, ksession.getKieBase() );

        FactHandle handle = ksession.insert( new Entity(  ) );
        ksession.fireAllRules();

        final ArrayList list = new ArrayList();

        ksession.addEventListener( new RuleRuntimeEventListener() {
            public void objectInserted( org.kie.api.event.rule.ObjectInsertedEvent objectInsertedEvent ) { }
            public void objectUpdated( org.kie.api.event.rule.ObjectUpdatedEvent objectUpdatedEvent ) { }
            public void objectDeleted( org.kie.api.event.rule.ObjectDeletedEvent objectRetractedEvent ) {
                Object o = objectRetractedEvent.getOldObject();
                if ( o instanceof TraitProxy ) {
                    String traitName = ( (TraitProxy) o )._getTraitName();
                    list.add( traitName.substring( traitName.lastIndexOf( "." ) + 1 ) );
                }
            }
        } );

        ksession.delete( handle );
        ksession.fireAllRules();

        System.out.println( list );
        assertEquals( Arrays.asList( "B", "C", "A" ), list );
    }


    @Test(timeout=10000)
    public void testTraitWithManySoftFields() {
        String drl = "" +
                     "package org.drools.core.factmodel.traits.test;\n" +
                     "\n" +
                     "import org.drools.core.factmodel.traits.*;\n" +
                     "import java.util.*;\n" +
                     "\n" +
                     "declare trait Tx \n";

        for ( int j = 0; j < 150; j ++ ) {
            drl += " fld" + j + " : String \n";
        }

        drl += "" +
               "end \n" +
               "\n" +
               "declare TBean @Traitable fld0 : String end \n" +
               "" +
               "rule \"don\"\n" +
               "when \n" +
               "then\n" +
               "  don( new TBean(), Tx.class ); \n" +
               "end\n" +
               "" +
               "";

        KieSession ksession = loadKnowledgeBaseFromString(drl).newKieSession();
        TraitFactory.setMode( mode, ksession.getKieBase() );

        ksession.fireAllRules();

        assertEquals( 2, ksession.getObjects().size() );

    }



    public static class CountingWorkingMemoryEventListener implements RuleRuntimeEventListener {

        private int inserts = 0;
        private int updates = 0;
        private int deletes = 0;

        public int getInserts() {
            return inserts;
        }

        public int getUpdates() {
            return updates;
        }

        public int getdeletes() {
            return deletes;
        }

        @Override
        public void objectInserted( org.kie.api.event.rule.ObjectInsertedEvent event ) {
            if ( ! ( event.getObject() instanceof String ) ) {
                inserts++;
            }
        }

        @Override
        public void objectUpdated( org.kie.api.event.rule.ObjectUpdatedEvent event ) {
            if ( ! ( event.getObject() instanceof String ) ) {
                updates++;
            }
        }

        public void objectDeleted( org.kie.api.event.rule.ObjectDeletedEvent objectdeleteedEvent ) {
            if ( ! ( objectdeleteedEvent.getOldObject() instanceof String ) ) {
                deletes++;
            }
        }

        public void reset() {
            inserts = 0;
            deletes = 0;
            updates = 0;
        }
    }


    @Test(timeout=10000)
    public void testDonManyTraitsAtOnce() {
        String drl = "" +
                     "package org.drools.core.factmodel.traits.test;\n" +
                     "\n" +
                     "import org.drools.core.factmodel.traits.*;\n" +
                     "import java.util.*;\n" +
                     "\n" +
                     "global List list; \n" +
                     "" +
                     "declare trait A end \n" +
                     "declare trait B end \n" +
                     "declare trait C end \n" +
                     "declare trait D end \n" +
                     "declare trait E end \n" +
                     "declare trait F end \n" +
                     "\n" +
                     "declare TBean @Traitable @propertyReactive fld0 : String end \n" +
                     "" +
                     "rule \"Don 1\"\n" +
                     "when \n" +
                     "then\n" +
                     "  TBean t = new TBean(); \n" +
                     "  don( t, A.class ); \n" +
                     "  don( t, B.class ); \n" +
                     "end\n" +
                     "" +
                     "rule \"Don 2\" " +
                     "when \n" +
                     "  $s : String( this == \"go\" ) \n" +
                     "  $t : TBean() \n" +
                     "then \n" +
                     "  list.add( 0 ); \n" +
                     "  System.out.println( \"Call DON MANY \" ); " +
                     "  don( $t, Arrays.asList( C.class, D.class, E.class, F.class ), true ); \n" +
                     "end \n" +
                     "" +
                     "rule Clear \n" +
                     "when \n" +
                     "  $s : String( this == \"undo\" ) \n" +
                     "  $t : TBean() \n" +
                     "then \n" +
                     "  delete( $s ); \n" +
                     "  delete( $t ); \n" +
                     "end \n" +
                     "" +
                     "rule C \n" +
                     "when\n" +
                     "  B( this isA C ) \n" +
                     "then \n" +
                     "  list.add( 1 ); \n" +
                     "  System.err.println( \"C is HERE !! \" ); " +
                     "end \n" +
                     "rule D \n" +
                     "when\n" +
                     "  D( this isA A, this isA C ) \n" +
                     "then \n" +
                     "  list.add( 2 ); \n" +
                     "  System.err.println( \"D is HERE TOO !! \" ); " +
                     "end \n"+
                     "rule E \n" +
                     "when\n" +
                     "  D( this isA A, this isA E ) \n" +
                     "then \n" +
                     "  list.add( 3 ); \n" +
                     "  System.err.println( \"AND E JOINS THE COMPANY !! \" ); " +
                     "end \n";

        KieSession ksession = loadKnowledgeBaseFromString(drl).newKieSession();
        TraitFactory.setMode( mode, ksession.getKieBase() );
        ArrayList list = new ArrayList();
        ksession.setGlobal( "list", list );

        CountingWorkingMemoryEventListener cwm = new CountingWorkingMemoryEventListener();
        ksession.addEventListener( cwm );

        ksession.fireAllRules();

        // insert Core Bean, A, B, Thing.
        // Update the bean on don B
        assertEquals( 0, cwm.getdeletes() );
        assertEquals( 3, cwm.getInserts() );
        assertEquals( 1, cwm.getUpdates() );
        cwm.reset();

        FactHandle handle = ksession.insert( "go" );
        ksession.fireAllRules();

        // don C, D, E, F at once : 4 inserts
        // Update the bean, A and B.
        assertEquals( 0, cwm.getdeletes() );
        assertEquals( 4, cwm.getInserts() );
        assertEquals( 3, cwm.getUpdates() );
        cwm.reset();

        ksession.delete( handle );
        ksession.fireAllRules();

        // logically asserted C, D, E, F are deleteed
        // as a logical deleteion, no update is made. This could be a bug....
        assertEquals( 4, cwm.getdeletes() );
        assertEquals( 0, cwm.getInserts() );
        assertEquals( 0, cwm.getUpdates() );
        cwm.reset();

        for ( Object o : ksession.getObjects() ) {
            System.out.println( o );
        }

        ksession.insert( "undo" );
        ksession.fireAllRules();

        // deleteing the core bean
        // A, B, Thing are deleteed too
        assertEquals( 3, cwm.getdeletes() );
        assertEquals( 0, cwm.getInserts() );
        assertEquals( 0, cwm.getUpdates() );
        cwm.reset();


        assertEquals( 4, list.size() );
        assertTrue( list.containsAll( Arrays.asList( 0, 1, 2, 3 ) ) );
    }

    @Test
    public void testDonManyTraitsAtOnce2() {
        String drl = "" +
                     "package org.drools.core.factmodel.traits.test;\n" +
                     "\n" +
                     "import org.drools.core.factmodel.traits.*;\n" +
                     "import java.util.*;\n" +
                     "\n" +
                     "global List list; \n" +
                     "" +
                     "declare trait A @propertyReactive end \n" +
                     "declare trait B @propertyReactive end \n" +
                     "\n" +
                     "declare TBean @Traitable @propertyReactive fld0 : String end \n" +
                     "" +
                     "rule \"Don 1\"\n" +
                     "when \n" +
                     "then\n" +
                     "  TBean t = new TBean(); \n" +
                     "  don( t, A.class ); \n" +
                     "  don( t, B.class ); \n" +
                     "end\n" +
                     "" +
                     "rule \"Test Don A,B\" " +
                     "when \n" +
                     "  A(this isA B) \n" +
                     "then \n" +
                     "  list.add( 0 ); \n" +
                     "  System.out.println( \"Call DON MANY (A isA B) \" ); " +
                     "end \n" +
                     "" +
                     "" ;

        KieSession ksession = loadKnowledgeBaseFromString(drl).newKieSession();
        TraitFactory.setMode( mode, ksession.getKieBase() );
        ArrayList list = new ArrayList();
        ksession.setGlobal( "list", list );

        CountingWorkingMemoryEventListener cwm = new CountingWorkingMemoryEventListener();
        ksession.addEventListener( cwm );

        ksession.fireAllRules();

        assertEquals( Arrays.asList( 0 ), list );

        assertEquals( 0, cwm.getdeletes() );
        assertEquals( 3, cwm.getInserts() );
        assertEquals( 1, cwm.getUpdates() );

    }




    @Traitable
    public static class Item {
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class TraitRulesThread implements Runnable {
        int threadIndex;
        int numRepetitions;
        KieSession ksession;

        public TraitRulesThread(int threadIndex, int numRepetitions, final KieSession ksession) {
            this.threadIndex = threadIndex;
            this.numRepetitions = numRepetitions;
            this.ksession = ksession;
        }
        public void run() {
            for (int repetitionIndex = 0; repetitionIndex < numRepetitions; repetitionIndex++) {
                final Item i = new Item();
                i.setId(String.format("testId_%d%d", threadIndex, repetitionIndex));
                ksession.insert(i);
                ksession.fireAllRules();
            }
        }
    }

    @Test(timeout=10000)
    @Ignore("Triple Store is not thread safe and needs to be rewritten")
    public void testMultithreadingTraits() throws InterruptedException {
        final String s1 = "package test;\n" +
                          "import org.drools.core.factmodel.traits.TraitTest.Item;\n" +
                          "declare Item end\n" +
                          "declare trait ItemStyle\n" +
                          "	id: String\n" +
                          "	adjustable: boolean\n" +
                          "end\n" +
                          "rule \"Don ItemStyle\"\n" +
                          "	no-loop true\n" +
                          "	when\n" +
                          "		$p : Item ()\n" +
                          "		not ItemStyle ( id == $p.id )\n" +
                          "	then\n" +
                          "		don($p, ItemStyle.class);\n" +
                          "end\n" +
                          "rule \"Item Style - Adjustable\"" +
                          "	no-loop true" +
                          "	when" +
                          "		$style : ItemStyle ( !adjustable )" +
                          "		Item (" +
                          "			id == $style.id " +
                          "		)" +
                          "	then" +
                          "		modify($style) {" +
                          "			setAdjustable(true)" +
                          "		};" +
                          "end";
        KieBase kbase = getKieBaseFromString(s1);
        TraitFactory.setMode( mode, kbase );

        // might need to tweak these numbers.  often works with 7-10,100,60, but often fails 15-20,100,60
        int MAX_THREADS = 20;
        int MAX_REPETITIONS = 100;
        int MAX_WAIT_SECONDS = 60;

        final ExecutorService executorService = Executors.newFixedThreadPool( MAX_THREADS );
        for (int threadIndex = 0; threadIndex < MAX_THREADS; threadIndex++) {
            executorService.execute(new TraitRulesThread(threadIndex, MAX_REPETITIONS, kbase.newKieSession()));
        }

        executorService.shutdown();
        executorService.awaitTermination(MAX_WAIT_SECONDS, TimeUnit.SECONDS);
        final List<Runnable> queuedTasks = executorService.shutdownNow();

        assertEquals(0, queuedTasks.size());
        assertEquals(true, executorService.isTerminated());
    }


    @Test(timeout=10000)
    public void testShedOneLastTrait() throws InterruptedException {
        final String s1 = "package test;\n" +
                          "import org.drools.core.factmodel.traits.*; \n" +
                          "global java.util.List list;\n" +
                          "" +
                          "declare Core @Traitable end\n" +
                          "" +
                          "declare trait Mask\n" +
                          "end\n" +
                          "" +
                          "rule \"Don ItemStyle\"\n" +
                          "	when\n" +
                          "	then\n" +
                          "		don( new Core(), Mask.class );\n" +
                          "end\n" +
                          "" +
                          "rule \"React\" \n" +
                          "	when \n" +
                          "     $s : String() \n" +
                          "		$m : Mask() \n" +
                          "then \n" +
                          "     delete( $s ); \n" +
                          "     shed( $m, Mask.class ); \n" +
                          "end\n" +
                          "" +
                          "rule Log \n" +
                          "when \n" +
                          " $t : Thing() \n" +
                          "then \n" +
                          " System.out.println( \"Thing detected \" + $t ); \n" +
                          " list.add( $t.getClass().getName() ); \n" +
                          "end \n" +
                          "";

        KieBase kbase = getKieBaseFromString(s1);
        TraitFactory.setMode( mode, kbase );
        ArrayList list = new ArrayList();

        KieSession knowledgeSession = kbase.newKieSession();
        knowledgeSession.setGlobal( "list", list );

        knowledgeSession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( Arrays.asList( "test.Mask.test.Core_Proxy" ), list );

        knowledgeSession.insert( "shed" );
        knowledgeSession.fireAllRules();

        assertEquals( 2, list.size() );
        assertEquals( Arrays.asList( "test.Mask.test.Core_Proxy", "org.drools.core.factmodel.traits.Thing.test.Core_Proxy" ), list );
    }



    @Test(timeout=10000)
    public void testShedThingCompletelyThenDonAgain() throws InterruptedException {
        final String s1 = "package test;\n" +
                          "import org.drools.core.factmodel.traits.*; \n" +
                          "global java.util.List list;\n" +
                          "" +
                          "declare Core @Traitable end\n" +
                          "" +
                          "declare trait Mask end\n" +
                          "declare trait Mask2 end\n" +
                          "" +
                          "rule \"Don ItemStyle\"\n" +
                          "	when\n" +
                          "     $s : String( this == \"don1\" ) \n" +
                          "	then\n" +
                          "     delete( $s ); \n" +
                          "		don( new Core(), Mask.class );\n" +
                          "end\n" +
                          "" +
                          "rule \"Clear\" \n" +
                          "	when \n" +
                          "     $s : String( this == \"shed1\" ) \n" +
                          "		$m : Mask() \n" +
                          "then \n" +
                          "     delete( $s ); \n" +
                          "     shed( $m, Thing.class ); \n" +
                          "end\n" +
                          "" +
                          "rule \"Add\" \n" +
                          "	when \n" +
                          "     $s : String( this == \"don2\" ) \n" +
                          "		$c : Core() \n" +
                          "then \n" +
                          "     delete( $s ); \n" +
                          "     don( $c, Mask2.class ); \n" +
                          "end\n" +
                          "" +
                          "rule \"Clear Again\" \n" +
                          "	when \n" +
                          "     $s : String( this == \"shed2\" ) \n" +
                          "		$m : Mask2() \n" +
                          "then \n" +
                          "     delete( $s ); \n" +
                          "     shed( $m, Mask2.class ); \n" +
                          "end\n" +
                          "" +
                          "" +
                          "rule Log \n" +
                          "when \n" +
                          " $t : Thing() \n" +
                          "then \n" +
                          "  System.out.println( \"Thing detected \" + $t ); \n" +
                          "  list.add( $t.getClass().getName() ); \n" +
                          "end \n" +
                          "";

        KieBase kbase = getKieBaseFromString(s1);
        TraitFactory.setMode( mode, kbase );
        ArrayList list = new ArrayList();

        KieSession knowledgeSession = kbase.newKieSession();
        knowledgeSession.setGlobal( "list", list );

        knowledgeSession.insert( "don1" );
        knowledgeSession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( Arrays.asList( "test.Mask.test.Core_Proxy" ), list );

        knowledgeSession.insert( "shed1" );
        knowledgeSession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( Arrays.asList( "test.Mask.test.Core_Proxy" ), list );

        knowledgeSession.insert( "don2" );
        knowledgeSession.fireAllRules();

        System.out.println( list );
        assertEquals( 2, list.size() );
        assertEquals( Arrays.asList( "test.Mask.test.Core_Proxy", "test.Mask2.test.Core_Proxy" ), list );

        knowledgeSession.insert( "shed2" );
        knowledgeSession.fireAllRules();

        assertEquals( 3, list.size() );
        assertEquals( Arrays.asList( "test.Mask.test.Core_Proxy", "test.Mask2.test.Core_Proxy", "org.drools.core.factmodel.traits.Thing.test.Core_Proxy" ), list );

    }


    @Test(timeout=10000)
    public void testTraitImplicitInsertionExceptionOnNonTraitable() throws InterruptedException {
        final String s1 = "package test;\n" +
                          "import org.drools.core.factmodel.traits.*; \n" +
                          "global java.util.List list;\n" +
                          "" +
                          "declare Core id : String  end\n" +  // should be @Traitable
                          "" +
                          "declare trait Mask  id : String end\n" +
                          "" +
                          "rule \"Don ItemStyle\"\n" +
                          "	when\n" +
                          "	then\n" +
                          "		don( new Core(), Mask.class );\n" +
                          "end\n" +
                          "" +
                          "";

        KieBase kbase = getKieBaseFromString(s1);
        TraitFactory.setMode( mode, kbase );
        ArrayList list = new ArrayList();

        KieSession knowledgeSession = kbase.newKieSession();
        knowledgeSession.setGlobal( "list", list );

        try {
            knowledgeSession.fireAllRules();
            fail( "Core is not declared @Traitable, this test should have thrown an exception" );
        } catch ( Exception csq ) {
            assertTrue( csq.getCause() instanceof IllegalStateException );
        }

    }


    @Trait
    public static interface SomeTrait<K> extends Thing<K> {
        public String getFoo();
        public void setFoo( String foo );
    }

    @Test(timeout=10000)
    public void testTraitLegacyTraitableWithLegacyTrait() {
        final String s1 = "package org.drools.compiler.factmodel.traits;\n" +
                          "import " + TraitTest.class.getName() + ".SomeTrait; \n" +
                          "import org.drools.core.factmodel.traits.*; \n" +
                          "global java.util.List list;\n" +
                          "" +
                          "rule \"Don ItemStyle\"\n" +
                          "	when\n" +
                          "	then\n" +
                          "		don( new StudentImpl(), SomeTrait.class );\n" +
                          "end\n";

        KieBase kbase = getKieBaseFromString(s1);
        TraitFactory.setMode( mode, kbase );
        ArrayList list = new ArrayList();

        KieSession knowledgeSession = kbase.newKieSession();
        knowledgeSession.setGlobal( "list", list );

        knowledgeSession.fireAllRules();

        assertEquals( 2, knowledgeSession.getObjects().size() );
    }

    @Test(timeout=10000)
    public void testIsALegacyTrait() {
        final String s1 = "package org.drools.compiler.factmodel.traits;\n" +
                          "import " + TraitTest.class.getName() + ".SomeTrait; \n" +
                          "import org.drools.core.factmodel.traits.*; \n" +
                          "global java.util.List list;\n" +
                          "" +
                          "declare trait IStudent end \n" +
                          "" +
                          "rule \"Don ItemStyle\"\n" +
                          "	when\n" +
                          "	then\n" +
                          "		insert( new StudentImpl() );\n" +
                          "		don( new Entity(), IStudent.class );\n" +
                          "end\n" +
                          "" +
                          "rule Check " +
                          " when " +
                          "  $s : StudentImpl() " +
                          "  $e : Entity( this isA $s ) " +
                          " then " +
                          "  list.add( 1 ); " +
                          " end ";

        KieBase kbase = getKieBaseFromString(s1);
        TraitFactory.setMode( mode, kbase );
        ArrayList list = new ArrayList();

        KieSession knowledgeSession = kbase.newKieSession();
        knowledgeSession.setGlobal( "list", list );

        knowledgeSession.fireAllRules();

        assertEquals( Arrays.asList( 1 ), list );
    }


    @Test(timeout=10000)
    public void testClassLiteralsWithOr() {

        String drl = "package org.drools.test; " +
                     "import org.drools.core.factmodel.traits.*; " +
                     "global java.util.List list; " +

                     "declare Foo " +
                     "@Traitable " +
                     "end " +

                     "declare trait A end " +
                     "declare trait B end " +

                     "rule Init " +
                     "when " +
                     "then " +
                     "  Foo f = new Foo(); " +
                     "  insert( f ); " +
                     "end " +

                     "rule One " +
                     "when " +
                     "  $f : Foo( this not isA A ) " +
                     "then " +
                     "  don( $f, A.class ); " +
                     "end " +

                     "rule Two " +
                     "when " +
                     "  $f : Foo( this not isA B ) " +
                     "then " +
                     "  don( $f, B.class ); " +
                     "end " +

                     "rule Check " +
                     "when " +
                     "    $f : Foo( this isA B || this isA A ) " +
                     "then " +
                     "  list.add( 1 ); " +
                     "end " +

                     "";


        KnowledgeBase kbase = loadKnowledgeBaseFromString( drl );
        TraitFactory.setMode( mode, kbase );
        ArrayList list = new ArrayList();

        KieSession ksession = kbase.newKieSession();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        assertEquals( Arrays.asList( 1 ), list );

    }



    @Test(timeout=10000)
    public void testIsASwappedArg() {

        String drl = "package org.drools.test; " +
                     "import org.drools.core.factmodel.traits.*; " +
                     "import org.drools.compiler.factmodel.traits.*; " +
                     "global java.util.List list; " +

                     "declare Foo " +
                     "@Traitable " +
                     "  object : Object " +
                     "end " +

                     "declare Bar " +
                     "@Traitable " +
                     "end " +

                     "declare trait IPerson end " +
                     "declare trait IStudent end " +

                     "rule Init " +
                     "when " +
                     "then " +
                     "  Foo f = new Foo( new StudentImpl() ); " +
                     "  don( f, IPerson.class ); " +
                     "end " +

                     "rule Match1 " +
                     "when " +
                     "  $f : Foo( $x : object ) " +
                     "  $p : StudentImpl( this isA $f ) from $x " +
                     "then " +
                     "  list.add( 1 ); " +
                     "end " +

                     "rule Match2 " +
                     "when " +
                     "  $f : Foo( $x : object ) " +
                     "  $p : StudentImpl( $f isA this ) from $x " +
                     "then " +
                     "  list.add( 2 ); " +
                     "end " +

                     "";


        KnowledgeBase kbase = loadKnowledgeBaseFromString( drl );
        TraitFactory.setMode( mode, kbase );
        ArrayList list = new ArrayList();

        KieSession ksession = kbase.newKieSession();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        assertEquals( 2, list.size() );
        assertTrue( list.contains( 1 ) );
        assertTrue( list.contains( 2 ) );

    }


    @Test(timeout=10000)
    public void testHierarchyEncodeOnPackageMerge() {

        String drl0 = "package org.drools.test; " +
                      "declare trait X end ";

        String drl1 = "package org.drools.test; " +
                     "import org.drools.core.factmodel.traits.*; " +
                     "global java.util.List list; " +

                     "declare trait A end " +
                     "declare trait B extends A end " +
                     "declare trait C extends B end " +

                     "";

        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        TraitFactory.setMode( mode, knowledgeBase );

        KnowledgeBuilder kb = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kb.add( new ByteArrayResource( drl0.getBytes() ), ResourceType.DRL );
        assertFalse( kb.hasErrors() );

        knowledgeBase.addKnowledgePackages( kb.getKnowledgePackages() );

        KnowledgeBuilder kb2 = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kb2.add( new ByteArrayResource( drl1.getBytes() ), ResourceType.DRL );
        System.out.print( kb2.getErrors() );
        assertFalse( kb2.hasErrors() );

        knowledgeBase.addKnowledgePackages( kb2.getKnowledgePackages() );

        HierarchyEncoder<String> hier = ( (KnowledgeBaseImpl) knowledgeBase ).getConfiguration().getComponentFactory().getTraitRegistry().getHierarchy();
        BitSet b = (BitSet) hier.getCode( "org.drools.test.B" ).clone();
        BitSet c = (BitSet) hier.getCode( "org.drools.test.C" ).clone();

        c.and( b );
        assertEquals( b, c );

    }




    @Test(timeout=10000)
    public void testDonThenReinsert() throws InterruptedException {
        final String s1 = "package test;\n" +
                          "import org.drools.core.factmodel.traits.*; \n" +
                          "import org.drools.compiler.factmodel.traits.TraitTest.TBean;\n" +
                          "global java.util.List list;\n" +
                          "" +
                          "declare TBean " +
                          " @Traitable " +
                          " @propertyReactive " +
                          "end " +
                          "" +
                          "declare trait Mask " +
                          " @propertyReactive " +
                          "end " +
                          "" +
                          "rule 'Don ItemStyle' " +
                          "	when\n" +
                          "     $e : TBean( ) " +
                          "	then " +
                          "     System.out.println( 'Don' ); " +
                          "		don( $e, Mask.class );\n" +
                          "end\n" +
                          "" +
                          "rule \"React\" \n" +
                          "	when \n" +
                          "		$m : Mask() \n" +
                          "then \n" +
                          "     System.out.println( $m ); \n" +
                          "end\n" +
                          "" +
                          "rule Zero when not Object() then System.out.println( 'Clean' ); end ";

        KieBase kbase = getKieBaseFromString(s1, EqualityBehaviorOption.IDENTITY);

        TraitFactory.setMode( mode, kbase );
        ArrayList list = new ArrayList();

        KieSession knowledgeSession = kbase.newKieSession();
        knowledgeSession.setGlobal( "list", list );
        TBean e = new TBean( "aaa" );

        int n = knowledgeSession.fireAllRules();
        assertEquals( 1, n );

        knowledgeSession.insert( e );
        n = knowledgeSession.fireAllRules();
        assertEquals( 2, n );

        knowledgeSession.insert( e );
        n = knowledgeSession.fireAllRules();
        assertEquals( 0, n );

        knowledgeSession.delete( knowledgeSession.getFactHandle( e ) );
        n = knowledgeSession.fireAllRules();
        assertEquals( 1, n );

        assertEquals( 0, knowledgeSession.getObjects().size() );

    }

    @Test
    public void testCastOnTheFly() throws InterruptedException {
        final String s1 = "package test; " +

                          "import org.drools.core.factmodel.traits.*; " +

                          "global java.util.List list; " +

                          "declare Foo " +
                          " @Traitable " +
                          " @propertyReactive " +
                          " id : int " +
                          "end " +

                          "declare trait Upper " +
                          " @propertyReactive " +
                          " id : int " +
                          "end " +

                          "declare trait Lower extends Upper " +
                          " @propertyReactive " +
                          "end " +

                          "rule Init " +
                          " dialect 'mvel' " +
                          "	when " +
                          "	then " +
                          "     Foo o = insert( new Foo( 42 ) ).as( Foo.class ); " +
                          "     list.add( o.getId() ); " +
                          "end " +

                          "rule Don " +
                          " when " +
                          "     $f : Foo() " +
                          " then " +
                          "     Lower l = don( $f, Lower.class ); " +
                          "     Upper u = bolster( $f ).as( Upper.class ); " +
                          "     list.add( u.getId() + 1 ); " +
                          " end ";

        KieBase kbase = getKieBaseFromString(s1);

        TraitFactory.setMode( mode, kbase );
        ArrayList list = new ArrayList();

        KieSession knowledgeSession = kbase.newKieSession();
        knowledgeSession.setGlobal( "list", list );

        knowledgeSession.fireAllRules();

        assertEquals( Arrays.asList( 42, 43 ), list );
    }



    @Test
    public void testDonModify() {
        String drl =
                "import org.drools.core.factmodel.traits.Entity;\n" +
                "import org.drools.compiler.factmodel.traits.IPerson;\n" +
                "import org.drools.compiler.factmodel.traits.IStudent;\n" +

                "declare trait IPerson end\n" +

                "declare trait IStudent end\n" +

                "declare trait Person\n" +
                "    name : String\n" +
                "end\n" +

                "rule \"Init\"\n" +
                "when\n" +
                "then\n" +
                "    Entity core = new Entity();\n" +
                "    insert( core );\n" +
                "end\n" +

                "rule Trait when\n" +
                "    $core: Entity( )\n" +
                "then\n" +
                "    IPerson x = don( $core, IPerson.class, true );\n" +
                "    IStudent s = don( $core, IStudent.class, true );\n" +
                "    Person p = don( $core, Person.class, true );\n" +
                "    System.err.println( \"            Done donning              \" );\n " +
                "end\n" +

                "rule R2 when\n" +
                "    $p: IPerson( name == null )\n" +
                "then\n" +
                "    System.out.println(\"IPerson: \" + $p);\n" +
                "end\n" +
                "\n";

        KieBase kbase = getKieBaseFromString( drl );
        TraitFactory.setMode( mode, kbase );
        KieSession kSession = kbase.newKieSession();

        assertEquals( 3, kSession.fireAllRules() );
    }

    @Test
    public void testAlphaNodeSharing() {
        String drl =
                "package test; " +
                "import " + Entity.class.getName() + " " +

                "declare trait Person\n" +
                "    name : String\n" +
                "end\n" +

                "rule Init " +
                "when " +
                "then " +
                "    don( new Entity(), Person.class ); " +
                "end\n" +

                "rule One when" +
                "    $core: Entity( this isA Person ) " +
                "then " +
                "end " +

                "rule Two when" +
                "    $core: Entity( this isA Person ) " +
                "then " +
                "end " +

                "\n";

        final KieBase kbase = getKieBaseFromString( drl );
        TraitFactory.setMode( mode, kbase );
        KieSession kSession = kbase.newKieSession();

        assertEquals( 3, kSession.fireAllRules() );
        NamedEntryPoint nep = ( (NamedEntryPoint) kSession.getEntryPoint( EntryPointId.DEFAULT.getEntryPointId() ) );
        ObjectTypeNode otn = nep.getEntryPointNode().getObjectTypeNodes().get( new ClassObjectType( Entity.class ) );
        assertNotNull( otn );
        assertEquals( 1, otn.getObjectSinkPropagator().getSinks().length );
    }


    @Test
    public void testPartitionWithSiblingsOnDelete() {
        String drl =
                "import " + Entity.class.getName() + ";" +
                "global java.util.List list; " +

                "declare trait A @propertyReactive end " +
                "declare trait B extends A @propertyReactive end " +
                "declare trait C extends A @propertyReactive end " +

                "rule Trait when " +
                "    $core: Entity( ) " +
                "then " +
                "    don( $core, A.class ); " +
                "    don( $core, B.class ); " +
                "    don( $core, C.class ); " +
                "end " +

                "rule Shed when " +
                "   $s: String() " +
                "   $core : Entity() " +
                "then " +
                "   shed( $core, C.class ); " +
                "end " +

                "rule RA when A() then list.add( 'A' ); end " +
                "rule RB when B() then list.add( 'B' ); end " +
                "rule RC when C() then list.add( 'C' ); end " +
                " ";

        KieBase kbase = getKieBaseFromString( drl );
        TraitFactory.setMode( mode, kbase );
        KieSession ksession = kbase.newKieSession();

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        Entity e = new Entity();
        ksession.insert( e );
        ksession.fireAllRules();

        ksession.insert( "go" );
        ksession.fireAllRules();

        Set<BitSet> s = checkOTNPartitioning( e, ksession );
        assertEquals( 2, s.size() );

        assertEquals( Arrays.asList( 'A', 'B', 'C' ), list );
    }


    @Test
    public void testTupleIntegrityOnModification() {
        String drl = "package test " +
                     "import " + Entity.class.getName() + ";" +
                     "global java.util.List list; " +

                     "declare trait A @propertyReactive value : int end " +

                     "rule Trait when " +
                     "    $core: Entity( ) " +
                     "then " +
                     "    A o = don( $core, A.class ); " +
                     "    System.out.println( 'Found ! ' + o ); " +
                     "end " +

                     "rule Test when " +
                     "   $x: A( value == 0 ) " +
                     "then " +
                     "   list.add( 0 ); " +
                     "end " +

                     "rule Check when " +
                     "   $x: A( value == 42 ) " +
                     "then " +
                     "   list.add( 42 ); " +
                     "end " +

                     "rule Mood when " +
                     "  $x : A( value != 42 ) " +
                     "then " +
                     "  modify ( $x ) { setValue( 42 ); } " +
                     "end ";

        KieBase kbase = getKieBaseFromString( drl );
        TraitFactory.setMode( mode, kbase );
        KieSession ksession = kbase.newKieSession();
        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.insert( new Entity() );
        ksession.fireAllRules();

        for ( final Object o : ksession.getObjects( new ObjectFilter() {
            @Override
            public boolean accept( Object object ) {
                return object.getClass().getName().contains( "test.A" );
            }
        } ) ) {
            InternalFactHandle handle = (InternalFactHandle) ksession.getFactHandle( o );
            LeftTuple first = handle.getFirstLeftTuple();
            assertTrue( first instanceof RuleTerminalNodeLeftTuple );
            assertEquals( "Check", ((RuleTerminalNodeLeftTuple) first).getRule().getName() );
        }

        assertEquals( Arrays.asList( 0, 42 ), list );
    }

    @Test
    public void testShedVacancy() {
        String drl = "package org.drools.test " +
                     "import " + Entity.class.getName() + ";" +
                     "global java.util.List list; " +

                     "declare trait A @propertyReactive end " +
                     "declare trait B @propertyReactive end " +
                     "declare trait C extends A,B @propertyReactive end " +
                     "declare trait D extends B @propertyReactive end " +

                     "rule Trait when " +
                     "then " +
                     "    Entity e = new Entity( 'x1' ); " +
                     "    don( e, C.class ); " +
                     "    don( e, D.class ); " +
                     "end " +

                     "rule Mood when " +
                     "  $x : B() " +
                     "then " +
                     "  System.out.println( 'Found B' ); " +
                     "end " +

                     "rule Shed when " +
                     "  $s : String() " +
                     "  $x : Entity() " +
                     "then " +
                     "  delete( $s ); " +
                     "  shed( $x, A.class ); " +
                     "end " +
                     "";

        KieBase kbase = getKieBaseFromString( drl );
        TraitFactory.setMode( mode, kbase );
        KieSession ksession = kbase.newKieSession();
        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        HierarchyEncoder<String> hier = ( (KnowledgeBaseImpl) kbase ).getConfiguration().getComponentFactory().getTraitRegistry().getHierarchy();
        BitSet a = (BitSet) hier.getCode( "org.drools.test.A" ).clone();
        BitSet b = (BitSet) hier.getCode( "org.drools.test.B" ).clone();
        BitSet c = (BitSet) hier.getCode( "org.drools.test.C" ).clone();
        BitSet d = (BitSet) hier.getCode( "org.drools.test.D" ).clone();

        int n = ksession.fireAllRules();
        assertEquals( 2, n );

        System.err.print( "---------------------------------------------------------------\n\n\n " );

        int counter = 0;
        for ( Object o : ksession.getObjects() ) {
            if ( o instanceof TraitProxy ) {
                TraitProxy tp = (TraitProxy) o;
                if ( tp._getTypeCode().equals( c ) ) {
                    assertEquals( 1, tp.listAssignedOtnTypeCodes().size() );
                    assertTrue( tp.listAssignedOtnTypeCodes().contains( b ) );
                    counter++;
                } else if ( tp._getTypeCode().equals( d ) ) {
                    assertTrue( tp.listAssignedOtnTypeCodes().isEmpty() );
                    counter++;
                }
            } else if ( o instanceof TraitableBean ) {
                TraitableBean tb = (TraitableBean) o;
                System.out.println( tb.getCurrentTypeCode() );
                counter++;
            }
        }
        assertEquals( 3, counter );


        ksession.insert( "go" );
        ksession.fireAllRules();

        System.err.print( "---------------------------------------------------------------\n\n\n " );

        int counter2 = 0;
        for ( Object o : ksession.getObjects() ) {
            if ( o instanceof TraitProxy ) {
                TraitProxy tp = (TraitProxy) o;
                assertEquals( d, tp._getTypeCode() );
                assertEquals( 1, tp.listAssignedOtnTypeCodes().size() );
                assertTrue( tp.listAssignedOtnTypeCodes().contains( b ) );
                counter2++;
            } else if ( o instanceof TraitableBean ) {
                TraitableBean tb = (TraitableBean) o;
                assertEquals( d, tb.getCurrentTypeCode() );
                counter2++;
            }
        }
        assertEquals( 2, counter2 );
    }


    @Test
    public void testExternalUpdateWithProxyRefreshInEqualityMode() {
        String drl = "package org.drools.trait.test; " +
                     "import " + ExtEntity.class.getCanonicalName() + "; " +
                     "global " + List.class.getName() + " list; " +

                     "declare trait Mask " +
                     "  id  : String " +
                     "  num : int " +
                     "end " +

                     "rule Don when " +
                     "  $x : ExtEntity( $id : id ) " +
                     "then " +
                     "  list.add( $id ); " +
                     "  don( $x, Mask.class ); " +
                     "end " +

                     "rule Test when " +
                     "  Mask( $n : num ) " +
                     "then " +
                     "  list.add( $n ); " +
                     "end ";

        KieBase kbase = getKieBaseFromString( drl, EqualityBehaviorOption.EQUALITY );
        TraitFactory.setMode( mode, kbase );

        KieSession ksession = kbase.newKieSession();
        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        FactHandle handle = ksession.insert( new ExtEntity( "x1", 42 ) );
        ksession.fireAllRules();

        ksession.update( handle, new ExtEntity( "x1", 35 ) );
        ksession.fireAllRules();

        assertEquals( Arrays.asList( "x1", 42, "x1", 42 ), list );
    }


    @Test(timeout=10000)
    public void testIsAInstanceOf() {

        String drl = "package org.drools.test; " +
                     "import " + StudentImpl.class.getName() + "; " +
                     "import " + IStudent.class.getName() + "; " +
                     "global java.util.List list; " +

                     "rule Test1 " +
                     "when " +
                     "  StudentImpl( this isA IStudent.class ) " +
                     "then list.add( 1 ); end " +

                     "rule Test2 " +
                     "when " +
                     "  IStudent( this isA StudentImpl.class ) " +
                     "then list.add( 2 ); end " +

                     "";

        KieBase kbase = getKieBaseFromString( drl );
        List list = new ArrayList(  );
        TraitFactory.setMode( mode, kbase );

        KieSession knowledgeSession = kbase.newKieSession();
        knowledgeSession.setGlobal( "list", list );
        knowledgeSession.insert( new StudentImpl(  ) );

        assertEquals( 2, knowledgeSession.fireAllRules() );
        assertEquals( Arrays.asList( 1, 2 ), list );
    }


    @Test(timeout=10000)
    public void testIsAInstanceOfNonTraitable() {

        String drl = "package org.drools.test; " +
                     "global java.util.List list; " +

                     "rule Test1 " +
                     "when " +
                     "  Object( this isA String.class ) " +
                     "then list.add( 1 ); end " +

                     "";

        KieBase kbase = getKieBaseFromString( drl );
        List list = new ArrayList(  );
        TraitFactory.setMode( mode, kbase );

        KieSession knowledgeSession = kbase.newKieSession();
        knowledgeSession.setGlobal( "list", list );
        knowledgeSession.insert( "hello" );

        assertEquals( 1, knowledgeSession.fireAllRules() );
        assertEquals( Arrays.asList( 1 ), list );
    }



    @Traitable
    @PropertyReactive
    public static class ExtEntity extends Entity {
        private int num;

        public int getNum() {
            return num;
        }

        public void setNum( int num ) {
            this.num = num;
        }

        public ExtEntity( String id, int num ) {
            super( id );
            this.num = num;
        }
    }

    protected Set<BitSet> checkOTNPartitioning( TraitableBean core, KieSession wm ) {
        Set<BitSet> otns = new HashSet<BitSet>();

        for ( Object o : core._getTraitMap().values() ) {
            TraitProxy tp = (TraitProxy) o;
            Set<BitSet> localNodes = tp.listAssignedOtnTypeCodes();

            for ( BitSet code : localNodes ) {
                assertFalse( otns.contains( code ) );
                otns.add( code );
            }
        }

        return otns;
    }

    @Test(timeout=10000)
    public void testSerializeKieBaseWithTraits() {
        // DRL-1123
        String drl = "package org.drools.test; " +
                     "import " + StudentImpl.class.getName() + "; " +
                     "import " + IStudent.class.getName() + "; " +
                     "global java.util.List list; " +

                     "rule Test1 " +
                     "when " +
                     "  StudentImpl( this isA IStudent.class ) " +
                     "then list.add( 1 ); end " +

                     "rule Test2 " +
                     "when " +
                     "  IStudent( this isA StudentImpl.class ) " +
                     "then list.add( 2 ); end " +

                     "";

        KieBase kbase = getKieBaseFromString( drl );

        try {
            kbase = SerializationHelper.serializeObject( kbase, ((InternalKnowledgeBase) kbase).getRootClassLoader() );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        List list = new ArrayList(  );
        TraitFactory.setMode( mode, kbase );

        KieSession knowledgeSession = kbase.newKieSession();
        knowledgeSession.setGlobal( "list", list );
        knowledgeSession.insert( new StudentImpl(  ) );

        assertEquals( 2, knowledgeSession.fireAllRules() );
        assertEquals( Arrays.asList( 1, 2 ), list );
    }

    @Test(timeout=10000)
    public void testMixin2() {
        String drl =
                 "package org.drools.test.traits\n" +
                 "import " + Scholar.class.getCanonicalName() + ";\n" +
                 "import " + ScholarImpl.class.getCanonicalName() + ";\n" +
                 "\n" +
                 "\n" +
                 "declare Person\n" +
                 "    @Traitable\n" +
                 "    name    : String       = \"john\"     @key\n" +
                 "    age     : int          = 18\n" +
                 "    weight  : Double       = 75.4\n" +
                 "end\n" +
                 "\n" +
                 "declare Scholar end\n" +
                 "\n" +
                 "declare trait Student extends Scholar\n" +
                 "    name    : String\n" +
                 "    age     : int\n" +
                 "    weight  : Double\n" +
                 "    school  : String\n" +
                 "end\n" +
                 "\n" +
                 "\n" +
                 "rule \"Zero\"\n" +
                 "when\n" +
                 "then\n" +
                 "    insert( new Person() );\n" +
                 "end\n" +
                 "\n" +
                 "\n" +
                 "rule \"Student\"\n" +
                 "no-loop\n" +
                 "when\n" +
                 "    $p : Person( $name : name, $age : age < 25, $weight : weight )\n" +
                 "then\n" +
                 "    Student s = don( $p, Student.class );\n" +
                 "        s.setSchool( \"SomeSchool\" );\n" +
                 "        s.learn( \" AI \" );\n" +
                 "end\n";

        KieSession ks = getSessionFromString( drl );
        TraitFactory.setMode( mode, ks.getKieBase() );

        ks.fireAllRules();
    }

    @Trait( impl = ScholarImpl.class )
    public interface Scholar<K>  {
        void learn(String subject);
    }

    public static class ScholarImpl<K> implements Scholar<K> {
        private Thing<K> core;

        public ScholarImpl() { }

        public ScholarImpl( Thing<K> arg ) {
            this.core = arg;
        }

        public void learn( String subject ) {
            System.out.println( "I " + core.getFields().get( "name" ) + ", now know everything about " + subject );
        }
    }

    @Trait( impl = YImpl.class )
    public interface Y {
        String getShared();
        String getYValue();
    }

    public static class YImpl implements Y {

        @Override
        public String getShared() {
            return "Y";
        }

        @Override
        public String getYValue() {
            return "Y";
        }
    }

    @Trait( impl = ZImpl.class )
    public interface Z {
        String getShared();
        String getZValue();
    }

    public static class ZImpl implements Z {

        @Override
        public String getShared() {
            return "Z";
        }

        @Override
        public String getZValue() {
            return "Z";
        }
    }

    @Test(timeout=10000)
    public void testMixinWithConflictsUsingDeclarationOrder() {
        checkMixinResolutionUsesOrder("Y,Z", "Y");
        checkMixinResolutionUsesOrder("Z,Y", "Z");
    }

    private void checkMixinResolutionUsesOrder(String interfaces, String first) {
        String drl =
                "package org.drools.test.traits\n" +
                "import " + Y.class.getCanonicalName() + ";\n" +
                "import " + Z.class.getCanonicalName() + ";\n" +
                "\n" +
                "global java.util.List list;" +
                "\n" +
                "declare Bean\n" +
                "    @Traitable\n" +
                "    name    : String       = \"xxx\"     @key\n" +
                "end\n" +
                "\n" +
                "\n" +
                "declare X extends " + interfaces + " @Trait( mixinSolveConflicts = Trait.MixinConflictResolutionStrategy.DECLARATION_ORDER ) end\n" +
                "\n" +
                "rule Init when\n" +
                "then\n" +
                "    insert( new Bean() );\n" +
                "end\n" +
                "\n" +
                "rule Exec no-loop when\n" +
                "    $b : Bean()\n" +
                "then\n" +
                "    X x = don( $b, X.class );\n" +
                "    list.add( x.getYValue() );\n" +
                "    list.add( x.getZValue() );\n" +
                "    list.add( x.getShared() );\n" +
                "end\n";

        KieSession ks = getSessionFromString( drl );
        TraitFactory.setMode( mode, ks.getKieBase() );

        List<String> list = new ArrayList<String>();
        ks.setGlobal( "list", list );

        ks.fireAllRules();

        System.out.println(list);
        assertEquals( "Y", list.get(0) );
        assertEquals( "Z", list.get(1) );
        assertEquals( first, list.get(2) );
    }

    @Test(timeout=10000)
    public void testMixinWithConflictsThrowingError() {
        String drl =
                "package org.drools.test.traits\n" +
                "import " + Y.class.getCanonicalName() + ";\n" +
                "import " + Z.class.getCanonicalName() + ";\n" +
                "\n" +
                "global java.util.List list;" +
                "\n" +
                "declare Bean\n" +
                "    @Traitable\n" +
                "    name    : String       = \"xxx\"     @key\n" +
                "end\n" +
                "\n" +
                "\n" +
                "declare X extends Y,Z @Trait( mixinSolveConflicts = Trait.MixinConflictResolutionStrategy.ERROR_ON_CONFLICT ) end\n" +
                "\n" +
                "rule Init when\n" +
                "then\n" +
                "    insert( new Bean() );\n" +
                "end\n" +
                "\n" +
                "rule Exec no-loop when\n" +
                "    $b : Bean()\n" +
                "then\n" +
                "    X x = don( $b, X.class );\n" +
                "    list.add( x.getYValue() );\n" +
                "    list.add( x.getZValue() );\n" +
                "    list.add( x.getShared() );\n" +
                "end\n";

        KieSession ks = getSessionFromString( drl );
        TraitFactory.setMode( mode, ks.getKieBase() );

        List<String> list = new ArrayList<String>();
        ks.setGlobal( "list", list );

        try {
            ks.fireAllRules();
            fail("don should fail due to the conflict in getShared() method");
        } catch (Exception e) { }
    }
}
