/*
 * Copyright 2014 JBoss by Red Hat.
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

package org.jbpm.executor.impl.mem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.kie.internal.executor.api.ErrorInfo;
import org.kie.internal.executor.api.ExecutorQueryService;
import org.kie.internal.executor.api.RequestInfo;
import org.kie.internal.executor.api.STATUS;

@SuppressWarnings("unchecked")
public class InMemoryExecutorQueryServiceImpl implements ExecutorQueryService {

	private InMemoryExecutorStoreService storeService;
	
	public void setStoreService(InMemoryExecutorStoreService storeService) {
		this.storeService = storeService;
	}
	
	public InMemoryExecutorQueryServiceImpl(boolean active) {
		
	}
	
	@Override
	public List<RequestInfo> getPendingRequests() {
		Map<Long, RequestInfo> requests = storeService.getRequests();
		
		return (List<RequestInfo>) CollectionUtils.select(requests.values(), new GetRequestsByStatus(STATUS.QUEUED, STATUS.RETRYING));
	}

	@Override
	public List<RequestInfo> getPendingRequestById(Long id) {
		List<RequestInfo> requests = new ArrayList<RequestInfo>();
		RequestInfo request = storeService.findRequest(id);
		if (request != null && request.getStatus() == STATUS.QUEUED) {
			requests.add(request);
		}
		return requests;
	}

	@Override
	public RequestInfo getRequestById(Long id) {
		return storeService.findRequest(id);
	}

	
	@Override
	public List<RequestInfo> getRequestByBusinessKey(String businessKey) {
		Map<Long, RequestInfo> requests = storeService.getRequests();
		return (List<RequestInfo>) CollectionUtils.select(requests.values(), new GetRequestsByKey(businessKey));
	}

	@Override
	public List<ErrorInfo> getErrorsByRequestId(Long id) {
		
		return (List<ErrorInfo>) storeService.findRequest(id).getErrorInfo();
	}

	@Override
	public List<RequestInfo> getQueuedRequests() {
		Map<Long, RequestInfo> requests = storeService.getRequests();
		
		return (List<RequestInfo>) CollectionUtils.select(requests.values(), new GetRequestsByStatus(STATUS.QUEUED));
	}

	@Override
	public List<RequestInfo> getCompletedRequests() {
		Map<Long, RequestInfo> requests = storeService.getProcessedRequests();
		
		return (List<RequestInfo>) CollectionUtils.select(requests.values(), new GetRequestsByStatus(STATUS.DONE));
	}

	@Override
	public List<RequestInfo> getInErrorRequests() {
		Map<Long, RequestInfo> requests = storeService.getProcessedRequests();
		
		return (List<RequestInfo>) CollectionUtils.select(requests.values(), new GetRequestsWitError());
	}

	@Override
	public List<RequestInfo> getCancelledRequests() {
		Map<Long, RequestInfo> requests = storeService.getProcessedRequests();
		
		return (List<RequestInfo>) CollectionUtils.select(requests.values(), new GetRequestsByStatus(STATUS.CANCELLED));
	}

	@Override
	public List<ErrorInfo> getAllErrors() {
		return new ArrayList<ErrorInfo>(storeService.getErrors().values());
	}

	@Override
	public List<RequestInfo> getAllRequests() {
		Map<Long, RequestInfo> requests = new HashMap<Long, RequestInfo>(storeService.getRequests());
		requests.putAll(storeService.getProcessedRequests());
		return new ArrayList<RequestInfo>(requests.values());
	}

	@Override
	public List<RequestInfo> getRunningRequests() {
		Map<Long, RequestInfo> requests = storeService.getRequests();
		
		return (List<RequestInfo>) CollectionUtils.select(requests.values(), new GetRequestsByStatus(STATUS.RUNNING));
	}

	@Override
	public List<RequestInfo> getFutureQueuedRequests() {
		return getQueuedRequests();
	}

	@Override
	public List<RequestInfo> getRequestsByStatus(List<STATUS> statuses) {
		Map<Long, RequestInfo> requests = new HashMap<Long, RequestInfo>(storeService.getRequests());
		requests.putAll(storeService.getProcessedRequests());
		
		return (List<RequestInfo>) CollectionUtils.select(requests.values(), new GetRequestsByStatus(statuses));
	}

	@Override
	public RequestInfo getRequestForProcessing() {
		
		return storeService.getAndLockFirst();
	}
	
	private class GetRequestsByStatus implements Predicate {
		
		private List<STATUS> statuses;
		
		GetRequestsByStatus(STATUS... status) {
			this.statuses = Arrays.asList(status);
		}
		
		GetRequestsByStatus(List<STATUS> statuses) {
			this.statuses = statuses;
		}
		

		@Override
		public boolean evaluate(Object object) {
			if (object instanceof RequestInfo) {
				if (statuses.contains(((RequestInfo)object).getStatus())) {
					return true;
				}
			}
			return false;
		}
		
	}
	
	private class GetRequestsByKey implements Predicate {
		
		private String key;
		
		GetRequestsByKey(String key) {
			this.key = key;
		}

		@Override
		public boolean evaluate(Object object) {
			if (object instanceof RequestInfo) {
				if (key.equals(((RequestInfo)object).getKey())) {
					return true;
				}
			}
			return false;
		}
		
	}
	
	private class GetRequestsWitError implements Predicate {
		
		GetRequestsWitError() {
			
		}

		@Override
		public boolean evaluate(Object object) {
			if (object instanceof RequestInfo) {
				if (!((RequestInfo)object).getErrorInfo().isEmpty()) {
					return true;
				}
			}
			return false;
		}
		
	}
	


}
