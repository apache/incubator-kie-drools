package org.drools.persistence;

public interface PersistentWorkItem extends Transformable {

	Long getId();
	
	void setId(Long id);
	
	long getProcessInstanceId();
}
