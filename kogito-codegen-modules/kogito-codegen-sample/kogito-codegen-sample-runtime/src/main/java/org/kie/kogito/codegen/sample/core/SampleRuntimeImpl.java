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
package org.kie.kogito.codegen.sample.core;

import java.util.HashMap;
import java.util.Map;

import org.kie.kogito.Application;

public class SampleRuntimeImpl implements SampleRuntime {

    protected final Map<String, String> rawContent = new HashMap<>();
    protected SampleConfig config;

    public SampleRuntimeImpl() {

    }

    public SampleRuntimeImpl(Application application) {
        initApplication(application);
    }

    protected void initApplication(Application application) {
        this.config = application.config().get(SampleConfig.class);
    }

    protected void setConfig(SampleConfig config) {
        this.config = config;
    }

    public void addModels(Map<String, String> content) {
        this.rawContent.putAll(content);
    }

    @Override
    public SampleModel getModel(String name) {
        if (!rawContent.containsKey(name)) {
            throw new IllegalArgumentException("Impossible to find " + name);
        }
        if (config == null) {
            throw new IllegalStateException("No SampleConfig instance provided");
        }
        return new SampleModelImpl(name, rawContent.get(name), config.numberOfCopy());
    }
}
