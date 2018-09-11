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

package org.drools.modelcompiler.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.compiler.rule.builder.dialect.mvel.MVELAnalysisResult;
import org.drools.compiler.rule.builder.dialect.mvel.MVELDialect;
import org.drools.core.base.EvaluatorWrapper;
import org.drools.core.base.mvel.MVELCompilationUnit;
import org.drools.core.base.mvel.MVELObjectExpression;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.DeclarationScopeResolver;
import org.drools.core.spi.KnowledgeHelper;
import org.kie.api.definition.rule.Rule;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;
import org.mvel2.util.PropertyTools;

import static org.drools.compiler.rule.builder.dialect.mvel.MVELExprAnalyzer.analyze;
import static org.drools.core.rule.constraint.EvaluatorHelper.WM_ARGUMENT;

public class MvelUtil {

    public static MVELObjectExpression createMvelObjectExpression( String expression, ClassLoader classLoader, Map<String, Declaration> decls ) {
        if (expression == null) {
            return null;
        }

        ParserConfiguration conf = new ParserConfiguration();
        conf.setClassLoader( classLoader );

        MVELAnalysisResult analysis = analyzeExpression( expression, conf, new BoundIdentifiers( DeclarationScopeResolver.getDeclarationClasses( decls ), null ) );

        final BoundIdentifiers usedIdentifiers = analysis.getBoundIdentifiers();
        int i = usedIdentifiers.getDeclrClasses().keySet().size();
        Declaration[] previousDeclarations = new Declaration[i];
        i = 0;
        for ( String id :  usedIdentifiers.getDeclrClasses().keySet() ) {
            previousDeclarations[i++] = decls.get( id );
        }
        Arrays.sort(previousDeclarations, RuleTerminalNode.SortDeclarations.instance);

        MVELCompilationUnit unit = MVELDialect.getMVELCompilationUnit( expression,
                                                                       analysis,
                                                                       previousDeclarations,
                                                                       null,
                                                                       null,
                                                                       null,
                                                                       "drools",
                                                                       KnowledgeHelper.class,
                                                                       false,
                                                                       MVELCompilationUnit.Scope.EXPRESSION );

        MVELObjectExpression expr = new MVELObjectExpression( unit, "mvel" );
        expr.compile( conf );
        return expr;
    }

    public static MVELAnalysisResult analyzeExpression(Class<?> thisClass, String expr) {
        ParserConfiguration conf = new ParserConfiguration();
        conf.setClassLoader( thisClass.getClassLoader() );
        return analyzeExpression( expr, conf, new BoundIdentifiers( thisClass ) );
    }

    private static MVELAnalysisResult analyzeExpression(String expr,
                                                        ParserConfiguration conf,
                                                        BoundIdentifiers availableIdentifiers) {
        if ( expr.trim().length() <= 0 ) {
            MVELAnalysisResult result = analyze( (Set<String> ) Collections.EMPTY_SET, availableIdentifiers );
            result.setMvelVariables( new HashMap<String, Class< ? >>() );
            result.setTypesafe( true );
            return result;
        }

        MVEL.COMPILER_OPT_ALLOW_NAKED_METH_CALL = true;
        MVEL.COMPILER_OPT_ALLOW_OVERRIDE_ALL_PROPHANDLING = true;
        MVEL.COMPILER_OPT_ALLOW_RESOLVE_INNERCLASSES_WITH_DOTNOTATION = true;
        MVEL.COMPILER_OPT_SUPPORT_JAVA_STYLE_CLASS_LITERALS = true;

        // first compilation is for verification only
        final ParserContext parserContext1 = new ParserContext( conf );
        if ( availableIdentifiers.getThisClass() != null ) {
            parserContext1.addInput( "this", availableIdentifiers.getThisClass() );
        }

        if ( availableIdentifiers.getOperators() != null ) {
            for ( Map.Entry<String, EvaluatorWrapper> opEntry : availableIdentifiers.getOperators().entrySet() ) {
                parserContext1.addInput( opEntry.getKey(), opEntry.getValue().getClass() );
            }
        }

        parserContext1.setStrictTypeEnforcement( false );
        parserContext1.setStrongTyping( false );
        Class< ? > returnType;

        try {
            returnType = MVEL.analyze( expr, parserContext1 );
        } catch ( Exception e ) {
            return null;
        }

        Set<String> requiredInputs = new HashSet<>( parserContext1.getInputs().keySet() );
        Map<String, Class> variables = parserContext1.getVariables();

        // MVEL includes direct fields of context object in non-strict mode. so we need to strip those
        if ( availableIdentifiers.getThisClass() != null ) {
            requiredInputs.removeIf( s -> PropertyTools.getFieldOrAccessor( availableIdentifiers.getThisClass(), s ) != null );
        }

        // now, set the required input types and compile again
        final ParserContext parserContext2 = new ParserContext( conf );
        parserContext2.setStrictTypeEnforcement( true );
        parserContext2.setStrongTyping( true );

        for ( String input : requiredInputs ) {
            if ("this".equals( input )) {
                continue;
            }
            if (WM_ARGUMENT.equals( input )) {
                parserContext2.addInput( input, InternalWorkingMemory.class );
                continue;
            }

            Class< ? > cls = availableIdentifiers.resolveType( input );
            if ( cls == null ) {
                if ( input.equals( "rule" ) ) {
                    cls = Rule.class;
                }
            }
            if ( cls != null ) {
                parserContext2.addInput( input, cls );
            }
        }

        if ( availableIdentifiers.getThisClass() != null ) {
            parserContext2.addInput( "this", availableIdentifiers.getThisClass() );
        }

        try {
            returnType = MVEL.analyze( expr, parserContext2 );
        } catch ( Exception e ) {
            return null;
        }

        requiredInputs = new HashSet<>();
        requiredInputs.addAll( parserContext2.getInputs().keySet() );
        requiredInputs.addAll( variables.keySet() );
        variables = parserContext2.getVariables();

        MVELAnalysisResult result = analyze( requiredInputs, availableIdentifiers );
        result.setReturnType( returnType );
        result.setMvelVariables( (Map<String, Class< ? >>) (Map) variables );
        result.setTypesafe( true );
        return result;
    }
}
