package org.kie.pmml.pmml_4_2.compiler;

import org.dmg.pmml.pmml_4_2.descr.DATATYPE;
import org.kie.api.definition.type.Key;

public class FieldMarker {

    @Key
    private String name;
    private DATATYPE dataType;

    public FieldMarker(String name, DATATYPE dataType) {
        super();
        this.name = name;
        this.dataType = dataType;
    }

    public FieldMarker() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DATATYPE getDataType() {
        return dataType;
    }

    public void setDataType(DATATYPE dataType) {
        this.dataType = dataType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FieldMarker other = (FieldMarker) obj;
        if (dataType != other.dataType)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

}
