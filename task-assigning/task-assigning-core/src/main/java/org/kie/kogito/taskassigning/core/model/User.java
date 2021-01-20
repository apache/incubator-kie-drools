/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.taskassigning.core.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class User extends ChainElement implements OrganizationalEntity {

    private boolean enabled;
    private Set<Group> groups = new HashSet<>();
    private Map<String, Object> attributes = new HashMap<>();

    public User() {
        // required for marshaling and FieldAccessingSolutionCloner purposes.
    }

    public User(String id) {
        super(id);
    }

    public User(String id, boolean enabled) {
        super(id);
        this.enabled = enabled;
    }

    public User(String id, boolean enabled, Set<Group> groups, Map<String, Object> attributes) {
        super(id);
        this.enabled = enabled;
        this.groups = groups != null ? groups : new HashSet<>();
        this.attributes = attributes != null ? attributes : new HashMap<>();
    }

    @Override
    public boolean isTaskAssignment() {
        return false;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups != null ? groups : new HashSet<>();
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes != null ? attributes : new HashMap<>();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isUser() {
        return true;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", enabled=" + enabled +
                ", groups=" + groups +
                ", attributes=" + attributes +
                '}';
    }
}
