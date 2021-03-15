/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.mvel;

import org.drools.mvel.MVELConstraint.EvaluationContext;

public class ConstraintEvaluationException extends RuntimeException {

    private static final long serialVersionUID = -3413225194510143529L;

    public ConstraintEvaluationException(String expression, EvaluationContext evaluationContext, Throwable cause) {
        super("Error evaluating constraint '" + expression + "' in " + evaluationContext, cause);
    }
}
