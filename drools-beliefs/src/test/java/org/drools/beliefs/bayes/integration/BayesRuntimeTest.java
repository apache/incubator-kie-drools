package org.drools.beliefs.bayes.integration;

import org.drools.beliefs.bayes.BayesInstance;
import org.drools.beliefs.bayes.runtime.BayesRuntime;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.junit.Test;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.conf.RuleEngineOption;
import org.kie.internal.io.ResourceFactory;

import static org.drools.beliefs.bayes.JunctionTreeTest.scaleDouble;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BayesRuntimeTest {


    @Test
    public void testBayesRuntimeManager() throws Exception {
        KnowledgeBuilderImpl kbuilder = new KnowledgeBuilderImpl();
        kbuilder.add( ResourceFactory.newClassPathResource("Garden.xmlbif", AssemblerTest.class), ResourceType.BAYES );


        KnowledgeBase kbase = getKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl) kbase.newStatefulKnowledgeSession();

        BayesRuntime bayesRuntime = ksession.getKieRuntime(BayesRuntime.class);
        BayesInstance<Garden> instance = bayesRuntime.createInstance(Garden.class);
        assertNotNull(  instance );
    }

    protected KnowledgeBase getKnowledgeBase() {
        KieBaseConfiguration kBaseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kBaseConfig.setOption(RuleEngineOption.PHREAK);
        return getKnowledgeBase(kBaseConfig);
    }

    protected KnowledgeBase getKnowledgeBase(KieBaseConfiguration kBaseConfig) {
        kBaseConfig.setOption(RuleEngineOption.PHREAK);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kBaseConfig);
        return kbase;
    }


}
