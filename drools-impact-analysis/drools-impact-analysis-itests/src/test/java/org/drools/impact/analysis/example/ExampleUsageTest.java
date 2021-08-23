/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.impact.analysis.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.drools.impact.analysis.example.domain.Order;
import org.drools.impact.analysis.example.domain.Product;
import org.drools.impact.analysis.graph.Graph;
import org.drools.impact.analysis.graph.GraphCollapsionHelper;
import org.drools.impact.analysis.graph.ImpactAnalysisHelper;
import org.drools.impact.analysis.graph.ModelToGraphConverter;
import org.drools.impact.analysis.graph.TextReporter;
import org.drools.impact.analysis.graph.graphviz.GraphImageGenerator;
import org.drools.impact.analysis.integrationtests.RuleExecutionHelper;
import org.drools.impact.analysis.model.AnalysisModel;
import org.drools.impact.analysis.parser.internal.ImpactAnalysisKieModule;
import org.drools.impact.analysis.parser.internal.ImpactAnalysisProject;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.KieResources;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;

/**
 * This test demonstrates a typical usage of drools-impact-analysis. See README.md
 */
public class ExampleUsageTest {

    @Test
    public void testExampleUsage() throws IOException {

        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId("org.drools.impact.analysis.example", "order-process", "1.0.0");

        KieFileSystem kfs = createKieFileSystemWithClassPathResourceNames(releaseId, getClass(),
                                                                          "/org/drools/impact/analysis/example/CustomerCheck.xls",
                                                                          "/org/drools/impact/analysis/example/PriceCheck.xls",
                                                                          "/org/drools/impact/analysis/example/StatusCheck.xls",
                                                                          "/org/drools/impact/analysis/example/inventory.drl");

        // just to confirm this rule can run
        Order order = new Order(1, "Guitar", 6000, 65, 5);
        Product guitar = new Product("Guitar", 5500, 8);
        KieSession kieSession = RuleExecutionHelper.getKieSession(kfs);
        kieSession.insert(order);
        kieSession.insert(guitar);
        kieSession.fireAllRules();
        kieSession.dispose();

        KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll(ImpactAnalysisProject.class);
        ImpactAnalysisKieModule analysisKieModule = (ImpactAnalysisKieModule) kieBuilder.getKieModule();
        AnalysisModel analysisModel = analysisKieModule.getAnalysisModel();

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        // whole graph
        generateSvg(graph, "example-whole-graph");

        ImpactAnalysisHelper impactFilter = new ImpactAnalysisHelper();
        // Assume you want to modify RHS of PriceCheck_11
        Graph impactedSubGraph = impactFilter.filterImpactedNodes(graph, "org.drools.impact.analysis.example.PriceCheck_11");

        // changed node and impacted nodes
        generateSvg(impactedSubGraph, "example-impacted-sub-graph");

        // whole graph with impact coloring
        generateSvg(graph, "example-impacted-whole-graph");

        // Collapse graph based on rule name prefix (= RuleSet in spreadsheet)
        Graph collapsedGraph = new GraphCollapsionHelper().collapseWithRuleNamePrefix(graph);
        generateSvg(collapsedGraph, "example-collapsed-graph");

        // You can also do impact analysis for the collapsedGraph
        Graph impactedCollapsedSubGraph = impactFilter.filterImpactedNodes(collapsedGraph, "org.drools.impact.analysis.example.PriceCheck");
        generateSvg(impactedCollapsedSubGraph, "example-impacted-collapsed-sub-graph");

        // Simple text output
        System.out.println("--- toHierarchyText ---");
        String hierarchyText = TextReporter.toHierarchyText(impactedSubGraph);
        System.out.println(hierarchyText);

        System.out.println("--- toFlatText ---");
        String flatText = TextReporter.toFlatText(impactedSubGraph);
        System.out.println(flatText);
    }

    /*
     * You can modify this method to meet places where you have assets. It doesn't have to be ClassPathResource
     */
    private KieFileSystem createKieFileSystemWithClassPathResourceNames(ReleaseId releaseId, Class<?> classForClassLoader, String... resourceNames) throws IOException {
        KieServices ks = KieServices.Factory.get();
        KieResources kieResources = ks.getResources();
        List<Resource> resourceList = new ArrayList<>();
        for (String resourceName : resourceNames) {
            Resource resource = kieResources.newClassPathResource(resourceName, classForClassLoader);
            resource.setSourcePath("src/main/resources/" + resource.getSourcePath());
            resourceList.add(resource);
        }

        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writePomXML(RuleExecutionHelper.getPom(releaseId));
        for (Resource resource : resourceList.toArray(new Resource[]{})) {
            kfs.write(resource.getSourcePath(), resource);
        }
        return kfs;
    }

    protected void generateSvg(Graph graph, String fileName) {
        GraphImageGenerator generator = new GraphImageGenerator(fileName);

        //        generator.generateDot(graph);  // DOT : Quick. Can be visualized by other Graphviz tools
        generator.generateSvg(graph); // SVG : Quicker than PNG
        //                generator.generatePng(graph); // Slow. Probably not useful for a large number of rules (e.g. more than 200)
    }
}
