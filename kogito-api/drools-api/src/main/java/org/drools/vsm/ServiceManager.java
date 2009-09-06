package org.drools.vsm;

import java.util.Collection;

import org.drools.KnowledgeBaseProvider;
import org.drools.agent.KnowledgeAgentProvider;
import org.drools.builder.KnowledgeBuilderProvider;
import org.drools.persistence.jpa.JPAKnowledgeServiceProvider;
import org.drools.runtime.CommandExecutor;
import org.drools.runtime.Environment;

public interface ServiceManager {
    KnowledgeBuilderProvider getKnowledgeBuilderFactory();

    KnowledgeBaseProvider getKnowledgeBaseFactory();

    KnowledgeAgentProvider getKnowledgeAgentFactory();
    
    JPAKnowledgeServiceProvider JPAKnowledgeService();

    void register(String identifier,
                  CommandExecutor executor);

    CommandExecutor lookup(String identifer);

    Collection<String> list();

    void release(Object object);

    void release(String identifier);
    
    Environment getEnvironment();
}
