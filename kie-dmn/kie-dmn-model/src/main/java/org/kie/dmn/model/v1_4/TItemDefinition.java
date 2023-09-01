package org.kie.dmn.model.v1_4;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.kie.dmn.model.api.FunctionItem;
import org.kie.dmn.model.api.ItemDefinition;
import org.kie.dmn.model.api.UnaryTests;

public class TItemDefinition extends TNamedElement implements ItemDefinition {

    /**
     * align to internal model
     */
    protected QName typeRef;
    protected UnaryTests allowedValues;
    protected List<ItemDefinition> itemComponent;
    protected FunctionItem functionItem;
    protected String typeLanguage;
    protected Boolean isCollection;

    @Override
    public QName getTypeRef() {
        return typeRef;
    }

    @Override
    public void setTypeRef(final QName value) {
        this.typeRef = value;
    }

    @Override
    public UnaryTests getAllowedValues() {
        return allowedValues;
    }

    @Override
    public void setAllowedValues(UnaryTests value) {
        this.allowedValues = value;
    }

    @Override
    public List<ItemDefinition> getItemComponent() {
        if (itemComponent == null) {
            itemComponent = new ArrayList<>();
        }
        return this.itemComponent;
    }

    @Override
    public String getTypeLanguage() {
        return typeLanguage;
    }

    @Override
    public void setTypeLanguage(String value) {
        this.typeLanguage = value;
    }

    @Override
    public boolean isIsCollection() {
        if (isCollection == null) {
            return false;
        } else {
            return isCollection;
        }
    }

    @Override
    public void setIsCollection(Boolean value) {
        this.isCollection = value;
    }

    @Override
    public FunctionItem getFunctionItem() {
        return functionItem;
    }

    @Override
    public void setFunctionItem(FunctionItem value) {
        this.functionItem = value;
    }
}
