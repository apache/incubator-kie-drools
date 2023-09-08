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
package org.drools.compiler.integrationtests.drl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.model.Cheesery;
import org.drools.testcoverage.common.model.FirstClass;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.model.SecondClass;
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
import org.kie.api.definition.KiePackage;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class ImportsTest {

    private static final Logger logger = LoggerFactory.getLogger(ImportsTest.class);

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public ImportsTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testImportFunctions() {
        testImportFunctionsBase(StaticMethods.class.getCanonicalName(), StaticMethods2.class.getCanonicalName());
    }

    @Test()
    public void testImport() {
        // Same package as this test
        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "import java.lang.Math;\n" +
                "rule \"Test Rule\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
            "    new Cheese(Cheese.STILTON);\n" +
            "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("imports-test", kieBaseTestConfiguration, drl);
        final KieSession session = kbase.newKieSession();
        try {
            session.fireAllRules();
        } finally {
            session.dispose();
        }
    }

    @Test
    public void testImportColision() {
        final String drl1 = "package org.drools.compiler.integrationtests.drl;\n" +
                "\n" +
                "//list any import classes here.\n" +
                "import " + FirstClass.class.getCanonicalName() + ";\n" +
                "import " + FirstClass.class.getCanonicalName() + ".AlternativeKey;\n" +
                "\n" +
                "//declare any global variables here\n" +
                "\n" +
                "rule \"First Class\"\n" +
                "\n" +
                "    when\n" +
                "        FirstClass()\n" +
                "        FirstClass.AlternativeKey()\n" +
                "    then\n" +
                "        System.out.println(\"First class!\");\n" +
                "\n" +
                "end";

        final String drl2 = "package org.drools.compiler.integrationtests.drl;\n" +
                "\n" +
                "//list any import classes here.\n" +
                "import " + SecondClass.class.getCanonicalName() + ";\n" +
                "import " + SecondClass.class.getCanonicalName() + ".AlternativeKey;\n" +
                "\n" +
                "//declare any global variables here\n" +
                "\n" +
                "rule \"Second Class\"\n" +
                "\n" +
                "    when\n" +
                "        SecondClass()\n" +
                "        SecondClass.AlternativeKey()\n" +
                "    then\n" +
                "        System.out.println(\"Second class!\");\n" +
                "\n" +
                "end";

        final InternalKnowledgeBase kbase = (InternalKnowledgeBase) KieBaseUtil.getKieBaseFromKieModuleFromDrl("imports-test", kieBaseTestConfiguration, drl1);
        final Collection<KiePackage> kpkgs = KieBaseUtil.getKieBaseFromKieModuleFromDrl("imports-test", kieBaseTestConfiguration, drl2).getKiePackages();
        kbase.addPackages(kpkgs);
        final KieSession ksession = kbase.newKieSession();
        try {
            ksession.insert(new FirstClass());
            ksession.insert(new SecondClass());
            ksession.insert(new FirstClass.AlternativeKey());
            ksession.insert(new SecondClass.AlternativeKey());

            ksession.fireAllRules();
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testImportConflict() {

        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule \"A rule\"\n" +
                "    when\n" +
                "        p:Person( )\n" +
                "    then\n" +
                "        // do something\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("imports-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        ksession.dispose();
    }

    @Test
    public void testMissingImport() {
        final String drl =
            "package org.drools.compiler.integrationtests.drl;\n" +
            "import " + Person.class.getName() + ";\n" +
            "global java.util.List list \n" +
            "rule rule1 \n" +
            "when \n" +
            "    $i : Cheese() \n" +
            "         MissingClass( fieldName == $i ) \n" +
            "then \n" +
            "    list.add( $i ); \n" +
            "end \n";

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        assertThat(kieBuilder.getResults().getMessages()).isNotEmpty();
        assertThat(kieBuilder.getResults().getMessages()).extracting(Message::getText).doesNotContain("");
    }

    @Test
    public void testMissingImports() {

        final String drl = "package foo;\n" +
                "\n" +
                "rule \"Generates NPE\"\n" +
                "  when\n" +
                "    $count : Thing( size > 0 ) from collect( Gizmo( length == 1 ) )\n" +
                "  then\n" +
                "    System.out.println(\"boo\");\n" +
                "end";

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        assertThat(kieBuilder.getResults().getMessages()).isNotEmpty();
        assertThat(kieBuilder.getResults().getMessages()).extracting(Message::getText).doesNotContain("");
    }

    @Test
    public void testPackageImportWithMvelDialect() {
        // JBRULES-2244
        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "import " + Cheese.class.getCanonicalName() + "\n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "dialect \"mvel\"\n" +
                "rule R1 no-loop when\n" +
                "   $p : Person( )" +
                "   $c : Cheese( )" +
                "then\n" +
                "   modify($p) { setCheese($c) };\n" +
                "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("imports-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final Person p = new Person( "Mario", 38 );
            ksession.insert( p );
            final Cheese c = new Cheese( "Gorgonzola" );
            ksession.insert( c );

            assertThat(ksession.fireAllRules()).isEqualTo(1);
            assertThat(p.getCheese()).isSameAs(c);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testImportStaticClass() {

        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "import " + Cheesery.class.getCanonicalName() + ";\n" +
                "import " + Cheesery.Maturity.class.getCanonicalName() + ";\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule \"status, int based enum\"\n" +
                "    salience 10\n" +
                "    when\n" +
                "           p : Cheesery(status == Cheesery.SELLING_CHEESE, maturity == Maturity.OLD)\n" +
                "    then\n" +
                "        list.add( p );\n" +
                "\n" +
                "end   \n" +
                "\n" +
                "rule \"maturity, object based enum\"\n" +
                "    when\n" +
                "           p : Cheesery(status == Cheesery.MAKING_CHEESE, maturity == Maturity.YOUNG)\n" +
                "    then\n" +
                "        list.add( p );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("imports-test", kieBaseTestConfiguration, drl);
        final KieSession session = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            session.setGlobal("list", list);

            final Cheesery cheesery1 = new Cheesery();
            cheesery1.setStatus(Cheesery.SELLING_CHEESE);
            cheesery1.setMaturity(Cheesery.Maturity.OLD);
            session.insert(cheesery1);

            final Cheesery cheesery2 = new Cheesery();
            cheesery2.setStatus(Cheesery.MAKING_CHEESE);
            cheesery2.setMaturity(Cheesery.Maturity.YOUNG);
            session.insert(cheesery2);

            session.fireAllRules();

            assertThat(list.size()).isEqualTo(2);

            assertThat(list.get(0)).isEqualTo(cheesery1);
            assertThat(list.get(1)).isEqualTo(cheesery2);
        } finally {
            session.dispose();
        }
    }

    public static class StaticMethods {

        public static String getString1(final String string) {
            return string;
        }

        public static String getString2(final String string) {
            return string;
        }

    }

    public static class StaticMethods2 {
        public static String getString3(final String string, final Integer integer) {
            return string + integer;
        }
    }

    @Test
    public void testImportInnerFunctions() {

        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "import function " + org.drools.compiler.integrationtests.drl.ImportsTest.StaticMethods.class.getCanonicalName() + ".*;\n" +
                "import function " + org.drools.compiler.integrationtests.drl.ImportsTest.StaticMethods2.class.getCanonicalName() + ".getString3;\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "\n" +
                "function String getString4( String string ) {\n" +
                "    return string;\n" +
                "}\n" +
                "\n" +
                "rule \"test rule1\"\n" +
                "    salience 30\n" +
                "    when\n" +
                "        Cheese()\n" +
                "    then\n" +
                "        list.add( getString1( \"rule1\" ) );\n" +
                "end    \n" +
                "\n" +
                "rule \"test rule2\"\n" +
                "    salience 20\n" +
                "    when\n" +
                "        Cheese( type == ( getString2(\"stilton\") ) );\n" +
                "    then\n" +
                "        list.add( getString3( \"rule\", new Integer( 2 ) ) );\n" +
                "end    \n" +
                "\n" +
                "rule \"test rule3\"\n" +
                "    salience 10\n" +
                "    when\n" +
                "        Cheese( $type : type);\n" +
                "        eval( $type.equals( getString1( \"stilton\" ) ) );\n" +
                "    then\n" +
                "        list.add( getString2( \"rule3\" ) );\n" +
                "end    \n" +
                "\n" +
                "rule \"test rule4\"\n" +
                "    salience 0\n" +
                "    when\n" +
                "        Cheese();\n" +
                "    then\n" +
                "        list.add( getString4( \"rule4\" ) );\n" +
                "end";

        testImportFunctionsBase(org.drools.compiler.integrationtests.drl.ImportsTest.StaticMethods.class.getCanonicalName(),
                                org.drools.compiler.integrationtests.drl.ImportsTest.StaticMethods2.class.getCanonicalName());
    }

    private void testImportFunctionsBase(final String staticMethodImport1, final String staticMethodImport2) {
        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "import function " + staticMethodImport1 + ".*;\n" +
                "import function " + staticMethodImport2 + ".getString3;\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "\n" +
                "function String getString4( String string ) {\n" +
                "    return string;\n" +
                "}\n" +
                "\n" +
                "rule \"test rule1\"\n" +
                "    salience 30\n" +
                "    when\n" +
                "        Cheese()\n" +
                "    then\n" +
                "        list.add( getString1( \"rule1\" ) );\n" +
                "end    \n" +
                "\n" +
                "rule \"test rule2\"\n" +
                "    salience 20\n" +
                "    when\n" +
                "        Cheese( type == ( getString2(\"stilton\") ) );\n" +
                "    then\n" +
                "        list.add( getString3( \"rule\", new Integer( 2 ) ) );\n" +
                "end    \n" +
                "\n" +
                "rule \"test rule3\"\n" +
                "    salience 10\n" +
                "    when\n" +
                "        Cheese( $type : type);\n" +
                "        eval( $type.equals( getString1( \"stilton\" ) ) );\n" +
                "    then\n" +
                "        list.add( getString2( \"rule3\" ) );\n" +
                "end    \n" +
                "\n" +
                "rule \"test rule4\"\n" +
                "    salience 0\n" +
                "    when\n" +
                "        Cheese();\n" +
                "    then\n" +
                "        list.add( getString4( \"rule4\" ) );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("imports-test", kieBaseTestConfiguration, drl);
        final KieSession session = kbase.newKieSession();
        try {
            final Cheese cheese = new Cheese("stilton",
                                             15);
            session.insert(cheese);
            List list = new ArrayList();
            session.setGlobal("list", list);
            final int fired = session.fireAllRules();

            list = (List) session.getGlobal("list");

            assertThat(fired).isEqualTo(4);
            assertThat(list.size()).isEqualTo(4);

            assertThat(list.get(0)).isEqualTo("rule1");
            assertThat(list.get(1)).isEqualTo("rule2");
            assertThat(list.get(2)).isEqualTo("rule3");
            assertThat(list.get(3)).isEqualTo("rule4");
        } finally {
            session.dispose();
        }
    }

    @Test
    public void testWrongImportWithDeclaredType() {
        // KOGITO-7729

        final String drl =
                "package org.acme.order\n" +
                "\n" +
                "import org.kie.order.Order\n" +
                "\n" +
                "declare ExpressDeliverer\n" +
                "  term: int\n" +
                "end\n" +
                "\n" +
                "rule R1 when\n" +
                "    $order: Order ()\n" +
                "    $deliverer: ExpressDeliverer ()\n" +
                "then\n" +
                "    $order.setDiscount(0);\n" +
                "    $deliverer.setTerm(5);\n" +
                "end";

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        assertThat(kieBuilder.getResults().getMessages()).isNotEmpty();
        assertThat(kieBuilder.getResults().getMessages()).extracting(Message::getText).doesNotContain("");
    }
}
