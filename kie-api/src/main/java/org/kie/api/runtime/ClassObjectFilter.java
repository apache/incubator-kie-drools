package org.kie.api.runtime;

/**
 * Filters objects by class, only accepting objects of the class specified in the constructor
 */
public class ClassObjectFilter
    implements
    ObjectFilter {
    private Class<?> clazz;

    /**
     * The Allowed Class type
     * @param clazz
     */
    public ClassObjectFilter(Class<?> clazz) {
        this.clazz = clazz;
    }

    /**
     * @return true if the Iterator accepts the given object according to its class.
     */
    public boolean accept(Object object) {
        return this.clazz.isAssignableFrom( object.getClass() );
    }

    public Class getFilteredClass() {
        return clazz;
    }
}
