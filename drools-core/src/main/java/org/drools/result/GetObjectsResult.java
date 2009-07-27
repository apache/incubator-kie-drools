package org.drools.result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GetObjectsResult extends AbstractResult implements GenericResult {

    private List<Object> objects;

    public GetObjectsResult( String identifier, Object object ){
        super( identifier );
        this.objects = new ArrayList<Object>();
        this.objects.add( object );
    }

    public GetObjectsResult( String identifier, List<Object> objects ){
        super( identifier );
        this.objects = objects;
    }

    public List<Object> getObjects(){
        return objects;
    }

    public Object getValue(){
	return objects;
    }

}
