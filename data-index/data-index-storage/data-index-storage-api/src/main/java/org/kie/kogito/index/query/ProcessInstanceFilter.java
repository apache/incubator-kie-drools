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

package org.kie.kogito.index.query;

import java.util.List;

public class ProcessInstanceFilter extends AbstractFilter {

    private List<Integer> state;
    private List<String> id;
    private List<String> parentProcessInstanceId;
    private List<String> rootProcessInstanceId;
    private List<String> processId;

    public List<Integer> getState() {
        return state;
    }

    public void setState(List<Integer> state) {
        this.state = state;
    }

    public List<String> getId() {
        return id;
    }

    public void setId(List<String> id) {
        this.id = id;
    }

    public List<String> getProcessId() {
        return processId;
    }

    public void setProcessId(List<String> processId) {
        this.processId = processId;
    }

    public List<String> getParentProcessInstanceId() {
        return parentProcessInstanceId;
    }

    public void setParentProcessInstanceId(List<String> parentProcessInstanceId) {
        this.parentProcessInstanceId = parentProcessInstanceId;
    }

    public List<String> getRootProcessInstanceId() {
        return rootProcessInstanceId;
    }

    public void setRootProcessInstanceId(List<String> rootProcessInstanceId) {
        this.rootProcessInstanceId = rootProcessInstanceId;
    }

    @Override
    public String toString() {
        return "ProcessInstanceFilter{" +
                "state=" + state +
                ", id=" + id +
                ", parentProcessInstanceId=" + parentProcessInstanceId +
                ", rootProcessInstanceId=" + rootProcessInstanceId +
                ", processId=" + processId +
                "} " + super.toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private ProcessInstanceFilter filter;

        private Builder() {
            filter = new ProcessInstanceFilter();
        }

        public Builder limit(Integer limit) {
            filter.setLimit(limit);
            return this;
        }

        public Builder offset(Integer offset) {
            filter.setOffset(offset);
            return this;
        }

        public Builder state(List<Integer> state) {
            filter.setState(state);
            return this;
        }

        public Builder id(List<String> id) {
            filter.setId(id);
            return this;
        }

        public Builder processId(List<String> processId) {
            filter.setProcessId(processId);
            return this;
        }

        public Builder parentProcessInstanceId(List<String> parentProcessInstanceId) {
            filter.setParentProcessInstanceId(parentProcessInstanceId);
            return this;
        }

        public Builder rootProcessInstanceId(List<String> rootProcessInstanceId) {
            filter.setRootProcessInstanceId(rootProcessInstanceId);
            return this;
        }

        public ProcessInstanceFilter build() {
            return filter;
        }
    }
}
