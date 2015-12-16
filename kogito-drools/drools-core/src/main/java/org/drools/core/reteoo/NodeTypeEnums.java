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

package org.drools.core.reteoo;

import org.drools.core.common.NetworkNode;

/**
 * 
 * ObjectSource   : < NodeTypeEnums.ObjectSource * 
 * LeftTupleSource: > LeftTupleSource
 * BetaNode       : > BetaNode
 * 
 * ObjectSink     : % 2 == 0
 * LeftSource     : % 2 != 0
 *
 */
public class NodeTypeEnums {
    // ObjectSource, ObjectSink
    public static final short EntryPointNode          = 10;
    public static final short ReteNode                = 20;
    public static final short ObjectTypeNode          = 30;
    public static final short AlphaNode               = 40;
    public static final short PropagationQueuingNode  = 50;
    public static final short WindowNode              = 60;
    public static final short PropagationQueueingNode = 65;

    // ObjectSource, LeftTupleSink
    public static final short RightInputAdaterNode    = 71; // also ObjectSource %2 != 0

    public static final short ObjectSource            = 80;

    // LefTTupleSink
    public static final short QueryTerminalNode       = 91;
    public static final short RuleTerminalNode        = 101;

    // LeftTupleSource, LefTTupleSink
    public static final short LeftTupleSource         = 111;
    public static final short LeftInputAdapterNode    = 120; // also ObjectSink %2 == 0
    public static final short EvalConditionNode       = 131;
    public static final short TimerConditionNode      = 133;
    public static final short QueryRiaFixerNode       = 141;
    public static final short FromNode                = 151;
    public static final short ReactiveFromNode        = 153;
    public static final short UnificationNode         = 165; // these two need to be merged
    public static final short QueryElementNode        = 165;
    public static final short ConditionalBranchNode   = 167;

    // LeftTupleSource, LefTTupleSink, BetaNode
    public static final short BetaNode                = 171;
    public static final short JoinNode                = 181;
    public static final short NotNode                 = 191;
    public static final short ExistsNode              = 201;
    public static final short AccumulateNode          = 211;
    public static final short ForallNotNode           = 221;
    public static final short ElseNode                = 231;
    //public static final short CollectNode          = 5;   // no longer used, since accumulate nodes execute collect logic now

    // mdp not sure what number this should be yet
    public static final short OperatorNode            = 19;

    public static boolean isObjectSource(NetworkNode node) {
        return node.getType() < NodeTypeEnums.ObjectSource;
    }

    public static boolean isObjectSink(NetworkNode node) {
        return node.getType() % 2 == 0;
    }

    public static boolean isLeftTupleSource(NetworkNode node) {
        return node.getType() > NodeTypeEnums.LeftTupleSource;
    }

    public static boolean isBetaNode(NetworkNode node) {
        return node.getType() > NodeTypeEnums.BetaNode;
    }

    public static boolean isTerminalNode(NetworkNode node) {
        return node.getType() == QueryTerminalNode || node.getType() == RuleTerminalNode;
    }

    public static boolean isLeftTupleSink(NetworkNode node) {
        return node.getType() % 2 != 0;
    }
}
