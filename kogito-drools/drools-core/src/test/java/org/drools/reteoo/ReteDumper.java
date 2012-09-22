package org.drools.reteoo;

import org.drools.KnowledgeBase;
import org.drools.RuleBase;
import org.drools.common.BaseNode;
import org.drools.impl.InternalKnowledgeBase;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.StatefulKnowledgeSession;

import java.util.Collection;

public class ReteDumper {

    private ReteDumper() { }

    public static void dumpRete(KnowledgeBase kbase) {
        dumpRete(((InternalKnowledgeBase) kbase).getRuleBase());
    }

    public static void dumpRete(KnowledgeRuntime session) {
        dumpRete(((KnowledgeBaseImpl)session.getKnowledgeBase()).getRuleBase());
    }

    public static void dumpRete(RuleBase ruleBase) {
        dumpRete(((ReteooRuleBase)ruleBase).getRete());
    }

    public static void dumpRete(Rete rete) {
        for (EntryPointNode entryPointNode : rete.getEntryPointNodes().values()) {
            dumpNode( entryPointNode, "" );
        }
    }

    private static void dumpNode(BaseNode node, String ident) {
        System.out.println(ident + node);
        Sink[] sinks = null;
        if (node instanceof EntryPointNode) {
            EntryPointNode source = (EntryPointNode) node;
            Collection<ObjectTypeNode> otns = source.getObjectTypeNodes().values();
            sinks = otns.toArray(new Sink[otns.size()]);
        } else if (node instanceof ObjectSource) {
            ObjectSource source = (ObjectSource) node;
            sinks = source.getSinkPropagator().getSinks();
        } else if (node instanceof LeftTupleSource) {
            LeftTupleSource source = (LeftTupleSource) node;
            sinks = source.getSinkPropagator().getSinks();
        }
        if (sinks != null) {
            for (Sink sink : sinks) {
                if (sink instanceof BaseNode) {
                    dumpNode((BaseNode)sink, ident + "    ");
                }
            }
        }
    }
}
