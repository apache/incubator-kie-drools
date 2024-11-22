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
package org.drools.testcoverage.regression;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.drools.testcoverage.common.KieSessionTest;
import org.drools.testcoverage.common.model.Overloaded;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieSessionTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.command.Command;
import org.kie.api.io.Resource;
import org.kie.internal.command.CommandFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test verifies that bug #843284 is fixed. Bug is about Mvel not choosing
 * correct overloaded method from multiple with same types of arguments but in
 * different order, e.g. method(int, int, String) vs method(int, String, int).
 */
public class MvelOverloadedMethodsUsageTest extends KieSessionTest {

    private static final String DRL =
            "package org.drools.testcoverage.regression;\n" +
            "import org.drools.testcoverage.common.model.Overloaded;\n" +
            "rule MvelOverloadedMethods\n" +
            "    dialect \"mvel\"" +
            "    when\n" +
            "        o : Overloaded()\n" +
            "        eval (o.method(5, 9, \"x\") == 15)\n" +
            "        eval (o.method(\"x\", 5, 9) == -13)\n" +
            "        eval (o.method(5, \"x\", 9) == -3)\n" +
            "    then\n" +
            "end\n" +
            "rule MvelOverloadedMethods2\n" +
            "    dialect \"mvel\"" +
            "    when\n" +
            "        o : Overloaded()\n" +
            "        eval (\"helloworld150.32\".equals(o.method2(\"hello\", \"world\", 15L, 0.32)))\n" +
            "        eval (\"32hello0.53world\".equals(o.method2(32L, \"hello\", 0.53, \"world\")))\n" +
            "    then\n" +
            "end\n";

    public static Stream<Arguments> parameters() {
        return TestParametersUtil2.getKieBaseAndStatefulKieSessionConfigurations().stream();
    }

    @ParameterizedTest(name = "{1}" + " (from " + "{0}" + ")")
	@MethodSource("parameters")
    public void testMvelOverloadedMethodsUsage(KieBaseTestConfiguration kieBaseTestConfiguration,
            KieSessionTestConfiguration kieSessionTestConfiguration) {
    	createKieSession(kieBaseTestConfiguration, kieSessionTestConfiguration);
        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(CommandFactory.newInsert(new Overloaded()));
        commands.add(CommandFactory.newFireAllRules());
        session.execute(CommandFactory.newBatchExecution(commands));

        assertThat(firedRules.ruleFiredCount("MvelOverloadedMethods")).isEqualTo(1);
        assertThat(firedRules.ruleFiredCount("MvelOverloadedMethods2")).isEqualTo(1);
    }

    @Override
    protected Resource[] createResources() {
        return KieUtil.createResources(DRL);
    }
}
