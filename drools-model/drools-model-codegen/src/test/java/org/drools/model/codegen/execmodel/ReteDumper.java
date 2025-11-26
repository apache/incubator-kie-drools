/*
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
package org.drools.model.codegen.execmodel;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import org.drools.core.common.BaseNode;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.Rete;
import org.drools.core.reteoo.RightInputAdapterNode;
import org.drools.core.reteoo.Sink;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

public class ReteDumper {

    private static final BiConsumer<String, BaseNode> voidConsumer = (ident, node) -> {};
    private static final BiConsumer<String, BaseNode> dumpConsumer = (ident, node) -> System.out.println(ident + node);

    private ReteDumper() { }

    public static Set<BaseNode> collectNodes(KieBase kbase) {
        return visitRete(((InternalKnowledgeBase) kbase).getRete(), voidConsumer);
    }

    public static Set<BaseNode> collectNodes(KieSession session) {
        return collectNodes( session.getKieBase() );
    }

    public static Set<BaseNode> dumpRete(KieBase kbase) {
        return visitRete(((InternalKnowledgeBase) kbase).getRete(), dumpConsumer);
    }

    public static Set<BaseNode> dumpRete(KieSession session) {
        return dumpRete( session.getKieBase() );
    }

    public static Set<BaseNode> checkRete(KieBase kbase, Predicate<BaseNode> predicate) {
        return visitRete(((InternalKnowledgeBase) kbase).getRete(), toConsumer(predicate));
    }

    public static Set<BaseNode> checkRete(KieSession session, Predicate<BaseNode> predicate) {
        return checkRete(session.getKieBase(), predicate);
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
        } else if (node instanceof RightInputAdapterNode<?> ria) {
            BetaNode betaNode = ria.getBetaNode();
            sinks = new Sink[] { betaNode };
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
