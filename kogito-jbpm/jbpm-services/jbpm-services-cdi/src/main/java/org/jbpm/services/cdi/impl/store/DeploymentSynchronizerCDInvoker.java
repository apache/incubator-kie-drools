package org.jbpm.services.cdi.impl.store;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import org.jbpm.kie.services.impl.store.DeploymentSynchronizer;

@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Lock(LockType.WRITE)
@TransactionManagement(TransactionManagementType.BEAN)
public class DeploymentSynchronizerCDInvoker {
	
	private Timer timer;
	@Resource
    private TimerService timerService;
	@Inject
	private DeploymentSynchronizer deploymentSynchronizer;
	
	@PostConstruct
	public void configure() {
		if (DeploymentSynchronizer.DEPLOY_SYNC_ENABLED) {
			ScheduleExpression schedule = new ScheduleExpression();
			
			schedule.hour("*");
			schedule.minute("*");
			schedule.second("*/" + DeploymentSynchronizer.DEPLOY_SYNC_INTERVAL);
			timer = timerService.createCalendarTimer(schedule);
		
		}
	}
	
	@PreDestroy
	public void shutdown() {
		if (timer != null) {
			timer.cancel();
		}
	}

	@Timeout
	public void synchronize() {
		deploymentSynchronizer.synchronize();
	}

	
}
