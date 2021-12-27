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
package org.kie.kogito.incubation.processes.services.contexts;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class Policy {
    public static Policy of(String user, List<String> groups) {
        return new Policy(user, groups);
    }

    @JsonProperty
    private String user;
    @JsonProperty
    private List<String> groups;

    protected Policy() {
    }

    protected Policy(String user, List<String> groups) {
        this.user = user;
        this.groups = groups;
    }

    public String user() {
        return user;
    }

    void setUser(String user) {
        this.user = user;
    }

    public List<String> groups() {
        return groups;
    }

    void setGroups(List<String> groups) {
        this.groups = groups;
    }

}
