package org.mvel;                                      

import java.io.Serializable;

public class CompiledExpression implements Serializable {
    private char[] expression;
    private TokenIterator tokenMap;

    private Class knownEgressType;
    private Class knownIngressType;

    private boolean convertableIngressEgress;

    public CompiledExpression(char[] expression, TokenIterator tokenMap) {
        this.expression = expression;
        this.tokenMap = new FastTokenIterator(tokenMap);
    }

    public char[] getExpression() {
        return expression;
    }

    public void setExpression(char[] expression) {
        this.expression = expression;
    }

    public TokenIterator getTokenMap() {
        return tokenMap;
    }

    public void setTokenMap(TokenIterator tokenMap) {
        this.tokenMap = new FastTokenIterator(tokenMap);
    }


    public Class getKnownEgressType() {
        return knownEgressType;
    }

    public void setKnownEgressType(Class knownEgressType) {
        this.knownEgressType = knownEgressType;
    }


    public Class getKnownIngressType() {
        return knownIngressType;
    }

    public void setKnownIngressType(Class knownIngressType) {
        this.knownIngressType = knownIngressType;
    }


    public boolean isConvertableIngressEgress() {
        return convertableIngressEgress;
    }

    public void setConvertableIngressEgress(boolean convertableIngressEgress) {
        this.convertableIngressEgress = convertableIngressEgress;
    }

    public void pack() {
        if (knownIngressType != null && knownEgressType != null) {
             convertableIngressEgress = knownIngressType.isAssignableFrom(knownEgressType);
        }
    }
}
