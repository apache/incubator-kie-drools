package org.drools;

import java.io.Serializable;

public class FactA implements Serializable {
    String  field1;
    Integer field2;
    Float   field3;
    TestEnum enumVal;

    public FactA() {
    }

    public FactA( String f1 ) {
        this.field1 = f1;
    }
    
    public FactA( final Integer f2 ) {
        this.field2 = f2;
    }

    public FactA(final String f1,
                 final Integer f2,
                 final Float f3) {
        this.field1 = f1;
        this.field2 = f2;
        this.field3 = f3;
    }

    public String getField1() {
        return this.field1;
    }

    public void setField1(final String s) {
        this.field1 = s;
    }

    public Integer getField2() {
        return this.field2;
    }

    public void setField2(final Integer i) {
        this.field2 = i;
    }

    public Float getField3() {
        return this.field3;
    }

    public void setField3(final Float f) {
        this.field3 = f;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((field1 == null) ? 0 : field1.hashCode());
        result = prime * result + ((field2 == null) ? 0 : field2.hashCode());
        result = prime * result + ((field3 == null) ? 0 : field3.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final FactA other = (FactA) obj;
        if ( field1 == null ) {
            if ( other.field1 != null ) return false;
        } else if ( !field1.equals( other.field1 ) ) return false;
        if ( field2 == null ) {
            if ( other.field2 != null ) return false;
        } else if ( !field2.equals( other.field2 ) ) return false;
        if ( field3 == null ) {
            if ( other.field3 != null ) return false;
        } else if ( !field3.equals( other.field3 ) ) return false;
        return true;
    }

    /**
     * @return the enumVal
     */
    public TestEnum getEnumVal() {
        return enumVal;
    }

    /**
     * @param enumVal the enumVal to set
     */
    public void setEnumVal( TestEnum enumVal ) {
        this.enumVal = enumVal;
    }

}
