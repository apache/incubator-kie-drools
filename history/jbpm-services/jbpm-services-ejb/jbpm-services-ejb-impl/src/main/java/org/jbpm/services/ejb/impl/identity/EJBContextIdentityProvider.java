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

package org.jbpm.services.ejb.impl.identity;

import java.util.Collections;
import java.util.List;

import javax.ejb.EJBContext;

import org.kie.internal.identity.IdentityProvider;


public class EJBContextIdentityProvider implements IdentityProvider {

	private EJBContext context;
	
	public EJBContextIdentityProvider(EJBContext context) {
		this.context = context;
	}
	
	@Override
	public String getName() {
		return context.getCallerPrincipal().getName();
	}

	@Override
	public List<String> getRoles() {
		return Collections.emptyList();
	}

	@Override
	public boolean hasRole(String role) {
		return context.isCallerInRole(role);
	}

}
