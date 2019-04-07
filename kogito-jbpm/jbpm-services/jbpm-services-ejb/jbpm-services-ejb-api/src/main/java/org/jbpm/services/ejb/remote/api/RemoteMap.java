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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Dedicated <code>Map</code> implementation to simplify remote invocation of service methods that accept custom object input.
 * This map is backed by an internal map that holds already serialized content to avoid additional serialization on sending time.
 * That removes the burden of ensuring that container will know about all custom data model classes as part of global classpath.
 *  
 * This implementation does not support all methods that are usually not used when sending data. It shall be considered only as a wrapper
 * and not actual and complete implementation of a map. 
 *
 */
public class RemoteMap extends AbstractRemoteObject implements Map<String, Object>, Serializable {
	
	private static final long serialVersionUID = 6538775214677901097L;
	
	private HashMap<String, byte[]> data = new HashMap<String, byte[]>();

	@Override
	public void clear() {
		data.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return data.containsKey(key);
	}

	@Override
	public Object get(Object key) {
		if (data.containsKey(key)) {
			byte[] bytes = data.get(key);
			
			return deserialize(bytes);
		}
		return null;
	}

	@Override
	public boolean isEmpty() {
		return data.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		return data.keySet();
	}

	@Override
	public Object put(String key, Object value) {
		byte[] bytes = serialize(value);
		
		data.put(key, bytes);
		return null;
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> input) {
		if (input == null) {
			throw new IllegalArgumentException("Input map cannot be null");
		}
		
		for (Entry<? extends String, ? extends Object> entry : input.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public Object remove(Object key) {
		if (data.containsKey(key)) {
			byte[] bytes = data.remove(key);
			
			return deserialize(bytes);
		}
		return null;
	}

	@Override
	public int size() {
		return data.size();
	}

	@Override
	public Collection<Object> values() {
		throw new UnsupportedOperationException("Not suported operation");
	}	

	@Override
	public boolean containsValue(Object arg0) {
		throw new UnsupportedOperationException("Not suported operation");
	}

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		throw new UnsupportedOperationException("Not suported operation");
	}

}
