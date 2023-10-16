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
package org.kie.kogito.persistence.redis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Person {

    public static final String NAME_PROPERTY = "name";
    public static final String AGE_PROPERTY = "age";
    public static final String BIOGRAPHY_PROPERTY = "biography";

    @JsonProperty(NAME_PROPERTY)
    public String name;

    @JsonProperty(AGE_PROPERTY)
    public Integer age;

    @JsonProperty(BIOGRAPHY_PROPERTY)
    public String biography;

    public Person() {
    }

    public Person(String name, Integer age) {
        this(name, age, "");
    }

    public Person(String name, Integer age, String biography) {
        this.name = name;
        this.age = age;
        this.biography = biography;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    public String getBiography() {
        return biography;
    }
}
