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
package org.jbpm.services.task.assignment;

import java.io.Serializable;

import org.kie.api.task.model.User;
import org.kie.internal.task.api.TaskModelProvider;

public class UserTaskLoad implements Serializable, Comparable<UserTaskLoad> {
	private static final long serialVersionUID = 19630331L;
	private String calculatorIdentifier;
	private User user;
	private Double calculatedLoad;

	public UserTaskLoad(String calculatorIdentifier, User user, Double calculatedLoad) {
		super();
		this.calculatorIdentifier = calculatorIdentifier;
		this.user = user;
		this.calculatedLoad = calculatedLoad;
	}
	
	public UserTaskLoad(String calculatorIdentifier, String user, Double calculatedLoad) {
		super();
		this.calculatorIdentifier = calculatorIdentifier;
		this.user = TaskModelProvider.getFactory().newUser(user);
		this.calculatedLoad = calculatedLoad;
	}
	
	public UserTaskLoad(String calculatorIdentifier, User user) {
		super();
		this.calculatorIdentifier = calculatorIdentifier;
		this.user = user;
		this.calculatedLoad = Double.NaN;
	}
	
	public UserTaskLoad(String calculatorIdentifier, String user) {
		super();
		this.calculatorIdentifier = calculatorIdentifier;
		this.user = TaskModelProvider.getFactory().newUser(user);
		this.calculatedLoad = Double.NaN;
	}

	public String getCalculatorIdentifier() {
		return calculatorIdentifier;
	}
	public void setCalculatorIdentifier(String calculatorIdentifier) {
		this.calculatorIdentifier = calculatorIdentifier;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Double getCalculatedLoad() {
		return calculatedLoad;
	}
	public void setCalculatedLoad(Double calculatedLoad) {
		this.calculatedLoad = calculatedLoad;
	}

	@Override
	public int compareTo(UserTaskLoad o) {
		if (o == null) {
			throw new IllegalArgumentException("Illegal attempt to compare UserTaskLoad with a null object");
		}
		if (this.calculatedLoad.isNaN() || o.calculatedLoad.isNaN()) {
			throw new IllegalStateException("Uninitialized UserTaskLoad encountered during UserTaskLoad comparison");
		}
		
		return this.calculatedLoad.compareTo(o.calculatedLoad);
	}
	
	@Override
	public String toString() {
		return "UserTaskLoad { user = "+user.getId()+", calculator = "+calculatorIdentifier+", load = "+calculatedLoad+"}";
	}
}
