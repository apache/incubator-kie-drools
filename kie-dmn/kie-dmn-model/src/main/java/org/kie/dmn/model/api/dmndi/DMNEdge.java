package org.kie.dmn.model.api.dmndi;

import javax.xml.namespace.QName;

public interface DMNEdge extends Edge {

    public DMNLabel getDMNLabel();

    public void setDMNLabel(DMNLabel value);

    public QName getDmnElementRef();

    public void setDmnElementRef(QName value);

    /**
     * @since DMN v1.3
     */
    QName getSourceElement();

    /**
     * @since DMN v1.3
     */
    void setSourceElement(QName value);

    /**
     * @since DMN v1.3
     */
    QName getTargetElement();

    /**
     * @since DMN v1.3
     */
    void setTargetElement(QName value);
}
