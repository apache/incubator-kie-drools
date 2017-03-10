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
import org.drools.testcoverage.common.model.Promotion;
import org.drools.testcoverage.common.util.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.kie.api.marshalling.Marshaller;
import org.kie.api.runtime.KieSession;

import java.io.*;
import java.util.Collection;

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
        File tempFile = createTempFile(name.getMethodName(), "");

        ksession.fireAllRules();

        OutputStream fos = null;
        try {
            fos = new FileOutputStream(tempFile);

            Marshaller marshaller = KieServices.Factory.get().getMarshallers().newMarshaller(getKbase());
            marshaller.marshall(fos, ksession);
        } finally {
            if (fos != null) {
                fos.close();
            }
        }

        InputStream fis = null;
        try {
            fis = new FileInputStream(tempFile);

            Marshaller marshaller = KieServices.Factory.get().getMarshallers().newMarshaller(getKbase());
            marshaller.unmarshall(fis, ksession);
        } finally {
            if (fis != null) {
                fis.close();
            }
        }

        ksession.insert(new Promotion("Claire", "Scientist"));
        int firedRules = ksession.fireAllRules();

        Assertions.assertThat(firedRules).isEqualTo(1);
    }

    private final File createTempFile(String name, String extension) {
        File dir = new File(PropertiesUtil.getTempDir(), name);
        if (!dir.exists()) {
            dir.mkdir();
        }
        int i = 0;
        File temp;
        while ((temp = new File(dir, String.format("%s_%03d.%s", name, i++, extension))).exists()) {
        }
        return temp;
    }

    private KieBase getKbase() {
        return session.isStateful() ? session.getStateful().getKieBase() : session.getStateless().getKieBase();
    }

    @Override
    protected Resource[] createResources() {
        return new Resource[] { KieServices.Factory.get().getResources().newClassPathResource(DRL_FILE, LogicalInsertionsSerializationTest.class) };
    }
}
