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

package org.drools.drl.quarkus.testext.deployment;

import java.util.Map;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.StartupEvent;

@Singleton
public class GlobalsSingleton {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalsSingleton.class);
    
    private Map<String, Map<String, java.lang.reflect.Type>> globals;

    void onStart(@Observes StartupEvent ev) {
        LOGGER.info("The application is starting...");
    }

    public Map<String, Map<String, java.lang.reflect.Type>> getGlobals() {
        return globals;
    }

    public void setGlobals(Map<String, Map<String, java.lang.reflect.Type>> globals) {
        this.globals = globals;
    }
}
