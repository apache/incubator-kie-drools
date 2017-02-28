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

public class HitPolicyValidation {

    public static Set<String> getReservedAttributes( GuidedDecisionTable52.HitPolicy hitPolicy ) {
        switch ( hitPolicy ) {
            case UNIQUE_HIT:
                return new HashSet<>( Arrays.asList( GuidedDecisionTable52.ACTIVATION_GROUP_ATTR ) );
            case FIRST_HIT:
            case RESOLVED_HIT:
                return new HashSet<>( Arrays.asList( GuidedDecisionTable52.SALIENCE_ATTR,
                                                     GuidedDecisionTable52.ACTIVATION_GROUP_ATTR ) );
            case RULE_ORDER:
                return new HashSet<>( Arrays.asList( GuidedDecisionTable52.SALIENCE_ATTR ) );
            case NONE:
            default:
                return Collections.EMPTY_SET;
        }
    }

}
