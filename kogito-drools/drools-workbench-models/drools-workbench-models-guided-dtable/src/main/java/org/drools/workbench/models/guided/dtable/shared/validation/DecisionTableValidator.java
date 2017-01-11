/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.models.guided.dtable.shared.validation;

import java.util.Set;

import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;

public class DecisionTableValidator {

    private final GuidedDecisionTable52 dtable;

    private final Set<String> illegalAttributes;

    public DecisionTableValidator( final GuidedDecisionTable52 dtable ) {
        this.dtable = dtable;
        this.illegalAttributes = HitPolicyValidation.getReservedAttributes( dtable.getHitPolicy() );
    }

    public void validate() {
        validateAttributes();
    }

    public void isValidToAdd( final AttributeCol52 attributeCol52 ) {

        isValid( attributeCol52 );

        for ( final AttributeCol52 existingAttributeCol52 : dtable.getAttributeCols() ) {
            if ( attributeCol52.getAttribute()
                    .equals( existingAttributeCol52.getAttribute() ) ) {
                throw new DuplicateAttributeException( existingAttributeCol52.getAttribute() );
            }
        }
    }

    private void isValid( final AttributeCol52 attributeCol52 ) {
        if ( illegalAttributes.contains( attributeCol52.getAttribute() ) ) {
            throw new InvalidAttributeColumnForHitPolicyException( attributeCol52.getAttribute(),
                                                                   dtable.getHitPolicy() );
        }
    }

    private void validateAttributes() {
        for ( final AttributeCol52 attributeCol52 : dtable.getAttributeCols() ) {
            isValid( attributeCol52 );
        }
    }
}
