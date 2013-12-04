package org.kie.internal.runtime.manager;

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
	
}
