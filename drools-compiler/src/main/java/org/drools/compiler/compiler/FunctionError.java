/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.compiler.compiler;

import org.drools.drl.ast.descr.FunctionDescr;
import org.drools.drl.parser.DroolsError;
import org.kie.internal.jci.CompilationProblem;

public class FunctionError extends DroolsError {
    final private FunctionDescr functionDescr;
    final private Object        object;
    private int[]               errorLines;

    public FunctionError(final FunctionDescr functionDescr,
                         final Object object,
                         final String message) {
        super(functionDescr.getResource(), "");
        this.functionDescr = functionDescr;
        this.object = object;
        setErrorLines();
        setMessage(createMessage(message));
    }

    @Override
    public String getNamespace() {
        return functionDescr.getNamespace();
    }

    public FunctionDescr getFunctionDescr() {
        return this.functionDescr;
    }

    public Object getObject() {
        return this.object;
    }
    
    public int[] getLines() {
        return errorLines;
    }

    public String toString() {
        return getMessage();
    }

    private void setErrorLines() {
        if( object instanceof CompilationProblem[] cp) {
            this.errorLines = new int[cp.length];
            for( int i = 0; i < cp.length ; i ++ ) {
                this.errorLines[i] = cp[i].getStartLine() - this.functionDescr.getOffset() + this.getFunctionDescr().getLine() - 1;
            }
        } else {
            this.errorLines = new int[1];
            this.errorLines[0] = functionDescr.getLine();
        }
    }
    
    private String createMessage( String message ) {
        StringBuilder detail = new StringBuilder();
        if( object instanceof CompilationProblem[] cp) {
            for( int i = 0; i < cp.length ; i ++ ) {
               detail.append( this.functionDescr.getName() );
               detail.append( " (line:" );
               detail.append( this.errorLines[i] );
               detail.append( "): " );
               detail.append( cp[i].getMessage() );
               detail.append( "\n" );
            }
        } else if( object instanceof Exception ex) {
            detail.append( " (line:" );
            detail.append( this.errorLines[0] );
            detail.append( "): " );
            detail.append( message );
            detail.append( " " );
            detail.append( ex.getClass().getName() );
            if( ex.getMessage() != null ) {
                detail.append( ": " );
                detail.append( ex.getMessage() );
            }
        }
        return "[ function "+ functionDescr.getName() + detail +" ]";
    }

}
