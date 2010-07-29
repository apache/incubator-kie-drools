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

import java.util.HashMap;
import java.util.Map;

public class TypeFieldDescr extends BaseDescr {

    private static final long   serialVersionUID = 510l;
    private String              fieldName;
    private String              initExpr;
    private PatternDescr        pattern;
    private Map<String, String> metaAttributes;

    public TypeFieldDescr() {
        this( null );
    }

    public TypeFieldDescr(final String fieldName) {
        this.fieldName = fieldName;
        this.metaAttributes = new HashMap<String, String>();
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
     * @param identifier the identifier to set
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
                                 String value) {
        this.metaAttributes.put( attr,
                                 value );
    }

    /**
     * Returns an attribute value or null if it is not defined
     * @param attr
     * @return
     */
    public String getMetaAttribute(String attr) {
        return this.metaAttributes.get( attr );
    }

    /**
     * Returns the attribute map
     * @return
     */
    public Map<String, String> getMetaAttributes() {
        return this.metaAttributes;
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

}
