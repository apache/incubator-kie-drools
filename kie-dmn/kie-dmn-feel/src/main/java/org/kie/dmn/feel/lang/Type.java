package org.kie.dmn.feel.lang;

/**
 * A type definition interface
 */
public interface Type {

    String getName();
    
    /**
     * Definition of `instance of` accordingly to FEEL specifications Table 49.
     * @param o
     * @return if o is instance of the type represented by this type. If the parameter is null, returns false. 
     */
    boolean isInstanceOf(Object o);
    
    /**
     * Check if the value passed as parameter can be assigned to this type.
     * @param value
     * @return if value can be assigned to the type represented by this type. If the parameter is null, returns true. 
     */
    boolean isAssignableValue(Object value);

    /**
     * Check if this type does Conform to specified type <code>t</code> accordingly to FEEL DMN specification 10.3.2.9.2 Type Conformance
     * @param t
     * @return if this type does conform to specified type.
     */
    boolean conformsTo(Type t);
}
