/**
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kie.kogito.resources;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConditionHolderTest {

    private static final String RESOURCE_NAME = "my-test-resource";
    private static final String RESOURCE_PROPERTY = String.format(ConditionHolder.TEST_CATEGORY_PROPERTY, RESOURCE_NAME);

    private ConditionHolder condition;

    @BeforeEach
    public void setup() {
        condition = new ConditionHolder(RESOURCE_NAME);
    }

    @Test
    public void shouldBeEnabledByDefault() {
        assertTrue(condition.isEnabled());
    }

    @Test
    public void shouldBeDisabledIfSystemPropertyDoesNotExist() {
        System.clearProperty(RESOURCE_PROPERTY);
        condition.enableConditional();
        assertFalse(condition.isEnabled());
    }

    @Test
    public void shouldBeDisabledIfSystemPropertyIsNotTrue() {
        System.setProperty(RESOURCE_PROPERTY, "anything");
        condition.enableConditional();
        assertFalse(condition.isEnabled());
    }

    @Test
    public void shouldBeEnabledIfSystemPropertyIsTrue() {
        System.setProperty(RESOURCE_PROPERTY, "true");
        condition.enableConditional();
        assertTrue(condition.isEnabled());
    }
}
