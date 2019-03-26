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

import java.util.HashMap;
import java.util.Map;

import org.jbpm.services.ejb.remote.api.RemoteMap;
import org.jbpm.services.ejb.remote.api.RemoteObject;

public class RemoteObjectProcessor {

	@SuppressWarnings("unchecked")
	public static <T> T processRemoteObjects(T variables, ClassLoader cl) {
		if (variables instanceof RemoteMap) {
			RemoteMap remoteMap = ((RemoteMap) variables);
			remoteMap.setClassLoader(cl);
			
			Map<String, Object> data = new HashMap<String, Object>();
			
			for (String key : remoteMap.keySet()) {
				data.put(key, remoteMap.get(key));
			}
			
			return (T) data;
		} else if (variables instanceof RemoteObject) {
			((RemoteObject) variables).setClassLoader(cl);
			
			return (T) ((RemoteObject) variables).get();
		}
		
		return null;
	}
}
