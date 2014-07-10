package org.jbpm.runtime.manager.impl;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.Context;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.task.TaskService;
import org.kie.internal.runtime.manager.InternalRuntimeManager;

public interface RuntimeEngineInitlializer {

	KieSession initKieSession(Context<?> context, InternalRuntimeManager manager, RuntimeEngine engine);
	
	TaskService initTaskService(Context<?> context, InternalRuntimeManager manager, RuntimeEngine engine);
}
