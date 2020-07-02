/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.services.event.impl;

import java.util.Objects;

public class MilestoneEventBody {

    private String id;
    private String name;
    private String status;

    private MilestoneEventBody() {
    }

    public static Builder create() {
        return new Builder(new MilestoneEventBody());
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "MilestoneEventBody{" +
                "name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MilestoneEventBody)) {
            return false;
        }
        MilestoneEventBody that = (MilestoneEventBody) o;
        return getId().equals(that.getId()) &&
                getName().equals(that.getName()) &&
                getStatus().equals(that.getStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getStatus());
    }

    public Builder update() {
        return new Builder(this);
    }

    public static class Builder {

        private MilestoneEventBody instance;

        private Builder(MilestoneEventBody instance) {
            this.instance = instance;
        }

        public Builder id(String id) {
            instance.id = id;
            return this;
        }

        public Builder name(String name) {
            instance.name = name;
            return this;
        }

        public Builder status(String status) {
            instance.status = status;
            return this;
        }

        public MilestoneEventBody build() {
            return instance;
        }
    }
}
