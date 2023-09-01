package org.kie.dmn.model.v1_4;

import org.kie.dmn.model.api.Association;
import org.kie.dmn.model.api.AssociationDirection;
import org.kie.dmn.model.api.DMNElementReference;

public class TAssociation extends TArtifact implements Association {

    protected DMNElementReference sourceRef;
    protected DMNElementReference targetRef;
    protected AssociationDirection associationDirection;

    @Override
    public DMNElementReference getSourceRef() {
        return sourceRef;
    }

    @Override
    public void setSourceRef(DMNElementReference value) {
        this.sourceRef = value;
    }

    @Override
    public DMNElementReference getTargetRef() {
        return targetRef;
    }

    @Override
    public void setTargetRef(DMNElementReference value) {
        this.targetRef = value;
    }

    @Override
    public AssociationDirection getAssociationDirection() {
        if (associationDirection == null) {
            return AssociationDirection.NONE;
        } else {
            return associationDirection;
        }
    }

    @Override
    public void setAssociationDirection(AssociationDirection value) {
        this.associationDirection = value;
    }

}
