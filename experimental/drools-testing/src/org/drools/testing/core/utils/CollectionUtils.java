package org.drools.testing.core.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * A Collection of utilities that can be used modify collections. Many
 * utilities are provided within the java.util.Collections class and
 * should not be duplicated here.
 * @see java.util.Collections
 */
public class CollectionUtils {
	
	/**
	 * A method used to return a sub collection given an original, a to
	 * and from index. The subCollection is backed by a java.util.ArrayList. This method 
	 * should be used when a subCollection is required to be passed as a 
	 * Serializable object. For example, the method List.subList(int fromIndex, int toIndex)
	 * is not appropriate as it not Serializable. 
	 * @param original The original collection
	 * @param fromIndex The starting index to inclusive add from
	 * @param toIndex The endIndex to inclusive add too
	 * @return Collection with 0 or more elements fom the original.
	 */
	public static Collection getSubCollection(Collection original, int fromIndex, int toIndex)
	{
		String methodName = "getSubCollection(Collection original, int fromIndex, int toIndex)";		
		ArrayList subCollection = new ArrayList();
		Iterator originalIterator = original.iterator();
		int count = 0;
		while (originalIterator.hasNext())
		{
			Object nextObject = originalIterator.next();
			if (count >= fromIndex && count <= toIndex)
			{	
				subCollection.add(nextObject);
			}	
			count++;
		}
		return subCollection;
	}
	
	/**
	 * Convert an Object array to a Collection
	 * @param objectArray
	 * @return
	 */
	public static Collection arrayToCollection(Object[] objectArray)
	{
		ArrayList conversion = new ArrayList();
		for (int i = 0; i < objectArray.length; i++)
		{
			conversion.add(objectArray[i]);
		}
		return conversion;
	}
	
	public static Object getLastItem (Collection items) {
	    Object nextObject = new Object();
	    Iterator i = items.iterator();
	    while (i.hasNext()) {
	        nextObject = i.next();
	    }
	    return nextObject;
	}

}
