package org.drools.vsm;

import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.KnowledgeBaseProvider;
import org.drools.SystemEventListenerFactory;
import org.drools.agent.KnowledgeAgentProvider;
import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderProvider;
import org.drools.persistence.jpa.JPAKnowledgeServiceProvider;
import org.drools.runtime.CommandExecutor;
import org.drools.runtime.Environment;

public class ServiceManagerServer extends BaseMinaServer
    implements
    Runnable {
    private AtomicInteger sessionIdCounter = new AtomicInteger();
    
    public AtomicInteger getSessionIdCounter() {
        return sessionIdCounter;
    }
    
    public ServiceManagerServer() {
        super( new ServiceManagerServerResponseHandler( SystemEventListenerFactory.getSystemEventListener() ),
               9123 );
        ((ServiceManagerServerResponseHandler)this.handler).setServiceManagerService( this );
    }
    
    

}
