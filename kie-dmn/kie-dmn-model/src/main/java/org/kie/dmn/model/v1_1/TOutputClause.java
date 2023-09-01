package org.kie.dmn.model.v1_1;

import javax.xml.namespace.QName;

import org.kie.dmn.model.api.LiteralExpression;
import org.kie.dmn.model.api.OutputClause;
import org.kie.dmn.model.api.UnaryTests;

public class TOutputClause extends TDMNElement implements OutputClause {

    private UnaryTests outputValues;
    private LiteralExpression defaultOutputEntry;
    private String name;
    private QName typeRef;

    @Override
    public UnaryTests getOutputValues() {
        return outputValues;
    }

    @Override
    public void setOutputValues(final UnaryTests value) {
        this.outputValues = value;
    }

    @Override
    public LiteralExpression getDefaultOutputEntry() {
        return defaultOutputEntry;
    }

    @Override
    public void setDefaultOutputEntry(final LiteralExpression value) {
        this.defaultOutputEntry = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName( final String value ) {
        this.name = value;
    }

    @Override
    public QName getTypeRef() {
        return typeRef;
    }

    @Override
    public void setTypeRef( final QName value ) {
        this.typeRef = value;
    }

}
