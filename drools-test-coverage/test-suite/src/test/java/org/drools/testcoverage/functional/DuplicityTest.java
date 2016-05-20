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

package org.drools.testcoverage.functional;

import org.assertj.core.api.Assertions;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.slf4j.LoggerFactory;

/**
 * Testing of duplicities in rule files.
 * https://bugzilla.redhat.com/show_bug.cgi?id=724753
 */
public class DuplicityTest {

    @Test
    public void testTwoRulesWithSameNameInOneFile() {
        try {
            final Resource resource =
                    KieServices.Factory.get().getResources().newClassPathResource("rule-name.drl", getClass());
            KieBaseUtil.getKieBuilderFromResources(true, resource);
            Assertions.fail("Builder should have had errors, two rules of the same name are not allowed in one file together!");
        } catch (AssertionError e) {
            // expected
            LoggerFactory.getLogger(getClass()).info("", e);
        }
    }
}
