package org.drools.persistence.jpa.impl;

import java.util.Properties;

import org.drools.KnowledgeBase;
import org.drools.SessionConfiguration;
import org.drools.command.CommandService;
import org.drools.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.persistence.jpa.JPAKnowledgeServiceProvider;
import org.drools.persistence.session.SingleSessionCommandService;
import org.drools.runtime.Environment;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;

public class JPAKnowledgeServiceProviderImpl
    implements
    JPAKnowledgeServiceProvider {

    public StatefulKnowledgeSession newStatefulKnowledgeSession(KnowledgeBase kbase,
                                                                KnowledgeSessionConfiguration configuration,
                                                                Environment environment) {
        if ( configuration == null ) {
            configuration = new SessionConfiguration();
        }
        
        if ( environment == null ) {
            throw new IllegalArgumentException( "Environment cannot be null" );
        }
        
        Properties props = new Properties();
        
        props.setProperty( "drools.commandService",
                                   "org.drools.persistence.session.SingleSessionCommandService" );
        props.setProperty( "drools.processInstanceManagerFactory",
                                   "org.drools.persistence.processinstance.JPAProcessInstanceManagerFactory" );
        props.setProperty( "drools.workItemManagerFactory",
                                   "org.drools.persistence.processinstance.JPAWorkItemManagerFactory" );
        props.setProperty( "drools.processSignalManagerFactory",
                                   "org.drools.persistence.processinstance.JPASignalManagerFactory" );   
        
        ((SessionConfiguration)configuration).addProperties( props );

        CommandService commandService = new SingleSessionCommandService( kbase,
                                                                         configuration,
                                                                         environment );
        return new CommandBasedStatefulKnowledgeSession( commandService );
    }

    public StatefulKnowledgeSession loadStatefulKnowledgeSession(int id,
                                                                 KnowledgeBase kbase,
                                                                 KnowledgeSessionConfiguration configuration,
                                                                 Environment environment) {
        if ( configuration == null ) {
            configuration = new SessionConfiguration();
        }
        
        if ( environment == null ) {
            throw new IllegalArgumentException( "Environment cannot be null" );
        }
        
        Properties props = new Properties();
        
        props.setProperty( "drools.commandService",
                                   "org.drools.persistence.session.SingleSessionCommandService" );
        props.setProperty( "drools.processInstanceManagerFactory",
                                   "org.drools.persistence.processinstance.JPAProcessInstanceManagerFactory" );
        props.setProperty( "drools.workItemManagerFactory",
                                   "org.drools.persistence.processinstance.JPAWorkItemManagerFactory" );
        props.setProperty( "drools.processSignalManagerFactory",
                                   "org.drools.persistence.processinstance.JPASignalManagerFactory" );   
        
        ((SessionConfiguration)configuration).addProperties( props );
        
        CommandService commandService = new SingleSessionCommandService( id,
                                                                         kbase,
                                                                         configuration,
                                                                         environment );
        return new CommandBasedStatefulKnowledgeSession( commandService );
    }
    
    public int getStatefulKnowledgeSessionId(StatefulKnowledgeSession ksession) {
        if ( ksession instanceof CommandBasedStatefulKnowledgeSession) {
            SingleSessionCommandService commandService = ( SingleSessionCommandService ) ((CommandBasedStatefulKnowledgeSession)ksession).getCommandService();
            return commandService.getSessionId();
        } else {
            throw new IllegalArgumentException( "StatefulKnowledgeSession must be an a CommandBasedStatefulKnowledgeSession" );
        }
    }
    

}
