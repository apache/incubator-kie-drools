package org.drools.result;

import java.util.ArrayList;
import java.util.List;

import org.drools.runtime.rule.FactHandle;

public class InsertElementsResult extends AbstractResult implements GenericResult {

    private List<FactHandle> handles;
    private List<Object>     objects;

    public InsertElementsResult( String identifier ){
        super( identifier );
    }

    public List<FactHandle> getHandles(){
        return handles;
    }

    public void setHandles( List<FactHandle> handles ){
	this.handles = handles;
    }

    public List<Object> getObjects(){
        return objects;
    }

    public void setObjects( List<Object> objects ){
	this.objects = objects;
    }

    public Object getValue(){
        return this.objects;
    }

    public Object getFactHandle(){
        return this.handles;
    }
}
