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
