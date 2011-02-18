package org.drools;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MockPersistentSet extends AbstractSet implements Set
{

	private Set set;
	
	private boolean exception;

	public MockPersistentSet()
	{
		exception = true;
		set = new HashSet();
	}
	
	public MockPersistentSet(boolean exception)
	{
		this.exception = exception;
		set = new HashSet();
	}
	
	public int size()
	{
		return set.size();
	}
	
	public Iterator iterator()
	{
		return set.iterator();
	}
	
	public boolean addAll(Collection c)
	{
		if (exception)
			throw new MockPersistentSetException("error message like PersistentSet");
		return set.addAll(c);
	}

}
