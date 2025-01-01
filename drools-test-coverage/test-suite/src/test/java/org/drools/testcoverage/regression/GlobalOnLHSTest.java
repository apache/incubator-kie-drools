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
import org.drools.testcoverage.common.model.Message;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieSessionTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class GlobalOnLHSTest extends KieSessionTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalOnLHSTest.class);

    private static final String DRL_FILE = "bz1019473.drl";

    public static Stream<Arguments> parameters() {
        return TestParametersUtil2.getKieBaseAndStatefulKieSessionConfigurations().stream();
    }

    @ParameterizedTest(name = "{1}" + " (from " + "{0}" + ")")
	@MethodSource("parameters")
    public void testNPEOnMutableGlobal(KieBaseTestConfiguration kieBaseTestConfiguration,
            KieSessionTestConfiguration kieSessionTestConfiguration) throws Exception {
    	createKieSession(kieBaseTestConfiguration, kieSessionTestConfiguration);
        KieSession ksession = session.getStateful();

        List<String> context = new ArrayList<String>();
        ksession.setGlobal("context", context);
        ksession.setGlobal("LOGGER", LOGGER);

        FactHandle b = ksession.insert( new Message( "b" ) );
        ksession.delete(b);
        int fired = ksession.fireAllRules(1);

        assertThat(fired).isEqualTo(0);
        ksession.dispose();
    }

    @Override
    protected Resource[] createResources() {
        return KieUtil.createResources(DRL_FILE, GlobalOnLHSTest.class);
    }
}
