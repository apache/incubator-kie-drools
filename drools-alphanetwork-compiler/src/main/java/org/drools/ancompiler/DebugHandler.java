/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.ancompiler;

import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.WindowNode;
import org.drools.core.rule.IndexableConstraint;
import org.drools.core.util.index.AlphaRangeIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DebugHandler extends NetworkHandlerAdaptor {

    private final Logger logger = LoggerFactory.getLogger(DebugHandler.class);

    private String formatString(Object node) {
        return String.format("%s", node);
    }

    @Override
    public void startObjectTypeNode(ObjectTypeNode objectTypeNode) {
        logger.debug(formatString(objectTypeNode));
    }

    @Override
    public void startNonHashedAlphaNode(AlphaNode alphaNode) {
        logger.debug(formatString(alphaNode));
    }

    @Override
    public void endNonHashedAlphaNode(AlphaNode alphaNode) {
        logger.debug(formatString(alphaNode));
    }

    @Override
    public void startBetaNode(BetaNode betaNode) {
        logger.debug(formatString(betaNode));
    }

    @Override
    public void endBetaNode(BetaNode betaNode) {
        logger.debug(formatString(betaNode));
    }

    @Override
    public void startWindowNode(WindowNode windowNode) {
        logger.debug(formatString(windowNode));
    }

    @Override
    public void endWindowNode(WindowNode windowNode) {
        logger.debug(formatString(windowNode));
    }

    @Override
    public void startLeftInputAdapterNode(Object parent, LeftInputAdapterNode leftInputAdapterNode) {
        logger.debug(formatString(leftInputAdapterNode));
    }

    @Override
    public void endWindowNode(LeftInputAdapterNode leftInputAdapterNode) {
        logger.debug(formatString(leftInputAdapterNode));
    }

    @Override
    public void startHashedAlphaNodes(IndexableConstraint hashedFieldReader) {
        logger.debug(formatString(hashedFieldReader));
    }

    @Override
    public void endHashedAlphaNodes(IndexableConstraint hashedFieldReader) {
        logger.debug(formatString(hashedFieldReader));
    }

    @Override
    public void startHashedAlphaNode(AlphaNode hashedAlpha, Object hashedValue) {
        logger.debug(formatString(hashedAlpha));
    }

    @Override
    public void endHashedAlphaNode(AlphaNode hashedAlpha, Object hashedValue) {
        logger.debug(formatString(hashedAlpha));
    }

    @Override
    public void endObjectTypeNode(ObjectTypeNode objectTypeNode) {
        logger.debug(formatString(objectTypeNode));
    }

    @Override
    public void nullCaseAlphaNodeStart(AlphaNode hashedAlpha) {
        logger.debug(formatString(hashedAlpha));
    }

    @Override
    public void nullCaseAlphaNodeEnd(AlphaNode hashedAlpha) {
        logger.debug(formatString(hashedAlpha));
    }

    @Override
    public void startRangeIndex(AlphaRangeIndex alphaRangeIndex) {
        logger.debug(formatString(alphaRangeIndex));
    }

    @Override
    public void endRangeIndex(AlphaRangeIndex alphaRangeIndex) {
        logger.debug(formatString(alphaRangeIndex));
    }

    @Override
    public void startRangeIndexedAlphaNode(AlphaNode alphaNode) {
        logger.debug(formatString(alphaNode));
    }

    @Override
    public void endRangeIndexedAlphaNode(AlphaNode alphaNode) {
        logger.debug(formatString(alphaNode));
    }
}
