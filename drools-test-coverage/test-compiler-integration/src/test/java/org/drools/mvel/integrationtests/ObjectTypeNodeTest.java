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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

@RunWith(Parameterized.class)
public class ObjectTypeNodeTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public ObjectTypeNodeTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testDeterministicOTNOrdering() throws Exception {
        // JBRULES-3632
        String str =
                "package indexingproblem.remove.me.anditworks;\n" +
                        "declare Criteria\n" +
                        "   processed : boolean\n" +
                        "end\n" +
                        "\n" +
                        "declare CheeseCriteria extends Criteria end\n" +
                        "\n" +
                        "rule setUp salience 10000 when\n" +
                        "then\n" +
                        "   insert(new CheeseCriteria());\n" +
                        "end\n" +
                        "\n" +
                        "rule aaa when\n" +
                        "   CheeseCriteria( )\n" +
                        "then\n" +
                        "end\n" +
                        "\n" +
                        "rule bbb when\n" +
                        "   CheeseCriteria( )\n" +
                        "then\n" +
                        "end\n" +
                        "\n" +
                        "rule ccc when\n" +
                        "   CheeseCriteria( )\n" +
                        "then\n" +
                        "end\n" +
                        "\n" +
                        "rule eeeFalse when\n" +
                        "   Criteria( processed == false )\n" +
                        "then\n" +
                        "end\n" +
                        "\n" +
                        "declare Filter end\n" +
                        "\n" +
                        "rule fffTrue when\n" +
                        "   Criteria( processed == true )\n" +
                        "   Filter( )\n" +
                        "then\n" +
                        "end\n" +
                        "\n" +
                        "rule ruleThatFails when\n" +
                        "   $criteria : Criteria( processed == false )\n" +
                        "then\n" +
                        "   modify($criteria) { setProcessed(true) }\n" +
                        "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        ksession.fireAllRules();

        // check that OTNs ordering is not breaking serialization
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
    }

}
