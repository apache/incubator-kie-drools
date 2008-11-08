package org.drools.builder;

import java.io.Reader;
import java.net.URL;
import java.util.Collection;

import org.drools.definition.KnowledgePackage;

public interface KnowledgeBuilder extends RuleBuilder, ProcessBuilder {
    public void addResorce(URL url, KnowledgeType type);    
    public void addResource(Reader reader, KnowledgeType type);
    
	Collection<KnowledgePackage> getKnowledgePackages();
}
