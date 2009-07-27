package org.drools.result;

import java.util.ArrayList;
import java.util.List;

import org.drools.runtime.rule.FactHandle;

public class InsertObjectResult extends AbstractResult implements GenericResult {

    private FactHandle handle;
    private Object     object;

    public InsertObjectResult( String identifier, FactHandle handle ){
        super( identifier );
        this.handle = handle;
    }

    public void setObject( Object object ){
	this.object = object;
    }

    public Object getValue(){
        return this.object;
    }

    public Object getFactHandle(){
        return this.handle;
    }
}
