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
 *
 * Created on Jan 23, 2008
 */

package org.drools.rule;

import org.drools.facttemplates.FactTemplate;

import java.io.Serializable;

/**
 * The type declaration class stores all type's metadata
 * declared in source files.
 *  
 * @author etirelli
 */
public class TypeDeclaration implements Serializable {

    public static enum Role {
        FACT,
        EVENT;

        public static Role parseRole( String role ) {
            if( "event".equalsIgnoreCase( role ) ) {
                return EVENT;
            } else if( "fact".equalsIgnoreCase( role ) ) {
                return FACT;
            }
            return null;
        }
    }

    public static enum Format {
        POJO,
        TEMPLATE;

        public static Format parseFormat( String format ) {
            if( "pojo".equalsIgnoreCase( format ) ) {
                return POJO;
            } else if( "template".equalsIgnoreCase( format ) ) {
                return TEMPLATE;
            }
            return null;
        }
    }

    public static enum ClockStrategy {
        NONE,
        PSEUDO,
        SYSTEM,
        HEARTBEAT,
        ATTRIBUTE;

        public static ClockStrategy parseClockStrategy( String clockStrategy ) {
            if( "none".equalsIgnoreCase( clockStrategy ) ) {
                return NONE;
            } else if( "pseudo".equalsIgnoreCase( clockStrategy ) ) {
                return PSEUDO;
            } else if( "system".equalsIgnoreCase( clockStrategy ) ) {
                return SYSTEM;
            } else if( "heartbeat".equalsIgnoreCase( clockStrategy ) ) {
                return HEARTBEAT;
            } else if( "attribute".equalsIgnoreCase( clockStrategy ) ) {
                return ATTRIBUTE;
            }
            return null;
        }
    }

    private final String typeName;
    private Role role;
    private Format format;
    private ClockStrategy clockStrategy;
    private String timestampAttribute;
    private String durationAttribute;
    private Class<?> typeClass;
    private FactTemplate typeTemplate;

    public TypeDeclaration( String typeName ) {
        this.typeName = typeName;
        this.role = Role.FACT;
        this.format = Format.POJO;
        this.clockStrategy = ClockStrategy.NONE;
        this.durationAttribute = null;
        this.timestampAttribute = null;
        this.typeClass = null;
        this.typeTemplate = null;
    }

    /**
     * @return the type
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * @return the category
     */
    public Role getRole() {
        return role;
    }

    /**
     * @param role the category to set
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * @return the format
     */
    public Format getFormat() {
        return format;
    }

    /**
     * @param format the format to set
     */
    public void setFormat(Format format) {
        this.format = format;
    }

    /**
     * @return the clockStrategy
     */
    public ClockStrategy getClockStrategy() {
        return clockStrategy;
    }

    /**
     * @param clockStrategy the clockStrategy to set
     */
    public void setClockStrategy(ClockStrategy clockStrategy) {
        this.clockStrategy = clockStrategy;
    }

    /**
     * @return the timestampAttribute
     */
    public String getTimestampAttribute() {
        return timestampAttribute;
    }

    /**
     * @param timestampAttribute the timestampAttribute to set
     */
    public void setTimestampAttribute(String timestampAttribute) {
        this.timestampAttribute = timestampAttribute;
    }

    /**
     * @return the durationAttribute
     */
    public String getDurationAttribute() {
        return durationAttribute;
    }

    /**
     * @param durationAttribute the durationAttribute to set
     */
    public void setDurationAttribute(String durationAttribute) {
        this.durationAttribute = durationAttribute;
    }

    /**
     * @return the typeClass
     */
    public Class< ? > getTypeClass() {
        return typeClass;
    }

    /**
     * @param typeClass the typeClass to set
     */
    public void setTypeClass(Class< ? > typeClass) {
        this.typeClass = typeClass;
    }

    /**
     * @return the typeTemplate
     */
    public FactTemplate getTypeTemplate() {
        return typeTemplate;
    }

    /**
     * @param typeTemplate the typeTemplate to set
     */
    public void setTypeTemplate(FactTemplate typeTemplate) {
        this.typeTemplate = typeTemplate;
    }

    /**
     * Returns true if the given parameter matches this type declaration
     * 
     * @param clazz
     * @return
     */
    public boolean matches( Object clazz ) {
        boolean matches = false;
        if( clazz instanceof FactTemplate ) {
            matches = this.typeTemplate.equals( clazz );
        } else {
            matches = this.typeClass.isAssignableFrom( (Class<?>) clazz );
        }
        return matches;
    }


}
