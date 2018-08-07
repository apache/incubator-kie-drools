package org.kie.dmn.model.v1x;

public interface Association extends Artifact {

    DMNElementReference getSourceRef();

    void setSourceRef(DMNElementReference value);

    DMNElementReference getTargetRef();

    void setTargetRef(DMNElementReference value);

    AssociationDirection getAssociationDirection();

    void setAssociationDirection(AssociationDirection value);

}
