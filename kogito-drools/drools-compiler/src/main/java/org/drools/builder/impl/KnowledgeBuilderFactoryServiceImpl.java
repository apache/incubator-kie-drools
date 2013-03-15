package org.drools.builder.impl;

import java.util.Properties;

import org.drools.builder.conf.impl.DecisionTableConfigurationImpl;
import org.drools.builder.conf.impl.JaxbConfigurationImpl;
import org.drools.compiler.compiler.PackageBuilder;
import org.drools.compiler.compiler.PackageBuilderConfiguration;
import org.drools.impl.KnowledgeBaseImpl;
import org.kie.KnowledgeBase;
import org.kie.builder.DecisionTableConfiguration;
import org.kie.builder.JaxbConfiguration;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderConfiguration;
import org.kie.builder.KnowledgeBuilderFactoryService;

import com.sun.tools.xjc.Options;

public class KnowledgeBuilderFactoryServiceImpl implements KnowledgeBuilderFactoryService {
    
    public KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration() {
        return new PackageBuilderConfiguration();
    }
    
    public KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration(Properties properties, ClassLoader... classLoaders) {
        return new PackageBuilderConfiguration(properties, classLoaders);
    }
    
    public DecisionTableConfiguration newDecisionTableConfiguration() {
        return new DecisionTableConfigurationImpl();
    }
    
    public KnowledgeBuilder newKnowledgeBuilder() {
        return new KnowledgeBuilderImpl( new PackageBuilder() );
    }

    public KnowledgeBuilder newKnowledgeBuilder(KnowledgeBuilderConfiguration conf) {
        return new KnowledgeBuilderImpl( new PackageBuilder( (PackageBuilderConfiguration) conf ) );
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
            return new KnowledgeBuilderImpl(new PackageBuilder( (PackageBuilderConfiguration) conf ) );            
        }        
    }

    public JaxbConfiguration newJaxbConfiguration(Options xjcOpts,
                                                  String systemId) {
        return new JaxbConfigurationImpl( xjcOpts, systemId );
    }
}
