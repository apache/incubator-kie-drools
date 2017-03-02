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

import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestConstants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Tests generics in RHS with modify - BZ 1142886.
 */
public class GenericsWithModifyTest {

    private static final String DRL =
            "package defaultKBase;\n" +
            "import java.util.Map;\n" +
            "import java.util.HashMap;\n" +
            "rule R no-loop when\n" +
            " $s : String( )\n" +
            "then\n" +
            " Map<String,String> a = new HashMap<String,String>();\n" +
            " modify( $s ) { };\n" +
            "end\n";

    private KieSession kieSession;

    @Before
    public void initialize() throws IOException {
        final Resource resource = KieServices.Factory.get().getResources().newByteArrayResource(DRL.getBytes(Charset.forName("UTF-8")));
        resource.setTargetPath(TestConstants.DRL_TEST_TARGET_PATH);
        final KieBuilder kbuilder = KieBaseUtil.getKieBuilderFromResources(true, resource);

        final KieContainer kieContainer = KieServices.Factory.get().newKieContainer(kbuilder.getKieModule().getReleaseId());
        this.kieSession = kieContainer.newKieSession();
    }

    @After
    public void dispose() {
        if (this.kieSession != null) {
            this.kieSession.dispose();
        }
    }

    /**
     * Tests generics on rule's RHS with modify.
     */
    @Test
    public void testModifyWithGenericsOnRHS() {
        this.kieSession.insert("1");
        this.kieSession.fireAllRules();
    }
}
