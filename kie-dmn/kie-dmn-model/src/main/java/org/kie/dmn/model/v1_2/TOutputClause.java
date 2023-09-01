package org.kie.dmn.model.v1_2;

import javax.xml.namespace.QName;

import org.kie.dmn.model.api.LiteralExpression;
import org.kie.dmn.model.api.OutputClause;
import org.kie.dmn.model.api.UnaryTests;

public class TOutputClause extends TDMNElement implements OutputClause {

    protected UnaryTests outputValues;
    protected LiteralExpression defaultOutputEntry;
    protected String name;
    /**
     * align to internal model
     */
    protected QName typeRef;

    @Override
    public UnaryTests getOutputValues() {
        return outputValues;
    }

    @Override
    public void setOutputValues(UnaryTests value) {
        this.outputValues = value;
    }

    @Override
    public LiteralExpression getDefaultOutputEntry() {
        return defaultOutputEntry;
    }

    @Override
    public void setDefaultOutputEntry(LiteralExpression value) {
        this.defaultOutputEntry = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String value) {
        this.name = value;
    }

    @Override
    public QName getTypeRef() {
        return typeRef;
    }

    @Override
    public void setTypeRef(QName value) {
        this.typeRef = value;
    }

}
