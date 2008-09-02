package org.drools.persistence.memory;

public interface MemoryObject {
	
	void setData(byte[] data, String id);
	
	byte[] getData(String id);

}
