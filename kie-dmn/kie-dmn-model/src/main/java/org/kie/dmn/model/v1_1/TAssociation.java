/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
