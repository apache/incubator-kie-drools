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
package org.drools.mvel.integrationtests.concurrency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.drools.mvel.integrationtests.facts.CategoryTypeEnum;
import org.drools.mvel.integrationtests.facts.Product;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class EnumEvaluationConcurrentSessionsTest extends AbstractConcurrentTest {

    private static final Integer NUMBER_OF_THREADS = 10;
    private static final Integer NUMBER_OF_REPETITIONS = 1;

    @Parameterized.Parameters(name = "Enforced jitting={0}, Share KieBase={1}, KieBase type={2}")
    public static List<Object[]> getTestParameters() {
        List<Boolean[]> baseParams = Arrays.asList(
                                                   new Boolean[]{false, false},
                                                   new Boolean[]{true, false},
                                                   new Boolean[]{false, true},
                                                   new Boolean[]{true, true});

        Collection<Object[]> kbParams = TestParametersUtil.getKieBaseCloudConfigurations(true);
        // combine
        List<Object[]> params = new ArrayList<>();
        for (Boolean[] baseParam : baseParams) {
            for (Object[] kbParam : kbParams) {
                if (baseParam[0] == true && ((KieBaseTestConfiguration) kbParam[0]).isExecutableModel()) {
                    // jitting & exec-model test is not required
                } else {
                    params.add(new Object[]{baseParam[0], baseParam[1], kbParam[0]});
                }
            }
        }
        return params;
    }

    public EnumEvaluationConcurrentSessionsTest(final boolean enforcedJitting,
                                                final boolean sharedKieBase, final KieBaseTestConfiguration kieBaseTestConfiguration) {
        super(enforcedJitting, false, sharedKieBase, false, kieBaseTestConfiguration);
    }

    @Test(timeout = 40000)
    public void testEnum2() throws InterruptedException {
        final String drl1 =
                "import " + Product.class.getCanonicalName() + ";\n" +
                        "import " + CategoryTypeEnum.class.getCanonicalName() + ";\n" +
                        "rule R1 when\n" +
                        "  $s : String( this == \"odd\" )\n" +
                        "  $p : Product( id != \"test\", categoryAsEnum == CategoryTypeEnum.ODD, firings not contains \"R1\" )\n" +
                        "then\n" +
                        "  $p.getFirings().add(\"R1\");\n" +
                        "  $p.appendDescription($s);\n" +
                        "  update($p);\n" +
                        "end\n";

        final String drl2 =
                "import " + Product.class.getCanonicalName() + ";\n" +
                        "import " + CategoryTypeEnum.class.getCanonicalName() + ";\n" +
                        "rule R2 when\n" +
                        "  $s : String( this == \"pair\" )\n" +
                        "  $p : Product( id != \"test\", categoryAsEnum == CategoryTypeEnum.PAIR, firings not contains \"R2\" )\n" +
                        "then\n" +
                        "  $p.getFirings().add(\"R2\");\n" +
                        "  $p.appendDescription($s);\n" +
                        "  update($p);" +
                        "end\n";

        parallelTest(NUMBER_OF_REPETITIONS, NUMBER_OF_THREADS, (kieSession, counter) -> {
            final Product[] products = new Product[10];
            final boolean pair = counter % 2 == 0;
            final String pairString = pair ? "pair" : "odd";
            for (int i = 0; i < 10; i++) {
                products[i] = new Product( "" + i, pairString );
            }

            kieSession.insert( pairString );
            for (int i = 0; i < 10; i++) {
                kieSession.insert( products[i] );
            }

            kieSession.fireAllRules();

            for (int i = 0; i < 10; i++) {
                if ( products[i].getCategory().equals(pairString) && !products[i].getCategory().equals( products[i].getDescription() ) ) {
                    return false;
                } else if (!products[i].getCategory().equals(pairString) && !products[i].getDescription().isEmpty()) {
                    return false;
                }
            }
            return true;
        }, null, null, drl1, drl2 );
    }
}
