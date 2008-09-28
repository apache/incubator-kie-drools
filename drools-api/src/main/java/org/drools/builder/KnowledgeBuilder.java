package org.drools.builder;

import java.util.Collection;

import org.drools.knowledge.definitions.KnowledgePackage;

public interface KnowledgeBuilder extends RuleBuilder, ProcessBuilder {
	
	Collection<KnowledgePackage> getKnowledgePackages();
}
