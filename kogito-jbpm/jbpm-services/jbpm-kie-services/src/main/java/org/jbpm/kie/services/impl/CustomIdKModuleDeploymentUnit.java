/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.kie.services.impl;


public class CustomIdKModuleDeploymentUnit extends KModuleDeploymentUnit {

    private static final long serialVersionUID = -987852949210762267L;
    
    private String id;

    public CustomIdKModuleDeploymentUnit(String id, String groupId, String artifactId, String version) {
        super(groupId, artifactId, version);
        this.id = id;
    }

    public CustomIdKModuleDeploymentUnit(String id, String groupId, String artifactId, String version, String kbaseName, String ksessionName) {
        super(groupId, artifactId, version, kbaseName, ksessionName);
        this.id = id;
    }

    @Override
    public String getIdentifier() {
        return this.id;
    }
}
