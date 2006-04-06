package org.drools.base;

import java.io.IOException;
import java.io.ObjectInputStream;

import org.drools.RuntimeDroolsException;
import org.drools.spi.FieldExtractor;
import org.drools.spi.ObjectType;

/**
 * This provides access to fields, and what their numerical index/object type is.
 * This is basically a wrapper class around dynamically generated subclasses of 
 * BaseClassFieldExtractor,
 *  which allows serialization by regenerating the accessor classes 
 * when needed.
 * 
 * @author Michael Neale
 */
public class ClassFieldExtractor
    implements
    FieldExtractor {
    private String                  fieldName;
    private Class                    clazz;
    private transient FieldExtractor extractor;

    public ClassFieldExtractor(Class clazz,
                               String fieldName) {
        this.clazz = clazz;
        this.fieldName = fieldName;
        init();
    }

    private void readObject(ObjectInputStream is) throws ClassNotFoundException,
                                                           IOException, Exception {
        //always perform the default de-serialization first
        is.defaultReadObject();
        init();       
    }
    
    public void init() {
        try {
            this.extractor = ClassFieldExtractorFactory.getClassFieldExtractor( clazz, fieldName );
        } catch ( Exception e ) {
            throw new RuntimeDroolsException( e );
        }        
    }

    public int getIndex() {
        return this.extractor.getIndex();
    }

    public Object getValue(Object object) {
        return this.extractor.getValue( object );
    }

    public ObjectType getObjectType() {
        return this.extractor.getObjectType();
    }

    public boolean equals(Object other) {
        if ( this == other ) {
            return true;
        }
        if ( !(other instanceof ClassFieldExtractor) ) {
            return false;
        }
        ClassFieldExtractor extr = (ClassFieldExtractor) other;
        return this.extractor.getObjectType().equals( extr.getObjectType() ) && 
            this.extractor.getIndex() == extr.getIndex();
    }

    public int hashCode() {
        return this.getObjectType().hashCode() * 17 + this.getIndex();
    }
}