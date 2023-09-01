package org.kie.dmn.model.v1_1;

import org.kie.dmn.model.api.Association;
import org.kie.dmn.model.api.AssociationDirection;
import org.kie.dmn.model.api.DMNElementReference;

public class TAssociation extends TArtifact implements Association {

    private DMNElementReference sourceRef;
    private DMNElementReference targetRef;
    private AssociationDirection associationDirection;

    @Override
    public DMNElementReference getSourceRef() {
        return sourceRef;
    }

    @Override
    public void setSourceRef(final DMNElementReference value) {
        this.sourceRef = value;
    }

    @Override
    public DMNElementReference getTargetRef() {
        return targetRef;
    }

    @Override
    public void setTargetRef(final DMNElementReference value) {
        this.targetRef = value;
    }

    @Override
    public AssociationDirection getAssociationDirection() {
        if ( associationDirection == null ) {
            return AssociationDirection.NONE;
        } else {
            return associationDirection;
        }
    }

    @Override
    public void setAssociationDirection( final AssociationDirection value ) {
        this.associationDirection = value;
    }

}
