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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.microprofile.config.Config;

public class DefLinesReader {

    private static final String CONNECTOR_CONFIG_PREFIX = "kogito.task-assigning.properties-connector.";

    private static final String USER_DEF_PREFIX = "kogito.task-assigning.properties-connector.user.";

    private static final String USER_DEF_PREFIX_REGEX = "(kogito\\.task-assigning\\.properties-connector\\.user\\.)";

    private static final String USER_NAME_REGEX = "([^.\\s]+)";

    private static final String GROUPS_REGEX = "(\\.groups)";

    private static final String USER_GROUP_DEF_REGEX = USER_DEF_PREFIX_REGEX + USER_NAME_REGEX + GROUPS_REGEX;

    private static final String USER_ATTRIBUTE_NAME_REGEX = "([^.\\s]+)";

    private static final String ATTRIBUTE_REGEX = "(\\.attribute\\.)";

    private static final String USER_ATTRIBUTE_DEF_REGEX = USER_DEF_PREFIX_REGEX + USER_NAME_REGEX + ATTRIBUTE_REGEX + USER_ATTRIBUTE_NAME_REGEX;

    private static final Pattern GROUP_DEF_PATTERN = Pattern.compile(USER_GROUP_DEF_REGEX);

    private static final Pattern ATTRIBUTE_DEF_PATTERN = Pattern.compile(USER_ATTRIBUTE_DEF_REGEX);

    private static final String UNRECOGNIZED_PROPERTY = "The configured property name: %s, is not" +
            " a configuration property supported by the PropertiesConnector. Valid examples are: " +
            " " + USER_DEF_PREFIX + "myUserName.groups=group1,group2" + " or " + USER_DEF_PREFIX + "myUserName.attribute.myAttributeName=someValue";

    public abstract static class DefLine {
        protected String userName;
        protected String value;

        protected DefLine(String userName, String value) {
            this.userName = userName;
            this.value = value;
        }

        public String getUserName() {
            return userName;
        }

        public String getValue() {
            return value;
        }
    }

    public static class GroupsDefLine extends DefLine {
        public GroupsDefLine(String userName, String groups) {
            super(userName, groups);
        }

        @Override
        public String toString() {
            return "GroupsDefLine{" +
                    "userName='" + userName + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    public static class AttributeDefLine extends DefLine {
        private String attributeName;

        public AttributeDefLine(String userName, String attributeName, String value) {
            super(userName, value);
            this.attributeName = attributeName;
        }

        public String getAttributeName() {
            return attributeName;
        }

        @Override
        public String toString() {
            return "AttributeDefLine{" +
                    "userName='" + userName + '\'' +
                    ", value='" + value + '\'' +
                    ", attributeName='" + attributeName + '\'' +
                    '}';
        }
    }

    private DefLinesReader() {
    }

    /**
     * Reads the configuration properties that makes sense to the UserServicePropertiesConnector, i.e., properties that
     * defines users with their corresponding groups and attributes, for example:
     *
     * kogito.task-assigning.properties-connector.user.katy.groups=managers,hr
     * kogito.task-assigning.properties-connector.user.katy.attribute.skills=c++,java
     *
     * and returns the corresponding DefLines for processing.
     *
     * @param config a configuration from where to scan the properties and values.
     * @return a list of definitions that makes sense to the UserServicePropertiesConnector.
     */
    public static Map<String, List<DefLine>> readDefLines(Config config) {
        Map<String, List<DefLine>> result = new HashMap<>();

        for (String propertyName : config.getPropertyNames()) {
            if (propertyName.startsWith(CONNECTOR_CONFIG_PREFIX)) {
                String propertyValue = config.getOptionalValue(propertyName, String.class).orElse(null);
                if (GROUP_DEF_PATTERN.matcher(propertyName).matches()) {
                    GroupsDefLine groupsDefLine = extractGroupsDefLine(propertyName, propertyValue);
                    result.computeIfAbsent(groupsDefLine.getUserName(), p -> new ArrayList<>())
                            .add(groupsDefLine);
                } else if (ATTRIBUTE_DEF_PATTERN.matcher(propertyName).matches()) {
                    AttributeDefLine attributeDefLine = extractAttributeDefLine(propertyName, propertyValue);
                    result.computeIfAbsent(attributeDefLine.getUserName(), p -> new ArrayList<>())
                            .add(attributeDefLine);
                } else {
                    throw new IllegalArgumentException(String.format(UNRECOGNIZED_PROPERTY, propertyName));
                }
            }
        }
        return result;
    }

    private static GroupsDefLine extractGroupsDefLine(String propertyName, String value) {
        //split the propertyName using the USER_DEF_PREFIX -> ["", "john.groups"]
        String[] split = propertyName.split(USER_DEF_PREFIX_REGEX);
        //split the userName+groups using the groups delimiter -> ["john"]
        String[] subSplit = split[1].split(GROUPS_REGEX);
        //first value is the userName -> "john"
        String userName = subSplit[0];
        return new GroupsDefLine(userName, value);
    }

    private static AttributeDefLine extractAttributeDefLine(String propertyName, String value) {
        //split the propertyName using the USER_DEF_PREFIX -> ["", "john.attribute.skills"]
        String[] split = propertyName.split(USER_DEF_PREFIX_REGEX);
        //split the userName+attribute+attributeName using the ATTRIBUTE_REGEX -> ["john", "skills"]
        String[] subSplit = split[1].split(ATTRIBUTE_REGEX);
        //first value is the userName -> "john"
        String userName = subSplit[0];
        //second value is the attributeName -> "skills"
        String attributeName = subSplit[1];
        return new AttributeDefLine(userName, attributeName, value);
    }
}