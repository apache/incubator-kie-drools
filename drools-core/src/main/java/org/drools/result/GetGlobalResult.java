package org.drools.result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GetGlobalResult extends AbstractResult implements GenericResult {

    private Object object;

    public GetGlobalResult( String identifier, Object object ){
        super( identifier );
        this.object = object;
    }

    public Object getValue(){
	return object;
    }
}
