/**
 * Copyright 2010 JBoss Inc
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

package org.jbpm.task.service.hornetq;

import java.util.HashMap;
import java.util.Map;

import org.drools.task.service.ResponseHandler;
import org.jbpm.task.service.BaseHandler;

public class BaseHornetQHandler implements BaseHandler {

	protected Map<Integer, ResponseHandler> responseHandlers;

	public BaseHornetQHandler() {
		responseHandlers = new HashMap<Integer, ResponseHandler>();
	}

	public void addResponseHandler(int id, ResponseHandler responseHandler) {
		responseHandlers.put( id, responseHandler );
	}

}