package org.drools.util;

import junit.framework.TestCase;
import org.drools.util.ObjectHashMap;

public class ObjectHashMapTest2 extends TestCase {

	public ObjectHashMapTest2() {
		super();
	}
	
	public void testJUHashmap() {
		java.util.HashMap map = new java.util.HashMap();
		assertNotNull(map);
		int count = 1000;
		for (int idx=0; idx < count; idx++) {
			String key = "key" + idx;
			String val = "value" + idx;
			map.put(key, val);
			assertEquals(val,map.get(key));
		}
	}

	public void testStringData() {
		ObjectHashMap map = new ObjectHashMap();
		assertNotNull(map);
		int count = 1000;
		for (int idx=0; idx < count; idx++) {
			String key = "key" + idx;
			String val = "value" + idx;
			map.put(key, val);
			assertEquals(val,map.get(key));
		}
	}

	public void testIntegerData() {
		ObjectHashMap map = new ObjectHashMap();
		assertNotNull(map);
		int count = 1000;
		for (int idx=0; idx < count; idx++) {
			Integer key = new Integer(idx);
			Integer val = new Integer(idx);
			map.put(key, val);
			assertEquals(val,map.get(key));
		}
	}
	
	public void testStringData2() {
		int count = 100000;
		ObjectHashMap map = new ObjectHashMap();
		assertNotNull(map);
		long start = System.currentTimeMillis();
		for (int idx=0; idx < count; idx++) {
			String key = "key" + idx;
			String strval = "value" + idx;
			map.put(key,strval);
		}
		long end = System.currentTimeMillis();
		System.out.println("Custom ObjectHashMap ET - " + (end-start));
	}
	
	public void testJUHashMap1() {
		int count = 100000;
		java.util.HashMap map = new java.util.HashMap();
		assertNotNull(map);
		long start = System.currentTimeMillis();
		for (int idx=0; idx < count; idx++) {
			String key = "key" + idx;
			String strval = "value" + idx;
			map.put(key,strval);
		}
		long end = System.currentTimeMillis();
		System.out.println("java.util.HashMap ET - " + (end-start));
	}

	public void testStringData3() {
		int count = 100000;
		ObjectHashMap map = new ObjectHashMap();
		assertNotNull(map);
		for (int idx=0; idx < count; idx++) {
			String key = "key" + idx;
			String strval = "value" + idx;
			map.put(key,strval);
		}
		long start = System.currentTimeMillis();
		for (int idx=0; idx < count; idx++) {
			String key = "key" + idx;
			map.get(key);
		}
		long end = System.currentTimeMillis();
		System.out.println("Custom ObjectHashMap get(key) ET - " + (end-start));
	}
	
	public void testJUHashMap2() {
		int count = 100000;
		java.util.HashMap map = new java.util.HashMap();
		assertNotNull(map);
		for (int idx=0; idx < count; idx++) {
			String key = "key" + idx;
			String strval = "value" + idx;
			map.put(key,strval);
		}
		long start = System.currentTimeMillis();
		for (int idx=0; idx < count; idx++) {
			String key = "key" + idx;
			map.get(key);
		}
		long end = System.currentTimeMillis();
		System.out.println("java.util.HashMap get(key) ET - " + (end-start));
	}
	
	public void testStringData4() {
		int count = 100000;
		ObjectHashMap map = new ObjectHashMap();
		assertNotNull(map);
		for (int idx=0; idx < count; idx++) {
			String key = "key" + idx;
			String strval = "value" + idx;
			map.put(key,strval);
		}
		long start = System.currentTimeMillis();
		org.drools.util.Iterator itr = map.iterator();
		Object val = null;
		while ( (val = itr.next()) != null) {
			val.hashCode();
		}
		long end = System.currentTimeMillis();
		System.out.println("Custom ObjectHashMap iterate ET - " + (end-start));
	}

	public void testJUHashMap3() {
		int count = 100000;
		java.util.HashMap map = new java.util.HashMap();
		assertNotNull(map);
		for (int idx=0; idx < count; idx++) {
			String key = "key" + idx;
			String strval = "value" + idx;
			map.put(key,strval);
		}
		long start = System.currentTimeMillis();
		java.util.Iterator itr = map.values().iterator();
		Object val = null;
		while ( itr.hasNext()) {
			itr.next().hashCode();
		}
		long end = System.currentTimeMillis();
		System.out.println("java.util.HashMap iterate ET - " + (end-start));
	}
}