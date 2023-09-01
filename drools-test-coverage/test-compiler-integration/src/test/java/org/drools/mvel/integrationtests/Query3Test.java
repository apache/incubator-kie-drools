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


import java.util.Collection;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class Query3Test {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public Query3Test(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    private KieBase knowledgeBase;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
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

    @Test
    public void testDifferent() {
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

    @Test
    public void testDifferentWithUpdate() {
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

    @Test
    public void testSame() {
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

    @Test
    public void testSameWithUpdate() {
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

    @Test
    public void testExtends() {
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

    @Test
    public void testExtendsWithUpdate() {
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

    @Test
    public void testExtendsWithRetract() {
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
