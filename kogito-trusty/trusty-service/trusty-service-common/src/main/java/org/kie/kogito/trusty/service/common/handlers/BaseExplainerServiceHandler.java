/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.trusty.service.common.handlers;

import org.kie.kogito.explainability.api.BaseExplainabilityResultDto;
import org.kie.kogito.trusty.storage.api.model.BaseExplainabilityResult;
import org.kie.kogito.trusty.storage.api.model.ExplainabilityStatus;
import org.kie.kogito.trusty.storage.common.TrustyStorageService;

public abstract class BaseExplainerServiceHandler<R extends BaseExplainabilityResult, D extends BaseExplainabilityResultDto> implements ExplainerServiceHandler<R, D> {

    protected TrustyStorageService storageService;

    protected BaseExplainerServiceHandler() {
        //CDI proxy
    }

    protected BaseExplainerServiceHandler(TrustyStorageService storageService) {
        this.storageService = storageService;
    }

    protected ExplainabilityStatus statusFrom(org.kie.kogito.explainability.api.ExplainabilityStatus status) {
        if (org.kie.kogito.explainability.api.ExplainabilityStatus.SUCCEEDED.equals(status)) {
            return ExplainabilityStatus.SUCCEEDED;
        }
        return ExplainabilityStatus.FAILED;
    }

}
