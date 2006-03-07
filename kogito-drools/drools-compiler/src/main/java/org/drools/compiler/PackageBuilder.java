package org.drools.compiler;

import java.util.ArrayList;
import java.util.List;

import org.drools.lang.descr.PackageDescr;

public class PackageBuilder {
    
    private List results = new ArrayList();
    
	Package build(PackageDescr descr) {
        
    }
    
    public BuilderResult[] getResults() {
        return ( BuilderResult[] ) this.results.toArray( new BuilderResult[ this.results.size() ] );
    }    
}
