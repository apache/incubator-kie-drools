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

package org.drools.runtime.conf;


/**
 * A class for the session clock configuration.
 * 
 * @author etirelli
 */
public class ClockTypeOption implements SingleValueKnowledgeSessionOption {

    private static final long serialVersionUID = 510l;

    /**
     * The property name for the clock type configuration
     */
    public static final String PROPERTY_NAME = "drools.clockType";
    
    /**
     * clock type
     */
    private final String clockType;
    
    /**
     * Private constructor to enforce the use of the factory method
     * @param clockType
     */
    private ClockTypeOption( String clockType ) {
        this.clockType = clockType;
    }
    
    /**
     * This is a factory method for this Clock Type configuration.
     * The factory method is a best practice for the case where the 
     * actual object construction is changed in the future.
     * 
     * @param clockType the identifier for the clock type
     * 
     * @return the actual type safe default clock type configuration.
     */
    public static ClockTypeOption get( String clockType ) {
        return new ClockTypeOption( clockType );
    }
    
    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }
    
    /**
     * Returns the configured clock type
     * 
     * @return
     */
    public String getClockType() {
        return clockType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (( clockType == null) ? 0 :  clockType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        ClockTypeOption other = (ClockTypeOption) obj;
        if (  clockType == null ) {
            if ( other. clockType != null ) return false;
        } else if ( ! clockType.equals( other.clockType ) ) return false;
        return true;
    }
    
    @Override
    public String toString() {
        return "ClockTypeOption( "+ clockType +" )";
    }
}
