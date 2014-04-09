/*
 * Copyright 2013 JBoss Inc
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
package org.jbpm.runtime.manager.impl;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.process.core.timer.GlobalSchedulerService;
import org.jbpm.process.core.timer.impl.QuartzSchedulerService;
import org.jbpm.process.core.timer.impl.ThreadPoolSchedulerService;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.runtime.manager.impl.mapper.InMemoryMapper;
import org.jbpm.runtime.manager.impl.mapper.JPAMapper;
import org.jbpm.services.task.identity.MvelUserGroupCallbackImpl;
import org.kie.api.runtime.EnvironmentName;

/**
 * Default implementation of RuntimeEnvironment that aims at providing all 
 * common settings with minimum need for configuration.
 * 
 * It configures automatically following components:
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
        // TODO is this the right one to be default?
        this.userGroupCallback = new MvelUserGroupCallbackImpl(true);
    }
    
    public DefaultRuntimeEnvironment(EntityManagerFactory emf, boolean usePersistence) {
        this(emf, null);
        this.usePersistence = usePersistence;
        this.emf = emf;
        // TODO is this the right one to be default?
        this.userGroupCallback = new MvelUserGroupCallbackImpl(true);
    }
    
    public void init() {
        if (emf == null && getEnvironmentTemplate().get(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER) == null) {
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
        } 
        return new ThreadPoolSchedulerService(3);
        
    }

}
