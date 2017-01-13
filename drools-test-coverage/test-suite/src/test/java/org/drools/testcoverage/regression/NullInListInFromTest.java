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
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests handling a null value in a list used in FROM (BZ 1093174).
 */
public class NullInListInFromTest {

    private static final String DRL =
            "global java.util.List list\n" +
            "\n" +
            "rule R\n" +
            "when\n" +
            "    $i : Integer( ) from list\n" +
            "then\n" +
            "end\n";

    @Test
    public void testNullValueInFrom() throws Exception {
        final Resource resource = KieServices.Factory.get().getResources().newReaderResource(new StringReader(DRL));
        resource.setTargetPath(TestConstants.DRL_TEST_TARGET_PATH);
        final KieBuilder kbuilder = KieBaseUtil.getKieBuilderFromResources(true, resource);

        final KieBase kbase = KieBaseUtil.getDefaultKieBaseFromKieBuilder(kbuilder);
        final KieSession ksession = kbase.newKieSession();

        final List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list);

        list.add(1);
        list.add(null);
        list.add(2);

        try {
            ksession.fireAllRules();
        } finally {
            ksession.dispose();
        }
    }
}
