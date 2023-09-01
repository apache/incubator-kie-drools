package org.drools.beliefs.bayes.integration;

import org.drools.beliefs.bayes.assembler.BayesPackage;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.internal.io.ResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class AssemblerTest {

    @Test
    public void testBayesPackageAssembly() throws Exception {
        KnowledgeBuilderImpl kbuilder = new KnowledgeBuilderImpl();
        kbuilder.add( ResourceFactory.newClassPathResource("Garden.xmlbif", AssemblerTest.class), ResourceType.BAYES );

        InternalKnowledgePackage kpkg = kbuilder.getPackageRegistry("org.drools.beliefs.bayes.integration").getPackage();
        BayesPackage bkpg = (BayesPackage) kpkg.getResourceTypePackages().get( ResourceType.BAYES );
        assertThat(bkpg.getJunctionTree("Garden")).isNotNull();
    }
}
