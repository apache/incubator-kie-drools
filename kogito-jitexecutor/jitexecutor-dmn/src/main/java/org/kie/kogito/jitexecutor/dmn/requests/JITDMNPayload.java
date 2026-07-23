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
package org.kie.kogito.jitexecutor.dmn.requests;

import java.util.List;
import java.util.Map;

import org.kie.kogito.jitexecutor.common.requests.MultipleResourcesPayload;
import org.kie.kogito.jitexecutor.common.requests.ResourceWithURI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JITDMNPayload extends MultipleResourcesPayload {

    private String model;
    private Map<String, Object> context;

    public JITDMNPayload() {
    }

    public JITDMNPayload(String model, Map<String, Object> context) {
        this.model = model;
        this.context = context;
    }

    public JITDMNPayload(String mainURI, List<ResourceWithURI> resources, Map<String, Object> context) {
        super(mainURI, resources);
        this.context = context;
    }

    @Override
    public List<ResourceWithURI> getResources() {
        consistencyChecks();
        return super.getResources();
    }

    public String getModel() {
        consistencyChecks();
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    @Override
    public String toString() {
        return "JITDMNPayload{" +
                "model='" + model + '\'' +
                ", context=" + context +
                "} " + super.toString();
    }

    private void consistencyChecks() {
        if (model != null && getMainURI() != null && getResources() != null && !getResources().isEmpty()) {
            throw new IllegalStateException("JITDMNPayload should not contain both (main) model and resources collection");
        }
    }
}
