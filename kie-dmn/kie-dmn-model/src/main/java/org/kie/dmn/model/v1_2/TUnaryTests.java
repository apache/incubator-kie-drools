package org.kie.dmn.model.v1_2;

import javax.xml.namespace.QName;

import org.kie.dmn.model.api.UnaryTests;

public class TUnaryTests extends TExpression implements UnaryTests {

    protected String text;
    protected String expressionLanguage;

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String value) {
        this.text = value;
    }

    @Override
    public String getExpressionLanguage() {
        return expressionLanguage;
    }

    @Override
    public void setExpressionLanguage(String value) {
        this.expressionLanguage = value;
    }

    @Override
    public QName getTypeRef() {
        throw new UnsupportedOperationException("Not on DMN v1.2");
    }

    @Override
    public void setTypeRef(QName value) {
        throw new UnsupportedOperationException("Not on DMN v1.2");
    }
}
