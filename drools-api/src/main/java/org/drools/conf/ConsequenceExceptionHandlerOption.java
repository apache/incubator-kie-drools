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

import org.drools.runtime.rule.ConsequenceExceptionHandler;

/**
 * A class for the consequence exception handler configuration configuration.
 * 
 * @author etirelli
 */
public class ConsequenceExceptionHandlerOption implements SingleValueKnowledgeBaseOption {

    private static final long serialVersionUID = -8461267995706982981L;

    /**
     * The property name for consequence exception handler configuration
     */
    public static final String PROPERTY_NAME = "drools.consequenceExceptionHandler";
    
    /**
     * the consequence exception handler class instance
     */
    private final Class<? extends ConsequenceExceptionHandler> handler;
    
    /**
     * Private constructor to enforce the use of the factory method
     */
    private ConsequenceExceptionHandlerOption( final Class<? extends ConsequenceExceptionHandler> handler ) {
        this.handler = handler;
    }
    
    /**
     * This is a factory method for this ConsequenceExceptionHandler configuration.
     * The factory method is a best practice for the case where the 
     * actual object construction is changed in the future.
     *
     * @param handler the actual consequence exception handler class to be used 
     * 
     * @return the actual type safe consequence exception handler configuration.
     */
    public static ConsequenceExceptionHandlerOption get( final Class<? extends ConsequenceExceptionHandler> handler ) {
        return new ConsequenceExceptionHandlerOption( handler );
    }
    
    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }
    
    /**
     * Returns the consequence exception handler instance
     * @return
     */
    public Class<? extends ConsequenceExceptionHandler> getHandler() {
        return handler;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((handler == null) ? 0 : handler.getClass().getName().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        ConsequenceExceptionHandlerOption other = (ConsequenceExceptionHandlerOption) obj;
        if ( handler == null ) {
            if ( other.handler != null ) return false;
        } else if( other.handler == null ) {
            return false;
        } else if ( !handler.getClass().getName().equals( other.handler.getClass().getName() ) ) return false;
        return true;
    }

    
}
