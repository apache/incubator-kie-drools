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
package org.kie.kogito.jitexecutor.common.requests;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MultipleResourcesPayload {

    @JsonProperty("mainURI")
    private String mainURI;
    @JsonProperty("resources")
    private List<ResourceWithURI> resources;
    @JsonProperty("isStrictMode")
    private boolean isStrictMode;

    public MultipleResourcesPayload() {
    }

    public MultipleResourcesPayload(String mainURI, List<ResourceWithURI> resources) {
        this(mainURI, resources, false);
    }

    public MultipleResourcesPayload(String mainURI, List<ResourceWithURI> resources, boolean isStrictMode) {
        this.mainURI = mainURI;
        this.resources = resources;
        this.isStrictMode = isStrictMode;
    }

    public String getMainURI() {
        return mainURI;
    }

    public void setMainURI(String mainURI) {
        this.mainURI = mainURI;
    }

    public List<ResourceWithURI> getResources() {
        return resources;
    }

    public void setResources(List<ResourceWithURI> resources) {
        this.resources = resources;
    }

    public boolean isStrictMode() {
        return isStrictMode;
    }

    public void setStrictMode(boolean strictMode) {
        isStrictMode = strictMode;
    }

    @Override
    public String toString() {
        return "MultipleResourcesPayload{" +
                "mainURI='" + mainURI + '\'' +
                ", resources=" + resources +
                ", isStrictMode =" + isStrictMode +
                '}';
    }
}
