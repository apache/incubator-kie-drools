/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.validation.dtanalysis;

import org.kie.dmn.model.api.DecisionTable;

public class DMNDTAnalysisException extends RuntimeException {

    private final DecisionTable dt;

    public DMNDTAnalysisException(String message, DecisionTable dt) {
        super(message);
        this.dt = dt;
    }

    public DMNDTAnalysisException(Throwable cause, DecisionTable dt) {
        super(cause);
        this.dt = dt;
    }

    public DecisionTable getDt() {
        return dt;
    }

}
