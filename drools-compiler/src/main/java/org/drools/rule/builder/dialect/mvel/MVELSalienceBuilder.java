package org.drools.rule.builder.dialect.mvel;

import java.io.Serializable;

import org.drools.base.mvel.DroolsMVELFactory;
import org.drools.base.mvel.MVELConsequence;
import org.drools.base.mvel.MVELSalienceExpression;
import org.drools.compiler.RuleError;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.SalienceBuilder;
import org.mvel.MVEL;
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
            final ClassImportResolverFactory classImportResolverFactory = ((MVELDialect) context.getDialect( "mvel" )).getClassImportResolverFactory();
            factory.setNextFactory( classImportResolverFactory );

            final Serializable expr = MVEL.compileExpression( (String) context.getRuleDescr().getSalience(),
                                                              classImportResolverFactory.getImportedClasses() );

            MVELSalienceExpression salience = new MVELSalienceExpression( expr,
                                                                          factory );

            context.getRule().setSalience( salience );
        } catch ( final Exception e ) {
            context.getErrors().add( new RuleError( context.getRule(),
                                                    context.getRuleDescr(),
                                                    null,
                                                    "Unable to build expression for 'salience' node '" + context.getRuleDescr().getSalience() + "'" ) );
        }
    }

}
