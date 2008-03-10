package org.drools.lang.descr;

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

import java.util.HashMap;
import java.util.Map;

public class TypeDeclarationDescr extends BaseDescr {
    
    /**
     * The attribute key used to define what role the type assumes (fact, event)
     */
    public static final String ATTR_ROLE = "role";
    /**
     * The attribute key used to define what is the clock strategy used for that type
     */
    public static final String ATTR_CLOCK_STRATEGY = "clock_strategy";
    /**
     * The attribute key used to define what is the attribute to read the timestamp from
     */
    public static final String ATTR_TIMESTAMP = "timestamp_attribute";
    /**
     * The attribute key used to define what is the attribute to read the duration from
     */
    public static final String ATTR_DURATION = "duration_attribute";
    /**
     * The attribute key used to define what is the class name that implements the type
     */
    public static final String ATTR_CLASS = "class";
    /**
     * The attribute key used to define what is the template name that implements the type
     */
    public static final String ATTR_TEMPLATE = "template";
    

    private static final long   serialVersionUID = 400L;
    private String              typeName;
    private Map<String, String> attributes;

    public TypeDeclarationDescr() {
        this(null);
    }
    
    public TypeDeclarationDescr(final String typeName) {
        this.typeName = typeName;
        this.attributes = new HashMap<String, String>();
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
    public void addAttribute( String attr, String value ) {
        this.attributes.put( attr, value );
    }
    
    /**
     * Returns an attribute value or null if it is not defined
     * @param attr
     * @return
     */
    public String getAttribute( String attr ) {
        return this.attributes.get( attr );
    }

    /**
     * Returns the attribute map
     * @return
     */
    public Map<String, String> getAttributes() {
        return this.attributes;
    }

    public String toString() {
        return "TypeDeclaration[ "+this.getTypeName()+" ]";
    }
}