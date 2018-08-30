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

package org.drools.core.reteoo;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.drools.core.common.BaseNode;
import org.drools.core.impl.InternalKnowledgeBase;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.KieSession;

public class ReteDumper {

    private Predicate<BaseNode> nodesFilter;

    private ReteDumper() {
        this(node -> true);
    }

    public ReteDumper(Predicate<BaseNode> nodesFilter) {
        this.nodesFilter = nodesFilter;
    }

    public ReteDumper(String ruleName) {
        this( node -> Stream.of( node.getAssociatedRules() ).anyMatch( rule -> rule.getName().equals( ruleName ) ) );
    }

    public static void dumpRete(KieBase kbase ) {
        new ReteDumper().dump((InternalKnowledgeBase) kbase);
    }

    public static void dumpRete(KieRuntime session ) {
        new ReteDumper().dump((InternalKnowledgeBase)session.getKieBase());
    }

    public static void dumpRete(KieSession session) {
        new ReteDumper().dump((InternalKnowledgeBase)session.getKieBase());
    }

    public static void dumpRete(InternalKnowledgeBase kBase) {
        new ReteDumper().dump(kBase.getRete());
    }

    public static void dumpRete(Rete rete) {
        new ReteDumper().dump(rete);
    }

    public void dump(KieBase kbase ) {
        dump((InternalKnowledgeBase) kbase);
    }

    public void dump(KieRuntime session ) {
        dump((InternalKnowledgeBase)session.getKieBase());
    }

    public void dump(KieSession session) {
        dump((InternalKnowledgeBase)session.getKieBase());
    }

    public void dump(InternalKnowledgeBase kBase) {
        dump(kBase.getRete());
    }

    public void dump(Rete rete) {
        traverseRete(rete, this::dumpNode);
    }

    public static Set<BaseNode> collectRete(KieBase kbase ) {
        return new ReteDumper().collect((InternalKnowledgeBase) kbase);
    }

    public static Set<BaseNode> collectRete(KieRuntime session ) {
        return new ReteDumper().collect((InternalKnowledgeBase)session.getKieBase());
    }

    public static Set<BaseNode> collectRete(KieSession session) {
        return new ReteDumper().collect((InternalKnowledgeBase)session.getKieBase());
    }

    public static Set<BaseNode> collectRete(InternalKnowledgeBase kBase) {
        return new ReteDumper().collect(kBase.getRete());
    }

    public static Set<BaseNode> collectRete(Rete rete) {
        return new ReteDumper().collect(rete);
    }

    public Set<BaseNode> collect(KieBase kbase ) {
        return collect((InternalKnowledgeBase) kbase);
    }

    public Set<BaseNode> collect(KieRuntime session ) {
        return collect((InternalKnowledgeBase)session.getKieBase());
    }

    public Set<BaseNode> collect(KieSession session) {
        return collect((InternalKnowledgeBase)session.getKieBase());
    }

    public Set<BaseNode> collect(InternalKnowledgeBase kBase) {
        return collect(kBase.getRete());
    }

    public Set<BaseNode> collect(Rete rete) {
        Set<BaseNode> nodes = new HashSet<>();
        traverseRete(rete, (node, s) -> nodes.add(node));
        return nodes;
    }

    public void traverseRete(Rete rete, BiConsumer<BaseNode, String> consumer) {
        for (EntryPointNode entryPointNode : rete.getEntryPointNodes().values()) {
            dumpNode( entryPointNode, "", new HashSet<BaseNode>(), consumer);
        }
    }

    private void dumpNode( BaseNode node, String ident, Set<BaseNode> visitedNodes, BiConsumer<BaseNode, String> consumer ) {
        consumer.accept( node, ident );
        if (!visitedNodes.add( node )) {
            return;
        }
        Sink[] sinks = node.getSinks();
        if (sinks != null) {
            for (Sink sink : sinks) {
                if (sink instanceof BaseNode) {
                    BaseNode sinkNode = ( BaseNode ) sink;
                    if ( nodesFilter.test( sinkNode ) ) {
                        dumpNode( sinkNode, ident + "    ", visitedNodes, consumer );
                    }
                }
            }
        }
    }

    private void dumpNode( BaseNode node, String ident ) {
        System.out.print(ident + node + " on " + node.getPartitionId());
        try {
            Object declaredMask = node.getClass().getMethod("getDeclaredMask").invoke(node);
            Object inferreddMask = node.getClass().getMethod("getInferredMask").invoke(node);
            System.out.print(" d "+declaredMask + " i " + inferreddMask);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            // do nothing.
        }
        try {
            Object declaredMask = node.getClass().getMethod("getLeftDeclaredMask").invoke(node);
            Object inferreddMask = node.getClass().getMethod("getLeftInferredMask").invoke(node);
            System.out.print(" Ld "+declaredMask + " Li " + inferreddMask);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            // do nothing.
        }
        try {
            Object declaredMask = node.getClass().getMethod("getRightDeclaredMask").invoke(node);
            Object inferreddMask = node.getClass().getMethod("getRightInferredMask").invoke(node);
            System.out.print(" Rd "+declaredMask + " Ri " + inferreddMask);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            // do nothing.
        }
        System.out.print("\n");
    }
}
