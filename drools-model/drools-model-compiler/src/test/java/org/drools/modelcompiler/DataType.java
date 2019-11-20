package org.drools.modelcompiler;

import java.util.Date;

public class DataType {
    String field1;
    String field2;
    Date fieldDate;

    public DataType(String field1, String field2, Date fieldDate) {
        this.field1 = field1;
        this.field2 = field2;
        this.fieldDate = fieldDate;
    }

    public DataType(String field1, String field2) {
        this(field1, field2, null);
    }

    public DataType() {
    }

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }

    public Date getFieldDate() {
        return fieldDate;
    }

    public void setFieldDate(Date fieldDate) {
        this.fieldDate = fieldDate;
    }
}