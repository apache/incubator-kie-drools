package org.drools.lang.descr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuleDescr extends PatternDescr  {
    private String name;
    private String documentation;
    
    private AndDescr lhs;
    private String   consequence;
    private List attributes = Collections.EMPTY_LIST;
    private Map declarations = new HashMap(1);
    
    private String className;

    public RuleDescr(String name) {
        this( name, "");
    }
    
    public RuleDescr(String ruleName, String documentation) {
        this.name = ruleName;
        this.documentation = documentation;
    }
    
    public String getName() {
        return name;
    }       
      
    public String getClassName() {
        return this.className;
    }
    
    public void SetClassName(String className) {
        this.className = className;
    }    
    
    public String getDocumentation() {
        return documentation;
    }    
    
    public List getAttributes() {
        return attributes;
    }    
    
    public void addAttribute(AttributeDescr attribute) {
        if ( this.attributes == Collections.EMPTY_LIST) {
            this.attributes = new ArrayList();
        }
    }      

    public AndDescr getLhs() {
        return lhs;
    }


    public void setLhs(AndDescr lhs) {
        this.lhs = lhs;
    }


    public String getConsequence() {
        return this.consequence;
    }


    public void setConsequence(String consequence) {
        this.consequence = consequence;
    }
    
    public Map getDeclarations() {
        return this.declarations;
    }       
}
