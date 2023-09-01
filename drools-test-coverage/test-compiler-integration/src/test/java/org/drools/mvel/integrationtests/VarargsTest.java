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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.definition.type.Modifies;
import org.kie.api.definition.type.PropertyReactive;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class VarargsTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public VarargsTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testStrStartsWith() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration, "varargs.drl");
        KieSession ksession = kbase.newKieSession();

        Invoker inv = new Invoker();
        ksession.setGlobal( "invoker", inv );
        assertThat(ksession.fireAllRules()).isEqualTo(1);

        assertThat(inv.isI1()).isTrue();
        assertThat(inv.isI2()).isTrue();
        assertThat(inv.isI3()).isTrue();
    }

    @Test
    public void testVarargs() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration, "varargs2.drl");
        KieSession ksession = kbase.newKieSession();

        MySet mySet = new MySet( "one", "two" );
        ksession.insert(mySet);
        int fired = ksession.fireAllRules(20);
        assertThat(fired).isEqualTo(5);

        assertThat(mySet.contains("one")).isTrue();
        assertThat(mySet.contains("two")).isTrue();
        assertThat(mySet.contains("three")).isTrue();
        assertThat(mySet.contains("four")).isTrue();
        assertThat(mySet.contains("z")).isTrue();

        mySet = (MySet) ksession.getGlobal("set");
        assertThat(mySet.contains("x")).isTrue();
        assertThat(mySet.contains("y")).isTrue();
        assertThat(mySet.contains("three")).isTrue();
        assertThat(mySet.contains("four")).isTrue();
        assertThat(mySet.contains("z")).isTrue();     }

    public static class Invoker {
        private boolean i1;
        private boolean i2;
        private boolean i3;
         public void invoke(String s1, int num, String... strings) {
            if (num != strings.length) {
                throw new RuntimeException("Expected num: " + num + ", got: " + strings.length);
            }
             i1 = true;
        }
        public void invoke(String s1, int num, A... as) {
            if (num != as.length) {
                throw new RuntimeException("Expected num: " + num + ", got: " + as.length);
            }
            i2 = true;
        }
        public void invoke(int total, A... as) {
            int sum = 0;
            for (A a : as) sum += a.getValue();
            if (total != sum) {
                throw new RuntimeException("Expected total: " + total);
            }
            i3 = true;
        }

        public boolean isI1() {
            return i1;
        }

        public void setI1(boolean i1) {
            this.i1 = i1;
        }

        public boolean isI2() {
            return i2;
        }

        public void setI2(boolean i2) {
            this.i2 = i2;
        }

        public boolean isI3() {
            return i3;
        }

        public void setI3(boolean i3) {
            this.i3 = i3;
        }
    }

    public interface A {
        int getValue();
    }

    public static class B implements A {
        private int value;
        public B() { }
        public B(int value) { this.value = value; }
        public B(String value) { this.value = Integer.parseInt(value); }
        public int getValue() { return value; }
        public boolean equals(Object other) { return other instanceof B && value == ((B) other).value; };
    }

    @PropertyReactive
    public static class MySet {
        private boolean processed;
        Set<String> set = new HashSet<String>();

        public MySet( String... strings ){
            add( strings );
        }

        public boolean isProcessed() {
            return processed;
        }

        public void setProcessed(boolean processed) {
            this.processed = processed;
        }

        @Modifies("processed")
        public void add( String... strings ){
            Collections.addAll(set, strings);
        }

        public boolean contains( String s ){
            return set.contains( s );
        }

        public Set<String> getSet() {
            return this.set;
        }

        public String toString(){
            return set.toString();
        }
    }
}
