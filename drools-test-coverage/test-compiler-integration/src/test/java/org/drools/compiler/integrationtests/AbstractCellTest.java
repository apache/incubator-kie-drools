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

import java.io.IOException;

import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.marshalling.ClassObjectMarshallingStrategyAcceptor;
import org.drools.serialization.protobuf.marshalling.IdentityPlaceholderResolverStrategy;
import org.drools.testcoverage.common.model.Cell;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieSessionTestConfiguration;
import org.drools.testcoverage.common.util.SerializationHelper;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractCellTest {

    protected final KieBaseTestConfiguration kieBaseTestConfiguration;

    public AbstractCellTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
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
                assertThat(cell.getValue()).isEqualTo(9);
            } else {
                assertThat(cell.getValue()).isEqualTo(0);
            }
        } finally {
            session.dispose();
        }
    }
}
