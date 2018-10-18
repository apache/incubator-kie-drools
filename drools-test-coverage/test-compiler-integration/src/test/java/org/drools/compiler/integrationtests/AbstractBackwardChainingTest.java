/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
                assertEquals(5, list.size());
                assertTrue(list.contains("darth : 100"));
                assertTrue(list.contains("darth : 200"));
                assertTrue(list.contains("yoda : 300"));
                assertTrue(list.contains("luke : 300"));
                assertTrue(list.contains("bobba : 300"));
            } else {
                assertEquals(4, list.size());
                assertTrue(list.contains("darth : 100"));
                assertTrue(list.contains("yoda : 300"));
                assertTrue(list.contains("luke : 300"));
                assertTrue(list.contains("bobba : 300"));
            }

            list.clear();
            ksession.insert("go2");
            ksession.fireAllRules();

            if (kieBaseTestConfiguration.isIdentity()) {
                assertEquals(3, list.size());
                assertTrue(list.contains("darth : 100"));
                assertTrue(list.contains("darth : 200"));
                assertTrue(list.contains("yoda : 300"));
            } else {
                assertEquals(2, list.size());
                assertTrue(list.contains("darth : 100"));
                assertTrue(list.contains("yoda : 300"));
            }

            list.clear();
            ksession.insert("go3");
            ksession.fireAllRules();

            if (kieBaseTestConfiguration.isIdentity()) {
                assertEquals(2, list.size());
                assertTrue(list.contains("darth : 100"));
                assertTrue(list.contains("darth : 200"));
            } else {
                assertEquals(1, list.size());
                assertTrue(list.contains("darth : 100"));
            }

            list.clear();
            ksession.insert("go4");
            ksession.fireAllRules();
            if (kieBaseTestConfiguration.isIdentity()) {
                assertEquals(1, list.size());
                assertTrue(list.contains("darth : 200"));
            } else {
                assertEquals(0, list.size());
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
