/*
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
 * A class for the belief system configuration.
 */
public class BeliefSystemTypeOption implements SingleValueKnowledgeSessionOption {

    private static final long serialVersionUID = 510l;

    /**
     * The property name for the belief system configuration
     */
    public static final String PROPERTY_NAME = "drools.beliefSystem";
    
    /**
     * Belie System Type
     */
    private final String belieSystemType;
    
    /**
     * Private constructor to enforce the use of the factory method
     * @param belieSystemType
     */
    private BeliefSystemTypeOption( String belieSystemType ) {
        this.belieSystemType = belieSystemType;
    }
    
    /**
     * This is a factory method for this belief system configuration.
     * The factory method is a best practice for the case where the 
     * actual object construction is changed in the future.
     * 
     * @param belieSystem  the identifier for the belie system 
     * 
     * @return the actual type safe default clock type configuration.
     */
    public static BeliefSystemTypeOption get( String belieSystemType ) {
        return new BeliefSystemTypeOption( belieSystemType );
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
    public String getBelieSystemType() {
        return belieSystemType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (( belieSystemType == null) ? 0 :  belieSystemType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        BeliefSystemTypeOption other = (BeliefSystemTypeOption) obj;
        if (  belieSystemType == null ) {
            if ( other.belieSystemType != null ) return false;
        } else if ( ! belieSystemType.equals( other.belieSystemType ) ) return false;
        return true;
    }
    
    @Override
    public String toString() {
        return "BelieSystemTypeOption( "+ belieSystemType +" )";
    }
}
