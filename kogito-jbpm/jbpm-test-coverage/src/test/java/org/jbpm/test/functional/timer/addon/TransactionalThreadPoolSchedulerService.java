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

package org.jbpm.test.functional.timer.addon;

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
