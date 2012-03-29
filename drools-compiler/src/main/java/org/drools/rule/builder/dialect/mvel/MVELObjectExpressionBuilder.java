package org.drools.rule.builder.dialect.mvel;

import org.drools.base.mvel.MVELCompilationUnit;
import org.drools.base.mvel.MVELObjectExpression;
import org.drools.compiler.BoundIdentifiers;
import org.drools.compiler.DescrBuildError;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.rule.Declaration;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.spi.KnowledgeHelper;

import java.util.Arrays;
import java.util.Map;

import static org.drools.rule.builder.dialect.DialectUtil.copyErrorLocation;

public class MVELObjectExpressionBuilder {

    public MVELObjectExpression build( String expression, RuleBuildContext context ) {
        boolean typesafe = context.isTypesafe();
        // pushing consequence LHS into the stack for variable resolution
        context.getBuildStack().push( context.getRule().getLhs() );

        try {
            // This builder is re-usable in other dialects, so specify by name
            MVELDialect dialect = (MVELDialect) context.getDialect( "mvel" );

            Map<String, Declaration> decls = context.getDeclarationResolver().getDeclarations(context.getRule());

            MVELAnalysisResult analysis = ( MVELAnalysisResult) dialect.analyzeExpression( context,
                                                                                           context.getRuleDescr(),
                                                                                           expression,
                                                                                           new BoundIdentifiers(context.getDeclarationResolver().getDeclarationClasses( decls ),
                                                                                                                context.getPackageBuilder().getGlobals() ) );
            context.setTypesafe( analysis.isTypesafe() );
            final BoundIdentifiers usedIdentifiers = analysis.getBoundIdentifiers();
            int i = usedIdentifiers.getDeclrClasses().keySet().size();
            Declaration[] previousDeclarations = new Declaration[i];
            i = 0;
            for ( String id :  usedIdentifiers.getDeclrClasses().keySet() ) {
                previousDeclarations[i++] = decls.get( id );
            }
            Arrays.sort(previousDeclarations, RuleTerminalNode.SortDeclarations.instance);

            MVELCompilationUnit unit = dialect.getMVELCompilationUnit( expression,
                                                                       analysis,
                                                                       previousDeclarations,
                                                                       null,
                                                                       null,
                                                                       context,
                                                                       "drools",
                                                                       KnowledgeHelper.class );

            MVELObjectExpression expr = new MVELObjectExpression( unit,
                                                                      dialect.getId() );

            MVELDialectRuntimeData data = (MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData( "mvel" );
            data.addCompileable( context.getRule(),
                                 expr );

            expr.compile( data );
            return expr;
        } catch ( final Exception e ) {
            copyErrorLocation(e, context.getRuleDescr());
            context.addError( new DescrBuildError( context.getParentDescr(),
                                                          context.getRuleDescr(),
                                                          null,
                                                          "Unable to build expression : " + e.getMessage() + "'" + expression + "'" ) );
            return null;
        } finally {
            context.setTypesafe( typesafe );
        }
    }

}
