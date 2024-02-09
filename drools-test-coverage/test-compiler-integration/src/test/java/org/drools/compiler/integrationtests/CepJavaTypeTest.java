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

import java.util.Collection;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.drools.testcoverage.common.util.TimeUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.builder.KieBuilder;
import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class CepJavaTypeTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public CepJavaTypeTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseStreamConfigurations(true);
    }

    @Role(value = Role.Type.EVENT)
    public static class Event { }

    @Test
    public void testJavaTypeAnnotatedWithRole_WindowTime() {
        final String drl = "package org.drools.compiler.integrationtests\n"
                + "\n"
                + "import " + CepJavaTypeTest.Event.class.getCanonicalName() + ";\n"
                + "\n"
                + "rule \"CEP Window Time\"\n"
                + "when\n"
                + "    Event() over window:time (1d)\n"
                + "then\n"
                + "end\n";

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        assertThat(kieBuilder.getResults().getMessages()).isEmpty();
    }

    @Test
    public void testJavaTypeAnnotatedWithRole_WindowLength() {
        final String drl = "package org.drools.compiler.integrationtests\n"
                + "\n"
                + "import " + CepJavaTypeTest.Event.class.getCanonicalName() + ";\n"
                + "\n"
                + "rule \"CEP Window Length\"\n"
                + "when\n"
                + "    Event() over window:length (10)\n"
                + "then\n"
                + "end\n";

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        assertThat(kieBuilder.getResults().getMessages()).isEmpty();
    }

    @Role(value = Role.Type.EVENT)
    @Timestamp( "Ts" )
    @Expires( "1ms" )
    public static class MyMessage {
        String name;
        long ts;

        public MyMessage(final String n) {
            name = n;
            ts = System.currentTimeMillis();
        }

        public void setName(final String n) { name = n; }
        public String getName() { return name; }
        public void setTs(final long t) { ts = t; }
        public long getTs() { return ts; }
    }

    @Test(timeout = 5000)
    public void testEventWithShortExpiration() throws InterruptedException {
        // BZ-1265773
        final String drl = "import " + MyMessage.class.getCanonicalName() +"\n" +
                     "rule \"Rule A Start\"\n" +
                     "when\n" +
                     "  MyMessage ( name == \"ATrigger\" )\n" +
                     "then\n" +
                     "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("cep-java-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            ksession.insert(new MyMessage("ATrigger"));
            assertThat(ksession.fireAllRules()).isEqualTo(1);
            TimeUtil.sleepMillis(2L);
            assertThat(ksession.fireAllRules()).isEqualTo(0);
            while (ksession.getObjects().size() != 0) {
                TimeUtil.sleepMillis(30L);
                // Expire action is put into propagation queue by timer job, so there
                // can be a race condition where it puts it there right after previous fireAllRules
                // flushes the queue. So there needs to be another flush -> another fireAllRules
                // to flush the queue.
                assertThat(ksession.fireAllRules()).isEqualTo(0);
            }
        } finally {
            ksession.dispose();
        }
    }
}
