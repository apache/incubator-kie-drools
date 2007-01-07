package org.mvel;

import java.util.ArrayList;

public class FastTokenIterator implements TokenIterator, Cloneable {
    private Token[] token;
    private int length = 0;
    private int cursor = 0;

    public FastTokenIterator(TokenIterator map) {
        ArrayList<Token> tokens = new ArrayList<Token>();
        map.reset();
        while (map.hasMoreTokens()) {
            tokens.add(map.nextToken());
        }

        token = tokens.toArray(new Token[length = tokens.size()]);
    }

    public void reset() {
        cursor = 0;
        for (Token tk : token) {
            tk.reset();
        }
    }

    public Token nextToken() {
        if (cursor < length)
            return token[cursor++];
        else
            return null;
    }

    public Token peekToken() {
        if (cursor < length)
            return token[cursor];
        else
            return null;
    }

    public Token peekLast() {
        if (cursor > 0) {
            return token[cursor - 1];
        }
        else {
            return null;
        }
    }


    public Token tokensBack(int offset) {
        if (cursor - offset >= 0) {
            return token[cursor - offset];
        }
        else {
            return null;
        }
    }

    public void back() {
        cursor--;
    }

    public boolean hasMoreTokens() {
        return cursor < length;
    }


    public String showTokenChain() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append("(" + i + "): <<" + token[i].getName() + ">> = <<" + token[i].getValue() + ">> [" + (token[i].getValue() != null ? token[i].getValue().getClass() : "null") + "]").append("\n");
        }

        return sb.toString();
    }


    public TokenIterator clone() {
        try {
            FastTokenIterator ti = (FastTokenIterator) super.clone();
            ti.token = new Token[token.length];

            for (int i = 0; i < token.length; i++) {
                ti.token[i] = token[i].clone();
            }
            return ti;
        }
        catch (CloneNotSupportedException e) {
            // no handling needed.
            return null;
        }

    }
}
