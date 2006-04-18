package org.drools.lang.descr;
/*
 * Copyright 2005 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



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
    
    public void addGlobal(String identifier, String type) {
        if ( this.globals == Collections.EMPTY_MAP) {
            this.globals = new HashMap();
        }
        this.globals.put( identifier, type );
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