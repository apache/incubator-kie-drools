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
package org.drools.mvel.integrationtests;

import org.drools.base.common.NetworkNode;
import org.drools.core.impl.InternalRuleBase;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.Rete;
import org.drools.core.reteoo.RightInputAdapterNode;
import org.drools.core.reteoo.RuleTerminalNode;
import org.kie.api.KieBase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NetworkVisitor {

    public void debugNetworkStructure(KieBase kbase) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                    NETWORK STRUCTURE DEBUG                  ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        Rete rete = ((InternalRuleBase) kbase).getRete();
        List<ObjectTypeNode> objectTypeNodes = rete.getObjectTypeNodes();

        // Collect bugs during traversal
        List<String> bugs = new ArrayList<>();

        System.out.println("\n📊 ObjectTypeNodes found: " + objectTypeNodes.size());
        System.out.println("┌─────────────────────────────────────────────────────────────┐");

        for (ObjectTypeNode otn : objectTypeNodes) {
            String className = otn.getObjectType().toString();
            if (className.contains("class=")) {
                className = className.substring(className.lastIndexOf('.') + 1, className.lastIndexOf(']'));
            }
            System.out.println("│ 🏭 OTN: " + className + " (id=" + otn.getId() + ")");

            debugSinks(otn.getObjectSinkPropagator().getSinks(), "│   ", new java.util.HashSet<>(), bugs);
            System.out.println("│");
        }
        System.out.println("└─────────────────────────────────────────────────────────────┘");

        // Report bugs summary
        if (!bugs.isEmpty()) {
            System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
            System.out.println("║              ⚠️  NETWORK BUGS DETECTED (" + bugs.size() + ")                    ║");
            System.out.println("╚══════════════════════════════════════════════════════════════╝");
            for (int i = 0; i < bugs.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + bugs.get(i));
            }
            System.out.println();
        }
    }

    private void debugSinks(Object[] sinks, String indent, java.util.Set<Integer> visited, List<String> bugs) {
        if (sinks == null) return;

        for (int i = 0; i < sinks.length; i++) {
            Object sink = sinks[i];
            int nodeId = ((NetworkNode) sink).getId();
            boolean isLast = (i == sinks.length - 1);
            String branch = isLast ? "└─" : "├─";
            String nextIndent = indent + (isLast ? "  " : "│ ");

            String sinkIcon = getSinkIcon(sink);
            String nodeInfo = getDetailedNodeInfo(sink, bugs);

            // Check for cycle
            if (visited.contains(nodeId)) {
                System.out.println(indent + branch + " ↺ [Already visited: " + sink.getClass().getSimpleName() +
                        " (id=" + nodeId + ")]");
                continue;
            }
            visited.add(nodeId);

            System.out.println(indent + branch + " " + sinkIcon + " " + sink.getClass().getSimpleName() +
                    " (id=" + nodeId + ")" + nodeInfo);

            visitSink(nextIndent, sink, visited, bugs);
        }
    }

    private String getDetailedNodeInfo(Object sink, List<String> bugs) {
        if (sink instanceof AlphaNode alphaNode) {
            return " 🔍 [" + alphaNode.getConstraint() + "]";
        } else if (sink.getClass().getSimpleName().equals("BiLinearJoinNode")) {
            // Handle BiLinearJoinNode specifically - show left and right input IDs
            try {
                int nodeId = ((NetworkNode) sink).getId();
                Object leftInput = sink.getClass().getMethod("getLeftTupleSource").invoke(sink);
                Object secondInput = sink.getClass().getMethod("getSecondLeftInput").invoke(sink);
                int leftId = leftInput != null ? ((NetworkNode) leftInput).getId() : -1;
                int rightId = secondInput != null ? ((NetworkNode) secondInput).getId() : -1;

                // Check for self-reference BUG
                StringBuilder info = new StringBuilder(" 🔗 [left=" + leftId + ", right=" + rightId + "]");
                if (rightId == nodeId) {
                    String bugMsg = "BiLinearJoinNode (id=" + nodeId + ") references ITSELF as secondLeftInput (right=" + rightId + ")";
                    info.append(" ⚠️ BUG: SELF-REFERENCE!");
                    if (bugs != null) bugs.add(bugMsg);
                }
                if (leftId == nodeId) {
                    String bugMsg = "BiLinearJoinNode (id=" + nodeId + ") references ITSELF as leftInput (left=" + leftId + ")";
                    info.append(" ⚠️ BUG: SELF-REFERENCE!");
                    if (bugs != null) bugs.add(bugMsg);
                }
                return info.toString();
            } catch (Exception e) {
                return " 🔗 [error getting inputs: " + e.getMessage() + "]";
            }
        } else if (sink instanceof JoinNode joinNode) {
            if (joinNode.getConstraints().length == 0) {
                return " 🔗 [no constraints]";
            } else {
                String constraints = String.join(", ",
                        java.util.Arrays.stream(joinNode.getConstraints())
                                .map(Object::toString)
                                .toArray(String[]::new));
                return " 🔗 [" + constraints + "]";
            }
        } else if (sink instanceof RuleTerminalNode rtn) {
            return " 🏁 [" + rtn.getRule().getName() + "]";
        }
        return "";
    }

    private void visitSink(String indent, Object sink, java.util.Set<Integer> visited, List<String> bugs) {
        if (sink instanceof AlphaNode) {
            AlphaNode alphaNode = (AlphaNode) sink;
            debugSinks(alphaNode.getObjectSinkPropagator().getSinks(), indent, visited, bugs);
        } else if (sink instanceof LeftTupleSource lts) {
            // Generic handling for ALL LeftTupleSource types:
            // JoinNode, EvalConditionNode, NotNode, ExistsNode, FromNode, LeftInputAdapterNode, etc.
            LeftTupleSink[] sinks = lts.getSinkPropagator().getSinks();
            if (sinks != null && sinks.length > 0) {
                for (int j = 0; j < sinks.length; j++) {
                    LeftTupleSink js = sinks[j];
                    int nodeId = js.getId();
                    boolean isLastSink = (j == sinks.length - 1);
                    String branch = isLastSink ? "└─" : "├─";
                    String nextIndent = indent + (isLastSink ? "  " : "│ ");

                    // Check for cycle
                    if (visited.contains(nodeId)) {
                        System.out.println(indent + branch + " ↺ [Already visited: " + js.getClass().getSimpleName() +
                                " (id=" + nodeId + ")]");
                        continue;
                    }
                    visited.add(nodeId);

                    String jsIcon = getSinkIcon(js);
                    String jsInfo = getDetailedNodeInfo(js, bugs);

                    System.out.println(indent + branch + " " + jsIcon + " " + js.getClass().getSimpleName() +
                            " (id=" + js.getId() + ")" + jsInfo);

                    if (!(js instanceof RuleTerminalNode)) {
                        visitSink(nextIndent, js, visited, bugs);
                    }
                }
            }
        } else if (sink instanceof RightInputAdapterNode right) {
            System.out.println(indent + "├─ ➡️  Adapts to: " + right.getBetaNode().getClass().getSimpleName() +
                    " (id=" + right.getBetaNode().getId() + ")");
        }
    }

    private String getSinkIcon(Object sink) {
        if (sink instanceof LeftInputAdapterNode) return "⬅️";
        if (sink.getClass().getSimpleName().equals("BiLinearJoinNode")) return "🔗";
        if (sink instanceof JoinNode) return "🔗";
        if (sink instanceof RightInputAdapterNode) return "➡️";
        if (sink instanceof AlphaNode) return "🔍";
        if (sink instanceof RuleTerminalNode) return "🏁";
        if (sink instanceof ObjectTypeNode) return "📦";
        return "⚪";
    }

    /**
     * Finds all BiLinearJoinNodes in the network.
     * @param kbase The KieBase to search
     * @return List of BiLinearJoinNode instances (as NetworkNode)
     */
    public List<NetworkNode> findBiLinearJoinNodes(KieBase kbase) {
        List<NetworkNode> biLinearNodes = new ArrayList<>();
        Rete rete = ((InternalRuleBase) kbase).getRete();
        collectBiLinearNodes(rete, biLinearNodes, new HashSet<>());
        return biLinearNodes;
    }

    private void collectBiLinearNodes(NetworkNode node, List<NetworkNode> biLinearNodes, Set<Integer> visited) {
        if (visited.contains(node.getId())) {
            return;
        }
        visited.add(node.getId());

        String className = node.getClass().getSimpleName();
        if (className.equals("BiLinearJoinNode")) {
            biLinearNodes.add(node);
        }

        // Traverse children based on node type
        // Check Rete and EntryPointNode FIRST before ObjectSource since they have specialized handling
        if (node instanceof Rete) {
            for (EntryPointNode epn : ((Rete) node).getEntryPointNodes().values()) {
                collectBiLinearNodes(epn, biLinearNodes, visited);
            }
        } else if (node instanceof EntryPointNode) {
            for (ObjectTypeNode otn : ((EntryPointNode) node).getObjectTypeNodes().values()) {
                collectBiLinearNodes(otn, biLinearNodes, visited);
            }
        } else if (node instanceof ObjectSource) {
            for (Object sink : ((ObjectSource) node).getObjectSinkPropagator().getSinks()) {
                collectBiLinearNodes((NetworkNode) sink, biLinearNodes, visited);
            }
        }

        // Also check LeftTupleSource sinks (not else-if, since some nodes are both)
        if (node instanceof LeftTupleSource) {
            LeftTupleSink[] sinks = ((LeftTupleSource) node).getSinkPropagator().getSinks();
            if (sinks != null) {
                for (LeftTupleSink sink : sinks) {
                    collectBiLinearNodes(sink, biLinearNodes, visited);
                }
            }
        }
    }

}