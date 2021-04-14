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

package org.kie.kogito.taskassigning.user.service.properties;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.smallrye.config.PropertiesConfigSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class DefLinesReaderTest {

    private static final String CONNECTOR_PREFIX = "kogito.task-assigning.properties-connector.";

    static final String SKILLS_ATTRIBUTE = "skills";
    static final String AFFINITIES_ATTRIBUTE = "affinities";
    static final String NAME_ATTRIBUTE = "name";
    static final String SURNAME_ATTRIBUTE = "surname";
    static final String ZIPCODE_ATTRIBUTE = "zipCode";

    static final String USER1 = "user1";
    static final String USER1_GROUPS_VALUE = "user1_group1,  user1_group2,   user1_group3";
    static final String USER1_SKILLS_VALUE = "user1_skills_value";
    static final String USER1_AFFINITIES_VALUE = "user1_affinities_value";
    static final String USER1_NAME_VALUE = "user1_name_value";
    static final String USER1_SURNAME_VALUE = "user1_surname_value";

    static final String USER2 = "user2";
    static final String USER2_GROUPS_VALUE = "user2_group1";
    static final String USER2_SKILLS_VALUE = "user2_skills_value";
    static final String USER2_SURNAME_VALUE = "user2_surname_value";
    static final String USER2_ZIPCODE_VALUE = "user2_zipCode_value";

    static final String USER3 = "user3";
    static final String USER3_NAME_VALUE = "user3_name_value";

    static final String USER4 = "user4";
    static final String USER4_GROUPS_VALUE = "user4_group1,user4_group2";

    static final String USER5 = "user5";
    static final String USER6 = "user6";

    @Test
    void readDefLines() throws IOException {
        Config config = readTestConfig();

        List<DefLinesReader.DefLine> user1ExpectedLines =
                Arrays.asList(new DefLinesReader.GroupsDefLine(USER1, USER1_GROUPS_VALUE),
                        new DefLinesReader.AttributeDefLine(USER1, SKILLS_ATTRIBUTE, USER1_SKILLS_VALUE),
                        new DefLinesReader.AttributeDefLine(USER1, AFFINITIES_ATTRIBUTE, USER1_AFFINITIES_VALUE),
                        new DefLinesReader.AttributeDefLine(USER1, NAME_ATTRIBUTE, USER1_NAME_VALUE),
                        new DefLinesReader.AttributeDefLine(USER1, SURNAME_ATTRIBUTE, USER1_SURNAME_VALUE));

        List<DefLinesReader.DefLine> user2ExpectedLines =
                Arrays.asList(new DefLinesReader.GroupsDefLine(USER2, USER2_GROUPS_VALUE),
                        new DefLinesReader.AttributeDefLine(USER2, SKILLS_ATTRIBUTE, USER2_SKILLS_VALUE),
                        new DefLinesReader.AttributeDefLine(USER2, SURNAME_ATTRIBUTE, USER2_SURNAME_VALUE),
                        new DefLinesReader.AttributeDefLine(USER2, ZIPCODE_ATTRIBUTE, USER2_ZIPCODE_VALUE));

        List<DefLinesReader.DefLine> user3ExpectedLines =
                Collections.singletonList(new DefLinesReader.AttributeDefLine(USER3, NAME_ATTRIBUTE, USER3_NAME_VALUE));

        List<DefLinesReader.DefLine> user4ExpectedLines =
                Collections.singletonList(new DefLinesReader.GroupsDefLine(USER4, USER4_GROUPS_VALUE));

        List<DefLinesReader.DefLine> user5ExpectedLines =
                Collections.singletonList(new DefLinesReader.GroupsDefLine(USER5, null));

        List<DefLinesReader.DefLine> user6ExpectedLines =
                Collections.singletonList(new DefLinesReader.AttributeDefLine(USER6, NAME_ATTRIBUTE, null));

        Map<String, List<DefLinesReader.DefLine>> linesByUser = DefLinesReader.readDefLines(config);

        assertUserLines(linesByUser.get(USER1), user1ExpectedLines);
        assertUserLines(linesByUser.get(USER2), user2ExpectedLines);
        assertUserLines(linesByUser.get(USER3), user3ExpectedLines);
        assertUserLines(linesByUser.get(USER4), user4ExpectedLines);
        assertUserLines(linesByUser.get(USER5), user5ExpectedLines);
        assertUserLines(linesByUser.get(USER6), user6ExpectedLines);
    }

    static Config readTestConfig() throws IOException {
        return ConfigProviderResolver.instance().getBuilder()
                .withSources(new PropertiesConfigSource(DefLinesReader.class.getResource("DefLinesReaderTest.properties")))
                .build();
    }

    @Test
    void attributeDefLine() {
        DefLinesReader.AttributeDefLine attributeDefLine = new DefLinesReader.AttributeDefLine(USER1, AFFINITIES_ATTRIBUTE, USER1_AFFINITIES_VALUE);
        assertThat(attributeDefLine.getUserName()).isEqualTo(USER1);
        assertThat(attributeDefLine.getAttributeName()).isEqualTo(AFFINITIES_ATTRIBUTE);
        assertThat(attributeDefLine.getValue()).isEqualTo(USER1_AFFINITIES_VALUE);
    }

    @Test
    void groupsDefLine() {
        DefLinesReader.GroupsDefLine groupsDefLine = new DefLinesReader.GroupsDefLine(USER1, USER1_GROUPS_VALUE);
        assertThat(groupsDefLine.getUserName()).isEqualTo(USER1);
        assertThat(groupsDefLine.getValue()).isEqualTo(USER1_GROUPS_VALUE);
    }

    @ParameterizedTest
    @MethodSource("readDefLinesWithErrorParams")
    void readDefLinesWithError(String line) {
        Config config = mock(Config.class);
        doReturn(Collections.singletonList(line)).when(config).getPropertyNames();
        Assertions.assertThatThrownBy(() -> DefLinesReader.readDefLines(config))
                .hasMessageStartingWith("The configured property name: %s,", line);
    }

    private static List<String> readDefLinesWithErrorParams() {
        return Arrays.asList(CONNECTOR_PREFIX + "user.userName.unRecognizedDefItem",
                CONNECTOR_PREFIX + "user Name.unRecognizedDefItem",
                CONNECTOR_PREFIX + "user.user.Name.unRecognizedDefItem",
                CONNECTOR_PREFIX + "user.userName",
                CONNECTOR_PREFIX + "user.",
                CONNECTOR_PREFIX + "unRecognizedItem",
                CONNECTOR_PREFIX);
    }

    private static void assertUserLines(List<DefLinesReader.DefLine> currentLines, List<DefLinesReader.DefLine> expectedLines) {
        assertThat(currentLines).hasSize(expectedLines.size());
        for (DefLinesReader.DefLine expectedLine : expectedLines) {
            if (expectedLine instanceof DefLinesReader.GroupsDefLine) {
                assertHasGroupsDefLine(currentLines, (DefLinesReader.GroupsDefLine) expectedLine);
            } else {
                assertHasAttributeDefLine(currentLines, (DefLinesReader.AttributeDefLine) expectedLine);
            }
        }
    }

    private static void assertHasGroupsDefLine(List<? extends DefLinesReader.DefLine> currentLines, DefLinesReader.GroupsDefLine expectedLine) {
        final Predicate<DefLinesReader.GroupsDefLine> predicate = currentLine -> Objects.equals(currentLine.getUserName(), expectedLine.getUserName()) &&
                Objects.equals(currentLine.getValue(), expectedLine.getValue());
        assertHasLine(currentLines, DefLinesReader.GroupsDefLine.class, predicate, expectedLine);
    }

    private static void assertHasAttributeDefLine(List<? extends DefLinesReader.DefLine> currentLines, DefLinesReader.AttributeDefLine expectedLine) {
        final Predicate<DefLinesReader.AttributeDefLine> predicate = currentLine -> Objects.equals(currentLine.getUserName(), expectedLine.getUserName()) &&
                Objects.equals(currentLine.getAttributeName(), expectedLine.getAttributeName()) &&
                Objects.equals(currentLine.getValue(), expectedLine.getValue());
        assertHasLine(currentLines, DefLinesReader.AttributeDefLine.class, predicate, expectedLine);
    }

    private static <T extends DefLinesReader.DefLine> void assertHasLine(List<? extends DefLinesReader.DefLine> currentLines,
            Class<T> clazz,
            Predicate<T> predicate,
            T expectedLine) {
        long count = filterLines(currentLines, clazz, predicate).count();
        assertThat(count)
                .withFailMessage("Exactly one instance of the expectedLine: %s " +
                        "must be present in the currentLines: %s, but there are: %s", expectedLine, currentLines, count)
                .isEqualTo(1);

    }

    private static <T extends DefLinesReader.DefLine> Stream<T> filterLines(List<? extends DefLinesReader.DefLine> currentLines,
            Class<T> clazz, Predicate<T> predicate) {
        return currentLines.stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .filter(predicate);
    }
}
