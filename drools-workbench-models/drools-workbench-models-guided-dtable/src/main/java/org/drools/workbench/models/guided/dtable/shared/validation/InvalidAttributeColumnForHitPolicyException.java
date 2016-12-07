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

import org.drools.workbench.models.guided.dtable.shared.hitpolicy.DTableValidationException;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;

public class InvalidAttributeColumnForHitPolicyException
        extends DTableValidationException {

    private final String attribute;
    private final GuidedDecisionTable52.HitPolicy hitPolicy;


    public InvalidAttributeColumnForHitPolicyException( final String attribute,
                                                        final GuidedDecisionTable52.HitPolicy hitPolicy ) {
        super( "A decision table can not have both. Attribute: " + attribute + " and hit policy: " + hitPolicy.toString() );

        this.attribute = attribute;
        this.hitPolicy = hitPolicy;
    }

    private InvalidAttributeColumnForHitPolicyException() {
        this.attribute = null;
        this.hitPolicy = null;
    }

    public String getAttribute() {
        return attribute;
    }

    public GuidedDecisionTable52.HitPolicy getHitPolicy() {
        return hitPolicy;
    }
}
