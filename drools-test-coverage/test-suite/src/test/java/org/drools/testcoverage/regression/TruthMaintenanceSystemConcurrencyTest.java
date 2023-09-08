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
package org.drools.testcoverage.regression;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
import org.drools.mvel.compiler.Cheese;
import org.junit.Test;
import org.kie.api.definition.KiePackage;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;


public class TruthMaintenanceSystemConcurrencyTest {

    @Test
    public void testUsingMultipleSessionsConcurrently() throws InterruptedException {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(
            ResourceFactory.newClassPathResource(
                "test_concurrency.drl",
                getClass()
            ),
            ResourceType.DRL
        );
        Collection<KiePackage> kpkgs = kbuilder.getKnowledgePackages();

        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kpkgs);

        final ExecutorService executorService = Executors.newFixedThreadPool(20);

        final Collection<Throwable> errors =
            Collections.synchronizedCollection(new LinkedList<>());
        for (int i = 0; i < 2000; i++) {
            executorService.submit(() -> {
                try {
                    StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl) kbase.newKieSession();

                    ksession.fireAllRules();
                    assertThat(ksession.getObjects(new ClassObjectFilter(Cheese.class)))
                        .hasSize(2);
                } catch (Throwable e) {
                    errors.add(e);
                }
            });
        }
        executorService.shutdown();
        assertThat(executorService.awaitTermination(2, TimeUnit.SECONDS))
            .isTrue();

        assertThat(errors)
            .isEmpty();
    }

}