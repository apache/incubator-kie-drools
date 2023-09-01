package org.drools.drl.ast.descr;

/**
 * An enum for connective types
 */
public enum ConnectiveType {
    
    AND("&&", 2),
    OR("||", 1),
    XOR("^", 4),
    INC_OR("|", 3),
    INC_AND("&", 5);
    
    private String connective;
    // higher precedence connectives are executed before lower precedence
    private int    precedence;
    
    ConnectiveType( String connective, int precedence ) {
        this.connective = connective;
        this.precedence = precedence;
    }
    
    public String getConnective() {
        return this.connective;
    }
    
    public int getPrecedence() {
        return this.precedence;
    }
    
    public String toString() {
        return this.connective;
    }
}
