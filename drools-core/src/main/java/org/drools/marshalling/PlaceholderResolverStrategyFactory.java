package org.drools.marshalling;

import java.util.ArrayList;
import java.util.List;

public class PlaceholderResolverStrategyFactory {
    List<PlaceholderResolverStrategy> strategiesList;
    
    public PlaceholderResolverStrategyFactory() {
        this.strategiesList = new ArrayList<PlaceholderResolverStrategy>();
    }
    
    public void addStrategy(PlaceholderResolverStrategy strategy) {
        strategy.setIndex( this.strategiesList.size() );
        this.strategiesList.add( strategy );
    }    
    
    public PlaceholderResolverStrategy getStrategy(int index) {
        return this.strategiesList.get( index );        
    }
    
    public PlaceholderResolverStrategy getStrategy(Object object) {
        for ( PlaceholderResolverStrategy strategy: strategiesList ) {
            if ( strategy.accept( object ) ) {
                return strategy;
            }
        }
        throw new RuntimeException( "Unable to info PlaceholderResolverStrategy for class : " + object.getClass() + " object : " + object );
    }
}
