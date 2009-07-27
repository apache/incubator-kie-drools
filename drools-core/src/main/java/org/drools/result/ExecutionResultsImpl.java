package org.drools.result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExecutionResultsImpl implements ExecutionResults {

    private List<GenericResult> results;

    public ExecutionResultsImpl(){
    }

    public List<GenericResult> getResults(){
	if( results == null ){
	    results = new ArrayList<GenericResult>();
	}
        return results;
    }

    public Object getValue( String identifier ){
        for( GenericResult genRes: getResults() ){
	    if( identifier.equals( genRes.getIdentifier() ) ){
                return genRes.getValue();
	    }
	}
        return null;
    }

    public Object getFactHandle( String identifier ){
        for( GenericResult genRes: getResults() ){
	    if( identifier.equals( genRes.getIdentifier() ) ){
                return genRes.getFactHandle();
	    }
	}
        return null;
    }

    public List<String> getIdentifiers(){
	List<String> idList = new ArrayList<String>();
        for( GenericResult genRes: getResults() ){
	    String id = genRes.getIdentifier();
            if( id != null ){
                idList.add( id );
	    }
	}
        return idList;
    }
    
    public Map<String,Object> getResultsAsMap(){
    	Map<String,Object> resmap = new HashMap<String,Object>();
    	for( GenericResult genres: results ){
    		String key = genres.getIdentifier();
    		Object val = genres.getValue();
    		if( key != null && val != null ){
    			resmap.put( key, val );
    		}
    	}
    	return resmap;
    }

    public Map<String,Object> getFactHandlesAsMap(){
    	Map<String,Object> hanmap = new HashMap<String,Object>();
    	for( GenericResult genres: results ){
    		String key = genres.getIdentifier();
    		Object val = genres.getFactHandle();
    		if( key != null && val != null ){
    			hanmap.put( key, val );
    		}
    	}
    	return hanmap;
    }


}
