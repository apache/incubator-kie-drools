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

package org.drools.conf;


/**
 * A class for the alpha node hashing threshold configuration.
 * 
 * @author etirelli
 */
public class AlphaThresholdOption implements SingleValueKnowledgeBaseOption {

    private static final long serialVersionUID = -8461267995706982981L;

    /**
     * The property name for the default DIALECT
     */
    public static final String PROPERTY_NAME = "drools.alphaNodeHashingThreshold";
    
    /**
     * alpha threshold
     */
    private final int threshold;
    
    /**
     * Private constructor to enforce the use of the factory method
     * @param threshold
     */
    private AlphaThresholdOption( int threshold ) {
        this.threshold = threshold;
    }
    
    /**
     * This is a factory method for this Alpha Threshold configuration.
     * The factory method is a best practice for the case where the 
     * actual object construction is changed in the future.
     * 
     * @param threshold the threshold value for the alpha hashing option
     * 
     * @return the actual type safe alpha threshold configuration.
     */
    public static AlphaThresholdOption get( int threshold ) {
        return new AlphaThresholdOption( threshold );
    }
    
    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }
    
    /**
     * Returns the threshold value for alpha hashing
     * 
     * @return
     */
    public int getThreshold() {
        return threshold;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + threshold;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        AlphaThresholdOption other = (AlphaThresholdOption) obj;
        if ( threshold != other.threshold ) return false;
        return true;
    }
    
}
