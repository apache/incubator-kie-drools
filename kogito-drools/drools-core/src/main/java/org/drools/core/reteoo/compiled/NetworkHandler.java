/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.reteoo.compiled;

import org.drools.core.base.ClassFieldReader;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.ObjectTypeNode;

/**
 * Receive notification of the logical parts of the RETE-OO network.
 * todo: finish documenting 
 */
public interface NetworkHandler {

    /**
     * Receive notification of the beginning of an {@link org.kie.reteoo.ObjectTypeNode}
     *
     * <p>The Network parser will invoke this method only once, before any other event callback.</p>
     *
     * @param objectTypeNode the object type node
     * @see #endObjectTypeNode(org.kie.reteoo.ObjectTypeNode)
     */
    void startObjectTypeNode(ObjectTypeNode objectTypeNode);

    /**
     * Receive notification of the a non-hashed {@link org.kie.reteoo.AlphaNode}.
     *
     * <p>The Parser will invoke this method at the beginning of every non-hashed Alpha in the Network;
     * there will be a corresponding endNonHashedAlphaNode() event for every startNonHashedAlphaNode() event.
     * All of the node's decendants will be reported, in order, before the corresponding endNonHashedAlphaNode()
     * event.</p>
     *
     * @param alphaNode non-hashed AlphaNode
     * @see #endNonHashedAlphaNode
     */
    void startNonHashedAlphaNode(AlphaNode alphaNode);

    /**
     * Receive notification of the end of a non-hashed {@link org.kie.reteoo.AlphaNode}.
     *
     * <p>The parser will invoke this method at the end of every alpha in the network; there will be a corresponding
     * {@link #startNonHashedAlphaNode(org.kie.reteoo.AlphaNode)} event for every endNonHashedAlphaNode event.</p>
     *
     * @param alphaNode non-hashed AlphaNode
     */
    void endNonHashedAlphaNode(AlphaNode alphaNode);

    void startBetaNode(BetaNode betaNode);

    void endBetaNode(BetaNode betaNode);

    void startLeftInputAdapterNode(LeftInputAdapterNode leftInputAdapterNode);

    void endLeftInputAdapterNode(LeftInputAdapterNode leftInputAdapterNode);

    /**
     * Receive notification of the a group of hashed {@link org.kie.reteoo.AlphaNode}s.
     *
     * <p>The Parser will invoke this method at the beginning of every groups of hashed Alphas in the Network;
     * there will be a corresponding {@link #endHashedAlphaNodes} event for every startHashedAlphaNodes() event.
     *
     * The actual alpha nodes will be reported via the {@link #startHashedAlphaNode} method, along with all of the
     * node's decendants, in order, before the corresponding {@link #endHashedAlphaNode}
     * event.</p>
     *
     * @param hashedFieldReader field reader that is used to access the hashed attribute
     * @see #endHashedAlphaNodes
     * @see #startHashedAlphaNode
     */
    void startHashedAlphaNodes(ClassFieldReader hashedFieldReader);

    void endHashedAlphaNodes(ClassFieldReader hashedFieldReader);

    void startHashedAlphaNode(AlphaNode hashedAlpha, Object hashedValue);

    void endHashedAlphaNode(AlphaNode hashedAlpha, Object hashedValue);

    void endObjectTypeNode(ObjectTypeNode objectTypeNode);
}
