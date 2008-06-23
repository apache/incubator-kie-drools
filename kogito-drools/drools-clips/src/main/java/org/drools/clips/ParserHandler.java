package org.drools.clips;

import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.ImportDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.lang.descr.TypeDeclarationDescr;

public interface ParserHandler {  
    
    public void importHandler(ImportDescr descr);
    
    public void functionHandler(FunctionDescr ruleDescr);
    
    public void templateHandler(TypeDeclarationDescr typeDescr);
    
    public void ruleHandler(RuleDescr ruleDescr);
    
    public void lispFormHandler(LispForm lispForm);  
}
