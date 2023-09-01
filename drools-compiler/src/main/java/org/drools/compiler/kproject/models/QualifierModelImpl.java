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
package org.drools.compiler.kproject.models;

import java.util.HashMap;
import java.util.Map;

import org.kie.api.builder.model.QualifierModel;

public class QualifierModelImpl implements QualifierModel {
    private String type;
    private String value;
    private Map<String, String> arguments = new HashMap<>();

    public QualifierModelImpl() { }

    public QualifierModelImpl(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public QualifierModel addArgument(String key, String value) {
        arguments.put(key, value);
        return this;
    }

    public Map<String, String> getArguments() {
        return arguments;
    }

    boolean isSimple() {
        return value == null && arguments.isEmpty();
    }
}
