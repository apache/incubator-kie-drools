package org.drools.rule.builder.dialect.mvel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.drools.base.mvel.DroolsMVELFactory;
import org.drools.base.mvel.MVELConsequence;
import org.drools.compiler.DescrBuildError;
import org.drools.compiler.Dialect;
import org.drools.rule.builder.ConsequenceBuilder;
import org.drools.rule.builder.RuleBuildContext;
import org.mvel.Macro;
import org.mvel.MacroProcessor;

public class MVELConsequenceBuilder
    implements
    ConsequenceBuilder {

    //private final Interceptor assertInterceptor;
    //private final Interceptor modifyInterceptor;

    private static final Map macros = new HashMap( 5 );
    static {
        macros.put( "insert",
                    new Macro() {
                        public String doMacro() {
                            return "drools.insert";
                        }
                    } );

        macros.put( "insertLogical",
                    new Macro() {
                        public String doMacro() {
                            return "drools.insertLogical";
                        }
                    } );

        macros.put( "modify",
                    new Macro() {
                        public String doMacro() {
                            return "@Modify with";
                        }
                    } );

        macros.put( "update",
                    new Macro() {
                        public String doMacro() {
                            return "drools.update";
                        }
                    } );

        macros.put( "retract",
                    new Macro() {
                        public String doMacro() {
                            return "drools.retract";
                        }
                    } );;
    }

    public MVELConsequenceBuilder() {

    }

    public void build(final RuleBuildContext context) {
        // pushing consequence LHS into the stack for variable resolution
        context.getBuildStack().push( context.getRule().getLhs() );

        try {
            MVELDialect dialect = (MVELDialect) context.getDialect( context.getDialect().getId() );

            String text = processMacros( (String) context.getRuleDescr().getConsequence() );

            Dialect.AnalysisResult analysis = dialect.analyzeBlock( context,
                                                                    context.getRuleDescr(),
                                                                    dialect.getInterceptors(),
                                                                    text,
                                                                    new Set[]{context.getDeclarationResolver().getDeclarations().keySet(), context.getPkg().getGlobals().keySet()},
                                                                    null );

            final Serializable expr = dialect.compile( text,
                                                       analysis,
                                                       dialect.getInterceptors(),
                                                       null,
                                                       null,
                                                       context );

            final DroolsMVELFactory factory = new DroolsMVELFactory( context.getDeclarationResolver().getDeclarations(),
                                                                     null,
                                                                     context.getPkg().getGlobals(),
                                                                     analysis.getBoundIdentifiers() );
            

            context.getRule().setConsequence( new MVELConsequence( expr,
                                                                   factory,
                                                                   dialect.getId() ) );
        } catch ( final Exception e ) {
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                    context.getRuleDescr(),
                                                    null,
                                                    "Unable to build expression for 'consequence': "+e.getMessage()+" '" + context.getRuleDescr().getConsequence() + "'" ) );
        }
    }

    public static String processMacros(String consequence) {
        MacroProcessor macroProcessor = new MacroProcessor();
        macroProcessor.setMacros( macros );
        return macroProcessor.parse( delimitExpressions( consequence ) );
    }

    /**
     * Allows newlines to demarcate expressions, as per MVEL command line.
     * If expression spans multiple lines (ie inside an unbalanced bracket) then
     * it is left alone.
     * Uses character based iteration which is at least an order of magnitude faster then a single
     * simple regex.
     */
    public static String delimitExpressions(String s) {

        StringBuffer result = new StringBuffer();
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

}
