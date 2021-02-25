/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.serverless.workflow;

import org.jbpm.process.core.Context;
import org.jbpm.workflow.core.NodeContainer;
import org.kie.api.definition.process.Node;

public class TestNodeContainer implements NodeContainer {
    @Override
    public void addNode(Node node) {

    }

    @Override
    public void removeNode(Node node) {
    }

    @Override
    public Context resolveContext(String contextId, Object param) {
        return null;
    }

    @Override
    public Node internalGetNode(long id) {
        return null;
    }

    @Override
    public Node[] getNodes() {
        return new Node[0];
    }

    @Override
    public Node getNode(long id) {
        return null;
    }

    @Override
    public Node getNodeByUniqueId(String nodeId) {
        return null;
    }
}
