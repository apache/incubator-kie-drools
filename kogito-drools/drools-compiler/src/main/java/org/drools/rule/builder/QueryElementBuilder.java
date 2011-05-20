package org.drools.rule.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.drools.base.ArrayElements;
import org.drools.base.ClassObjectType;
import org.drools.base.extractors.ArrayElementReader;
import org.drools.base.extractors.SelfReferenceClassFieldReader;
import org.drools.compiler.DescrBuildError;
import org.drools.compiler.DrlExprParser;
import org.drools.compiler.DroolsParserException;
import org.drools.lang.MVELDumper;
import org.drools.lang.descr.AtomicExprDescr;
import org.drools.lang.descr.BindingDescr;
import org.drools.lang.descr.ConstraintConnectiveDescr;
import org.drools.lang.descr.ExprConstraintDescr;
import org.drools.lang.descr.LiteralDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.VariableDescr;
import org.drools.rule.Declaration;
import org.drools.rule.Pattern;
import org.drools.rule.Query;
import org.drools.rule.QueryElement;
import org.drools.rule.Rule;
import org.drools.rule.RuleConditionElement;
import org.drools.rule.Variable;
import org.drools.spi.InternalReadAccessor;
import org.drools.spi.ObjectType;
import org.mvel2.MVEL;
import org.mvel2.compiler.ExpressionCompiler;
import org.mvel2.util.PropertyTools;

public class QueryElementBuilder
    implements
    RuleConditionBuilder {

    public RuleConditionElement build(RuleBuildContext context,
                                      BaseDescr descr) {
        return this.build( context,
                           descr,
                           null );
    }

    public RuleConditionElement build(RuleBuildContext context,
                                      BaseDescr descr,
                                      Pattern prefixPattern) {
        PatternDescr patternDescr = (PatternDescr) descr;
        if ( !patternDescr.isQuery() ) {
            // error, can't have non binding slots.
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                          descr,
                                                          null,
                                                          "Query's must ? as they are pull only:\n" ) );
            return null;            
        }
        Query query = (Query) context.getPkg().getRule( patternDescr.getObjectType() );
        if ( query == null ) { 
            // we already checked this before, so we know it's either in the package or recursive on itself
            query = (Query) context.getRule();            
        }

        Declaration[] params = query.getParameters();

        List<BaseDescr> args = (List<BaseDescr>) patternDescr.getDescrs();
        List<Integer> declrIndexes = new ArrayList<Integer>();
        List<Integer> varIndexes = new ArrayList<Integer>();
        List<Object> arguments = new ArrayList<Object>(params.length);
        for ( int i = 0; i < params.length; i++ ) {
            // as these could be set in any order, initialise first, to allow setting later.
            arguments.add( null );
        }
        List<Declaration> requiredDeclarations = new ArrayList<Declaration>();       

        ObjectType argsObjectType = new ClassObjectType( Object[].class );
        InternalReadAccessor arrayReader = new SelfReferenceClassFieldReader(Object[].class, "this");
        Pattern pattern = new Pattern( context.getNextPatternId(),
                                       0,
                                       argsObjectType,
                                       null );

        // first do the positional
        for ( int i = 0, length = args.size(); i < length; i++ ) {
            ExprConstraintDescr arg = ( ExprConstraintDescr ) args.get( i );

            if( arg.getType() != ExprConstraintDescr.Type.POSITIONAL || arg.getPosition() == -1 ) {
                // error, can't have non binding slots.
                context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                              descr,
                                                              null,
                                                              "Query's must use positional or bindings, not field constraints:\n" + arg.getExpression() ) );
                continue;
            }
                            
            // it's a unary positional argument
            String expr =  arg.getExpression().trim();
            if ( isVariable( expr ) ) {
                // is this already bound?
                Declaration declr = context.getDeclarationResolver().getDeclaration( query,
                                                                                     expr );
                if ( declr != null ) {
                    // it exists, so it's an input
                    arguments.set( arg.getPosition(), declr );
                    declrIndexes.add( arg.getPosition() );                    
                    requiredDeclarations.add( declr );                        
                } else {
                    // it doesn't exist, so it's an output                    
                    declr = pattern.addDeclaration( expr );

                    // this bit is different, notice its the ArrayElementReader that we wire up to, not the declaration.
                    ArrayElementReader reader = new ArrayElementReader( arrayReader,
                                                                        varIndexes.size(),
                                                                        params[arg.getPosition()].getExtractor().getExtractToClass() );                    

                    declr.setReadAccessor( reader );

                    varIndexes.add( arg.getPosition() );
                    arguments.set( arg.getPosition(), Variable.variable );                        
                }
            } else {
                // it's an expression and thus an input
                DrlExprParser parser = new DrlExprParser();
                ConstraintConnectiveDescr result = parser.parse( expr );
                if ( parser.hasErrors() ) {
                    for ( DroolsParserException error : parser.getErrors() ) {
                        context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                                      descr,
                                                                      null,
                                                                      "Unable to parser pattern expression:\n" + error.getMessage() ) );
                    }
                    return null;
                } 
              
              MVELDumper.MVELDumperContext mvelCtx = new MVELDumper.MVELDumperContext();
              expr = new MVELDumper().dump( result,
                                            mvelCtx );
              arguments.set( arg.getPosition(), MVEL.eval( expr )); // for now we just work with literals  
            }
        }
        
        // now do the slotted, all of which should be done via : bindings
        for ( BindingDescr binding : patternDescr.getBindings() ) {
            // if left is for existing binding, we need to actual execute as an input var           
            Declaration declr = context.getDeclarationResolver().getDeclaration( context.getRule(), binding.getVariable() );
            if ( declr != null ) {
                // check right maps to a slot, otherwise we can't reverse this and should error
                int pos = getPos( binding.getExpression().trim(), params );
                if ( pos >= 0 ) {                
                    // slot exist, reverse and continue
                    String slot = binding.getExpression().trim();
                    String var = binding.getVariable().trim();
                    binding.setVariable( slot );
                    binding.setExpression( var );
                } else {
                    // else error, we cannot find the slot to unify against
                }
            } 

            // left does not already exist, is it a slot?
            int pos = getPos( binding.getVariable(), params );
            if ( pos >= 0 ) {
                // it's an input on a slot, is the input using bindings?
                declr = context.getDeclarationResolver().getDeclaration( context.getRule(), binding.getExpression() );
                if ( declr != null ) {
                    arguments.set( pos, declr );
                    declrIndexes.add( pos );                    
                    requiredDeclarations.add( declr );   
                } else {
                    // it must be a literal/expression
                    // it's an expression and thus an input
                    DrlExprParser parser = new DrlExprParser();
                    ConstraintConnectiveDescr result = parser.parse( binding.getExpression() );
                    if ( parser.hasErrors() ) {
                        for ( DroolsParserException error : parser.getErrors() ) {
                            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                                          descr,
                                                                          null,
                                                                          "Unable to parser pattern expression:\n" + error.getMessage() ) );
                        }
                        return null;
                    } 

                    MVELDumper.MVELDumperContext mvelCtx = new MVELDumper.MVELDumperContext();
                    String expr = new MVELDumper().dump( result,
                                                         mvelCtx );
                    Object o = MVEL.eval( expr );
                    arguments.set( pos, o ); // for now we just work with literals                    
                }                 
            } else {
                // this is creating a new output binding
                // we know it doesn't exist, as we already checked for left == var                    
                declr = pattern.addDeclaration( binding.getVariable() );
                
                pos = getPos( binding.getExpression(), params );
                if ( pos < 0 ) {
                    // error this must be a binding on a slot
                    throw new RuntimeException( "named argument does not exist" );
                }

                // this bit is different, notice its the ArrayElementReader that we wire up to, not the declaration.
                ArrayElementReader reader = new ArrayElementReader( arrayReader,
                                                                    varIndexes.size(),
                                                                    params[pos].getExtractor().getExtractToClass() );

                declr.setReadAccessor( reader );

                varIndexes.add( pos );
                arguments.set( pos, Variable.variable );                    
            }  
        }
            
        Declaration[] declrsArray = requiredDeclarations.toArray( new Declaration[requiredDeclarations.size()] );
        int[] declrIndexArray = new int[declrIndexes.size()];
        for( int i = 0; i < declrsArray.length; i++ ) {
            declrIndexArray[i] = declrIndexes.get( i );
        }
        int[] varIndexesArray = new int[varIndexes.size()];
        for( int i = 0; i < varIndexesArray.length; i++ ) {
            varIndexesArray[i] = varIndexes.get( i );
        }
        
        return new QueryElement( pattern,
                                 query.getName(),
                                 arguments.toArray( new Object[arguments.size()] ),
                                 declrsArray,
                                 declrIndexArray,
                                 varIndexesArray );
    }
    
    public static int getPos(String identifier, Declaration[] params) {
        for ( int i = 0; i < params.length; i++ ) {
            if ( params[i].getIdentifier().trim().equals( identifier ) ) {
                return i;
            }
        }
        return -1;
    }
        
    public static boolean isAtomic(ConstraintConnectiveDescr result) {
        return (result.getDescrs().size() == 1 && result.getDescrs().get(0) instanceof AtomicExprDescr );
    }
    
    public static boolean isVariable(String str) {
        str = str.trim();
        switch ( str.charAt( 0 )) {
            case '"':
            case '\'':
            case '-':
            case '+':
            case '.':
            case '(':
            case ')':
            case '[':
            case ']':
            case '{':
            case '}':                
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                return false;
        }
        
        for ( int i = 1; i < str.length(); i++ ) {
            switch ( str.charAt( i )) {
                case '"':
                case '\'':
                case '-':
                case '+':
                case '.':   
                case '(':
                case ')':
                case '[':
                case ']': 
                case '{':
                case '}': 
                    return false; 
            }
        }
        
        return true;
    }

}
