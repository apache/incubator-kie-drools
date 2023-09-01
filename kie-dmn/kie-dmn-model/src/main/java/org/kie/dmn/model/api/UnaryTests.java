package org.kie.dmn.model.api;

import javax.xml.namespace.QName;

public interface UnaryTests extends Expression {

    String getText();

    void setText(String value);

    String getExpressionLanguage();

    void setExpressionLanguage(String value);

    @Override
    default QName getTypeRef() {
        throw new UnsupportedOperationException("An instance of UnaryTests inherits an optional typeRef from Expression, which must not be used");
    }

    @Override
    default void setTypeRef(QName value) {
        throw new UnsupportedOperationException("An instance of UnaryTests inherits an optional typeRef from Expression, which must not be used");
    }
}
