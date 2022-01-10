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
package org.kie.kogito.event.cloudevents.extension;

import java.util.Set;

import io.cloudevents.CloudEventExtension;
import io.cloudevents.CloudEventExtensions;
import io.cloudevents.core.provider.ExtensionProvider;

import static org.kie.kogito.event.cloudevents.CloudEventExtensionConstants.PMML_FULL_RESULT;
import static org.kie.kogito.event.cloudevents.CloudEventExtensionConstants.PMML_MODEL_NAME;
import static org.kie.kogito.event.cloudevents.extension.KogitoExtensionUtils.readBooleanExtension;
import static org.kie.kogito.event.cloudevents.extension.KogitoExtensionUtils.readStringExtension;

public class KogitoPredictionsExtension implements CloudEventExtension {

    private static final Set<String> KEYS = Set.of(PMML_MODEL_NAME, PMML_FULL_RESULT);

    private String pmmlModelName;
    private Boolean pmmlFullResult;

    public static void register() {
        ExtensionProvider.getInstance().registerExtension(KogitoPredictionsExtension.class, KogitoPredictionsExtension::new);
    }

    @Override
    public void readFrom(CloudEventExtensions extensions) {
        readStringExtension(extensions, PMML_MODEL_NAME, this::setPmmlModelName);
        readBooleanExtension(extensions, PMML_FULL_RESULT, this::setPmmlFullResult);
    }

    @Override
    public Object getValue(String key) throws IllegalArgumentException {
        switch (key) {
            case PMML_MODEL_NAME:
                return getPmmlModelName();
            case PMML_FULL_RESULT:
                return isPmmlFullResult();
            default:
                return null;
        }
    }

    @Override
    public Set<String> getKeys() {
        return KEYS;
    }

    public String getPmmlModelName() {
        return pmmlModelName;
    }

    public void setPmmlModelName(String pmmlModelName) {
        this.pmmlModelName = pmmlModelName;
    }

    public Boolean isPmmlFullResult() {
        return pmmlFullResult;
    }

    public void setPmmlFullResult(Boolean pmmlFullResult) {
        this.pmmlFullResult = pmmlFullResult;
    }
}
