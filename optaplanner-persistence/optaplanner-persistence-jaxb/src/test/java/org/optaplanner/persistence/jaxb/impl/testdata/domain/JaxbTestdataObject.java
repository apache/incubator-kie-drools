package org.optaplanner.persistence.jaxb.impl.testdata.domain;

import javax.xml.bind.annotation.XmlID;

import org.optaplanner.core.impl.testdata.util.CodeAssertable;

public abstract class JaxbTestdataObject implements CodeAssertable {

    protected String code;

    public JaxbTestdataObject() {
    }

    public JaxbTestdataObject(String code) {
        this.code = code;
    }

    @Override
    @XmlID
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }

}
