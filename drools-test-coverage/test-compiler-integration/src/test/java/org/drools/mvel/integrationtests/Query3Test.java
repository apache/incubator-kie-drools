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


import java.util.stream.Stream;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;

import static org.assertj.core.api.Assertions.assertThat;

public class Query3Test {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    private KieBase knowledgeBase;

    public void setUp(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String text = "";
        text += "package org.drools.integrationtests\n";
        text += "import " + Query3Test.Bar.class.getCanonicalName() + "\n";
        text += "import " + Query3Test.Foo.class.getCanonicalName() + "\n";
        text += "import " + Query3Test.Foo2.class.getCanonicalName() + "\n";
        text += "query \"testDifferent\"\n";
        text += "    foo : Foo();\n";
        text += "    bar : Bar(id == foo.id)\n";
        text += "end\n";
        text += "query \"testSame\"\n";
        text += "    foo : Foo();\n";
        text += "    foo2 : Foo(id == foo.id);\n";
        text += "end\n";
        text += "query \"testExtends\"\n";
        text += "    foo : Foo();\n";
        text += "    foo2 : Foo2(id == foo.id);\n";
        text += "end\n";

        knowledgeBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, text);
    }

    private void doIt(Object o1,
                      Object o2,
                      String query,
                      int expected,
                      boolean doUpdate,
                      boolean doRetract) {
        KieSession knowledgeSession = knowledgeBase.newKieSession();
        try {
            knowledgeSession.insert( o1 );
            FactHandle handle2 = knowledgeSession.insert( o2 );
            if ( doUpdate ) {
                knowledgeSession.update( handle2,
                                         o2 );
            } else if ( doRetract ) {
                knowledgeSession.retract( handle2 );
                handle2 = knowledgeSession.insert( o2 );
            }
            QueryResults queryResults = knowledgeSession.getQueryResults( query );
            assertThat(queryResults.size()).isEqualTo(expected);
        } finally {
            knowledgeSession.dispose();
        }
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testDifferent(KieBaseTestConfiguration kieBaseTestConfiguration) {
    	setUp(kieBaseTestConfiguration);
        Foo foo = new Foo();
        foo.setId( "x" );
        Bar bar = new Bar();
        bar.setId( "x" );
        doIt( foo,
              bar,
              "testDifferent",
              1,
              false,
              false );
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testDifferentWithUpdate(KieBaseTestConfiguration kieBaseTestConfiguration) {
    	setUp(kieBaseTestConfiguration);
        Foo foo = new Foo();
        foo.setId( "x" );
        Bar bar = new Bar();
        bar.setId( "x" );
        doIt( foo,
              bar,
              "testDifferent",
              1,
              true,
              false );
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testSame(KieBaseTestConfiguration kieBaseTestConfiguration) {
    	setUp(kieBaseTestConfiguration);
        Foo foo = new Foo();
        foo.setId( "x" );
        Foo foo2 = new Foo();
        foo2.setId( "x" );
        doIt( foo,
              foo2,
              "testSame",
              4,
              false,
              false );
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testSameWithUpdate(KieBaseTestConfiguration kieBaseTestConfiguration) {
    	setUp(kieBaseTestConfiguration);
        Foo foo = new Foo();
        foo.setId( "x" );
        Foo foo2 = new Foo();
        foo2.setId( "x" );
        doIt( foo,
              foo2,
              "testSame",
              4,
              true,
              false );
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testExtends(KieBaseTestConfiguration kieBaseTestConfiguration) {
    	setUp(kieBaseTestConfiguration);
        Foo foo = new Foo();
        foo.setId( "x" );
        Foo2 foo2 = new Foo2();
        foo2.setId( "x" );
        doIt( foo,
              foo2,
              "testExtends",
              2,
              false,
              false );
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testExtendsWithUpdate(KieBaseTestConfiguration kieBaseTestConfiguration) {
    	setUp(kieBaseTestConfiguration);
        Foo foo = new Foo();
        foo.setId( "x" );
        Foo2 foo2 = new Foo2();
        foo2.setId( "x" );
        doIt( foo,
              foo2,
              "testExtends",
              2,
              true,
              false );
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testExtendsWithRetract(KieBaseTestConfiguration kieBaseTestConfiguration) {
    	setUp(kieBaseTestConfiguration);
        Foo foo = new Foo();
        foo.setId( "x" );
        Foo2 foo2 = new Foo2();
        foo2.setId( "x" );
        doIt( foo,
              foo2,
              "testExtends",
              2,
              false,
              true );
    }

    public static class Bar {

        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

    }

    public static class Foo {

        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

    }

    public static class Foo2 extends Foo {

    }
}
