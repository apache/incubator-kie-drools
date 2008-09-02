package org.drools.persistence.memory;

public class DefaultMemoryObject implements MemoryObject {

	private byte[] data;
	
	public byte[] getData(String id) {
		return data;
	}

	public void setData(byte[] data, String id) {
		this.data = data;
	}

}
