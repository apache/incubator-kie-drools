/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.process.flexible;

public class Milestone extends ItemDescription {

    private final String condition;

    private Milestone(String id, String name, Status status, String condition) {
        super(id, name, status);
        this.condition = condition;
    }

    public String getCondition() {
        return condition;
    }

    @Override
    public String toString() {
        return "Milestone{" +
                "condition='" + condition + '\'' +
                ", {" + super.toString() + "}";
    }

    public static class Builder {

        private String id;
        private String name;
        private Status status;
        private String condition;

        public Builder(String id) {
            this.id = id;
        }

        public Builder(Milestone m) {
            this.id = m.getId();
            this.name = m.getName();
            this.status = m.getStatus();
            this.condition = m.getCondition();
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withStatus(Status status) {
            this.status = status;
            return this;
        }

        public Builder withCondition(String condition) {
            this.condition = condition;
            return this;
        }

        public Milestone build() {
            return new Milestone(id, name, status, condition);
        }
    }
}
