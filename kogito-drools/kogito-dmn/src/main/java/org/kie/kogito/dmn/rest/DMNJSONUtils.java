/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.dmn.rest;

import java.util.Map;

import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.FEELPropertyAccessible;
import org.kie.kogito.decision.DecisionModel;

/**
 * Internal Utility class.<br/>
 * Used to simplify generated/scaffolded code to create a DMNContext accordingly to JSON un-marshalling semantic
 */
public class DMNJSONUtils {

    /**
     * Internal Utility method.<br/>
     * Used to simplify generated/scaffolded code to create a DMNContext accordingly to JSON un-marshalling semantic
     */
    public static DMNContext ctx(DecisionModel dm, Map<String, Object> variables) {
        if (variables != null && variables.size() > 0) {
            return new org.kie.dmn.core.internal.utils.DynamicDMNContextBuilder(new org.kie.dmn.core.impl.DMNContextImpl(), dm.getDMNModel()).populateContextWith(variables);
        } else {
            return dm.newContext(variables);
        }
    }

    /**
     * Internal Utility method.<br/>
     * Used to simplify generated/scaffolded code to create a DMNContext accordingly to JSON un-marshalling semantic
     */
    public static DMNContext ctx(DecisionModel dm, FEELPropertyAccessible variables) {
        return dm.newContext(variables);
    }

    /**
     * Internal Utility method.<br/>
     * Used to simplify generated/scaffolded code to create a DMNContext accordingly to JSON un-marshalling semantic
     */
    public static DMNContext ctx(DecisionModel dm, Map<String, Object> variables, String decisionServiceName) {
        if (variables != null && variables.size() > 0) {
            return new org.kie.dmn.core.internal.utils.DynamicDMNContextBuilder(new org.kie.dmn.core.impl.DMNContextImpl(), dm.getDMNModel()).populateContextForDecisionServiceWith(decisionServiceName, variables);
        } else {
            return dm.newContext(variables);
        }
    }

    /**
     * Internal Utility method.<br/>
     * Used to simplify generated/scaffolded code to create a DMNContext accordingly to JSON un-marshalling semantic
     */
    public static DMNContext ctx(DecisionModel dm, FEELPropertyAccessible variables, String decisionServiceName) {
        return dm.newContext(variables);
    }

    private DMNJSONUtils() {
        // intentionally private.
    }
}
