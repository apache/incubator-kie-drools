package org.kie.dmn.model.v1_4;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.kie.dmn.model.api.FunctionItem;
import org.kie.dmn.model.api.InformationItem;

public class TFunctionItem extends TDMNElement implements FunctionItem {

    protected List<InformationItem> parameters;
    protected QName outputTypeRef;

    @Override
    public List<InformationItem> getParameters() {
        if (parameters == null) {
            parameters = new ArrayList<>();
        }
        return this.parameters;
    }

    @Override
    public QName getOutputTypeRef() {
        return outputTypeRef;
    }

    @Override
    public void setOutputTypeRef(QName value) {
        this.outputTypeRef = value;
    }

}
