package org.kie.dmn.model.v1x;

public interface LiteralExpression extends Expression {

    String getText();

    void setText(String value);

    ImportedValues getImportedValues();

    void setImportedValues(ImportedValues value);

    String getExpressionLanguage();

    void setExpressionLanguage(String value);

}
