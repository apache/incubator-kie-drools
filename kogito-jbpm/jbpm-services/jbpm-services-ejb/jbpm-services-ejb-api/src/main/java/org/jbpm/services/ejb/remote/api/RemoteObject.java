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

package org.jbpm.services.ejb.remote.api;

import java.io.Serializable;

/**
 * Similar to a <code>RemoteMap</code> acts as a wrapper for object instances to be send over wire.
 *
 */
public class RemoteObject extends AbstractRemoteObject implements Serializable {

	private static final long serialVersionUID = 61486849584640922L;

	private byte[] content;
	
	public RemoteObject(Object object) {
		this.content = serialize(object);
	}
	
	public Object get() {
		if (this.content == null) {
			return null;
		}
		
		return deserialize(content);
	}
}
