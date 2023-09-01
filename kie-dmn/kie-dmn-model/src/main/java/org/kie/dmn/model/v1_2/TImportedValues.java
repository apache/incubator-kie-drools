package org.kie.dmn.model.v1_2;

import org.kie.dmn.model.api.ImportedValues;

public class TImportedValues extends TImport implements ImportedValues {

    protected String importedElement;
    protected String expressionLanguage;

    @Override
    public String getImportedElement() {
        return importedElement;
    }

    @Override
    public void setImportedElement(String value) {
        this.importedElement = value;
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
