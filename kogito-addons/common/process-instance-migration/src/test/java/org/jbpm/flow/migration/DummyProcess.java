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
package org.jbpm.flow.migration;

import java.util.Map;

import org.kie.api.io.Resource;

public class DummyProcess implements org.kie.api.definition.process.Process {

    private String id;
    private String version;

    public DummyProcess(String id, String version) {
        this.id = id;
        this.version = version;
    }

    @Override
    public KnowledgeType getKnowledgeType() {
        return null;
    }

    @Override
    public String getNamespace() {
        return null;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getPackageName() {
        return null;
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public Map<String, Object> getMetaData() {
        return null;
    }

    @Override
    public Resource getResource() {
        return null;
    }

    @Override
    public void setResource(Resource res) {
    }

}