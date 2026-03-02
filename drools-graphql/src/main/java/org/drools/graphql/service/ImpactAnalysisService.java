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
package org.drools.graphql.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.drools.graphql.dto.ImpactAnalysisReport;
import org.drools.graphql.dto.ImpactedRuleInfo;
import org.drools.impact.analysis.graph.Graph;
import org.drools.impact.analysis.graph.ImpactAnalysisHelper;
import org.drools.impact.analysis.graph.ModelToGraphConverter;
import org.drools.impact.analysis.graph.Node;
import org.drools.impact.analysis.graph.ReactivityType;
import org.drools.impact.analysis.model.AnalysisModel;
import org.drools.impact.analysis.parser.ModelBuilder;

/**
 * Service that performs forward and backward impact analysis on DRL rules
 * using the drools-impact-analysis infrastructure.
 *
 * <p>The analysis builds a dependency graph from DRL source, then queries
 * which rules are impacted by (or impact) a target rule.
 */
public class ImpactAnalysisService {

    private final AnalysisModel model;
    private final Graph graph;

    /**
     * Build the analysis model and graph from DRL source strings.
     */
    public ImpactAnalysisService(String... drlSources) {
        ModelBuilder modelBuilder = new ModelBuilder();
        this.model = modelBuilder.build(drlSources);
        ModelToGraphConverter converter = new ModelToGraphConverter();
        this.graph = converter.toGraph(model);
    }

    /**
     * Build from a pre-built model and graph (for injection/testing).
     */
    public ImpactAnalysisService(AnalysisModel model, Graph graph) {
        this.model = model;
        this.graph = graph;
    }

    /**
     * Analyze which rules are impacted if the given rule changes,
     * and which rules impact the given rule.
     */
    public ImpactAnalysisReport analyze(String targetRuleName) {
        Node targetNode = findNode(targetRuleName);
        if (targetNode == null) {
            ImpactAnalysisReport report = new ImpactAnalysisReport();
            report.setTargetRule(targetRuleName);
            report.setImpactedRules(new ArrayList<>());
            report.setImpactingRules(new ArrayList<>());
            report.setTotalImpacted(0);
            report.setTotalImpacting(0);
            return report;
        }

        ImpactAnalysisHelper helper = new ImpactAnalysisHelper();
        Graph forwardGraph = helper.filterImpactedNodes(graph, targetNode);
        Graph backwardGraph = helper.filterImpactingNodes(graph, targetNode);

        List<ImpactedRuleInfo> impacted = extractImpactedRules(forwardGraph, targetRuleName);
        List<ImpactedRuleInfo> impacting = extractImpactedRules(backwardGraph, targetRuleName);

        ImpactAnalysisReport report = new ImpactAnalysisReport();
        report.setTargetRule(targetRuleName);
        report.setTargetPackage(extractPackage(targetNode));
        report.setImpactedRules(impacted);
        report.setImpactingRules(impacting);
        report.setTotalImpacted(impacted.size());
        report.setTotalImpacting(impacting.size());
        return report;
    }

    public List<String> getAllAnalyzedRuleNames() {
        return graph.getNodeMap().values().stream()
                .map(Node::getRuleName)
                .sorted()
                .collect(Collectors.toList());
    }

    public Graph getGraph() {
        return graph;
    }

    private Node findNode(String ruleName) {
        return graph.getNodeMap().values().stream()
                .filter(n -> ruleName.equals(n.getRuleName()))
                .findFirst()
                .orElse(null);
    }

    private static List<ImpactedRuleInfo> extractImpactedRules(Graph filteredGraph, String excludeName) {
        return filteredGraph.getNodeMap().values().stream()
                .filter(n -> !excludeName.equals(n.getRuleName()))
                .map(n -> {
                    String reactivity = n.getStatus() != null ? n.getStatus().toString() : ReactivityType.UNKNOWN.toString();
                    String pkg = extractPackage(n);
                    return new ImpactedRuleInfo(pkg, n.getRuleName(), reactivity);
                })
                .collect(Collectors.toList());
    }

    private static String extractPackage(Node node) {
        String pkg = node.getPackageName();
        return pkg != null ? pkg : "";
    }
}
