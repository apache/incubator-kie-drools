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

    private Milestone(String id, String name, Status status) {
        super(id, name, status);
    }

    @Override
    public String toString() {
        return "Milestone{" + super.toString() + "}";
    }

    public static class Builder {

        private String id;
        private String name;
        private Status status;

        public Builder(String id) {
            this.id = id;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withStatus(Status status) {
            this.status = status;
            return this;
        }

        public Milestone build() {
            return new Milestone(id, name, status);
        }
    }

}
