package org.kie.dmn.model.v1_1;

import org.kie.dmn.model.api.ImportedValues;

public class TImportedValues extends TImport implements ImportedValues {

    private String importedElement;
    private String expressionLanguage;

    @Override
    public String getImportedElement() {
        return importedElement;
    }

    @Override
    public void setImportedElement( final String value ) {
        this.importedElement = value;
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
