package org.drools.base.facttemplates;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.base.base.ValueType;

public class FieldTemplateImpl implements FieldTemplate, Externalizable {

    private static final long serialVersionUID = 510l;

    private String      name;
    private ValueType   valueType;

    public FieldTemplateImpl() {

    }

    public FieldTemplateImpl(String name, Class clazz) {
        this.name = name;
        this.valueType = clazz != null ? ValueType.OBJECT_TYPE : ValueType.determineValueType( clazz );
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        name    = (String)in.readObject();
        valueType   = (ValueType)in.readObject();
        if (valueType != null)
            valueType   = ValueType.determineValueType(valueType.getClassType());
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(name);
        out.writeObject(valueType);
    }

    public String getName() {
        return this.name;
    }

    public ValueType getValueType() {
        return this.valueType;
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + this.name.hashCode();
        result = PRIME * result + this.valueType.hashCode();
        return result;
    }

    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || getClass() != object.getClass() ) {
            return false;
        }

        final FieldTemplateImpl other = (FieldTemplateImpl) object;

        return this.name.equals( other.name ) && this.valueType.equals( other.valueType );
    }

}
