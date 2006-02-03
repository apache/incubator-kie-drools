package org.drools.util.proxy;

/**
 * This provides access to "fields" by number.
 * Fields are properties, essentually.
 * 
 * Actually, they are methods are public "getXXX", or "isXXX" and take no parameters, and return something.
 * 
 * The order in which they are numbered are the order in which they appear in the class file.
 * (NOT the order of the attributes which they expose, which according to *my* reading of the bytecode spec,
 * is not defined. 
 * 
 * Of course, if code obfuscators were used this may mean that it is also out of order (but at least consistent).
 * A future enhancement would be to allow annotations to specify the order, or similar.
 * 
 * @author Michael Neale
 */
public interface FieldIndexAccessor {

    Object getField(int index);
    
}
