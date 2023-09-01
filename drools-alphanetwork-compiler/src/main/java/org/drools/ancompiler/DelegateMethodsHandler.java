package org.drools.ancompiler;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.PropagationContext;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.ModifyPreviousTuples;
import org.drools.core.reteoo.ObjectTypeNode;

public class DelegateMethodsHandler extends AbstractCompilerHandler {

    private final StringBuilder builder;

    private static final String FIXED_PART = "" +

            "\n" +
            "    public short getType() {\n" +
            "        return objectTypeNode.getType();\n" +
            "    }\n" +
            "\n" +
            "\n" +
            "    public boolean isAssociatedWith( org.kie.api.definition.rule.Rule rule ) {\n" +
            "        return objectTypeNode.isAssociatedWith(rule);\n" +
            "    }\n " +
            "\n" +
            "    public void byPassModifyToBetaNode (" + InternalFactHandle.class.getCanonicalName() + " factHandle,\n" +
            "                                        " + ModifyPreviousTuples.class.getCanonicalName() + " modifyPreviousTuples,\n" +
            "                                        " + PropagationContext.class.getCanonicalName() + " context,\n" +
            "                                        " + ReteEvaluator.class.getCanonicalName() + " reteEvaluator) {\n" +
            "        throw new UnsupportedOperationException();\n" +
            "    }\n" +
            "\n";


    AlphaNode alphaNode;

    public DelegateMethodsHandler(StringBuilder builder) {
        this.builder = builder;
    }

    @Override
    public void startObjectTypeNode(ObjectTypeNode objectTypeNode) {
        builder.append(FIXED_PART);
    }

    @Override
    public void startLeftInputAdapterNode(LeftInputAdapterNode leftInputAdapterNode) {
    }

    @Override
    public void startNonHashedAlphaNode(AlphaNode alphaNode) {
        this.alphaNode = alphaNode;

    }

    @Override
    public void endObjectTypeNode(ObjectTypeNode objectTypeNode) {
    }

    @Override
    public void startBetaNode(BetaNode betaNode) {
    }
}
