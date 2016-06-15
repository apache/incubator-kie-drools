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


package org.drools.core.reteoo.builder;


import org.drools.core.common.BetaConstraints;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.AccumulateNode;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.ConditionalBranchEvaluator;
import org.drools.core.reteoo.ConditionalBranchNode;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.EvalConditionNode;
import org.drools.core.reteoo.ExistsNode;
import org.drools.core.reteoo.FromNode;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.NotNode;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.QueryElementNode;
import org.drools.core.reteoo.ReactiveFromNode;
import org.drools.core.reteoo.RightInputAdapterNode;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.reteoo.TimerNode;
import org.drools.core.reteoo.WindowNode;
import org.drools.core.rule.Accumulate;
import org.drools.core.rule.Behavior;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.EntryPointId;
import org.drools.core.rule.EvalCondition;
import org.drools.core.rule.From;
import org.drools.core.rule.GroupElement;
import org.drools.core.rule.QueryElement;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.DataProvider;
import org.drools.core.spi.ObjectType;
import org.drools.core.time.impl.Timer;

import java.util.List;

public interface NodeFactory {

    EntryPointNode buildEntryPointNode( int id,
                                        ObjectSource objectSource,
                                        BuildContext context);

    EntryPointNode buildEntryPointNode( int id,
                                        RuleBasePartitionId partitionId,
                                        boolean partitionsEnabled,
                                        ObjectSource objectSource,
                                        EntryPointId entryPoint);


    AlphaNode buildAlphaNode( int id,
                              AlphaNodeFieldConstraint constraint,
                              ObjectSource objectSource,
                              BuildContext context );

    TerminalNode buildTerminalNode( int id,
                                    LeftTupleSource source,
                                    RuleImpl rule,
                                    GroupElement subrule,
                                    int subruleIndex,
                                    BuildContext context );

    ObjectTypeNode buildObjectTypeNode( int id,
                                        EntryPointNode objectSource,
                                        ObjectType objectType,
                                        BuildContext context );

    EvalConditionNode buildEvalNode( int id,
                                     LeftTupleSource tupleSource,
                                     EvalCondition eval,
                                     BuildContext context);


    RightInputAdapterNode buildRightInputNode( int id,
                                               LeftTupleSource leftInput,
                                               LeftTupleSource startTupleSource,
                                               BuildContext context );

    JoinNode buildJoinNode( int id,
                            LeftTupleSource leftInput,
                            ObjectSource rightInput,
                            BetaConstraints binder,
                            BuildContext context );

    NotNode buildNotNode( int id,
                          LeftTupleSource leftInput,
                          ObjectSource rightInput,
                          BetaConstraints binder,
                          BuildContext context );

    ExistsNode buildExistsNode( int id,
                                LeftTupleSource leftInput,
                                ObjectSource rightInput,
                                BetaConstraints binder,
                                BuildContext context );

    AccumulateNode buildAccumulateNode( int id,
                                        LeftTupleSource leftInput,
                                        ObjectSource rightInput,
                                        AlphaNodeFieldConstraint[] resultConstraints,
                                        BetaConstraints sourceBinder,
                                        BetaConstraints resultBinder,
                                        Accumulate accumulate,
                                        boolean unwrapRightObject,
                                        BuildContext context );
    LeftInputAdapterNode buildLeftInputAdapterNode( int nextId,
                                                    ObjectSource objectSource,
                                                    BuildContext context );

    TerminalNode buildQueryTerminalNode( int id,
                                         LeftTupleSource source,
                                         RuleImpl rule,
                                         GroupElement subrule,
                                         int subruleIndex,
                                         BuildContext context );

    QueryElementNode buildQueryElementNode( int nextId,
                                            LeftTupleSource tupleSource,
                                            QueryElement qe,
                                            boolean tupleMemoryEnabled,
                                            boolean openQuery,
                                            BuildContext context );

    FromNode buildFromNode( int id,
                            DataProvider dataProvider,
                            LeftTupleSource tupleSource,
                            AlphaNodeFieldConstraint[] alphaNodeFieldConstraints,
                            BetaConstraints betaConstraints,
                            boolean tupleMemoryEnabled,
                            BuildContext context,
                            From from );

    ReactiveFromNode buildReactiveFromNode( int id,
                                            DataProvider dataProvider,
                                            LeftTupleSource tupleSource,
                                            AlphaNodeFieldConstraint[] alphaNodeFieldConstraints,
                                            BetaConstraints betaConstraints,
                                            boolean tupleMemoryEnabled,
                                            BuildContext context,
                                            From from );

    TimerNode buildTimerNode( int id,
                              Timer timer,
                              final String[] calendarNames,
                              final Declaration[][]   declarations,
                              LeftTupleSource tupleSource,
                              BuildContext context );

    ConditionalBranchNode buildConditionalBranchNode(int id,
                                                     LeftTupleSource tupleSource,
                                                     ConditionalBranchEvaluator branchEvaluator,
                                                     BuildContext context);

    WindowNode buildWindowNode(int id,
                               List<AlphaNodeFieldConstraint> constraints,
                               List<Behavior> behaviors,
                               ObjectSource objectSource,
                               BuildContext context);
}
