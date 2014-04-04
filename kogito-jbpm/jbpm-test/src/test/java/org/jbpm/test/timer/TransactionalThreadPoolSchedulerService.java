package org.jbpm.test.timer;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;

import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import org.drools.core.time.impl.TimerJobInstance;
import org.jbpm.persistence.timer.GlobalJpaTimerJobInstance;
import org.jbpm.process.core.timer.impl.ThreadPoolSchedulerService;

public class TransactionalThreadPoolSchedulerService extends ThreadPoolSchedulerService {

	public TransactionalThreadPoolSchedulerService(int poolSize) {
		super(poolSize);
	}


	@Override
	public void internalSchedule(TimerJobInstance timerJobInstance) {
		TimerJobInstance proxy = (TimerJobInstance) Proxy.newProxyInstance(this.getClass().getClassLoader(),
				new Class[] {Callable.class,
		    Comparable.class,
		    TimerJobInstance.class, Serializable.class}, new TransactionalTimerJobInstance(timerJobInstance));
		super.internalSchedule(proxy);
	}
	
	
	private class TransactionalTimerJobInstance implements InvocationHandler {
		
		private GlobalJpaTimerJobInstance delegate;
		
		public TransactionalTimerJobInstance(TimerJobInstance timerJobInstance) {
			this.delegate = (GlobalJpaTimerJobInstance) timerJobInstance;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			boolean txStarted = false;
			UserTransaction ut = null;
			
			try {
				if ("call".equals(method.getName())) {
					System.out.println("Starting transaction");
					ut = InitialContext.doLookup("java:comp/UserTransaction");
					ut.begin();
					txStarted = true;
				}
				Object result = method.invoke(delegate, args);
				if (txStarted) {
					System.out.println("Committing transaction");
					ut.commit();
				}
				return result;
			} catch (Exception e ) {
				if (txStarted) {
					System.out.println("Rolling back transaction");
					ut.rollback();
				}
				throw e;
			}
			
		}
		
	}
}
