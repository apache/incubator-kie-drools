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

import org.drools.mvel.compiler.util.debug.DebugList;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieSession;
import org.kie.internal.conf.ParallelExecutionOption;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class ParallelEvaluationTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public ParallelEvaluationTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    private String getRule(int i, String rhs) {
        return getRule( i, rhs, "" );
    }

    private String getRule(int i, String rhs, String attributes) {
        return  "rule R" + i + " " + attributes + " when\n" +
                "    $i : Integer( intValue == " + i + " )" +
                "    String( toString == $i.toString )\n" +
                "then\n" +
                "    list.add($i);\n" +
                rhs +
                "end\n";
    }

    private String getNotRule(int i) {
        return  "rule Rnot" + i + " when\n" +
                "    String( toString == \"" + i + "\" )\n" +
                "    not Integer( intValue == " + i + " )" +
                "then\n" +
                "    list.add(" + -i + ");\n" +
                "end\n";
    }

    @Test
    public void testSalience() {
        int ruleNr = 20;
        StringBuilder sb = new StringBuilder( 400 );
        sb.append( "global java.util.List list;\n" );
        for (int i = 0; i < ruleNr; i++) {
            sb.append( getRule( i, "", "salience " + i ) );
        }

        final KieModule kieModule = KieUtil.getKieModuleFromDrls("test", kieBaseTestConfiguration, sb.toString());
        final KieBase kbase = KieBaseUtil.newKieBaseFromKieModuleWithAdditionalOptions(kieModule, kieBaseTestConfiguration, ParallelExecutionOption.PARALLEL_EVALUATION );

        KieSession ksession = kbase.newKieSession();

        List<Integer> list = new DebugList<>();
        ksession.setGlobal( "list", list );

        for (int i = 0; i < ruleNr; i++) {
            ksession.insert( i );
            ksession.insert( "" + i );
        }

        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(ruleNr);

        List<Integer> expected = Stream.iterate(ruleNr-1, i -> i-1).limit(ruleNr).collect(Collectors.toList());
        assertThat(list).isEqualTo(expected);
    }

    @Test
    public void testSalienceWithInserts() {
        int ruleNr = 20;
        StringBuilder sb = new StringBuilder( 400 );
        sb.append( "global java.util.List list;\n" );
        for (int i = 0; i < ruleNr; i++) {
            String rhs = i % 2 == 0 ? "insert(" + (i+1) + ");\n" : "";
            sb.append( getRule( i, rhs, "salience " + i ) );
        }

        final KieModule kieModule = KieUtil.getKieModuleFromDrls("test", kieBaseTestConfiguration, sb.toString());
        final KieBase kbase = KieBaseUtil.newKieBaseFromKieModuleWithAdditionalOptions(kieModule, kieBaseTestConfiguration, ParallelExecutionOption.PARALLEL_EVALUATION );

        KieSession ksession = kbase.newKieSession();

        List<Integer> list = new DebugList<>();
        ksession.setGlobal( "list", list );

        for (int i = 0; i < ruleNr; i++) {
            ksession.insert( "" + i );
            if (i % 2 == 0) {
                ksession.insert(i);
            }
        }

        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(ruleNr);

        List<Integer> expected = Stream.iterate(ruleNr-1, i -> i-1).limit(ruleNr).filter(i -> i % 2 == 0)
                .flatMap(i -> Arrays.asList(i, i+1).stream()).collect(Collectors.toList());
        assertThat(list).isEqualTo(expected);
    }
}
