package org.drools.lang;

public class MockExpander implements Expander {
	
	private int timesCalled = 0;

    public String expand(String scope,
    						String pattern,
    						RuleParser context) {
        
    		int grist = (++timesCalled);
        return "foo" + grist + " : Bar(a==" + grist + ")";
    }

}
