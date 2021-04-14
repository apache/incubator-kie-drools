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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.Config;
import org.kie.kogito.taskassigning.user.service.User;
import org.kie.kogito.taskassigning.user.service.UserServiceConnector;

/**
 * Simple user service connector implementation that reads the users definitions from a configuration.
 *
 * A user can be defined by declaring it's groups and attributes in the corresponding configuration source.
 * E.g. by adding the following lines in the application.properties file:
 *
 * kogito.task-assigning.properties-connector.user.user1.groups=managers,hr
 * kogito.task-assigning.properties-connector.user.user1.attribute.skills=c++,java
 * kogito.task-assigning.properties-connector.user.user1.attribute.name=User1Name
 * kogito.task-assigning.properties-connector.user.user1.attribute.arbitraryAttributeName=ArbitraryAttributeValue
 *
 * A user "user1" with the following information is created:
 * the userId -> "user1"
 * the groups -> "managers" and "hr" (two separate groups are configured)
 * and the attributes:
 * "skills" with value "c++,java" (the full string is configured as the attribute value)
 * "name" with value "User1Name" (the full string is configured as the attribute value)
 * "arbitraryAttributeName" with value "ArbitraryAttributeValue" (the full string is configured as the attribute value)
 *
 * @see Config
 */
@ApplicationScoped
public class UserServicePropertiesConnector implements UserServiceConnector {

    public static final String NAME = "PropertiesConnector";

    private final Map<String, User> users = new HashMap<>();

    private final Config config;

    @Inject
    public UserServicePropertiesConnector(Config config) {
        this.config = config;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void start() {
        Map<String, List<DefLinesReader.DefLine>> defLineByUser = DefLinesReader.readDefLines(config);
        for (Map.Entry<String, List<DefLinesReader.DefLine>> entry : defLineByUser.entrySet()) {
            User user = users.computeIfAbsent(entry.getKey(), userId -> new UserImpl(userId, new HashSet<>(), new HashMap<>()));
            for (DefLinesReader.DefLine userDefLine : entry.getValue()) {
                if (userDefLine instanceof DefLinesReader.GroupsDefLine) {
                    addGroups(user, (DefLinesReader.GroupsDefLine) userDefLine);
                } else {
                    addAttribute(user, (DefLinesReader.AttributeDefLine) userDefLine);
                }
            }
        }
    }

    private void addGroups(User user, DefLinesReader.GroupsDefLine groupsDefLine) {
        if (groupsDefLine != null && groupsDefLine.getValue() != null) {
            Stream.of(groupsDefLine.getValue().split(","))
                    .forEach(groupName -> {
                        if (!groupName.trim().isEmpty()) {
                            user.getGroups().add(new GroupImpl(groupName.trim()));
                        }
                    });
        }
    }

    private void addAttribute(User user, DefLinesReader.AttributeDefLine attributeDefLine) {
        user.getAttributes().put(attributeDefLine.getAttributeName(), attributeDefLine.getValue());
    }

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findUser(String id) {
        return users.get(id);
    }

}