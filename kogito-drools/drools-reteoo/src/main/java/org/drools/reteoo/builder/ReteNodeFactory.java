/*
 * Copyright 2010 JBoss Inc
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

package org.drools.reteoo.builder;


import org.drools.core.base.ClassObjectType;
import org.drools.core.base.ValueType;
import org.drools.core.common.BaseNode;
import org.drools.core.common.BetaConstraints;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.factmodel.traits.TraitProxy;
import org.drools.core.reteoo.AccumulateNode;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.ConditionalBranchEvaluator;
import org.drools.core.reteoo.ConditionalBranchNode;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.EvalConditionNode;
import org.drools.core.reteoo.ExistsNode;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.NotNode;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.PropagationQueuingNode;
import org.drools.core.reteoo.QueryElementNode;
import org.drools.core.reteoo.QueryRiaFixerNode;
import org.drools.core.reteoo.RightInputAdapterNode;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.reteoo.TraitObjectTypeNode;
import org.drools.core.reteoo.TraitProxyObjectTypeNode;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.reteoo.builder.NodeFactory;
import org.drools.core.rule.Accumulate;
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
import org.drools.reteoo.nodes.ReteAccumulateNode;
import org.drools.reteoo.nodes.ReteAlphaNode;
import org.drools.reteoo.nodes.ReteConditionalBranchNode;
import org.drools.reteoo.nodes.ReteEntryPointNode;
import org.drools.reteoo.nodes.ReteEvalConditionNode;
import org.drools.reteoo.nodes.ReteExistsNode;
import org.drools.reteoo.nodes.ReteFromNode;
import org.drools.reteoo.nodes.ReteJoinNode;
import org.drools.reteoo.nodes.ReteLeftInputAdapterNode;
import org.drools.reteoo.nodes.ReteNotNode;
import org.drools.reteoo.nodes.ReteQueryElementNode;
import org.drools.reteoo.nodes.ReteQueryTerminalNode;
import org.drools.reteoo.nodes.ReteRightInputAdapterNode;
import org.drools.reteoo.nodes.ReteRuleTerminalNode;

import java.io.Serializable;

public class ReteNodeFactory implements NodeFactory, Serializable {


    public EntryPointNode buildEntryPointNode(int id, ObjectSource objectSource, BuildContext context) {
        return new ReteEntryPointNode(id, objectSource, context);
    }

    public EntryPointNode buildEntryPointNode(int id, RuleBasePartitionId partitionId, boolean partitionsEnabled, ObjectSource objectSource, EntryPointId entryPoint) {
        return new ReteEntryPointNode(id, partitionId, partitionsEnabled, objectSource, entryPoint);
    }

    public AlphaNode buildAlphaNode( int id, AlphaNodeFieldConstraint constraint, ObjectSource objectSource, BuildContext context ) {
        return new ReteAlphaNode( id, constraint, objectSource, context );
    }

    public TerminalNode buildTerminalNode( int id, LeftTupleSource source, RuleImpl rule, GroupElement subrule, int subruleIndex, BuildContext context ) {
        return new ReteRuleTerminalNode( id, source, rule, subrule, subruleIndex, context );
    }

    public ObjectTypeNode buildObjectTypeNode( int id, EntryPointNode objectSource, ObjectType objectType, BuildContext context ) {
        if ( objectType.getValueType().equals( ValueType.TRAIT_TYPE ) ) {
            if ( TraitProxy.class.isAssignableFrom( ( (ClassObjectType) objectType ).getClassType() ) ) {
                return new TraitProxyObjectTypeNode( id, objectSource, objectType, context );
            } else {
                return new TraitObjectTypeNode( id, objectSource, objectType, context );
            }
        } else {
            return new ObjectTypeNode( id, objectSource, objectType, context );
        }
    }

    public EvalConditionNode buildEvalNode(final int id,
                                           final LeftTupleSource tupleSource,
                                           final EvalCondition eval,
                                           final BuildContext context) {
        return new ReteEvalConditionNode( id, tupleSource, eval, context );
    }


    public QueryRiaFixerNode buildQueryRiaFixerNode(int id, LeftTupleSource tupleSource, BuildContext context) {
        return new QueryRiaFixerNode(id, tupleSource, context);
    }

    public PropagationQueuingNode buildPropagationQueuingNode(final int id,
                                                              final ObjectSource objectSource,
                                                              final BuildContext context) {
        return new PropagationQueuingNode(id, objectSource, context);
    }

    public RightInputAdapterNode buildRightInputNode( int id, LeftTupleSource leftInput, LeftTupleSource startTupleSource, BuildContext context ) {
        return new ReteRightInputAdapterNode( id, leftInput, startTupleSource, context );
    }

    public JoinNode buildJoinNode( int id, LeftTupleSource leftInput, ObjectSource rightInput, BetaConstraints binder, BuildContext context ) {
        return new ReteJoinNode( id, leftInput, rightInput, binder, context );
    }

    public NotNode buildNotNode( int id, LeftTupleSource leftInput, ObjectSource rightInput, BetaConstraints binder, BuildContext context ) {
        return new ReteNotNode( id, leftInput, rightInput, binder, context );
    }

    public ExistsNode buildExistsNode( int id, LeftTupleSource leftInput, ObjectSource rightInput, BetaConstraints binder, BuildContext context ) {
        return new ReteExistsNode( id, leftInput, rightInput, binder, context );
    }

    public AccumulateNode buildAccumulateNode(int id, LeftTupleSource leftInput, ObjectSource rightInput,
                                              AlphaNodeFieldConstraint[] resultConstraints, BetaConstraints sourceBinder,
                                              BetaConstraints resultBinder, Accumulate accumulate, boolean unwrapRightObject, BuildContext context ) {
        return new ReteAccumulateNode( id, leftInput, rightInput, resultConstraints, sourceBinder,resultBinder, accumulate, unwrapRightObject, context );
    }

    public LeftInputAdapterNode buildLeftInputAdapterNode( int id, ObjectSource objectSource, BuildContext context ) {
        return new ReteLeftInputAdapterNode( id, objectSource, context );
    }

    public TerminalNode buildQueryTerminalNode(int id, LeftTupleSource source, RuleImpl rule, GroupElement subrule, int subruleIndex, BuildContext context) {
        return new ReteQueryTerminalNode( id, source, rule, subrule, subruleIndex, context );
    }

    public QueryElementNode buildQueryElementNode( int id, LeftTupleSource tupleSource, QueryElement qe, boolean tupleMemoryEnabled, boolean openQuery, BuildContext context ) {
        return new ReteQueryElementNode( id, tupleSource, qe, tupleMemoryEnabled, openQuery, context );
    }

    public BaseNode buildFromNode(int id, DataProvider dataProvider, LeftTupleSource tupleSource, AlphaNodeFieldConstraint[] alphaNodeFieldConstraints, BetaConstraints betaConstraints, boolean tupleMemoryEnabled, BuildContext context, From from) {
        return new ReteFromNode( id, dataProvider, tupleSource, alphaNodeFieldConstraints, betaConstraints, tupleMemoryEnabled, context, from );
    }

    public BaseNode buildReactiveFromNode(int id, DataProvider dataProvider, LeftTupleSource tupleSource, AlphaNodeFieldConstraint[] alphaNodeFieldConstraints, BetaConstraints betaConstraints, boolean tupleMemoryEnabled, BuildContext context, From from) {
        throw new UnsupportedOperationException("Cannot create a ReactiveFromNode with RETE engine");
    }

    public BaseNode buildTimerNode( int id,
                                    Timer timer,
                                    final String[] calendarNames,
                                    final Declaration[][]   declarations,
                                    LeftTupleSource tupleSource,
                                    BuildContext context ) {
        throw new UnsupportedOperationException();
    }

    public ConditionalBranchNode buildConditionalBranchNode(int id,
                                                            LeftTupleSource tupleSource,
                                                            ConditionalBranchEvaluator branchEvaluator,
                                                            BuildContext context) {
        return new ReteConditionalBranchNode( id, tupleSource, branchEvaluator, context );

    }
}
