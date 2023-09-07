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

package org.optaplanner.operator.impl.solver.model.keda;

import io.fabric8.kubernetes.api.model.SecretKeySelector;

public final class SecretTargetRef {

    public static SecretTargetRef fromSecretKeySelector(String parameter, SecretKeySelector secretKeySelector) {
        return new SecretTargetRef(parameter, secretKeySelector.getName(), secretKeySelector.getKey());
    }

    private String parameter;
    private String name;
    private String key;

    public SecretTargetRef() {
        // Required by Jackson.
    }

    public SecretTargetRef(String parameter, String name, String key) {
        this.parameter = parameter;
        this.name = name;
        this.key = key;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
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
}
