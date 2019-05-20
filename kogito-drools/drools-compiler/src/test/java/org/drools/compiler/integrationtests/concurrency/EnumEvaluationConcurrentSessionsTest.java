/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.integrationtests.concurrency;

import java.util.stream.Stream;

import org.drools.compiler.integrationtests.facts.CategoryTypeEnum;
import org.drools.compiler.integrationtests.facts.Product;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class EnumEvaluationConcurrentSessionsTest extends AbstractConcurrentTest {

    private static final Integer NUMBER_OF_THREADS = 10;
    private static final Integer NUMBER_OF_REPETITIONS = 1;

    static Stream<Arguments> parameters() {
        return Stream.of(
                new Parameters(true, true, true),
                new Parameters(true, true, false),
                new Parameters(true, false, true),
                new Parameters(true, false, false),
                new Parameters(false, true, true),
                new Parameters(false, true, false),
                new Parameters(false, false, true),
                new Parameters(false, false, false)
        ).map(Arguments::arguments);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testEnum2(Parameters params) throws InterruptedException {
        final String drl1 =
                "import " + Product.class.getCanonicalName() + ";\n" +
                        "import " + CategoryTypeEnum.class.getCanonicalName() + ";\n" +
                        "rule R1 when\n" +
                        "  $s : String( this == \"odd\" )\n" +
                        "  $p : Product( id != \"test\", categoryAsEnum == CategoryTypeEnum.ODD, firings not contains" +
                        " \"R1\" )\n" +
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
                        "  $p : Product( id != \"test\", categoryAsEnum == CategoryTypeEnum.PAIR, firings not " +
                        "contains \"R2\" )\n" +
                        "then\n" +
                        "  $p.getFirings().add(\"R2\");\n" +
                        "  $p.appendDescription($s);\n" +
                        "  update($p);" +
                        "end\n";

        parallelTest(params, NUMBER_OF_REPETITIONS, NUMBER_OF_THREADS, (kieSession, counter) -> {
            final Product[] products = new Product[10];
            final boolean pair = counter % 2 == 0;
            final String pairString = pair ? "pair" : "odd";
            for (int i = 0; i < 10; i++) {
                products[i] = new Product("" + i, pairString);
            }

            kieSession.insert(pairString);
            for (int i = 0; i < 10; i++) {
                kieSession.insert(products[i]);
            }

            kieSession.fireAllRules();

            for (int i = 0; i < 10; i++) {
                if (products[i].getCategory().equals(pairString) && !products[i].getCategory().equals(
                        products[i].getDescription())) {
                    return false;
                } else if (!products[i].getCategory().equals(pairString) && !products[i].getDescription().isEmpty()) {
                    return false;
                }
            }
            return true;
        }, null, null, drl1, drl2);
    }
}
