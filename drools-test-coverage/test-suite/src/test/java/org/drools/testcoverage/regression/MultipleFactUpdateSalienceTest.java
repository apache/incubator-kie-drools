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


import org.assertj.core.api.Assertions;
import org.drools.testcoverage.common.KieSessionTest;
import org.drools.testcoverage.common.listener.OrderListener;
import org.drools.testcoverage.common.model.Customer;
import org.drools.testcoverage.common.model.Sale;
import org.drools.testcoverage.common.util.*;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.io.Resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Test to verify that BRMS-586 (Rules do not fire according to salience after
 * multiple fact updates) is fixed
 *
 */
public class MultipleFactUpdateSalienceTest extends KieSessionTest {

    private static final String DRL_FILE = "BRMS-586.drl";

    // expected rules fired - result of Drools 5.2, same results from BRMS-590
    // patch
    private static final String[] EXPECTED = new String[] { "Rebate", "No rebate", "Rebate", "3rd sale", "No rebate",
            "after 3rd sale", "No rebate", "No rebate", "No rebate", "3rd sale", "No rebate", "after 3rd sale",
            "No rebate", "Rebate", "No rebate", "3rd sale", "Rebate", "after 3rd sale", "Print customer",
            "Print customer", "Print customer", "Print sales", "Print sales", "Print sales", "Print sales",
            "Print sales", "Print sales", "Print sales", "Print sales", "Print sales", "Print sales", "Print sales",
            "Print sales" };

    public MultipleFactUpdateSalienceTest(final KieBaseTestConfiguration kieBaseTestConfiguration,
                                          final KieSessionTestConfiguration kieSessionTestConfiguration) {
        super(kieBaseTestConfiguration, kieSessionTestConfiguration);
    }

    @Parameterized.Parameters(name = "{1}" + " (from " + "{0}" + ")")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseAndStatefulKieSessionConfigurations();
    }

    @Test
    public void test() {
        OrderListener listener = new OrderListener();
        session.addEventListener(listener);

        List<Command<?>> commands = new ArrayList<Command<?>>();

        commands.add(KieServices.Factory.get().getCommands().newInsert(new Customer(1, "Homer")));
        commands.add(KieServices.Factory.get().getCommands().newInsert(new Customer(2, "Bart")));
        commands.add(KieServices.Factory.get().getCommands().newInsert(new Customer(3, "Marge")));

        commands.add(KieServices.Factory.get().getCommands().newInsert(new Sale(10, 1, 1250)));
        commands.add(KieServices.Factory.get().getCommands().newInsert(new Sale(11, 1, 500)));
        commands.add(KieServices.Factory.get().getCommands().newInsert(new Sale(12, 1, 2300)));
        commands.add(KieServices.Factory.get().getCommands().newInsert(new Sale(13, 1, 590)));

        commands.add(KieServices.Factory.get().getCommands().newInsert(new Sale(27, 2, 111)));
        commands.add(KieServices.Factory.get().getCommands().newInsert(new Sale(28, 2, 678)));
        commands.add(KieServices.Factory.get().getCommands().newInsert(new Sale(29, 2, 250)));
        commands.add(KieServices.Factory.get().getCommands().newInsert(new Sale(30, 2, 450)));

        commands.add(KieServices.Factory.get().getCommands().newInsert(new Sale(45, 3, 446)));
        commands.add(KieServices.Factory.get().getCommands().newInsert(new Sale(46, 3, 3280)));
        commands.add(KieServices.Factory.get().getCommands().newInsert(new Sale(47, 3, 340)));
        commands.add(KieServices.Factory.get().getCommands().newInsert(new Sale(48, 3, 1250)));

        commands.add(KieServices.Factory.get().getCommands().newFireAllRules());
        session.execute(KieServices.Factory.get().getCommands().newBatchExecution(commands, null));

        for (int i = 0; i < Math.min(EXPECTED.length, listener.size()); i++) {
            Assertions.assertThat(listener.get(i)).isEqualTo(EXPECTED[i]);
        }
    }

    @Override
    protected Resource[] createResources() {
        return new Resource[] { KieServices.Factory.get().getResources().newClassPathResource(DRL_FILE, MultipleFactUpdateSalienceTest.class) };
    }
}
