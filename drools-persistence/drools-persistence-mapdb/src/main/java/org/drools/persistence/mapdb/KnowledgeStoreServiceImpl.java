/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.drools.persistence.mapdb;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import org.drools.core.SessionConfiguration;
import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.core.process.instance.WorkItemManagerFactory;
import org.drools.core.time.TimerService;
import org.drools.persistence.processinstance.mapdb.MapDBWorkItemManagerFactory;
import org.kie.api.KieBase;
import org.kie.api.Service;
import org.kie.api.persistence.jpa.KieStoreServices;
import org.kie.api.runtime.CommandExecutor;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.ExecutableRunner;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.TimerJobFactoryOption;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.utils.ServiceRegistryImpl;

public class KnowledgeStoreServiceImpl
    implements
    KieStoreServices, Service {

	static {
		ServiceRegistryImpl.getInstance().addDefault(
				KieStoreServices.class, 
				KnowledgeStoreServiceImpl.class.getName());
	}
	
	
    private Class< ? extends CommandExecutor>               commandServiceClass;
    private Class< ? extends WorkItemManagerFactory>        workItemManagerFactoryClass;

    private Properties                                      configProps = new Properties();

    public KnowledgeStoreServiceImpl() {
        setDefaultImplementations();
    }

    protected void setDefaultImplementations() {
        setCommandServiceClass( MapDBSessionCommandService.class );
        setProcessInstanceManagerFactoryClass( "org.jbpm.persistence.mapdb.MapDBProcessInstanceManagerFactory" );
        setWorkItemManagerFactoryClass( MapDBWorkItemManagerFactory.class );
        setProcessSignalManagerFactoryClass( "org.jbpm.persistence.mapdb.MapDBSignalManagerFactory" );
        setTimerServiceClass(MapDBJDKTimerService.class);
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

        
        ExecutableRunner<?> runner = (ExecutableRunner<?>) buildCommandService( kbase, mergeConfig( configuration ), environment );
/*        if (commandService instanceof MapDBSessionCommandService) {
        	((MapDBSessionCommandService) commandService).
        		addInterceptor(new ManualPersistInterceptor((MapDBSessionCommandService) commandService));
        	try {
        		Class<?> clazz = Class.forName("org.jbpm.persistence.ManualPersistProcessInterceptor");
        		Constructor<?> c = clazz.getConstructor(MapDBSessionCommandService.class);
        		Interceptor interceptor = (Interceptor) c.newInstance(commandService);
        		((MapDBSessionCommandService) commandService).addInterceptor(interceptor);
        	} catch (ClassNotFoundException e) {
        		//Expected of non-jbpm based projects
        	} catch (Exception e) {
        		//something unexpected happened
        		throw new RuntimeException("Something wrong initializing manual process persistor interceptor", e);
        	}
        }*/
        
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

        ExecutableRunner<?> runner = (ExecutableRunner<?>) buildCommandService( new Long(id), kbase, mergeConfig( configuration ), environment );
/*        if (commandService instanceof SingleSessionCommandService) {
        	((SingleSessionCommandService) commandService).
        		addInterceptor(new ManualPersistInterceptor((SingleSessionCommandService) commandService));
        	try {
        		Class<?> clazz = Class.forName("org.jbpm.persistence.ManualPersistProcessInterceptor");
        		Constructor<?> c = clazz.getConstructor(SingleSessionCommandService.class);
        		Interceptor interceptor = (Interceptor) c.newInstance(commandService);
        		((SingleSessionCommandService) commandService).addInterceptor(interceptor);
        	} catch (ClassNotFoundException e) {
        		//Expected of non-jbpm based projects
        	} catch (Exception e) {
        		//something unexpected happened
        		throw new RuntimeException("Something wrong initializing manual process persistor interceptor", e);
        	}
        }*/
        
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

        ExecutableRunner<?> runner = (ExecutableRunner<?>) buildCommandService( id, kbase, mergeConfig( configuration ), environment );
/*        if (commandService instanceof SingleSessionCommandService) {
            ((SingleSessionCommandService) commandService).
                    addInterceptor(new ManualPersistInterceptor((SingleSessionCommandService) commandService));
            try {
                Class<?> clazz = Class.forName("org.jbpm.persistence.ManualPersistProcessInterceptor");
                Constructor<?> c = clazz.getConstructor(SingleSessionCommandService.class);
                Interceptor interceptor = (Interceptor) c.newInstance(commandService);
                ((SingleSessionCommandService) commandService).addInterceptor(interceptor);
            } catch (ClassNotFoundException e) {
                //Expected of non-jbpm based projects
            } catch (Exception e) {
                //something unexpected happened
                throw new RuntimeException("Something wrong initializing manual process persistor interceptor", e);
            }
        }*/

        return new CommandBasedStatefulKnowledgeSession( runner );
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
        KieSessionConfiguration merged = ((SessionConfiguration) configuration).addDefaultProperties(configProps);
        merged.setOption(TimerJobFactoryOption.get("mapdb"));
        return merged;
    }

    public long getStatefulKnowledgeSessionId(StatefulKnowledgeSession ksession) {
        if ( ksession instanceof CommandBasedStatefulKnowledgeSession ) {
            MapDBSessionCommandService commandService = (MapDBSessionCommandService) ((CommandBasedStatefulKnowledgeSession) ksession).getRunner();
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

    public void setTimerServiceClass(Class<? extends TimerService> timerServiceClass) {
    	configProps.put("drools.timerService", timerServiceClass.getName());
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
