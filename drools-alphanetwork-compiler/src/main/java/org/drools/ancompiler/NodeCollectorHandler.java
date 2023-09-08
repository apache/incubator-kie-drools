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
package org.drools.ancompiler;

import java.util.ArrayList;
import java.util.List;

import org.drools.base.common.NetworkNode;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.WindowNode;

public class NodeCollectorHandler extends AbstractCompilerHandler {

    private List<NetworkNode> nodes = new ArrayList<>();

    public NodeCollectorHandler() {
    }

    @Override
    public void startObjectTypeNode(ObjectTypeNode objectTypeNode) {

    }

    @Override
    public void endObjectTypeNode(ObjectTypeNode objectTypeNode) {
    }

    @Override
    public void startNonHashedAlphaNode(AlphaNode alphaNode) {
        nodes.add(alphaNode);
    }

    @Override
    public void startBetaNode(BetaNode betaNode) {
        nodes.add(betaNode);
    }

    @Override
    public void startWindowNode(WindowNode windowNode) {
        nodes.add(windowNode);
    }

    @Override
    public void startLeftInputAdapterNode(LeftInputAdapterNode leftInputAdapterNode) {
        nodes.add(leftInputAdapterNode);
    }

    public List<NetworkNode> getNodes() {
        return nodes;
    }
}
