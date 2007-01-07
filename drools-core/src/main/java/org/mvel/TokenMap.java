package org.mvel;

public class TokenMap implements TokenIterator {
    private TokenNode firstToken;
    private TokenNode current;

    public TokenMap(TokenNode firstToken) {
        this.current = this.firstToken = firstToken;
    }

    public void addTokenNode(Token token) {
        if (this.current == null) {
            this.firstToken = this.current = new TokenNode(token);
        }
        else {
           this.current = this.current.next = new TokenNode(token);
        }
    }

    public void reset() {
        this.current = firstToken;
    }

    public boolean hasMoreTokens() {
        return this.current != null;
    }

    public Token nextToken() {
        if (current == null) return null;

        Token tk = current.token;
        current = current.next;
        return tk;
    }


    public Token peekToken() {
        if (current == null) return null;
        return current.token;
    }

    public void removeToken() {
        if (current != null) {
            current = current.next;
        }
    }

    public Token peekLast() {
        throw new RuntimeException("unimplemented");
    }

    public Token tokensBack(int offset) {
        throw new RuntimeException("unimplemented");
    }


    public void back() {
        throw new RuntimeException("unimplemented");
    }

    public String showTokenChain() {
        throw new RuntimeException("unimplemented");        
    }


    @SuppressWarnings({"CloneDoesntDeclareCloneNotSupportedException"})
    public TokenIterator clone() {
        return null;
    }
}
