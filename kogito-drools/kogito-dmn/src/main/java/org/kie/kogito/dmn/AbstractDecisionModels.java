/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.dmn;

import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.function.BiFunction;

import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.compiler.DMNProfile;
import org.kie.kogito.Application;
import org.kie.kogito.ExecutionIdSupplier;
import org.kie.kogito.KogitoGAV;
import org.kie.kogito.config.ConfigBean;
import org.kie.kogito.decision.DecisionConfig;
import org.kie.kogito.decision.DecisionModel;
import org.kie.kogito.decision.DecisionModels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDecisionModels implements DecisionModels {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDecisionModels.class);
    private static final boolean CAN_PLATFORM_CLASSLOAD = org.kie.dmn.feel.util.ClassLoaderUtil.CAN_PLATFORM_CLASSLOAD;
    private static DMNRuntime dmnRuntime = null;
    private static ExecutionIdSupplier execIdSupplier = null;
    private static BiFunction<DecisionModel, KogitoGAV, DecisionModel> decisionModelTransformer = null;
    private KogitoGAV gav = KogitoGAV.EMPTY_GAV;

    protected static void init(ExecutionIdSupplier executionIdSupplier,
            BiFunction<DecisionModel, KogitoGAV, DecisionModel> decisionModelTransformerInit,
            Set<DMNProfile> customDMNProfiles,
            boolean enableRuntimeTypeCheckOption,
            Reader... readers) {
        DMNKogitoCallbacks.beforeAbstractDecisionModelsInit(executionIdSupplier, decisionModelTransformerInit, readers);
        dmnRuntime = DMNKogito.createGenericDMNRuntime(customDMNProfiles, enableRuntimeTypeCheckOption, readers);
        execIdSupplier = executionIdSupplier;
        decisionModelTransformer = decisionModelTransformerInit;
        DMNKogitoCallbacks.afterAbstractDecisionModelsInit(dmnRuntime);
    }

    public DecisionModel getDecisionModel(String namespace, String name) {
        DecisionModel model = new DmnDecisionModel(dmnRuntime, namespace, name, execIdSupplier);
        return decisionModelTransformer == null
                ? model
                : decisionModelTransformer.apply(model, gav);
    }

    public AbstractDecisionModels() {
        // needed by CDI
    }

    public AbstractDecisionModels(Application app) {
        initApplication(app);
    }

    protected void initApplication(Application app) {
        app.config().get(DecisionConfig.class).decisionEventListeners().listeners().forEach(dmnRuntime::addListener);
        gav = app.config().get(ConfigBean.class).getGav().orElse(KogitoGAV.EMPTY_GAV);
    }

    @Deprecated
    protected static java.io.InputStreamReader readResource(java.io.InputStream stream) {
        Charset defaultEncoding = Charset.defaultCharset();
        return readResource(stream, defaultEncoding.name());
    }

    protected static java.io.InputStreamReader readResource(java.io.InputStream stream, String encoding) {
        if (CAN_PLATFORM_CLASSLOAD) {
            return isrWithEncodingOrFallback(stream, encoding);
        }

        try {
            byte[] bytes = org.drools.util.IoUtils.readBytesFromInputStream(stream);
            java.io.ByteArrayInputStream byteArrayInputStream = new java.io.ByteArrayInputStream(bytes);
            return isrWithEncodingOrFallback(byteArrayInputStream, encoding);
        } catch (java.io.IOException e) {
            throw new java.io.UncheckedIOException(e);
        }
    }

    /**
     * Create an InputStreamReader using the supplied encoding, or otherwise fallback to the default constructor using the default encoding.
     */
    private static java.io.InputStreamReader isrWithEncodingOrFallback(java.io.InputStream stream, String encoding) {
        try {
            return new java.io.InputStreamReader(stream, encoding);
        } catch (UnsupportedEncodingException e) {
            LOG.warn("Unable to create Reader using encoding {}, will use fallback Reader.", encoding);
            return new java.io.InputStreamReader(stream);
        }
    }
}
