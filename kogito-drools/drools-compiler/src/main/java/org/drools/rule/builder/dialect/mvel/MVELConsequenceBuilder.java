package org.drools.rule.builder.dialect.mvel;

import java.util.HashMap;
import java.util.Map;

import org.drools.base.mvel.MVELCompilationUnit;
import org.drools.base.mvel.MVELConsequence;
import org.drools.compiler.DescrBuildError;
import org.drools.compiler.Dialect;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.Declaration;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.builder.ConsequenceBuilder;
import org.drools.rule.builder.RuleBuildContext;
import org.mvel2.Macro;
import org.mvel2.MacroProcessor;

public class MVELConsequenceBuilder
    implements
    ConsequenceBuilder {

    private static final Map<String, Macro> macros = new HashMap<String,Macro>( 10 );
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
                    } );
        macros.put( "entryPoints",
                    new Macro() {
                        public String doMacro() {
                            return "drools.entryPoints";
                        }
                    } );
        macros.put( "exitPoints",
                    new Macro() {
                        public String doMacro() {
                            return "drools.exitPoints";
                        }
                    } );
    }

    public MVELConsequenceBuilder() {

    }

    public void build(final RuleBuildContext context, String consequenceName) {
    	 
        // pushing consequence LHS into the stack for variable resolution
        context.getBuildStack().push( context.getRule().getLhs() );

        try {
            MVELDialect dialect = (MVELDialect) context.getDialect( context.getDialect().getId() );
            
            final RuleDescr ruleDescr = context.getRuleDescr();
            
            String text = ( "default".equals( consequenceName ) ) ? (String) ruleDescr.getConsequence() : (String) ruleDescr.getNamedConsequences().get( consequenceName );

            text = processMacros( text );

            Dialect.AnalysisResult analysis = dialect.analyzeBlock( context,
                                                                    context.getRuleDescr(),
                                                                    dialect.getInterceptors(),
                                                                    text,
                                                                    new Map[]{context.getDeclarationResolver().getDeclarationClasses(context.getRule()), context.getPackageBuilder().getGlobals()},
                                                                    null );
            
            Declaration[] previousDeclarations = (Declaration[]) context.getDeclarationResolver().getDeclarations(context.getRule()).values().toArray( new Declaration[context.getDeclarationResolver().getDeclarations(context.getRule()).size()] );
            MVELCompilationUnit unit = dialect.getMVELCompilationUnit( text,
                                                                       analysis,
                                                                       previousDeclarations,
                                                                       null,
                                                                       null,
                                                                       context );

            MVELConsequence expr = new MVELConsequence( unit,
                                                        dialect.getId() );
            
            if ( "default".equals( consequenceName ) ) {
            	context.getRule().setConsequence( expr );
            } else {
            	context.getRule().getNamedConsequences().put(consequenceName, expr);
            }
            
            MVELDialectRuntimeData data = (MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData( context.getDialect().getId() );            
            data.addCompileable( context.getRule(),
                                  expr );
            
            expr.compile( context.getPackageBuilder().getRootClassLoader() );
        } catch ( final Exception e ) {
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                          context.getRuleDescr(),
                                                          null,
                                                          "Unable to build expression for 'consequence': " + e.getMessage() + " '" + context.getRuleDescr().getConsequence() + "'" ) );
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

}
