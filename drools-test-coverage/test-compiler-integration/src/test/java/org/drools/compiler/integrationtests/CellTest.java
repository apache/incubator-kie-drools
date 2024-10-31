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

import java.util.stream.Stream;

import org.drools.testcoverage.common.model.Cell;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class CellTest extends AbstractCellTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testFreeFormExpressions(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final String drl = "package org.drools.compiler\n" +
                "import " + Cell.class.getCanonicalName() + "\n" +
                "rule r1\n" +
                "when\n" +
                "    $p1 : Cell( row == 2 )\n" +
                "    $p2 : Cell( row == $p1.row + 1, row == ($p1.row + 1), row == 1 + $p1.row, row == (1 + $p1.row) )\n" +
                "then\n" +
                "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("cell-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final Cell c1 = new Cell(1, 2, 0);
            final Cell c2 = new Cell(1, 3, 0);
            ksession.insert(c1);
            ksession.insert(c2);

            final int rules = ksession.fireAllRules();
            assertThat(rules).isEqualTo(1);
        } finally {
            ksession.dispose();
        }
    }
}
