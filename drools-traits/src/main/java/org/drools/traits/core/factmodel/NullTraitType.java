package org.drools.traits.core.factmodel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.BitSet;
import java.util.Collections;
import java.util.Map;

import org.drools.base.factmodel.traits.Thing;
import org.drools.base.factmodel.traits.TraitType;

public class NullTraitType implements TraitType,
                                      Thing, Externalizable {

    private BitSet typeCode;

    public NullTraitType() {
    }

    public NullTraitType( BitSet code ) {
        typeCode = code;
    }

    public BitSet _getTypeCode() {
        return typeCode;
    }

    public boolean _isVirtual() {
        return true;
    }

    public String _getTraitName() {
        return "";
    }

    @Override
    public boolean _hasTypeCode( BitSet typeCode ) {
        return false;
    }

    public void _setTypeCode(BitSet typeCode) {
        this.typeCode = typeCode;
    }

    public Map<String, Object> getFields() {
        return Collections.EMPTY_MAP;
    }

    public Object getCore() {
        return null;
    }

    public boolean isTop() {
        return false;
    }

    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        objectOutput.writeObject( typeCode );
    }

    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        typeCode = (BitSet) objectInput.readObject();
    }
}
