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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;
import org.kie.kogito.taskassigning.model.processing.TaskInfo;
import org.mockito.Mockito;

import static org.mockito.Mockito.doReturn;

public class DefaultTaskAttributesProcessorTest extends AbstractDefaultAttributesProcessorTest<TaskInfo> {

    @Override
    protected AbstractDefaultAttributesProcessor<TaskInfo> createProcessor() {
        DefaultTaskAttributesProcessor result = new DefaultTaskAttributesProcessor();
        result.skillsAttribute = SKILLS_ATTRIBUTE;
        result.affinitiesAttribute = AFFINITIES_ATTRIBUTE;
        result.priority = PRIORITY;
        result.enabled = ENABLED;
        return result;
    }

    @Override
    protected Stream<Arguments> createProcessTestParams() {
        return Stream.of(
                Arguments.of(mockTaskInfo("skill1,skill2", "affinity1,affinity2"),
                        new HashSet<>(Arrays.asList("skill1", "skill2")),
                        new HashSet<>(Arrays.asList("affinity1", "affinity2"))),
                Arguments.of(mockTaskInfo(null, "affinity1,affinity2"),
                        null,
                        new HashSet<>(Arrays.asList("affinity1", "affinity2"))),
                Arguments.of(mockTaskInfo("skill1,skill2", null),
                        new HashSet<>(Arrays.asList("skill1", "skill2")),
                        null),
                Arguments.of(mockTaskInfo(null, null),
                        null,
                        null));
    }

    @Override
    protected Stream<Arguments> createGetSkillsTestParams() {
        return Stream.of(
                Arguments.of(mockTaskInfoWithSkills("skill1,skill2"), "skill1,skill2"),
                Arguments.of(mockTaskInfoWithSkills(null), null));
    }

    @Override
    protected Stream<Arguments> createGetAffinitiesTestParams() {
        return Stream.of(
                Arguments.of(mockTaskInfoWithAffinities("affinity1,affinity2"), "affinity1,affinity2"),
                Arguments.of(mockTaskInfoWithAffinities(null), null));
    }

    @Override
    protected int getExpectedPriority() {
        return PRIORITY;
    }

    @Override
    protected boolean getExpectedIsEnabled() {
        return ENABLED;
    }

    private static TaskInfo mockTaskInfoWithSkills(String skillsStr) {
        return mockTaskInfo(skillsStr, null);
    }

    private static TaskInfo mockTaskInfoWithAffinities(String affinitiesStr) {
        return mockTaskInfo(null, affinitiesStr);
    }

    private static TaskInfo mockTaskInfo(String skillsStr, String affinitiesStr) {
        TaskInfo taskInfo = Mockito.mock(TaskInfo.class);
        Map<String, Object> inputs = new HashMap<>();
        inputs.put(SKILLS_ATTRIBUTE, skillsStr);
        inputs.put(AFFINITIES_ATTRIBUTE, affinitiesStr);
        doReturn(inputs).when(taskInfo).getInputs();
        return taskInfo;
    }
}
