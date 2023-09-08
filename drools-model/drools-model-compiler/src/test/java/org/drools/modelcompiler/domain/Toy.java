/**
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
package org.drools.modelcompiler.domain;

import org.drools.core.phreak.AbstractReactiveObject;

public class Toy extends AbstractReactiveObject {

    private String name;

    private String owner;

    private Integer targetAge;

    public Toy(String name) {
        this.name = name;
    }

    public Toy(String name, Integer targetAge) {
        this.name = name;
        this.targetAge = targetAge;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
        notifyModification();
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Integer getTargetAge() {
        return targetAge;
    }

    public void setTargetAge(Integer targetAge) {
        this.targetAge = targetAge;
    }

    @Override
    public String toString() {
        return "Toy: " + name;
    }
}

