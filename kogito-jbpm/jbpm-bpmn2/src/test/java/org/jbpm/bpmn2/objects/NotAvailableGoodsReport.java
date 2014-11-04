package org.jbpm.bpmn2.objects;

@javax.xml.bind.annotation.XmlRootElement
public class NotAvailableGoodsReport implements java.io.Serializable {

    static final long serialVersionUID = 1L;

    @org.kie.api.definition.type.Position(value = 0)
    private java.lang.String type;

    public NotAvailableGoodsReport() {
    }

    public NotAvailableGoodsReport(java.lang.String type) {
        this.type = type;
    }

    public java.lang.String getType() {
        return this.type;
    }

    public void setType(java.lang.String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "NotAvailableGoodsReport{type:" + type + "}";
    }

}
