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

import java.util.Collection;

import org.assertj.core.api.Assertions;
import org.drools.mvel.compiler.StockTick;
import org.drools.mvel.integrationtests.SerializationHelper;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.TimerJobFactoryOption;

/**
 * Verifies that serialization and de-serialization of a composite trigger succeeds (BZ 1142914).
 */
@RunWith(Parameterized.class)
public class DeserializationWithCompositeTriggerTest {

    private static final String DRL =
            "package org.drools.test;\n" +
            "import org.drools.mvel.compiler.StockTick;\n" +
            "global java.util.List list;\n" +
            "\n" +
            "declare StockTick\n" +
            " @role( event )\n" +
            " @expires( 1s )\n" +
            "end\n" +
            "\n" +
            "rule \"One\"\n" +
            "when\n" +
            " $event : StockTick( )\n" +
            " not StockTick( company == \"BBB\", this after[0,96h] $event )\n" +
            " not StockTick( company == \"CCC\", this after[0,96h] $event )\n" +
            "then\n" +
            "end\n";

    private KieSession ksession;

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public DeserializationWithCompositeTriggerTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseConfigurations();
    }

    @Before
    public void prepare() {
        final KieBuilder kbuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, true, DRL);
        final KieContainer kcontainer = KieServices.Factory.get().newKieContainer(kbuilder.getKieModule().getReleaseId());

        final KieBaseConfiguration kieBaseConfiguration = KieServices.Factory.get().newKieBaseConfiguration();
        kieBaseConfiguration.setOption(EventProcessingOption.STREAM);

        final KieSessionConfiguration kieSessionConfiguration = KieServices.Factory.get().newKieSessionConfiguration();
        kieSessionConfiguration.setOption(TimerJobFactoryOption.get("trackable"));

        final KieBase kbase = kcontainer.newKieBase(kieBaseConfiguration);
        this.ksession = kbase.newKieSession(kieSessionConfiguration, null);
    }

    @After
    public void cleanup() {
        if (this.ksession != null) {
            this.ksession.dispose();
        }
    }

    /**
     * Verifies that serialization of a rule with composite trigger does not fail on
     * org.drools.core.time.impl.CompositeMaxDurationTrigger class serialization.
     */
    @Test
    public void testSerializationAndDeserialization() throws Exception {
        this.ksession.insert(new StockTick(2, "AAA", 1.0, 0));

        this.ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true, false);
        Assertions.assertThat(this.ksession).isNotNull();
    }
}
