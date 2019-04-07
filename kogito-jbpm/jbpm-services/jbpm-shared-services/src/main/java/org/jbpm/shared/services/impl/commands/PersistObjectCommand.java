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

package org.jbpm.shared.services.impl.commands;

import org.jbpm.shared.services.impl.JpaPersistenceContext;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;

public class PersistObjectCommand implements ExecutableCommand<Void> {

	private static final long serialVersionUID = -4014807273522465028L;

	private Object[] objectsToPersist;

	public PersistObjectCommand(Object ...objects) {
		this.objectsToPersist = objects;
	}
	
	@Override
	public Void execute(Context context ) {
		JpaPersistenceContext ctx = (JpaPersistenceContext) context;
		if (objectsToPersist != null) {
			for (Object object : objectsToPersist) {
				ctx.persist(object);
			}
		}
		return null;
	}
}
