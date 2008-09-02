package org.drools.persistence.memory;

import java.util.HashMap;

public class HashMapMemoryObject implements MemoryObject {

	private HashMap<String, byte[]> map;
	
	public HashMapMemoryObject(HashMap<String, byte[]> map) {		
		this.map = map;
	}
	
	public byte[] getData(String id) {
		return map.get(id);
	}

	public void setData(byte[] data, String id) {
		map.put(id, data);
	}

}
