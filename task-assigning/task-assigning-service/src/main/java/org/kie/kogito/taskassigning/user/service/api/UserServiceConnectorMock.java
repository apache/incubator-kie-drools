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

package org.kie.kogito.taskassigning.user.service.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

/**
 * TODO: Upcoming iteration removes this implementation in favor of real implementation.
 */
@ApplicationScoped
public class UserServiceConnectorMock implements UserServiceConnector {

    private static class UserMock implements User {

        private String id;
        Set<Group> groups;
        Map<String, Object> attributes = new HashMap<>();

        public UserMock(String id, Set<Group> groups) {
            this.id = id;
            this.groups = groups;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public Set<Group> getGroups() {
            return groups;
        }

        @Override
        public Map<String, Object> getAttributes() {
            return attributes;
        }
    }

    private static class GroupMock implements Group {

        private String id;

        public GroupMock(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return id;
        }
    }

    private Map<String, User> mockedUsers = new HashMap<>();

    public UserServiceConnectorMock() {
        if (System.getProperty("kieServerDataset") != null) {
            buildKieServerDataset();
        } else {
            buildKogitoDataset();
        }
    }

    void buildKogitoDataset() {
        mockedUsers.put("john", new UserMock("john",
                new HashSet<>(Arrays.asList(new GroupMock("employees")))));
        mockedUsers.put("mary", new UserMock("mary",
                new HashSet<>(Arrays.asList(new GroupMock("managers")))));
        mockedUsers.put("poul", new UserMock("poul",
                new HashSet<>(Arrays.asList(new GroupMock("interns"),
                        new GroupMock("managers")))));

    }

    void buildKieServerDataset() {
        mockedUsers.put("krisv", new UserMock("krisv",
                new HashSet<>(Arrays.asList(new GroupMock("admin"),
                        new GroupMock("analyst"),
                        new GroupMock("user")))));
        mockedUsers.put("john", new UserMock("john",
                new HashSet<>(Arrays.asList(new GroupMock("analyst"),
                        new GroupMock("Accounting"),
                        new GroupMock("PM")))));
        mockedUsers.put("mary", new UserMock("mary",
                new HashSet<>(Arrays.asList(new GroupMock("analyst"),
                        new GroupMock("HR")))));
        mockedUsers.put("sales-rep", new UserMock("sales-rep",
                new HashSet<>(Arrays.asList(new GroupMock("analyst"),
                        new GroupMock("sales")))));
        mockedUsers.put("jack", new UserMock("jack",
                new HashSet<>(Arrays.asList(new GroupMock("analyst"),
                        new GroupMock("IT")))));
        mockedUsers.put("katy", new UserMock("katy",
                new HashSet<>(Arrays.asList(new GroupMock("analyst"),
                        new GroupMock("HR")))));
        mockedUsers.put("salaboy", new UserMock("salaboy",
                new HashSet<>(Arrays.asList(new GroupMock("admin"),
                        new GroupMock("analyst"),
                        new GroupMock("IT"),
                        new GroupMock("HR"),
                        new GroupMock("Accounting")))));
        mockedUsers.put("maciek", new UserMock("maciek",
                new HashSet<>(Arrays.asList(new GroupMock("admin"),
                        new GroupMock("analyst"),
                        new GroupMock("user"),
                        new GroupMock("PM"),
                        new GroupMock("HR")))));

    }

    public List<User> findAllUsers() {
        return new ArrayList<>(mockedUsers.values());
    }

    public User findUser(String id) {
        return mockedUsers.get(id);
    }
}
