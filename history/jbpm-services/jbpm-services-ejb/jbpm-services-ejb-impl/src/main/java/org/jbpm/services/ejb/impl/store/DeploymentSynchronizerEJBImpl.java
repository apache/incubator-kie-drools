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

package org.jbpm.services.ejb.impl.store;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.AccessTimeout;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.NoSuchObjectLocalException;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;

import org.jbpm.kie.services.impl.store.DeploymentStore;
import org.jbpm.kie.services.impl.store.DeploymentSynchronizer;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.ejb.api.DeploymentServiceEJBLocal;
import org.jbpm.services.ejb.impl.tx.TransactionalCommandServiceEJBImpl;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Lock(LockType.WRITE)
@AccessTimeout(value=1, unit=TimeUnit.MINUTES)
public class DeploymentSynchronizerEJBImpl extends DeploymentSynchronizer {
    
    private static final Logger logger = LoggerFactory.getLogger(DeploymentSynchronizerEJBImpl.class);

	@Resource
    private TimerService timerService;
	
	private Timer timer;
	private TransactionalCommandService commandService;
	
	@PostConstruct
	public void configure() {
        DeploymentStore store = new DeploymentStore();
        store.setCommandService(commandService);
        
        setDeploymentStore(store);
        
		if (DEPLOY_SYNC_ENABLED) {
			ScheduleExpression schedule = new ScheduleExpression();
			
			schedule.hour("*");
			schedule.minute("*");
			schedule.second("*/" + DEPLOY_SYNC_INTERVAL);
			timer = timerService.createCalendarTimer(schedule, new TimerConfig(null, false));
		}
	}
	
	@PreDestroy
	public void shutdown() {
		if (timer != null) {
		    try {
                timer.cancel();
            } catch (NoSuchObjectLocalException e) {
                logger.debug("Timer {} is already canceled or expired", timer);
            }
		}
	}
	
	@EJB(beanInterface=DeploymentServiceEJBLocal.class)
	@Override
	public void setDeploymentService(DeploymentService deploymentService) {
		super.setDeploymentService(deploymentService);
	}
	
	@EJB(beanInterface=TransactionalCommandServiceEJBImpl.class)
	public void setCommandService(TransactionalCommandService commandService) {
		this.commandService = commandService;
	}

	@Timeout
	public void synchronize() {
		super.synchronize();
	}

	
}
