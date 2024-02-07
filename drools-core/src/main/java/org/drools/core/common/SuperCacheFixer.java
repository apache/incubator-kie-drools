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
package org.drools.core.common;

import org.drools.base.common.NetworkNode;
import org.drools.base.reteoo.NodeTypeEnums;
import org.drools.core.reteoo.AccumulateNode;
import org.drools.core.reteoo.AlphaTerminalNode;
import org.drools.core.reteoo.AsyncReceiveNode;
import org.drools.core.reteoo.AsyncSendNode;
import org.drools.core.reteoo.ConditionalBranchNode;
import org.drools.core.reteoo.EvalConditionNode;
import org.drools.core.reteoo.ExistsNode;
import org.drools.core.reteoo.FromNode;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftTupleNode;
import org.drools.core.reteoo.LeftTupleSinkNode;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.NotNode;
import org.drools.core.reteoo.ObjectTypeNodeId;
import org.drools.core.reteoo.QueryElementNode;
import org.drools.core.reteoo.QueryTerminalNode;
import org.drools.core.reteoo.ReactiveFromNode;
import org.drools.core.reteoo.RightInputAdapterNode;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.RightTupleSink;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.Sink;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.reteoo.TimerNode;
import org.drools.core.reteoo.TupleImpl;
import org.drools.core.reteoo.WindowNode;

// It's better to always cast to a concrete or abstract class to avoid
// secondary super cache problem. See https://issues.redhat.com/browse/DROOLS-7521
public class SuperCacheFixer {
    
    public static LeftTupleNode getLeftTupleNode(TupleImpl t) {
        Sink s = t.getSink();
        switch (s.getType()) {
            case NodeTypeEnums.RuleTerminalNode : return (RuleTerminalNode) s;
            case NodeTypeEnums.QueryTerminalNode: return (QueryTerminalNode) s;
            case NodeTypeEnums.RightInputAdapterNode: return (RightInputAdapterNode) s;
            case NodeTypeEnums.LeftInputAdapterNode: return (LeftInputAdapterNode) s;
            case NodeTypeEnums.AlphaTerminalNode: return (AlphaTerminalNode) s;
            case NodeTypeEnums.AccumulateNode : return (AccumulateNode) s;
            case NodeTypeEnums.ExistsNode: return (ExistsNode) s;
            case NodeTypeEnums.NotNode: return (NotNode) s;
            case NodeTypeEnums.FromNode: return (FromNode) s;
            case NodeTypeEnums.JoinNode: return (JoinNode) s;
            case NodeTypeEnums.EvalConditionNode: return (EvalConditionNode) s;
            case NodeTypeEnums.AsyncReceiveNode: return (AsyncReceiveNode) s;
            case NodeTypeEnums.AsyncSendNode: return (AsyncSendNode) s;
            case NodeTypeEnums.ReactiveFromNode: return (ReactiveFromNode) s;
            case NodeTypeEnums.ConditionalBranchNode: return (ConditionalBranchNode) s;
            case NodeTypeEnums.QueryElementNode: return (QueryElementNode) s;
            case NodeTypeEnums.TimerConditionNode: return (TimerNode) s;
            case NodeTypeEnums.MockBetaNode: return (LeftTupleNode) s;
            default:
                throw new UnsupportedOperationException("Cannot be cast to LeftTupleNode: " + s);
        }
    }

    public static RightTupleSink getRightTupleSink(RightTuple t) {
        Sink s = t.getSink();
        switch (s.getType()) {
            case NodeTypeEnums.AccumulateNode : return (AccumulateNode) s;
            case NodeTypeEnums.ExistsNode: return (ExistsNode) s;
            case NodeTypeEnums.NotNode: return (NotNode) s;
            case NodeTypeEnums.JoinNode: return (JoinNode) s;
            case NodeTypeEnums.WindowNode: return (WindowNode) s;
            case NodeTypeEnums.MockBetaNode: return (RightTupleSink) s;
            case NodeTypeEnums.MockAlphaNode: return (RightTupleSink) s;
            default:
                throw new UnsupportedOperationException("Cannot be cast to RightTupleSink: " + s);
        }
    }

    public static LeftTupleSinkNode asLeftTupleSink(NetworkNode n) {
        switch (n.getType()) {
            case NodeTypeEnums.RuleTerminalNode : return (RuleTerminalNode) n;
            case NodeTypeEnums.QueryTerminalNode: return (QueryTerminalNode) n;
            case NodeTypeEnums.RightInputAdapterNode: return (RightInputAdapterNode) n;
            case NodeTypeEnums.AccumulateNode : return (AccumulateNode) n;
            case NodeTypeEnums.ExistsNode: return (ExistsNode) n;
            case NodeTypeEnums.NotNode: return (NotNode) n;
            case NodeTypeEnums.FromNode: return (FromNode) n;
            case NodeTypeEnums.JoinNode: return (JoinNode) n;
            case NodeTypeEnums.EvalConditionNode: return (EvalConditionNode) n;
            case NodeTypeEnums.AsyncReceiveNode: return (AsyncReceiveNode) n;
            case NodeTypeEnums.AsyncSendNode: return (AsyncSendNode) n;
            case NodeTypeEnums.ReactiveFromNode: return (ReactiveFromNode) n;
            case NodeTypeEnums.ConditionalBranchNode: return (ConditionalBranchNode) n;
            case NodeTypeEnums.QueryElementNode: return (QueryElementNode) n;
            case NodeTypeEnums.TimerConditionNode: return (TimerNode) n;
            case NodeTypeEnums.MockBetaNode: return (LeftTupleSinkNode) n;
            default:
                throw new UnsupportedOperationException("Cannot be cast to LeftTupleNode: " + n);
        }
    }

    public static ObjectTypeNodeId getLeftInputOtnId(TupleImpl t) {
        Sink s = t.getSink();
        switch (s.getType()) {
            case NodeTypeEnums.RuleTerminalNode : return ((RuleTerminalNode) s).getLeftInputOtnId();
            case NodeTypeEnums.QueryTerminalNode: return ((QueryTerminalNode) s).getLeftInputOtnId();
            case NodeTypeEnums.RightInputAdapterNode: return ((RightInputAdapterNode) s).getLeftInputOtnId();
            case NodeTypeEnums.AccumulateNode : return ((AccumulateNode) s).getLeftInputOtnId();
            case NodeTypeEnums.ExistsNode: return ((ExistsNode) s).getLeftInputOtnId();
            case NodeTypeEnums.NotNode: return ((NotNode) s).getLeftInputOtnId();
            case NodeTypeEnums.JoinNode: return ((JoinNode) s).getLeftInputOtnId();
            case NodeTypeEnums.FromNode: return ((FromNode) s).getLeftInputOtnId();
            case NodeTypeEnums.EvalConditionNode: return ((EvalConditionNode) s).getLeftInputOtnId();
            case NodeTypeEnums.AsyncReceiveNode: return ((AsyncReceiveNode) s).getLeftInputOtnId();
            case NodeTypeEnums.AsyncSendNode: return ((AsyncSendNode) s).getLeftInputOtnId();
            case NodeTypeEnums.ReactiveFromNode: return ((ReactiveFromNode) s).getLeftInputOtnId();
            case NodeTypeEnums.ConditionalBranchNode: return ((ConditionalBranchNode) s).getLeftInputOtnId();
            case NodeTypeEnums.QueryElementNode: return ((QueryElementNode) s).getLeftInputOtnId();
            case NodeTypeEnums.TimerConditionNode: return ((TimerNode) s).getLeftInputOtnId();
            case NodeTypeEnums.MockBetaNode: return ((LeftTupleSource)s).getLeftInputOtnId();
            default:
                throw new UnsupportedOperationException("Node does not have an LeftInputOtnId: " + s);
        }
    }

    public static ObjectTypeNodeId getRightInputOtnId(TupleImpl t) {
        Sink s = t.getSink();
        switch (s.getType()) {
            case NodeTypeEnums.AccumulateNode : return ((AccumulateNode) s).getRightInputOtnId();
            case NodeTypeEnums.ExistsNode: return ((ExistsNode) s).getRightInputOtnId();
            case NodeTypeEnums.NotNode: return ((NotNode) s).getRightInputOtnId();
            case NodeTypeEnums.JoinNode: return ((JoinNode) s).getRightInputOtnId();
            case NodeTypeEnums.WindowNode: return ((WindowNode) s).getRightInputOtnId();
            default:
                throw new UnsupportedOperationException("Node does not have an RightInputOtnId: " + s);
        }
    }

    public static LeftTupleSource getLeftTupleSource(TupleImpl t) {
        Sink s = t.getSink();
        switch (s.getType()) {
            case NodeTypeEnums.RuleTerminalNode : return ((RuleTerminalNode) s).getLeftTupleSource();
            case NodeTypeEnums.QueryTerminalNode: return  ((QueryTerminalNode) s).getLeftTupleSource();
            case NodeTypeEnums.RightInputAdapterNode: return  ((RightInputAdapterNode) s).getLeftTupleSource();
            case NodeTypeEnums.AccumulateNode : return  ((AccumulateNode) s).getLeftTupleSource();
            case NodeTypeEnums.ExistsNode: return  ((ExistsNode) s).getLeftTupleSource();
            case NodeTypeEnums.NotNode: return  ((NotNode) s).getLeftTupleSource();
            case NodeTypeEnums.JoinNode: return  ((JoinNode) s).getLeftTupleSource();
            case NodeTypeEnums.FromNode: return  ((FromNode) s).getLeftTupleSource();
            case NodeTypeEnums.EvalConditionNode: return  ((EvalConditionNode) s).getLeftTupleSource();
            case NodeTypeEnums.AsyncReceiveNode: return  ((AsyncReceiveNode) s).getLeftTupleSource();
            case NodeTypeEnums.AsyncSendNode: return  ((AsyncSendNode) s).getLeftTupleSource();
            case NodeTypeEnums.ReactiveFromNode: return  ((ReactiveFromNode) s).getLeftTupleSource();
            case NodeTypeEnums.ConditionalBranchNode: return  ((ConditionalBranchNode) s).getLeftTupleSource();
            case NodeTypeEnums.QueryElementNode: return  ((QueryElementNode) s).getLeftTupleSource();
            case NodeTypeEnums.TimerConditionNode: return  ((TimerNode) s).getLeftTupleSource();
            default:
                throw new UnsupportedOperationException("Does not have a LeftTupleSource: " + s);
        }
    }

    public static TerminalNode asTerminalNode(TupleImpl t) {
        Sink s = t.getSink();
        switch (t.getSink().getType()) {
            case NodeTypeEnums.RuleTerminalNode : return (RuleTerminalNode) s;
            case NodeTypeEnums.QueryTerminalNode: return (QueryTerminalNode) s;
            default: return null;
        }
    }
}
