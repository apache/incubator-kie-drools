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
package org.drools.core.reteoo;

import org.drools.base.common.NetworkNode;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.reteoo.NodeTypeEnums;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RuleNameExtractor {

    public static String getRuleNames(ObjectSink[] sinks) {
        Set<String> ruleNames = new HashSet<>();
        for (RuleImpl rule : getAssociatedRules(sinks)) {
            ruleNames.add(rule.getName());
        }
        return ruleNames.toString();
    }

    private static ArrayList<RuleImpl> getAssociatedRules(ObjectSink[] sinks) {
        ArrayList<RuleImpl> rules = new ArrayList<>();
        for (ObjectSink osink : sinks) {
            LeftTupleSinkPropagator sinkPropagator;
            if (osink instanceof BetaNode) {
                sinkPropagator = ((BetaNode) osink).getSinkPropagator();
            } else if (osink instanceof RightInputAdapterNode) {
                sinkPropagator = ((RightInputAdapterNode<?>) osink).getBetaNode().getSinkPropagator();
            } else {
                continue; // Skip unsupported node types
            }

            for (LeftTupleSink ltsink : sinkPropagator.getSinks()) {
                findAndAddTN(ltsink, rules);
            }
        }
        return rules;
    }

    private static void findAndAddTN(LeftTupleSink ltsink, List<RuleImpl> terminalNodes) {
        if (NodeTypeEnums.isTerminalNode(ltsink)) {
            terminalNodes.add(((TerminalNode) ltsink).getRule());
        } else if (ltsink.getType() == NodeTypeEnums.TupleToObjectNode) {
            for (NetworkNode childSink : (ltsink).getSinks()) {
                findAndAddTN((LeftTupleSink) childSink, terminalNodes);
            }
        } else {
            for (LeftTupleSink childLtSink : (ltsink).getSinkPropagator().getSinks()) {
                findAndAddTN(childLtSink, terminalNodes);
            }
        }
    }
}
