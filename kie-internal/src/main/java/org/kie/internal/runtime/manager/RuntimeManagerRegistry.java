/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.internal.runtime.manager;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import org.kie.api.runtime.manager.RuntimeManager;

public class RuntimeManagerRegistry {
	
	private static RuntimeManagerRegistry INSTANCE = new RuntimeManagerRegistry();

	protected volatile ConcurrentHashMap<String, RuntimeManager> registeredManager = new ConcurrentHashMap<String, RuntimeManager>();
		
	private RuntimeManagerRegistry() {
		
	}
	
	public static RuntimeManagerRegistry get() {
		return INSTANCE;
	}
	
	public synchronized void register(RuntimeManager manager) {
		if (registeredManager.containsKey(manager.getIdentifier())) {
			throw new IllegalStateException("RuntimeManager is already registered with id " + manager.getIdentifier());
		}
		this.registeredManager.put(manager.getIdentifier(), manager);
	}
	
	public synchronized void remove(RuntimeManager manager) {

		this.registeredManager.remove(manager.getIdentifier());
	}
	
	public synchronized void remove(String identifier) {

		this.registeredManager.remove(identifier);
	}
	
	public RuntimeManager getManager(String id) {
		return this.registeredManager.get(id);
	}
	
	public boolean isRegistered(String id) {
		return this.registeredManager.containsKey(id);
	}
	
	public Collection<String> getRegisteredIdentifiers() {
	    return Collections.unmodifiableCollection(this.registeredManager.keySet());
	}
	
}
