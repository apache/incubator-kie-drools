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
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestConstants;
import org.junit.After;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for BZ 1150308.
 */
public class FixedPatternTest {

    private KieSession ksession;

    @After
    public void cleanup() {
        if (this.ksession != null) {
            this.ksession.dispose();
        }
    }

    /**
     * Tests fixed pattern without constraint in Decision table (BZ 1150308).
     */
    @Test
    public void testFixedPattern() {

        final Resource resource = KieServices.Factory.get().getResources().newClassPathResource("fixedPattern.xls", getClass());
        final KieBuilder kbuilder = KieUtil.getKieBuilderFromResources(true, resource);

        final KieContainer kcontainer = KieServices.Factory.get().newKieContainer(kbuilder.getKieModule().getReleaseId());
        ksession = kcontainer.newKieSession();

        final List<Long> list = new ArrayList<Long>();
        ksession.setGlobal("list", list);

        ksession.insert(1L);
        ksession.insert(2);
        ksession.fireAllRules();

        Assertions.assertThat(list).hasSize(1);
        Assertions.assertThat(list).first().isEqualTo(1L);
    }
}
