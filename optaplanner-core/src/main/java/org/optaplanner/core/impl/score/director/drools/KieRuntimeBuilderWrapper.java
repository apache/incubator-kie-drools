/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.score.director.drools;

import org.kie.api.KieBase;
import org.kie.kogito.legacy.rules.KieRuntimeBuilder;

/**
 * Wraps {@link KieRuntimeBuilder} so the dependency on kogito-api is optional.
 */
public class KieRuntimeBuilderWrapper {

    private final KieRuntimeBuilder kieRuntimeBuilder;

    public KieRuntimeBuilderWrapper(KieRuntimeBuilder kieRuntimeBuilder) {
        this.kieRuntimeBuilder = kieRuntimeBuilder;
    }

    public KieBase extractKieBase() {
        return kieRuntimeBuilder.getKieBase();
    }

}
