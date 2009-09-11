package org.drools.vsm;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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
import org.drools.command.Context;
import org.drools.command.ContextManager;
import org.drools.command.impl.ContextImpl;
import org.drools.persistence.jpa.JPAKnowledgeServiceProvider;
import org.drools.runtime.CommandExecutor;
import org.drools.runtime.Environment;
import org.drools.vsm.ServiceManagerServerResponseHandler.ContextManagerImpl;

public class ServiceManagerServer extends BaseMinaServer
    implements
    Runnable {
    private AtomicInteger       sessionIdCounter = new AtomicInteger();

    private ContextManager      contextManager;

    private Context             root;
    private Context             temp;

    public static String               ROOT             = "ROOT";
    public static String               TEMP             = "__TEMP__";
    public static String               SERVICE_MANAGER  = "__ServiceManager__";

    public AtomicInteger getSessionIdCounter() {
        return sessionIdCounter;
    }

    public ServiceManagerServer() {
        super( new ServiceManagerServerResponseHandler( SystemEventListenerFactory.getSystemEventListener() ),
               9123 );
        this.contextManager = new ContextManagerImpl();

        this.root = new ContextImpl( ROOT,
                                     this.contextManager );
        ((ContextManagerImpl) this.contextManager).addContext( this.root );

        this.temp = new ContextImpl( TEMP,
                                     this.contextManager,
                                     this.root );
        ((ContextManagerImpl) this.contextManager).addContext( this.temp );

        ((ServiceManagerServerResponseHandler) this.handler).setServiceManagerService( this );

        this.temp.set( "__ServiceManager__",
                       this );
    }
    
    public ContextManager getContextManager() {
        return contextManager;
    }

    public Context getRoot() {
        return root;
    }

    public Context getTemp() {
        return temp;
    }

}
