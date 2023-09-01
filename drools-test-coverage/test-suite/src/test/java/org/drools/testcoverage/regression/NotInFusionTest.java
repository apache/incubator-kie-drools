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

import java.util.Collection;

import org.drools.testcoverage.common.KieSessionTest;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieSessionTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

// BZ 1009348
public class NotInFusionTest extends KieSessionTest {

    private static final String DRL_FILE = "JBRULES-3075.drl";

    private static final String RULE1 = "not equal";
    private static final String RULE2 = "not equal 2";
    private static final String RULE3 = "different";

    public NotInFusionTest(final KieBaseTestConfiguration kieBaseTestConfiguration,
                           final KieSessionTestConfiguration kieSessionTestConfiguration) {
        super(kieBaseTestConfiguration, kieSessionTestConfiguration);
    }

    @Parameterized.Parameters(name = "{1}" + " (from " + "{0}" + ")")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getStreamKieBaseAndStatefulKieSessionConfigurations();
    }

    @Test
    public void testNoEvent() {
        KieSession ksession = session.getStateful();
        ksession.fireAllRules();

        assertThat(firedRules.isRuleFired(RULE1)).as(RULE1).isTrue();
    }

    @Test
    public void testInsertFirst() throws Exception {
        KieSession ksession = session.getStateful();

        insertNotEvent(ksession);
        ksession.fireAllRules();

        assertThat(firedRules.isRuleFired(RULE1)).as(RULE1).isFalse();
    }

    @Test
    public void testInsertFirstAndAdd() throws Exception {
        KieSession ksession = session.getStateful();

        insertNotEvent(ksession);
        ksession.fireAllRules();

        assertThat(firedRules.isRuleFired(RULE1)).as(RULE1).isFalse();

        insertEvent(ksession);
        insertEvent(ksession);
        insertEvent(ksession);
        ksession.fireAllRules();

        assertThat(firedRules.isRuleFired(RULE1)).as(RULE1).isTrue();
    }

    @Test
    public void testInsertFirstAndAdd2() throws Exception {
        KieSession ksession = session.getStateful();

        insertNotEvent(ksession);
        ksession.fireAllRules();

        assertThat(firedRules.isRuleFired(RULE1)).as(RULE1).isFalse();

        for (int i = 0; i < 3; i++) {
            insertNotEvent(ksession, "different value");
        }

        ksession.fireAllRules();

        assertThat(firedRules.isRuleFired(RULE1)).as(RULE1).isTrue();
    }

    @Test
    public void testInsertFirstAndAdd3() throws Exception {
        KieSession ksession = session.getStateful();

        insertNotEvent(ksession);
        ksession.fireAllRules();

        assertThat(firedRules.isRuleFired(RULE1)).as(RULE1).isFalse();
        assertThat(firedRules.isRuleFired(RULE3)).as(RULE3).isFalse();

        for (int i = 0; i < 4; i++) {
            insertNotEvent(ksession, "different value");
            ksession.fireAllRules();
        }

        assertThat(firedRules.isRuleFired(RULE3)).as(RULE3).isTrue();
        assertThat(firedRules.isRuleFired(RULE1)).as(RULE1).isTrue();
    }

    @Test
    public void testNoEntryPoint() throws Exception {
        KieSession ksession = session.getStateful();

        ksession.insert(createNotEvent(ksession, "value"));
        ksession.fireAllRules();

        assertThat(firedRules.isRuleFired(RULE2)).isFalse();

        for (int i = 0; i < 3; i++) {
            ksession.insert(createNotEvent(ksession, "different value"));
        }

        ksession.fireAllRules();

        assertThat(firedRules.isRuleFired(RULE2)).as(RULE2).isTrue();
    }

    private void insertNotEvent(KieSession ksession) throws Exception {
        insertNotEvent(ksession, "value");
    }

    private void insertNotEvent(KieSession ksession, String property) throws Exception {
        ksession.getEntryPoint("entryPoint").insert(createNotEvent(ksession, property));
    }

    private Object createNotEvent(KieSession ksession, String property) throws Exception {
        FactType type = ksession.getKieBase().getFactType("org.drools.testcoverage.regression", "NotEvent");
        Object instance = type.newInstance();
        type.set(instance, "property", property);

        return instance;
    }

    private void insertEvent(KieSession ksession) throws Exception {
        ksession.getEntryPoint("entryPoint").insert(createEvent(ksession));
    }

    private Object createEvent(KieSession ksession) throws Exception {
        FactType type = ksession.getKieBase().getFactType("org.drools.testcoverage.regression", "Event");
        Object instance = type.newInstance();
        type.set(instance, "property", "some value");

        return instance;
    }

    @Override
    protected Resource[] createResources() {
        return KieUtil.createResources(DRL_FILE, NotInFusionTest.class);
    }
}
