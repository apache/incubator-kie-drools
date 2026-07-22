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
package org.kie.kogito.usertask.model;

import java.io.Serializable;
import java.util.Date;

public class UserTaskEntity<K extends Serializable, T extends Serializable> implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    private K id;
    protected T content;
    protected Date updatedAt;
    protected String updatedBy;

    public UserTaskEntity() {
    }

    public UserTaskEntity(K id, String user) {
        this.id = id;
        this.updatedBy = user;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setId(K id) {
        this.id = id;
    }

    public K getId() {
        return id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        return id.equals(((UserTaskEntity<K, T>) obj).id);
    }

    @Override
    public String toString() {
        return "id=" + id + ", content=" + content + ", updatedAt=" + updatedAt + ", updatedBy=" +
                updatedBy;
    }

}
