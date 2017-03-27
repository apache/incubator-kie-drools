/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.dmn.model.v1_1;

public class Association extends Artifact {

    private DMNElementReference sourceRef;
    private DMNElementReference targetRef;
    private AssociationDirection associationDirection;

    public DMNElementReference getSourceRef() {
        return sourceRef;
    }

    public void setSourceRef( final DMNElementReference value ) {
        this.sourceRef = value;
    }

    public DMNElementReference getTargetRef() {
        return targetRef;
    }

    public void setTargetRef( final DMNElementReference value ) {
        this.targetRef = value;
    }

    public AssociationDirection getAssociationDirection() {
        if ( associationDirection == null ) {
            return AssociationDirection.NONE;
        } else {
            return associationDirection;
        }
    }

    public void setAssociationDirection( final AssociationDirection value ) {
        this.associationDirection = value;
    }

}
