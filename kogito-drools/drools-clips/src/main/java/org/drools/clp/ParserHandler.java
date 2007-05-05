package org.drools.clp;

import org.drools.lang.descr.RuleDescr;

public interface ParserHandler {       
    
    //public void functionHandler(RuleDescr ruleDescr);
    
    public void ruleDescrHandler(RuleDescr ruleDescr);
    
    public void lispFormHandler(ExecutionEngine engine);
}
