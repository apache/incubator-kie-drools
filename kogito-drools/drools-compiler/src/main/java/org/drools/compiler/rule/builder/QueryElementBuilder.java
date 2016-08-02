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

package org.drools.compiler.rule.builder;

import org.drools.compiler.compiler.AnalysisResult;
import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.compiler.compiler.DrlExprParser;
import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.lang.MVELDumper;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.BindingDescr;
import org.drools.compiler.lang.descr.ConstraintConnectiveDescr;
import org.drools.compiler.lang.descr.ExprConstraintDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.extractors.ArrayElementReader;
import org.drools.core.base.extractors.SelfReferenceClassFieldReader;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.QueryArgument;
import org.drools.core.rule.QueryElement;
import org.drools.core.rule.QueryImpl;
import org.drools.core.rule.RuleConditionElement;
import org.drools.core.spi.DeclarationScopeResolver;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.spi.ObjectType;
import org.drools.core.util.ClassUtils;
import org.drools.core.util.MVELSafeHelper;
import org.drools.core.util.StringUtils;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.drools.core.rule.LogicTransformer.toIntArray;
import static org.drools.core.util.StringUtils.isDereferencingIdentifier;

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

    @SuppressWarnings("unchecked")
    public RuleConditionElement build( RuleBuildContext context,
                                       BaseDescr descr,
                                       QueryImpl query) {
        PatternDescr patternDescr = (PatternDescr) descr;

        Declaration[] params = query.getParameters();

        List<BaseDescr> args = (List<BaseDescr>) patternDescr.getDescrs();
        List<Declaration> requiredDeclarations = new ArrayList<Declaration>();

        ObjectType argsObjectType = ClassObjectType.ObjectArray_ObjectType;
        InternalReadAccessor arrayReader = new SelfReferenceClassFieldReader( Object[].class );
        Pattern pattern = new Pattern( context.getNextPatternId(),
                                       0,
                                       argsObjectType,
                                       null );

        if ( !StringUtils.isEmpty( patternDescr.getIdentifier() ) ) {
            if ( query.isAbductive() ) {
                Declaration declr = context.getDeclarationResolver().getDeclaration( query, patternDescr.getIdentifier() );
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

        List<Integer> varIndexList = new ArrayList<Integer>();
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

    @SuppressWarnings("unchecked")
    private void processBinding( RuleBuildContext context,
                                 BaseDescr descr,
                                 Declaration[] params,
                                 QueryArgument[] arguments,
                                 List<Declaration> requiredDeclarations,
                                 InternalReadAccessor arrayReader,
                                 Pattern pattern,
                                 BindingDescr bind ) {
        Declaration declr = context.getDeclarationResolver().getDeclaration( context.getRule(),
                                                                             bind.getVariable() );
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
            declr = context.getDeclarationResolver().getDeclaration( context.getRule(), bind.getExpression() );
            if ( declr != null ) {
                requiredDeclarations.add( declr );
                arguments[pos] = new QueryArgument.Declr( declr );
            } else {
                // it must be a literal/expression
                // it's an expression and thus an input
                DrlExprParser parser = new DrlExprParser( context.getConfiguration().getLanguageLevel() );
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
                                    InternalReadAccessor arrayReader,
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
        Declaration declr = isVariable ? declarationResolver.getDeclaration( query, expression ) : null;

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
                List<Declaration> declarations = new ArrayList<Declaration>();
                for (String identifier : analysisResult.getIdentifiers()) {
                    Declaration declaration = declarationResolver.getDeclaration( query, identifier );
                    if (declaration != null) {
                        declarations.add( declaration );
                    }
                }
                if (declarations.size() == analysisResult.getIdentifiers().size()) {
                    arguments[pos] = new QueryArgument.Expression( declarations, expression, getParserContext( context ) );
                } else {
                    arguments[pos] = getLiteralQueryArgument( context, base, result );
                }
            }
        }
    }

    private AnalysisResult analyzeExpression( RuleBuildContext context, BaseDescr base, String expression ) {
        Map<String, Declaration> decls = context.getDeclarationResolver().getDeclarations( context.getRule() );
        Map<String, Class< ? >> declarationClasses = DeclarationScopeResolver.getDeclarationClasses( decls );
        BoundIdentifiers boundIds = new BoundIdentifiers( declarationClasses, context.getKnowledgeBuilder().getGlobals() );
        return context.getDialect().analyzeBlock( context, base, expression, boundIds );
    }

    private QueryArgument getVariableQueryArgument( InternalReadAccessor arrayReader, Declaration[] params, int pos, Pattern pattern, String expression) {
        // this bit is different, notice its the ArrayElementReader that we wire up to, not the declaration.
        ArrayElementReader reader = new ArrayElementReader( arrayReader,
                                                            pos,
                                                            params[pos].getDeclarationClass() );

        // it's a variable that doesn't exist and doesn't contain a dot, so it's an output
        pattern.addDeclaration( expression ).setReadAccessor( reader );
        return QueryArgument.VAR;
    }

    private QueryArgument getLiteralQueryArgument( RuleBuildContext context, BaseDescr descr, ConstraintConnectiveDescr result ) {
        MVELDumper.MVELDumperContext mvelCtx = new MVELDumper.MVELDumperContext();
        String expr = context.getCompilerFactory().getExpressionProcessor().dump( result, mvelCtx );
        try {
            Object value = MVELSafeHelper.getEvaluator().executeExpression( MVEL.compileExpression( expr, getParserContext(context) ) );
            return new QueryArgument.Literal( value );
        } catch ( Exception e ) {
            context.addError( new DescrBuildError( context.getParentDescr(),
                                                   descr,
                                                   null,
                                                   "Unable to compile expression: " + expr ) );
        }
        return null;
    }

    private ParserContext getParserContext(RuleBuildContext context) {
        MVELDialectRuntimeData data = ( MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData( "mvel" );
        ParserConfiguration conf = data.getParserConfiguration();
        conf.setClassLoader( context.getKnowledgeBuilder().getRootClassLoader() );
        return new ParserContext( conf );
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

    @SuppressWarnings("unchecked")
    private ConstraintConnectiveDescr parseExpression( final RuleBuildContext context,
                                                       final PatternDescr patternDescr,
                                                       final String expression ) {
        DrlExprParser parser = new DrlExprParser( context.getConfiguration().getLanguageLevel() );
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
