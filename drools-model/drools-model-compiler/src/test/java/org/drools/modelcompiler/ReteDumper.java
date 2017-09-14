package org.drools.modelcompiler;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import org.drools.core.common.BaseNode;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.Rete;
import org.drools.core.reteoo.Sink;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

public class ReteDumper {

    private static final BiConsumer<String, BaseNode> dumpConsumer = (ident, node) -> System.out.println(ident + node);

    private ReteDumper() { }

    public static Set<BaseNode> dumpRete(KieBase kbase) {
        return dumpRete((InternalKnowledgeBase) kbase);
    }

    public static Set<BaseNode> dumpRete(KieSession session) {
        return dumpRete((InternalKnowledgeBase)session.getKieBase());
    }

    public static Set<BaseNode> dumpRete(InternalKnowledgeBase kBase) {
        return visitRete( kBase.getRete(), dumpConsumer );
    }

    public static Set<BaseNode> checkRete(KieBase kbase, Predicate<BaseNode> predicate) {
        return checkRete((InternalKnowledgeBase) kbase, predicate);
    }

    public static Set<BaseNode> checkRete(KieSession session, Predicate<BaseNode> predicate) {
        return checkRete((InternalKnowledgeBase)session.getKieBase(), predicate);
    }

    public static Set<BaseNode> checkRete(InternalKnowledgeBase kBase, Predicate<BaseNode> predicate) {
        return visitRete( kBase.getRete(), toConsumer( predicate ) );
    }

    public static Set<BaseNode> visitRete( Rete rete, BiConsumer<String, BaseNode> consumer ) {
        HashSet<BaseNode> visitedNodes = new HashSet<BaseNode>();
        for (EntryPointNode entryPointNode : rete.getEntryPointNodes().values()) {
            visitNode( entryPointNode, "", visitedNodes, consumer );
        }
        return visitedNodes;
    }

    private static void visitNode( BaseNode node, String ident, Set<BaseNode> visitedNodes, BiConsumer<String, BaseNode> consumer ) {
        consumer.accept( ident, node );
        if (!visitedNodes.add( node )) {
            return;
        }
        Sink[] sinks = getSinks( node );
        if (sinks != null) {
            for (Sink sink : sinks) {
                if (sink instanceof BaseNode) {
                    visitNode( (BaseNode)sink, ident + "    ", visitedNodes, consumer );
                }
            }
        }
    }

    public static Sink[] getSinks( BaseNode node ) {
        Sink[] sinks = null;
        if (node instanceof EntryPointNode ) {
            EntryPointNode source = (EntryPointNode) node;
            Collection<ObjectTypeNode> otns = source.getObjectTypeNodes().values();
            sinks = otns.toArray(new Sink[otns.size()]);
        } else if (node instanceof ObjectSource ) {
            ObjectSource source = (ObjectSource) node;
            sinks = source.getObjectSinkPropagator().getSinks();
        } else if (node instanceof LeftTupleSource ) {
            LeftTupleSource source = (LeftTupleSource) node;
            sinks = source.getSinkPropagator().getSinks();
        }
        return sinks;
    }

    private static BiConsumer<String, BaseNode> toConsumer( Predicate<BaseNode> predicate ) {
        return (ident, node) -> {
            if ( !predicate.test( node ) ) {
                throw new RuntimeException( "Failed on " + node );
            }
        };
    }
}
