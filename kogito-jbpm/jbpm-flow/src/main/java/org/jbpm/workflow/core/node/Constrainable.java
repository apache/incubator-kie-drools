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

package org.jbpm.workflow.core.node;

import org.jbpm.workflow.core.Constraint;
import org.jbpm.workflow.core.impl.ConnectionRef;

public interface Constrainable {
	
	/**
	 * Adds the given constraint.
	 * In cases where the constraint is associated with a specific connection,
	 * this connection will be identified using a ConnectionRef.  In other cases
	 * the ConnectionRef will be null and can be ignored.
	 * @param connection
	 * @param constraint
	 */
	public void addConstraint(ConnectionRef connection, Constraint constraint);


}
