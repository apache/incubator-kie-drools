/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.process.impl;

import java.util.Map;

import org.kie.kogito.process.WorkItem;

public class BaseWorkItem implements WorkItem {

    private final String id;
    private final String name;
    
    private Map<String, Object> parameters;

    public BaseWorkItem(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public BaseWorkItem(String id, String name, Map<String, Object> parameters) {
        this.id = id;
        this.name = name;
        this.parameters = parameters;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }    

    @Override
    public Map<String, Object> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return "WorkItem [id=" + id + ", name=" + name + "]";
    }


}
