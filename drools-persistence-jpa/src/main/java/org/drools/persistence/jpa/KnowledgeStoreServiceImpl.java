/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 package org.drools.persistence.jpa;

import org.kie.api.runtime.ExecutableRunner;
import org.drools.core.SessionConfiguration;
import org.drools.core.command.EntryPointCreator;
import org.drools.core.command.impl.CommandBasedEntryPoint;
import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.core.process.instance.WorkItemManagerFactory;
import org.drools.persistence.PersistableRunner;
import org.drools.persistence.jpa.processinstance.JPAWorkItemManagerFactory;
import org.kie.api.KieBase;
import org.kie.api.persistence.jpa.KieStoreServices;
import org.kie.api.runtime.CommandExecutor;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.TimerJobFactoryOption;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

public class KnowledgeStoreServiceImpl
    implements
    KieStoreServices {

    private Class< ? extends CommandExecutor>               commandServiceClass;
    private Class< ? extends WorkItemManagerFactory>        workItemManagerFactoryClass;

    private Properties                                      configProps = new Properties();

    public KnowledgeStoreServiceImpl() {
        setDefaultImplementations();
    }

    protected void setDefaultImplementations() {
        setCommandServiceClass( PersistableRunner.class );
        setProcessInstanceManagerFactoryClass( "org.jbpm.persistence.processinstance.JPAProcessInstanceManagerFactory" );
        setWorkItemManagerFactoryClass( JPAWorkItemManagerFactory.class );
        setProcessSignalManagerFactoryClass( "org.jbpm.persistence.processinstance.JPASignalManagerFactory" );
    }

    public StatefulKnowledgeSession newKieSession(KieBase kbase,
                                                  KieSessionConfiguration configuration,
                                                  Environment environment) {
        if ( configuration == null ) {
            configuration = SessionConfiguration.newInstance();
        }

        if ( environment == null ) {
            throw new IllegalArgumentException( "Environment cannot be null" );
        }

        ExecutableRunner runner = (ExecutableRunner) buildCommandService( kbase,
                                                                          mergeConfig( configuration ),
                                                                          environment );
        runner.createContext().set( EntryPointCreator.class.getName(),
                                    new CommandBasedEntryPointCreator(runner) );
        return new CommandBasedStatefulKnowledgeSession( runner );
    }

    public StatefulKnowledgeSession loadKieSession(int id,
                                                   KieBase kbase,
                                                   KieSessionConfiguration configuration,
                                                   Environment environment) {
        if ( configuration == null ) {
            configuration = SessionConfiguration.newInstance();
        }

        if ( environment == null ) {
            throw new IllegalArgumentException( "Environment cannot be null" );
        }

        ExecutableRunner runner = (ExecutableRunner) buildCommandService( new Long( id),
                                                                          kbase,
                                                                          mergeConfig( configuration ),
                                                                          environment );
        runner.createContext().set( EntryPointCreator.class.getName(),
                                    new CommandBasedEntryPointCreator(runner) );
        return new CommandBasedStatefulKnowledgeSession( runner );
    }

    public StatefulKnowledgeSession loadKieSession(Long id,
            KieBase kbase,
            KieSessionConfiguration configuration,
            Environment environment) {
        if ( configuration == null ) {
            configuration = SessionConfiguration.newInstance();
        }

        if ( environment == null ) {
            throw new IllegalArgumentException( "Environment cannot be null" );
        }

        ExecutableRunner runner = (ExecutableRunner) buildCommandService( id,
                                                                          kbase,
                                                                          mergeConfig( configuration ),
                                                                          environment );
        runner.createContext().set( EntryPointCreator.class.getName(),
                                    new CommandBasedEntryPointCreator(runner) );
        return new CommandBasedStatefulKnowledgeSession( runner );
    }

    public static class CommandBasedEntryPointCreator implements EntryPointCreator {
        private final ExecutableRunner runner;

        public CommandBasedEntryPointCreator(ExecutableRunner runner ) {
            this.runner = runner;
        }

        public EntryPoint getEntryPoint(String entryPoint) {
            return new CommandBasedEntryPoint( runner, entryPoint );
        }
    }

    private CommandExecutor buildCommandService(Long sessionId,
                                                KieBase kbase,
                                                KieSessionConfiguration conf,
                                                Environment env) {

        try {
            Class< ? extends CommandExecutor> serviceClass = getCommandServiceClass();
            Constructor< ? extends CommandExecutor> constructor = serviceClass.getConstructor( Long.class,
                                                                                              KieBase.class,
                                                                                              KieSessionConfiguration.class,
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

    private CommandExecutor buildCommandService(KieBase kbase,
                                                KieSessionConfiguration conf,
                                                Environment env) {

        Class< ? extends CommandExecutor> serviceClass = getCommandServiceClass();
        try {
            Constructor< ? extends CommandExecutor> constructor = serviceClass.getConstructor( KieBase.class,
                                                                                              KieSessionConfiguration.class,
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

    private KieSessionConfiguration mergeConfig(KieSessionConfiguration configuration) {
        KieSessionConfiguration merged = ((SessionConfiguration) configuration).addDefaultProperties( configProps );
        merged.setOption(TimerJobFactoryOption.get("jpa"));
        return merged;
    }

    public long getStatefulKnowledgeSessionId(StatefulKnowledgeSession ksession) {
        if ( ksession instanceof CommandBasedStatefulKnowledgeSession ) {
            PersistableRunner commandService = (PersistableRunner) ((CommandBasedStatefulKnowledgeSession) ksession).getRunner();
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
