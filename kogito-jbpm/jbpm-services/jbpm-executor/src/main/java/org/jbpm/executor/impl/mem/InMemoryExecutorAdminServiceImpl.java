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

import java.util.Map;

import org.kie.api.executor.ErrorInfo;
import org.kie.api.executor.ExecutorAdminService;
import org.kie.api.executor.RequestInfo;



public class InMemoryExecutorAdminServiceImpl implements ExecutorAdminService {

	private InMemoryExecutorStoreService storeService;
	
	public void setStoreService(InMemoryExecutorStoreService storeService) {
		this.storeService = storeService;
	}
	
	public InMemoryExecutorAdminServiceImpl(boolean active) {
		
	}

	@Override
	public int clearAllRequests() {
		Map<Long, RequestInfo> requests = storeService.getRequests();
		int size = requests.size();
		requests.clear();
		Map<Long, RequestInfo> processedRequests = storeService.getProcessedRequests();
		size += processedRequests.size();
		processedRequests.clear();
		return size;
	}

	@Override
	public int clearAllErrors() {
		Map<Long, ErrorInfo> errors = storeService.getErrors();
		int size = errors.size();
		errors.clear();
		return size;
	}

}
