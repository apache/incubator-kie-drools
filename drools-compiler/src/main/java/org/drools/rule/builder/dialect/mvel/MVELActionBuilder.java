package org.drools.rule.builder.dialect.mvel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.base.mvel.MVELAction;
import org.drools.base.mvel.MVELCompilationUnit;
import org.drools.compiler.DescrBuildError;
import org.drools.compiler.Dialect;
import org.drools.lang.descr.ActionDescr;
import org.drools.process.core.ContextResolver;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.builder.ActionBuilder;
import org.drools.rule.builder.PackageBuildContext;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.ProcessContext;
import org.drools.workflow.core.DroolsAction;
import org.mvel2.Macro;
import org.mvel2.MacroProcessor;

public class MVELActionBuilder
    implements
    ActionBuilder {

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
    
    public MVELActionBuilder() {

    }

    public void build(final PackageBuildContext context,
                      final DroolsAction action,
                      final ActionDescr actionDescr,
                      final ContextResolver contextResolver) {

        String text = processMacros( actionDescr.getText() );

        try {
            MVELDialect dialect = (MVELDialect) context.getDialect( "mvel" );

            Map<String, Class<?>> variables = new HashMap<String,Class<?>>();
            variables.put("context", ProcessContext.class);
            variables.put("kcontext", org.drools.runtime.process.ProcessContext.class);
            variables.put("drools", KnowledgeHelper.class);
            Dialect.AnalysisResult analysis = dialect.analyzeBlock( context,
                                                                    actionDescr,
                                                                    dialect.getInterceptors(),
                                                                    text,
                                                                    new Map[]{variables, context.getPackageBuilder().getGlobals()},
                                                                    null );                       


            List<String> variableNames = analysis.getNotBoundedIdentifiers();
            if (contextResolver != null) {
	            for (String variableName: variableNames) {
	            	VariableScope variableScope = (VariableScope) contextResolver.resolveContext(VariableScope.VARIABLE_SCOPE, variableName);
	            	if (variableScope == null) {
	            		context.getErrors().add(
	        				new DescrBuildError(
	    						context.getParentDescr(),
	                            actionDescr,
	                            null,
	                            "Could not find variable '" + variableName + "' for action '" + actionDescr.getText() + "'" ) );            		
	            	} else {
	            		variables.put(variableName,
            				context.getDialect().getTypeResolver().resolveType(
        						variableScope.findVariable(variableName).getType().getStringType()));
	            	}
	            }
            }

            MVELCompilationUnit unit = dialect.getMVELCompilationUnit( text,
                                                                       analysis,
                                                                       null,
                                                                       null,
                                                                       variables,
                                                                       context );              
            MVELAction expr = new MVELAction( unit, context.getDialect().getId() );
            expr.setVariableNames(variableNames);
            
            
            action.setMetaData("Action",  expr );
            
            MVELDialectRuntimeData data = (MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData( dialect.getId() );            
            data.addCompileable( action,
                                  expr );  
            
            expr.compile( context.getPackageBuilder().getRootClassLoader() );
        } catch ( final Exception e ) {
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                          actionDescr,
                                                          null,
                                                          "Unable to build expression for action '" + actionDescr.getText() + "' :" + e ) );
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
