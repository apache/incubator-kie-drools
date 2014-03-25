package org.drools.impl;

import java.util.Properties;

import org.drools.KnowledgeBase;
import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.JaxbConfiguration;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactoryService;
import org.drools.compiler.compiler.PackageBuilder;
import org.drools.compiler.compiler.PackageBuilderConfiguration;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.impl.adapters.KnowledgeBuilderConfigurationAdapter;

import com.sun.tools.xjc.Options;

public class KnowledgeBuilderFactoryServiceImpl implements KnowledgeBuilderFactoryService {

    public KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration() {
        return new KnowledgeBuilderConfigurationAdapter(new PackageBuilderConfiguration());
    }

    public KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration(Properties properties, ClassLoader... classLoaders) {
        return new KnowledgeBuilderConfigurationAdapter(new PackageBuilderConfiguration(properties, classLoaders));
    }

    public DecisionTableConfiguration newDecisionTableConfiguration() {
        return new DecisionTableConfigurationImpl();
    }

    public KnowledgeBuilder newKnowledgeBuilder() {
        return new KnowledgeBuilderImpl( new PackageBuilder() );
    }

    public KnowledgeBuilder newKnowledgeBuilder(KnowledgeBuilderConfiguration conf) {
        return new KnowledgeBuilderImpl( new PackageBuilder( conf == null ? null : (PackageBuilderConfiguration) ((KnowledgeBuilderConfigurationAdapter)conf).getDelegate() ) );
    }

    public KnowledgeBuilder newKnowledgeBuilder(KnowledgeBase kbase) {
        if ( kbase != null ) {
            return new KnowledgeBuilderImpl( new PackageBuilder( ((KnowledgeBaseImpl) kbase).ruleBase ) );
        } else {
            return new KnowledgeBuilderImpl( new PackageBuilder() );
        }
    }

    public KnowledgeBuilder newKnowledgeBuilder(KnowledgeBase kbase,
                                                KnowledgeBuilderConfiguration conf) {
        if ( kbase != null ) {
            return new KnowledgeBuilderImpl( new PackageBuilder( ((KnowledgeBaseImpl) kbase).ruleBase, (PackageBuilderConfiguration) conf ) );
        } else {
            return new KnowledgeBuilderImpl(new PackageBuilder( conf == null ? null : (PackageBuilderConfiguration) ((KnowledgeBuilderConfigurationAdapter)conf).getDelegate() ) );
        }
    }

    public JaxbConfiguration newJaxbConfiguration(Options xjcOpts,
                                                  String systemId) {
        return new org.drools.impl.JaxbConfigurationImpl( xjcOpts, systemId );
    }
}
