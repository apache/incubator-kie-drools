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

import org.drools.compiler.Address;
import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.drools.compiler.TestEnum;
import org.drools.core.base.ClassFieldReader;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.extractors.MVELObjectClassFieldReader;
import org.drools.core.base.mvel.MVELDebugHandler;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.rule.MapBackedClassLoader;
import org.drools.core.rule.constraint.MvelConstraint;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.FieldValue;
import org.drools.core.util.DateUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class MVELTest extends CommonTestMethodBase {
    
    @Test
    public void testHelloWorld() throws Exception {
        // read in the source
        KieBase kbase = loadKnowledgeBase("test_mvel.drl");
        KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal( "list",
                                 list );

        final List list2 = new ArrayList();
        ksession.setGlobal( "list2",
                                 list2 );

        Cheese c = new Cheese( "stilton",
                               10 );
        ksession.insert( c );
        ksession.fireAllRules();
        assertEquals( 2,
                      list.size() );
        assertEquals( BigInteger.valueOf( 30 ),
                      list.get( 0 ) );
        assertEquals( Integer.valueOf( 22 ),
                      list.get( 1 ) );

        assertEquals( "hello world",
                      list2.get( 0 ) );

        Date dt = DateUtils.parseDate( "10-Jul-1974" );
        assertEquals( dt,
                      c.getUsedBy() );
    }

    @Test
    public void testIncrementOperator() throws Exception {
        String str = "";
        str += "package org.kie \n";
        str += "global java.util.List list \n";
        str += "rule rule1 \n";
        str += "    dialect \"mvel\" \n";
        str += "when \n";
        str += "    $I : Integer() \n";
        str += "then \n";
        str += "    i = $I.intValue(); \n";
        str += "    i += 5; \n";
        str += "    list.add( i ); \n";
        str += "end \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        assertFalse( kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );
        ksession.insert( 5 );

        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );
        assertEquals( 10,
                      list.get( 0 ) );
    }

    @Test
    public void testEvalWithBigDecimal() throws Exception {
        String str = "";
        str += "package org.kie \n";
        str += "import java.math.BigDecimal; \n";
        str += "global java.util.List list \n";
        str += "rule rule1 \n";
        str += "    dialect \"mvel\" \n";
        str += "when \n";
        str += "    $bd : BigDecimal() \n";
        str += "    eval( $bd.compareTo( BigDecimal.ZERO ) > 0 ) \n";
        str += "then \n";
        str += "    list.add( $bd ); \n";
        str += "end \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            System.err.println( kbuilder.getErrors() );
        }
        assertFalse( kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );
        ksession.insert( new BigDecimal( 1.5 ) );

        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );
        assertEquals( new BigDecimal( 1.5 ),
                      list.get( 0 ) );
    }

    @Test
    public void testLocalVariableMVELConsequence() throws Exception {
        KieBase kbase = loadKnowledgeBase("test_LocalVariableMVELConsequence.drl");
        KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal( "results",
                                 list );

        ksession.insert( new Person( "bob",
                                          "stilton" ) );
        ksession.insert( new Person( "mark",
                                          "brie" ) );

        try {
            ksession.fireAllRules();

            assertEquals( "should have fired twice",
                          2,
                          list.size() );

        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "Should not raise any exception" );
        }

    }

    @Test
    public void testMVELUsingGlobalsInDebugMode() throws Exception {
        MVELDebugHandler.setDebugMode( true );
        try {
            KieBase kbase = loadKnowledgeBase("test_MVELGlobalDebug.drl");
            KieSession ksession = kbase.newKieSession();
            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, false);
            ksession.dispose();
            MVELDebugHandler.setDebugMode( false );
        } catch ( Exception e ) {
            MVELDebugHandler.setDebugMode( false );
            e.printStackTrace();
            fail( "Should not raise exceptions" );
        }

    }

    @Test
    public void testDuplicateLocalVariableMVELConsequence() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newInputStreamResource(getClass().getResourceAsStream("test_DuplicateLocalVariableMVELConsequence.drl")), ResourceType.DRL);

        assertTrue( kbuilder.hasErrors() );
    }

    @Test
    public void testArrays() throws Exception {
        String text = "package test_mvel;\n";
        text += "import " + TestObject.class.getCanonicalName() + ";\n";
        text += "import function " + TestObject.class.getCanonicalName() + ".array;\n";
        text += "no-loop true\n";
        text += "dialect \"mvel\"\n";
        text += "rule \"1\"\n";
        text += "salience 1\n";
        text += "when\n";
        text += "    $fact: TestObject()\n";
        text += "    eval($fact.checkHighestPriority(\"mvel\", 2))\n";
        text += "    eval($fact.stayHasDaysOfWeek(\"mvel\", false, new String[][]{{\"2008-04-01\", \"2008-04-10\"}}))\n";
        text += "then\n";
        text += "    $fact.applyValueAddPromo(1,2,3,4,\"mvel\");\n";
        text += "end";

        KieBase kieBase = loadKnowledgeBaseFromString(text.replaceAll( "mvel", "java" ), text);
        StatelessKieSession statelessKieSession = kieBase.newStatelessKieSession();

        List<String> list = new ArrayList<String>();
        statelessKieSession.execute(new TestObject(list));
        
        assertEquals( 6, list.size() );

        assertTrue(list.containsAll( Arrays.asList("TestObject.checkHighestPriority: java|2",
                                                   "TestObject.stayHasDaysOfWeek: java|false|[2008-04-01, 2008-04-10]",
                                                   "TestObject.checkHighestPriority: mvel|2",
                                                   "TestObject.stayHasDaysOfWeek: mvel|false|[2008-04-01, 2008-04-10]",
                                                   "TestObject.applyValueAddPromo: 1|2|3|4|mvel",
                                                   "TestObject.applyValueAddPromo: 1|2|3|4|java") ));
    }
    
    @Test
    public void testPackageImports() throws Exception {
        String str = "";
        str += "package org.kie \n";
        str += "dialect \"mvel\"\n";
        str += "import org.acme.healthcare.* \n";
        str += "import org.acme.insurance.* \n";
        str += "import org.acme.sensors.SensorReading \n";
        str += "rule rule1 \n";
        str += "  when \n";
        str += "    eval(true)\n";
        str += "  then \n";
        str += "    insert(new Claim());         // from org.acme.healthcare.* \n";
        str += "    insert(new Policy());        // from org.acme.insurance.* \n";
        str += "    insert(new SensorReading()); // from org.acme.sensor.SensorReading \n";
        str += "end\n";
        
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        if (kbuilder.hasErrors()) {
          throw new RuntimeException(kbuilder.getErrors().toString());
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        int result = ksession.fireAllRules();
        
        assertEquals(1, result);
        Collection<? extends Object> insertedObjects = ksession.getObjects();
        assertEquals(3, insertedObjects.size());
    }
    
    @Test
    public void testSizeCheckInObject() {
        String str = ""+
        "package org.drools.compiler.test \n" +
        "import " + Triangle.class.getCanonicalName() + "\n" +
        //"import " + Address.class.getCanonicalName() + "\n" +
        "global java.util.List list \n" +
        "rule \"show\" \n" + 
        "when  \n" + 
        "    $m : Triangle( deliveries.size == 0) \n" + 
        "then \n" + 
        "    list.add('r1'); \n" + 
        "end \n";
         KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
         kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
         
         if ( kbuilder.hasErrors() ) {
             System.out.println( kbuilder.getErrors().toString()  );
             fail( kbuilder.getErrors().toString());
         }
    }
    
    
    @Test
    public void testNestedEnum() {
        String str = ""+
           "package org.drools.compiler.test \n" +
           "import " + Triangle.class.getCanonicalName() + "\n" +
           "global java.util.List list \n" +
           "rule \"show\" \n" + 
           "when  \n" + 
           "    $t: Triangle(t == Triangle.Type.ACUTE) \n" + 
           "then \n" + 
           "    list.add($t.getT()); \n" + 
           "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
 
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        Triangle t = new Triangle( Triangle.Type.ACUTE);
        ksession.insert( t );
        
        ksession.fireAllRules();    
        
        assertEquals(Triangle.Type.ACUTE, list.get(0) );
    }
    
    @Test
    public void testNestedEnumWithMap() {
        String str = ""+
           "package org.drools.compiler.test \n" +
           "import " + DMap.class.getCanonicalName() + " \n" +
           "import " + Triangle.class.getCanonicalName() + "\n" +
           "global java.util.List list \n" +
           "rule \"show\" \n" + 
           "when  \n" + 
           "    $m : DMap( this[Triangle.Type.ACUTE] == 'xxx') \n" + 
           "then \n" + 
           "    list.add('r1'); \n" + 
           "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
 
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        
        DMap m = new DMap();
        m.put(  Triangle.Type.ACUTE, "xxx" );
        
        ksession.insert( m );
        
        ksession.fireAllRules();    
        
        assertEquals( "r1", list.get(0) );
    } 
    
    @Test
    public void testNewConstructor() {
        String str = ""+
           "package org.drools.compiler.test \n" +
           "import " + Person.class.getCanonicalName() + "\n" +
           "import " + Address.class.getCanonicalName() + "\n" +
           "global java.util.List list \n" +
           "rule \"show\" \n" + 
           "when  \n" + 
           "    $m : Person( address == new Address('s1')) \n" + 
           "then \n" + 
           "    list.add('r1'); \n" + 
           "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
 
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        
        Person p = new Person("yoda");
        p.setAddress( new Address("s1") );
        
        ksession.insert( p );
        
        ksession.fireAllRules();    
        
        assertEquals( "r1", list.get(0) );
        
        // Check it was built with MVELReturnValueExpression constraint
        List<ObjectTypeNode> nodes = ((InternalKnowledgeBase)kbase).getRete().getObjectTypeNodes();
        ObjectTypeNode node = null;
        for ( ObjectTypeNode n : nodes ) {
            if ( ((ClassObjectType)n.getObjectType()).getClassType() == Person.class ) {
                node = n;
                break;
            }
        }
        
        AlphaNode alphanode = (AlphaNode) node.getSinkPropagator().getSinks()[0];
        AlphaNodeFieldConstraint constraint = alphanode.getConstraint();

        if (constraint instanceof MvelConstraint) {
            assertTrue( (( MvelConstraint )constraint).getFieldExtractor() instanceof ClassFieldReader );
            FieldValue r = (( MvelConstraint )constraint).getField();
            assertEquals( p.getAddress(), r.getValue() );
        }
    }         
    
    @Test
    public void testArrayAccessorWithGenerics() {
        String str = ""+
           "package org.drools.compiler.test \n" +
           "import " + Person.class.getCanonicalName() + "\n" +
           "import " + Address.class.getCanonicalName() + "\n" +
           "global java.util.List list \n" +
           "rule \"show\" \n" + 
           "when  \n" + 
           "    $m : Person( addresses[0] == new Address('s1'), addresses[0].street == new Address('s1').street ) \n" + 
           "then \n" + 
           "    list.add('r1'); \n" + 
           "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
 
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        
        Person p = new Person("yoda");
        p.addAddress( new Address("s1") );
        
        ksession.insert( p );
        
        ksession.fireAllRules();    
        
        assertEquals( "r1", list.get(0) );
        
        // Check it was built with MVELReturnValueExpression constraint
        List<ObjectTypeNode> nodes = ((InternalKnowledgeBase)kbase).getRete().getObjectTypeNodes();
        ObjectTypeNode node = null;
        for ( ObjectTypeNode n : nodes ) {
            if ( ((ClassObjectType)n.getObjectType()).getClassType() == Person.class ) {
                node = n;
                break;
            }
        }
        
        AlphaNode alphanode = (AlphaNode) node.getSinkPropagator().getSinks()[0];        
        AlphaNodeFieldConstraint constraint = alphanode.getConstraint();

        if (constraint instanceof MvelConstraint) {
            assertTrue( ((MvelConstraint)constraint).getFieldExtractor() instanceof MVELObjectClassFieldReader );
            assertEquals( new Address("s1"), (( MvelConstraint )constraint).getField().getValue() );
        }

        alphanode = (AlphaNode) alphanode.getSinkPropagator().getSinks()[0];
        constraint = alphanode.getConstraint();

        if (constraint instanceof MvelConstraint) {
            assertTrue( (( MvelConstraint )constraint).getFieldExtractor() instanceof MVELObjectClassFieldReader );
            assertEquals( new Address("s1").getStreet(), (( MvelConstraint )constraint).getField().getValue() );
        }
    }    
    
    @Test
    public void testArrayAccessorWithStaticFieldAccess() {
        String str = ""+
           "package org.drools.compiler.test \n" +
           "import " + Person.class.getCanonicalName() + "\n" +
           "import " + Address.class.getCanonicalName() + "\n" +
           "import " + Triangle.class.getCanonicalName() + "\n" +
           "global java.util.List list \n" +
           "rule \"show\" \n" + 
           "when  \n" + 
           "    $m : Person( addresses[Triangle.ZERO] == new Address('s1'), addresses[Triangle.ZERO].street == new Address('s1').street ) \n" + 
           "then \n" + 
           "    list.add('r1'); \n" + 
           "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
 
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        
        Person p = new Person("yoda");
        p.addAddress( new Address("s1") );
        
        ksession.insert( p );
        
        ksession.fireAllRules();    
        
        assertEquals( "r1", list.get(0) );
        
        // Check it was built with MVELReturnValueExpression constraint
        List<ObjectTypeNode> nodes = ((InternalKnowledgeBase)kbase).getRete().getObjectTypeNodes();
        ObjectTypeNode node = null;
        for ( ObjectTypeNode n : nodes ) {
            if ( ((ClassObjectType)n.getObjectType()).getClassType() == Person.class ) {
                node = n;
                break;
            }
        }
        
        AlphaNode alphanode = (AlphaNode) node.getSinkPropagator().getSinks()[0];        
        AlphaNodeFieldConstraint constraint = alphanode.getConstraint();

        if (constraint instanceof MvelConstraint) {
            assertTrue( (( MvelConstraint ) alphanode.getConstraint()).getFieldExtractor() instanceof MVELObjectClassFieldReader );
            assertEquals( new Address("s1"), (( MvelConstraint ) alphanode.getConstraint()).getField().getValue() );
        }

        alphanode = (AlphaNode) alphanode.getSinkPropagator().getSinks()[0];
        constraint = alphanode.getConstraint();
        if (constraint instanceof MvelConstraint) {
            assertTrue( (( MvelConstraint ) alphanode.getConstraint()).getFieldExtractor() instanceof MVELObjectClassFieldReader );
            assertEquals( new Address("s1").getStreet(), (( MvelConstraint ) alphanode.getConstraint()).getField().getValue() );
        }
    }       
    
    @Test
    public void testMapAccessorWithStaticFieldAccess() {
        String str = ""+
           "package org.drools.compiler.test \n" +
           "import " + Person.class.getCanonicalName() + "\n" +
           "import " + Address.class.getCanonicalName() + "\n" +
           "import " + TestEnum.class.getCanonicalName() + "\n" +
           "global java.util.List list \n" +
           "rule \"show\" \n" + 
           "when  \n" + 
           "    $m : Person( namedAddresses[TestEnum.ONE] == new Address('s1'), namedAddresses[TestEnum.ONE].street == new Address('s1').street ) \n" + 
           "then \n" + 
           "    list.add('r1'); \n" + 
           "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL );
        
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
 
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        
        Person p = new Person("yoda");
        p.getNamedAddresses().put( TestEnum.ONE,  new Address("s1") );
        
        ksession.insert( p );
        
        ksession.fireAllRules();    
        
        assertEquals( "r1", list.get(0) );
        
        // Check it was built with MVELReturnValueExpression constraint
        List<ObjectTypeNode> nodes = ((InternalKnowledgeBase)kbase).getRete().getObjectTypeNodes();
        ObjectTypeNode node = null;
        for ( ObjectTypeNode n : nodes ) {
            if ( ((ClassObjectType)n.getObjectType()).getClassType() == Person.class ) {
                node = n;
                break;
            }
        }
        
        AlphaNode alphanode = (AlphaNode) node.getSinkPropagator().getSinks()[0];        
        AlphaNodeFieldConstraint constraint = alphanode.getConstraint();

        if (constraint instanceof MvelConstraint) {
            assertTrue( (( MvelConstraint ) alphanode.getConstraint()).getFieldExtractor() instanceof MVELObjectClassFieldReader );
            assertEquals( new Address("s1"), (( MvelConstraint ) alphanode.getConstraint()).getField().getValue() );
        }

        alphanode = (AlphaNode) alphanode.getSinkPropagator().getSinks()[0];
        constraint = alphanode.getConstraint();

        if (constraint instanceof MvelConstraint) {
            assertTrue( (( MvelConstraint ) alphanode.getConstraint()).getFieldExtractor() instanceof MVELObjectClassFieldReader );
            assertEquals( new Address("s1").getStreet(), (( MvelConstraint ) alphanode.getConstraint()).getField().getValue() );
        }
    }     
    
    @Test
    public void testArrayAccessorWithoutGenerics() {
        String str = ""+
           "package org.drools.compiler.test \n" +
           "import " + Person.class.getCanonicalName() + "\n" +
           "import " + Address.class.getCanonicalName() + "\n" +
           "global java.util.List list \n" +
           "rule \"show\" \n" + 
           "when  \n" + 
           "    $m : Person( addressesNoGenerics[0].street == new Address('s1').street) \n" + 
           "then \n" + 
           "    list.add('r1'); \n" + 
           "end \n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        
        // This should fail as there are no generics for the List 
        assertTrue( kbuilder.hasErrors() );
        
    }         
    
    public static class DMap extends HashMap {
        
    }

    @Test
    @Ignore("Added 30-APR-2011 -Rikkola-")
    public void testNestedEnumFromJar() {
        String str = ""+
           "package org.drools.compiler.test \n" +
           "import org.kie.examples.eventing.EventRequest \n" +
           "global java.util.List list \n" +
           "rule 'zaa'\n  " +
           "when \n  " +
           "request: EventRequest( status == EventRequest.Status.ACTIVE )\n   " +
           "then \n " +
           "request.setStatus(EventRequest.Status.ACTIVE); \n  " +
           "end";


        JarInputStream jis = null;
        try {
            jis = new JarInputStream( this.getClass().getResourceAsStream( "/eventing-example.jar" ) );
        } catch (IOException e) {
            fail("Failed to load the jar");
        }
        MapBackedClassLoader loader = createClassLoader( jis );

        KnowledgeBuilderConfiguration knowledgeBuilderConfiguration = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(null, loader);
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(knowledgeBuilderConfiguration);
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KieBaseConfiguration knowledgeBaseConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration(null,loader);

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(knowledgeBaseConfiguration);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        Triangle t = new Triangle( Triangle.Type.ACUTE);
        ksession.insert( t );

        ksession.fireAllRules();

        assertEquals(Triangle.Type.ACUTE, list.get(0) );
    }

    public static MapBackedClassLoader createClassLoader(JarInputStream jis) {
        ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();

        final ClassLoader p = parentClassLoader;

        MapBackedClassLoader loader = AccessController.doPrivileged(new PrivilegedAction<MapBackedClassLoader>() {
            public MapBackedClassLoader run() {
                return new MapBackedClassLoader(p);
            }
        });

        try {
            JarEntry entry = null;
            byte[] buf = new byte[1024];
            int len = 0;
            while ((entry = jis.getNextJarEntry()) != null) {
                if (!entry.isDirectory() && !entry.getName().endsWith(".java")) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    while ((len = jis.read(buf)) >= 0) {
                        out.write(buf, 0, len);
                    }

                    loader.addResource(entry.getName(), out.toByteArray());
                }
            }

        } catch (IOException e) {
            fail("failed to read the jar");
        }
        return loader;
    }

    public static class Triangle {
        public static final int ZERO = 0;
        
        private List<Map<String, Object>> deliveries;
        
        public static enum Type {
            ACUTE, OBTUSE;
        }
        
        private Type t;
        
        public Triangle(Type t) {
            this.t = t;
        }

        public Type getT() {
            return t;
        }

        public void setT(Type t) {
            this.t = t;
        }
        
        public List<Map<String, Object>> getDeliveries() {
            return deliveries;
        }
    
        public void setDeliveries(List<Map<String, Object>> deliveries) {
                this.deliveries = deliveries;
        }        
    }
    
    public Object compiledExecute(String ex) {
        Serializable compiled = MVEL.compileExpression( ex );
        return MVEL.executeExpression( compiled,
                                       new Object(),
                                       new HashMap() );
    }

    @Test
    public void test1() {
    	ParserContext pc = new ParserContext();
    	pc.addInput("x", String.class);
    	pc.setStrongTyping(true);
    	Object o = MVEL.compileExpression("x.startsWith('d')", pc);
    	Map vars = new HashMap();
    	vars.put("x", "d");
    	MVEL.executeExpression(o, vars);
    	System.out.println( o );
    }

    @Test
    public void testTokensInString(){
        //should query antldr DFA63 class but don't know how
        String [] operators = {"," ,"=" , "|=", "*"};
        //test various in consequence
        String strBegin = "" +
            "package org.kie \n" +
            "import org.drools.compiler.Cheese \n" +
            "dialect \"mvel\"\n" +
            "rule rule1 \n" +
            "when \n" +
            "$c:Cheese(type==\"swiss\") \n" +
            "then \n"+
            "modify($c){ type = \"swiss";

        String strEnd= "good\"};\n" +
                "end\n";
        StringBuffer failures = new StringBuffer();
        for(String oper:operators){
            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            String rule = strBegin+oper+strEnd;
            System.out.print(rule);
            kbuilder.add(ResourceFactory.newByteArrayResource(rule.getBytes()), ResourceType.DRL);
            if (kbuilder.hasErrors()) {
                failures.append(kbuilder.getErrors().toString());
            }
        }
        String failStr = failures.toString();
        if(failStr.length()>0){
            fail(failStr);
        }
    }
}
