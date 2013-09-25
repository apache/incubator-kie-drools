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

package org.drools.core.marshalling.impl;

import java.util.HashMap;
import java.util.Map;

import org.drools.core.util.StringUtils;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.marshalling.ObjectMarshallingStrategyStore;

public class ObjectMarshallingStrategyStoreImpl implements ObjectMarshallingStrategyStore {
    private ObjectMarshallingStrategy[] strategiesList;
    
    private Map<String, String> backwardCompatible = new HashMap<String, String>();
    
    public ObjectMarshallingStrategyStoreImpl(ObjectMarshallingStrategy[] strategiesList) {
        this.strategiesList = strategiesList;
        // store backward compatibility values due to package change
        this.backwardCompatible.put("org.drools.marshalling.impl.SerializablePlaceholderResolverStrategy", 
                                    "org.drools.core.marshalling.impl.SerializablePlaceholderResolverStrategy");
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
        if (backwardCompatible.containsKey(strategyClassName)) {
            strategyClassName = backwardCompatible.get(strategyClassName);
        }
        ObjectMarshallingStrategy objectMarshallingStrategy = null; 
        for( int i = 0; i < this.strategiesList.length; ++i ) { 
           if( strategiesList[i].getClass().getName().equals(strategyClassName) ) {
               return strategiesList[i];
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
