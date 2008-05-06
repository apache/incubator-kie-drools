package org.drools.persister;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaceholderResolverStrategyFactory {
    List<PlaceholderResolverStrategy> strategiesList;
    Map<Object, PlaceholderResolverStrategy> strategiesMap = new HashMap<Object, PlaceholderResolverStrategy>();
    
    public PlaceholderResolverStrategyFactory() {
        this.strategiesList = new ArrayList<PlaceholderResolverStrategy>();
    }
    
    public void addPlaceholderResolverStrategy(PlaceholderResolverStrategy strategy) {
        this.strategiesList.add( strategy );
    }
    
    public PlaceholderResolverStrategy get(int i) {
        return this.strategiesList.get( i );
    }
    
    public PlaceholderResolverStrategy get(Object object) {
        return this.strategiesList.get( 0 );
        //return this.strategiesMap.get( object );
    }
}
