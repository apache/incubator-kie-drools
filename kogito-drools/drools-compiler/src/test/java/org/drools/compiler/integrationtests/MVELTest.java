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

package org.drools.compiler.integrationtests;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.drools.compiler.Address;
import org.drools.compiler.Cheese;
import org.drools.compiler.Cheesery;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.drools.compiler.TestEnum;
import org.drools.core.base.ClassFieldReader;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.extractors.MVELObjectClassFieldReader;
import org.drools.core.base.mvel.MVELDebugHandler;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.rule.constraint.MvelConstraint;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.FieldValue;
import org.drools.core.util.DateUtils;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;

public class MVELTest extends CommonTestMethodBase {

    @Test
    public void testHelloWorld() {
        // read in the source
        final KieBase kbase = loadKnowledgeBase("test_mvel.drl");
        final KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final List list2 = new ArrayList();
        ksession.setGlobal("list2", list2);

        final Cheese c = new Cheese("stilton", 10);
        ksession.insert(c);
        ksession.fireAllRules();
        assertEquals(2, list.size());
        assertEquals(BigInteger.valueOf(30), list.get(0));
        assertEquals(22, list.get(1));

        assertEquals("hello world", list2.get(0));

        final Date dt = DateUtils.parseDate("10-Jul-1974");
        assertEquals(dt, c.getUsedBy());
    }

    @Test
    public void testIncrementOperator() {
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

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL);
        assertFalse(kbuilder.hasErrors());

        final InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());

        final KieSession ksession = createKnowledgeSession(kbase);
        final List list = new ArrayList();
        ksession.setGlobal("list", list);
        ksession.insert(5);

        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertEquals(10, list.get(0));
    }

    @Test
    public void testEvalWithBigDecimal() {
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

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL);
        if (kbuilder.hasErrors()) {
            System.err.println(kbuilder.getErrors());
        }
        assertFalse(kbuilder.hasErrors());

        final InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());

        final KieSession ksession = createKnowledgeSession(kbase);
        final List list = new ArrayList();
        ksession.setGlobal("list", list);
        ksession.insert(new BigDecimal(1.5));

        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertEquals(new BigDecimal(1.5), list.get(0));
    }

    @Test
    public void testLocalVariableMVELConsequence() {
        final KieBase kbase = loadKnowledgeBase("test_LocalVariableMVELConsequence.drl");
        final KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal("results", list);

        ksession.insert(new Person("bob", "stilton"));
        ksession.insert(new Person("mark", "brie"));

        try {
            ksession.fireAllRules();
            assertEquals("should have fired twice", 2, list.size());
        } catch (final Exception e) {
            e.printStackTrace();
            fail("Should not raise any exception");
        }

    }

    @Test
    public void testMVELUsingGlobalsInDebugMode() {
        MVELDebugHandler.setDebugMode(true);
        try {
            final KieBase kbase = loadKnowledgeBase("test_MVELGlobalDebug.drl");
            KieSession ksession = kbase.newKieSession();
            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, false);
            ksession.dispose();
            MVELDebugHandler.setDebugMode(false);
        } catch (final Exception e) {
            MVELDebugHandler.setDebugMode(false);
            e.printStackTrace();
            fail("Should not raise exceptions");
        }

    }

    @Test
    public void testDuplicateLocalVariableMVELConsequence() {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newInputStreamResource(getClass().getResourceAsStream("test_DuplicateLocalVariableMVELConsequence.drl")), ResourceType.DRL);
        assertTrue( kbuilder.hasErrors() );
    }

    @Test
    public void testArrays() {
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

        final KieBase kieBase = loadKnowledgeBaseFromString(text.replaceAll("mvel", "java"), text);
        final StatelessKieSession statelessKieSession = kieBase.newStatelessKieSession();

        final List<String> list = new ArrayList<String>();
        statelessKieSession.execute(new TestObject(list));

        assertEquals(6, list.size());
        assertTrue(list.containsAll( Arrays.asList("TestObject.checkHighestPriority: java|2",
                                                   "TestObject.stayHasDaysOfWeek: java|false|[2008-04-01, 2008-04-10]",
                                                   "TestObject.checkHighestPriority: mvel|2",
                                                   "TestObject.stayHasDaysOfWeek: mvel|false|[2008-04-01, 2008-04-10]",
                                                   "TestObject.applyValueAddPromo: 1|2|3|4|mvel",
                                                   "TestObject.applyValueAddPromo: 1|2|3|4|java") ));
    }
    
    @Test
    public void testPackageImports() {
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
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL);
        if (kbuilder.hasErrors()) {
            throw new RuntimeException(kbuilder.getErrors().toString());
        }
        final InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());

        final KieSession ksession = createKnowledgeSession(kbase);
        final int result = ksession.fireAllRules();
        assertEquals(1, result);
        final Collection<? extends Object> insertedObjects = ksession.getObjects();
        assertEquals(3, insertedObjects.size());
    }
    
    @Test
    public void testSizeCheckInObject() {
        final String str = ""+
        "package org.drools.compiler.test \n" +
        "import " + Triangle.class.getCanonicalName() + "\n" +
        "global java.util.List list \n" +
        "rule \"show\" \n" + 
        "when  \n" + 
        "    $m : Triangle( deliveries.size == 0) \n" + 
        "then \n" + 
        "    list.add('r1'); \n" + 
        "end \n";

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL);

        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }
    }
    
    
    @Test
    public void testNestedEnum() {
        final String str = ""+
           "package org.drools.compiler.test \n" +
           "import " + Triangle.class.getCanonicalName() + "\n" +
           "global java.util.List list \n" +
           "rule \"show\" \n" + 
           "when  \n" + 
           "    $t: Triangle(t == Triangle.Type.ACUTE) \n" + 
           "then \n" + 
           "    list.add($t.getT()); \n" + 
           "end \n";

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL);

        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }

        final InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());

        final KieSession ksession = createKnowledgeSession(kbase);
        final List list = new ArrayList();
        ksession.setGlobal("list", list);
        final Triangle t = new Triangle(Triangle.Type.ACUTE);
        ksession.insert(t);
        ksession.fireAllRules();
        assertEquals(Triangle.Type.ACUTE, list.get(0));
    }
    
    @Test
    public void testNestedEnumWithMap() {
        final String str = ""+
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

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL);

        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }

        final InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());

        final KieSession ksession = createKnowledgeSession(kbase);
        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final DMap m = new DMap();
        m.put(Triangle.Type.ACUTE, "xxx");

        ksession.insert(m);
        ksession.fireAllRules();
        assertEquals("r1", list.get(0));
    } 
    
    @Test
    public void testNewConstructor() {
        final String str = ""+
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

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL);

        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }

        final InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());

        final KieSession ksession = createKnowledgeSession(kbase);
        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final Person p = new Person("yoda");
        p.setAddress(new Address("s1"));

        ksession.insert(p);
        ksession.fireAllRules();
        assertEquals("r1", list.get(0));

        // Check it was built with MVELReturnValueExpression constraint
        final List<ObjectTypeNode> nodes = ((InternalKnowledgeBase) kbase).getRete().getObjectTypeNodes();
        ObjectTypeNode node = null;
        for (final ObjectTypeNode n : nodes) {
            if (((ClassObjectType) n.getObjectType()).getClassType() == Person.class) {
                node = n;
                break;
            }
        }

        final AlphaNode alphanode = (AlphaNode) node.getObjectSinkPropagator().getSinks()[0];
        final AlphaNodeFieldConstraint constraint = alphanode.getConstraint();

        if (constraint instanceof MvelConstraint) {
            assertTrue(((MvelConstraint) constraint).getFieldExtractor() instanceof ClassFieldReader);
            final FieldValue r = ((MvelConstraint) constraint).getField();
            assertEquals(p.getAddress(), r.getValue());
        }
    }         
    
    @Test
    public void testArrayAccessorWithGenerics() {
        final String str = ""+
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

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL);

        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }

        final InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());

        final KieSession ksession = createKnowledgeSession(kbase);
        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final Person p = new Person("yoda");
        p.addAddress(new Address("s1"));

        ksession.insert(p);
        ksession.fireAllRules();
        assertEquals("r1", list.get(0));

        // Check it was built with MVELReturnValueExpression constraint
        final List<ObjectTypeNode> nodes = ((InternalKnowledgeBase) kbase).getRete().getObjectTypeNodes();
        ObjectTypeNode node = null;
        for (final ObjectTypeNode n : nodes) {
            if (((ClassObjectType) n.getObjectType()).getClassType() == Person.class) {
                node = n;
                break;
            }
        }

        AlphaNode alphanode = (AlphaNode) node.getObjectSinkPropagator().getSinks()[0];
        AlphaNodeFieldConstraint constraint = alphanode.getConstraint();

        if (constraint instanceof MvelConstraint) {
            assertTrue(((MvelConstraint) constraint).getFieldExtractor() instanceof MVELObjectClassFieldReader);
            assertEquals(new Address("s1"), ((MvelConstraint) constraint).getField().getValue());
        }

        alphanode = (AlphaNode) alphanode.getObjectSinkPropagator().getSinks()[0];
        constraint = alphanode.getConstraint();

        if (constraint instanceof MvelConstraint) {
            assertTrue(((MvelConstraint) constraint).getFieldExtractor() instanceof MVELObjectClassFieldReader);
            assertEquals(new Address("s1").getStreet(), ((MvelConstraint) constraint).getField().getValue());
        }
    }    
    
    @Test
    public void testArrayAccessorWithStaticFieldAccess() {
        final String str = ""+
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

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL);

        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }

        final InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());

        final KieSession ksession = createKnowledgeSession(kbase);
        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final Person p = new Person("yoda");
        p.addAddress(new Address("s1"));

        ksession.insert(p);
        ksession.fireAllRules();
        assertEquals("r1", list.get(0));

        // Check it was built with MVELReturnValueExpression constraint
        final List<ObjectTypeNode> nodes = ((InternalKnowledgeBase) kbase).getRete().getObjectTypeNodes();
        ObjectTypeNode node = null;
        for (final ObjectTypeNode n : nodes) {
            if (((ClassObjectType) n.getObjectType()).getClassType() == Person.class) {
                node = n;
                break;
            }
        }

        AlphaNode alphanode = (AlphaNode) node.getObjectSinkPropagator().getSinks()[0];
        AlphaNodeFieldConstraint constraint = alphanode.getConstraint();

        if (constraint instanceof MvelConstraint) {
            assertTrue(((MvelConstraint) alphanode.getConstraint()).getFieldExtractor() instanceof MVELObjectClassFieldReader);
            assertEquals(new Address("s1"), ((MvelConstraint) alphanode.getConstraint()).getField().getValue());
        }

        alphanode = (AlphaNode) alphanode.getObjectSinkPropagator().getSinks()[0];
        constraint = alphanode.getConstraint();
        if (constraint instanceof MvelConstraint) {
            assertTrue(((MvelConstraint) alphanode.getConstraint()).getFieldExtractor() instanceof MVELObjectClassFieldReader);
            assertEquals(new Address("s1").getStreet(), ((MvelConstraint) alphanode.getConstraint()).getField().getValue());
        }
    }       
    
    @Test
    public void testMapAccessorWithStaticFieldAccess() {
        final String str = ""+
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

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL);

        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }

        final InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());

        final KieSession ksession = createKnowledgeSession(kbase);
        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final Person p = new Person("yoda");
        p.getNamedAddresses().put(TestEnum.ONE, new Address("s1"));

        ksession.insert(p);

        ksession.fireAllRules();

        assertEquals("r1", list.get(0));

        // Check it was built with MVELReturnValueExpression constraint
        final List<ObjectTypeNode> nodes = ((InternalKnowledgeBase) kbase).getRete().getObjectTypeNodes();
        ObjectTypeNode node = null;
        for (final ObjectTypeNode n : nodes) {
            if (((ClassObjectType) n.getObjectType()).getClassType() == Person.class) {
                node = n;
                break;
            }
        }

        AlphaNode alphanode = (AlphaNode) node.getObjectSinkPropagator().getSinks()[0];
        AlphaNodeFieldConstraint constraint = alphanode.getConstraint();

        if (constraint instanceof MvelConstraint) {
            assertTrue(((MvelConstraint) alphanode.getConstraint()).getFieldExtractor() instanceof MVELObjectClassFieldReader);
            assertEquals(new Address("s1"), ((MvelConstraint) alphanode.getConstraint()).getField().getValue());
        }

        alphanode = (AlphaNode) alphanode.getObjectSinkPropagator().getSinks()[0];
        constraint = alphanode.getConstraint();

        if (constraint instanceof MvelConstraint) {
            assertTrue(((MvelConstraint) alphanode.getConstraint()).getFieldExtractor() instanceof MVELObjectClassFieldReader);
            assertEquals(new Address("s1").getStreet(), ((MvelConstraint) alphanode.getConstraint()).getField().getValue());
        }
    }     
    
    @Test
    public void testArrayAccessorWithoutGenerics() {
        final String str = ""+
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

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL);

        // This should fail as there are no generics for the List 
        assertTrue(kbuilder.hasErrors());
        
    }         
    
    public static class DMap extends HashMap {
        
    }

    public static class Triangle {

        public static final int ZERO = 0;

        private List<Map<String, Object>> deliveries;

        public static enum Type {
            ACUTE, OBTUSE;
        }

        private Type t;

        public Triangle(final Type t) {
            this.t = t;
        }

        public Type getT() {
            return t;
        }

        public void setT(final Type t) {
            this.t = t;
        }

        public List<Map<String, Object>> getDeliveries() {
            return deliveries;
        }

        public void setDeliveries(final List<Map<String, Object>> deliveries) {
            this.deliveries = deliveries;
        }
    }
    
    public Object compiledExecute(final String ex) {
        final Serializable compiled = MVEL.compileExpression( ex );
        return MVEL.executeExpression( compiled,
                                       new Object(),
                                       new HashMap() );
    }

    @Test
    public void test1() {
        final ParserContext pc = new ParserContext();
        pc.addInput("x", String.class);
        pc.setStrongTyping(true);
        final Object o = MVEL.compileExpression("x.startsWith('d')", pc);
        final Map vars = new HashMap();
        vars.put("x", "d");
        MVEL.executeExpression(o, vars);
        System.out.println(o);
    }

    @Test
    public void testTokensInString(){
        //should query antldr DFA63 class but don't know how
        final String [] operators = {"," ,"=" , "|=", "*"};
        //test various in consequence
        final String strBegin = "" +
            "package org.kie \n" +
            "import org.drools.compiler.Cheese \n" +
            "dialect \"mvel\"\n" +
            "rule rule1 \n" +
            "when \n" +
            "$c:Cheese(type==\"swiss\") \n" +
            "then \n"+
            "modify($c){ type = \"swiss";

        final String strEnd = "good\"};\n" + "end\n";
        final StringBuilder failures = new StringBuilder();
        for (final String oper : operators) {
            final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            final String rule = strBegin + oper + strEnd;
            System.out.print(rule);
            kbuilder.add(ResourceFactory.newByteArrayResource(rule.getBytes()), ResourceType.DRL);
            if (kbuilder.hasErrors()) {
                failures.append(kbuilder.getErrors().toString());
            }
        }
        final String failStr = failures.toString();
        if (failStr.length() > 0) {
            fail(failStr);
        }
    }

    @Test
    public void testGeneratedBeansMVEL() throws IllegalAccessException, InstantiationException {
        final KieBase kbase = loadKnowledgeBase("test_GeneratedBeansMVEL.drl");

        // Retrieve the generated fact type
        final FactType pf = kbase.getFactType("mortgages", "Applicant");
        final FactType af = kbase.getFactType("mortgages", "LoanApplication");

        final Object person = pf.newInstance();
        pf.set(person, "creditRating", "OK");

        final Object application = af.newInstance();
        final KieSession ksession = createKnowledgeSession(kbase);
        ksession.insert(person);
        ksession.insert(application);

        ksession.fireAllRules();
    }

    @Test
    public void testMVELClassReferences() throws InstantiationException, IllegalAccessException {
        final String str = "package org.drools.compiler\n" +
                "declare Assignment\n" +
                "    source : Class\n" +
                "    target : Class\n" +
                "end\n" +
                "rule ObjectIsAssignable1\n" +
                "when\n" +
                "    Assignment( $t: target == java.lang.Object.class || target == source )\n" +
                "then\n" +
                "end\n" +
                "rule ObjectIsAssignable2\n" +
                "when\n" +
                "    Assignment( $t: target == source || target == java.lang.Object.class )\n" +
                "then\n" +
                "end";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = createKnowledgeSession(kbase);

        final FactType asgType = kbase.getFactType("org.drools.compiler", "Assignment");
        final Object asg = asgType.newInstance();
        asgType.set(asg, "source", Object.class);
        asgType.set(asg, "target", Object.class);

        ksession.insert(asg);

        final int rules = ksession.fireAllRules();
        ksession.dispose();

        assertEquals(2, rules);
    }

    @Test
    public void testMVELConstraintsWithFloatingPointNumbersInScientificNotation() {
        final String rule = "package test; \n" +
                "dialect \"mvel\"\n" +
                "global java.util.List list;" +
                "\n" +
                "declare Bean \n" +
                " field : double \n" +
                "end \n" +
                "\n" +
                "rule \"Init\" \n" +
                "when \n" +
                "then \n" +
                "\t insert( new Bean( 1.0E-2 ) ); \n" +
                "end \n" +
                "\n" +
                "rule \"Check\" \n" +
                "when \n" +
                "\t Bean( field < 1.0E-1 ) \n" +
                "then \n" +
                "\t list.add( \"OK\" ); \n" +
                "end";

        final KieBase kbase = loadKnowledgeBaseFromString(rule);
        final KieSession kSession = kbase.newKieSession();

        final List<String> list = new ArrayList<String>();
        kSession.setGlobal("list", list);

        kSession.fireAllRules();

        assertEquals(1, list.size());
    }

    @Test
    public void testMvelDoubleInvocation() {
        final String rule = "package org.drools.compiler\n" +
                "import " + TestUtility.class.getCanonicalName() + ";\n" +
                "import " + TestFact.class.getCanonicalName() + ";\n" +
                "rule \"First Rule\"\n" +
                "    when\n" +
                "    $tf : TestFact(TestUtility.utilMethod(s, \"Value1\") == true\n" +
                "             && i > 0\n" +
                "    )\n" +
                "    then\n" +
                "        System.out.println(\"First Rule Fires\");\n" +
                "end\n" +
                "\n" +
                "rule \"Second Rule\"\n" +
                "    when\n" +
                "    $tf : TestFact(TestUtility.utilMethod(s, \"Value2\") == true\n" +
                "             && i > 0\n" +
                "    )\n" +
                "    then\n" +
                "        System.out.println(\"Second Rule Fires\");\n" +
                "end\n" +
                "\n" +
                "rule \"Third Rule\"\n" +
                "    when\n" +
                "    $tf : TestFact(TestUtility.utilMethod(s, \"Value3\") == true\n" +
                "             && i > 0\n" +
                "    )\n" +
                "    then\n" +
                "        System.out.println(\"Third Rule Fires\");\n" +
                "end ";

        final KieBase kbase = loadKnowledgeBaseFromString(rule);
        final KieSession ksession = createKnowledgeSession(kbase);

        final TestFact fact = new TestFact();
        fact.setS("asdf");
        fact.setI(10);
        ksession.insert(fact);
        ksession.fireAllRules();

        ksession.dispose();
    }

    public static class TestUtility {

        public static Boolean utilMethod(final String s1, final String s2) {
            Boolean result = null;

            if (s1 != null) {
                result = s1.equals(s2);
            }

            return result;
        }
    }

    public static class TestFact {

        private int i;
        private String s;

        public int getI() {
            return i;
        }

        public void setI(final int i) {
            this.i = i;
        }

        public String getS() {
            return s;
        }

        public void setS(final String s) {
            this.s = s;
        }
    }

    @Test
    public void testMVELSoundex() throws Exception {
        // read in the source
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("MVEL_soundex.drl"));
        KieSession ksession = createKnowledgeSession(kbase);

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);

        final Cheese c = new Cheese("fubar", 2);

        ksession.insert(c);
        ksession.fireAllRules();
        assertEquals(42, c.getPrice());
    }

    @Test
    public void testMVELSoundexNoCharParam() throws Exception {
        // read in the source
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("MVEL_soundexNPE2500.drl"));
        KieSession ksession = createKnowledgeSession(kbase);

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);

        final Cheese foobarCheese = new Cheese("foobar", 2);
        final Cheese nullCheese = new Cheese(null, 2);
        final Cheese starCheese = new Cheese("*", 2);

        ksession.insert(foobarCheese);
        ksession.insert(nullCheese);
        ksession.insert(starCheese);
        ksession.fireAllRules();
        assertEquals(42, foobarCheese.getPrice());
        assertEquals(2, nullCheese.getPrice());
        assertEquals(2, starCheese.getPrice());
    }

    @Test
    public void testMVELRewrite() throws Exception {
        // read in the source
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_MVELrewrite.drl"));
        KieSession ksession = createKnowledgeSession(kbase);

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        final List results = new ArrayList();
        ksession.setGlobal("results", results);

        final Cheese brie = new Cheese("brie", 2);
        final Cheese stilton = new Cheese("stilton", 2);
        final Cheesery cheesery = new Cheesery();
        cheesery.addCheese(brie);
        cheesery.addCheese(stilton);

        ksession.insert(cheesery);
        ksession.fireAllRules();

        assertEquals(1, results.size());
        assertEquals(cheesery, results.get(0));
    }

    @Test
    public void testMVELTypeCoercion() {
        final String str = "package org.drools.compiler.test; \n" +
                "\n" +
                "global java.util.List list;" +
                "\n" +
                "declare Bean\n" +
                // NOTICE: THIS WORKS WHEN THE FIELD IS "LIST", BUT USED TO WORK WITH ARRAYLIST TOO
                "  field : java.util.ArrayList\n" +
                "end\n" +
                "\n" +
                "\n" +
                "rule \"Init\"\n" +
                "when  \n" +
                "then\n" +
                "  insert( new Bean( new java.util.ArrayList( java.util.Arrays.asList( \"x\" ) ) ) );\n" +
                "end\n" +
                "\n" +
                "rule \"Check\"\n" +
                "when\n" +
                "  $b : Bean( $fld : field == [\"x\"] )\n" +
                "then\n" +
                "  System.out.println( $fld );\n" +
                "  list.add( \"OK\" ); \n" +
                "end";

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL);
        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }
        final KieBaseConfiguration kbConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbConf.setOption(EqualityBehaviorOption.EQUALITY);
        final InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kbConf);
        kbase.addPackages(kbuilder.getKnowledgePackages());
        final KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();
        assertTrue(list.contains("OK"));

        ksession.dispose();
    }

    @Test
    public void testNoMvelSyntaxInFunctions() {
        // JBRULES-3433
        final String str = "import java.util.*;\n" +
                "dialect \"mvel\"\n" +
                "function Integer englishToInt(String englishNumber) { \n" +
                "   Map m = [\"one\":1, \"two\":2, \"three\":3, \"four\":4, \"five\":5]; \n" +
                "   Object obj = m.get(englishNumber.toLowerCase()); \n" +
                "   return Integer.parseInt(obj.toString()); \n" +
                "}\n";

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL);

        assertTrue(kbuilder.hasErrors());
    }
}
