/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.integrationtests;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.mvel.accessors.ClassFieldReader;
import org.drools.base.base.ClassObjectType;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.base.rule.constraint.AlphaNodeFieldConstraint;
import org.drools.mvel.compiler.PersonHolder;
import org.drools.mvel.integrationtests.facts.FactWithList;
import org.drools.util.DateUtils;
import org.drools.mvel.MVELConstraint;
import org.drools.mvel.compiler.Address;
import org.drools.mvel.compiler.Cheese;
import org.drools.mvel.compiler.Cheesery;
import org.drools.mvel.compiler.FactA;
import org.drools.mvel.compiler.Person;
import org.drools.mvel.compiler.TestEnum;
import org.drools.mvel.expr.MVELDebugHandler;
import org.drools.mvel.extractors.MVELObjectClassFieldReader;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.Message;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(Parameterized.class)
public class MVELTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public MVELTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
     // TODO: EM failed with some tests. File JIRAs
        return TestParametersUtil.getKieBaseCloudConfigurations(false);
    }

    @Test
    public void testHelloWorld() {
        // read in the source
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_mvel.drl");
        final KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final List list2 = new ArrayList();
        ksession.setGlobal("list2", list2);

        final Cheese c = new Cheese("stilton", 10);
        ksession.insert(c);
        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isEqualTo(BigInteger.valueOf(30));
        assertThat(list.get(1)).isEqualTo(22);

        assertThat(list2.get(0)).isEqualTo("hello world");

        final Date dt = DateUtils.parseDate("10-Jul-1974");
        assertThat(c.getUsedBy()).isEqualTo(dt);
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

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();
        final List list = new ArrayList();
        ksession.setGlobal("list", list);
        ksession.insert(5);

        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo(10);
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

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();
        final List list = new ArrayList();
        ksession.setGlobal("list", list);
        ksession.insert(new BigDecimal(1.5));

        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo(new BigDecimal(1.5));
    }

    @Test
    public void testLocalVariableMVELConsequence() {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_LocalVariableMVELConsequence.drl");
        final KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal("results", list);

        ksession.insert(new Person("bob", "stilton"));
        ksession.insert(new Person("mark", "brie"));

        try {
            ksession.fireAllRules();
            assertThat(list.size()).as("should have fired twice").isEqualTo(2);
        } catch (final Exception e) {
            e.printStackTrace();
            fail("Should not raise any exception");
        }

    }

    @Test
    public void testMVELUsingGlobalsInDebugMode() {
        MVELDebugHandler.setDebugMode(true);
        try {
            KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_MVELGlobalDebug.drl");
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
        KieBuilder kieBuilder = KieUtil.getKieBuilderFromClasspathResources(kieBaseTestConfiguration, getClass(), false, "test_DuplicateLocalVariableMVELConsequence.drl");
        List<org.kie.api.builder.Message> errors = kieBuilder.getResults().getMessages(org.kie.api.builder.Message.Level.ERROR);
        assertThat(errors.isEmpty()).as("Should have an error").isFalse();
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

        KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, text.replaceAll("_mvel", "_java").replaceAll("\"mvel\"", "\"java\""), text);
        final StatelessKieSession statelessKieSession = kieBase.newStatelessKieSession();

        final List<String> list = new ArrayList<String>();
        statelessKieSession.execute(new TestObject(list));

        assertThat(list.size()).isEqualTo(6);
        assertThat(list.containsAll(Arrays.asList("TestObject.checkHighestPriority: java|2",
                "TestObject.stayHasDaysOfWeek: java|false|[2008-04-01, 2008-04-10]",
                "TestObject.checkHighestPriority: mvel|2",
                "TestObject.stayHasDaysOfWeek: mvel|false|[2008-04-01, 2008-04-10]",
                "TestObject.applyValueAddPromo: 1|2|3|4|mvel",
                "TestObject.applyValueAddPromo: 1|2|3|4|java"))).isTrue();
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

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();
        final int result = ksession.fireAllRules();
        assertThat(result).isEqualTo(1);
        final Collection<? extends Object> insertedObjects = ksession.getObjects();
        assertThat(insertedObjects.size()).isEqualTo(3);
    }
    
    @Test
    public void testSizeCheckInObject() {
        final String str = ""+
        "package org.drools.mvel.compiler.test \n" +
        "import " + Triangle.class.getCanonicalName() + "\n" +
        "global java.util.List list \n" +
        "rule \"show\" \n" + 
        "when  \n" + 
        "    $m : Triangle( deliveries.size == 0) \n" + 
        "then \n" + 
        "    list.add('r1'); \n" + 
        "end \n";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, str);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors.isEmpty()).as(errors.toString()).isTrue();
    }
    
    
    @Test
    public void testNestedEnum() {
        final String str = ""+
           "package org.drools.mvel.compiler.test \n" +
           "import " + Triangle.class.getCanonicalName() + "\n" +
           "global java.util.List list \n" +
           "rule \"show\" \n" + 
           "when  \n" + 
           "    $t: Triangle(t == Triangle.Type.ACUTE) \n" + 
           "then \n" + 
           "    list.add($t.getT()); \n" + 
           "end \n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();
        final List list = new ArrayList();
        ksession.setGlobal("list", list);
        final Triangle t = new Triangle(Triangle.Type.ACUTE);
        ksession.insert(t);
        ksession.fireAllRules();
        assertThat(list.get(0)).isEqualTo(Triangle.Type.ACUTE);
    }
    
    @Test
    public void testNestedEnumWithMap() {
        final String str = ""+
           "package org.drools.mvel.compiler.test \n" +
           "import " + DMap.class.getCanonicalName() + " \n" +
           "import " + Triangle.class.getCanonicalName() + "\n" +
           "global java.util.List list \n" +
           "rule \"show\" \n" + 
           "when  \n" + 
           "    $m : DMap( this[Triangle.Type.ACUTE] == 'xxx') \n" + 
           "then \n" + 
           "    list.add('r1'); \n" + 
           "end \n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();
        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final DMap m = new DMap();
        m.put(Triangle.Type.ACUTE, "xxx");

        ksession.insert(m);
        ksession.fireAllRules();
        assertThat(list.get(0)).isEqualTo("r1");
    } 
    
    @Test
    public void testNewConstructor() {
        final String str = ""+
           "package org.drools.mvel.compiler.test \n" +
           "import " + Person.class.getCanonicalName() + "\n" +
           "import " + Address.class.getCanonicalName() + "\n" +
           "global java.util.List list \n" +
           "rule \"show\" \n" + 
           "when  \n" + 
           "    $m : Person( address == new Address('s1')) \n" + 
           "then \n" + 
           "    list.add('r1'); \n" + 
           "end \n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();
        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final Person p = new Person("yoda");
        p.setAddress(new Address("s1"));

        ksession.insert(p);
        ksession.fireAllRules();
        assertThat(list.get(0)).isEqualTo("r1");

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

        if (constraint instanceof MVELConstraint) {
            assertThat(((MVELConstraint) constraint).getFieldExtractor() instanceof ClassFieldReader).isTrue();
        }
    }         
    
    @Test
    public void testArrayAccessorWithGenerics() {
        final String str = ""+
           "package org.drools.mvel.compiler.test \n" +
           "import " + Person.class.getCanonicalName() + "\n" +
           "import " + Address.class.getCanonicalName() + "\n" +
           "global java.util.List list \n" +
           "rule \"show\" \n" + 
           "when  \n" + 
           "    $m : Person( addresses[0] == new Address('s1'), addresses[0].street == new Address('s1').street ) \n" + 
           "then \n" + 
           "    list.add('r1'); \n" + 
           "end \n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();
        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final Person p = new Person("yoda");
        p.addAddress(new Address("s1"));

        ksession.insert(p);
        ksession.fireAllRules();
        assertThat(list.get(0)).isEqualTo("r1");

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

        if (constraint instanceof MVELConstraint) {
            assertThat(((MVELConstraint) constraint).getFieldExtractor() instanceof MVELObjectClassFieldReader).isTrue();
        }

        alphanode = (AlphaNode) alphanode.getObjectSinkPropagator().getSinks()[0];
        constraint = alphanode.getConstraint();

        if (constraint instanceof MVELConstraint) {
            assertThat(((MVELConstraint) constraint).getFieldExtractor() instanceof MVELObjectClassFieldReader).isTrue();
        }
    }    
    
    @Test
    public void testArrayAccessorWithStaticFieldAccess() {
        final String str = ""+
           "package org.drools.mvel.compiler.test \n" +
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

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();
        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final Person p = new Person("yoda");
        p.addAddress(new Address("s1"));

        ksession.insert(p);
        ksession.fireAllRules();
        assertThat(list.get(0)).isEqualTo("r1");

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

        if (constraint instanceof MVELConstraint) {
            assertThat(((MVELConstraint) alphanode.getConstraint()).getFieldExtractor() instanceof MVELObjectClassFieldReader).isTrue();
        }

        alphanode = (AlphaNode) alphanode.getObjectSinkPropagator().getSinks()[0];
        constraint = alphanode.getConstraint();
        if (constraint instanceof MVELConstraint) {
            assertThat(((MVELConstraint) alphanode.getConstraint()).getFieldExtractor() instanceof MVELObjectClassFieldReader).isTrue();
        }
    }       
    
    @Test
    public void testMapAccessorWithStaticFieldAccess() {
        final String str = ""+
           "package org.drools.mvel.compiler.test \n" +
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

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();
        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final Person p = new Person("yoda");
        p.getNamedAddresses().put(TestEnum.ONE, new Address("s1"));

        ksession.insert(p);

        ksession.fireAllRules();

        assertThat(list.get(0)).isEqualTo("r1");

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

        if (constraint instanceof MVELConstraint) {
            assertThat(((MVELConstraint) alphanode.getConstraint()).getFieldExtractor() instanceof MVELObjectClassFieldReader).isTrue();
        }

        alphanode = (AlphaNode) alphanode.getObjectSinkPropagator().getSinks()[0];
        constraint = alphanode.getConstraint();

        if (constraint instanceof MVELConstraint) {
            assertThat(((MVELConstraint) alphanode.getConstraint()).getFieldExtractor() instanceof MVELObjectClassFieldReader).isTrue();
        }
    }     
    
    @Test
    public void testArrayAccessorWithoutGenerics() {
        final String str = ""+
           "package org.drools.mvel.compiler.test \n" +
           "import " + Person.class.getCanonicalName() + "\n" +
           "import " + Address.class.getCanonicalName() + "\n" +
           "global java.util.List list \n" +
           "rule \"show\" \n" + 
           "when  \n" + 
           "    $m : Person( addressesNoGenerics[0].street == new Address('s1').street) \n" + 
           "then \n" + 
           "    list.add('r1'); \n" + 
           "end \n";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, str);
        List<org.kie.api.builder.Message> errors = kieBuilder.getResults().getMessages(org.kie.api.builder.Message.Level.ERROR);
        assertThat(errors.isEmpty()).as("Should have an error").isFalse(); // This should fail as there are no generics for the List 
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
            "import org.drools.mvel.compiler.Cheese \n" +
            "dialect \"mvel\"\n" +
            "rule rule1 \n" +
            "when \n" +
            "$c:Cheese(type==\"swiss\") \n" +
            "then \n"+
            "modify($c){ type = \"swiss";

        final String strEnd = "good\"};\n" + "end\n";
        for (final String oper : operators) {
            final String rule = strBegin + oper + strEnd;
            System.out.print(rule);
            KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, rule);
            List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
            assertThat(errors.isEmpty()).as(errors.toString()).isTrue();
        }
    }

    @Test
    public void testGeneratedBeansMVEL() throws IllegalAccessException, InstantiationException {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_GeneratedBeansMVEL.drl");

        // Retrieve the generated fact type
        final FactType pf = kbase.getFactType("mortgages", "Applicant");
        final FactType af = kbase.getFactType("mortgages", "LoanApplication");

        final Object person = pf.newInstance();
        pf.set(person, "creditRating", "OK");

        final Object application = af.newInstance();
        KieSession ksession = kbase.newKieSession();
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

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        final FactType asgType = kbase.getFactType("org.drools.compiler", "Assignment");
        final Object asg = asgType.newInstance();
        asgType.set(asg, "source", Object.class);
        asgType.set(asg, "target", Object.class);

        ksession.insert(asg);

        final int rules = ksession.fireAllRules();
        ksession.dispose();

        assertThat(rules).isEqualTo(2);
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

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, rule);
        KieSession kSession = kbase.newKieSession();

        final List<String> list = new ArrayList<String>();
        kSession.setGlobal("list", list);

        kSession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);
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

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, rule);
        KieSession ksession = kbase.newKieSession();

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
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "MVEL_soundex.drl");
        KieSession ksession = kbase.newKieSession();

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);

        final Cheese c = new Cheese("fubar", 2);

        ksession.insert(c);
        ksession.fireAllRules();
        assertThat(c.getPrice()).isEqualTo(42);
    }

    @Test
    public void testMVELSoundexNoCharParam() throws Exception {
        // read in the source
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "MVEL_soundexNPE2500.drl");
        KieSession ksession = kbase.newKieSession();

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);

        final Cheese foobarCheese = new Cheese("foobar", 2);
        final Cheese nullCheese = new Cheese(null, 2);
        final Cheese starCheese = new Cheese("*", 2);

        ksession.insert(foobarCheese);
        ksession.insert(nullCheese);
        ksession.insert(starCheese);
        ksession.fireAllRules();
        assertThat(foobarCheese.getPrice()).isEqualTo(42);
        assertThat(nullCheese.getPrice()).isEqualTo(2);
        assertThat(starCheese.getPrice()).isEqualTo(2);
    }

    @Test
    public void testMVELRewrite() throws Exception {
        // read in the source
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_MVELrewrite.drl");
        KieSession ksession = kbase.newKieSession();

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

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0)).isEqualTo(cheesery);
    }

    @Test
    public void testMVELTypeCoercion() {
        final String str = "package org.drools.mvel.compiler.test; \n" +
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

        KieBaseTestConfiguration equalityConfig = TestParametersUtil.getEqualityInstanceOf(kieBaseTestConfiguration);
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", equalityConfig, str);
        KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();
        assertThat(list.contains("OK")).isTrue();

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

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, str);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors.isEmpty()).as("Should have an error").isFalse();
    }

    @Test
    public void testModifyObjectWithMutableHashCodeInEqualityMode() {
        // DROOLS-2828
        String str = "package com.sample\n" +
                "import " + Human.class.getCanonicalName() + ";\n" +
                "rule \"Step A\"\n" +
                "dialect \"mvel\"\n" +
                "    no-loop true\n" +
                "when\n" +
                "    e : Human()\n" +
                "then\t\n" +
                "    modify( e ) {\n" +
                "        setAge( 10 );\n" +
                "    }\n" +
                "end";

        KieBaseTestConfiguration equalityConfig = TestParametersUtil.getEqualityInstanceOf(kieBaseTestConfiguration);
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", equalityConfig, str);
        KieSession ksession = kbase.newKieSession();
        Human h = new Human(2);
        ksession.insert(h);
        ksession.fireAllRules();
        assertThat(h.getAge()).isEqualTo(10);
    }

    @Test
    public void testModifyObjectWithMutableHashCodeInEqualityMode2() {
        // DROOLS-2828
        String str = "package com.sample\n" +
                "import " + Human.class.getCanonicalName() + ";\n" +
                "rule \"Step A\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "    e : Human()\n" +
                "    not String()\n" +
                "then\t\n" +
                "    insert(\"test\");\n" +
                "    modify( e ) {\n" +
                "        setAge( 10 );\n" +
                "    }\n" +
                "end";

        KieBaseTestConfiguration equalityConfig = TestParametersUtil.getEqualityInstanceOf(kieBaseTestConfiguration);
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", equalityConfig, str);
        KieSession ksession = kbase.newKieSession();
        Human h = new Human(2);
        ksession.insert(h);
        ksession.fireAllRules();
        assertThat(h.getAge()).isEqualTo(10);
    }

    public static class Human {

        private int age;

        public Human( int age ) {
            this.age = age;
        }

        public int getAge() {
            return this.age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public int hashCode() {
            return age;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            return age == ((Human) obj).age;
        }
    }

    @Test
    public void test2ndDashInMvelConsequnence() {
        // DROOLS-3678
        String str = "package com.sample\n" +
                "import " + Fact.class.getCanonicalName() + ";\n" +
                "dialect \"mvel\"\n" +
                "rule \"testRule\"\n" +
                "    when\n" +
                "        $fact : Fact();\n" +
                "    then\n" +
                "        $fact.name = \"A#\";\n" +
                "        $fact.value = \"B#\";\n" +
                "        System.out.println( $fact );\n" +
                "end";

        KieBaseTestConfiguration equalityConfig = TestParametersUtil.getEqualityInstanceOf(kieBaseTestConfiguration);
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", equalityConfig, str);
        KieSession ksession = kbase.newKieSession();

        Fact f = new Fact();
        ksession.insert(f);
        ksession.fireAllRules();
        assertThat(f.getName()).isEqualTo("A#");
        assertThat(f.getValue()).isEqualTo("B#");
    }

    public static class Fact {
        private String name;
        private String value;

        public String getName() {
            return name;
        }
        public void setName( String name ) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }
        public void setValue( String value ) {
            this.value = value;
        }
    }

    @Test
    public void testTypeCoercionLongDivByInt() {
        // DROOLS-5051
        String str = "package com.sample\n" +
                     "import " + Person.class.getCanonicalName() + ";\n" +
                     "rule R1\n" +
                     "no-loop true\n" +
                     "dialect \"mvel\"\n" +
                     "when\n" +
                     "  $p : Person()\n" +
                     "then\n" +
                     "  modify ($p) { setBigDecimal(15 * Math.round( new java.math.BigDecimal(\"49.4\") ) / 100 ) }\n" +
                     "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();
        Person p = new Person("Toshiya");
        ksession.insert(p);
        ksession.fireAllRules();
        assertThat(p.getBigDecimal().round(MathContext.DECIMAL32)).isEqualTo(new BigDecimal(7.35d, MathContext.DECIMAL32));
    }

    @Test
    public void testTypeCoercionIntCompareToDouble() {
        // DROOLS-2391
        String str = "package com.sample\n" +
                     "import " + IntFact.class.getCanonicalName() + ";\n" +
                     "rule R1\n" +
                     "dialect \"mvel\"\n" +
                     "when\n" +
                     "  $f : IntFact(a == 1, b == 2, a / b < 0.99)\n" +
                     "then\n" +
                     "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();
        IntFact f = new IntFact();
        f.setA(1);
        f.setB(2);
        ksession.insert(f);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    public static class IntFact {

        private int a;
        private int b;

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }

        public int getB() {
            return b;
        }

        public void setB(int b) {
            this.b = b;
        }
    }

    @Test
    public void testTypeCoercionFloatCompareToDouble() {
        String str = "package com.sample\n" +
                     "import " + FactA.class.getCanonicalName() + ";\n" +
                     "rule R1\n" +
                     "dialect \"mvel\"\n" +
                     "when\n" +
                     "  $f : FactA(field3 == 15.1)\n" +
                     "then\n" +
                     "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();
        FactA f = new FactA();
        f.setField3(new Float(15.1f));
        ksession.insert(f);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }
}
