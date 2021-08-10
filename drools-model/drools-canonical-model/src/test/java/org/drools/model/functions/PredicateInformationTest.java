/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.drools.model.functions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PredicateInformationTest {

    @Test
    public void testMultipleRules() {
        PredicateInformation info = new PredicateInformation("age > 20");
        info.addRuleName("R1", "sample.drl");
        info.addRuleName("R2", "sample.drl");
        info.addRuleName("R3", "sample.drl");

        RuntimeException betterException = info.betterErrorMessage(new RuntimeException("OriginalException"));
        assertEquals("Error evaluating constraint 'age > 20' in [Rule \"R1\", \"R2\", \"R3\" in sample.drl]", betterException.getMessage());
    }

    @Test
    public void testMultipleRuleFiles() {
        PredicateInformation info = new PredicateInformation("age > 20");
        info.addRuleName("R1", "sample1.drl");
        info.addRuleName("R2", "sample1.drl");
        info.addRuleName("R3", "sample2.drl");

        RuntimeException betterException = info.betterErrorMessage(new RuntimeException("OriginalException"));
        assertEquals("Error evaluating constraint 'age > 20' in [Rule \"R1\", \"R2\" in sample1.drl] [Rule \"R3\" in sample2.drl]", betterException.getMessage());
    }
}
