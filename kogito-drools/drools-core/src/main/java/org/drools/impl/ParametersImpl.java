package org.drools.impl;

import org.drools.runtime.FactParams;
import org.drools.runtime.GlobalParams;
import org.drools.runtime.Parameters;

public class ParametersImpl implements Parameters {
    private FactParams factParams = new FactParamsImpl();
    private GlobalParams globalParams = new GlobalParamsImpl();
        
    public FactParams getFactParams() {
        return this.factParams;
    }

    public GlobalParams getGlobalParams() {
        return this.globalParams;
    }

}
