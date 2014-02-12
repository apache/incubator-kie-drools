package org.drools.compiler.builder.impl;

import java.util.Properties;

import org.drools.core.builder.conf.impl.DecisionTableConfigurationImpl;
import org.drools.core.builder.conf.impl.JaxbConfigurationImpl;
import org.drools.core.builder.conf.impl.ScoreCardConfigurationImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.JaxbConfiguration;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactoryService;

import com.sun.tools.xjc.Options;
import org.kie.internal.builder.ScoreCardConfiguration;

public class KnowledgeBuilderFactoryServiceImpl implements KnowledgeBuilderFactoryService {
    
    public KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration() {
        return new KnowledgeBuilderConfigurationImpl();
    }
    
    public KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration(Properties properties, ClassLoader... classLoaders) {
        return new KnowledgeBuilderConfigurationImpl(properties, classLoaders);
    }
    
    public DecisionTableConfiguration newDecisionTableConfiguration() {
        return new DecisionTableConfigurationImpl();
    }

    public ScoreCardConfiguration newScoreCardConfiguration() {
        return new ScoreCardConfigurationImpl();
    }

    public KnowledgeBuilder newKnowledgeBuilder() {
        return new KnowledgeBuilderImpl( );
    }

    public KnowledgeBuilder newKnowledgeBuilder(KnowledgeBuilderConfiguration conf) {
        return new KnowledgeBuilderImpl( (KnowledgeBuilderConfigurationImpl) conf );
    }

    public KnowledgeBuilder newKnowledgeBuilder(KnowledgeBase kbase) {
        if ( kbase != null ) {
            return new KnowledgeBuilderImpl( (InternalKnowledgeBase)kbase );
        } else {
            return new KnowledgeBuilderImpl();
        }
    }

    public KnowledgeBuilder newKnowledgeBuilder(KnowledgeBase kbase,
                                                KnowledgeBuilderConfiguration conf) {
        if ( kbase != null ) {
            return new KnowledgeBuilderImpl( (InternalKnowledgeBase)kbase, (KnowledgeBuilderConfigurationImpl) conf );
        } else {
            return new KnowledgeBuilderImpl((KnowledgeBuilderConfigurationImpl) conf );
        }        
    }

    public JaxbConfiguration newJaxbConfiguration(Options xjcOpts,
                                                  String systemId) {
        return new JaxbConfigurationImpl( xjcOpts, systemId );
    }
}
