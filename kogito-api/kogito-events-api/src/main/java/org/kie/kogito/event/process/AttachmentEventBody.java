/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.event.process;

import java.net.URI;
import java.util.Date;
import java.util.Objects;

public class AttachmentEventBody {

    private String id;
    private String name;
    private URI content;
    private Date updatedAt;
    private String updatedBy;

    private AttachmentEventBody() {
    }

    public static Builder create() {
        return new Builder(new AttachmentEventBody());
    }

    public URI getContent() {
        return content;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "AttachmentEventBody{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", content=" + content +
                ", updatedAt=" + updatedAt +
                ", updatedBy='" + updatedBy + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AttachmentEventBody that = (AttachmentEventBody) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(content, that.content) && Objects.equals(updatedAt, that.updatedAt)
                && Objects.equals(updatedBy, that.updatedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, content, updatedAt, updatedBy);
    }

    public Builder update() {
        return new Builder(this);
    }

    public static class Builder {

        private AttachmentEventBody instance;

        private Builder(AttachmentEventBody instance) {
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

        public Builder content(URI content) {
            instance.content = content;
            return this;
        }

        public Builder updatedBy(String updatedBy) {
            instance.updatedBy = updatedBy;
            return this;
        }

        public Builder updatedAt(Date updatedAt) {
            instance.updatedAt = updatedAt;
            return this;
        }

        public AttachmentEventBody build() {
            return instance;
        }
    }
}
