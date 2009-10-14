/**
 * 
 */
package org.drools.vsm.local;

import java.util.Properties;

import org.drools.KnowledgeBase;
import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.KnowledgeBuilderProvider;

public class KnowledgeBuilderProviderLocalClient
    implements
    KnowledgeBuilderProvider {

    public DecisionTableConfiguration newDecisionTableConfiguration() {
        return KnowledgeBuilderFactory.newDecisionTableConfiguration();
    }

    public KnowledgeBuilder newKnowledgeBuilder() {
        return KnowledgeBuilderFactory.newKnowledgeBuilder();
    }

    public KnowledgeBuilder newKnowledgeBuilder(KnowledgeBuilderConfiguration conf) {
        return KnowledgeBuilderFactory.newKnowledgeBuilder( conf );
    }

    public KnowledgeBuilder newKnowledgeBuilder(KnowledgeBase kbase) {
        return KnowledgeBuilderFactory.newKnowledgeBuilder( kbase );
    }

    public KnowledgeBuilder newKnowledgeBuilder(KnowledgeBase kbase,
                                                KnowledgeBuilderConfiguration conf) {
        return KnowledgeBuilderFactory.newKnowledgeBuilder( kbase,
                                                            conf );
    }

    public KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration() {
        return KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
    }

    public KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration(Properties properties,
                                                                          ClassLoader classLoader) {
        return KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration( properties,
                                                                         classLoader );
    }

}