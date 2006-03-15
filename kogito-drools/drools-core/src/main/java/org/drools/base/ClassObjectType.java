package org.drools.base;

/*
 * $Id: ClassObjectType.java,v 1.5 2005/02/04 02:13:36 mproctor Exp $
 *
 * Copyright 2002 (C) The Werken Company. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "drools" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Werken Company.
 * For written permission, please contact bob@werken.com.
 *
 * 4. Products derived from this Software may not be called "drools" nor may
 * "drools" appear in their names without prior written permission of The Werken
 * Company. "drools" is a registered trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company.
 * (http://drools.werken.com/).
 *
 * THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE WERKEN COMPANY OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;

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

    /** Java object class. */
    protected Class objectTypeClass;
    
    protected int valueType;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Construct.
     * 
     * @param objectTypeClass
     *            Java object class.
     */
    public ClassObjectType(Class objectTypeClass) {
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
    public boolean matches(Object object) {
        return getClassType().isInstance( object );
    }
    
    public int getValueType() {
        return this.valueType;
    }    

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // java.lang.Object
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    protected void setValueType(Class clazz) {
        if ( clazz == null ) {
            this.valueType = Evaluator.NULL_TYPE;
        } else if ( clazz == Character.class ) {
            this.valueType = Evaluator.CHAR_TYPE;
        } else if ( clazz == Byte.class ) { 
            this.valueType = Evaluator.BYTE_TYPE;
        } else if (clazz == Short.class ) {
            this.valueType = Evaluator.SHORT_TYPE;
        } else if (clazz == Integer.class ) {
            this.valueType = Evaluator.INTEGER_TYPE;
        } else if (clazz == Long.class) {
            this.valueType = Evaluator.LONG_TYPE;
        } else if (clazz == Float.class) {
            this.valueType = Evaluator.FLOAT_TYPE;
        } else if (clazz == Double.class) {
            this.valueType = Evaluator.DOUBLE_TYPE;
        } else if (clazz == Boolean.class) {
            this.valueType = Evaluator.BOOLEAN_TYPE;            
        } else if (clazz == java.sql.Date.class) {
            this.valueType = Evaluator.DATE_TYPE;
        } else if (clazz == java.util.Date.class) {
            this.valueType = Evaluator.DATE_TYPE;
        } else if (clazz.isAssignableFrom( Object[].class )) {
            this.valueType = Evaluator.ARRAY_TYPE;
        } else if (clazz == String.class) {
            this.valueType = Evaluator.STRING_TYPE;
        }   else if (clazz instanceof Object) {
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
    public boolean equals(Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || !(object instanceof ClassObjectType) ) {
            return false;
        }

        return this.objectTypeClass == ( (ClassObjectType) object).objectTypeClass;
    }
    
    public int hashCode() {
        return this.objectTypeClass.hashCode();
    }


}
