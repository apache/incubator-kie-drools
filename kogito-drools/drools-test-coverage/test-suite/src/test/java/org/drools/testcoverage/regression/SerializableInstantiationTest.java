/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.testcoverage.regression;

import org.drools.testcoverage.common.KieSessionTest;
import org.drools.testcoverage.common.util.*;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.kie.api.command.Command;
import org.kie.api.io.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.drools.testcoverage.common.util.KieUtil.getCommands;

public class SerializableInstantiationTest extends KieSessionTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SerializableInstantiationTest.class);

    private static final String DRL =
            "package org.drools.testcoverage.regression;\n" +
            "import org.drools.testcoverage.regression.SerializableInstantiationTest.SerializableWrapper;\n" +
            "global org.slf4j.Logger LOGGER;\n" +
            "rule serializable\n" +
            "    when\n" +
            "        $holder : SerializableWrapper( original == \"hello\" )\n" +
            "    then\n" +
            "//        LOGGER.info(\"Works like a charm!\");\n" +
            "end\n";

    public SerializableInstantiationTest(final KieBaseTestConfiguration kieBaseTestConfiguration,
                                         final KieSessionTestConfiguration kieSessionTestConfiguration) {
        super(kieBaseTestConfiguration, kieSessionTestConfiguration);
    }

    @Parameterized.Parameters(name = "{1}" + " (from " + "{0}" + ")")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseAndKieSessionConfigurations();
    }

    @Test
    public void testSerializableInstantiation() {
        session.setGlobal("LOGGER", LOGGER);
        List<Command<?>> commands = new LinkedList<Command<?>>();
        commands.add(getCommands().newInsert(new SerializableWrapper("hello")));
        commands.add(getCommands().newFireAllRules());

        for (int i = 0; i < 500; i++) {
            session.execute(getCommands().newBatchExecution(commands));
        }
    }

    @SuppressWarnings("serial")
    public static class SerializableWrapper implements Serializable {
        private final Serializable original;
        public SerializableWrapper(Serializable original) {
            this.original = original;
        }
        public Serializable getOriginal() {
            return original;
        }
    }

    @Override
    protected Resource[] createResources() {
        return KieUtil.createResources(DRL);
    }
}
