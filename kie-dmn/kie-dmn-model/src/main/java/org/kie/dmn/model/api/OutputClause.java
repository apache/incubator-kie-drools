package org.kie.dmn.model.api;

import javax.xml.namespace.QName;

public interface OutputClause extends DMNElement {

    UnaryTests getOutputValues();

    void setOutputValues(UnaryTests value);

    LiteralExpression getDefaultOutputEntry();

    void setDefaultOutputEntry(LiteralExpression value);

    String getName();

    void setName(String value);

    QName getTypeRef();

    void setTypeRef(QName value);

}
