/**
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

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.base.base.ClassObjectType;
import org.drools.base.base.ObjectType;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.XpathBackReference;
import org.drools.compiler.rule.builder.EvaluatorWrapper;
import org.drools.compiler.rule.builder.PackageBuildContext;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.kie.api.prototype.PrototypeFactInstance;

import static org.drools.util.ClassUtils.rawType;

public class BoundIdentifiers {
    private Map<String, Class< ? >>       declrClasses;
    private Map<String, Type>             globals;
    private Map<String, EvaluatorWrapper> operators;
    private Class< ? >                    thisClass;
    private PackageBuildContext           context;

    public BoundIdentifiers(Class< ? > thisClass) {
        this( Collections.EMPTY_MAP, null, Collections.EMPTY_MAP, thisClass );
    }

    public BoundIdentifiers(Map<String, Class< ? >> declarations,
                            PackageBuildContext context) {
        this( declarations, context, Collections.EMPTY_MAP, null );
    }

    public BoundIdentifiers(Map<String, Class< ? >> declarations,
                            PackageBuildContext context,
                            Map<String, EvaluatorWrapper> operators) {
        this( declarations, context, operators, null );
    }

    public BoundIdentifiers(Pattern pattern,
                            PackageBuildContext context,
                            Map<String, EvaluatorWrapper> operators,
                            ObjectType objectType) {
        this(getDeclarationsMap( pattern, context ), context, operators, objectType.isPrototype() ? PrototypeFactInstance.class : ((ClassObjectType) objectType).getClassType());
    }

    public BoundIdentifiers(Map<String, Class< ? >> declarations,
                            PackageBuildContext context,
                            Map<String, EvaluatorWrapper> operators,
                            Class< ? > thisClass) {
        this.declrClasses = declarations;
        this.context = context;
        this.globals = context != null ? context.getKnowledgeBuilder().getGlobals() : Collections.EMPTY_MAP;
        this.operators = operators;
        this.thisClass = thisClass;
    }

    public PackageBuildContext getContext() {
        return context;
    }

    public Map<String, Class< ? >> getDeclrClasses() {
        return declrClasses;
    }

    public Map<String, Type> getGlobals() {
        return globals;
    }

    public void setGlobals( Map<String, Type> globals ) {
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
            cls = rawType( resolveVarType(identifier) );
        }

        if ( cls == null && operators.containsKey( identifier )) {
            cls = EvaluatorWrapper.class;
        }

        return cls;
    }

    public Type resolveVarType(String identifier) {
        return context == null ? null : context.resolveVarType(identifier);
    }

    public String toString() {
        return ( "thisClass: " + thisClass + "\n" ) + "declarations:" + declrClasses + "\n" + "globals:" + globals + "\n" + "operators:" + operators + "\n";
    }

    private static Map<String, Class< ? >> getDeclarationsMap( Pattern pattern, PackageBuildContext context ) {
        Map<String, Class< ? >> declarations = new HashMap<>();
        if (context instanceof RuleBuildContext) {
            RuleBuildContext ruleContext = ( (RuleBuildContext) context );
            for ( Map.Entry<String, Declaration> entry : ruleContext.getDeclarationResolver().getDeclarations( ruleContext.getRule() ).entrySet() ) {
                if ( entry.getValue().getExtractor() != null ) {
                    declarations.put( entry.getKey(),
                                      entry.getValue().getDeclarationClass() );
                }
            }

            if ( pattern != null ) {
                List<Class<?>> xpathBackReferenceClasses = pattern.getXpathBackReferenceClasses();
                for ( int i = 0; i < xpathBackReferenceClasses.size(); i++ ) {
                    declarations.put( XpathBackReference.BACK_REFERENCE_HEAD + i, xpathBackReferenceClasses.get( i ) );
                }
            }
        }
        return declarations;
    }
}
