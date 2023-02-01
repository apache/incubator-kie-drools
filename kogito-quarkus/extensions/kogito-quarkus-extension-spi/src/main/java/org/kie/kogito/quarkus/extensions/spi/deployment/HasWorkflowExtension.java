/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.quarkus.extensions.spi.deployment;

import java.util.function.BooleanSupplier;

/**
 * Can be used to execute a BuildItem `onlyIf` the Workflow extension is presented.
 * Yet, another way of implementing this feature is to use the capability feature.
 * But in this case, it would require the `BuildItem` to be presented and/or the method to be executed
 * to add the conditional verifying the capability.
 *
 * @see <a href="https://quarkus.io/guides/writing-extensions#conditional-step-inclusion">Conditional Step Inclusion</a>
 * @see <a href="https://quarkus.io/guides/writing-extensions#capabilities">Capabilities</a>
 */
public class HasWorkflowExtension implements BooleanSupplier {

    private static final String WORKFLOW_EXTENSION_CLASS = "org.kie.kogito.quarkus.serverless.workflow.deployment.ServerlessWorkflowAssetsProcessor";

    @Override
    public boolean getAsBoolean() {
        try {
            Class.forName(WORKFLOW_EXTENSION_CLASS);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
