package org.drools.pmml.pmml_4_2.model;

import org.dmg.pmml.pmml_4_2.descr.DataField;
import org.dmg.pmml.pmml_4_2.descr.OutputField;
import org.drools.pmml.pmml_4_2.PMML4Helper;

public class PMMLDataField {
    private String type;
    private String name;
    private static PMML4Helper helper = new PMML4Helper();

    public PMMLDataField(DataField field) {
        this.type = helper.mapDatatype(field.getDataType(),true);
        this.name = helper.compactAsJavaId(field.getName());
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompactUpperCaseName() {
        return helper.compactUpperCase(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PMMLDataField dataField = (PMMLDataField) o;

        if (getType() != null ? !getType().equals(dataField.getType()) : dataField.getType() != null) {
            return false;
        }
        return getName() != null ? getName().equals(dataField.getName()) : dataField.getName() == null;
    }

    @Override
    public int hashCode() {
        int result = getType() != null ? getType().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        return result;
    }
}
