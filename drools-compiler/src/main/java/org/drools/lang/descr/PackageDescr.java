package org.drools.lang.descr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackageDescr extends PatternDescr  {
    private final String name;
    private final String documentation;
    
    private List imports    = Collections.EMPTY_LIST;
    private List attributes = Collections.EMPTY_LIST;
    private Map globals = Collections.EMPTY_MAP;    
    private List functions  = Collections.EMPTY_LIST;
    private List rules      = Collections.EMPTY_LIST;

    public PackageDescr(String name) {
        this(name, "");
    } 
    
    public PackageDescr(String name, String documentation) {
        this.name = name;
        this.documentation = documentation;
    }      

    public String getName() {
        return this.name;
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
    
    public void addGlobal(DeclarationDescr global) {
        if ( this.globals == Collections.EMPTY_MAP) {
            this.globals = new HashMap();
        }
        this.globals.put( global.getIdentifier(), global );
    }
    
    public Map getGlobals() {
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
            this.functions = new ArrayList(1);
        }
        this.functions.add( function );
    }        
    
    public List getFunctions() {
        return this.functions;
    }    

    public void addRule(RuleDescr rule) {
        if ( this.rules == Collections.EMPTY_LIST) {
            this.rules = new ArrayList(1);
        }
        this.rules.add( rule );
    }               
    
    public List getRules() {
        return this.rules;
    }    
}
