package org.drools.rule.builder.dialect.mvel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.drools.FactHandle;
import org.drools.base.mvel.DroolsMVELFactory;
import org.drools.base.mvel.DroolsMVELPreviousDeclarationVariable;
import org.drools.base.mvel.MVELConsequence;
import org.drools.compiler.RuleError;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.builder.ConsequenceBuilder;
import org.drools.rule.builder.RuleBuildContext;
import org.mvel.ASTNode;
import org.mvel.MVEL;
import org.mvel.Macro;
import org.mvel.MacroProcessor;
import org.mvel.ast.WithNode;
import org.mvel.integration.Interceptor;
import org.mvel.integration.VariableResolverFactory;

public class MVELConsequenceBuilder
    implements
    ConsequenceBuilder {

    //private final Interceptor assertInterceptor;
    //private final Interceptor modifyInterceptor;
    private final Map         interceptors;
    private final Map         macros;

    public MVELConsequenceBuilder() {
        this.interceptors = new HashMap(1);
        this.interceptors.put( "Modify", new ModifyInterceptor() );
//        this.assertInterceptor = new AssertInterceptor();
//        this.modifyInterceptor = new ModifyInterceptor();
        this.macros = new HashMap(1);
//        macros.put( "assert",
//                    new Macro() {
//                        public String doMacro() {
//                            return "@Modify with";
//                        }
//                    } );   
        
        macros.put( "modify",
                    new Macro() {
                        public String doMacro() {
                            return "@Modify with";
                        }
                    } );
        
     
    }

    public void build(final RuleBuildContext context) {
        // pushing consequence LHS into the stack for variable resolution
        context.getBuildStack().push( context.getRule().getLhs() );

        try {
            final DroolsMVELFactory factory = new DroolsMVELFactory( context.getDeclarationResolver().getDeclarations(),
                                                                     null,
                                                                     context.getPkg().getGlobals() );
            factory.setNextFactory( ((MVELDialect) context.getDialect()).getClassImportResolverFactory() );

            
            
            final Serializable expr = MVEL.compileExpression( new MacroProcessor(delimitExpressions( (String) context.getRuleDescr().getConsequence() )).parse(this.macros),
                                                              ((MVELDialect) context.getDialect()).getClassImportResolverFactory().getImportedClasses(), this.interceptors );

            context.getRule().setConsequence( new MVELConsequence( expr,
                                                                   factory ) );
        } catch ( final Exception e ) {
            context.getErrors().add( new RuleError( context.getRule(),
                                                    context.getRuleDescr(),
                                                    null,
                                                    "Unable to build expression for 'consequence' '" + context.getRuleDescr().getConsequence() + "'" ) );
        }
    }

    /**
     * Allows newlines to demarcate expressions, as per MVEL command line.
     * If expression spans multiple lines (ie inside an unbalanced bracket) then
     * it is left alone.
     * Uses character based iteration which is at least an order of magnitude faster then a single 
     * simple regex.
     */
    public String delimitExpressions(String s) {

        StringBuffer result = new StringBuffer();
        char[] cs = s.toCharArray();
        int brace = 0;
        int sqre = 0;
        int crly = 0;
        char lastNonWhite = ' ';
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

    public static class AssertInterceptor
        implements
        Interceptor {
        public int doBefore(ASTNode node,
                            VariableResolverFactory factory) {
            return 0;
        }

        public int doAfter(Object value,
                           ASTNode node,
                           VariableResolverFactory factory) {
            ((DroolsMVELFactory) factory).getWorkingMemory().assertObject( value );
            return 0;
        }
    }

    public static class ModifyInterceptor
        implements
        Interceptor {
        public int doBefore(ASTNode node,
                            VariableResolverFactory factory) {
            Object object = ((WithNode) node). getNestedStatement().getValue( null,
                                                                              factory );
            FactHandle handle = ((DroolsMVELFactory) factory).getWorkingMemory().getFactHandle( object );
            ((DroolsMVELFactory) factory).getWorkingMemory().modifyRetract( handle, null, null );
            return 0;
        }

        public int doAfter(Object value,
                           ASTNode node,
                           VariableResolverFactory factory) {
            FactHandle handle = ((DroolsMVELFactory) factory).getWorkingMemory().getFactHandle( value );            
            ((DroolsMVELFactory) factory).getWorkingMemory().modifyAssert( handle, value, null, null );
            return 0;
        }
    }

}
