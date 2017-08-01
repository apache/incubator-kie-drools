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

package org.jbpm.executor.impl.mem;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

import org.jbpm.executor.ExecutorServiceFactory;
import org.jbpm.executor.impl.event.ExecutorEventSupport;
import org.kie.api.executor.ErrorInfo;
import org.kie.api.executor.ExecutorStoreService;
import org.kie.api.executor.RequestInfo;
import org.kie.api.executor.STATUS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InMemoryExecutorStoreService implements ExecutorStoreService {
	
	private static final Logger logger = LoggerFactory.getLogger(InMemoryExecutorStoreService.class);
	
	private AtomicLong requestIds = new AtomicLong();
	private AtomicLong errorIds = new AtomicLong();
	
	private static ConcurrentNavigableMap<Long, RequestInfo> requests = new ConcurrentSkipListMap<Long, RequestInfo>();
	private static ConcurrentNavigableMap<Long, RequestInfo> processedRequests = new ConcurrentSkipListMap<Long, RequestInfo>();
	private static ConcurrentNavigableMap<Long, ErrorInfo> errors = new ConcurrentSkipListMap<Long, ErrorInfo>();

	private ExecutorEventSupport eventSupport = new ExecutorEventSupport();
	
	public InMemoryExecutorStoreService(boolean active) {
		
	}
	        
    public void setEventSupport(ExecutorEventSupport eventSupport) {
        this.eventSupport = eventSupport;
    }
	
	@Override
	public synchronized void persistRequest(RequestInfo request) {
		setId(request, requestIds.incrementAndGet());
		logger.debug("Storing request {}", request);
		requests.put(request.getId(), request);
	}

	@Override
	public synchronized void updateRequest(RequestInfo request) {
		if (request.getStatus() == STATUS.CANCELLED 
			|| request.getStatus() == STATUS.DONE
			|| request.getStatus() == STATUS.ERROR
			|| request.getStatus() == STATUS.RUNNING) {
			logger.debug("Updating request by removing it as it was already processed {}", request);
			requests.remove(request.getId());
			if (processedRequests.size() > 100) {
				processedRequests.pollFirstEntry();
			}
			processedRequests.put(request.getId(), request);
			
			// process errors if any 
			if (request.getErrorInfo() != null) {
				for (ErrorInfo error : request.getErrorInfo()) {
					if (error.getId() == null) {
						persistError(error);
					}
				}
			}
			return;
		}
		logger.debug("Regular update of request {}", request);
		requests.put(request.getId(), request);
	}

	@Override
	public synchronized RequestInfo removeRequest(Long requestId) {
		RequestInfo request = requests.remove(requestId);
		if (processedRequests.size() > 100) {
			processedRequests.pollFirstEntry();
		}
		processedRequests.put(request.getId(), request);
		request.setStatus(STATUS.CANCELLED);
		return request;
	}

	@Override
	public synchronized RequestInfo findRequest(Long id) {
		return requests.get(id);
	}

	@Override
	public synchronized void persistError(ErrorInfo error) {
		setId(error, errorIds.incrementAndGet());
		errors.put(error.getId(), error);

	}

	@Override
	public synchronized void updateError(ErrorInfo error) {
		errors.put(error.getId(), error);

	}

	@Override
	public synchronized ErrorInfo removeError(Long errorId) {
		return errors.remove(errorId);

	}

	@Override
	public synchronized ErrorInfo findError(Long id) {
		return errors.get(id);
	}

	@Override
	public Runnable buildExecutorRunnable() {		
		return ExecutorServiceFactory.buildRunable(eventSupport);
	}
	
	public synchronized RequestInfo getAndLockFirst() {
		if (requests.isEmpty()) {
			return null;
		}
		Long toProceed = requests.firstKey();
		
		return requests.remove(toProceed);
	}
	
	public synchronized Map<Long, RequestInfo> getRequests() {
		return requests;
	}
	
	public synchronized Map<Long, ErrorInfo> getErrors() {
		return errors;
	}
	
	public synchronized Map<Long, RequestInfo> getProcessedRequests() {
		return processedRequests;
	}

	protected void setId(Object object, Long id) {
		try {
			Field idField = object.getClass().getDeclaredField("id");
			idField.setAccessible(true);
			idField.set(object, id);
		} catch (Exception e) {
			throw new IllegalStateException("Unable to set id for object" + object);
		}
	}
}
