/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
import org.drools.testcoverage.common.listener.TrackingAgendaEventListener;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestConstants;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Parameterized.class)
public class UnwantedStringConversionTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(UnwantedStringConversionTest.class);

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public UnwantedStringConversionTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseConfigurations();
    }

    @Test
    public void testStringToDecimalConversion() {
        final Resource drlResource =
                KieServices.Factory.get().getResources().newClassPathResource("unwantedStringConversionTest.drl", getClass());
        final KieBase kieBase = KieBaseUtil.getKieBaseAndBuildInstallModule(TestConstants.PACKAGE_REGRESSION,
                kieBaseTestConfiguration, drlResource);

        final KieSession ksession = kieBase.newKieSession();
        final TrackingAgendaEventListener listener = new TrackingAgendaEventListener();
        ksession.addEventListener(listener);
        final Message message = new Message();
        final SomeEntity someEntity = new SomeEntity("1.5");
        message.setSomeEntity(someEntity);
        message.setMessage("1.5");
        ksession.insert(message);
        ksession.fireAllRules();

        Assertions.assertThat(listener.isRuleFired("R1")).isFalse();
        Assertions.assertThat(listener.isRuleFired("R2")).isFalse();

        Assertions.assertThat(listener.rulesCount()).isEqualTo(0);
    }

    public static class Message {

        public static final int HELLO = 0;
        public static final int GOODBYE = 1;

        private SomeEntity someEntity;

        private String message;

        private int status;

        public String getMessage() {
            return this.message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getStatus() {
            return this.status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public void setSomeEntity(SomeEntity someEntity) {
            this.someEntity = someEntity;
        }

        public SomeEntity getSomeEntity() {
            return someEntity;
        }

    }

    public static class SomeEntity {

        private String someString;

        public SomeEntity() {
        }

        public SomeEntity(String someString) {
            this.someString = someString;
        }

        public void setSomeString(String someString) {
            this.someString = someString;
        }

        public String getSomeString() {
            return someString;
        }

        public void customMethod() {
            LOGGER.debug("executing customMethod");
        }

    }
}
