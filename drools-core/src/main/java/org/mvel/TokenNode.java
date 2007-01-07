package org.mvel;

public class TokenNode implements Cloneable {
    public TokenNode(Token token) {
        try {
            this.token = token.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException("unable to clone node", e);
        }
    }

    public Token token;
    public TokenNode next;
}
