package org.drools.beliefs.bayes;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PropertyReference<T> {
    private Object instance;
    private String name;
    private Method getter;

    public PropertyReference(Object instance, String name) {
        this.instance = instance;
        this.name = name;
        try {
            PropertyDescriptor[] propDescrs = Introspector.getBeanInfo(instance.getClass()).getPropertyDescriptors();
            for ( PropertyDescriptor propDescr : propDescrs ) {
                if ( propDescr.getName().equals( name ) ) {
                    getter = propDescr.getReadMethod();
                    break;
                }
            }
        } catch (IntrospectionException e) {
            throw new RuntimeException( "Unable to create getter", e );
        }
    }

    public Object getInstance() {
        return instance;
    }

    public String getName() {
        return name;
    }

    public T get(){
        try {
            return (T) getter.invoke( instance );
        } catch (Exception e) {
            throw new RuntimeException( "Unable read getter", e );
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        PropertyReference that = (PropertyReference) o;

        if (!name.equals(that.name)) { return false; }
        if (!instance.equals(that.instance)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = instance.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "PropertyReference{" +
               "instance=" + instance +
               ", name='" + name + '\'' +
               '}';
    }
}
