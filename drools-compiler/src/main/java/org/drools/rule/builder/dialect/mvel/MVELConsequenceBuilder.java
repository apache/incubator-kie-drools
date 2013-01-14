package org.drools.rule.builder.dialect.mvel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.drools.base.mvel.MVELCompilationUnit;
import org.drools.base.mvel.MVELConsequence;
import org.drools.compiler.AnalysisResult;
import org.drools.compiler.BoundIdentifiers;
import org.drools.compiler.DescrBuildError;
import org.drools.lang.descr.RuleDescr;
import org.drools.reteoo.RuleTerminalNode.SortDeclarations;
import org.drools.rule.Declaration;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.Rule;
import org.drools.rule.builder.ConsequenceBuilder;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.spi.DeclarationScopeResolver;
import org.drools.spi.KnowledgeHelper;
import org.mvel2.Macro;
import org.mvel2.MacroProcessor;

import static org.drools.rule.builder.dialect.DialectUtil.copyErrorLocation;

public class MVELConsequenceBuilder
    implements
    ConsequenceBuilder {

    public static final Map<String, Macro> macros = new HashMap<String,Macro>( 10 );
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

        macros.put( "don",
                    new Macro() {
                        public String doMacro() {
                            return "drools.don";
                        }
                    } );

        macros.put( "shed",
                    new Macro() {
                        public String doMacro() {
                            return "drools.shed";
                        }
                    } );

        macros.put( "ward",
                    new Macro() {
                        public String doMacro() {
                            return "drools.ward";
                        }
                    } );

        macros.put( "grant",
                    new Macro() {
                        public String doMacro() {
                            return "drools.grant";
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
            
            String text = ( Rule.DEFAULT_CONSEQUENCE_NAME.equals(consequenceName) ) ?
                    (String) ruleDescr.getConsequence() :
                    (String) ruleDescr.getNamedConsequences().get( consequenceName );

            text = processMacros( text );
            
            Map<String, Declaration> decls = context.getDeclarationResolver().getDeclarations(context.getRule());
            
            AnalysisResult analysis = dialect.analyzeBlock( context,
                                                            context.getRuleDescr(),
                                                            dialect.getInterceptors(),
                                                            text,
                                                            new BoundIdentifiers(DeclarationScopeResolver.getDeclarationClasses(decls),
                                                                                 context.getPackageBuilder().getGlobals(),
                                                                                 null,
                                                                                 KnowledgeHelper.class),
                                                            null,
                                                            "drools",
                                                            KnowledgeHelper.class );
            
            if ( analysis == null ) {
                // something bad happened, issue already logged in errors
                return;
            }
            
            final BoundIdentifiers usedIdentifiers = analysis.getBoundIdentifiers();
            
            final Declaration[] declarations =  new Declaration[usedIdentifiers.getDeclrClasses().size()];
            String[] declrStr = new String[declarations.length];
            int j = 0;
            for (String str : usedIdentifiers.getDeclrClasses().keySet() ) {
                declrStr[j] = str;
                declarations[j++] = decls.get( str );
            }
            Arrays.sort( declarations, SortDeclarations.instance  );
            for ( int i = 0; i < declrStr.length; i++) {
                declrStr[i] = declarations[i].getIdentifier();
            }
            context.getRule().setRequiredDeclarationsForConsequence(consequenceName, declrStr);
            MVELCompilationUnit unit = dialect.getMVELCompilationUnit( text,
                                                                       analysis,
                                                                       declarations,
                                                                       null,
                                                                       null,
                                                                       context,
                                                                       "drools",
                                                                       KnowledgeHelper.class,
                                                                       false );

            MVELConsequence expr = new MVELConsequence( unit,
                                                        dialect.getId() );
            
            if ( Rule.DEFAULT_CONSEQUENCE_NAME.equals( consequenceName ) ) {
                context.getRule().setConsequence( expr );
            } else {
                context.getRule().addNamedConsequence(consequenceName, expr);
            }
            
            MVELDialectRuntimeData data = (MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData( "mvel" );
            data.addCompileable( context.getRule(),
                                 expr );
            
            expr.compile( data );
        } catch ( final Exception e ) {
            copyErrorLocation(e, context.getRuleDescr());
            context.addError(new DescrBuildError(context.getParentDescr(),
                    context.getRuleDescr(),
                    null,
                    "Unable to build expression for 'consequence': " + e.getMessage() + " '" + context.getRuleDescr().getConsequence() + "'"));
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
        boolean inString = false;
        char lastNonWhite = ';';
        for ( int i = 0; i < cs.length; i++ ) {
            char c = cs[i];
            switch ( c ) {
                case '\"' :
                    if ( i == 0 || cs[i-1] != '\\' ) {
                        inString = !inString;
                    }
                    break;
                case '/' :
                    if( i < cs.length-1 && cs[i+1] == '*' && !inString ) {
                        // multi-line comment
                        int start = i;
                        i+=2; // skip the /*
                        for( ; i < cs.length; i++ ) {
                            if( cs[i] == '*' && i < cs.length-1 && cs[i+1] == '/' ) {
                                i++; // skip the */
                                break;
                            } else if( cs[i] == '\n' || cs[i] == '\r' ) {
                                lastNonWhite = checkAndAddSemiColon( result,
                                                                     brace,
                                                                     sqre,
                                                                     crly,
                                                                     lastNonWhite );
                            }
                        }
                        result.append( cs, start, i-start );
                        break;
                    } else if( i < cs.length-1 && cs[i+1] != '/' ) {
                        // not a line comment
                        break;
                    }
                    // otherwise handle it in the same way as #
                case '#' :
                    // line comment
                    lastNonWhite = checkAndAddSemiColon( result,
                                                         brace,
                                                         sqre,
                                                         crly,
                                                         lastNonWhite );
                    i = processLineComment( cs,
                                            i,
                                            result);
                    continue;
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
            if ( brace == 0 && sqre == 0 && crly == 0 && ( c == '\n' || c == '\r' ) ){
                // line break 
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

    private static int processLineComment(char[] cs,
                                          int i,
                                          StringBuilder result) {
        for( ; i < cs.length; i++ ) {
            result.append( cs[i] );
            if( cs[i] == '\n' || cs[i] == '\r' ) {
                break;
            }
        }
        return i;
    }

    private static char checkAndAddSemiColon(StringBuilder result,
                                             int brace,
                                             int sqre,
                                             int crly,
                                             char lastNonWhite) {
        if ( brace == 0 && sqre == 0 && crly == 0 ){
            if ( lastNonWhite != ';' ) {
                result.append( ';' );
                lastNonWhite = ';';
            }
        }
        return lastNonWhite;
    }

}
