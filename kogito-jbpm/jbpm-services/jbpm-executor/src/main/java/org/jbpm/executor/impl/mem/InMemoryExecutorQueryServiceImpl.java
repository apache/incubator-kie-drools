/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.kie.api.executor.ErrorInfo;
import org.kie.api.executor.RequestInfo;
import org.kie.api.executor.STATUS;
import org.kie.api.runtime.query.QueryContext;
import org.kie.internal.executor.api.ExecutorQueryService;


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
    public List<RequestInfo> getRequestByBusinessKey(String businessKey, QueryContext queryContext) {
        Map<Long, RequestInfo> requests = storeService.getRequests();
        List<RequestInfo> requestsByBusinessKey = (List<RequestInfo>) CollectionUtils.select(requests.values(), new GetRequestsByKey(businessKey));
        return applyPaginition(requestsByBusinessKey, queryContext);
    }

    @Override
    public List<RequestInfo> getRequestByCommand(String command, QueryContext queryContext) {
        Map<Long, RequestInfo> requests = storeService.getRequests();
        List<RequestInfo> requestsByCommand = (List<RequestInfo>) CollectionUtils.select(requests.values(), new GetRequestsByCommand(command));
        return applyPaginition(requestsByCommand, queryContext);
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
	
    private class GetRequestsByCommand implements Predicate {
        
        private String command;
        
        GetRequestsByCommand(String command) {
            this.command = command;
        }

        @Override
        public boolean evaluate(Object object) {
            if (object instanceof RequestInfo) {
                if (command.equals(((RequestInfo)object).getCommandName())) {
                    return true;
                }
            }
            return false;
        }
        
    }
    
    private class GetRequestsByDeploymentId implements Predicate {
        
        private String deploymentId;
        
        GetRequestsByDeploymentId(String deploymentId) {
            this.deploymentId = deploymentId;
        }

        @Override
        public boolean evaluate(Object object) {
            if (object instanceof RequestInfo) {
                if (deploymentId.equals(((RequestInfo)object).getDeploymentId())) {
                    return true;
                }
            }
            return false;
        }
        
    }
    
    private class GetRequestsByProcessInstanceId implements Predicate {
        
        private Long processInstanceId;
        
        GetRequestsByProcessInstanceId(Long processInstanceId) {
            this.processInstanceId = processInstanceId;
        }

        @Override
        public boolean evaluate(Object object) {
            if (object instanceof RequestInfo) {
                if (processInstanceId.equals(((RequestInfo)object).getProcessInstanceId())) {
                    return true;
                }
            }
            return false;
        }
        
    }    

    @Override
    public List<RequestInfo> getPendingRequests(QueryContext queryContext) {
        return applyPaginition(getPendingRequests(), queryContext);
    }

    @Override
    public List<RequestInfo> getQueuedRequests(QueryContext queryContext) {
        return applyPaginition(getQueuedRequests(), queryContext);
    }

    @Override
    public List<RequestInfo> getCompletedRequests(QueryContext queryContext) {
        return applyPaginition(getCompletedRequests(), queryContext);
    }

    @Override
    public List<RequestInfo> getInErrorRequests(QueryContext queryContext) {
        return applyPaginition(getInErrorRequests(), queryContext);
    }

    @Override
    public List<RequestInfo> getCancelledRequests(QueryContext queryContext) {
        return applyPaginition(getCancelledRequests(), queryContext);
    }

    @Override
    public List<ErrorInfo> getAllErrors(QueryContext queryContext) {
        return applyPaginition(getAllErrors(), queryContext);
    }

    @Override
    public List<RequestInfo> getAllRequests(QueryContext queryContext) {
        
        return applyPaginition(getAllRequests(), queryContext);
    }

    @Override
    public List<RequestInfo> getRunningRequests(QueryContext queryContext) {
        
        return applyPaginition(getRunningRequests(), queryContext);
    }

    @Override
    public List<RequestInfo> getFutureQueuedRequests(QueryContext queryContext) {
        
        return applyPaginition(getFutureQueuedRequests(), queryContext);
    }

    @Override
    public List<RequestInfo> getRequestsByStatus(List<STATUS> statuses, QueryContext queryContext) {

        return applyPaginition(getRequestsByStatus(statuses), queryContext);
    }
	
    protected <T> List<T> applyPaginition(List<T> input, QueryContext queryContext) {
        
        int end = queryContext.getOffset() + queryContext.getCount();
        if (input.size() < queryContext.getOffset()) {
            // no elements in given range
            return new ArrayList<T>();
        } else if (input.size() >= end) {
            return Collections.unmodifiableList(new ArrayList<T>(input.subList(queryContext.getOffset(), end)));
        } else if (input.size() < end) {
            return Collections.unmodifiableList(new ArrayList<T>(input.subList(queryContext.getOffset(), input.size())));
        } else {
            return Collections.unmodifiableList(input);
        }
    }

    @Override
    public RequestInfo getRequestForProcessing(Long requestId) {        
        return storeService.removeRequest(requestId);
    }

    @Override
    public List<RequestInfo> getRequestsByBusinessKey(String businessKey, List<STATUS> statuses, QueryContext queryContext) {
        Map<Long, RequestInfo> requests = storeService.getRequests();
        List<RequestInfo> requestsByKey = (List<RequestInfo>) CollectionUtils.select(requests.values(), new GetRequestsByKey(businessKey));
        requestsByKey = (List<RequestInfo>) CollectionUtils.select(requestsByKey, new GetRequestsByStatus(statuses));
        return applyPaginition(requestsByKey, queryContext);
    }

    @Override
    public List<RequestInfo> getRequestsByCommand(String command, List<STATUS> statuses, QueryContext queryContext) {
        Map<Long, RequestInfo> requests = storeService.getRequests();
        List<RequestInfo> requestsByCommand = (List<RequestInfo>) CollectionUtils.select(requests.values(), new GetRequestsByCommand(command));
        requestsByCommand = (List<RequestInfo>) CollectionUtils.select(requestsByCommand, new GetRequestsByStatus(statuses));
        return applyPaginition(requestsByCommand, queryContext);
    }

    @Override
    public List<RequestInfo> getRequestsByDeployment(String deploymentId, List<STATUS> statuses, QueryContext queryContext) {
        Map<Long, RequestInfo> requests = storeService.getRequests();
        List<RequestInfo> requestsByDeployment = (List<RequestInfo>) CollectionUtils.select(requests.values(), new GetRequestsByDeploymentId(deploymentId));
        requestsByDeployment = (List<RequestInfo>) CollectionUtils.select(requestsByDeployment, new GetRequestsByStatus(statuses));
        return applyPaginition(requestsByDeployment, queryContext);
    }

    @Override
    public List<RequestInfo> getRequestsByProcessInstance(Long processInstanceId, List<STATUS> statuses, QueryContext queryContext) {
        Map<Long, RequestInfo> requests = storeService.getRequests();
        List<RequestInfo> requestsByProcessInstance = (List<RequestInfo>) CollectionUtils.select(requests.values(), new GetRequestsByProcessInstanceId(processInstanceId));
        requestsByProcessInstance = (List<RequestInfo>) CollectionUtils.select(requestsByProcessInstance, new GetRequestsByStatus(statuses));
        return applyPaginition(requestsByProcessInstance, queryContext);
    }

}
