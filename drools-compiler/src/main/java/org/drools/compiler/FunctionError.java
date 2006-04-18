package org.drools.compiler;
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



import org.drools.lang.descr.FunctionDescr;

public class FunctionError extends DroolsError {
    private FunctionDescr functionDescr;
    private Object       object;
    private String       message;
    
    public FunctionError(FunctionDescr functionDescr,
                     Object object,
                     String message) {
        super();
        this.functionDescr = functionDescr;
        this.object = object;
        this.message = message;
    }    

    public FunctionDescr getFunctionDescr() {
        return this.functionDescr;
    }
       
    public Object getObject() {
        return this.object;
    }

    public String getMessage() {
        return this.message;
    }
            
}