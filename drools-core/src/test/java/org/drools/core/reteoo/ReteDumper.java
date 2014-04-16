package org.drools.core.reteoo;

import org.drools.core.common.BaseNode;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.runtime.KnowledgeRuntime;

import java.util.Collection;

public class ReteDumper {

    private ReteDumper() { }

    public static void dumpRete(KnowledgeBase kbase) {
        dumpRete((InternalKnowledgeBase) kbase);
    }

    public static void dumpRete(KnowledgeRuntime session) {
        dumpRete((InternalKnowledgeBase)session.getKieBase());
    }

    public static void dumpRete(InternalKnowledgeBase kBase) {
        dumpRete(kBase.getRete());
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
