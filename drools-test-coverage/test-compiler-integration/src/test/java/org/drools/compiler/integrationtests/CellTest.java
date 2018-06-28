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

package org.drools.compiler.integrationtests;

import java.io.IOException;
import java.util.Collection;

import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.marshalling.impl.ClassObjectMarshallingStrategyAcceptor;
import org.drools.core.marshalling.impl.IdentityPlaceholderResolverStrategy;
import org.drools.testcoverage.common.model.Cell;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieSessionTestConfiguration;
import org.drools.testcoverage.common.util.SerializationHelper;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class CellTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public CellTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testCell() throws IOException, ClassNotFoundException {

        final String drl = "package evalmodify;\n" +
                "\n" +
                "import " + Cell.class.getCanonicalName() + "\n" +
                "import java.lang.Integer\n" +
                "\n" +
                "rule \"test eval\"\n" +
                "    when\n" +
                "        cell1 : Cell(value1:value != 0)\n" +
                "        cell2 : Cell(value2:value < value1)\n" +
                "        eval (true)\n" +
                "    then\n" +
                "        cell2.setValue(value2 + 1);\n" +
                "        update(cell2);\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("cell-test", kieBaseTestConfiguration, drl);
        final Environment env = EnvironmentFactory.newEnvironment();
        env.set(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES,
                new ObjectMarshallingStrategy[]{new IdentityPlaceholderResolverStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT)});
        KieSession session = kbase.newKieSession(KieSessionTestConfiguration.STATEFUL_REALTIME.getKieSessionConfiguration(), env);
        try {
            final Cell cell1 = new Cell(9);
            final Cell cell = new Cell(0);

            session.insert(cell1);
            session.insert(cell);

            session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, true);

            session.fireAllRules();
            if (kieBaseTestConfiguration.isIdentity()) {
                assertEquals(9, cell.getValue());
            } else {
                assertEquals(0, cell.getValue());
            }
        } finally {
            session.dispose();
        }
    }

    @Test
    public void testFreeFormExpressions() {
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
            assertEquals(1, rules);
        } finally {
            ksession.dispose();
        }
    }
}
