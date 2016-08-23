/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.phreak;

import org.drools.core.base.ClassFieldAccessorStore;
import org.drools.core.base.ClassObjectType;
import org.drools.core.common.BetaConstraints;
import org.drools.core.common.EmptyBetaConstraints;
import org.drools.core.common.SingleBetaConstraints;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.ExistsNode;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.NodeTypeEnums;
import org.drools.core.reteoo.NotNode;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.reteoo.builder.NodeFactory;
import org.drools.core.reteoo.test.dsl.ReteTesterHelper;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.Pattern;
import org.drools.core.spi.InternalReadAccessor;

public class BetaNodeBuilder {
    BuildContext buildContext;
    int          nodeType;
    Class        leftType;
    Class        rightType;
    String       leftFieldName;
    String       leftVariableName;
    String       constraintFieldName;
    String       constraintOperator;
    String       constraintVariableName;

    public BetaNodeBuilder(int nodeType, BuildContext buildContext) {
        this.nodeType = nodeType;
        this.buildContext = buildContext;
    }

    public static BetaNodeBuilder create(int nodeType, BuildContext buildContext) {
        return new BetaNodeBuilder(nodeType, buildContext);
    }

    public BetaNodeBuilder setLeftType(Class type) {
        this.leftType = type;
        return this;
    }

    public BetaNodeBuilder setRightType(Class type) {
        this.rightType = type;
        return this;
    }

    public BetaNodeBuilder setBinding(String leftFieldName,
                                      String leftVariableName) {
        this.leftFieldName = leftFieldName;
        this.leftVariableName = leftVariableName;
        return this;
    }

    public BetaNodeBuilder setConstraint(String constraintFieldName,
                                         String constraintOperator,
                                         String constraintVariableName) {
        this.constraintFieldName = constraintFieldName;
        this.constraintOperator = constraintOperator;
        this.constraintVariableName = constraintVariableName;
        return this;
    }

    public BetaNode build() {
        NodeFactory nFactory = buildContext.getComponentFactory().getNodeFactoryService();

        EntryPointNode epn = buildContext.getKnowledgeBase().getRete().getEntryPointNodes().values().iterator().next();

        ObjectTypeNode otn = nFactory.buildObjectTypeNode(buildContext.getNextId(),
                                                          epn,
                                                          new ClassObjectType(leftType),
                                                          buildContext);

        LeftInputAdapterNode leftInput = nFactory.buildLeftInputAdapterNode(buildContext.getNextId(),
                                                                            otn,
                                                                            buildContext);

        ObjectSource rightInput = nFactory.buildObjectTypeNode(buildContext.getNextId(),
                                                               epn,
                                                               new ClassObjectType(rightType),
                                                               buildContext);

        ReteTesterHelper reteTesterHelper = new ReteTesterHelper();

        Pattern pattern = new Pattern(0, new ClassObjectType(leftType));

        //BetaNodeFieldConstraint betaConstraint = null;
        BetaConstraints betaConstraints = null;
        if (constraintFieldName != null) {
            ClassFieldAccessorStore store = (ClassFieldAccessorStore) reteTesterHelper.getStore();

            InternalReadAccessor extractor = store.getReader(leftType,
                                                             leftFieldName);

            Declaration declr = new Declaration(leftVariableName,
                                                extractor,
                                                pattern);
                betaConstraints = new SingleBetaConstraints(reteTesterHelper.getBoundVariableConstraint(rightType,
                                                                                                        constraintFieldName,
                                                                                                        declr,
                                                                                                        constraintOperator), buildContext.getKnowledgeBase().getConfiguration());
        } else {
            betaConstraints = new EmptyBetaConstraints();
        }

        switch (nodeType) {
            case NodeTypeEnums.JoinNode:
                return new JoinNode(0, leftInput, rightInput, betaConstraints, buildContext);
            case NodeTypeEnums.NotNode:
                return new NotNode(0, leftInput, rightInput, betaConstraints, buildContext);
            case NodeTypeEnums.ExistsNode:
                return new ExistsNode(0, leftInput, rightInput, betaConstraints, buildContext);
        }
        throw new IllegalStateException("Unable to build Node");
    }

}
