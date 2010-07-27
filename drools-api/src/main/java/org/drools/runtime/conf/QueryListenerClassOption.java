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

import org.drools.runtime.rule.QueryViewChangedEventListener;


/**
 * A class to configure the session query listener configuration.
 * 
 * @author etirelli
 */
public class QueryListenerClassOption implements SingleValueKnowledgeSessionOption {

    private static final long serialVersionUID = -8461267995706982981L;
    
    /**
     * The property name for the clock type configuration
     */
    public static final String PROPERTY_NAME = "drools.queryListener";
    
    /**
     * clock type
     */
    private final Class<? extends QueryViewChangedEventListener> queryListener;
    
    /**
     * Private constructor to enforce the use of the factory method
     * @param queryListener
     */
    private QueryListenerClassOption( Class<? extends QueryViewChangedEventListener> queryListener ) {
        this.queryListener = queryListener;
    }
    
    /**
     * This is a factory method for this Query Listener configuration.
     * The factory method is a best practice for the case where the 
     * actual object construction is changed in the future.
     * 
     * @param queryListener is the class that implements the actual query listener
     * 
     * @return the actual type safe query listener configuration.
     */
    public static QueryListenerClassOption get( Class<? extends QueryViewChangedEventListener> queryListener ) {
        return new QueryListenerClassOption( queryListener );
    }
    
    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }
    
    /**
     * Returns the configured query listener class
     * 
     * @return
     */
    public Class<? extends QueryViewChangedEventListener> getQueryListenerClass() {
        return queryListener;
    }
    
    /**
     * Returns a new instance of the query listener class
     * 
     * @return
     */
    public QueryViewChangedEventListener newQueryListenerInstance() {
        try {
            return queryListener.newInstance();
        } catch ( Exception e ) {
            throw new RuntimeException( "Error instantiating configured query view listener class: '"+queryListener.getName()+"'", e );
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (( queryListener == null) ? 0 :  queryListener.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        QueryListenerClassOption other = (QueryListenerClassOption) obj;
        if (  queryListener == null ) {
            if ( other. queryListener != null ) return false;
        } else if ( ! queryListener.equals( other.queryListener ) ) return false;
        return true;
    }
    
    @Override
    public String toString() {
        return "QueryListenerOption( "+ queryListener +" )";
    }
}
