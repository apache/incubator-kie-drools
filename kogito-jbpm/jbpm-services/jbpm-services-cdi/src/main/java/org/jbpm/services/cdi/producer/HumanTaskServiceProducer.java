/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.services.cdi.producer;

import java.util.HashSet;
import java.util.List;

import javax.enterprise.inject.AmbiguousResolutionException;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.drools.core.impl.EnvironmentFactory;
import org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorManager;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.services.task.HumanTaskConfigurator;
import org.jbpm.services.task.HumanTaskServiceFactory;
import org.jbpm.services.task.audit.JPATaskLifeCycleEventListener;
import org.jbpm.services.task.impl.command.CommandBasedTaskService;
import org.jbpm.services.task.lifecycle.listeners.BAMTaskEventListener;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.task.TaskLifeCycleEventListener;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.identity.IdentityProvider;
import org.kie.internal.runtime.conf.AuditMode;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.task.api.InternalTaskService;
import org.kie.internal.task.api.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CDI producer for <code>TaskService</code> instances. By default it runs in new mode,
 * meaning new <code>TaskService</code> instance for every injection point.
 * This behavior can be altered by setting <code>org.jbpm.cdi.taskservice.mode</code> system
 * property to one of the values.
 * <ul>
 * <li>none - disables producer to not return TaskService instances</li>
 * <li>singleton - produces only one instance of TaskService that will be shared</li>
 * <li>new - produces new instance for every injection point</li>
 * </ul>
 * This bean accept following injections:
 * <ul>
 * <li>UserGroupCallback</li>
 * <li>UserInfo</li>
 * <li>TaskLifeCycleEventListener</li>
 * </ul>
 * all of these are optional injections and if not available defaults will be used. Underneath it uses
 * <code>HumanTaskConfigurator</code> for <code>TaskService</code> instances creations.
 * @see HumanTaskConfigurator
 */
public class HumanTaskServiceProducer {

    private static final Logger logger = LoggerFactory.getLogger( HumanTaskServiceProducer.class );
    final String mode = System.getProperty( "org.jbpm.cdi.taskservice.mode", "new" );

    @Inject
    private Instance<UserGroupCallback> userGroupCallback;

    @Inject
    private Instance<UserInfo> userInfo;

    @Inject
    @Any
    private Instance<TaskLifeCycleEventListener> taskListeners;
    
    @Inject
    @Any
    private Instance<List<TaskLifeCycleEventListener>> listOfListeners;

    @Inject
    @PersistenceUnit(unitName = "org.jbpm.domain")
    private EntityManagerFactory emf;
    
    @Inject
    private Instance<IdentityProvider> identityProvider;

    // internal member to ensure only single instance of task service is produced
    private InternalTaskService taskService;

    @Produces
    public CommandBasedTaskService produceTaskService() {
        if ( mode.equalsIgnoreCase( "none" ) ) {
            return null;
        }
        if ( taskService == null ) {
            HumanTaskConfigurator configurator = createHumanTaskConfigurator();
            
            if ( mode.equalsIgnoreCase( "singleton" ) ) {
                this.taskService = (CommandBasedTaskService) configurator.getTaskService();
            } else {
                return (CommandBasedTaskService) configurator.getTaskService();
            }
        }

        return (CommandBasedTaskService) taskService;
    }

    protected HumanTaskConfigurator createHumanTaskConfigurator() {
        HumanTaskConfigurator configurator = HumanTaskServiceFactory.newTaskServiceConfigurator();
        
        configureHumanTaskConfigurator(configurator);
        return configurator;
    }

    protected void configureHumanTaskConfigurator(HumanTaskConfigurator configurator) {
        configurator
                .environment(getEnvironment(identityProvider))
                .entityManagerFactory( emf )
                .userGroupCallback( safeGet( userGroupCallback ) )
                .userInfo( safeGet( userInfo ) );
        
        DeploymentDescriptorManager manager = new DeploymentDescriptorManager("org.jbpm.domain");
        DeploymentDescriptor descriptor = manager.getDefaultDescriptor();
        // in case there is descriptor with enabled audit register then by default
        if (!descriptor.getAuditMode().equals(AuditMode.NONE)) {
        	JPATaskLifeCycleEventListener listener = new JPATaskLifeCycleEventListener(false);
        	BAMTaskEventListener bamListener = new BAMTaskEventListener(false);
        	// if the audit persistence unit is different than default for the engine perform proper init
        	if (!"org.jbpm.domain".equals(descriptor.getAuditPersistenceUnit())) {
        		 EntityManagerFactory emf = EntityManagerFactoryManager.get().getOrCreate(descriptor.getAuditPersistenceUnit());
        		 listener = new JPATaskLifeCycleEventListener(emf);
        		 
        		 bamListener = new BAMTaskEventListener(emf);
        	}
        	configurator.listener( listener );
        	configurator.listener( bamListener );
        }
        // next proceed with registration of further listeners as cdi injections
        try {
            for ( TaskLifeCycleEventListener listener : taskListeners ) {
                configurator.listener( listener );
                logger.debug( "Registering listener {}", listener );
            }
        } catch ( Exception e ) {
            logger.debug( "Cannot add listeners to task service due to {}", e.getMessage() );
        }
    }
    
    protected Environment getEnvironment(Instance<IdentityProvider> identityProvider) {
        Environment env = EnvironmentFactory.newEnvironment();
        try {
            env.set(EnvironmentName.IDENTITY_PROVIDER, identityProvider.get());
            
            return env;
        } catch (Exception e) {
            return env;
        }
    }

    protected <T> T safeGet( Instance<T> instance ) {
        try {
            T object = instance.get();
            logger.debug( "About to set object {} on task service", object );
            return object;
        } catch ( AmbiguousResolutionException e ) {
            // special handling in case cdi discovered multiple injections
            // that are actually same instances - e.g. weld on tomcat
            HashSet<T> available = new HashSet<T>();

            for ( T object : instance ) {
                available.add( object );
            }

            if ( available.size() == 1 ) {
                return available.iterator().next();
            } else {
                throw e;
            }
        } catch ( Throwable e ) {
            logger.debug( "Cannot get value of of instance {} due to {}", instance, e.getMessage() );
        }

        return null;
    }

}
