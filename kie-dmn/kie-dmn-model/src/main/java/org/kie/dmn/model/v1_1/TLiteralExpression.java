package org.kie.dmn.model.v1_1;

import org.kie.dmn.model.api.ImportedValues;
import org.kie.dmn.model.api.LiteralExpression;

public class TLiteralExpression extends TExpression implements LiteralExpression {

    private String text;
    private ImportedValues importedValues;
    private String expressionLanguage;

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText( final String value ) {
        this.text = value;
    }

    @Override
    public ImportedValues getImportedValues() {
        return importedValues;
    }

    @Override
    public void setImportedValues(final ImportedValues value) {
        this.importedValues = value;
    }

    @Override
    public String getExpressionLanguage() {
        return expressionLanguage;
    }

    @Override
    public void setExpressionLanguage( final String value ) {
        this.expressionLanguage = value;
    }

}
