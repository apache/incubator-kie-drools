package org.drools.rule.builder.dialect.mvel;

import java.util.HashMap;
import java.util.Map;

import org.drools.base.mvel.MVELCompilationUnit;
import org.drools.base.mvel.MVELEnabledExpression;
import org.drools.compiler.DescrBuildError;
import org.drools.compiler.Dialect;
import org.drools.rule.Declaration;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.builder.EnabledBuilder;
import org.drools.rule.builder.RuleBuildContext;

public class MVELEnabledBuilder
    implements
    EnabledBuilder {

    public void build(RuleBuildContext context) {
        // pushing consequence LHS into the stack for variable resolution
        context.getBuildStack().push( context.getRule().getLhs() );

        try {
            // This builder is re-usable in other dialects, so specify by name            
            MVELDialect dialect = (MVELDialect) context.getDialect( "mvel" );

            Map<String,Class<?>> otherVars = new HashMap<String, Class<?>>();
            otherVars.put( "rule", org.drools.rule.Rule.class );

            Map<String, Declaration> declarations = context.getDeclarationResolver().getDeclarations(context.getRule());
            Map<String, Class<?>>[] available = new Map[]{ context.getDeclarationResolver().getDeclarationClasses(context.getRule()), context.getPackageBuilder().getGlobals()};
            Dialect.AnalysisResult analysis = dialect.analyzeExpression( context,
                                                                         context.getRuleDescr(),
                                                                         (String) context.getRuleDescr().getEnabled(),
                                                                         available,
                                                                         otherVars );

            Declaration[] previousDeclarations = (Declaration[]) declarations.values().toArray( new Declaration[declarations.size()] );
            
            String exprStr = (String) context.getRuleDescr().getEnabled();
            exprStr = exprStr.substring( 1, exprStr.length()-1 )+" ";
            MVELCompilationUnit unit = dialect.getMVELCompilationUnit( exprStr,
                                                                       analysis,
                                                                       previousDeclarations,
                                                                       null,
                                                                       otherVars,
                                                                       context );

            MVELEnabledExpression expr = new MVELEnabledExpression( unit,
                                                                    dialect.getId() );
            context.getRule().setEnabled( expr );

            MVELDialectRuntimeData data = (MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData( dialect.getId() );
            data.addCompileable( context.getRule(),
                                 expr );

            expr.compile( context.getPackageBuilder().getRootClassLoader() );
        } catch ( final Exception e ) {
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                          context.getRuleDescr(),
                                                          null,
                                                          "Unable to build expression for 'enabled' : " + e.getMessage() + " '" + context.getRuleDescr().getEnabled() + "'" ) );
        }
    }

}
