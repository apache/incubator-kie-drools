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

package org.jbpm.runtime.manager.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.kie.internal.runtime.Cacheable;
import org.kie.internal.runtime.manager.CacheManager;

/**
 * Default implementation of CacheManager that is backed by <code>ConcurrentHashMap</code>.
 * Allows to close <code>Cacheable</code> items from cache upon dispose.
 */
public class CacheManagerImpl implements CacheManager {
	
	private ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<String, Object>();

	@Override
	public void add(String key, Object value) {
		cache.put(key, value);
	}

	@Override
	public Object get(String key) {		
		return cache.get(key);
	}

	@Override
	public Object remove(String key) {
		return cache.remove(key);
	}

	@Override
	public void dispose() {
		for (Map.Entry<String, Object> entry : cache.entrySet()) {
			if (entry.getValue() instanceof Cacheable) {
				((Cacheable) entry.getValue()).close();
			}
		}
		cache.clear();
	}

}
