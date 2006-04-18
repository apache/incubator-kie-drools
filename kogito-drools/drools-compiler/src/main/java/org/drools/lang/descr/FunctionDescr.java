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