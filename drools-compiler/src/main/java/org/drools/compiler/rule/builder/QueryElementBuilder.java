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
package org.drools.compiler.rule.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.base.base.ClassObjectType;
import org.drools.base.base.CoreComponentsBuilder;
import org.drools.base.base.ObjectType;
import org.drools.base.base.extractors.ArrayElementReader;
import org.drools.base.base.extractors.SelfReferenceClassFieldReader;
import org.drools.base.definitions.rule.impl.QueryImpl;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.QueryArgument;
import org.drools.base.rule.QueryElement;
import org.drools.base.rule.RuleConditionElement;
import org.drools.base.rule.accessor.DeclarationScopeResolver;
import org.drools.base.rule.accessor.ReadAccessor;
import org.drools.compiler.compiler.AnalysisResult;
import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.compiler.lang.DescrDumper;
import org.drools.compiler.lang.DumperContext;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.BindingDescr;
import org.drools.drl.ast.descr.ConstraintConnectiveDescr;
import org.drools.drl.ast.descr.ExprConstraintDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.drl.parser.DrlExprParser;
import org.drools.drl.parser.DroolsParserException;
import org.drools.util.ClassUtils;
import org.drools.util.StringUtils;
import org.kie.internal.builder.conf.LanguageLevelOption;

import static org.drools.base.rule.LogicTransformer.toIntArray;
import static org.drools.util.StringUtils.isDereferencingIdentifier;

public class QueryElementBuilder
    implements
    RuleConditionBuilder {

    private  static final QueryElementBuilder INSTANCE = new QueryElementBuilder();

    public static QueryElementBuilder getInstance() {
        return INSTANCE;
    }

    public RuleConditionElement build( RuleBuildContext context,
                                       BaseDescr descr ) {
        throw new UnsupportedOperationException();
    }
    
    public RuleConditionElement build( RuleBuildContext context,
                                       BaseDescr descr,
                                       Pattern prefixPattern ) {
        throw new UnsupportedOperationException();
    }

    public RuleConditionElement build( RuleBuildContext context,
                                       BaseDescr descr,
                                       QueryImpl query) {
        PatternDescr patternDescr = (PatternDescr) descr;

        Declaration[] params = query.getParameters();

        List<BaseDescr> args = (List<BaseDescr>) patternDescr.getDescrs();
        List<Declaration> requiredDeclarations = new ArrayList<>();

        ObjectType argsObjectType = ClassObjectType.ObjectArray_ObjectType;
        ReadAccessor arrayReader = new SelfReferenceClassFieldReader( Object[].class );
        Pattern pattern = new Pattern( context.getNextPatternId(),
                                       0, // tupleIndex is 0 by default
                                       0, // patternIndex is 0 by default
                                       argsObjectType,
                                       null );

        if ( !StringUtils.isEmpty( patternDescr.getIdentifier() ) ) {
            if ( query.isAbductive() ) {
                Declaration declr = context.getDeclarationResolver().getDeclaration( patternDescr.getIdentifier() );
                if ( declr != null && ! patternDescr.isUnification() ) {
                    context.addError( new DescrBuildError( context.getParentDescr(),
                                                           descr,
                                                           null,
                                                           "Duplicate declaration " + patternDescr.getIdentifier() +", unable to bind abducted value" ) );
                }
            } else {
                context.addError( new DescrBuildError( context.getParentDescr(),
                                                       descr,
                                                       null,
                                                       "Query binding is not supported by non-abductive queries : " + patternDescr.getIdentifier() ) );
            }
        }

        boolean addAbductiveReturnArgument = query.isAbductive()
                                             && ! StringUtils.isEmpty( patternDescr.getIdentifier() )
                                             && args.size() < params.length;

        if ( addAbductiveReturnArgument ) {
            ExprConstraintDescr extraDescr = new ExprConstraintDescr( patternDescr.getIdentifier() );
            extraDescr.setPosition( patternDescr.getConstraint().getDescrs().size() );
            extraDescr.setType( ExprConstraintDescr.Type.POSITIONAL );
            args.add( extraDescr );
        }

        QueryArgument[] arguments = new QueryArgument[params.length];

        // Deal with the constraints, both positional and bindings
        for ( BaseDescr base : args ) {
            String expression = null;
            boolean isPositional = false;
            boolean isBinding = false;
            BindingDescr bind = null;
            ConstraintConnectiveDescr result = null;
            if ( base instanceof BindingDescr ) {
                bind = (BindingDescr) base;
                expression = bind.getVariable() + ( bind.isUnification() ? " := " : " : " ) + bind.getExpression();
                isBinding = true;
            } else {
                if ( base instanceof ExprConstraintDescr ) {
                    ExprConstraintDescr ecd = (ExprConstraintDescr) base;
                    expression = ecd.getExpression();
                    isPositional = ecd.getType() == ExprConstraintDescr.Type.POSITIONAL;

                } else {
                    expression = base.getText();
                }

                result = parseExpression( context,
                                          patternDescr,
                                          expression );
                if ( result == null ) {
                    // error, can't parse expression.
                    context.addError( new DescrBuildError( context.getParentDescr(),
                                                           descr,
                                                           null,
                                                           "Unable to parse constraint: \n" + expression ) );
                    continue;
                }
                isBinding = result.getDescrs().size() == 1 && result.getDescrs().get( 0 ) instanceof BindingDescr;
                if ( isBinding ) {
                    bind = (BindingDescr) result.getDescrs().get( 0 );
                }
            }

            if ( ( !isPositional ) && ( !isBinding ) ) {
                // error, can't have non binding slots.
                context.addError( new DescrBuildError( context.getParentDescr(),
                                                       descr,
                                                       null,
                                                       "Query's must use positional or bindings, not field constraints:\n" + expression ) );
            } else if ( isPositional && isBinding ) {
                // error, can't have positional binding slots.
                context.addError( new DescrBuildError( context.getParentDescr(),
                                                       descr,
                                                       null,
                                                       "Query's can't use positional bindings:\n" + expression ) );
            } else if ( isPositional ) {
                processPositional( context,
                                   query,
                                   params,
                                   arguments,
                                   requiredDeclarations,
                                   arrayReader,
                                   pattern,
                                   base,
                                   expression,
                                   result );
            } else {
                // it is binding
                processBinding( context,
                                descr,
                                params,
                                arguments,
                                requiredDeclarations,
                                arrayReader,
                                pattern,
                                bind );
            }
        }

        List<Integer> varIndexList = new ArrayList<>();
        for (int i = 0; i < arguments.length; i++) {
            if (!(arguments[i] instanceof QueryArgument.Declr)) {
                if (arguments[i] instanceof QueryArgument.Var) {
                    varIndexList.add(i);
                }
                continue;
            }
            Class actual = ((QueryArgument.Declr) arguments[i]).getArgumentClass();
            Declaration formalArgument = query.getParameters()[i];
            Class formal = formalArgument.getDeclarationClass();

            // with queries invoking each other, we won't know until runtime whether a declaration is input, output or else
            // input argument require a broader type, while output types require a narrower type, so we check for both.
            if ( ! ClassUtils.isTypeCompatibleWithArgumentType( actual, formal ) && ! ClassUtils.isTypeCompatibleWithArgumentType( formal, actual ) ) {
                context.addError( new DescrBuildError( context.getParentDescr(),
                                                       descr,
                                                           null,
                                                       "Query is being invoked with known argument of type " + actual +
                                                       " at position " + i + ", but the expected query argument is of type " + formal ) );
            }
        }

        return new QueryElement( pattern,
                                 query.getName(),
                                 arguments,
                                 toIntArray( varIndexList ),
                                 requiredDeclarations.toArray( new Declaration[requiredDeclarations.size()] ),
                                 !patternDescr.isQuery(),
                                 query.isAbductive() );
    }

    private void processBinding( RuleBuildContext context,
                                 BaseDescr descr,
                                 Declaration[] params,
                                 QueryArgument[] arguments,
                                 List<Declaration> requiredDeclarations,
                                 ReadAccessor arrayReader,
                                 Pattern pattern,
                                 BindingDescr bind ) {
        Declaration declr = context.getDeclarationResolver().getDeclaration( bind.getVariable() );
        if ( declr != null ) {
            // check right maps to a slot, otherwise we can't reverse this and should error
            int pos = getPos( bind.getExpression(),
                              params );
            if ( pos >= 0 ) {
                // slot exist, reverse and continue
                String slot = bind.getExpression();
                String var = bind.getVariable();
                bind.setVariable( slot );
                bind.setExpression( var );
            } else {
                // else error, we cannot find the slot to unify against
            }
        }

        // left does not already exist, is it a slot?
        int pos = getPos( bind.getVariable(), params );
        if ( pos >= 0 ) {
            // it's an input on a slot, is the input using bindings?
            declr = context.getDeclarationResolver().getDeclaration( bind.getExpression() );
            if ( declr != null ) {
                requiredDeclarations.add( declr );
                arguments[pos] = new QueryArgument.Declr( declr );
            } else {
                // it must be a literal/expression
                // it's an expression and thus an input
                DrlExprParser parser = new DrlExprParser( context.getConfiguration().getOption(LanguageLevelOption.KEY));
                ConstraintConnectiveDescr bresult = parser.parse( bind.getExpression() );
                if ( parser.hasErrors() ) {
                    for ( DroolsParserException error : parser.getErrors() ) {
                        context.addError( new DescrBuildError( context.getParentDescr(),
                                                                      descr,
                                                                      null,
                                                                      "Unable to parser pattern expression:\n" + error.getMessage() ) );
                    }
                    return;
                }

                arguments[pos] = getLiteralQueryArgument( context, descr, bresult );
            }
        } else {
            // this is creating a new output binding
            // we know it doesn't exist, as we already checked for left == var                    
            pos = getPos( bind.getExpression(), params );
            if ( pos < 0 ) {
                // error this must be a binding on a slot
                context.addError( new DescrBuildError( context.getParentDescr(),
                                                              descr,
                                                              null,
                                                              "named argument does not exist:\n" + bind.getExpression() ) );
                return;                
            }

            arguments[pos] = getVariableQueryArgument( arrayReader, params, pos, pattern, bind.getVariable() );
        }
    }

    private void processPositional( RuleBuildContext context,
                                    QueryImpl query,
                                    Declaration[] params,
                                    QueryArgument[] arguments,
                                    List<Declaration> requiredDeclarations,
                                    ReadAccessor arrayReader,
                                    Pattern pattern,
                                    BaseDescr base,
                                    String expression,
                                    ConstraintConnectiveDescr result ) {
        int pos = ((ExprConstraintDescr) base).getPosition();
        if ( pos >= arguments.length ) {
            context.addError( new DescrBuildError( context.getParentDescr(),
                                                   base,
                                                   null,
                                                   "Unable to parse query '" + query.getName() + "', as postion " + pos + " for expression '" + expression + "' does not exist on query size " + arguments.length) );
            return;
        }

        boolean isVariable = isVariable( expression );

        DeclarationScopeResolver declarationResolver = context.getDeclarationResolver();
        Declaration declr = isVariable ? declarationResolver.getDeclaration( expression ) : null;

        if ( declr != null ) {
            // it exists, so it's an input
            requiredDeclarations.add( declr );
            arguments[pos] = new QueryArgument.Declr(declr);
        } else if( isVariable && expression.indexOf( '.' ) < 0 ) {
            arguments[pos] = getVariableQueryArgument( arrayReader, params, pos, pattern, expression);
        } else {
            // it's an expression and thus an input
            AnalysisResult analysisResult = analyzeExpression( context, base, expression );
            if (analysisResult == null || analysisResult.getIdentifiers().isEmpty()) {
                arguments[pos] = getLiteralQueryArgument( context, base, result );
            } else {
                List<Declaration> declarations = new ArrayList<>();
                for (String identifier : analysisResult.getIdentifiers()) {
                    Declaration declaration = declarationResolver.getDeclaration( identifier );
                    if (declaration != null) {
                        declarations.add( declaration );
                    }
                }
                if (declarations.size() == analysisResult.getIdentifiers().size()) {
                    arguments[pos] = ConstraintBuilder.get().buildExpressionQueryArgument( context, declarations, expression );
                } else {
                    arguments[pos] = getLiteralQueryArgument( context, base, result );
                }
            }
        }
    }

    private AnalysisResult analyzeExpression( RuleBuildContext context, BaseDescr base, String expression ) {
        Map<String, Declaration> decls = context.getDeclarationResolver().getDeclarations( context.getRule() );
        Map<String, Class< ? >> declarationClasses = DeclarationScopeResolver.getDeclarationClasses( decls );
        BoundIdentifiers boundIds = new BoundIdentifiers( declarationClasses, context );
        return context.getDialect().analyzeBlock( context, base, expression, boundIds );
    }

    private QueryArgument getVariableQueryArgument( ReadAccessor arrayReader, Declaration[] params, int pos, Pattern pattern, String expression) {
        // this bit is different, notice its the ArrayElementReader that we wire up to, not the declaration.
        ArrayElementReader reader = new ArrayElementReader( arrayReader,
                                                            pos,
                                                            params[pos].getDeclarationClass() );

        // it's a variable that doesn't exist and doesn't contain a dot, so it's an output
        pattern.addDeclaration( expression ).setReadAccessor( reader );
        return QueryArgument.VAR;
    }

    private QueryArgument getLiteralQueryArgument( RuleBuildContext context, BaseDescr descr, ConstraintConnectiveDescr result ) {
        DumperContext mvelCtx = new DumperContext();
        String expr = DescrDumper.getInstance().dump( result, mvelCtx );
        try {
            Object value = CoreComponentsBuilder.get()
                    .evaluateMvelExpression( context.getPkg().getDialectRuntimeRegistry().getDialectData( "mvel" ),
                            context.getKnowledgeBuilder().getRootClassLoader(), expr );
            return new QueryArgument.Literal( value );
        } catch ( Exception e ) {
            context.addError( new DescrBuildError( context.getParentDescr(),
                                                   descr,
                                                   null,
                                                   "Unable to compile expression: " + expr ) );
        }
        return null;
    }

    private static int getPos( String identifier,
                              Declaration[] params ) {
        for ( int i = 0; i < params.length; i++ ) {
            if ( params[i].getIdentifier().trim().equals( identifier ) ) {
                return i;
            }
        }
        return -1;
    }

    private ConstraintConnectiveDescr parseExpression( final RuleBuildContext context,
                                                       final PatternDescr patternDescr,
                                                       final String expression ) {
        DrlExprParser parser = new DrlExprParser( context.getConfiguration().getOption(LanguageLevelOption.KEY));
        ConstraintConnectiveDescr result = parser.parse( expression );
        if ( result == null || parser.hasErrors() ) {
            for ( DroolsParserException error : parser.getErrors() ) {
                context.addError( new DescrBuildError( context.getParentDescr(),
                                                              patternDescr,
                                                              null,
                                                              "Unable to parser pattern expression:\n" + error.getMessage() ) );
            }
            return null;
        }
        return result;
    }

    private static boolean isVariable( String str ) {
        str = str.trim();
        return isDereferencingIdentifier( str ) && !str.endsWith( ".class" );
    }

}
