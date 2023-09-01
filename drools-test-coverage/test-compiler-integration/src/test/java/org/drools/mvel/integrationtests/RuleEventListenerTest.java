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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.drools.mvel.compiler.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.Match;
import org.kie.internal.event.rule.RuleEventListener;
import org.kie.internal.event.rule.RuleEventManager;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class RuleEventListenerTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public RuleEventListenerTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testRuleEventListener() {
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "global java.util.List list\n" +
                "rule R when\n" +
                "  $p: Person( $age: age < 20 )\n" +
                "then\n" +
                "  modify($p) { setAge( $age + 1 ) };" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        List<String> list = new ArrayList<>();

        ( (RuleEventManager) ksession ).addEventListener( new RuleEventListener() {
            @Override
            public void onBeforeMatchFire( Match match ) {
                list.add("onBeforeMatchFire: " + match.getDeclarationValue( "$age" ));
            }

            @Override
            public void onAfterMatchFire( Match match ) {
                list.add("onAfterMatchFire: " + match.getDeclarationValue( "$age" ));
            }

            @Override
            public void onDeleteMatch( Match match ) {
                list.add("onDeleteMatch: " + match.getDeclarationValue( "$age" ));
            }

            @Override
            public void onUpdateMatch( Match match ) {
                list.add("onUpdateMatch: " + match.getDeclarationValue( "$age" ));
            }
        } );

        ksession.insert( new Person("John Smith", 18) );
        ksession.fireAllRules();

        List<String> expected = Arrays.asList( "onBeforeMatchFire: 18",
                                               "onAfterMatchFire: 19",
                                               "onUpdateMatch: 19",
                                               "onBeforeMatchFire: 19",
                                               "onAfterMatchFire: 20",
                                               "onDeleteMatch: 20" );
        assertThat(list).isEqualTo(expected);
    }
}
