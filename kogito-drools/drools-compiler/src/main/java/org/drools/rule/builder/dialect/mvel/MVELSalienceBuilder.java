package org.drools.rule.builder.dialect.mvel;

import java.io.Serializable;
import java.util.Set;

import org.drools.base.mvel.DroolsMVELFactory;
import org.drools.base.mvel.MVELSalienceExpression;
import org.drools.compiler.Dialect;
import org.drools.compiler.DescrBuildError;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.SalienceBuilder;
import org.mvel.ExpressionCompiler;
import org.mvel.MVEL;
import org.mvel.ParserContext;
import org.mvel.integration.impl.ClassImportResolverFactory;

public class MVELSalienceBuilder
    implements
    SalienceBuilder {

    public void build(RuleBuildContext context) {
        // pushing consequence LHS into the stack for variable resolution
        context.getBuildStack().push( context.getRule().getLhs() );

        try {
            final DroolsMVELFactory factory = new DroolsMVELFactory( context.getDeclarationResolver().getDeclarations(),
                                                                     null,
                                                                     context.getPkg().getGlobals() );

            // This builder is re-usable in other dialects, so specify by name            
            MVELDialect dialect = (MVELDialect) context.getDialect( "mvel" );

            Dialect.AnalysisResult analysis = dialect.analyzeExpression( context,
                                                                         context.getRuleDescr(),
                                                                         (String) context.getRuleDescr().getSalience(),
                                                                         new Set[]{context.getDeclarationResolver().getDeclarations().keySet(), context.getPkg().getGlobals().keySet()} );

            final Serializable expr = dialect.compile( (String) context.getRuleDescr().getSalience(),
                                                       analysis,
                                                       null,
                                                       null,
                                                       context );

            MVELSalienceExpression salience = new MVELSalienceExpression( expr,
                                                                          factory );

            context.getRule().setSalience( salience );
        } catch ( final Exception e ) {
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                          context.getRuleDescr(),
                                                          null,
                                                          "Unable to build expression for 'salience' node '" + context.getRuleDescr().getSalience() + "'" ) );
        }
    }

}
