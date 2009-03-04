package org.drools.rule.builder.dialect.mvel;

import java.util.Map;

import org.drools.base.mvel.MVELCompilationUnit;
import org.drools.base.mvel.MVELSalienceExpression;
import org.drools.compiler.DescrBuildError;
import org.drools.compiler.Dialect;
import org.drools.rule.Declaration;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.SalienceBuilder;

public class MVELSalienceBuilder
    implements
    SalienceBuilder {

    public void build(RuleBuildContext context) {
        // pushing consequence LHS into the stack for variable resolution
        context.getBuildStack().push( context.getRule().getLhs() );

        try {        
            // This builder is re-usable in other dialects, so specify by name            
            MVELDialect dialect = (MVELDialect) context.getDialect( "mvel" );

            Dialect.AnalysisResult analysis = dialect.analyzeExpression( context,
                                                                         context.getRuleDescr(),
                                                                         (String) context.getRuleDescr().getSalience(),
                                                                         new Map[]{context.getDeclarationResolver().getDeclarationClasses(context.getRule()), context.getPackageBuilder().getGlobals()} );

            Declaration[] previousDeclarations = (Declaration[]) context.getDeclarationResolver().getDeclarations(context.getRule()).values().toArray( new Declaration[context.getDeclarationResolver().getDeclarations(context.getRule()).size()] );
            MVELCompilationUnit unit = dialect.getMVELCompilationUnit( (String) context.getRuleDescr().getSalience(),
                                                                       analysis,
                                                                       previousDeclarations,
                                                                       null,
                                                                       null,
                                                                       context );

            MVELSalienceExpression expr = new MVELSalienceExpression( unit,
                                                                          dialect.getId() );
            context.getRule().setSalience( expr );
            
            MVELDialectRuntimeData data = (MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData(dialect.getId() );
            data.addCompileable( context.getRule(),
                                 expr );          
            
            expr.compile( context.getPackageBuilder().getRootClassLoader() );
        } catch ( final Exception e ) {
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                          context.getRuleDescr(),
                                                          null,
                                                          "Unable to build expression for 'salience' : " + e.getMessage() + "'" + context.getRuleDescr().getSalience() + "'" ) );
        }
    }

}
