package org.drools.vsm.local;

import java.util.Collection;
import java.util.Properties;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseProvider;
import org.drools.agent.KnowledgeAgentProvider;
import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderProvider;
import org.drools.command.Command;
import org.drools.persistence.jpa.JPAKnowledgeServiceProvider;
import org.drools.persistence.jpa.impl.JPAKnowledgeServiceProviderImpl;
import org.drools.runtime.CommandExecutor;
import org.drools.runtime.Environment;
import org.drools.runtime.ExecutionResults;
import org.drools.vsm.HumanTaskServiceProvider;
import org.drools.vsm.ServiceManager;
import org.drools.vsm.ServiceManagerData;

public class ServiceManagerLocalClient
    implements
    ServiceManager {

    private ServiceManagerData data;

    public ServiceManagerLocalClient() {
        this( new ServiceManagerData() );
    }

    public ServiceManagerLocalClient(ServiceManagerData data) {
        this.data = data;
    }

    public void disconnect() {

    }

    public KnowledgeBuilderProvider getKnowledgeBuilderFactory() {
        return new KnowledgeBuilderProviderLocalClient();
    }

    public KnowledgeBaseProvider getKnowledgeBaseFactory() {
        return new KnowledgeBaseProviderLocalClient();
    }

    public KnowledgeAgentProvider getKnowledgeAgentFactory() {
        // TODO Auto-generated method stub
        return null;
    }

    public JPAKnowledgeServiceProvider JPAKnowledgeService() {
        return new JPAKnowledgeServiceProviderImpl();
    }

    public Environment getEnvironment() {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection<String> list() {
        // TODO Auto-generated method stub
        return null;
    }

    public void register(String identifier,
                         CommandExecutor executor) {
        this.data.getRoot().set( identifier,
                                 executor );
    }

    public CommandExecutor lookup(String identifier) {
        return (CommandExecutor) this.data.getRoot().get( identifier );
    }

    public void release(Object object) {
        // TODO Auto-generated method stub
    }

    public void release(String identifier) {
        // TODO Auto-generated method stub
    }

    public HumanTaskServiceProvider getHumanTaskService() {
    	return new HumanTaskServiceLocalProviderImpl(this);
    }

    public static class RemoveKnowledgeBuilderProvider
        implements
        KnowledgeBuilderProvider {

        public DecisionTableConfiguration newDecisionTableConfiguration() {
            // TODO Auto-generated method stub
            return null;
        }

        public KnowledgeBuilder newKnowledgeBuilder() {
            // TODO Auto-generated method stub
            return null;
        }

        public KnowledgeBuilder newKnowledgeBuilder(KnowledgeBuilderConfiguration conf) {
            // TODO Auto-generated method stub
            return null;
        }

        public KnowledgeBuilder newKnowledgeBuilder(KnowledgeBase kbase) {
            // TODO Auto-generated method stub
            return null;
        }

        public KnowledgeBuilder newKnowledgeBuilder(KnowledgeBase kbase,
                                                    KnowledgeBuilderConfiguration conf) {
            // TODO Auto-generated method stub
            return null;
        }

        public KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration() {
            // TODO Auto-generated method stub
            return null;
        }

        public KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration(Properties properties,
                                                                              ClassLoader classLoader) {
            // TODO Auto-generated method stub
            return null;
        }
    }

    public ExecutionResults execute(Command command) {
        // TODO Auto-generated method stub
        return null;
    }
}
