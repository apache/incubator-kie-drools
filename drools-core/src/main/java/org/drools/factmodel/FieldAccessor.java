package org.drools.factmodel;


/**
 * <p><b>Title:</b> FieldAccessor</p>
 * <p><b>Description:</b> An interface for dynamic generated FieldAccessor classes </p>
 * <p><b>Copyright:</b> Copyright (c) 2004-2006</p>
 * <p><b>Company:</b> Auster Solutions</p>
 *
 * @author etirelli
 * @version $Id: FieldAccessor.java 203 2006-03-20 21:44:48Z etirelli $
 */
public interface FieldAccessor {
    
    public void setValue(Object instance, Object value);
    
    public Object getValue(Object instance);

}
