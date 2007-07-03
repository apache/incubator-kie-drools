package org.drools.facttemplates;

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

import org.drools.base.ValueType;
import org.drools.spi.ObjectType;

/**
 * Java class semantics <code>ObjectType</code>.
 * 
 * @author <a href="mailto:bob@werken.com">bob@werken.com </a>
 * 
 * @version $Id: ClassObjectType.java,v 1.5 2005/02/04 02:13:36 mproctor Exp $
 */
public class FactTemplateObjectType
    implements
    ObjectType {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /**
     * 
     */
    private static final long serialVersionUID = 400L;

    /** FieldTemplate. */
    protected FactTemplate    factTemplate;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Construct.
     * 
     * @param objectTypeClass
     *            Java object class.
     */
    public FactTemplateObjectType(final FactTemplate factTemplate) {
        this.factTemplate = factTemplate;
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Return the Fact Template.
     * 
     * @return The Fact Template
     */
    public FactTemplate getFactTemplate() {
        return this.factTemplate;
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
        if ( object.getClass() == FactImpl.class ) {
            return this.factTemplate.equals( ((Fact) object).getFactTemplate() );
        } else {
            return false;
        }
    }

    public ValueType getValueType() {
        return ValueType.FACTTEMPLATE_TYPE;
    }
    
    public String toString() {
        return "[ClassObjectType class=" + this.factTemplate.getName() + "]";
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

        if ( object == null || object.getClass() != FactTemplateObjectType.class ) {
            return false;
        }

        final FactTemplateObjectType other = (FactTemplateObjectType) object;

        return this.factTemplate.equals( other.factTemplate );
    }

    public int hashCode() {
        return this.factTemplate.hashCode();
    }
}