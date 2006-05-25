package org.drools.base;
/*
 * Copyright 2005 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



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
    private String                   fieldName;
    private Class                    clazz;
    private transient FieldExtractor extractor;

    public ClassFieldExtractor(Class clazz,
                               String fieldName) {
        this.clazz = clazz;
        this.fieldName = fieldName;
        init();
    }

    private void readObject(ObjectInputStream is) throws ClassNotFoundException,
                                                 IOException,
                                                 Exception {
        //always perform the default de-serialization first
        is.defaultReadObject();
        init();
    }

    public void init() {
        try {
            this.extractor = ClassFieldExtractorFactory.getClassFieldExtractor( clazz,
                                                                                fieldName );
        } catch ( Exception e ) {
            throw new RuntimeDroolsException( e );
        }
    }

    public int getIndex() {
        return this.extractor.getIndex();
    }
    
    public String getFieldName() {
        return this.fieldName;
    }

    public Object getValue(Object object) {
        return this.extractor.getValue( object );
    }

    public ObjectType getObjectType() {
        return this.extractor.getObjectType();
    }

    public String toString() {
        return "[ClassFieldExtractor class=" + this.clazz + " field=" + this.fieldName  + "]";
    }
    
    public int hashCode() {
        return this.getObjectType().hashCode() * 17 + this.getIndex();
    }
    
    public boolean equals(Object object) {
        if ( this == object ) {
            return true;
        }
        
        if ( object == null || object.getClass() != ClassFieldExtractor.class ) {
            return false;
        }
        
        ClassFieldExtractor other = (ClassFieldExtractor) object;
        
        return this.extractor.getObjectType().equals( other.getObjectType() ) && this.extractor.getIndex() == other.getIndex();
    }
}