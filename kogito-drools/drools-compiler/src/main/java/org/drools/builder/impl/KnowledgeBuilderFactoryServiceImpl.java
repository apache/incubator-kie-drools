package org.drools.builder.impl;

import java.util.Properties;

import org.drools.KnowledgeBase;
import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.JaxbConfiguration;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactoryService;
import org.drools.builder.conf.impl.DecisionTableConfigurationImpl;
import org.drools.builder.conf.impl.JaxbConfigurationImpl;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.impl.KnowledgeBaseImpl;

import com.sun.tools.xjc.Options;

public class KnowledgeBuilderFactoryServiceImpl implements KnowledgeBuilderFactoryService {
    
    public KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration() {
        return new PackageBuilderConfiguration();
    }
    
    public KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration(Properties properties, ClassLoader classLoader) {
        return new PackageBuilderConfiguration(classLoader, properties);
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
        return new KnowledgeBuilderImpl( new PackageBuilder( ((KnowledgeBaseImpl) kbase).ruleBase ) );
    }

    public KnowledgeBuilder newKnowledgeBuilder(KnowledgeBase kbase,
                                                KnowledgeBuilderConfiguration conf) {
        return new KnowledgeBuilderImpl( new PackageBuilder( ((KnowledgeBaseImpl) kbase).ruleBase, (PackageBuilderConfiguration) conf ) );
    }

    public JaxbConfiguration newJaxbConfiguration(Options xjcOpts,
                                                  String systemId) {
        return new JaxbConfigurationImpl( xjcOpts, systemId );
    }
}
