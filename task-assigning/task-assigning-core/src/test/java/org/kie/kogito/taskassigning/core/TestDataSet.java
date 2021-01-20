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

package org.kie.kogito.taskassigning.core;

public enum TestDataSet {

    SET_OF_24TASKS_8USERS_SOLUTION("/data/unsolved/24tasks-8users.xml"),
    SET_OF_50TASKS_5USERS_SOLUTION("/data/unsolved/50tasks-5users.xml"),
    SET_OF_100TASKS_5USERS_SOLUTION("/data/unsolved/100tasks-5users.xml"),
    SET_OF_500TASKS_20USERS_SOLUTION("/data/unsolved/500tasks-20users.xml");

    private String resource;

    TestDataSet(String resource) {
        this.resource = resource;
    }

    public String resource() {
        return resource;
    }
}
