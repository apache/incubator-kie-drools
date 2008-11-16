package org.drools.builder.impl;

import java.util.Properties;

import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderProvider;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;

public class KnowledgeBuilderProviderImpl implements KnowledgeBuilderProvider {
    
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
}
