package org.drools.lang.descr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PackageDescr extends PatternDescr  {
    private String packageName;
    private String documentation;
    
    private List imports    = Collections.EMPTY_LIST;
    private List attributes = Collections.EMPTY_LIST;
    private List globals    = Collections.EMPTY_LIST;    
    private List functions  = Collections.EMPTY_LIST;
    private List rules      = Collections.EMPTY_LIST;
            
    public PackageDescr(String packageName, String documentation) {
        this.packageName = packageName;
        this.documentation = documentation;
    }      

    public String getPackageName() {
        return this.packageName;
    }
    
    public String getDocumentation() {
        return documentation;
    }

    public void addImport(String importEntry) {
        if ( this.imports == Collections.EMPTY_LIST) {
            this.imports = new ArrayList();
        }
        this.imports.add( importEntry );
    }
    
    public List getImports() {
        return this.imports;
    }
    
    public void addGlobal(String global) {
        if ( this.globals == Collections.EMPTY_LIST) {
            this.globals = new ArrayList();
        }
        this.globals.add( global );
    }
    
    public List getGlobals() {
        return this.globals;
    }    
    
    public void addAttribute(AttributeDescr attribute) {
        if ( this.attributes == Collections.EMPTY_LIST) {
            this.attributes = new ArrayList();
        }
        this.attributes.add( attribute );
    }
    
    public List getAttributes() {
        return this.attributes;
    }    
    
    public void addFunction(FunctionDescr function) {
        if ( this.functions == Collections.EMPTY_LIST) {
            this.functions = new ArrayList();
        }
        this.functions.add( function );
    }        
    
    public List getFunctions() {
        return this.functions;
    }    

    public void addRule(RuleDescr rule) {
        if ( this.rules == Collections.EMPTY_LIST) {
            this.rules = new ArrayList();
        }
        this.rules.add( rule );
    }               
    
    public List getRules() {
        return this.rules;
    }    
}
