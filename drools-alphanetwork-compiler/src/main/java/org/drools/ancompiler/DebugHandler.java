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

import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.WindowNode;
import org.drools.base.rule.IndexableConstraint;
import org.drools.core.util.index.AlphaRangeIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugHandler extends NetworkHandlerAdaptor {

    private final Logger logger = LoggerFactory.getLogger(DebugHandler.class);

    private void printNode(Object node) {
        if (logger.isDebugEnabled()) {
            logger.debug(node.toString());
        }
    }

    @Override
    public void startObjectTypeNode(ObjectTypeNode objectTypeNode) {
        printNode(objectTypeNode);
    }

    @Override
    public void startNonHashedAlphaNode(AlphaNode alphaNode) {
        printNode(alphaNode);
    }

    @Override
    public void endNonHashedAlphaNode(AlphaNode alphaNode) {
        printNode(alphaNode);
    }

    @Override
    public void startBetaNode(BetaNode betaNode) {
        printNode(betaNode);
    }

    @Override
    public void endBetaNode(BetaNode betaNode) {
        printNode(betaNode);
    }

    @Override
    public void startWindowNode(WindowNode windowNode) {
        printNode(windowNode);
    }

    @Override
    public void endWindowNode(WindowNode windowNode) {
        printNode(windowNode);
    }

    @Override
    public void startLeftInputAdapterNode(LeftInputAdapterNode leftInputAdapterNode) {
        printNode(leftInputAdapterNode);
    }

    @Override
    public void endWindowNode(LeftInputAdapterNode leftInputAdapterNode) {
        printNode(leftInputAdapterNode);
    }

    @Override
    public void startHashedAlphaNodes(IndexableConstraint hashedFieldReader) {
        printNode(hashedFieldReader);
    }

    @Override
    public void endHashedAlphaNodes(IndexableConstraint hashedFieldReader) {
        printNode(hashedFieldReader);
    }

    @Override
    public void startHashedAlphaNode(AlphaNode hashedAlpha, Object hashedValue) {
        printNode(hashedAlpha);
    }

    @Override
    public void endHashedAlphaNode(AlphaNode hashedAlpha, Object hashedValue) {
        printNode(hashedAlpha);
    }

    @Override
    public void endObjectTypeNode(ObjectTypeNode objectTypeNode) {
        printNode(objectTypeNode);
    }

    @Override
    public void nullCaseAlphaNodeStart(AlphaNode hashedAlpha) {
        printNode(hashedAlpha);
    }

    @Override
    public void nullCaseAlphaNodeEnd(AlphaNode hashedAlpha) {
        printNode(hashedAlpha);
    }

    @Override
    public void startRangeIndex(AlphaRangeIndex alphaRangeIndex) {
        printNode(alphaRangeIndex);
    }

    @Override
    public void endRangeIndex(AlphaRangeIndex alphaRangeIndex) {
        printNode(alphaRangeIndex);
    }

    @Override
    public void startRangeIndexedAlphaNode(AlphaNode alphaNode) {
        printNode(alphaNode);
    }

    @Override
    public void endRangeIndexedAlphaNode(AlphaNode alphaNode) {
        printNode(alphaNode);
    }
}
