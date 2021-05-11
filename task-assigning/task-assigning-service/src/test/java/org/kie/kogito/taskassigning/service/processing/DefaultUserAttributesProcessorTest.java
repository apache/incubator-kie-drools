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
import org.kie.kogito.taskassigning.user.service.User;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class DefaultUserAttributesProcessorTest extends AbstractDefaultAttributesProcessorTest<User> {

    @Override
    protected AbstractDefaultAttributesProcessor<User> createProcessor() {
        DefaultUserAttributesProcessor result = new DefaultUserAttributesProcessor();
        result.skillsAttribute = SKILLS_ATTRIBUTE;
        result.affinitiesAttribute = AFFINITIES_ATTRIBUTE;
        result.priority = PRIORITY;
        result.enabled = ENABLED;
        return result;
    }

    @Override
    protected Stream<Arguments> createProcessTestParams() {
        return Stream.of(
                Arguments.of(mockUser("skill1,skill2", "affinity1,affinity2"),
                        new HashSet<>(Arrays.asList("skill1", "skill2")),
                        new HashSet<>(Arrays.asList("affinity1", "affinity2"))),
                Arguments.of(mockUser(null, "affinity1,affinity2"),
                        null,
                        new HashSet<>(Arrays.asList("affinity1", "affinity2"))),
                Arguments.of(mockUser("skill1,skill2", null),
                        new HashSet<>(Arrays.asList("skill1", "skill2")),
                        null),
                Arguments.of(mockUser(null, null),
                        null,
                        null));
    }

    @Override
    protected Stream<Arguments> createGetSkillsTestParams() {
        return Stream.of(
                Arguments.of(mockUserWithSkills("skill1,skill2"), "skill1,skill2"),
                Arguments.of(mockUserWithSkills(null), null));
    }

    @Override
    protected Stream<Arguments> createGetAffinitiesTestParams() {
        return Stream.of(
                Arguments.of(mockUserWithAffinities("affinity1,affinity2"), "affinity1,affinity2"),
                Arguments.of(mockUserWithAffinities(null), null));
    }

    @Override
    protected int getExpectedPriority() {
        return PRIORITY;
    }

    @Override
    protected boolean getExpectedIsEnabled() {
        return ENABLED;
    }

    private static User mockUserWithSkills(String skillsStr) {
        return mockUser(skillsStr, null);
    }

    private static User mockUserWithAffinities(String affinitiesStr) {
        return mockUser(null, affinitiesStr);
    }

    private static User mockUser(String skillsStr, String affinitiesStr) {
        User user = mock(User.class);
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(SKILLS_ATTRIBUTE, skillsStr);
        attributes.put(AFFINITIES_ATTRIBUTE, affinitiesStr);
        doReturn(attributes).when(user).getAttributes();
        return user;
    }
}
