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

public class UserTaskInstanceFilter extends AbstractFilter {

    private List<String> state;
    private List<String> id;
    private List<String> processInstanceId;
    private List<String> actualOwner;
    private List<String> potentialUsers;
    private List<String> potentialGroups;

    public List<String> getActualOwner() {
        return actualOwner;
    }

    public void setActualOwner(List<String> actualOwner) {
        this.actualOwner = actualOwner;
    }

    public List<String> getPotentialUsers() {
        return potentialUsers;
    }

    public void setPotentialUsers(List<String> potentialUsers) {
        this.potentialUsers = potentialUsers;
    }

    public List<String> getPotentialGroups() {
        return potentialGroups;
    }

    public void setPotentialGroups(List<String> potentialGroups) {
        this.potentialGroups = potentialGroups;
    }

    public List<String> getState() {
        return state;
    }

    public void setState(List<String> state) {
        this.state = state;
    }

    public List<String> getId() {
        return id;
    }

    public void setId(List<String> id) {
        this.id = id;
    }

    public List<String> getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(List<String> processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public static Builder builder() {
        return new Builder();
    }
    
    @Override
    public String toString() {
        return "UserTaskInstanceFilter{" +
                "state=" + state +
                ", id=" + id +
                ", processInstanceId=" + processInstanceId +
                ", actualOwner=" + actualOwner +
                ", potentialUsers=" + potentialUsers +
                ", potentialGroups=" + potentialGroups +
                "} " + super.toString();
    }

    public static final class Builder {

        private UserTaskInstanceFilter filter;

        private Builder() {
            filter = new UserTaskInstanceFilter();
        }

        public Builder limit(Integer limit) {
            filter.setLimit(limit);
            return this;
        }

        public Builder offset(Integer offset) {
            filter.setOffset(offset);
            return this;
        }

        public Builder state(List<String> state) {
            filter.setState(state);
            return this;
        }

        public Builder id(List<String> id) {
            filter.setId(id);
            return this;
        }

        public Builder processInstanceId(List<String> processInstanceId) {
            filter.setProcessInstanceId(processInstanceId);
            return this;
        }

        public Builder actualOwner(List<String> actualOwner) {
            filter.setActualOwner(actualOwner);
            return this;
        }

        public Builder potentialUsers(List<String> potentialUsers) {
            filter.setPotentialUsers(potentialUsers);
            return this;
        }

        public Builder potentialGroups(List<String> potentialGroups) {
            filter.setPotentialGroups(potentialGroups);
            return this;
        }

        public UserTaskInstanceFilter build() {
            return filter;
        }
    }
}
