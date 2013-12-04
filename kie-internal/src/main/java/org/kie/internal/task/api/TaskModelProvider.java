package org.kie.internal.task.api;

import java.util.ServiceLoader;

public class TaskModelProvider {
	
	private static ServiceLoader<TaskModelFactory> serviceLoader = ServiceLoader.load(TaskModelFactory.class);

	public static TaskModelFactory getFactory() {
		for (TaskModelFactory factory : serviceLoader) {
			return factory;
		}
		
		throw new IllegalStateException("Cannot find any TaskModelFactory");
	}
}
