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
package org.drools.impact.analysis.integrationtests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.drools.core.event.DefaultAgendaEventListener;
import org.drools.impact.analysis.graph.Graph;
import org.drools.impact.analysis.graph.Link;
import org.drools.impact.analysis.graph.Node;
import org.drools.impact.analysis.graph.ReactivityType;
import org.drools.impact.analysis.graph.graphviz.GraphImageGenerator;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.io.KieResources;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class AbstractGraphTest {

    private static final Logger logger = LoggerFactory.getLogger(AbstractGraphTest.class);

    @Rule
    public TestName testName = new TestName();

    protected String getTestMethodName() {
        return testName.getMethodName();
    }

    // Keep this method for test convenience
    protected void generatePng(Graph graph) {
        generatePng(graph, "");
    }

    // Keep this method for test convenience
    protected void generateSvg(Graph graph) {
        generateSvg(graph, "");
    }

    // Keep this method for test convenience
    protected void generatePng(Graph graph, String suffix) {
        GraphImageGenerator generator = new GraphImageGenerator(getTestMethodName() + suffix);
        generator.generatePng(graph);
    }

    // Keep this method for test convenience
    protected void generateSvg(Graph graph, String suffix) {
        GraphImageGenerator generator = new GraphImageGenerator(getTestMethodName() + suffix);
        generator.generateSvg(graph);
    }

    /**
     * Assert that there are exact links with the types between source node and target node.
     * If no expectedTypes, it means there is no link
     */
    protected void assertLink(Graph graph, String sourceFqdn, String targetFqdn, ReactivityType... expectedTypes) {
        Node source = graph.getNodeMap().get(sourceFqdn);
        Node target = graph.getNodeMap().get(targetFqdn);
        List<Link> outgoingLinks = source.getOutgoingLinks().stream().filter(l -> l.getTarget().equals(target)).collect(Collectors.toList());
        List<Link> incomingLinks = target.getIncomingLinks().stream().filter(l -> l.getSource().equals(source)).collect(Collectors.toList());
        assertThat(outgoingLinks).hasSameElementsAs(incomingLinks);

        List<ReactivityType> outgoingLinkTypelist = outgoingLinks.stream().map(l -> l.getReactivityType()).collect(Collectors.toList());
        List<ReactivityType> expectedTypeList = Arrays.asList(expectedTypes);
        assertThat(outgoingLinkTypelist).hasSameElementsAs(expectedTypeList);
    }

    /*
     * Only for test development convenience (to confirm if the rule is valid)
     */
    protected void runRule(String drl, Object... facts) {
        final KieSession ksession = RuleExecutionHelper.getKieSession(drl);
        runRule(ksession, facts);
    }

    protected void runRule(final KieSession ksession, Object... facts) {
        ksession.addEventListener(new DefaultAgendaEventListener() {

            @Override
            public void afterMatchFired(AfterMatchFiredEvent event) {
                logger.info(event.getMatch().getRule().getName() + " : fired");
            }
        });
        for (Object fact : facts) {
            ksession.insert(fact);
        }
        int fired = ksession.fireAllRules(100);
        logger.info("fired = " + fired);
        ksession.dispose();
    }

    protected void runRuleWithGlobal(String drl, String globalName, Object global, Object... facts) {
        final KieSession ksession = RuleExecutionHelper.getKieSession(drl);
        ksession.setGlobal(globalName, global);
        runRule(ksession, facts);
    }

    protected void runRule(KieFileSystem kfs, Object... facts) {
        final KieSession ksession = RuleExecutionHelper.getKieSession(kfs);
        runRule(ksession, facts);
    }

    protected KieFileSystem createKieFileSystemWithClassPathResourceNames(ReleaseId releaseId, Class<?> classForClassLoader, String... resourceNames) throws IOException {
        KieServices ks = KieServices.Factory.get();
        KieResources kieResources = ks.getResources();
        List<Resource> resourceList = new ArrayList<>();
        for (String resourceName : resourceNames) {
            Resource resource = kieResources.newClassPathResource(resourceName, classForClassLoader);
            resource.setSourcePath("src/main/resources/" + resource.getSourcePath());
            resourceList.add(resource);
        }
        return createKieFileSystem(releaseId, resourceList.toArray(new Resource[]{}));
    }

    protected KieFileSystem createKieFileSystem(ReleaseId releaseId, Resource... resources) throws IOException {
        KieServices ks = KieServices.get();
        KieFileSystem kfs = ks.newKieFileSystem();

        kfs.writePomXML(RuleExecutionHelper.getPom(releaseId));

        for (Resource resource : resources) {
            kfs.write(resource.getSourcePath(), resource);
        }
        return kfs;
    }
}
