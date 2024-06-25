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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.drools.model.codegen.execmodel.domain.Address;
import org.drools.model.codegen.execmodel.domain.Person;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class GenericsTest extends BaseModelTest {

    public GenericsTest(RUN_TYPE testRunType) {
        super(testRunType);
    }

    @Test
    public void testGenericsAccumulateInlineCode() {
        // accumulate inline code supports generics
        String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                        "import " + Address.class.getCanonicalName() + ";\n" +
                        "import " + List.class.getCanonicalName() + ";\n" +
                        "import " + ArrayList.class.getCanonicalName() + ";\n" +
                        "global List results;\n" +
                        "dialect \"mvel\"\n" +
                        "rule R when\n" +
                        "  $l : List() from accumulate (Person($addrList : addresses),\n" +
                        "         init( List<String> cityList = new ArrayList(); ),\n" +
                        "         action( for(Address addr: $addrList){String city = addr.getCity(); cityList.add(city);} ),\n" +
                        "         result( cityList )\n" +
                        "       )\n" +
                        "then\n" +
                        "  results.add($l);\n" +
                        "end";

        KieSession ksession = getKieSession(str);
        List<List<String>> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        Person john = new Person("John");
        Address addr1 = new Address("1");
        Address addr2 = new Address("2");
        john.getAddresses().add(addr1);
        john.getAddresses().add(addr2);

        Person paul = new Person("Paul");
        Address addr3 = new Address("3");
        Address addr4 = new Address("4");
        john.getAddresses().add(addr3);
        john.getAddresses().add(addr4);

        ksession.insert(john);
        ksession.insert(paul);
        ksession.fireAllRules();
        assertThat(results.get(0).size()).isEqualTo(4);
    }

    public static class ClassWithGenericField<P extends Address> {

        private P extendedAddress;

        public ClassWithGenericField(final P extendedAddress) {
            this.extendedAddress = extendedAddress;
        }

        public P getExtendedAddress() {
            return extendedAddress;
        }

        public void setExtendedAddress(final P extendedAddress) {
            this.extendedAddress = extendedAddress;
        }
    }

    @Test
    public void testClassWithGenericField() {
        // KIE-1077
        String str =
                "import " + ClassWithGenericField.class.getCanonicalName() + ";\n " +
                "import " + List.class.getCanonicalName() + ";\n " +
                "global List results;\n " +
                "rule R when\n " +
                "    ClassWithGenericField($addressStreet: extendedAddress.street) \n " +
                "then\n " +
                "    results.add($addressStreet);\n " +
                "end";

        KieSession ksession = getKieSession(str);
        List<String> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        final Address address = new Address("someStreet", 1, "Levice");
        final ClassWithGenericField<Address> classWithGenericField = new ClassWithGenericField<>(address);

        ksession.insert(classWithGenericField);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testGenericsOnSuperclass() {
        // KIE-DROOLS-5925
        String str =
                "import " + DieselCar.class.getCanonicalName() + ";\n " +
                "dialect \"mvel\"\n" +
                "\n" +
                "rule \"Diesel vehicles with more than 95 kW use high-octane fuel (diesel has no octane, this is a test)\"\n" +
                "    when\n" +
                "        $v: DieselCar(motor.kw > 95, score<=0, !motor.highOctane)\n" +
                "    then\n" +
                "        System.out.println(\"Diesel vehicle with more than 95 kW: \" + $v+\", score=\"+$v.score);\n" +
                "        $v.engine.highOctane = true;\n" +
                "        update($v);\n" +
                "end\n" +
                "\n" +
                "rule \"High-octane fuel engines newer serial numbers have slightly higher score\"\n" +
                "    when\n" +
                "        $v: DieselCar(engine.highOctane, score<=1, motor.serialNumber > 50000)\n" +
                "    then\n" +
                "        System.out.println(\"High octane engine vehicle with newer serial number: \" + $v.motor.serialNumber);\n" +
                "        $v.score = $v.score + 1;\n" +
                "        update($v);\n" +
                "end";

        runTestWithGenerics(str);
    }

    @Test
    public void testGenericsOnSuperclassWithRedundantVariableDeclaration() {
        // KIE-DROOLS-5925
        String str =
                "import " + DieselCar.class.getCanonicalName() + ";\n " +
                "dialect \"mvel\"\n" +
                "\n" +
                "rule \"Diesel vehicles with more than 95 kW use high-octane fuel (diesel has no octane, this is a test)\"\n" +
                "    when\n" +
                "        $v: DieselCar($v.motor.kw > 95, score<=0, !$v.motor.highOctane)\n" +
                "    then\n" +
                "        System.out.println(\"Diesel vehicle with more than 95 kW: \" + $v+\", score=\"+$v.score);\n" +
                "        $v.engine.highOctane = true;\n" +
                "        update($v);\n" +
                "end\n" +
                "\n" +
                "rule \"High-octane fuel engines newer serial numbers have slightly higher score\"\n" +
                "    when\n" +
                "        $v: DieselCar($v.engine.highOctane, $v.score<=1, $v.motor.serialNumber > 50000)\n" +
                "    then\n" +
                "        System.out.println(\"High octane engine vehicle with newer serial number: \" + $v.motor.serialNumber);\n" +
                "        $v.score = $v.score + 1;\n" +
                "        update($v);\n" +
                "end";

        runTestWithGenerics(str);
    }

    private void runTestWithGenerics(String str) {
        KieSession ksession = getKieSession(str);

        DieselCar vehicle1 = new DieselCar("Volkswagen", "Passat", 100);
        vehicle1.setFrameMaxTorque(500);
        vehicle1.getEngine().setMaxTorque(350);
        vehicle1.getEngine().setSerialNumber(75_000);
        vehicle1.setScore(0);

        DieselCar vehicle2 = new DieselCar("Peugeot", "208", 50);
        vehicle2.setFrameMaxTorque(100);
        vehicle2.getEngine().setMaxTorque(200);
        vehicle2.setScore(0);

        ksession.insert(vehicle1);
        ksession.insert(vehicle2);
        assertThat(ksession.fireAllRules()).isEqualTo(3);
    }

    public static abstract class Vehicle<TEngine extends Engine> {

        private final String maker;
        private final String model;

        private int score;

        public Vehicle(String maker, String model) {
            this.maker = Objects.requireNonNull(maker);
            this.model = Objects.requireNonNull(model);
        }

        public String getMaker() {
            return maker;
        }

        public String getModel() {
            return model;
        }

        public abstract TEngine getEngine();

        public TEngine getMotor() {
            return getEngine();
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        @Override
        public String toString() {
            return "Vehicle{" + "maker='" + maker + '\'' + ", model='" + model + '\'' + '}';
        }
    }

    public static abstract class Engine {

        private final int kw;

        public Engine(int kw) {
            this.kw = kw;
        }

        public int getKw() {
            return kw;
        }

        public abstract boolean isZeroEmissions();

    }

    public static class DieselEngine extends Engine {

        // diesel has no octanes... but let's pretend it does
        private boolean highOctane;

        private int maxTorque;

        private long serialNumber;

        public DieselEngine(int kw) {
            super(kw);
        }

        @Override
        public boolean isZeroEmissions() {
            return false;
        }

        public boolean isHighOctane() {
            return highOctane;
        }

        public void setHighOctane(boolean highOctane) {
            this.highOctane = highOctane;
        }

        public int getMaxTorque() {
            return maxTorque;
        }

        public void setMaxTorque(int maxTorque) {
            this.maxTorque = maxTorque;
        }

        public void setSerialNumber(long serialNumber) {
            this.serialNumber = serialNumber;
        }

        public long getSerialNumber() {
            return serialNumber;
        }

    }

    public static class DieselCar extends Vehicle<DieselEngine> {
        private final DieselEngine engine;

        private long frameMaxTorque;



        public DieselCar(String maker, String model, int kw) {
            super(maker, model);
            this.engine = new DieselEngine(kw);
        }

        @Override
        public DieselEngine getEngine() {
            return engine;
        }

        public long getFrameMaxTorque() {
            return frameMaxTorque;
        }

        public void setFrameMaxTorque(long frameMaxTorque) {
            this.frameMaxTorque = frameMaxTorque;
        }
    }
}