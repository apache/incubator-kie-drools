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

import java.util.*;

import org.drools.rule.Namespaceable;

public class TypeDeclarationDescr extends BaseDescr implements Namespaceable {

    private static final long   serialVersionUID = 510l;
    private String              namespace;
    private String              typeName;
    private Map<String, Map<String, String>> metaAttributes;
    private Map<String, TypeFieldDescr> fields;
    private String              superTypeName;
    private Vector<String>      interfaceNames;

    public TypeDeclarationDescr() {
        this(null);
    }

    public TypeDeclarationDescr(final String typeName) {
        this.typeName = typeName;
        this.metaAttributes = new HashMap<String, Map<String, String>>();
        this.interfaceNames = new Vector<String>();
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
     * @param typeName the identifier to set
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     * Adds a new attribute
     * @param attr
     * @param values
     */
    public void addMetaAttribute( String attr, Map<String, String> values ) {
        if( this.metaAttributes == null ) {
            this.metaAttributes = new HashMap<String, Map<String, String>>();
        }
        this.metaAttributes.put( attr, values );
    }


    /**
     * Adds a new attribute
     * @param attr
     * @param value
     */
    public void addMetaAttribute( String attr, String value) {
        if( this.metaAttributes == null ) {
            this.metaAttributes = new HashMap<String, Map<String, String>>();
        }
        Hashtable<String, String> attrMap = new Hashtable<String, String>();
            attrMap.put(value,value);
        this.metaAttributes.put( attr, attrMap );
    }

    /**
     * Given the general attribute structure : @attr( key1=value1, key2=value2, ...)
     * Returns the first key, assuming that the annotation has structure @attr(key)
     * @param attr
     * @return key1
     */
    public String getMetaAttribute( String attr ) {
        if (this.metaAttributes == null) return null;
        Map<String, String> meta = this.metaAttributes.get(attr);
        return meta == null ? null : meta.keySet().iterator().next();
    }

     /**
     * Given the general attribute structure : @attr( key1=value1, key2=value2, ...)
     * Returns the set of keys, assuming that the annotation has structure @attr(key1,key2,...)
     * @param attr
     * @return set of attribute keys
     */
    public Set<String> getMetaAttributes(String attr) {
        if (this.metaAttributes == null) return null;
        Map<String, String> meta = this.metaAttributes.get(attr);
        return meta == null ? null : meta.keySet();
    }

      /**
     * Given the general attribute structure : @attr( key1=value1, key2=value2, ...)
     * Returns a mapped value, given the attribute and the key
     * @param attr
     * @param key
     * @return value
     */
     public String getMetaAttributeValue( String attr, String key ) {
        if (this.metaAttributes == null) return null;
        Map<String, String> meta = this.metaAttributes.get(attr);
        return meta == null ? null : meta.get(key);
     }


    /**
     * Returns the attribute map
     * @return
     */
    public Map<String, Map<String, String>> getMetaAttributes() {
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
            this.fields = new LinkedHashMap<String, TypeFieldDescr>();
        }
        this.fields.put( field.getFieldName(), field );
    }

    public String toString() {
        return "TypeDeclaration[ "+this.getTypeName()+" ]";
    }



    public String getSuperTypeName() {
        return superTypeName;
    }

    public void setSuperTypeName(String type) {
        this.superTypeName = type;
    }

    public Collection<String> getInterfaceNames() {
        return interfaceNames;
    }

    public void setInterfaceNames(Collection<String> interfaces) {
        this.interfaceNames.addAll(interfaceNames);
    }



}
