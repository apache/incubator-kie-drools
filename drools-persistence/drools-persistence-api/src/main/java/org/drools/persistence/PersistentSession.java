package org.drools.persistence;

public interface PersistentSession extends Transformable {

	Long getId();
	
	void setId(Long id);

	byte[] getData();

}
