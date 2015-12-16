/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.compiler;

import org.drools.core.base.EvaluatorWrapper;
import org.drools.core.rule.Declaration;

import java.util.Collections;
import java.util.Map;

public class BoundIdentifiers {
    private Map<String, Declaration>      declarations;
    private Map<String, Class< ? >>       declrClasses;
    private Map<String, Class< ? >>       globals;
    private Map<String, EvaluatorWrapper> operators;
    private Class< ? >                    thisClass;

    public BoundIdentifiers(Map<String, Class< ? >> declarations,
                            Map<String, Class< ? >> globals) {
        this( declarations,
              globals,
              null,
              null );
    }

    public BoundIdentifiers(Map<String, Class< ? >> declarations,
                            Map<String, Class< ? >> globals,
                            Map<String, EvaluatorWrapper> operators) {
        this( declarations,
              globals,
              operators,
              null );
    }

    public BoundIdentifiers(Map<String, Class< ? >> declarations,
                            Map<String, Class< ? >> globals,
                            Map<String, EvaluatorWrapper> operators,
                            Class< ? > thisClass) {
        this.declrClasses = declarations;
        this.globals = globals;
        this.operators = operators;
        this.thisClass = thisClass;
    }

    public Map<String, Class< ? >> getDeclrClasses() {
        return declrClasses;
    }

    public Map<String, Class< ? >> getGlobals() {
        return globals;
    }

    @SuppressWarnings("unchecked")
    public Map<String, EvaluatorWrapper> getOperators() {
        return operators != null ? operators : Collections.EMPTY_MAP;
    }

    public Class< ? > getThisClass() {
        return thisClass;
    }

    public String toString() {
        return ( "thisClass: " + thisClass + "\n" ) + "declarations:" + declrClasses + "\n" + "globals:" + globals + "\n" + "operators:" + operators + "\n";
    }

    public void setDeclarations(Map<String, Declaration> declarations) {
       this.declarations = declarations;
    }
    
    public Map<String, Declaration> getDeclarations() {
        return this.declarations;
    }
}
