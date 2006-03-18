package org.drools.lang.descr;

import java.util.Collections;
import java.util.List;

public class FunctionDescr {
    private final String name;    
    
    private String parameterTypes[] = new String[0];
    private String parameterNames[] = new String[0];
    private String returnType = null;
    
    private final String text;  

    public FunctionDescr(String name, String[] parameterTypes, String[] parameterNames, String returnType, String text) {
        this.name = name;
        if (parameterTypes != null) {
            this.parameterTypes = parameterTypes;
        }
        if (parameterNames != null) {
            this.parameterNames = parameterNames;
        }
        this.text = text;
        this.returnType = returnType;
    }
    
    public String getName() {
        return name;
    }

    public String[] getParameterNames() {
        return parameterNames;
    }

    public String[] getParameterTypes() {
        return parameterTypes;
    }

    public String getReturnType() {
        return returnType;
    }

    public String getText() {
        return this.text;
    } 
}
