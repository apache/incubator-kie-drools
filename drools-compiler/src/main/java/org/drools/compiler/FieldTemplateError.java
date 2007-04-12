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

import org.apache.commons.jci.problems.CompilationProblem;
import org.drools.lang.descr.BaseDescr;
import org.drools.rule.Package;

public class FieldTemplateError extends DroolsError {
    private Package   pkg;
    private BaseDescr descr;
    private Object    object;
    private String    message;

    public FieldTemplateError(final Package pkg,
                              final BaseDescr descr,
                              final Object object,
                              final String message) {
        this.pkg = pkg;
        this.descr = descr;
        this.object = object;
        this.message = message;
    }

    public Package getPackage() {
        return this.pkg;
    }

    public BaseDescr getDescr() {
        return this.descr;
    }

    public Object getObject() {
        return this.object;
    }

    /** 
     * This will return the line number of the error, if possible
     * Otherwise it will be -1
     */
    public int getLine() {
        if ( this.descr != null ) {
            return this.descr.getLine();
        } else {
            return -1;
        }
    }

    public String getMessage() {
        String summary = this.message;
        if ( this.object instanceof CompilationProblem[] ) {
            final CompilationProblem[] problem = (CompilationProblem[]) this.object;
            for ( int i = 0; i < problem.length; i++ ) {
                summary = summary + " " + problem[i].getMessage();
            }

        }
        return summary;
    }

}