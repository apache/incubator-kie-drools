package org.kie.dmn.typesafe;

class DMNInputSetType extends AbstractDMNSetType {

    DMNInputSetType(DMNAllTypesIndex index, DMNStronglyCodeGenConfig codeGenConfig) {
        super(index, codeGenConfig);
    }

    @Override
    public String getTypeName() {
        return "InputSet";
    }
}
