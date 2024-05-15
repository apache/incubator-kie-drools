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
package org.drools.mvel.integrationtests;

import java.util.Collection;

import org.drools.mvel.integrationtests.facts.vehicles.DieselCar;
import org.drools.mvel.integrationtests.facts.vehicles.ElectricCar;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This is a place where known behavior differences between exec-model and non-exec-model.
 * They are not treated as a bug and should be documented in "Migration from non-executable model to executable model" section
 */
@RunWith(Parameterized.class)
public class GenericsTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public GenericsTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void property_subClassMethod_genericsReturnType() {
        // DROOLS-7197
        String str = "package com.example.reproducer\n" +
                     "import " + DieselCar.class.getCanonicalName() + ";\n" +
                     "rule R\n" +
                     "dialect \"mvel\"\n" +
                     "when\n" +
                     "  $v : DieselCar(motor.adBlueRequired == true)\n" +
                     "then\n" +
                     "  $v.score = 5;\n" +
                     "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        DieselCar dieselCar = new DieselCar("ABC", "Model 1.6", 85, true);

        ksession.insert(dieselCar);
        ksession.fireAllRules();

        assertThat(dieselCar.getScore()).isEqualTo(5);
    }

    @Test
    public void property_subClassMethod_explicitReturnType() {
        // DROOLS-7197
        String str = "package com.example.reproducer\n" +
                     "import " + ElectricCar.class.getCanonicalName() + ";\n" +
                     "rule R\n" +
                     "dialect \"mvel\"\n" +
                     "when\n" +
                     "  $v : ElectricCar(engine.batterySize > 70)\n" +
                     "then\n" +
                     "  $v.score = 5;\n" +
                     "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        ElectricCar electricCar = new ElectricCar("XYZ", "Model 3", 200, 90);

        ksession.insert(electricCar);
        ksession.fireAllRules();

        assertThat(electricCar.getScore()).isEqualTo(5); // works for both cases
    }
}
