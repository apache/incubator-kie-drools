package org.drools.lang.descr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RuleDescr extends PatternDescr  {
    private String ruleName;
    private String documentation;
    
    private PatternDescr lhs;
    private PatternDescr rhs;
    private List attributes = Collections.EMPTY_LIST;
    
    public RuleDescr(String ruleName, String documentation) {
        this.ruleName = ruleName;
        this.documentation = documentation;
    }
    
    
    public void addAttribute(AttributeDescr attribute) {
        if ( this.attributes == Collections.EMPTY_LIST) {
            this.attributes = new ArrayList();
        }
    }


    public PatternDescr getLhs() {
        return lhs;
    }


    public void setLhs(PatternDescr lhs) {
        this.lhs = lhs;
    }


    public PatternDescr getRhs() {
        return rhs;
    }


    public void setRhs(PatternDescr rhs) {
        this.rhs = rhs;
    }


    public List getAttributes() {
        return attributes;
    }


    public String getDocumentation() {
        return documentation;
    }


    public String getRuleName() {
        return ruleName;
    }
    
    
 
}
