package org.optaplanner.persistence.xstream.impl.testdata.domain;

import org.optaplanner.core.impl.testdata.domain.TestdataObject;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("xStreamTestdataValue")
public class XStreamTestdataValue extends TestdataObject {

    public XStreamTestdataValue() {
    }

    public XStreamTestdataValue(String code) {
        super(code);
    }

}
