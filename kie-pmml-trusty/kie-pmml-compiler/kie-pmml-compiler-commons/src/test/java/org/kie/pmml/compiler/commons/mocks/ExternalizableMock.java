package org.kie.pmml.compiler.commons.mocks;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;

public class ExternalizableMock implements Externalizable {

    private AbstractKiePMMLComponent kiePMMLComponent;

    public ExternalizableMock() {
    }

    public AbstractKiePMMLComponent getKiePMMLComponent() {
        return kiePMMLComponent;
    }

    public void setKiePMMLComponent(AbstractKiePMMLComponent kiePMMLComponent) {
        this.kiePMMLComponent = kiePMMLComponent;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {

    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    }
}
