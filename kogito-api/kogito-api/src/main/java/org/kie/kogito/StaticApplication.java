/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito;

import org.kie.kogito.process.ProcessConfig;
import org.kie.kogito.uow.UnitOfWorkManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class StaticApplication implements Application {

    protected Config config;
    private final Map<Class<? extends KogitoEngine>, KogitoEngine> engineMap = new HashMap<>();

    public StaticApplication() {

    }

    public StaticApplication(
            Config config,
            KogitoEngine ... engines) {
        this(config, Arrays.asList(engines));
    }

    protected StaticApplication(
            Config config,
            Iterable<KogitoEngine> engines) {
        setConfig(config);
        engines.forEach(this::loadEngine);
    }

    @Override
    public Config config() {
        return config;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends KogitoEngine> T get(Class<T> clazz) {
        return (T) engineMap.entrySet().stream()
                .filter(entry -> clazz.isAssignableFrom(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    protected void loadEngines(KogitoEngine ... engines) {
        Arrays.stream(engines).forEach(this::loadEngine);
    }

    protected void loadEngine(KogitoEngine engine) {
        if(engine != null) {
            engineMap.put(engine.getClass(), engine);
        }
    }

    protected void setConfig(Config config) {
        this.config = config;

        if (config() != null && config().get(ProcessConfig.class) != null) {
            unitOfWorkManager().eventManager().setAddons(config().addons());
        }
    }

    @Override
    public UnitOfWorkManager unitOfWorkManager() {
        return config().get(ProcessConfig.class).unitOfWorkManager();
    }

}
