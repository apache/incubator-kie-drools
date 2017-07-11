/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.marshalling.impl;

import java.util.HashSet;
import java.util.Set;

import org.drools.core.util.StringUtils;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.marshalling.ObjectMarshallingStrategyStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectMarshallingStrategyStoreImpl implements ObjectMarshallingStrategyStore {

	private ObjectMarshallingStrategy[] strategiesList;
    
    public ObjectMarshallingStrategyStoreImpl(ObjectMarshallingStrategy[] strategiesList) {
        this.strategiesList = strategiesList;
        Set<String> names = new HashSet<String>();
        //Validate received set
        for( ObjectMarshallingStrategy strategy : strategiesList ){
        	String name = strategy.getName();
        	
        	if( names.contains( name ) ){
        		throw new RuntimeException( "Multiple ObjectMarshallingStrategies with the same name found in environment:" + name );
        	}else{
        		names.add( name );
        	}
        }
        names.clear();
    }
   
    // Old marshalling algorithm methods
    /* (non-Javadoc)
     * @see org.kie.api.marshalling.impl.ObjectMarshallingStrategyStore#getStrategy(int)
     */
    public ObjectMarshallingStrategy getStrategy(int index) {
        return this.strategiesList[ index ];
    }

    /* (non-Javadoc)
     * @see org.kie.api.marshalling.impl.ObjectMarshallingStrategyStore#getStrategy(java.lang.Object)
     */
    public int getStrategy(Object object) {
        for ( int i = 0, length = this.strategiesList.length; i < length; i++ ) {
            if ( strategiesList[i].accept( object ) ) {
                return i;
            }
        }
        throw new RuntimeException( "Unable to find PlaceholderResolverStrategy for class : " + object.getClass() + " object : " + object );
    }
    
    // New marshalling algorithm methods
    /* (non-Javadoc)
     * @see org.kie.api.marshalling.impl.ObjectMarshallingStrategyStore#getStrategyObject(java.lang.String)
     */
    public ObjectMarshallingStrategy getStrategyObject(String strategyName) {
        if( StringUtils.isEmpty(strategyName) ) { 
            return null;
        }
        if (strategyName.startsWith("org.drools.marshalling.impl")) {
            strategyName = strategyName.replaceFirst("org.drools.marshalling.impl", "org.drools.core.marshalling.impl");
        }
        
        for( int i = 0; i < this.strategiesList.length; ++i ) { 
        	 if( strategiesList[i].getName().equals(strategyName) ) {
            		return strategiesList[i];
            }
        }
        throw new RuntimeException( "Unable to find PlaceholderResolverStrategy for name : " + strategyName );
    }
    
    /* (non-Javadoc)
     * @see org.kie.api.marshalling.impl.ObjectMarshallingStrategyStore#getStrategyObject(java.lang.Object)
     */
    public ObjectMarshallingStrategy getStrategyObject(Object object) {
        for ( int i = 0, length = this.strategiesList.length; i < length; i++ ) {
            if ( strategiesList[i].accept( object ) ) {
                return strategiesList[i];
            }
        }
        throw new RuntimeException( "Unable to find PlaceholderResolverStrategy for class : " + object.getClass() + " object : " + object );
    }
    
}
