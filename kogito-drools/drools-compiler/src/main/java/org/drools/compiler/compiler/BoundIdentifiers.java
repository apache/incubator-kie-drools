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

import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.core.base.EvaluatorWrapper;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.XpathBackReference;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoundIdentifiers {
    private Map<String, Class< ? >>       declrClasses;
    private Map<String, Class< ? >>       globals;
    private Map<String, EvaluatorWrapper> operators;
    private Class< ? >                    thisClass;
    private RuleBuildContext context;

    public BoundIdentifiers(Class< ? > thisClass) {
        this( Collections.EMPTY_MAP, null, Collections.EMPTY_MAP, thisClass );
    }

    public BoundIdentifiers(Map<String, Class< ? >> declarations,
                            RuleBuildContext context) {
        this( declarations, context, Collections.EMPTY_MAP, null );
    }

    public BoundIdentifiers(Map<String, Class< ? >> declarations,
                            RuleBuildContext context,
                            Map<String, EvaluatorWrapper> operators) {
        this( declarations, context, operators, null );
    }

    public BoundIdentifiers(Pattern pattern,
                            RuleBuildContext context,
                            Map<String, EvaluatorWrapper> operators,
                            Class< ? > thisClass) {
        this(getDeclarationsMap( pattern, context ), context, operators, thisClass);
    }

    public BoundIdentifiers(Map<String, Class< ? >> declarations,
                            RuleBuildContext context,
                            Map<String, EvaluatorWrapper> operators,
                            Class< ? > thisClass) {
        this.declrClasses = declarations;
        this.context = context;
        this.globals = context != null ? context.getKnowledgeBuilder().getGlobals() : Collections.EMPTY_MAP;
        this.operators = operators;
        this.thisClass = thisClass;
    }

    public RuleBuildContext getContext() {
        return context;
    }

    public Map<String, Class< ? >> getDeclrClasses() {
        return declrClasses;
    }

    public Map<String, Class< ? >> getGlobals() {
        return globals;
    }

    public void setGlobals( Map<String, Class<?>> globals ) {
        this.globals = globals;
    }

    public Map<String, EvaluatorWrapper> getOperators() {
        return operators != null ? operators : Collections.EMPTY_MAP;
    }

    public Class< ? > getThisClass() {
        return thisClass;
    }

    public Class< ? > resolveType(String identifier) {
        Class< ? > cls = declrClasses.get( identifier );

        if ( cls == null ) {
            cls = resolveVarType(identifier);
        }

        if ( cls == null && operators.containsKey( identifier )) {
            cls = context.getConfiguration().getComponentFactory().getExpressionProcessor().getEvaluatorWrapperClass();
        }

        return cls;
    }

    public Class< ? > resolveVarType(String identifier) {
        return context != null ? context.getDeclarationResolver().resolveVarType( identifier ) : null;
    }

    public String toString() {
        return ( "thisClass: " + thisClass + "\n" ) + "declarations:" + declrClasses + "\n" + "globals:" + globals + "\n" + "operators:" + operators + "\n";
    }

    private static Map<String, Class< ? >> getDeclarationsMap( Pattern pattern, RuleBuildContext context ) {
        Map<String, Class< ? >> declarations = new HashMap<>();
        for ( Map.Entry<String, Declaration> entry : context.getDeclarationResolver().getDeclarations( context.getRule() ).entrySet() ) {
            if ( entry.getValue().getExtractor() != null ) {
                declarations.put( entry.getKey(),
                                  entry.getValue().getDeclarationClass() );
            }
        }

        if (pattern != null) {
            List<Class<?>> xpathBackReferenceClasses = pattern.getXpathBackReferenceClasses();
            for ( int i = 0; i <xpathBackReferenceClasses.size(); i++ ) {
                declarations.put( XpathBackReference.BACK_REFERENCE_HEAD + i, xpathBackReferenceClasses.get( i ) );
            }
        }

        return declarations;
    }
}
