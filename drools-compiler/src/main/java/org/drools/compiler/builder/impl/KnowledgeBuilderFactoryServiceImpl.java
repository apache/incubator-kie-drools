package org.drools.compiler.builder.impl;

import java.util.Properties;

import org.drools.core.builder.conf.impl.DecisionTableConfigurationImpl;
import org.drools.core.builder.conf.impl.JaxbConfigurationImpl;
import org.drools.compiler.compiler.PackageBuilder;
import org.drools.compiler.compiler.PackageBuilderConfiguration;
import org.drools.core.builder.conf.impl.ScoreCardConfigurationImpl;
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
        return new PackageBuilderConfiguration();
    }
    
    public KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration(Properties properties, ClassLoader... classLoaders) {
        return new PackageBuilderConfiguration(properties, classLoaders);
    }
    
    public DecisionTableConfiguration newDecisionTableConfiguration() {
        return new DecisionTableConfigurationImpl();
    }

    public ScoreCardConfiguration newScoreCardConfiguration() {
        return new ScoreCardConfigurationImpl();
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
