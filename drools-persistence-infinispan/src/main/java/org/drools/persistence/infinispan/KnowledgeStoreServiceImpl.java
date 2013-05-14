/*
 * Copyright 2010 JBoss Inc
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
 package org.drools.persistence.infinispan;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import org.drools.core.SessionConfiguration;
import org.drools.core.command.CommandService;
import org.drools.core.command.Interceptor;
import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.core.process.instance.WorkItemManagerFactory;
import org.drools.persistence.SingleSessionCommandService;
import org.drools.persistence.infinispan.processinstance.InfinispanWorkItemManagerFactory;
import org.drools.persistence.jta.JtaTransactionManager;
import org.kie.api.KieBase;
import org.kie.api.persistence.jpa.KieStoreServices;
import org.kie.api.runtime.CommandExecutor;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.TimerJobFactoryOption;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

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
        setCommandServiceClass( SingleSessionCommandService.class );
        setProcessInstanceManagerFactoryClass( "org.jbpm.persistence.processinstance.InfinispanProcessInstanceManagerFactory" );
        setWorkItemManagerFactoryClass( InfinispanWorkItemManagerFactory.class );
        setProcessSignalManagerFactoryClass( "org.jbpm.persistence.processinstance.InfinispanSignalManagerFactory" );
    }

    public StatefulKnowledgeSession newKieSession(KieBase kbase,
                                                  KieSessionConfiguration configuration,
                                                  Environment environment) {
        if ( configuration == null ) {
            configuration = new SessionConfiguration();
        }

        if ( environment == null ) {
            throw new IllegalArgumentException( "Environment cannot be null" );
        }

        
        CommandService commandService = (CommandService) buildCommandService( kbase, mergeConfig( configuration ), environment );
        if (commandService instanceof SingleSessionCommandService) {
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
        }
        
        return new CommandBasedStatefulKnowledgeSession( commandService );
    }

    public StatefulKnowledgeSession loadKieSession(int id,
                                                   KieBase kbase,
                                                   KieSessionConfiguration configuration,
                                                   Environment environment) {
        if ( configuration == null ) {
            configuration = new SessionConfiguration();
        }

        if ( environment == null ) {
            throw new IllegalArgumentException( "Environment cannot be null" );
        }

        CommandService commandService = (CommandService) buildCommandService( id, kbase, mergeConfig( configuration ), environment );
        if (commandService instanceof SingleSessionCommandService) {
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
        }
        
        return new CommandBasedStatefulKnowledgeSession( commandService );
    }

    private CommandExecutor buildCommandService(Integer sessionId,
                                                KieBase kbase,
                                                KieSessionConfiguration conf,
                                                Environment env) {

    	env = mergeEnvironment(env);
        try {
            Class< ? extends CommandExecutor> serviceClass = getCommandServiceClass();
            Constructor< ? extends CommandExecutor> constructor = serviceClass.getConstructor( Integer.class,
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

    private Environment mergeEnvironment(Environment env) {
    	if (env == null) {
    		env = KnowledgeBaseFactory.newEnvironment();
    	}
    	if (env.get(EnvironmentName.PERSISTENCE_CONTEXT_MANAGER) == null) {
    		try {
    			Class<?> clazz = Class.forName("org.jbpm.persistence.InfinispanProcessPersistenceContextManager");
    			Constructor<?> c = clazz.getConstructor(Environment.class);
    			env.set(EnvironmentName.PERSISTENCE_CONTEXT_MANAGER, c.newInstance(env));
    		} catch (ClassNotFoundException e) {
    			env.set(EnvironmentName.PERSISTENCE_CONTEXT_MANAGER, new InfinispanPersistenceContextManager(env));
    		} catch (Exception e) {
    			e.printStackTrace();
    			env.set(EnvironmentName.PERSISTENCE_CONTEXT_MANAGER, new InfinispanPersistenceContextManager(env));
    		}
    	}
    	Object tm = env.get(EnvironmentName.TRANSACTION_MANAGER);
    	if (tm != null && tm instanceof javax.transaction.TransactionManager) {
    		Object ut = env.get(EnvironmentName.TRANSACTION);
    		if (ut == null && tm instanceof javax.transaction.Transaction) {
    			ut = tm;
    		}
    		Object tsr = env.get(EnvironmentName.TRANSACTION_SYNCHRONIZATION_REGISTRY);
    		env.set(EnvironmentName.TRANSACTION_MANAGER, new JtaTransactionManager(ut, tsr, tm));
    	}
    	return env;
    }
    
    private CommandExecutor buildCommandService(KieBase kbase,
                                                KieSessionConfiguration conf,
                                                Environment env) {
    	env = mergeEnvironment(env);
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
        ((SessionConfiguration) configuration).addDefaultProperties(configProps);
        configuration.setOption(TimerJobFactoryOption.get("jpa"));
        return configuration;
    }

    public long getStatefulKnowledgeSessionId(StatefulKnowledgeSession ksession) {
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
