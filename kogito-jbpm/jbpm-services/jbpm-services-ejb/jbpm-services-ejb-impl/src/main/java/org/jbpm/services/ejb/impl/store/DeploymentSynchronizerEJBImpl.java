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
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;

import org.jbpm.kie.services.impl.store.DeploymentStore;
import org.jbpm.kie.services.impl.store.DeploymentSynchronizer;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.ejb.api.DeploymentServiceEJBLocal;
import org.jbpm.services.ejb.impl.tx.TransactionalCommandServiceEJBImpl;
import org.jbpm.shared.services.impl.TransactionalCommandService;

@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Lock(LockType.WRITE)
@AccessTimeout(value=1, unit=TimeUnit.MINUTES)
public class DeploymentSynchronizerEJBImpl extends DeploymentSynchronizer {

	@Resource
    private TimerService timerService;
	
	private Timer timer;
	private TransactionalCommandService commandService;
	
	@PostConstruct
	public void configure() {
		if (DEPLOY_SYNC_ENABLED) {
			ScheduleExpression schedule = new ScheduleExpression();
			
			schedule.hour("*");
			schedule.minute("*");
			schedule.second("*/" + DEPLOY_SYNC_INTERVAL);
			timer = timerService.createCalendarTimer(schedule);
			DeploymentStore store = new DeploymentStore();
			store.setCommandService(commandService);
			
			setDeploymentStore(store);
		}
	}
	
	@PreDestroy
	public void shutdown() {
		if (timer != null) {
			timer.cancel();
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
