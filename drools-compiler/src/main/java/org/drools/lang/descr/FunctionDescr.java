package org.drools.lang.descr;

import java.util.Collections;
import java.util.List;

public class FunctionDescr {
    private final String name;    
    private List paramters;;    
    private final String text;  

    public FunctionDescr(String name, String text) {
        this( name, Collections.EMPTY_LIST, text);
    }    
    
    public FunctionDescr(String name, List parameters, String text) {
        this.name = name;
        this.paramters = parameters;
        this.text = text;
    }
    
    public String getName() {
        return name;
    }

    public List getParamters() {
        return paramters;
    }

    public String getText() {
        return this.text;
    } 
}
