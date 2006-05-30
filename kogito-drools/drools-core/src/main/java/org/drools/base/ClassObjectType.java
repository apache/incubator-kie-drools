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

import org.drools.spi.Evaluator;
import org.drools.spi.ObjectType;

/**
 * Java class semantics <code>ObjectType</code>.
 * 
 * @author <a href="mailto:bob@werken.com">bob@werken.com </a>
 * 
 * @version $Id: ClassObjectType.java,v 1.5 2005/02/04 02:13:36 mproctor Exp $
 */
public class ClassObjectType
    implements
    ObjectType {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /**
     * 
     */
    private static final long serialVersionUID = -2527407693999309827L;

    /** Java object class. */
    protected Class           objectTypeClass;

    protected int             valueType;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Construct.
     * 
     * @param objectTypeClass
     *            Java object class.
     */
    public ClassObjectType(final Class objectTypeClass) {
        this.objectTypeClass = objectTypeClass;
        setValueType( objectTypeClass );
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Return the Java object class.
     * 
     * @return The Java object class.
     */
    public Class getClassType() {
        return this.objectTypeClass;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // org.drools.spi.ObjectType
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * Determine if the passed <code>Object</code> belongs to the object type
     * defined by this <code>objectType</code> instance.
     * 
     * @param object
     *            The <code>Object</code> to test.
     * 
     * @return <code>true</code> if the <code>Object</code> matches this
     *         object type, else <code>false</code>.
     */
    public boolean matches(final Object object) {
        return getClassType().isInstance( object );
    }

    public int getValueType() {
        return this.valueType;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // java.lang.Object
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    protected void setValueType(final Class clazz) {
        if ( clazz == null ) {
            this.valueType = Evaluator.NULL_TYPE;
        } else if ( clazz == Character.class ) {
            this.valueType = Evaluator.CHAR_TYPE;
        } else if ( clazz == Byte.class ) {
            this.valueType = Evaluator.BYTE_TYPE;
        } else if ( clazz == Short.class ) {
            this.valueType = Evaluator.SHORT_TYPE;
        } else if ( clazz == Integer.class ) {
            this.valueType = Evaluator.INTEGER_TYPE;
        } else if ( clazz == Long.class ) {
            this.valueType = Evaluator.LONG_TYPE;
        } else if ( clazz == Float.class ) {
            this.valueType = Evaluator.FLOAT_TYPE;
        } else if ( clazz == Double.class ) {
            this.valueType = Evaluator.DOUBLE_TYPE;
        } else if ( clazz == Boolean.class ) {
            this.valueType = Evaluator.BOOLEAN_TYPE;
        } else if ( clazz == java.sql.Date.class ) {
            this.valueType = Evaluator.DATE_TYPE;
        } else if ( clazz == java.util.Date.class ) {
            this.valueType = Evaluator.DATE_TYPE;
        } else if ( clazz.isAssignableFrom( Object[].class ) ) {
            this.valueType = Evaluator.ARRAY_TYPE;
        } else if ( clazz == String.class ) {
            this.valueType = Evaluator.STRING_TYPE;
        } else if ( clazz instanceof Object ) {
            this.valueType = Evaluator.OBJECT_TYPE;
        }
    }

    public String toString() {
        return "[ClassObjectType class=" + getClassType().getName() + "]";
    }

    /**
     * Determine if another object is equal to this.
     * 
     * @param object
     *            The object to test.
     * 
     * @return <code>true</code> if <code>object</code> is equal to this,
     *         otherwise <code>false</code>.
     */
    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || object.getClass() != ClassObjectType.class ) {
            return false;
        }

        return this.objectTypeClass == ((ClassObjectType) object).objectTypeClass;
    }

    public int hashCode() {
        return this.objectTypeClass.hashCode();
    }

}