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

package org.jbpm.services.ejb.timer;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.NoSuchObjectLocalException;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;

import org.drools.core.time.JobHandle;
import org.drools.core.time.impl.TimerJobInstance;
import org.jbpm.process.core.timer.TimerServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Lock(LockType.READ)
public class EJBTimerScheduler {
	
	private static final Logger logger = LoggerFactory.getLogger(EJBTimerScheduler.class);

	private static final Integer OVERDUE_WAIT_TIME = Integer.parseInt(System.getProperty("org.jbpm.overdue.timer.wait", "20000"));
	
	private ConcurrentMap<String, TimerJobInstance> localCache = new ConcurrentHashMap<String, TimerJobInstance>();
	
	@Resource
	private javax.ejb.TimerService timerService;
	
	@PostConstruct
	public void setup() {
	    // disable auto init of timers since ejb timer service supports persistence of timers
	    System.setProperty("org.jbpm.rm.init.timer", "false");
	}
	
	@SuppressWarnings("unchecked")
	@Timeout
	public void executeTimerJob(Timer timer) {
		
		EjbTimerJob timerJob = (EjbTimerJob) timer.getInfo();
		logger.debug("About to execute timer for job {}", timerJob);
		TimerJobInstance timerJobInstance = timerJob.getTimerJobInstance();
		String timerServiceId = ((EjbGlobalJobHandle)timerJobInstance.getJobHandle()).getDeploymentId();
		
		// handle overdue timers as ejb timer service might start before all deployments are ready		
		long time = 0;
        while (TimerServiceRegistry.getInstance().get(timerServiceId) == null) {
        	logger.debug("waiting for timer service to be available, elapsed time {} ms", time);
            try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            time += 500;
            
            if (time > OVERDUE_WAIT_TIME) {
            	logger.debug("No timer service found after waiting {} ms", time);
            	break;
            }
        }
		try {
			((Callable<Void>) timerJobInstance).call();
		} catch (Exception e) {
			logger.warn("Execution of time failed due to {}", e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
	
	public void internalSchedule(TimerJobInstance timerJobInstance) {
		TimerConfig config = new TimerConfig(new EjbTimerJob(timerJobInstance), true);
		Date expirationTime = timerJobInstance.getTrigger().hasNextFireTime();
		logger.debug("Timer expiration date is {}", expirationTime);
		if (expirationTime != null) {
			timerService.createSingleActionTimer(expirationTime, config);
			logger.debug("Timer scheduled {} on {} scheduler service", timerJobInstance);
			localCache.putIfAbsent(((EjbGlobalJobHandle) timerJobInstance.getJobHandle()).getUuid(), timerJobInstance);
		} else {
			logger.info("Timer that was to be scheduled has already expired");
		}
	}
	
	public boolean removeJob(JobHandle jobHandle) {
		EjbGlobalJobHandle ejbHandle = (EjbGlobalJobHandle) jobHandle;
		
		for (Timer timer : timerService.getTimers()) {
			try {
    		    Serializable info = timer.getInfo();
    			if (info instanceof EjbTimerJob) {
    				EjbTimerJob job = (EjbTimerJob) info;
    				
    				EjbGlobalJobHandle handle = (EjbGlobalJobHandle) job.getTimerJobInstance().getJobHandle();
    				if (handle.getUuid().equals(ejbHandle.getUuid())) {
    					logger.debug("Job handle {} does match timer and is going to be canceled", jobHandle);
    					localCache.remove(handle.getUuid());
    					try {
    					    timer.cancel();
    					} catch (Throwable e) {
    					    logger.debug("Timer cancel error due to {}", e.getMessage());
    					    return false;
    					}
    					return true;
    				}
    			}
			} catch (NoSuchObjectLocalException e) {
			    logger.debug("Timer {} has already expired or was canceled ", timer);
			}
		}
		logger.debug("Job handle {} does not match any timer on {} scheduler service", jobHandle, this);
		return false;
	}
	
	public TimerJobInstance getTimerByName(String jobName) {
	    
	    if (localCache.containsKey(jobName)) {
	        logger.debug("Found job {} in cache returning", jobName);
	        return localCache.get(jobName);
	    }
	    TimerJobInstance found = null;
	    
		for (Timer timer : timerService.getTimers()) {
		    try {
    			Serializable info = timer.getInfo();
    			if (info instanceof EjbTimerJob) {
    				EjbTimerJob job = (EjbTimerJob) info;
    				
    				EjbGlobalJobHandle handle = (EjbGlobalJobHandle) job.getTimerJobInstance().getJobHandle();
    				localCache.putIfAbsent(jobName, handle.getTimerJobInstance());
    				if (handle.getUuid().equals(jobName)) {
    					logger.debug("Job  {} does match timer and is going to be returned", jobName);
    					found = handle.getTimerJobInstance();
    				}
    			}
		    } catch (NoSuchObjectLocalException e) {
                logger.debug("Timer info for {} was not found ", timer);
            }
		}	
		
		return found;
	}
	
}
