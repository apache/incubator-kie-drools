/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.drools.core.common.BaseNode;
import org.drools.core.impl.InternalKnowledgeBase;
import org.kie.api.KieBase;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Utility class to view Rete models
 *
 */
public class ReteDumper {

    private static Logger logger = LoggerFactory.getLogger(ReteDumper.class);

    private PrintWriter writer;
    private StringBuilder sb;
    private Predicate<BaseNode> nodesFilter;

    private boolean nodeInfoOnly = false;

    public ReteDumper() {
        this(node -> true);
    }

    public ReteDumper(Predicate<BaseNode> nodesFilter) {
        this.nodesFilter = nodesFilter;
    }

    public ReteDumper(String ruleName) {
        this( node -> Stream.of( node.getAssociatedRules() ).anyMatch( rule -> rule.getName().equals( ruleName ) ) );
    }

    public PrintWriter getWriter() {
        return writer;
    }

    /**
     * Set a writer to which ReteDumper prints results. By default, results will be printed to STDOUT
     * @param writer
     */
    public void setWriter(PrintWriter writer) {
        this.writer = writer;
    }

    public boolean isNodeInfoOnly() {
        return nodeInfoOnly;
    }

    /**
     * If true, dump without partition/mask information. Default value is false
     * @param nodeInfoOnly
     */
    public void setNodeInfoOnly(boolean nodeInfoOnly) {
        this.nodeInfoOnly = nodeInfoOnly;
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
        // Other dump/dumpRete methods eventually call this method
        sb = new StringBuilder();
        traverseRete(rete, this::dumpNode);
        printResults();
    }

    private void printResults() {
        if (writer == null) {
            System.out.print(sb.toString());
        } else {
            // if a writer is given by a caller, the caller is responsible for closing
            writer.print(sb.toString());
        }
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
        Set<BaseNode> nodes = createIdentitySet();
        traverseRete(rete, (node, s) -> nodes.add(node));
        return nodes;
    }

    public void traverseRete(Rete rete, BiConsumer<BaseNode, String> consumer) {
        for (EntryPointNode entryPointNode : rete.getEntryPointNodes().values()) {
            dumpNode( entryPointNode, "", createIdentitySet(), consumer);
        }
    }

    private <T> Set<T> createIdentitySet() {
        return Collections.newSetFromMap(new IdentityHashMap<>());
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
                        dumpNode( sinkNode, ident + "  ", visitedNodes, consumer );
                    }
                }
            }
        }
    }

    private void dumpNode( BaseNode node, String ident ) {
        sb.append(ident + formatNode(node));
        if (!nodeInfoOnly) {
            sb.append(" on " + node.getPartitionId());
            try {
                Object declaredMask = node.getClass().getMethod("getDeclaredMask").invoke(node);
                Object inferreddMask = node.getClass().getMethod("getInferredMask").invoke(node);
                sb.append(" d "+declaredMask + " i " + inferreddMask);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                logger.trace("failed to log mask values", e);
            }
            try {
                Object declaredMask = node.getClass().getMethod("getLeftDeclaredMask").invoke(node);
                Object inferreddMask = node.getClass().getMethod("getLeftInferredMask").invoke(node);
                sb.append(" Ld "+declaredMask + " Li " + inferreddMask);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                logger.trace("failed to log left mask values", e);
            }
            try {
                Object declaredMask = node.getClass().getMethod("getRightDeclaredMask").invoke(node);
                Object inferreddMask = node.getClass().getMethod("getRightInferredMask").invoke(node);
                sb.append(" Rd "+declaredMask + " Ri " + inferreddMask);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                logger.trace("failed to log right mask values", e);
            }
        }
        sb.append("\n");
    }

    /**
     * Format a node to informative String
     *
     * @param node
     * @return formatted String
     */
    public String formatNode(BaseNode node) {
        StringBuilder additionalInfo = new StringBuilder();
        if (node instanceof BetaNode) {
            BetaNode betaNode = (BetaNode) node;
            additionalInfo.append("contraints=");
            if (betaNode.getRawConstraints() != null) {
                additionalInfo.append(Arrays.toString(betaNode.getConstraints()));
            }
            if (node instanceof AccumulateNode) {
                AccumulateNode accNode = (AccumulateNode) node;
                additionalInfo.append(", resultConstraints=" + Arrays.toString(accNode.getResultConstraints()));
                additionalInfo.append(", resultBinder=" + Arrays.toString(accNode.getResultBinder().getConstraints()));
            }
        } else if (node instanceof FromNode<?>) {
            FromNode<?> fromNode = (FromNode<?>) node;
            additionalInfo.append("result=" + fromNode.getResultClass().getName());
            additionalInfo.append(", alphaConstraints=" + Arrays.toString(fromNode.getAlphaConstraints()));
            additionalInfo.append(", betaConstraints=" + Arrays.toString(fromNode.getBetaConstraints().getConstraints()));
        }

        if (additionalInfo.length() > 0) {
            return node + " <" + additionalInfo.toString() + "> ";
        }

        return node.toString();
    }

    public static void dumpAssociatedRulesRete(KieBase kieBase) {
        new ReteDumper().dumpAssociatedRules(((InternalKnowledgeBase) kieBase).getRete());
    }

    /**
     * Dump nodes with associated rules. Helps to locate rules from a node in problem
     * @param kieBase
     */
    public void dumpAssociatedRules(KieBase kieBase) {
        dumpAssociatedRules(((InternalKnowledgeBase) kieBase).getRete());
    }

    public void dumpAssociatedRules(Rete rete) {
        sb = new StringBuilder();
        Set<BaseNode> nodes = collect(rete);
        for (BaseNode node : nodes) {
            String ruleNames = Arrays.stream(node.getAssociatedRules()).map(Rule::getName)
                                     .collect(Collectors.joining(", "));
            sb.append(node + " : [" + ruleNames + "]\n");
        }
        printResults();
    }
}
