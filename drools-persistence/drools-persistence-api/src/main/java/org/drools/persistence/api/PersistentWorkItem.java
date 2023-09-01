package org.drools.persistence.api;

/**
 * Basic interface so that persisteces can avoid depending on
 * the JPA implementation of a work item.
 */
public interface PersistentWorkItem extends Transformable {

	Long getId();
	
	void setId(Long id);

	String getProcessInstanceId();
}
