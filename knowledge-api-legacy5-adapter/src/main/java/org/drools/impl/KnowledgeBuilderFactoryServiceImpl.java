package org.drools.impl;

import java.util.Properties;

import org.drools.KnowledgeBase;
import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.JaxbConfiguration;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactoryService;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.core.builder.conf.impl.JaxbConfigurationImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.impl.adapters.KnowledgeBuilderConfigurationAdapter;

import com.sun.tools.xjc.Options;

public class KnowledgeBuilderFactoryServiceImpl implements KnowledgeBuilderFactoryService {

    public KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration() {
        return new KnowledgeBuilderConfigurationAdapter(new KnowledgeBuilderConfigurationImpl());
    }

    public KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration(Properties properties, ClassLoader... classLoaders) {
        return new KnowledgeBuilderConfigurationAdapter(new KnowledgeBuilderConfigurationImpl(properties, classLoaders));
    }

    public DecisionTableConfiguration newDecisionTableConfiguration() {
        return new DecisionTableConfigurationImpl();
    }

    public KnowledgeBuilder newKnowledgeBuilder() {
        return new KnowledgeBuilderImpl( );
    }

    public KnowledgeBuilder newKnowledgeBuilder(KnowledgeBuilderConfiguration conf) {
        return new KnowledgeBuilderImpl( (KnowledgeBuilderConfigurationImpl) ((KnowledgeBuilderConfigurationAdapter)conf).getDelegate() );
    }

    public KnowledgeBuilder newKnowledgeBuilder(KnowledgeBase kbase) {
        if ( kbase != null ) {
            return new KnowledgeBuilderImpl( (InternalKnowledgeBase) kbase );
        } else {
            return new KnowledgeBuilderImpl( );
        }
    }

    public KnowledgeBuilder newKnowledgeBuilder(KnowledgeBase kbase,
                                                KnowledgeBuilderConfiguration conf) {
        if ( kbase != null ) {
            return new KnowledgeBuilderImpl( (InternalKnowledgeBase) kbase, (KnowledgeBuilderConfigurationImpl) conf );
        } else {
            return new KnowledgeBuilderImpl((KnowledgeBuilderConfigurationImpl) ((KnowledgeBuilderConfigurationAdapter)conf).getDelegate() );
        }
    }

    public JaxbConfiguration newJaxbConfiguration(Options xjcOpts,
                                                  String systemId) {
        return new org.drools.impl.JaxbConfigurationImpl( xjcOpts, systemId );
    }
}
