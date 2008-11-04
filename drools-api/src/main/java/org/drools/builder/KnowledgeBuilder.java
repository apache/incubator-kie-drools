package org.drools.builder;

import java.util.Collection;

import org.drools.definition.KnowledgePackage;

public interface KnowledgeBuilder extends RuleBuilder, ProcessBuilder {
	
	Collection<KnowledgePackage> getKnowledgePackages();
}
