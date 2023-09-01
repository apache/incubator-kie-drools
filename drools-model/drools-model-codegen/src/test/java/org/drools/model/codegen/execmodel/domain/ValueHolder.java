package org.drools.model.codegen.execmodel.domain;

import java.math.BigDecimal;

public class ValueHolder {

    private int intValue;
    private String strValue;
    private BigDecimal bdValue;
    private Object objValue;

    private boolean primitiveBooleanValue;
    private Boolean wrapperBooleanValue;

    public ValueHolder() {}

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public String getStrValue() {
        return strValue;
    }

    public void setStrValue(String strValue) {
        this.strValue = strValue;
    }

    public BigDecimal getBdValue() {
        return bdValue;
    }

    public void setBdValue(BigDecimal bdValue) {
        this.bdValue = bdValue;
    }

    public Object getObjValue() {
        return objValue;
    }

    public void setObjValue(Object objValue) {
        this.objValue = objValue;
    }

    public boolean isPrimitiveBooleanValue() {
        return primitiveBooleanValue;
    }

    public void setPrimitiveBooleanValue(boolean primitiveBooleanValue) {
        this.primitiveBooleanValue = primitiveBooleanValue;
    }

    public Boolean getWrapperBooleanValue() {
        return wrapperBooleanValue;
    }

    public void setWrapperBooleanValue(Boolean wrapperBooleanValue) {
        this.wrapperBooleanValue = wrapperBooleanValue;
    }
}
