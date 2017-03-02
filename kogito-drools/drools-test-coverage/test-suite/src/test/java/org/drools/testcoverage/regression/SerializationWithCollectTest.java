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

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.marshalling.Marshaller;
import org.kie.api.runtime.KieSession;
import org.kie.internal.marshalling.MarshallerFactory;
import org.kie.internal.utils.KieHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Simple reproducer for BZ 1193600 - serialization of rules with collect.
 */
public class SerializationWithCollectTest {

    private static final String DRL =
            "import java.util.Collection\n"
            + "rule R1 when\n"
            + " Collection(empty==false) from collect( Integer() )\n"
            + " Collection() from collect( String() )\n"
            + "then\n"
            + "end\n"
            + "rule R2 when then end\n";

    private KieBase kbase;
    private KieSession ksession;

    @Before
    public void setup() {
        this.kbase = new KieHelper().addContent(DRL, ResourceType.DRL).build();
        this.ksession = kbase.newKieSession();
    }

    @After
    public void cleanup() {
        if (this.ksession != null) {
            this.ksession.dispose();
        }
    }

    /**
     * BZ 1193600
     */
    @Test
    public void test() throws Exception {
        Marshaller marshaller = MarshallerFactory.newMarshaller(kbase);
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            marshaller.marshall(baos, ksession);
            try (final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray())) {
                marshaller = MarshallerFactory.newMarshaller(kbase);
                ksession = marshaller.unmarshall(bais);
            }
        } catch (NullPointerException e) {
            Assertions.fail("Consider reopening BZ 1193600!", e);
        }
    }
}
