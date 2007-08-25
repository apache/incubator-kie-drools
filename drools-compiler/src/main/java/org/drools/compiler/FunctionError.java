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

import org.drools.commons.jci.problems.CompilationProblem;
import org.drools.lang.descr.FunctionDescr;

public class FunctionError extends DroolsError {
    private FunctionDescr functionDescr;
    private Object        object;
    private String        message;

    public FunctionError(final FunctionDescr functionDescr,
                         final Object object,
                         final String message) {
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
    
    public String toString() {
        StringBuffer detail = new StringBuffer();
        if( object instanceof CompilationProblem[] ) {
            CompilationProblem[] cp = (CompilationProblem[]) object;
            for( int i = 0; i < cp.length ; i ++ ) {
               detail.append( cp[i].toString() );
               detail.append( "\n" );
            }
        }
        return "[ "+functionDescr.getName()+" : "+this.getMessage() + "\n"+detail.toString()+" ]";
    }

}