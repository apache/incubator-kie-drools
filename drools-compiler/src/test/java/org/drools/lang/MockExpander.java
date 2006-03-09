package org.drools.lang;

public class MockExpander
    implements
    Expander {

    public String expand(String pattern,
                         RuleParser context) {
        
        return "foo : Bar(a==3)";
    }

}
