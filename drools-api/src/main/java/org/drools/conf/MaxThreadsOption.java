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

package org.drools.conf;


/**
 * A class for the max threads configuration.
 * 
 */
public class MaxThreadsOption implements SingleValueKnowledgeBaseOption {

    private static final long serialVersionUID = 510l;

    /**
     * The property name for the max threads
     */
    public static final String PROPERTY_NAME = "drools.maxThreads";
    
    /**
     * max threads
     */
    private final int maxThreads;
    
    /**
     * Private constructor to enforce the use of the factory method
     * @param maxThreads
     */
    private MaxThreadsOption( int maxThreads ) {
        this.maxThreads = maxThreads;
    }
    
    /**
     * This is a factory method for this Max Threads configuration.
     * The factory method is a best practice for the case where the 
     * actual object construction is changed in the future.
     * 
     * @param maxThreads the maximum number of threads for partition evaluation
     * 
     * @return the actual type safe max threads configuration.
     */
    public static MaxThreadsOption get( int threshold ) {
        return new MaxThreadsOption( threshold );
    }
    
    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }
    
    /**
     * Returns the maximum number of threads for partition evaluation
     * 
     * @return
     */
    public int getMaxThreads() {
        return maxThreads;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + maxThreads;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        MaxThreadsOption other = (MaxThreadsOption) obj;
        if ( maxThreads != other.maxThreads ) return false;
        return true;
    }
    
}
