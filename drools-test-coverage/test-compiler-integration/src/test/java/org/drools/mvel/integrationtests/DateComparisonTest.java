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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Stream;

import org.drools.mvel.compiler.Cheese;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This is a sample class to launch a rule.
 */
public class DateComparisonTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testDateComparisonThan(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        String str = "";
        str += "package org.drools.mvel.compiler;\n";
        str += "global java.util.List results;\n";
        str += "rule \"test date greater than\"\n";
        str += "     when\n";
        str += "         $c : Cheese(type == \"Yesterday\")\n";
        str += "         Cheese(type == \"Tomorrow\",  usedBy > ($c.usedBy))\n";
        str += "     then\n";
        str += "         results.add( \"test date greater than\" );\n";
        str += "end\n";

        str += "rule \"test date less than\"\n";
        str += "    when\n";
        str += "        $c : Cheese(type == \"Tomorrow\")\n";
        str += "        Cheese(type == \"Yesterday\", usedBy < ($c.usedBy));\n";
        str += "    then\n";
        str += "        results.add( \"test date less than\" );\n";
        str += "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        List<String> results = new ArrayList<String>();
        ksession.setGlobal( "results",
                            results );

        // go !
        Cheese yesterday = new Cheese( "Yesterday" );
        yesterday.setUsedBy( yesterday() );
        Cheese tomorrow = new Cheese( "Tomorrow" );
        tomorrow.setUsedBy( tomorrow() );
        ksession.insert( yesterday );
        ksession.insert( tomorrow );
        ksession.fireAllRules();
        assertThat(results.size()).isEqualTo(2);
        assertThat(results.contains("test date greater than")).isTrue();
        assertThat(results.contains("test date less than")).isTrue();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testDateComparisonAfter(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        String str = "";
        str += "package org.drools.mvel.compiler;\n";
        str += "global java.util.List results;\n";
        str += "rule \"test date greater than\"\n";
        str += "     when\n";
        str += "         $c : Cheese(type == \"Yesterday\")\n";
        str += "         Cheese(type == \"Tomorrow\", $c.usedBy before usedBy)\n";
        str += "     then\n";
        str += "         results.add( \"test date greater than\" );\n";
        str += "end\n";

        str += "rule \"test date less than\"\n";
        str += "    when\n";
        str += "        $c : Cheese(type == \"Tomorrow\")\n";
        str += "        Cheese(type == \"Yesterday\", $c.usedBy after usedBy);\n";
        str += "    then\n";
        str += "        results.add( \"test date less than\" );\n";
        str += "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        List<String> results = new ArrayList<String>();
        ksession.setGlobal( "results",
                            results );

        // go !
        Cheese yesterday = new Cheese( "Yesterday" );
        yesterday.setUsedBy( yesterday() );
        Cheese tomorrow = new Cheese( "Tomorrow" );
        tomorrow.setUsedBy( tomorrow() );
        ksession.insert( yesterday );
        ksession.insert( tomorrow );
        ksession.fireAllRules();
        assertThat(results.size()).isEqualTo(2);
        assertThat(results.contains("test date greater than")).isTrue();
        assertThat(results.contains("test date less than")).isTrue();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testDateComparisonAfterWithThisBinding(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        String str = "";
        str += "package org.drools.mvel.compiler;\n";
        str += "global java.util.List results;\n";
        str += "rule \"test date greater than\"\n";
        str += "     when\n";
        str += "         Cheese(type == \"Yesterday\", $c: this)\n";
        str += "         Cheese(type == \"Tomorrow\", $c.usedBy before usedBy)\n";
        str += "     then\n";
        str += "         results.add( \"test date greater than\" );\n";
        str += "end\n";

        str += "rule \"test date less than\"\n";
        str += "    when\n";
        str += "        Cheese(type == \"Tomorrow\", $c: this)\n";
        str += "        Cheese(type == \"Yesterday\", $c.usedBy after usedBy);\n";
        str += "    then\n";
        str += "        results.add( \"test date less than\" );\n";
        str += "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        List<String> results = new ArrayList<String>();
        ksession.setGlobal( "results",
                            results );

        // go !
        Cheese yesterday = new Cheese( "Yesterday" );
        yesterday.setUsedBy( yesterday() );
        Cheese tomorrow = new Cheese( "Tomorrow" );
        tomorrow.setUsedBy( tomorrow() );
        ksession.insert( yesterday );
        ksession.insert( tomorrow );
        ksession.fireAllRules();
        assertThat(results.size()).isEqualTo(2);
        assertThat(results.contains("test date greater than")).isTrue();
        assertThat(results.contains("test date less than")).isTrue();
    }

    private Date yesterday() {
        Calendar c = new GregorianCalendar();
        c.set( Calendar.DAY_OF_MONTH,
               c.get( Calendar.DAY_OF_MONTH ) - 1 );
        return c.getTime();
    }

    private Date tomorrow() {
        Calendar c = new GregorianCalendar();
        c.set( Calendar.DAY_OF_MONTH,
               c.get( Calendar.DAY_OF_MONTH ) + 1 );
        return c.getTime();
    }
}
