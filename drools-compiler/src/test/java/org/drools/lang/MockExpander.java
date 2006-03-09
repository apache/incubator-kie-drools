package org.drools.lang;

public class MockExpander
    implements
    Expander {

    public String expand(String pattern,
                         RuleParser context) {
        
    		System.err.println( "expanding: " + pattern );
        return "foo : Bar(a==3)";
    }

}
