package org.drools.rule.builder.dialect.mvel;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.drools.base.mvel.DroolsMVELFactory;
import org.drools.base.mvel.MVELAction;
import org.drools.base.mvel.MVELConsequence;
import org.drools.compiler.Dialect;
import org.drools.compiler.DescrBuildError;
import org.drools.lang.descr.ActionDescr;
import org.drools.rule.builder.ActionBuilder;
import org.drools.rule.builder.ConsequenceBuilder;
import org.drools.rule.builder.PackageBuildContext;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.ruleflow.core.impl.ActionNodeImpl;
import org.mvel.Macro;
import org.mvel.MacroProcessor;

public class MVELActionBuilder
    implements
    ActionBuilder {

    public MVELActionBuilder() {

    }

    public void build(final PackageBuildContext context,
                      final ActionNodeImpl actionNode,
                      final ActionDescr actionDescr) {

        String text = actionDescr.getText();

        try {
            MVELDialect dialect = (MVELDialect) context.getDialect();

            Dialect.AnalysisResult analysis = dialect.analyzeBlock( context,
                                                                    actionDescr,
                                                                    dialect.getInterceptors(),
                                                                    text,
                                                                    new Set[]{Collections.EMPTY_SET, context.getPkg().getGlobals().keySet()},
                                                                    null );

            final Serializable expr = dialect.compile( text,
                                                       analysis,
                                                       dialect.getInterceptors(),
                                                       null,
                                                       context );

            final DroolsMVELFactory factory = new DroolsMVELFactory( null,
                                                                     null,
                                                                     context.getPkg().getGlobals(),
                                                                     analysis.getBoundIdentifiers() );
            
            actionNode.setAction( new MVELAction( expr, factory )  );
        } catch ( final Exception e ) {
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                          actionDescr,
                                                          null,
                                                          "Unable to build expression for 'action' '" + actionDescr.getText() + "'" ) );
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
