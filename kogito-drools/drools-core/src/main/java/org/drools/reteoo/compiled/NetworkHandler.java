package org.drools.reteoo.compiled;

import org.drools.base.ClassFieldReader;
import org.drools.reteoo.AlphaNode;
import org.drools.reteoo.BetaNode;
import org.drools.reteoo.LeftInputAdapterNode;
import org.drools.reteoo.ObjectTypeNode;

/**
 * Receive notification of the logical parts of the RETE-OO network.
 * todo: finish documenting 
 * @author <a href="mailto:stampy88@yahoo.com">dave sinclair</a>
 */
public interface NetworkHandler {

    /**
     * Receive notification of the beginning of an {@link org.drools.reteoo.ObjectTypeNode}
     *
     * <p>The Network parser will invoke this method only once, before any other event callback.</p>
     *
     * @param objectTypeNode the object type node
     * @see #endObjectTypeNode(org.drools.reteoo.ObjectTypeNode)
     */
    void startObjectTypeNode(ObjectTypeNode objectTypeNode);

    /**
     * Receive notification of the a non-hashed {@link org.drools.reteoo.AlphaNode}.
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
     * Receive notification of the end of a non-hashed {@link org.drools.reteoo.AlphaNode}.
     *
     * <p>The parser will invoke this method at the end of every alpha in the network; there will be a corresponding
     * {@link #startNonHashedAlphaNode(org.drools.reteoo.AlphaNode)} event for every endNonHashedAlphaNode event.</p>
     *
     * @param alphaNode non-hashed AlphaNode
     */
    void endNonHashedAlphaNode(AlphaNode alphaNode);

    void startBetaNode(BetaNode betaNode);

    void endBetaNode(BetaNode betaNode);

    void startLeftInputAdapterNode(LeftInputAdapterNode leftInputAdapterNode);

    void endLeftInputAdapterNode(LeftInputAdapterNode leftInputAdapterNode);

    /**
     * Receive notification of the a group of hashed {@link org.drools.reteoo.AlphaNode}s.
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
