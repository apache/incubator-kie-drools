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

import org.drools.core.marshalling.NamedObjectMarshallingStrategy;
import org.drools.core.util.StringUtils;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.marshalling.ObjectMarshallingStrategyStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectMarshallingStrategyStoreImpl implements ObjectMarshallingStrategyStore {
	private static final Logger logger = LoggerFactory.getLogger(ObjectMarshallingStrategyStoreImpl.class);

	private ObjectMarshallingStrategy[] strategiesList;
    
    public ObjectMarshallingStrategyStoreImpl(ObjectMarshallingStrategy[] strategiesList) {
        this.strategiesList = strategiesList;
        Set<String> names = new HashSet<String>();
        for( ObjectMarshallingStrategy strategy : strategiesList ){
        	String name;
        	if( strategy instanceof NamedObjectMarshallingStrategy ){
        		name = ((NamedObjectMarshallingStrategy)strategy).getName();
        	}else{
        		name = strategy.getClass().getName();
        	}
        	if( names.contains( name ) ){
        		logger.warn( "Multiple ObjectMarshallingStrategies with the same name:" + name + " strange behaviour could occurr");
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
    public ObjectMarshallingStrategy getStrategyObject(String strategyClassName) {
        if( StringUtils.isEmpty(strategyClassName) ) { 
            return null;
        }
        if (strategyClassName.startsWith("org.drools.marshalling.impl")) {
            strategyClassName = strategyClassName.replaceFirst("org.drools.marshalling.impl", "org.drools.core.marshalling.impl");
        }
        ObjectMarshallingStrategy objectMarshallingStrategy = null; 
        for( int i = 0; i < this.strategiesList.length; ++i ) { 
        	if( strategiesList[i] instanceof NamedObjectMarshallingStrategy 
          		   && ((NamedObjectMarshallingStrategy)strategiesList[i]).getName().equals(strategyClassName )){
        		if( objectMarshallingStrategy == null ){
        			objectMarshallingStrategy = strategiesList[i];
        		}else{
        			logger.warn( "Multiple ObjectMarshallingStrategies with the same name:" + strategyClassName + " strange behaviour could occurr");
        		    
        		}
             }else if( strategiesList[i].getClass().getName().equals(strategyClassName) ) {
            	if( objectMarshallingStrategy == null ){
            		objectMarshallingStrategy = strategiesList[i];
            	}else{
         			logger.warn( "Multiple ObjectMarshallingStrategies with the same name:" + strategyClassName + " strange behaviour could occurr");
        		    
         		}
            }
        }
        return objectMarshallingStrategy;
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
