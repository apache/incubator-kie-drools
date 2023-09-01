package org.kie.dmn.typesafe;

class DMNOutputSetType extends AbstractDMNSetType {

    DMNOutputSetType(DMNAllTypesIndex index, DMNStronglyCodeGenConfig codeGenConfig) {
        super(index, codeGenConfig);
    }

    @Override
    public String getTypeName() {
        return "OutputSet";
    }
}
