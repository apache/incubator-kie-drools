package org.drools.compiler.rule.builder.dialect.mvel;

import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.compiler.rule.builder.SalienceBuilder;
import org.drools.core.base.mvel.MVELCompilationUnit;
import org.drools.core.base.mvel.MVELSalienceExpression;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.definitions.rule.impl.RuleImpl.SafeSalience;
import org.drools.core.reteoo.RuleTerminalNode.SortDeclarations;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.drools.core.spi.KnowledgeHelper;
import org.kie.internal.security.KiePolicyHelper;

import java.util.Arrays;
import java.util.Map;

import static org.drools.compiler.rule.builder.dialect.DialectUtil.copyErrorLocation;

public class MVELSalienceBuilder
    implements
    SalienceBuilder {

    public void build(RuleBuildContext context) {
        boolean typesafe = context.isTypesafe();
        // pushing consequence LHS into the stack for variable resolution
        context.getBuildStack().push( ((RuleImpl)context.getRule()).getLhs() );

        try {
            // This builder is re-usable in other dialects, so specify by name            
            MVELDialect dialect = (MVELDialect) context.getDialect( "mvel" );
            
            Map<String, Declaration> decls = context.getDeclarationResolver().getDeclarations((RuleImpl)context.getRule());

            MVELAnalysisResult analysis = ( MVELAnalysisResult) dialect.analyzeExpression( context,
                                                                                           context.getRuleDescr(),
                                                                                           (String) context.getRuleDescr().getSalience(),
                                                                                           new BoundIdentifiers(context.getDeclarationResolver().getDeclarationClasses( decls ),
                                                                                                                context.getKnowledgeBuilder().getGlobals() ) );
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
                                                                       KnowledgeHelper.class,
                                                                       false );

            MVELSalienceExpression expr = new MVELSalienceExpression( unit,
                                                                      dialect.getId() );
            context.getRule().setSalience( KiePolicyHelper.isPolicyEnabled() ? new SafeSalience(expr) : expr );
            
            MVELDialectRuntimeData data = (MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData( "mvel" );
            data.addCompileable( context.getRule(),
                                 expr );
            
            expr.compile( data, context.getRule() );
        } catch ( final Exception e ) {
            copyErrorLocation(e, context.getRuleDescr());
            context.addError( new DescrBuildError( context.getParentDescr(),
                                                          context.getRuleDescr(),
                                                          null,
                                                          "Unable to build expression for 'salience' : " + e.getMessage() + "'" + context.getRuleDescr().getSalience() + "'" ) );
        } finally {
            context.setTypesafe( typesafe );
        }
    }

}
