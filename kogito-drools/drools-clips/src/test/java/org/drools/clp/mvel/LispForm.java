/**
 * 
 */
package org.drools.clp.mvel;

public class LispForm
 implements SExpression {
    private SExpression[] sExpressions;
    
    public LispForm(SExpression[] sExpressions) {
        this.sExpressions = sExpressions;
    }

    public SExpression[] getSExpressions() {
        return sExpressions;
    }

    public void setSExpressions(SExpression[] sExpressions) {
        this.sExpressions = sExpressions;
    }
    
//    public String toString() {
//        StringBuilder builder = new StringBuilder();
//        
//        builder.append("(");
//        for ( int i = 0, length = sExpressions.length; i < length; i++) {
//            builder.append(" " + sExpressions[i] + " ");
//        }
//        builder.append(")");
//        return builder.toString();
//    }    
    public int size() {
        return this.sExpressions.length;
    }
    public String toString() {
        StringBuilder builder = new StringBuilder();
        
        builder.append("(");
        for ( SExpression sExpression : sExpressions) {
            builder.append(" " + sExpression + " ");
        }
        builder.append(")");
        return builder.toString();
    }  
    
}