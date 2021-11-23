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

package org.jbpm.bpmn2.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Collaboration implements Serializable {

    private static final long serialVersionUID = 8676065262126603722L;
    private String id;
    private String name;
    private List<String> processesRef;

    private List<CorrelationKey> correlationKeys;

    public Collaboration() {
        correlationKeys = new ArrayList<>();
        processesRef = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addCorrelationKey(CorrelationKey key) {
        this.correlationKeys.add(key);
    }

    public List<CorrelationKey> getCorrelationKeys() {
        return this.correlationKeys;
    }

    public List<String> getProcessesRef() {
        return processesRef;
    }

    public void setProcessesRef(List<String> processesRef) {
        this.processesRef = processesRef;
    }
}