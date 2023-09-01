package org.drools.verifier.components;

import org.drools.drl.ast.descr.BaseDescr;

public class EnumField extends Field {
  
    public EnumField(BaseDescr descr) {
        super(descr);
    }

    private static final long serialVersionUID = 510l;

    @Override
    public String toString() {
        return "Enum: " + objectTypeName + "." + name;
    }
}
