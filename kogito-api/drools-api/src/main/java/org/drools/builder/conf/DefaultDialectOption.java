/**
 * Copyright 2010 JBoss Inc
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

package org.drools.builder.conf;


/**
 * A class for the default dialect configuration.
 * 
 * @author etirelli
 */
public class DefaultDialectOption implements SingleValueKnowledgeBuilderOption {

    private static final long serialVersionUID = -8461267995706982981L;

    /**
     * The property name for the default DIALECT
     */
    public static final String PROPERTY_NAME = "drools.dialect.default";
    
    /**
     * dialect name
     */
    private final String name;
    
    /**
     * Private constructor to enforce the use of the factory method
     * @param name
     */
    private DefaultDialectOption( String name ) {
        this.name = name;
    }
    
    /**
     * This is a factory method for this DefaultDialect configuration.
     * The factory method is a best practice for the case where the 
     * actual object construction is changed in the future.
     * 
     * @param name the name of the dialect to be configured as default
     * 
     * @return the actual type safe default dialect configuration.
     */
    public static DefaultDialectOption get( String name ) {
        return new DefaultDialectOption( name );
    }
    
    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }
    
    /**
     * Returns the name of the dialect configured as default
     * 
     * @return
     */
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        DefaultDialectOption other = (DefaultDialectOption) obj;
        if ( name == null ) {
            if ( other.name != null ) return false;
        } else if ( !name.equals( other.name ) ) return false;
        return true;
    }
    
}
