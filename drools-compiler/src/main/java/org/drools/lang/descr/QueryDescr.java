package org.drools.lang.descr;


public class QueryDescr extends RuleDescr  {
    public QueryDescr(String name) {
        this( name, "");
    }
    
    public QueryDescr(String ruleName, String documentation) {
        super( ruleName, documentation );
    }
}
