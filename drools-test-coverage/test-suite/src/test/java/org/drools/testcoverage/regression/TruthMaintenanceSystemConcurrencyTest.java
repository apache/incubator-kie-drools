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