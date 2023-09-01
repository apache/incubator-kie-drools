package org.drools.persistence.api;

/**
 * Basic interface so that persisteces can avoid depending on
 * the JPA implementation of a session.
 */
public interface PersistentSession extends Transformable {

	Long getId();
	
	void setId(Long id);

	byte[] getData();

}
