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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import org.drools.testcoverage.common.KieSessionTest;
import org.drools.testcoverage.common.model.Promotion;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieSessionTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.io.Resource;
import org.kie.api.marshalling.Marshaller;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.testcoverage.common.util.KieUtil.getServices;

public class LogicalInsertionsSerializationTest extends KieSessionTest {

    private static final String DRL_FILE = "logical-insertion.drl";

    public LogicalInsertionsSerializationTest(final KieBaseTestConfiguration kieBaseTestConfiguration,
                                              final KieSessionTestConfiguration kieSessionTestConfiguration) {
        super(kieBaseTestConfiguration, kieSessionTestConfiguration);
    }

    @Rule
    public TestName name = new TestName();

    @Parameterized.Parameters(name = "{1}" + " (from " + "{0}" + ")")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseAndStatefulKieSessionConfigurations();
    }

    @Test
    public void testSerializeAndDeserializeSession() throws Exception {
        KieSession ksession = session.getStateful();
        File tempFile = File.createTempFile(name.getMethodName(), null);

        ksession.fireAllRules();

        try (OutputStream fos = new FileOutputStream(tempFile)) {
            Marshaller marshaller = getServices().getMarshallers().newMarshaller(getKbase());
            marshaller.marshall(fos, ksession);
        }

        try (InputStream fis = new FileInputStream(tempFile)) {
            Marshaller marshaller = getServices().getMarshallers().newMarshaller(getKbase());
            marshaller.unmarshall(fis, ksession);
        }

        ksession.insert(new Promotion("Claire", "Scientist"));
        int firedRules = ksession.fireAllRules();

        assertThat(firedRules).isEqualTo(1);
    }

    private KieBase getKbase() {
        return session.isStateful() ? session.getStateful().getKieBase() : session.getStateless().getKieBase();
    }

    @Override
    protected Resource[] createResources() {
        return KieUtil.createResources(DRL_FILE, LogicalInsertionsSerializationTest.class);
    }
}
