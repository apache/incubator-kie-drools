package org.drools.impl;

import java.util.Collection;
import java.util.Map;

import org.drools.runtime.FactParams;
import org.drools.runtime.GlobalParams;
import org.drools.runtime.Parameters;

public class ParametersImpl implements Parameters {
    private FactParamsImpl factParams = new FactParamsImpl();
    private GlobalParamsImpl globalParams = new GlobalParamsImpl();
        
    public FactParams getFactParams() {
        return this.factParams;
    }

    public GlobalParams getGlobalParams() {
        return this.globalParams;
    }
    
    public boolean isEmpty() {
        if ( !isEmpty(factParams.getIn() ) || !isEmpty( factParams.getOut() ) || !isEmpty( factParams.getInOut() ) ) {
            return false;
        }
        
        if ( !isEmpty(globalParams.getIn() ) || !isEmpty( globalParams.getOut() ) || !isEmpty( globalParams.getInOut() ) ) {
            return false;
        }        
        
        return true;
    }
    
    public boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }
    
    public boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }    

}
