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

import java.util.HashSet;
import java.util.Set;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Message;
import org.junit.Test;
import org.kie.internal.KnowledgeBase;
import org.kie.api.definition.type.Modifies;
import org.kie.api.definition.type.PropertyReactive;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

public class VarargsTest extends CommonTestMethodBase {
    
    @Test
    public void testStrStartsWith() throws Exception {
        KnowledgeBase kbase = loadKnowledgeBase("varargs.drl");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        Invoker inv = new Invoker();
        ksession.setGlobal( "invoker", inv );
        assertEquals( 1, ksession.fireAllRules() );

        assertTrue( inv.isI1()  );
        assertTrue( inv.isI2()  );
        assertTrue( inv.isI3()  );
    }

    @Test
    public void testVarargs() throws Exception {
        KnowledgeBase kbase = loadKnowledgeBase("varargs2.drl");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        MySet mySet = new MySet( "one", "two" );
        ksession.insert(mySet);
        ksession.fireAllRules();

        assertTrue(mySet.contains("one"));
        assertTrue( mySet.contains("two") );
        assertTrue( mySet.contains("three") );
        assertTrue( mySet.contains("four") );
        assertTrue( mySet.contains("z") );

        mySet = (MySet) ksession.getGlobal("set");
        assertTrue( mySet.contains("x") );
        assertTrue( mySet.contains("y") );
        assertTrue( mySet.contains("three") );
        assertTrue( mySet.contains("four") );
        assertTrue( mySet.contains("z") );     }

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
        public boolean equals(Object other) { return other != null && other instanceof B && value == ((B)other).value; };
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
            for( String s: strings ){
                set.add( s );
            }
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
