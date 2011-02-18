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

package org.drools.builder.conf;

import org.drools.runtime.rule.AccumulateFunction;

/**
 * A class for the accumulate function configuration.
 * 
 */
public class AccumulateFunctionOption implements MultiValueKnowledgeBuilderOption {

    private static final long serialVersionUID = 510l;

    /**
     * The prefix for the property name for accumulate functions
     */
    public static final String PROPERTY_NAME = "drools.accumulate.function.";
    
    /**
     * accumulate function name
     */
    private final String name;
    
    /**
     * the accumulate function instance
     */
    private final AccumulateFunction function;
    
    /**
     * Private constructor to enforce the use of the factory method
     * @param name
     */
    private AccumulateFunctionOption( final String name, final AccumulateFunction function ) {
        this.name = name;
        this.function = function;
    }
    
    /**
     * This is a factory method for this AccumulateFunction configuration.
     * The factory method is a best practice for the case where the 
     * actual object construction is changed in the future.
     * 
     * @param name the name of the function to be configured
     * 
     * @return the actual type safe accumulate function configuration.
     */
    public static AccumulateFunctionOption get( final String name, final AccumulateFunction function ) {
        return new AccumulateFunctionOption( name, function );
    }
    
    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME+name;
    }
    
    /**
     * Returns the name of the function configured
     * 
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the accumulate function instance
     * @return
     */
    public AccumulateFunction getFunction() {
        return function;
    }
    
    @Override
    public String toString() {
        return "AccumulateFunction( name="+name+" function="+function+" )";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((function == null) ? 0 : function.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        AccumulateFunctionOption other = (AccumulateFunctionOption) obj;
        if ( function == null ) {
            if ( other.function != null ) return false;
        } else if ( other.function == null ) {
            return false;
        } else if ( !function.getClass().equals( other.function.getClass() ) ) {
            return false;
        }
        if ( name == null ) {
            if ( other.name != null ) return false;
        } else if ( !name.equals( other.name ) ) {
            return false;
        }
        return true;
    }
}
