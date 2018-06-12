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
package org.jbpm.runtime.manager.impl;

import javax.naming.InitialContext;
import javax.persistence.EntityManagerFactory;

import org.jbpm.process.core.timer.GlobalSchedulerService;
import org.jbpm.process.core.timer.impl.QuartzSchedulerService;
import org.jbpm.process.core.timer.impl.ThreadPoolSchedulerService;
import org.jbpm.runtime.manager.impl.identity.UserDataServiceProvider;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.runtime.manager.impl.mapper.InMemoryMapper;
import org.jbpm.runtime.manager.impl.mapper.JPAMapper;
import org.kie.api.runtime.EnvironmentName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of the RuntimeEnvironment that aims at providing all 
 * common settings with a minimum need for configuration.
 * 
 * It automatically configures the following components:
 * <ul>
 *  <li>uses <code>DefaultRegisterableItemsFactory</code> to provide work item handlers and event listeners instances</li>
 *  <li>EntityManagerFactory - if non given uses persistence unit with "org.jbpm.persistence.jpa" name</li>
 *  <li>SchedulerService - if non given tries to discover if Quartz based scheduler shall be used by checking if 
 *  "org.quartz.properties" system property is given, if not uses ThreadPool based scheduler with thread pool size set to 3</li>
 *  <li>uses simple MVEL based UserGroupCallback that requires mvel files for users and groups to be present on classpath</li>
 * </ul>
 *
 */
public class DefaultRuntimeEnvironment extends SimpleRuntimeEnvironment {
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultRuntimeEnvironment.class);

    public DefaultRuntimeEnvironment() {
        this(null, discoverSchedulerService());
    }
    
    public DefaultRuntimeEnvironment(EntityManagerFactory emf) {
        this(emf, discoverSchedulerService());
    }
    
    public DefaultRuntimeEnvironment(EntityManagerFactory emf, GlobalSchedulerService globalSchedulerService) {
        super(new DefaultRegisterableItemsFactory());
        this.emf = emf;
        this.schedulerService = globalSchedulerService;
        this.usePersistence = true;
        this.userGroupCallback = UserDataServiceProvider.getUserGroupCallback();
        this.userInfo = UserDataServiceProvider.getUserInfo();
    }
    
    public DefaultRuntimeEnvironment(EntityManagerFactory emf, boolean usePersistence) {
        this(emf, null);
        this.usePersistence = usePersistence;
        this.emf = emf;
        this.userGroupCallback = UserDataServiceProvider.getUserGroupCallback();
        this.userInfo = UserDataServiceProvider.getUserInfo();
    }
    
    public void init() {
        if (usePersistence && emf == null && getEnvironmentTemplate().get(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER) == null) {
            emf = EntityManagerFactoryManager.get().getOrCreate("org.jbpm.persistence.jpa");
        }   
        addToEnvironment(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
        if (this.mapper == null) {
            if (this.usePersistence) {
                this.mapper = new JPAMapper(emf);
            } else {
                this.mapper = new InMemoryMapper();
            }
        }
    }
    
    protected static GlobalSchedulerService discoverSchedulerService() {
        if (System.getProperty("org.quartz.properties") != null) {
            return new QuartzSchedulerService();
        } else {
        	// if there is ejb scheduler service available make use of it unless it's disabled
        	if (!"true".equalsIgnoreCase(System.getProperty("org.kie.timer.ejb.disabled"))) {
	        	try {
	        		Class<?> clazz = Class.forName("org.jbpm.services.ejb.timer.EjbSchedulerService");
	        		// to ensure ejb timer service is actually available let's jndi look up
	        		InitialContext.doLookup("java:module/EJBTimerScheduler");
	        		return (GlobalSchedulerService) clazz.newInstance();
	        	} catch (Exception e) {
	        		logger.debug("Unable to find on initialize ejb schduler service due to {}", e.getMessage());
	        	}
        	}
        }
        return new ThreadPoolSchedulerService(3);
        
    }

}
