/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.index.model;

import java.time.ZonedDateTime;

public class Comment {

    private String id;
    private String content;
    private ZonedDateTime updatedAt;
    private String updatedBy;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Comment)) {
            return false;
        }

        Comment that = (Comment) o;

        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public String toString() {
        return "Comment {" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Comment comment;

        private Builder() {
            comment = new Comment();
        }

        public Builder id(String id) {
            comment.setId(id);
            return this;
        }

        public Builder content(String content) {
            comment.setContent(content);
            return this;
        }

        public Builder updatedAt(ZonedDateTime updatedAt) {
            comment.setUpdatedAt(updatedAt);
            return this;
        }

        public Builder updatedBy(String updatedBy) {
            comment.setUpdatedBy(updatedBy);
            return this;
        }

        public Comment build() {
            return comment;
        }
    }
}
