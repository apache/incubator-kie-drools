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

import org.drools.core.common.NetworkNode;
import org.drools.core.reteoo.*;

/**
 * This handler is used to create the member variable assignment statements section of a generated subclass of a
 * {@link CompiledNetwork#setNetworkNodeReference(org.kie.common.NetworkNode)}.
 * Currently we only need to create member variable assignments for the following types of nodes:
 * <p/>
 * <li>Non-hashed {@link AlphaNode}s</li>
 * <li>{@link LeftInputAdapterNode}s</li>
 * <li>{@link BetaNode}s</li>
 */
public class SetNodeReferenceHandler extends AbstractCompilerHandler {

    private static final String PARAM_TYPE = NetworkNode.class.getName();
    private static final String PARAM_NAME = "node";
    private static final String SET_NETWORK_NODE_REFERENCE_SIGNATURE = "protected void setNetworkNodeReference("
            + PARAM_TYPE + " " + PARAM_NAME + "){";

    private final StringBuilder builder;

    public SetNodeReferenceHandler(StringBuilder builder) {
        this.builder = builder;
    }

    private String getVariableAssignmentStatement(Sink sink, String nodeVariableName) {
        Class<?> variableType = getVariableType(sink);
        String assignmentStatement;

        // for non alphas, we just need to cast to the right variable type
        assignmentStatement = getVariableName(sink) + " = (" + variableType.getName() + ")" + nodeVariableName + ";";

        return assignmentStatement;
    }

    private String getVariableAssignmentStatement(AlphaNode alphaNode, String nodeVariableName) {
        Class<?> variableType = getVariableType(alphaNode);
        String assignmentStatement;

        // we need the constraint for an alpha node assignment, so generate a cast, plus the method call to get
        // the constraint
        assignmentStatement = getVariableName(alphaNode) + " = (" + variableType.getName() + ") ((" + AlphaNode.class.getName() + ")" + nodeVariableName + ").getConstraint();";

        return assignmentStatement;
    }

    @Override
    public void startObjectTypeNode(ObjectTypeNode objectTypeNode) {
        builder.append(SET_NETWORK_NODE_REFERENCE_SIGNATURE).append(NEWLINE);

        // we are switch based on the parameter's node ID
        builder.append("switch (").append(PARAM_NAME).append(".getId()) {").append(NEWLINE);
    }

    @Override
    public void endObjectTypeNode(ObjectTypeNode objectTypeNode) {
        // close the switch
        builder.append("}").append(NEWLINE);
        // and close the setNetworkNodeReference method
        builder.append("}").append(NEWLINE);
    }

    @Override
    public void startNonHashedAlphaNode(AlphaNode alphaNode) {
        // case statement for the non-hashed alphas looks like the following
        // case 24:
        //      variableConstraint24 = (VariableConstraint) ((AlphaNode) node).getConstraint();
        //      break;

        builder.append("case ").append(alphaNode.getId()).append(": ").append(NEWLINE);
        builder.append(getVariableAssignmentStatement(alphaNode, PARAM_NAME)).append(NEWLINE);
        builder.append("break;").append(NEWLINE);
    }

    @Override
    public void startBetaNode(BetaNode betaNode) {
        // case statement for the betas looks like the following
        // case 65:
        //      notNode65 = (NodeNode) node;
        //      break;

        builder.append("case ").append(betaNode.getId()).append(": ").append(NEWLINE);
        builder.append(getVariableAssignmentStatement(betaNode, PARAM_NAME)).append(NEWLINE);
        builder.append("break;").append(NEWLINE);
    }

    @Override
    public void startWindowNode(WindowNode windowNode) {
        // case statement for the window looks like the following
        // case 65:
        //      notNode65 = (NodeNode) node;
        //      break;

        builder.append("case ").append(windowNode.getId()).append(": ").append(NEWLINE);
        builder.append(getVariableAssignmentStatement(windowNode, PARAM_NAME)).append(NEWLINE);
        builder.append("break;").append(NEWLINE);
    }

    @Override
    public void startLeftInputAdapterNode(LeftInputAdapterNode leftInputAdapterNode) {
        // case statement for the lias looks like the following
        // case 5:
        //      leftInputAdapterNode5 = (LeftInputAdapterNode) node;
        //      break;
        builder.append("case ").append(leftInputAdapterNode.getId()).append(": ").append(NEWLINE);
        builder.append(getVariableAssignmentStatement(leftInputAdapterNode, PARAM_NAME)).append(NEWLINE);
        builder.append("break;").append(NEWLINE);
    }
}
