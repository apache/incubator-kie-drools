/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.index.model;

import javax.json.bind.annotation.JsonbTypeAdapter;

import org.kie.kogito.index.json.JsonStringTypeAdapter;

public class UserTaskInstance extends UserTaskInstanceMeta {

    private String processId;
    private String rootProcessId;
    private String rootProcessInstanceId;

    @JsonbTypeAdapter(JsonStringTypeAdapter.class)
    private String inputs;

    @JsonbTypeAdapter(JsonStringTypeAdapter.class)
    private String outputs;

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String id) {
        if (id != null && !id.trim().isEmpty()) {
            this.processId = id;
        }
    }

    public String getRootProcessId() {
        return rootProcessId;
    }

    public void setRootProcessId(String id) {
        if (id != null && !id.trim().isEmpty()) {
            this.rootProcessId = id;
        }
    }

    @Override
    public String toString() {
        return "UserTaskInstance{" +
                "processId='" + processId + '\'' +
                ", rootProcessId='" + rootProcessId + '\'' +
                ", rootProcessInstanceId='" + rootProcessInstanceId + '\'' +
                ", inputs='" + inputs + '\'' +
                ", outputs='" + outputs + '\'' +
                "} " + super.toString();
    }

    public String getRootProcessInstanceId() {
        return rootProcessInstanceId;
    }

    public void setRootProcessInstanceId(String id) {
        if (id != null && id.trim().isEmpty() == false) {
            this.rootProcessInstanceId = id;
        }
    }

    public String getInputs() {
        return inputs;
    }

    public void setInputs(String inputs) {
        this.inputs = inputs;
    }

    public String getOutputs() {
        return outputs;
    }

    public void setOutputs(String outputs) {
        this.outputs = outputs;
    }
}
