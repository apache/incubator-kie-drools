package org.drools.base;

/*
 * Copyright 2006 Alexander Bagerman
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

import org.drools.RuntimeDroolsException;
import org.drools.spi.FieldExtractor;
import org.drools.spi.ObjectType;
import org.drools.util.asm.ClassFieldInspector;

/**
 * This is the supertype for the ASM generated classes for accessing a field.
 * @author Alexander Bagerman
 */
abstract public class BaseClassFieldExtractor
    implements
    FieldExtractor {
    private final ClassObjectType objectType;

    private final int             index;

    private final Class           fieldType;

    public BaseClassFieldExtractor(Class clazz,
                                   String fieldName) {
        try {
            ClassFieldInspector inspector = new ClassFieldInspector( clazz );
            this.index = ((Integer) inspector.getFieldNames().get( fieldName )).intValue();
            this.fieldType = (Class) inspector.getFieldTypes().get( fieldName );
            this.objectType = ClassFieldExtractorFactory.getClassObjectType( this.fieldType );
        } catch ( Exception e ) {
            throw new RuntimeDroolsException( e );
        }
    }

    public int getIndex() {
        return this.index;
    }

    protected Class getFieldType() {
        return this.fieldType;
    }

    /** This will be implemented by the dynamic classes */
    abstract public Object getValue(Object object);

    public ObjectType getObjectType() {
        return this.objectType;
    }

    public boolean equals(Object other) {
        if ( this == other ) {
            return true;
        }
        if ( !(other instanceof BaseClassFieldExtractor) ) {
            return false;
        }
        BaseClassFieldExtractor extr = (BaseClassFieldExtractor) other;
        return this.objectType.equals( extr.objectType ) && this.index == extr.index;
    }

    public int hashCode() {
        return this.objectType.hashCode() * 17 + this.index;
    }
}