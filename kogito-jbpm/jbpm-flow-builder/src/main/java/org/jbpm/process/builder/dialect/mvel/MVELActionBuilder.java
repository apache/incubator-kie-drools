package org.jbpm.process.builder.dialect.mvel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.drools.base.mvel.MVELCompilationUnit;
import org.drools.compiler.BoundIdentifiers;
import org.drools.compiler.DescrBuildError;
import org.drools.lang.descr.ActionDescr;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.builder.PackageBuildContext;
import org.drools.rule.builder.dialect.mvel.MVELAnalysisResult;
import org.drools.rule.builder.dialect.mvel.MVELDialect;
import org.jbpm.process.builder.ActionBuilder;
import org.jbpm.process.core.ContextResolver;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.impl.MVELAction;
import org.jbpm.workflow.core.DroolsAction;
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
                            return "kcontext.getKnowledgeRuntime().insert";
                        }
                    } );

//        macros.put( "insertLogical",
//                    new Macro() {
//                        public String doMacro() {
//                            return "kcontext.getKnowledgeRuntime()..insertLogical";
//                        }
//                    } );


//        macros.put( "update",
//                    new Macro() {
//                        public String doMacro() {
//                            return "kcontext.getKnowledgeRuntime().update";
//                        }
//                    } );

//        macros.put( "retract",
//                    new Macro() {
//                        public String doMacro() {
//                            return "kcontext.getKnowledgeRuntime().retract";
//                        }
//                    } );;
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

            boolean typeSafe = context.isTypesafe();
            
            Map<String, Class<?>> variables = new HashMap<String,Class<?>>();
            
            context.setTypesafe( false ); // we can't know all the types ahead of time with processes, but we don't need return types, so it's ok
            BoundIdentifiers boundIdentifiers = new BoundIdentifiers(variables, context.getPackageBuilder().getGlobals());
            MVELAnalysisResult analysis = ( MVELAnalysisResult ) dialect.analyzeBlock( context,
                                                                                       actionDescr,
                                                                                       dialect.getInterceptors(),
                                                                                       text,
                                                                                       boundIdentifiers,
                                                                                       null,
                                                                                       "context",
                                                                                       org.kie.runtime.process.ProcessContext.class );                       
            context.setTypesafe( typeSafe );
            
            Set<String> variableNames = analysis.getNotBoundedIdentifiers();
            if (contextResolver != null) {
	            for (String variableName: variableNames) {
	                if ( analysis.getMvelVariables().keySet().contains( variableName ) ||  variableName.equals( "kcontext" ) || variableName.equals( "context" ) ) {
	                    continue;
	                }
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
	            		              context.getDialect().getTypeResolver().resolveType(variableScope.findVariable(variableName).getType().getStringType()));
	            	}
	            }
            }

            MVELCompilationUnit unit = dialect.getMVELCompilationUnit( text,
                                                                       analysis,
                                                                       null,
                                                                       null,
                                                                       variables,
                                                                       context,
                                                                       "context",
                                                                       org.kie.runtime.process.ProcessContext.class);              
            MVELAction expr = new MVELAction( unit, context.getDialect().getId() );
            
            
            action.setMetaData("Action",  expr );
            
            MVELDialectRuntimeData data = (MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData( dialect.getId() );            
            data.addCompileable( action,
                                  expr );  
            
            expr.compile( data );
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
