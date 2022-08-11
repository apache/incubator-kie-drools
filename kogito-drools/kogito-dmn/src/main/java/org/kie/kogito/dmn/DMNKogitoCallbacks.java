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
package org.kie.kogito.dmn;

import java.io.Reader;
import java.util.function.BiFunction;

import org.kie.dmn.api.core.DMNRuntime;
import org.kie.kogito.ExecutionIdSupplier;
import org.kie.kogito.KogitoGAV;
import org.kie.kogito.decision.DecisionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Internal Utility class, for debugging purposes.
 * These are internal callbacks that should occur only at Build or Init time.
 */
public final class DMNKogitoCallbacks {

    private static final Logger LOG = LoggerFactory.getLogger(DMNKogitoCallbacks.class);

    private DMNKogitoCallbacks() {
        // intentionally private.
    }

    public static void beforeCreateGenericDMNRuntime(Reader[] readers) {
        if (isGraalVMNIRuntime()) {
            LOG.warn("createGenericDMNRuntime with {} model(s) for DMNRuntime initialization...", readers.length);
        } else {
            LOG.debug("createGenericDMNRuntime with {} model(s) for DMNRuntime initialization...", readers.length);
        }
    }

    public static void afterCreateGenericDMNRuntime(DMNRuntime dmnRuntime) {
        if (isGraalVMNIRuntime()) {
            LOG.warn("createGenericDMNRuntime done. DMNRuntime contains {} DMNModel(s).", dmnRuntime.getModels().size());
        } else {
            LOG.debug("createGenericDMNRuntime done. DMNRuntime contains {} DMNModel(s).", dmnRuntime.getModels().size());
        }
    }

    public static void beforeAbstractDecisionModelsInit(ExecutionIdSupplier executionIdSupplier,
            BiFunction<DecisionModel, KogitoGAV, DecisionModel> decisionModelTransformerInit,
            Reader[] readers) {
        if (isGraalVMNIRuntime()) {
            LOG.warn("AbstractDecisionModels.init() called.");
        } else {
            LOG.debug("AbstractDecisionModels.init() called.");
        }
    }

    public static void afterAbstractDecisionModelsInit(DMNRuntime dmnRuntime) {
        if (isGraalVMNIRuntime()) {
            LOG.warn("AbstractDecisionModels.init() done.");
        } else {
            LOG.debug("AbstractDecisionModels.init() done.");
        }
    }

    private static boolean isGraalVMNIRuntime() {
        return "runtime".equals(System.getProperty("org.graalvm.nativeimage.imagecode"));
    }
}
