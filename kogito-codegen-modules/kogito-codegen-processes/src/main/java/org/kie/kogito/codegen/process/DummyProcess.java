/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.kie.kogito.codegen.process;

import java.util.List;
import java.util.Map;

import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.WorkflowElementIdentifier;
import org.kie.api.io.Resource;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcess;

/**
 * Class used as "placeholder" in case there are no processes in the application but
 * kie-addons-quarkus-source-files is present.
 * Temporary hack for incubator-kie-issues#2060
 */
public class DummyProcess implements KogitoWorkflowProcess {

    @Override
    public String getVisibility() {
        return "";
    }

    @Override
    public List<Node> getNodesRecursively() {
        return List.of();
    }

    @Override
    public Node[] getNodes() {
        return new Node[0];
    }

    @Override
    public Node getNode(WorkflowElementIdentifier workflowElementIdentifier) {
        return null;
    }

    @Override
    public Node getNodeByUniqueId(String s) {
        return null;
    }

    @Override
    public KnowledgeType getKnowledgeType() {
        return null;
    }

    @Override
    public String getNamespace() {
        return "dummy-namespace";
    }

    @Override
    public String getId() {
        return "dummy-id";
    }

    @Override
    public String getName() {
        return "dummy-name";
    }

    @Override
    public String getVersion() {
        return "dummy-version";
    }

    @Override
    public String getPackageName() {
        return "dummy-package-name";
    }

    @Override
    public String getType() {
        return "dummy-type";
    }

    @Override
    public Map<String, Object> getMetaData() {
        return Map.of();
    }

    @Override
    public Resource getResource() {
        return null;
    }

    @Override
    public void setResource(Resource resource) {

    }
}
