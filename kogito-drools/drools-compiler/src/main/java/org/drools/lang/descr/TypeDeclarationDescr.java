/*
 * Copyright 2008 Red Hat
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

package org.drools.lang.descr;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.drools.rule.Namespaceable;

public class TypeDeclarationDescr extends BaseDescr implements Namespaceable {

    private static final long   serialVersionUID = 510l;
    private String              namespace;
    private String              typeName;
    private Map<String, String> metaAttributes;
    private Map<String, TypeFieldDescr> fields;

    public TypeDeclarationDescr() {
        this(null);
    }

    public TypeDeclarationDescr(final String typeName) {
        this.typeName = typeName;
        this.metaAttributes = new HashMap<String, String>();
    }
    
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
    
    public String getNamespace() {
        return this.namespace;
    }  

    /**
     * @return the identifier
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * @param identifier the identifier to set
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     * Adds a new attribute
     * @param attr
     * @param value
     */
    public void addMetaAttribute( String attr, String value ) {
        if( this.metaAttributes == null ) {
            this.metaAttributes = new HashMap<String, String>();
        }
        this.metaAttributes.put( attr, value );
    }

    /**
     * Returns an attribute value or null if it is not defined
     * @param attr
     * @return
     */
    public String getMetaAttribute( String attr ) {
        return this.metaAttributes != null ? this.metaAttributes.get( attr ) : null;
    }

    /**
     * Returns the attribute map
     * @return
     */
    public Map<String, String> getMetaAttributes() {
        return this.metaAttributes != null ? this.metaAttributes : Collections.EMPTY_MAP;
    }

     /**
     * @return the fields
     */
    public Map<String, TypeFieldDescr> getFields() {
        return this.fields != null ? this.fields : Collections.EMPTY_MAP;
    }

    /**
     * @param fields the fields to set
     */
    public void setFields(Map<String, TypeFieldDescr> fields) {
        this.fields = fields;
    }

    public void addField( TypeFieldDescr field ) {
        if( this.fields == null ) {
            this.fields = new HashMap<String, TypeFieldDescr>();
        }
        this.fields.put( field.getFieldName(), field );
    }

    public String toString() {
        return "TypeDeclaration[ "+this.getTypeName()+" ]";
    }


}
