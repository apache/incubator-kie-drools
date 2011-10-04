package org.drools.rule.builder.dialect.mvel;

import java.util.Arrays;
import java.util.Map;

import org.drools.base.mvel.MVELCompilationUnit;
import org.drools.base.mvel.MVELSalienceExpression;
import org.drools.compiler.AnalysisResult;
import org.drools.compiler.BoundIdentifiers;
import org.drools.compiler.DescrBuildError;
import org.drools.reteoo.RuleTerminalNode.SortDeclarations;
import org.drools.rule.Declaration;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.SalienceBuilder;
import org.drools.spi.KnowledgeHelper;

import static org.drools.rule.builder.dialect.DialectUtil.copyErrorLocation;

public class MVELSalienceBuilder
    implements
    SalienceBuilder {

    public void build(RuleBuildContext context) {
        boolean typesafe = context.isTypesafe();
        // pushing consequence LHS into the stack for variable resolution
        context.getBuildStack().push( context.getRule().getLhs() );

        try {
            // This builder is re-usable in other dialects, so specify by name            
            MVELDialect dialect = (MVELDialect) context.getDialect( "mvel" );
            
            Map<String, Declaration> decls = context.getDeclarationResolver().getDeclarations(context.getRule());

            MVELAnalysisResult analysis = ( MVELAnalysisResult) dialect.analyzeExpression( context,
                                                                                           context.getRuleDescr(),
                                                                                           (String) context.getRuleDescr().getSalience(),
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
            Arrays.sort( previousDeclarations, SortDeclarations.instance  ); 
            
            MVELCompilationUnit unit = dialect.getMVELCompilationUnit( (String) context.getRuleDescr().getSalience(),
                                                                       analysis,
                                                                       previousDeclarations,
                                                                       null,
                                                                       null,
                                                                       context,
                                                                       "drools",
                                                                       KnowledgeHelper.class );

            MVELSalienceExpression expr = new MVELSalienceExpression( unit,
                                                                      dialect.getId() );
            context.getRule().setSalience( expr );
            
            MVELDialectRuntimeData data = (MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData( "mvel" );
            data.addCompileable( context.getRule(),
                                 expr );
            
            expr.compile( data );
        } catch ( final Exception e ) {
            copyErrorLocation(e, context.getRuleDescr());
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                          context.getRuleDescr(),
                                                          null,
                                                          "Unable to build expression for 'salience' : " + e.getMessage() + "'" + context.getRuleDescr().getSalience() + "'" ) );
        } finally {
            context.setTypesafe( typesafe );
        }
    }

}
