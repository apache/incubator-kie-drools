package org.drools.lang;

import java.util.HashSet;
import java.util.Set;

public class MockExpander implements Expander {
	
	private int timesCalled = 0;
    public Set patterns = new HashSet();
    
    public String expand(String scope,
    						String pattern) {
        
        patterns.add( scope + "," + pattern );
        
    	int grist = (++timesCalled);
        return "foo" + grist + " : Bar(a==" + grist + ")";
    }
    
    public boolean checkPattern(String pat) {
        return patterns.contains( pat );
    }

}
