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

package org.drools.lang.descr;

public class AttributeDescr extends BaseDescr {
    public static enum Type {
        STRING, NUMBER, DATE, BOOLEAN, LIST, EXPRESSION
    }

    private static final long serialVersionUID = 510l;
    private String            name;
    private String            value;
    private Type              type;

    public AttributeDescr(final String name) {
        this(name,
             null, 
             Type.EXPRESSION );
    }

    public AttributeDescr(final String name,
                          final String value) {
        this( name,
              value,
              Type.EXPRESSION );
    }

    public AttributeDescr(final String name,
                          final String value,
                          final Type type ) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue( final String value ) {
        this.value = value;
    }

    public void setType( Type type ) {
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }
    
    public String getValueString() {
        if( type == Type.STRING || type == Type.DATE ) {
            // needs escaping
            return "\""+this.value+"\"";
        }
        return this.value;
    }
}
