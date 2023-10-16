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
package org.kie.kogito.persistence.postgresql.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.kie.kogito.persistence.postgresql.hibernate.JsonBinaryType;

import com.fasterxml.jackson.databind.node.ObjectNode;

@Entity
@IdClass(CacheId.class)
@Table(name = "kogito_data_cache", uniqueConstraints = @UniqueConstraint(columnNames = { "name",
        "key" }), indexes = @Index(columnList = "name,key", unique = true))
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class CacheEntity {

    @Id
    @Column(nullable = false)
    private String name;

    @Id
    @Column(nullable = false)
    private String key;

    @Type(type = "jsonb")
    @Column(name = "json_value", columnDefinition = "jsonb")
    private ObjectNode value;

    public CacheEntity() {
    }

    public CacheEntity(String name, String key) {
        this.name = name;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ObjectNode getValue() {
        return value;
    }

    public void setValue(ObjectNode value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CacheEntity)) {
            return false;
        }
        CacheEntity that = (CacheEntity) o;
        return getName().equals(that.getName()) && getKey().equals(that.getKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getKey());
    }

    @Override
    public String toString() {
        return "CacheEntity{" +
                "name='" + name + '\'' +
                ", key='" + key + '\'' +
                ", value=" + value +
                '}';
    }
}
