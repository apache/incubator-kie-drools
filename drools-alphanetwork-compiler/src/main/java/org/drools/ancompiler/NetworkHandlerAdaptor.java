package org.drools.ancompiler;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.NameExpr;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.WindowNode;
import org.drools.base.rule.IndexableConstraint;
import org.drools.core.util.index.AlphaRangeIndex;


/**
 * An abstract adapter class for receiving network node events from the {@link org.kie.reteoo.compiled.ObjectTypeNodeParser}.
 * The methods in this class are empty. This class exists as convenience for creating handler objects.
 * <P>
 * Extend this class to create a <code>NetworkHandler</code> and override the methods for the nodes of interest.
 * (If you implement the {@link NetworkHandler} interface, you have to define all of the methods in it. This
 * abstract class defines null methods for them all, so you can only have to define methods for events you care about.)
 * <P>
 * @see org.kie.reteoo.compiled.NetworkHandler
 * @see org.kie.reteoo.compiled.ObjectTypeNodeParser
 */
public class NetworkHandlerAdaptor implements NetworkHandler {
    public void startObjectTypeNode(ObjectTypeNode objectTypeNode) {
        // do nothing
    }

    public void startNonHashedAlphaNode(AlphaNode alphaNode) {
        // do nothing
    }

    public void endNonHashedAlphaNode(AlphaNode alphaNode) {
        // do nothing
    }

    public void startBetaNode(BetaNode betaNode) {
        // do nothing
    }

    public void endBetaNode(BetaNode betaNode) {
        // do nothing
    }

    public void startWindowNode(WindowNode windowNode) {
        // do nothing
    }

    public void endWindowNode(WindowNode windowNode) {
        // do nothing
    }

    public void startLeftInputAdapterNode(LeftInputAdapterNode leftInputAdapterNode) {
        // do nothing
    }

    public void endWindowNode(LeftInputAdapterNode leftInputAdapterNode) {
        // do nothing
    }

    public void startHashedAlphaNodes(IndexableConstraint hashedFieldReader) {
        // do nothing
    }

    public void endHashedAlphaNodes(IndexableConstraint hashedFieldReader) {
        // do nothing
    }

    public void startHashedAlphaNode(AlphaNode hashedAlpha, Object hashedValue) {
        // do nothing
    }

    public void endHashedAlphaNode(AlphaNode hashedAlpha, Object hashedValue) {
        // do nothing
    }

    public void endObjectTypeNode(ObjectTypeNode objectTypeNode) {
        // do nothing
    }

    public void nullCaseAlphaNodeStart(AlphaNode hashedAlpha) {
        // do nothing
    }

    @Override
    public void nullCaseAlphaNodeEnd(AlphaNode hashedAlpha) {
        // do nothing
    }

    @Override
    public void startRangeIndex(AlphaRangeIndex alphaRangeIndex) {
        // do nothing
    }

    @Override
    public void endRangeIndex(AlphaRangeIndex alphaRangeIndex) {
        // do nothing
    }

    @Override
    public void startRangeIndexedAlphaNode(AlphaNode alphaNode) {
        // do nothing
    }

    @Override
    public void endRangeIndexedAlphaNode(AlphaNode alphaNode) {
        // do nothing
    }

    protected void replaceNameExpr(Node expression, String from, String to) {
        expression.findAll(NameExpr.class, n -> from.equals(n.toString())).forEach(c -> c.replace(new NameExpr(to)));
    }

}
