package org.drools.rule.builder.dialect.mvel;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.drools.base.mvel.DroolsMVELFactory;
import org.drools.base.mvel.MVELAction;
import org.drools.base.mvel.MVELConsequence;
import org.drools.base.mvel.MVELReturnValueEvaluator;
import org.drools.compiler.Dialect;
import org.drools.compiler.DescrBuildError;
import org.drools.compiler.ReturnValueDescr;
import org.drools.lang.descr.ActionDescr;
import org.drools.rule.builder.ActionBuilder;
import org.drools.rule.builder.ConsequenceBuilder;
import org.drools.rule.builder.PackageBuildContext;
import org.drools.rule.builder.ReturnValueEvaluatorBuilder;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.spi.ReturnValueEvaluator;
import org.drools.workflow.core.node.ActionNode;
import org.drools.workflow.instance.impl.ReturnValueConstraintEvaluator;
import org.mvel.Macro;
import org.mvel.MacroProcessor;

public class MVELReturnValueEvaluatorBuilder
    implements
    ReturnValueEvaluatorBuilder {

    public MVELReturnValueEvaluatorBuilder() {

    }

    public void build(final PackageBuildContext context,
                      final ReturnValueConstraintEvaluator constraintNode,
                      final ReturnValueDescr descr) {

        String text = descr.getText();

        try {
            MVELDialect dialect = (MVELDialect) context.getDialect( "mvel" );

            Dialect.AnalysisResult analysis = dialect.analyzeBlock( context,
                                                                    descr,
                                                                    dialect.getInterceptors(),
                                                                    text,
                                                                    new Set[]{Collections.EMPTY_SET, context.getPkg().getGlobals().keySet()},
                                                                    null );

            final Serializable expr = dialect.compile( text,
                                                       analysis,
                                                       dialect.getInterceptors(),
                                                       null,
                                                       null,
                                                       context );

            final DroolsMVELFactory factory = new DroolsMVELFactory( null,
                                                                     null,
                                                                     context.getPkg().getGlobals(),
                                                                     analysis.getBoundIdentifiers() );
            
            constraintNode.setEvaluator( new MVELReturnValueEvaluator( expr, factory ) );
        } catch ( final Exception e ) {
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                          descr,
                                                          null,
                                                          "Unable to build expression for 'returnValuEvaluator' '" + descr.getText() + "'" ) );
        }
    }

    /**
     * Allows newlines to demarcate expressions, as per MVEL command line.
     * If expression spans multiple lines (ie inside an unbalanced bracket) then
     * it is left alone.
     * Uses character based iteration which is at least an order of magnitude faster then a single
     * simple regex.
     */
    public static String delimitExpressions(String s) {

        StringBuffer result = new StringBuffer();
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
