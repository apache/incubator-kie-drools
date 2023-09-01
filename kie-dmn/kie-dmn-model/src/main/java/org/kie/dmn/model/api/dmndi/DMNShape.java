package org.kie.dmn.model.api.dmndi;

import javax.xml.namespace.QName;

public interface DMNShape extends Shape {

    public DMNLabel getDMNLabel();

    public void setDMNLabel(DMNLabel value);

    public DMNDecisionServiceDividerLine getDMNDecisionServiceDividerLine();

    public void setDMNDecisionServiceDividerLine(DMNDecisionServiceDividerLine value);

    public QName getDmnElementRef();

    public void setDmnElementRef(QName value);

    public Boolean isIsListedInputData();

    public void setIsListedInputData(Boolean value);

    public boolean isIsCollapsed();

    public void setIsCollapsed(Boolean value);

}
