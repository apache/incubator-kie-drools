package org.kie.dmn.model.v1_4;

import org.kie.dmn.model.api.ImportedValues;
import org.kie.dmn.model.api.LiteralExpression;

public class TLiteralExpression extends TExpression implements LiteralExpression {

    protected String text;
    protected ImportedValues importedValues;
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
    public ImportedValues getImportedValues() {
        return importedValues;
    }

    @Override
    public void setImportedValues(ImportedValues value) {
        this.importedValues = value;
    }

    @Override
    public String getExpressionLanguage() {
        return expressionLanguage;
    }

    @Override
    public void setExpressionLanguage(String value) {
        this.expressionLanguage = value;
    }

}
