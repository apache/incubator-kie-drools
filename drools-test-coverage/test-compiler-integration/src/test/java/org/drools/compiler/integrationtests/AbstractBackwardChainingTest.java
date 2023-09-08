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
package org.drools.compiler.integrationtests;

import java.util.ArrayList;
import java.util.List;

import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractBackwardChainingTest {

    protected final KieBaseTestConfiguration kieBaseTestConfiguration;

    public AbstractBackwardChainingTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Test(timeout = 10000)
    public void testQueryPositional() {
        String drl = getQueryHeader();

        drl += "rule x1\n" +
                "when\n" +
                "    String( this == \"go1\" )\n" +
                //         output        ,output          ,output
                "    ?peeps($name1 : $name, $likes1 : $likes, $age1 : $age )\n" +
                "then\n" +
                "   list.add( $name1 + \" : \" + $age1 );\n" +
                "end \n";

        drl += "rule x2\n" +
                "when\n" +
                "    String( this == \"go2\" )\n" +
                //         output, input      ,output
                "    ?peeps($name1, \"stilton\", $age1; )\n" +
                "then\n" +
                "   list.add( $name1 + \" : \" + $age1 );\n" +
                "end \n";

        drl += "rule x3\n" +
                "when\n" +
                "    String( this == \"go3\" )\n" +
                "    $name1 : String() from \"darth\";\n " +
                //         input , input      ,output
                "    ?peeps($name1, \"stilton\", $age1; )\n" +
                "then\n" +
                "   list.add( $name1 + \" : \" + $age1 );\n" +
                "end \n";

        drl += "rule x4\n" +
                "when\n" +
                "    String( this == \"go4\" )\n" +
                "    $name1 : String() from \"darth\"\n " +
                "    $age1 : Integer() from 200;\n " +
                //         input , input      ,input
                "    ?peeps($name1, \"stilton\", $age1; )\n" +
                "then\n" +
                "   list.add( $name1 + \" : \" + $age1 );\n" +
                "end \n";

        testQuery(drl);
    }

    @Test(timeout = 10000)
    public void testQueryNamed() {
        String drl = getQueryHeader();

        drl += "rule x1\n" +
                "when\n" +
                "    String( this == \"go1\" )\n" +
                //         output        ,output          ,output
                "    ?peeps($name1 : $name, $likes1 : $likes, $age1 : $age )\n" +
                "then\n" +
                "   list.add( $name1 + \" : \" + $age1 );\n" +
                "end \n";

        drl += "rule x2\n" +
                "when\n" +
                "    String( this == \"go2\" )\n" +
                //         output        ,output                ,output
                "    ?peeps($name1 : $name, $likes : \"stilton\", $age1 : $age )\n" +
                "then\n" +
                "   list.add( $name1 + \" : \" + $age1 );\n" +
                "end \n";

        drl += "rule x3\n" +
                "when\n" +
                "    String( this == \"go3\" )\n" +
                "    $name1 : String() from \"darth\";\n " +
                //         input         ,input                ,output
                "    ?peeps($name1 : $name, $likes : \"stilton\", $age1 : $age )\n" +
                "then\n" +
                "   list.add( $name1 + \" : \" + $age1 );\n" +
                "end \n";

        drl += "rule x4\n" +
                "when\n" +
                "    String( this == \"go4\" )\n" +
                "    $name1 : String() from \"darth\";\n " +
                "    $age1 : Integer() from 200;\n " +
                //         input         ,input                ,input
                "    ?peeps($name1 : $name, $likes : \"stilton\", $age1 : $age )\n" +
                "then\n" +
                "   list.add( $name1 + \" : \" + $age1 );\n" +
                "end \n";

        testQuery(drl);
    }

    @Test(timeout = 10000)
    public void testQueryMixed() {
        String drl = getQueryHeader();

        drl += "rule x1\n" +
                "when\n" +
                "    String( this == \"go1\" )\n" +
                //         output        ,output          ,output
                "    ?peeps($name1; $likes1 : $likes, $age1 : $age )\n" +
                "then\n" +
                "   list.add( $name1 + \" : \" + $age1 );\n" +
                "end \n";

        drl += "rule x2\n" +
                "when\n" +
                "    String( this == \"go2\" )\n" +
                //         output        ,output                ,output
                "    ?peeps($name1, \"stilton\"; $age1 : $age )\n" +
                "then\n" +
                "   list.add( $name1 + \" : \" + $age1 );\n" +
                "end \n";

        drl += "rule x3\n" +
                "when\n" +
                "    String( this == \"go3\" )\n" +
                "    $name1 : String() from \"darth\";\n " +
                //         input         ,input                ,output
                "    ?peeps($name1, \"stilton\"; $age1 : $age )\n" +
                "then\n" +
                "   list.add( $name1 + \" : \" + $age1 );\n" +
                "end \n";

        drl += "rule x4\n" +
                "when\n" +
                "    String( this == \"go4\" )\n" +
                "    $name1 : String() from \"darth\"\n " +
                "    $age1 : Integer() from 200;\n " +
                //         input         ,input                ,input
                "    ?peeps($name1; $likes : \"stilton\", $age1 : $age )\n" +
                "then\n" +
                "   list.add( $name1 + \" : \" + $age1 );\n" +
                "end \n";

        testQuery(drl);
    }

    private void testQuery(final String drl) {
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("backward-chaining-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List<String> list = new ArrayList<>();
            ksession.setGlobal("list", list);

            final Person p1 = new Person("darth",
                                         "stilton",
                                         100);
            final Person p2 = new Person("darth",
                                         "stilton",
                                         200);
            final Person p3 = new Person("yoda",
                                         "stilton",
                                         300);
            final Person p4 = new Person("luke",
                                         "brie",
                                         300);
            final Person p5 = new Person("bobba",
                                         "cheddar",
                                         300);

            ksession.insert(p1);
            ksession.insert(p2);
            ksession.insert(p3);
            ksession.insert(p4);
            ksession.insert(p5);

            ksession.insert("go1");
            ksession.fireAllRules();
            if (kieBaseTestConfiguration.isIdentity()) {
                assertThat(list.size()).isEqualTo(5);
                assertThat(list.contains("darth : 100")).isTrue();
                assertThat(list.contains("darth : 200")).isTrue();
                assertThat(list.contains("yoda : 300")).isTrue();
                assertThat(list.contains("luke : 300")).isTrue();
                assertThat(list.contains("bobba : 300")).isTrue();
            } else {
                assertThat(list.size()).isEqualTo(4);
                assertThat(list.contains("darth : 100")).isTrue();
                assertThat(list.contains("yoda : 300")).isTrue();
                assertThat(list.contains("luke : 300")).isTrue();
                assertThat(list.contains("bobba : 300")).isTrue();
            }

            list.clear();
            ksession.insert("go2");
            ksession.fireAllRules();

            if (kieBaseTestConfiguration.isIdentity()) {
                assertThat(list.size()).isEqualTo(3);
                assertThat(list.contains("darth : 100")).isTrue();
                assertThat(list.contains("darth : 200")).isTrue();
                assertThat(list.contains("yoda : 300")).isTrue();
            } else {
                assertThat(list.size()).isEqualTo(2);
                assertThat(list.contains("darth : 100")).isTrue();
                assertThat(list.contains("yoda : 300")).isTrue();
            }

            list.clear();
            ksession.insert("go3");
            ksession.fireAllRules();

            if (kieBaseTestConfiguration.isIdentity()) {
                assertThat(list.size()).isEqualTo(2);
                assertThat(list.contains("darth : 100")).isTrue();
                assertThat(list.contains("darth : 200")).isTrue();
            } else {
                assertThat(list.size()).isEqualTo(1);
                assertThat(list.contains("darth : 100")).isTrue();
            }

            list.clear();
            ksession.insert("go4");
            ksession.fireAllRules();
            if (kieBaseTestConfiguration.isIdentity()) {
                assertThat(list.size()).isEqualTo(1);
                assertThat(list.contains("darth : 200")).isTrue();
            } else {
                assertThat(list.size()).isEqualTo(0);
            }
        } finally {
            ksession.dispose();
        }
    }

    private String getQueryHeader() {
        return "package org.drools.compiler.test  \n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "global java.util.List list\n" +
                "query peeps( String $name, String $likes, int $age ) \n" +
                "    Person( $name := name, $likes := likes, $age := age; ) \n" +
                "end\n";
    }
}
