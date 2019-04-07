/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.builder.dialect.mvel;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.drools.compiler.compiler.AnalysisResult;
import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.rule.builder.PackageBuildContext;
import org.drools.compiler.rule.builder.dialect.mvel.MVELAnalysisResult;
import org.drools.compiler.rule.builder.dialect.mvel.MVELDialect;
import org.jbpm.process.builder.ProcessBuildContext;

public class AbstractMVELBuilder {

    /**
     * Allows newlines to demarcate expressions, as per MVEL command line.
     * If expression spans multiple lines (ie inside an unbalanced bracket) then
     * it is left alone.
     * Uses character based iteration which is at least an order of magnitude faster then a single
     * simple regex.
     */
    public static String delimitExpressions(String s) {

        StringBuilder result = new StringBuilder();
        char[] cs = s.toCharArray();
        int brace = 0;
        int sqre = 0;
        int crly = 0;
        char lastNonWhite = ';';
        for ( int i = 0; i < cs.length; i++ ) {
            char c = cs[i];
            switch ( c ) {
                case '(' :
                    brace++;
                    break;
                case '{' :
                    crly++;
                    break;
                case '[' :
                    sqre++;
                    break;
                case ')' :
                    brace--;
                    break;
                case '}' :
                    crly--;
                    break;
                case ']' :
                    sqre--;
                    break;
                default :
                    break;
            }
            if ( (brace == 0 && sqre == 0 && crly == 0) && (c == '\n' || c == '\r') ) {
                if ( lastNonWhite != ';' ) {
                    result.append( ';' );
                    lastNonWhite = ';';
                }
            } else if ( !Character.isWhitespace( c ) ) {
                lastNonWhite = c;
            }
            result.append( c );

        }
        return result.toString();
    }
   

    protected MVELAnalysisResult getAnalysis(final PackageBuildContext context,
                                         final BaseDescr descr, 
                                         MVELDialect dialect,
                                         final String text,
                                         Map<String,Class<?>> variables) { 
       
        boolean typeSafe = context.isTypesafe();
        
        // we can't know all the types ahead of time with processes, but we don't need return types, so it's ok
        context.setTypesafe( false ); 
        
        MVELAnalysisResult analysis = null;
        try { 
            BoundIdentifiers boundIdentifiers = new BoundIdentifiers(variables, context);
            analysis = ( MVELAnalysisResult ) dialect.analyzeBlock( context,
                                                                    text,
                                                                    boundIdentifiers,
                                                                    null,
                                                                    "context",
                                                                    org.kie.api.runtime.process.ProcessContext.class );
        } finally { 
            context.setTypesafe( typeSafe );
        }
        
        return analysis;
    }
    
    protected void collectTypes(String key, AnalysisResult analysis, ProcessBuildContext context) {
        if (context.getProcess() != null) {
            Set<String> referencedTypes = new HashSet<String>();
            
            MVELAnalysisResult mvelAnalysis = (MVELAnalysisResult) analysis;
            
            for( Class<?> varClass : mvelAnalysis.getMvelVariables().values() ) { 
                referencedTypes.add(varClass.getCanonicalName());
            }
            
            context.getProcess().getMetaData().put(key + "ReferencedTypes", referencedTypes);
        }
        
    }
}
