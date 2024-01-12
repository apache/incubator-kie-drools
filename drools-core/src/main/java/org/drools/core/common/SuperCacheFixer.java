package org.drools.core.common;

import org.drools.base.common.NetworkNode;
import org.drools.base.reteoo.NodeTypeEnums;
import org.drools.core.reteoo.AccumulateNode;
import org.drools.core.reteoo.AlphaNode;
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
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.ObjectTypeNodeId;
import org.drools.core.reteoo.QueryElementNode;
import org.drools.core.reteoo.QueryTerminalNode;
import org.drools.core.reteoo.ReactiveFromNode;
import org.drools.core.reteoo.RightInputAdapterNode;
import org.drools.core.reteoo.RightTupleImpl;
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
        switch (((BaseNode) s).getType()) {
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

    public static RightTupleSink getRightTupleSink(RightTupleImpl t) {
        Sink s = t.getSink();
        switch (((BaseNode) s).getType()) {
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
        switch (((BaseNode) n).getType()) {
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

    public static NetworkNode checkcast(NetworkNode n) {
        switch (((BaseNode)n).getType()) {
            case NodeTypeEnums.ObjectTypeNode : return (ObjectTypeNode) n;
            case NodeTypeEnums.AlphaNode : return (AlphaNode) n;
            case NodeTypeEnums.LeftInputAdapterNode: return (LeftInputAdapterNode) n;
            case NodeTypeEnums.AlphaTerminalNode: return (AlphaTerminalNode) n;
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
            case NodeTypeEnums.WindowNode: return (WindowNode) n;
            case NodeTypeEnums.TimerConditionNode: return (TimerNode) n;
            case NodeTypeEnums.MockBetaNode: return (NetworkNode) n;
            case NodeTypeEnums.MockAlphaNode: return (NetworkNode) n;
            default:
                throw new UnsupportedOperationException("Switch needs to include: " + n);
        }
    }

    public static NetworkNode getSink(TupleImpl t) {
        return checkcast(t.getSink());
    }

    public static ObjectTypeNode getObjectTypeNode(TupleImpl t) {
        Sink s = t.getSink();
        switch (((BaseNode) s).getType()) {
            case NodeTypeEnums.RuleTerminalNode : return ((RuleTerminalNode) s).getObjectTypeNode();
            case NodeTypeEnums.QueryTerminalNode: return ((QueryTerminalNode) s).getObjectTypeNode();
            case NodeTypeEnums.AccumulateNode : return ((AccumulateNode) s).getObjectTypeNode();
            case NodeTypeEnums.ExistsNode: return ((ExistsNode) s).getObjectTypeNode();
            case NodeTypeEnums.NotNode: return ((NotNode) s).getObjectTypeNode();
            case NodeTypeEnums.JoinNode: return ((JoinNode) s).getObjectTypeNode();
            default:
                throw new UnsupportedOperationException("Node does not have an ObjectType: " + s);
        }
    }

    public static ObjectTypeNodeId getLeftInputOtnId(TupleImpl t) {
        Sink s = t.getSink();
        switch (((BaseNode) s).getType()) {
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
        switch (((BaseNode) s).getType()) {
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
        LeftTupleSource n;
        switch (((BaseNode) s).getType()) {
            case NodeTypeEnums.RuleTerminalNode : n = ((RuleTerminalNode) s).getLeftTupleSource(); break;
            case NodeTypeEnums.QueryTerminalNode: n =  ((QueryTerminalNode) s).getLeftTupleSource(); break;
            case NodeTypeEnums.RightInputAdapterNode: n =  ((RightInputAdapterNode) s).getLeftTupleSource(); break;
            case NodeTypeEnums.AccumulateNode : n =  ((AccumulateNode) s).getLeftTupleSource(); break;
            case NodeTypeEnums.ExistsNode: n =  ((ExistsNode) s).getLeftTupleSource(); break;
            case NodeTypeEnums.NotNode: n =  ((NotNode) s).getLeftTupleSource(); break;
            case NodeTypeEnums.JoinNode: n =  ((JoinNode) s).getLeftTupleSource(); break;
            case NodeTypeEnums.FromNode: n =  ((FromNode) s).getLeftTupleSource(); break;
            case NodeTypeEnums.EvalConditionNode: n =  ((EvalConditionNode) s).getLeftTupleSource(); break;
            case NodeTypeEnums.AsyncReceiveNode: n =  ((AsyncReceiveNode) s).getLeftTupleSource(); break;
            case NodeTypeEnums.AsyncSendNode: n =  ((AsyncSendNode) s).getLeftTupleSource(); break;
            case NodeTypeEnums.ReactiveFromNode: n =  ((ReactiveFromNode) s).getLeftTupleSource(); break;
            case NodeTypeEnums.ConditionalBranchNode: n =  ((ConditionalBranchNode) s).getLeftTupleSource(); break;
            case NodeTypeEnums.QueryElementNode: n =  ((QueryElementNode) s).getLeftTupleSource(); break;
            case NodeTypeEnums.TimerConditionNode: n =  ((TimerNode) s).getLeftTupleSource(); break;
            default:
                throw new UnsupportedOperationException("Does not have a LeftTupleSource: " + s);
        }

        switch (n.getType()) {
            case NodeTypeEnums.LeftInputAdapterNode: return (LeftInputAdapterNode) n;
            case NodeTypeEnums.AlphaTerminalNode: return (AlphaTerminalNode) n;
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
            default:
                throw new UnsupportedOperationException("Switch needs to include: " + n);
        }
    }

    public static ObjectSource getObjectSource(TupleImpl t) {
        Sink s = t.getSink();
        ObjectSource o;
        switch (((BaseNode) s).getType()) {
            case NodeTypeEnums.AccumulateNode : o = ((AccumulateNode) s).getRightInput(); break;
            case NodeTypeEnums.ExistsNode: o =  ((ExistsNode) s).getRightInput(); break;
            case NodeTypeEnums.NotNode: o =  ((NotNode) s).getRightInput(); break;
            case NodeTypeEnums.JoinNode: o =  ((JoinNode) s).getRightInput(); break;
            default:
                throw new UnsupportedOperationException("Node does not have an ObjectSource: " + s);
        }

        switch (o.getType()) {
            case NodeTypeEnums.AlphaNode : return (AlphaNode) o;
            case NodeTypeEnums.WindowNode: return (WindowNode) o;
            case NodeTypeEnums.ObjectTypeNode : return (ObjectTypeNode) o;
            case NodeTypeEnums.RightInputAdapterNode : return (RightInputAdapterNode) o;
            default:
                throw new UnsupportedOperationException("Node does match an ObjectSource: " + o);
        }
    }

    public static TerminalNode asTerminalNode(TupleImpl t) {
        Sink s = t.getSink();
        switch (((BaseNode) t.getSink()).getType()) {
            case NodeTypeEnums.RuleTerminalNode : return (RuleTerminalNode) s;
            case NodeTypeEnums.QueryTerminalNode: return (QueryTerminalNode) s;
            default: return null;
        }
    }

    public static TerminalNode asTerminalNode(NetworkNode n) {
        switch (((BaseNode)n).getType()) {
            case NodeTypeEnums.RuleTerminalNode : return (RuleTerminalNode) n;
            case NodeTypeEnums.QueryTerminalNode: return (QueryTerminalNode) n;
            default: return null;
        }


    }
}
