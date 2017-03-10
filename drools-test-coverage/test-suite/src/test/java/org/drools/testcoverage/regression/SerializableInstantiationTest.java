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
import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.io.Resource;

import java.io.Serializable;
import java.io.StringReader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class SerializableInstantiationTest extends KieSessionTest {

    private static final String DRL =
            "package org.drools.testcoverage.regression;\n" +
            "import org.drools.testcoverage.regression.SerializableInstantiationTest.SerializableWrapper;\n" +
            "rule serializable\n" +
            "    when\n" +
            "        $holder : SerializableWrapper( original == \"hello\" )\n" +
            "    then\n" +
            "//        System.out.println(\"Works like a charm!\");\n" +
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
        List<Command<?>> commands = new LinkedList<Command<?>>();
        commands.add(KieServices.Factory.get().getCommands().newInsert(new SerializableWrapper("hello")));
        commands.add(KieServices.Factory.get().getCommands().newFireAllRules());

        for (int i = 0; i < 500; i++) {
            session.execute(KieServices.Factory.get().getCommands().newBatchExecution(commands));
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
        final Resource drlResource = KieServices.Factory.get().getResources().newReaderResource(new StringReader(DRL));
        drlResource.setTargetPath(TestConstants.DRL_TEST_TARGET_PATH);
        return new Resource[] { drlResource };
    }
}
