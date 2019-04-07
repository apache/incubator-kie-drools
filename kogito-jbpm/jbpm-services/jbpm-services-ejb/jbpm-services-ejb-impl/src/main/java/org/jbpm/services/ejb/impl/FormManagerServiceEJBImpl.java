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
package org.jbpm.services.ejb.impl;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import org.jbpm.kie.services.impl.FormManagerServiceImpl;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Lock(LockType.READ)
public class FormManagerServiceEJBImpl extends FormManagerServiceImpl{

	@Lock(LockType.WRITE)
	@Override
	public void registerForm(String deploymentId, String key, String formContent) {
		super.registerForm(deploymentId, key, formContent);
	}

	@Lock(LockType.WRITE)
	@Override
	public void unRegisterForms(String deploymentId) {
		super.unRegisterForms(deploymentId);
	}
    
}
