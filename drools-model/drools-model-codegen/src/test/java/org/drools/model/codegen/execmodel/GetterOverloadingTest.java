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
package org.drools.model.codegen.execmodel;

import java.util.List;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.Message.Level;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class GetterOverloadingTest extends BaseModelTest {

    public GetterOverloadingTest(RUN_TYPE testRunType) {
        super(testRunType);
    }

    // NOTE: Drools/Mvel accepts 5 kinds of getters
    // e.g. for "resource" property,
    //
    // isResource()   (only for boolean)
    // isresource()   (only for boolean)
    // getResource()
    // getresource()
    // resource()
    //
    // If a class has multiple getters of those 5, one getter has to be chosen based on the above priority order

    @Test
    public void testDuplicateDifferentPropertyInClassHierarchy() {
        // ClassA.resource is boolean : isResource()
        // ClassB.resource is String  : getResource()
        // Mvel picks a method from getResource or isResoure depending on the order of Class.getMethods() -> unreliable. So let's make this ERROR
        final String str =
                "import " + ClassB.class.getCanonicalName() + ";" +
                           "rule r1\n" +
                           "when\n" +
                           "    ClassB( resource == \"ABC\" )\n" +
                           "then\n" +
                           "end\n";

        KieBuilder kieBuilder = createKieBuilder(str);
        List<org.kie.api.builder.Message> messages = kieBuilder.getResults().getMessages(Level.ERROR);
        assertThat(messages.get(0).getText()).contains("Incompatible Getter overloading detected");
    }

    public static class ClassA {

        private boolean resource;

        public boolean isResource() {
            return resource;
        }

        public void setResource(boolean resource) {
            this.resource = resource;
        }
    }

    public static class ClassB extends ClassA {

        private String resource;

        public String getResource() {
            return resource;
        }

        public void setResource(String resource) {
            this.resource = resource;
        }
    }

    @Test
    public void testBooleanAccessorOverload() {
        // ClassC implements both isResource() and getResource() for boolean (This is acceptable according to Javabeans spec)
        // isResource() has to be prioritized per Javabeans spec.
        // No Warning
        final String str =
                "import " + ClassC.class.getCanonicalName() + ";" +
                           "rule r1\n" +
                           "when\n" +
                           "    ClassC( resource == true )\n" +
                           "then\n" +
                           "end\n";

        KieBuilder kieBuilder = createKieBuilder(str);
        List<org.kie.api.builder.Message> messages = kieBuilder.getResults().getMessages();
        assertThat(messages).isEmpty();

        KieContainer kieContainer = KieServices.get().newKieContainer(kieBuilder.getKieModule().getReleaseId());
        KieSession ksession = kieContainer.newKieSession();

        ClassC c = new ClassC();
        c.setResource(true);

        ksession.insert(c);
        int fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(1);
    }

    public static class ClassC {

        private boolean resource;

        public boolean isResource() {
            return resource;
        }

        public boolean getResource() {
            return !resource; // this getter should not be called!
        }

        public void setResource(boolean resource) {
            this.resource = resource;
        }
    }

    @Test
    public void testAccessorInMultipleInterfaces() {
        // 2 super interfaces have the same abstract method
        // Valid overriding. No warning.
        final String str =
                "import " + InterfaceC.class.getCanonicalName() + ";" +
                           "rule r1\n" +
                           "when\n" +
                           "    InterfaceC( name == \"ABC\" )\n" +
                           "then\n" +
                           "end\n";

        KieBuilder kieBuilder = createKieBuilder(str);
        List<org.kie.api.builder.Message> messages = kieBuilder.getResults().getMessages();
        assertThat(messages).isEmpty();

        KieContainer kieContainer = KieServices.get().newKieContainer(kieBuilder.getKieModule().getReleaseId());
        KieSession ksession = kieContainer.newKieSession();

        ClassD d = new ClassD("ABC");

        ksession.insert(d);
        int fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(1);
    }

    public static interface InterfaceA {

        String getName();
    }

    public static interface InterfaceB {

        String getName();
    }

    public static interface InterfaceC extends InterfaceA, InterfaceB {

    }

    public static class ClassD implements InterfaceC {

        private String name;

        public ClassD(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    @Test
    public void testAccessorInSuperClassAndInterface() {
        // Valid overriding from super class and interface. No Warning
        final String str =
                "import " + ClassF.class.getCanonicalName() + ";" +
                           "rule r1\n" +
                           "when\n" +
                           "    ClassF( name == \"ABC\" )\n" +
                           "then\n" +
                           "end\n";

        KieBuilder kieBuilder = createKieBuilder(str);
        List<org.kie.api.builder.Message> messages = kieBuilder.getResults().getMessages();
        assertThat(messages).isEmpty();

        KieContainer kieContainer = KieServices.get().newKieContainer(kieBuilder.getKieModule().getReleaseId());
        KieSession ksession = kieContainer.newKieSession();

        ClassF f = new ClassF("ABC");

        ksession.insert(f);
        int fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(1);
    }

    public static interface InterfaceD {

        String getName();
    }

    public static class ClassE {

        public String getName() {
            return "ClassE";
        };
    }

    public static class ClassF extends ClassE implements InterfaceD {

        private String name;

        public ClassF(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    @Test
    public void testAcceptableStringAccessorOverload() {
        // ClassG implements getName(), getname() and name() for String
        // This is acceptable overloading and getName() has to be prioritized. No Warning.
        final String str =
                "import " + ClassG.class.getCanonicalName() + ";" +
                           "rule r1\n" +
                           "when\n" +
                           "    ClassG( name == \"ABC\" )\n" +
                           "then\n" +
                           "end\n";

        KieBuilder kieBuilder = createKieBuilder(str);
        List<org.kie.api.builder.Message> messages = kieBuilder.getResults().getMessages();
        assertThat(messages).isEmpty();

        KieContainer kieContainer = KieServices.get().newKieContainer(kieBuilder.getKieModule().getReleaseId());
        KieSession ksession = kieContainer.newKieSession();

        ClassG g = new ClassG("ABC");

        ksession.insert(g);
        int fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(1);
    }

    public static class ClassG {

        private String name;

        public ClassG(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public String getname() {
            return name + "X"; // this getter should not be called!
        }

        public String name() {
            return name + "X"; // this getter should not be called!
        }
    }

    @Test
    public void testCovariantOverload() {
        // ClassH : getValue() returns Number
        // ClassI : getValue() returns Integer
        // a more specialized getter (covariant overload) is preferred.
        final String str =
                "import " + ClassI.class.getCanonicalName() + ";" +
                           "rule r1\n" +
                           "when\n" +
                           "    ClassI( value == 150 )\n" +
                           "then\n" +
                           "end\n";

        KieBuilder kieBuilder = createKieBuilder(str);
        List<org.kie.api.builder.Message> messages = kieBuilder.getResults().getMessages();
        assertThat(messages).isEmpty();

        KieContainer kieContainer = KieServices.get().newKieContainer(kieBuilder.getKieModule().getReleaseId());
        KieSession ksession = kieContainer.newKieSession();

        ClassI i = new ClassI(50);

        ksession.insert(i);
        int fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(1);
    }

    public static class ClassH {

        protected Integer value;

        public ClassH(Integer value) {
            this.value = value;
        }

        public Number getValue() {
            return value;
        }
    }

    public static class ClassI extends ClassH {

        public ClassI(Integer value) {
            super(value);
        }

        @Override
        public Integer getValue() {
            return value + 100;
        }
    }

    @Test
    public void testContravariantOverload() {
        // ClassJ : getValue() returns Integer
        // ClassK : getvalue() returns Number
        // a more specialized getter (covariant overload) is preferred regardless of class hierarchy.
        final String str =
                "import " + ClassK.class.getCanonicalName() + ";" +
                           "rule r1\n" +
                           "when\n" +
                           "    ClassK( value == 50 )\n" +
                           "then\n" +
                           "end\n";

        KieBuilder kieBuilder = createKieBuilder(str);
        List<org.kie.api.builder.Message> messages = kieBuilder.getResults().getMessages();
        assertThat(messages).isEmpty();

        KieContainer kieContainer = KieServices.get().newKieContainer(kieBuilder.getKieModule().getReleaseId());
        KieSession ksession = kieContainer.newKieSession();

        ClassK k = new ClassK(50);

        ksession.insert(k);
        int fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(1);
    }

    public static class ClassJ {

        protected Integer value;

        public ClassJ(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }
    }

    public static class ClassK extends ClassJ {

        public ClassK(Integer value) {
            super(value);
        }

        public Number getvalue() { // cannot define Number getValue()
            return value + 100;
        }
    }

    @Test
    public void testPossibleBooleanAccessorOverload() {
        // ClassL implements 5 possible getters
        // This is acceptable overloading and isResource() has to be prioritized. No Warning.
        final String str =
                "import " + ClassC.class.getCanonicalName() + ";" +
                           "rule r1\n" +
                           "when\n" +
                           "    ClassC( resource == true )\n" +
                           "then\n" +
                           "end\n";

        KieBuilder kieBuilder = createKieBuilder(str);
        List<org.kie.api.builder.Message> messages = kieBuilder.getResults().getMessages();
        assertThat(messages).isEmpty();

        KieContainer kieContainer = KieServices.get().newKieContainer(kieBuilder.getKieModule().getReleaseId());
        KieSession ksession = kieContainer.newKieSession();

        ClassC c = new ClassC();
        c.setResource(true);

        ksession.insert(c);
        int fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(1);
    }

    public static class ClassL {

        private boolean resource;

        public boolean isResource() {
            return resource;
        }

        public boolean isresource() {
            return !resource;
        }

        public boolean getResource() {
            return !resource;
        }

        public boolean getresource() {
            return !resource;
        }

        public boolean resource() {
            return !resource;
        }

        public void setResource(boolean resource) {
            this.resource = resource;
        }
    }
}
