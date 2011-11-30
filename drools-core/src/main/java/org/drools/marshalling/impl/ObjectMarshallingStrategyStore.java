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

package org.drools.marshalling.impl;

import java.util.HashSet;

import org.drools.core.util.StringUtils;
import org.drools.marshalling.ObjectMarshallingStrategy;

public class ObjectMarshallingStrategyStore {
    private ObjectMarshallingStrategy[] strategiesList;
    
    public ObjectMarshallingStrategyStore(ObjectMarshallingStrategy[] strategiesList) {
        this.strategiesList = strategiesList;
        HashSet<Class<?>> strategySet = new HashSet<Class<?>>();
        for( int i = 0; i < strategiesList.length; ++i ) { 
            if( ! strategySet.add(strategiesList[i].getClass()) ) { 
               throw new RuntimeException("Two strategy instances of type " + strategiesList[i].getClass().getSimpleName() 
                       + " are being used. Please only use one instance of each type of strategy." );
            }
        }
    }
   
    // Old marshalling algorithm methods
    public ObjectMarshallingStrategy getStrategy(int index) {
        return this.strategiesList[ index ];
    }

    public int getStrategy(Object object) {
        for ( int i = 0, length = this.strategiesList.length; i < length; i++ ) {
            if ( strategiesList[i].accept( object ) ) {
                return i;
            }
        }
        throw new RuntimeException( "Unable to find PlaceholderResolverStrategy for class : " + object.getClass() + " object : " + object );
    }
    
    // New marshalling algorithm methods
    public ObjectMarshallingStrategy getStrategyObject(String strategyClassName) {
        if( StringUtils.isEmpty(strategyClassName) ) { 
            return null;
        }
        ObjectMarshallingStrategy objectMarshallingStrategy = null; 
        for( int i = 0; i < this.strategiesList.length; ++i ) { 
           if( strategiesList[i].getClass().getName().equals(strategyClassName) ) {
               return strategiesList[i];
           }
        }
        return objectMarshallingStrategy;
    }
    
    public ObjectMarshallingStrategy getStrategyObject(Object object) {
        for ( int i = 0, length = this.strategiesList.length; i < length; i++ ) {
            if ( strategiesList[i].accept( object ) ) {
                return strategiesList[i];
            }
        }
        throw new RuntimeException( "Unable to find PlaceholderResolverStrategy for class : " + object.getClass() + " object : " + object );
    }
    
}
