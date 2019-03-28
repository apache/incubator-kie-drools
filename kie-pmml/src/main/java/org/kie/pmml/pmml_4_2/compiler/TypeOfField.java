package org.kie.pmml.pmml_4_2.compiler;

import org.dmg.pmml.pmml_4_2.descr.DATATYPE;
import org.kie.api.definition.type.Key;
import org.kie.api.definition.type.Position;

public class TypeOfField {

    @Key
    @Position(0)
    private String name;
    @Position(1)
    private DATATYPE dataType;

    public TypeOfField(String name, DATATYPE dataType) {
        super();
        this.name = name;
        this.dataType = dataType;
    }

    public TypeOfField() {
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
        TypeOfField other = (TypeOfField) obj;
        if (dataType != other.dataType)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "TypeOfField [name=" + name + ", dataType=" + dataType + "]";
    }

}
