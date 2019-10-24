package org.drools.core;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.ObjectFilter;

@XmlRootElement(name="class-object-filter")
@XmlAccessorType(XmlAccessType.FIELD)
public class ClassObjectSerializationFilter implements ObjectFilter {

    @XmlAttribute(name="string",required=true)
    private String className = null;

    private transient Class filteredClass;

    private transient boolean skipLoadClass;

    public ClassObjectSerializationFilter() {
        // JAXB constructor
    }

    public ClassObjectSerializationFilter(Class clazz) {
        // use canonical name to avoid problems with anonymous classes
        this.className = clazz.getCanonicalName();
        this.filteredClass = clazz;
    }

    public ClassObjectSerializationFilter(ClassObjectFilter objectFilter) {
        this(objectFilter.getFilteredClass());
    }

    public Class getFilteredClass() {
        return filteredClass;
    }

    public void setFilteredClass(Class filteredClass) {
        this.filteredClass = filteredClass;
    }

    /**
     * @param object The object to be filtered
     * @return whether or not the Iterator accepts the given object according to its class.
     */
    @Override
    public boolean accept(Object object) {
        if ( !skipLoadClass && filteredClass == null ) {
            try {
                filteredClass = Class.forName(this.className);
            } catch( ClassNotFoundException e ) {
                skipLoadClass = true;
            }
        }
        return skipLoadClass ?
                className.equals( object.getClass().getCanonicalName() ) :
                filteredClass.isAssignableFrom( object.getClass() );
    }
}
