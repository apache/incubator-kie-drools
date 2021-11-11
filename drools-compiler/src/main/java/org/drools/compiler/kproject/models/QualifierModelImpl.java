/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.kproject.models;

import java.util.HashMap;
import java.util.Map;

import org.kie.api.builder.model.QualifierModel;

public class QualifierModelImpl implements QualifierModel {
    private String type;
    private String value;
    private Map<String, String> arguments = new HashMap<String, String>();

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
