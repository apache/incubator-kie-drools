package org.drools.beliefs.bayes.integration;

import org.drools.beliefs.bayes.JunctionTree;
import org.drools.beliefs.bayes.assembler.BayesPackage;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.ResourceTypePackageRegistry;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.internal.io.ResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class WeaverTest {

    @Test
    public void testBayesPackageWeaving() throws Exception {
        KnowledgeBuilderImpl kbuilder = new KnowledgeBuilderImpl();
        kbuilder.add( ResourceFactory.newClassPathResource("Garden.xmlbif", AssemblerTest.class), ResourceType.BAYES );


        InternalKnowledgeBase kbase = getKnowledgeBase();
        kbase.addPackages( kbuilder.getKnowledgePackages() );

        InternalKnowledgePackage kpkg = (InternalKnowledgePackage) kbase.getKiePackage("org.drools.beliefs.bayes.integration");
        ResourceTypePackageRegistry map = kpkg.getResourceTypePackages();
        BayesPackage existing  = (BayesPackage) map.get( ResourceType.BAYES );
        JunctionTree jtree =  existing.getJunctionTree("Garden");
        assertThat(jtree).isNotNull();
    }

    protected InternalKnowledgeBase getKnowledgeBase() {
        return KnowledgeBaseFactory.newKnowledgeBase();
    }
}
