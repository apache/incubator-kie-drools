package org.drools.model.codegen.execmodel.domain;

public class TargetPolicy {

    private String customerCode;

    private String productCode;

    private int coefficient;

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public int getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(int coefficient) {
        this.coefficient = coefficient;
    }

    @Override
    public String toString() {
        return "TargetPolicy{" +
                "customerCode='" + customerCode + '\'' +
                ", productCode='" + productCode + '\'' +
                ", coefficient=" + coefficient +
                '}';
    }
}

