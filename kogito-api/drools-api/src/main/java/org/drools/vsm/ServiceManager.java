package org.drools.vsm;

import java.util.Collection;

import org.drools.KnowledgeBaseFactoryService;
import org.drools.agent.KnowledgeAgentProvider;
import org.drools.builder.KnowledgeBuilderFactoryService;
import org.drools.persistence.jpa.KnowledgeStoreService;
import org.drools.runtime.CommandExecutor;
import org.drools.runtime.Environment;

public interface ServiceManager extends CommandExecutor {
    KnowledgeBuilderFactoryService getKnowledgeBuilderFactoryService();

    KnowledgeBaseFactoryService getKnowledgeBaseFactoryService();

    KnowledgeAgentProvider getKnowledgeAgentFactory();
    
    KnowledgeStoreService JPAKnowledgeService();

    HumanTaskServiceProvider getHumanTaskService();

    void register(String identifier,
                  CommandExecutor executor);

    CommandExecutor lookup(String identifer);

    Collection<String> list();

    void release(Object object);

    void release(String identifier);
    
    Environment getEnvironment();
}
