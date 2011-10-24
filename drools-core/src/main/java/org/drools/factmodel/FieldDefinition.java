/*
 * Copyright 2008 JBoss Inc
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

package org.drools.factmodel;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

import org.drools.base.ClassFieldAccessor;
import org.drools.core.util.StringUtils;
import org.drools.definition.type.FactField;
import org.mvel2.MVEL;

/**
 * Declares a field to be dynamically generated.
 */
public class FieldDefinition
        implements
        FactField,
        Comparable<FieldDefinition> {

    private String             name       = null;
    private String             type       = null;
    private boolean            key        = false;
    private boolean            inherited  = false;
    private int                index      = -1;
    private String             initExpr   = null;

    private List<AnnotationDefinition> annotations;

    private ClassFieldAccessor accessor   = null;

    public FieldDefinition() {
    }

    /**
     * Default constructor
     *
     * @param name the field's name
     * @param type the fully qualified fields type
     */
    public FieldDefinition(String name,
                           String type) {
        this( name,
                type,
                false );
    }

    /**
     * Default constructor
     *
     * @param name the field's name
     * @param type the fully qualified fields type
     */
    public FieldDefinition(String name,
                           String type,
                           boolean key) {
        this.name = name;
        this.type = type;
        this.key = key;
    }

    public void setReadWriteAccessor(ClassFieldAccessor accessor) {
        this.accessor = accessor;
    }

    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        this.name = (String) in.readObject();
        this.type = (String) in.readObject();
        this.key = in.readBoolean();
        this.accessor = (ClassFieldAccessor) in.readObject();
        this.annotations = (List<AnnotationDefinition>) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( this.name );
        out.writeObject( this.type );
        out.writeBoolean( this.key );
        out.writeObject( this.accessor );
        out.writeObject( this.annotations );
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the fully qualified type.
     */
    public String getTypeName() {
        return this.type;
    }

    /**
     * @param type The fully qualified type to set.
     */
    public void setTypeName(String type) {
        this.type = type;
    }

    public Class< ? > getType() {
        return this.accessor.getFieldType();
    }

    /**
     * @return Returns the key.
     */
    public boolean isKey() {
        return key;
    }

    /**
     * @param key The key to set.
     */
    public void setKey(boolean key) {
        this.key = key;
    }

    /**
     * Creates the String name for the get method for a field with the given name and type
     * @return
     */
    public String getReadMethod() {
        String prefix = null;
        if ( "boolean".equals( this.type ) ) {
            prefix = "is";
        } else {
            prefix = "get";
        }
        return prefix + this.name.substring( 0,
                1 ).toUpperCase() + this.name.substring( 1 );
    }

    /**
     * Creates the String name for the set method for a field with the given name and type
     *
     * @return
     */
    public String getWriteMethod() {
        return "set" + this.name.substring( 0,
                1 ).toUpperCase() + this.name.substring( 1 );
    }

    /**
     * @inheritDoc
     */
    public boolean equals(Object o) {
        return this.getName().equals( ((FieldDefinition) o).getName() );
    }

    /**
     * @return Returns the field extractor
     */
    public ClassFieldAccessor getFieldAccessor() {
        return this.accessor;
    }

    /**
     * @inheritDoc
     */
    public int hashCode() {
        return this.getName().hashCode();
    }

    /**
     * Sets the value of this attribute in the target
     * bean instance
     *
     * @param bean the target bean instance where the attribute shall be set
     * @param value the value to set the attribute to
     */
    public void setValue(Object bean,
                         Object value) {
        this.accessor.setValue( bean,
                value );
    }

    /**
     * Returns the value of this attribute in the target bean instance
     *
     * @param bean the target bean instance
     *
     * @return target bean instance attribute value
     */
    public Object getValue(Object bean) {
        return this.accessor.getValue( bean );
    }

    public Object get(Object bean) {
        return this.accessor.getValue( bean );
    }

    public void set(Object bean,
                    Object value) {
        this.accessor.setValue( bean,
                value );
    }


    public boolean isInherited() {
        return inherited;
    }

    public void setInherited(boolean inherited) {
        this.inherited = inherited;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getInitExpr() {
        return initExpr;
    }

    public void setInitExpr(String initExpr) {
        this.initExpr = initExpr;
    }


    public int compareTo(FieldDefinition other) {
        return (this.index - other.index);
    }



    public void addAnnotation(AnnotationDefinition annotationDefinition) {
        if (this.annotations == null) {
            this.annotations = new ArrayList<AnnotationDefinition>();
        }
        this.annotations.add(annotationDefinition);
    }

    public List<AnnotationDefinition> getAnnotations() {
        return annotations;
    }



    public String getDefaultValueAsString() {
        return (String) MVEL.eval( initExpr );
    }


    public Boolean getDefaultValueAsBoolean( ) {
        if ( StringUtils.isEmpty( initExpr ) ) {
            return false;
        } else {
            if ( "true".equalsIgnoreCase( initExpr ) ) {
                return true;
            } else if ( "false".equalsIgnoreCase( initExpr ) ) {
                return false;
            } else {
                return (Boolean) MVEL.eval( initExpr );
            }
        }
    }
    public Byte getDefaultValueAsByte( ) {
        try {
            return initExpr == null ? 0 : Byte.parseByte(initExpr);
        } catch (NumberFormatException nfe) {
            return StringUtils.isEmpty( initExpr ) ? 0 : (Byte) MVEL.eval( initExpr );
        }
    }
    public Character getDefaultValueAsChar() {
        if ( StringUtils.isEmpty( initExpr ) ) {
            return '\u0000';
        } else {
            if ( initExpr.length() == 1 ) {
                return initExpr.charAt(0);
            } else {
                return (Character) MVEL.eval( initExpr );
            }
        }
    }
    public Double getDefaultValueAsDouble( ) {
        try {
            return initExpr == null ? 0.0 : Double.parseDouble(initExpr);
        } catch (NumberFormatException nfe) {
            return StringUtils.isEmpty( initExpr ) ? 0.0 : (Double) MVEL.eval( initExpr );
        }
    }
    public Float getDefaultValueAsFloat( ) {
        try {
            return initExpr == null ? 0.0f : Float.parseFloat(initExpr);
        } catch (NumberFormatException nfe) {
            return StringUtils.isEmpty( initExpr ) ? 0.0f : (Float) MVEL.eval( initExpr );
        }
    }
    public Integer getDefaultValueAsInt( ) {
        try {
            return initExpr == null ? 0 : Integer.parseInt(initExpr);
        } catch (NumberFormatException nfe) {
            return StringUtils.isEmpty( initExpr ) ? 0 : (Integer) MVEL.eval( initExpr );
        }
    }
    public Long getDefaultValueAsLong( ) {
        try {
            return initExpr == null ? 0L : Long.parseLong(initExpr);
        } catch (NumberFormatException nfe) {
            return StringUtils.isEmpty( initExpr ) ? 0 : (Long) MVEL.eval( initExpr );
        }
    }
    public Short getDefaultValueAsShort( ) {
        try {
            return initExpr == null ? 0 : Short.parseShort(initExpr);
        } catch (NumberFormatException nfe) {
            return StringUtils.isEmpty( initExpr ) ? 0 : (Short) MVEL.eval( initExpr );
        }
    }


    public boolean getDefaultValueAs_boolean() {
        if ( StringUtils.isEmpty( initExpr ) ) {
            return false;
        } else {
            if ( "true".equalsIgnoreCase( initExpr ) ) {
                return true;
            } else if ( "false".equalsIgnoreCase( initExpr ) ) {
                return false;
            } else {
                return (Boolean) MVEL.eval( initExpr );
            }
        }
    }
    public byte getDefaultValueAs_byte() {
        try {
            return initExpr == null ? 0 : Byte.parseByte(initExpr);
        } catch (NumberFormatException nfe) {
             return StringUtils.isEmpty( initExpr ) ? 0 : (Byte) MVEL.eval( initExpr );
        }
    }
    public char getDefaultValueAs_char() {
        if ( StringUtils.isEmpty( initExpr ) ) {
            return '\u0000';
        } else {
            if ( initExpr.length() == 1 ) {
                return initExpr.charAt(0);
            } else {
                return (Character) MVEL.eval( initExpr );
            }
        }
    }
    public double getDefaultValueAs_double() {
        try {
            return initExpr == null ? 0.0 : Double.parseDouble(initExpr);
        } catch (NumberFormatException nfe) {
            return StringUtils.isEmpty( initExpr ) ? 0.0 : (Double) MVEL.eval( initExpr );
        }
    }
    public float getDefaultValueAs_float() {
        try {
            return initExpr == null ? 0.0f : Float.parseFloat(initExpr);
        } catch (NumberFormatException nfe) {
            return StringUtils.isEmpty( initExpr ) ? 0.0f : (Float) MVEL.eval( initExpr );
        }
    }
    public int getDefaultValueAs_int() {
        try {
            return initExpr == null ? 0 : Integer.parseInt(initExpr);
        } catch (NumberFormatException nfe) {
            return StringUtils.isEmpty( initExpr ) ? 0 : (Integer) MVEL.eval( initExpr );
        }
    }
    public long getDefaultValueAs_long() {
        try {
            return initExpr == null ? 0L : Long.parseLong(initExpr);
        } catch (NumberFormatException nfe) {
            return StringUtils.isEmpty( initExpr ) ? 0L : (Long) MVEL.eval( initExpr );
        }
    }
    public short getDefaultValueAs_short() {
        try {
            return initExpr == null ? 0 : Short.parseShort(initExpr);
        } catch (NumberFormatException nfe) {
            return StringUtils.isEmpty( initExpr ) ? 0 : (Short) MVEL.eval( initExpr );
        }
    }


    public String toString() {
        return "FieldDefinition{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", key=" + key +
                ", inherited=" + inherited +
                ", index=" + index +
                ", initExpr='" + initExpr + '\'' +
                ", annotations=" + annotations +
                ", accessor=" + accessor +
                '}';
    }
}
