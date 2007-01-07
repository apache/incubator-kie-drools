package org.mvel;

public interface TokenIterator extends Cloneable {
    public void reset();
    public Token nextToken();
    public Token peekToken();
    public Token peekLast();
    public void back();
    public Token tokensBack(int offset);
    public boolean hasMoreTokens();
    public String showTokenChain();
    @SuppressWarnings({"CloneDoesntDeclareCloneNotSupportedException"})
    public TokenIterator clone();
}
