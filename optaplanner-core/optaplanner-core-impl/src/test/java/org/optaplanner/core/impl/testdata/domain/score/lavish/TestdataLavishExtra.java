package org.optaplanner.core.impl.testdata.domain.score.lavish;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.optaplanner.core.impl.testdata.domain.TestdataObject;

public class TestdataLavishExtra extends TestdataObject {

    private String stringProperty = "";
    private Integer integerProperty = 1;
    private Long longProperty = 1L;
    private BigInteger bigIntegerProperty = BigInteger.ONE;
    private BigDecimal bigDecimalProperty = BigDecimal.ONE;

    public TestdataLavishExtra() {
    }

    public TestdataLavishExtra(String code) {
        super(code);
    }

    public String getStringProperty() {
        return stringProperty;
    }

    public void setStringProperty(String stringProperty) {
        this.stringProperty = stringProperty;
    }

    public Integer getIntegerProperty() {
        return integerProperty;
    }

    public void setIntegerProperty(Integer integerProperty) {
        this.integerProperty = integerProperty;
    }

    public Long getLongProperty() {
        return longProperty;
    }

    public void setLongProperty(Long longProperty) {
        this.longProperty = longProperty;
    }

    public BigInteger getBigIntegerProperty() {
        return bigIntegerProperty;
    }

    public void setBigIntegerProperty(BigInteger bigIntegerProperty) {
        this.bigIntegerProperty = bigIntegerProperty;
    }

    public BigDecimal getBigDecimalProperty() {
        return bigDecimalProperty;
    }

    public void setBigDecimalProperty(BigDecimal bigDecimalProperty) {
        this.bigDecimalProperty = bigDecimalProperty;
    }
}
