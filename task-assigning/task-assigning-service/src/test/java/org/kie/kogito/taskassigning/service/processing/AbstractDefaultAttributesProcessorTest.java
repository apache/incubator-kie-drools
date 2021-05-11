/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.taskassigning.service.processing;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.kogito.taskassigning.core.model.DefaultLabels;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractDefaultAttributesProcessorTest<T> {

    protected static final int PRIORITY = 1;
    protected static final boolean ENABLED = true;
    protected static final String SKILLS_ATTRIBUTE = "skills";
    protected static final String AFFINITIES_ATTRIBUTE = "affinities";

    protected AbstractDefaultAttributesProcessor<T> processor;

    protected abstract AbstractDefaultAttributesProcessor<T> createProcessor();

    @BeforeEach
    void setUp() {
        processor = createProcessor();
    }

    @ParameterizedTest
    @MethodSource("createProcessTestParams")
    void process(T entity, Set<String> expectedSkills, Set<String> expectedAffinities) {
        Map<String, Object> targetAttributes = new HashMap<>();
        processor.process(entity, targetAttributes);
        Object currentSkills = targetAttributes.get(DefaultLabels.SKILLS.name());
        assertThat(currentSkills).isEqualTo(expectedSkills);
        Object currentAffinities = targetAttributes.get(DefaultLabels.AFFINITIES.name());
        assertThat(currentAffinities).isEqualTo(expectedAffinities);
    }

    protected abstract Stream<Arguments> createProcessTestParams();

    @ParameterizedTest
    @MethodSource("createGetSkillsTestParams")
    void getSkills(T entity, String expectedSkillsValue) {
        Object currentSkillsValue = processor.getSkillsValue(entity);
        assertThat(currentSkillsValue).isEqualTo(expectedSkillsValue);
    }

    protected abstract Stream<Arguments> createGetSkillsTestParams();

    @ParameterizedTest
    @MethodSource("createGetAffinitiesTestParams")
    void getAffinities(T entity, String expectedAffinitiesValue) {
        Object currentAffinities = processor.getAffinitiesValue(entity);
        assertThat(currentAffinities).isEqualTo(expectedAffinitiesValue);
    }

    protected abstract Stream<Arguments> createGetAffinitiesTestParams();

    @Test
    void getPriority() {
        assertThat(processor.getPriority()).isEqualTo(getExpectedPriority());
    }

    protected abstract int getExpectedPriority();

    @Test
    void isEnabled() {
        assertThat(processor.isEnabled()).isEqualTo(getExpectedIsEnabled());
    }

    protected abstract boolean getExpectedIsEnabled();
}
