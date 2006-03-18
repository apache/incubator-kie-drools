package org.drools.lang.descr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FunctionDescr {
    private final String name;    
    private final String returnType;
    
    private List parameterTypes = Collections.EMPTY_LIST;
    private List parameterNames = Collections.EMPTY_LIST;
    
    private String text;  

    public FunctionDescr(String name, String returnType) {
        this.name = name;
        this.returnType = returnType;
    }
    
    public String getName() {
        return name;
    }

    public List getParameterNames() {
        return parameterNames;
    }

    public List getParameterTypes() {
        return parameterTypes;
    }
    
    public void addParameter(String type, String name) {
        if (this.parameterTypes == Collections.EMPTY_LIST) {
            this.parameterTypes = new ArrayList();
        }
        this.parameterTypes.add( type );
        
        if (this.parameterNames == Collections.EMPTY_LIST) {
            this.parameterNames = new ArrayList();
        }
        this.parameterNames.add( name );
    }
    

    public String getReturnType() {
        return returnType;
    }
    
    public void setText(String text) {
    		this.text = text;
    }

    public String getText() {
        return this.text;
    } 
}
