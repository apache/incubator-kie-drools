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



public class TypeFieldDescr extends BaseDescr implements Comparable<TypeFieldDescr> {

    private static final long   serialVersionUID = 510l;
    private String              fieldName;
    private String              initExpr;
    private PatternDescr        pattern;
    private Map<String, Map<String, String>> metaAttributes;
    private int                 index = -1;

    public TypeFieldDescr() {
        this( null );
    }

    public TypeFieldDescr(final String fieldName) {
        this.fieldName = fieldName;
        this.metaAttributes = new HashMap<String, Map<String, String>>();
    }

    public TypeFieldDescr(final String fieldName, final PatternDescr pat) {
    	this(fieldName);
    	this.pattern = pat;
    }

    /**
     * @return the identifier
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * @param fieldName the identifier to set
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Adds a new attribute
     * @param attr
     * @param value
     */
    public void addMetaAttribute(String attr,
                                 Map<String, String> value) {
        this.metaAttributes.put( attr,
                                 value );
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
     * Given the general attribute structure : @attr( key1=value1, key2=value2, ...)
     * Returns the map {key->value}
     * @param attr
     * @return key/value map
     */
     public Map<String, String> getMetaAttributeValues( String attr ) {
        if (this.metaAttributes == null) return null;
        return this.metaAttributes.get(attr);
     }

    /**
     * Returns the attribute map
     * @return
     */
    public Map<String, Map<String, String>> getMetaAttributes() {
        return this.metaAttributes != null ? this.metaAttributes : Collections.EMPTY_MAP;
    }
    /**
    * @return the initExpr
    */
    public String getInitExpr() {
        return initExpr;
    }

    /**
     * @param initExpr the initExpr to set
     */
    public void setInitExpr(String initExpr) {
        this.initExpr = initExpr;
    }

    /**
     * @return the pattern
     */
    public PatternDescr getPattern() {
        return pattern;
    }

    /**
     * @param pattern the pattern to set
     */
    public void setPattern(PatternDescr pattern) {
        this.pattern = pattern;
    }

    public String toString() {
        return "TypeField[ " + this.getFieldName() + " = (" + this.initExpr + ") : " + this.pattern + " ]";
    }


    public int compareTo(TypeFieldDescr other) {
        return (this.index - other.index);
    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }



}
