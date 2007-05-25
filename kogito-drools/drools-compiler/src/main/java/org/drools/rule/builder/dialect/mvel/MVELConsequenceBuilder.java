package org.drools.rule.builder.dialect.mvel;

import java.io.Serializable;

import org.drools.base.mvel.DroolsMVELFactory;
import org.drools.base.mvel.MVELConsequence;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.builder.ConsequenceBuilder;
import org.drools.rule.builder.RuleBuildContext;
import org.mvel.MVEL;

public class MVELConsequenceBuilder
    implements
    ConsequenceBuilder {

    public void build(final RuleBuildContext context) {
        // pushing consequence LHS into the stack for variable resolution
        context.getBuildStack().push( context.getRule().getLhs() );

        final DroolsMVELFactory factory = new DroolsMVELFactory(context.getDeclarationResolver().getDeclarations(), null,  context.getPkg().getGlobals() );

        final Serializable expr = MVEL.compileExpression( delimitExpressions( (String) context.getRuleDescr().getConsequence()) );
        //final Serializable expr = MVEL.compileExpression( (String) ruleDescr.getConsequence() );

        context.getRule().setConsequence( new MVELConsequence( expr,
                                                               factory ) );
    }
    
    /**
     * Allows newlines to demarcate expressions, as per MVEL command line.
     * If expression spans multiple lines (ie inside an unbalanced bracket) then
     * it is left alone.
     * Uses character based iteration which is at least an order of magnitude faster then a single 
     * simple regex.
     */
    public String delimitExpressions(String s) {

        StringBuffer result = new StringBuffer();
        char[] cs = s.toCharArray();
        int brace = 0;
        int sqre = 0;
        int crly = 0;
        char lastNonWhite = ' ';
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
            if ((brace == 0 && sqre == 0 && crly == 0) &&
                    (c == '\n' || c == '\r')) {
                if (lastNonWhite != ';') {
                    result.append( ';' );
                    lastNonWhite = ';';
                }
            } else if  (!Character.isWhitespace( c )) {
                lastNonWhite = c;
            }
            result.append( c );
            

        }
        return result.toString();
    }

}
