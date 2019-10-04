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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;

import static org.drools.workbench.models.datamodel.rule.Attribute.ACTIVATION_GROUP;
import static org.drools.workbench.models.datamodel.rule.Attribute.SALIENCE;

public class HitPolicyValidation {

    public static Set<String> getReservedAttributes(GuidedDecisionTable52.HitPolicy hitPolicy ) {
        switch ( hitPolicy ) {
            case UNIQUE_HIT:
                return new HashSet<>( Arrays.asList( ACTIVATION_GROUP.getAttributeName() ) );
            case FIRST_HIT:
            case RESOLVED_HIT:
                return new HashSet<>( Arrays.asList( SALIENCE.getAttributeName(),
                                                     ACTIVATION_GROUP.getAttributeName() ) );
            case RULE_ORDER:
                return new HashSet<>( Arrays.asList( SALIENCE.getAttributeName() ) );
            case NONE:
            default:
                return Collections.EMPTY_SET;
        }
    }

}
