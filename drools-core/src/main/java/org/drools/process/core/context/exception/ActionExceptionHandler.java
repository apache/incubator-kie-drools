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

package org.drools.process.core.context.exception;

import java.io.Serializable;

import org.drools.workflow.core.DroolsAction;

public class ActionExceptionHandler implements ExceptionHandler, Serializable {
	
	private static final long serialVersionUID = 400L;
	
	private String faultVariable;
	private DroolsAction action;

	public String getFaultVariable() {
		return faultVariable;
	}

	public void setFaultVariable(String faultVariable) {
		this.faultVariable = faultVariable;
	}

	public DroolsAction getAction() {
		return action;
	}

	public void setAction(DroolsAction action) {
		this.action = action;
	}
	
	public String toString() {
		return action == null ? "" : action.toString();
	}

}
