package org.drools.rule.builder;

import java.util.ArrayList;
import java.util.List;

import org.drools.base.ClassObjectType;
import org.drools.base.extractors.ArrayElementReader;
import org.drools.base.extractors.SelfReferenceClassFieldReader;
import org.drools.compiler.DescrBuildError;
import org.drools.compiler.DrlExprParser;
import org.drools.compiler.DroolsParserException;
import org.drools.lang.MVELDumper;
import org.drools.lang.descr.AtomicExprDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.BindingDescr;
import org.drools.lang.descr.ConstraintConnectiveDescr;
import org.drools.lang.descr.ExprConstraintDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.rule.Declaration;
import org.drools.rule.Pattern;
import org.drools.rule.Query;
import org.drools.rule.QueryElement;
import org.drools.rule.RuleConditionElement;
import org.drools.runtime.rule.Variable;
import org.drools.spi.InternalReadAccessor;
import org.drools.spi.ObjectType;
import org.mvel2.MVEL;

public class QueryElementBuilder
    implements
    RuleConditionBuilder {

    public RuleConditionElement build( RuleBuildContext context,
                                       BaseDescr descr ) {
        return this.build( context,
                           descr,
                           null );
    }
    
    public RuleConditionElement build( RuleBuildContext context,
                                       BaseDescr descr,
                                       Pattern prefixPattern ) {
        throw new UnsupportedOperationException();
        
    }

    @SuppressWarnings("unchecked")
    public RuleConditionElement build( RuleBuildContext context,
                                       BaseDescr descr,
                                       Pattern prefixPattern,
                                       Query query) {
        PatternDescr patternDescr = (PatternDescr) descr;

        Declaration[] params = query.getParameters();

        List<BaseDescr> args = (List<BaseDescr>) patternDescr.getDescrs();
        List<Integer> declrIndexes = new ArrayList<Integer>();
        List<Integer> varIndexes = new ArrayList<Integer>();
        List<Object> arguments = new ArrayList<Object>( params.length );
        for ( int i = 0; i < params.length; i++ ) {
            // as these could be set in any order, initialise first, to allow setting later.
            arguments.add( null );
        }
        List<Declaration> requiredDeclarations = new ArrayList<Declaration>();

        ObjectType argsObjectType = ClassObjectType.ObjectArray_ObjectType;
        InternalReadAccessor arrayReader = new SelfReferenceClassFieldReader( Object[].class,
                                                                              "this" );
        Pattern pattern = new Pattern( context.getNextPatternId(),
                                       0,
                                       argsObjectType,
                                       null );

        // Deal with the constraints, both positional and bindings
        for ( int i = 0, length = args.size(); i < length; i++ ) {
            BaseDescr base = args.get( i );

            String expression = null;
            boolean isPositional = false;
            boolean isBinding = false;
            BindingDescr bind = null;
            ConstraintConnectiveDescr result = null;
            if ( base instanceof BindingDescr ) {
                bind = (BindingDescr) base;
                expression = bind.getVariable() + (bind.isUnification() ? " := " : " : ") + bind.getExpression();
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
                    context.getErrors().add( new DescrBuildError( context.getParentDescr(),
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

            if ( (!isPositional) && (!isBinding) ) {
                // error, can't have non binding slots.
                context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                              descr,
                                                              null,
                                                              "Query's must use positional or bindings, not field constraints:\n" + expression ) );
                continue;
            } else if ( isPositional && isBinding ) {
                // error, can't have positional binding slots.
                context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                              descr,
                                                              null,
                                                              "Query's can't use positional bindings:\n" + expression ) );
                continue;
            } else if ( isPositional ) {
                processPositional( context,
                                   query,
                                   params,
                                   declrIndexes,
                                   varIndexes,
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
                                declrIndexes,
                                varIndexes,
                                arguments,
                                requiredDeclarations,
                                arrayReader,
                                pattern,
                                bind,
                                result );
            }

        }

        Declaration[] declrsArray = requiredDeclarations.toArray( new Declaration[requiredDeclarations.size()] );
        int[] declrIndexArray = new int[declrIndexes.size()];
        for ( int i = 0; i < declrsArray.length; i++ ) {
            declrIndexArray[i] = declrIndexes.get( i );
        }
        int[] varIndexesArray = new int[varIndexes.size()];
        for ( int i = 0; i < varIndexesArray.length; i++ ) {
            varIndexesArray[i] = varIndexes.get( i );
        }

        return new QueryElement( pattern,
                                 query.getName(),
                                 arguments.toArray( new Object[arguments.size()] ),
                                 declrsArray,
                                 declrIndexArray,
                                 varIndexesArray,
                                 !patternDescr.isQuery() );
    }

    @SuppressWarnings("unchecked")
    private void processBinding( RuleBuildContext context,
                                 BaseDescr descr,
                                 Declaration[] params,
                                 List<Integer> declrIndexes,
                                 List<Integer> varIndexes,
                                 List<Object> arguments,
                                 List<Declaration> requiredDeclarations,
                                 InternalReadAccessor arrayReader,
                                 Pattern pattern,
                                 BindingDescr bind,
                                 ConstraintConnectiveDescr result ) {
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
        int pos = getPos( bind.getVariable(),
                          params );
        if ( pos >= 0 ) {
            // it's an input on a slot, is the input using bindings?
            declr = context.getDeclarationResolver().getDeclaration( context.getRule(),
                                                                     bind.getExpression() );
            if ( declr != null ) {
                arguments.set( pos,
                               declr );
                declrIndexes.add( pos );
                requiredDeclarations.add( declr );
            } else {
                // it must be a literal/expression
                // it's an expression and thus an input
                DrlExprParser parser = new DrlExprParser();
                ConstraintConnectiveDescr bresult = parser.parse( bind.getExpression() );
                if ( parser.hasErrors() ) {
                    for ( DroolsParserException error : parser.getErrors() ) {
                        context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                                      descr,
                                                                      null,
                                                                      "Unable to parser pattern expression:\n" + error.getMessage() ) );
                    }
                    return;
                }

                MVELDumper.MVELDumperContext mvelCtx = new MVELDumper.MVELDumperContext();
                String expr = new MVELDumper().dump( bresult,
                                                     mvelCtx );
                try {
                Object o = MVEL.eval( expr );
                arguments.set( pos,
                               o ); // for now we just work with literals
                } catch ( Exception e ) {
                    context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                                  descr,
                                                                  null,
                                                                  "Unable to compile expression:\n" + expr ) );
                }                       
            }
        } else {
            // this is creating a new output binding
            // we know it doesn't exist, as we already checked for left == var                    
            declr = pattern.addDeclaration( bind.getVariable() );

            pos = getPos( bind.getExpression(),
                          params );
            if ( pos < 0 ) {
                // error this must be a binding on a slot
                context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                              descr,
                                                              null,
                                                              "named argument does not exist:\n" + bind.getExpression() ) );
                return;                
            }

            // this bit is different, notice its the ArrayElementReader that we wire up to, not the declaration.
            ArrayElementReader reader = new ArrayElementReader( arrayReader,
                                                                pos,
                                                                params[pos].getExtractor().getExtractToClass() );

            declr.setReadAccessor( reader );

            varIndexes.add( pos );
            arguments.set( pos,
                           Variable.v );
        }
    }

    private void processPositional( RuleBuildContext context,
                                    Query query,
                                    Declaration[] params,
                                    List<Integer> declrIndexes,
                                    List<Integer> varIndexes,
                                    List<Object> arguments,
                                    List<Declaration> requiredDeclarations,
                                    InternalReadAccessor arrayReader,
                                    Pattern pattern,
                                    BaseDescr base,
                                    String expression,
                                    ConstraintConnectiveDescr result ) {
        int position = ((ExprConstraintDescr) base).getPosition();
        if ( position >= arguments.size() ) {
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                          base,
                                                          null,
                                                          "Unable to parse query '" + query.getName() + "', as postion " + (position-1) + " for expression '" + expression + "' does not exist on query size " + arguments.size()) );
            return;            
        }
        if ( isVariable( expression ) ) {
            // is this already bound?
            Declaration declr = context.getDeclarationResolver().getDeclaration( query,
                                                                                 expression );
            if ( declr != null ) {
                // it exists, so it's an input
                arguments.set( position,
                               declr );
                declrIndexes.add( position );
                requiredDeclarations.add( declr );
            } else {
                // it doesn't exist, so it's an output
                arguments.set( position,
                               Variable.v );
                
                varIndexes.add( position );
                               
                declr = pattern.addDeclaration( expression );

                // this bit is different, notice its the ArrayElementReader that we wire up to, not the declaration.
                ArrayElementReader reader = new ArrayElementReader( arrayReader,
                                                                    position,
                                                                    params[position].getExtractor().getExtractToClass() );

                declr.setReadAccessor( reader );
            }
        } else {
            // it's an expression and thus an input
            MVELDumper.MVELDumperContext mvelCtx = new MVELDumper.MVELDumperContext();
            String rewrittenExpr = new MVELDumper().dump( result,
                                                          mvelCtx );
            arguments.set( position,
                           MVEL.eval( rewrittenExpr ) ); // for now we just work with literals  
        }
    }

    public static int getPos( String identifier,
                              Declaration[] params ) {
        for ( int i = 0; i < params.length; i++ ) {
            if ( params[i].getIdentifier().trim().equals( identifier ) ) {
                return i;
            }
        }
        return -1;
    }

    public static boolean isAtomic( ConstraintConnectiveDescr result ) {
        return (result.getDescrs().size() == 1 && result.getDescrs().get( 0 ) instanceof AtomicExprDescr);
    }

    @SuppressWarnings("unchecked")
    private ConstraintConnectiveDescr parseExpression( final RuleBuildContext context,
                                                       final PatternDescr patternDescr,
                                                       final String expression ) {
        DrlExprParser parser = new DrlExprParser();
        ConstraintConnectiveDescr result = parser.parse( expression );
        if ( result == null || parser.hasErrors() ) {
            for ( DroolsParserException error : parser.getErrors() ) {
                context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                              patternDescr,
                                                              null,
                                                              "Unable to parser pattern expression:\n" + error.getMessage() ) );
            }
            return null;
        }
        return result;
    }

    public static boolean isVariable( String str ) {
        str = str.trim();
        
        // check for invalid variable name start char
        switch ( str.charAt( 0 ) ) {
            case '\'' :
            case '"' :
            case '-' :
            case '+' :
            case '!' :
            case '>' :
            case '<' :
            case '&' :
            case '|' :
            case '?' :
            case '^' :
            case '%' :
            case '=' :
            case '.' :
            case '(' :
            case ')' :
            case '[' :
            case ']' :
            case '{' :
            case '}' :
            case '0' :
            case '1' :
            case '2' :
            case '3' :
            case '4' :
            case '5' :
            case '6' :
            case '7' :
            case '8' :
            case '9' :
                return false;
        }

        // Check for operators
        for ( int i = 1; i < str.length(); i++ ) {
            switch ( str.charAt( i ) ) {
                //case '"' :
                case '\'' :
                case '"' :                
                case '-' :
                case '+' :
                case '!' :
                case '>' :
                case '<' :
                case '&' :
                case '|' :
                case '?' :
                case '^' :
                case '%' :
                case '=' :
                case '{' :
                case '}' :
                    return false;
            }
        }

        return true;
    }

}
