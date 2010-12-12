package org.drools.persistence.jpa.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import org.drools.KnowledgeBase;
import org.drools.SessionConfiguration;
import org.drools.command.CommandService;
import org.drools.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.persistence.jpa.KnowledgeStoreService;
import org.drools.persistence.processinstance.JPAWorkItemManagerFactory;
import org.drools.persistence.session.JpaJDKTimerService;
import org.drools.persistence.session.SingleSessionCommandService;
import org.drools.process.instance.WorkItemManagerFactory;
import org.drools.runtime.CommandExecutor;
import org.drools.runtime.Environment;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.time.TimerService;

public class KnowledgeStoreServiceImpl
    implements
    KnowledgeStoreService {

    private Class< ? extends CommandExecutor>               commandServiceClass;
    private Class< ? extends WorkItemManagerFactory>        workItemManagerFactoryClass;
    private Class< ? extends TimerService>                  timerServiceClass;

    private Properties                                      configProps = new Properties();

    public KnowledgeStoreServiceImpl() {
        setDefaultImplementations();
    }

    protected void setDefaultImplementations() {
        setCommandServiceClass( SingleSessionCommandService.class );
        setProcessInstanceManagerFactoryClass( "org.jbpm.persistence.processinstance.JPAProcessInstanceManagerFactory" );
        setWorkItemManagerFactoryClass( JPAWorkItemManagerFactory.class );
        setProcessSignalManagerFactoryClass( "org.jbpm.persistence.processinstance.JPASignalManagerFactory" );
        setTimerServiceClass( JpaJDKTimerService.class );
    }

    public StatefulKnowledgeSession newStatefulKnowledgeSession(KnowledgeBase kbase,
                                                                KnowledgeSessionConfiguration configuration,
                                                                Environment environment) {
        if ( configuration == null ) {
            configuration = new SessionConfiguration();
        }

        if ( environment == null ) {
            throw new IllegalArgumentException( "Environment cannot be null" );
        }

        return new CommandBasedStatefulKnowledgeSession( (CommandService) buildCommanService( kbase,
                                                                             mergeConfig( configuration ),
                                                                             environment ) );
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

        return new CommandBasedStatefulKnowledgeSession( (CommandService) buildCommanService( id,
                                                                             kbase,
                                                                             mergeConfig( configuration ),
                                                                             environment ) );
    }

    private CommandExecutor buildCommanService(int sessionId,
                                              KnowledgeBase kbase,
                                              KnowledgeSessionConfiguration conf,
                                              Environment env) {

        try {
            Class< ? extends CommandExecutor> serviceClass = getCommandServiceClass();
            Constructor< ? extends CommandExecutor> constructor = serviceClass.getConstructor( int.class,
                                                                                              KnowledgeBase.class,
                                                                                              KnowledgeSessionConfiguration.class,
                                                                                              Environment.class );
            return constructor.newInstance( sessionId,
                                            kbase,
                                            conf,
                                            env );
        } catch ( SecurityException e ) {
            throw new IllegalStateException( e );
        } catch ( NoSuchMethodException e ) {
            throw new IllegalStateException( e );
        } catch ( IllegalArgumentException e ) {
            throw new IllegalStateException( e );
        } catch ( InstantiationException e ) {
            throw new IllegalStateException( e );
        } catch ( IllegalAccessException e ) {
            throw new IllegalStateException( e );
        } catch ( InvocationTargetException e ) {
            throw new IllegalStateException( e );
        }
    }

    private CommandExecutor buildCommanService(KnowledgeBase kbase,
                                              KnowledgeSessionConfiguration conf,
                                              Environment env) {

        Class< ? extends CommandExecutor> serviceClass = getCommandServiceClass();
        try {
            Constructor< ? extends CommandExecutor> constructor = serviceClass.getConstructor( KnowledgeBase.class,
                                                                                              KnowledgeSessionConfiguration.class,
                                                                                              Environment.class );
            return constructor.newInstance( kbase,
                                            conf,
                                            env );
        } catch ( SecurityException e ) {
            throw new IllegalStateException( e );
        } catch ( NoSuchMethodException e ) {
            throw new IllegalStateException( e );
        } catch ( IllegalArgumentException e ) {
            throw new IllegalStateException( e );
        } catch ( InstantiationException e ) {
            throw new IllegalStateException( e );
        } catch ( IllegalAccessException e ) {
            throw new IllegalStateException( e );
        } catch ( InvocationTargetException e ) {
            throw new IllegalStateException( e );
        }
    }

    private KnowledgeSessionConfiguration mergeConfig(KnowledgeSessionConfiguration configuration) {
        ((SessionConfiguration) configuration).addProperties( configProps );
        return configuration;
    }

    public int getStatefulKnowledgeSessionId(StatefulKnowledgeSession ksession) {
        if ( ksession instanceof CommandBasedStatefulKnowledgeSession ) {
            SingleSessionCommandService commandService = (SingleSessionCommandService) ((CommandBasedStatefulKnowledgeSession) ksession).getCommandService();
            return commandService.getSessionId();
        }
        throw new IllegalArgumentException( "StatefulKnowledgeSession must be an a CommandBasedStatefulKnowledgeSession" );
    }

    public void setCommandServiceClass(Class< ? extends CommandExecutor> commandServiceClass) {
        if ( commandServiceClass != null ) {
            this.commandServiceClass = commandServiceClass;
            configProps.put( "drools.commandService",
                             commandServiceClass.getName() );
        }
    }

    public Class< ? extends CommandExecutor> getCommandServiceClass() {
        return commandServiceClass;
    }

    public void setTimerServiceClass(Class< ? extends TimerService> timerServiceClass) {
        if ( timerServiceClass != null ) {
            this.timerServiceClass = timerServiceClass;
            configProps.put( "drools.timerService",
            		         timerServiceClass.getName() );
        }
    }

    public Class< ? extends TimerService> getTimerServiceClass() {
        return timerServiceClass;
    }

    public void setProcessInstanceManagerFactoryClass(String processInstanceManagerFactoryClass) {
        configProps.put( "drools.processInstanceManagerFactory",
                         processInstanceManagerFactoryClass );
    }

    public void setWorkItemManagerFactoryClass(Class< ? extends WorkItemManagerFactory> workItemManagerFactoryClass) {
        if ( workItemManagerFactoryClass != null ) {
            this.workItemManagerFactoryClass = workItemManagerFactoryClass;
            configProps.put( "drools.workItemManagerFactory",
                             workItemManagerFactoryClass.getName() );
        }
    }

    public Class< ? extends WorkItemManagerFactory> getWorkItemManagerFactoryClass() {
        return workItemManagerFactoryClass;
    }

    public void setProcessSignalManagerFactoryClass(String processSignalManagerFactoryClass) {
        configProps.put( "drools.processSignalManagerFactory",
                         processSignalManagerFactoryClass );
    }
}
