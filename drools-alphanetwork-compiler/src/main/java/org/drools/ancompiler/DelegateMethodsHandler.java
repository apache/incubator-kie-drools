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
            "    public int getType() {\n" +
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
