package org.drools.marshalling.impl;

import java.util.ArrayList;
import java.util.List;

import org.drools.marshalling.ObjectMarshallingStrategy;

public class ObjectMarshallingStrategyStore {
    private ObjectMarshallingStrategy[] strategiesList;
    
    ObjectMarshallingStrategyStore(ObjectMarshallingStrategy[] strategiesList) {
        this.strategiesList = strategiesList;
    } 
    
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

}
