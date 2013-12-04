package org.kie.internal.task.api;

public interface TaskPersistenceContextManager {

	TaskPersistenceContext getPersistenceContext();
	
	void beginCommandScopedEntityManager();
	
	void endCommandScopedEntityManager();
	
	/**
     * Executes the necessary actions in order to clean up and dispose of the internal fields of this instance.
     */
    void dispose();
}
