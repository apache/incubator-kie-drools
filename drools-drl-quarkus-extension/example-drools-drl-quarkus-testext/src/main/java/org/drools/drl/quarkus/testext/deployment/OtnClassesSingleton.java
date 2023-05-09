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
import java.util.Set;

import jakarta.enterprise.event.Observes;
import jakarta.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.StartupEvent;

@Singleton
public class OtnClassesSingleton {
    private static final Logger LOGGER = LoggerFactory.getLogger(OtnClassesSingleton.class);
    
    private Map<String, Set<Class<?>>> patternsTypesClasses;
    private Set<String> allKnown;

    void onStart(@Observes StartupEvent ev) {
        LOGGER.info("The application is starting...");
    }

    public Map<String, Set<Class<?>>> getPatternsTypesClasses() {
        return patternsTypesClasses;
    }

    public void setPatternsTypesClasses(Map<String, Set<Class<?>>> patternsTypesClasses) {
        this.patternsTypesClasses = patternsTypesClasses;
    }

    public Set<String> getAllKnown() {
        return allKnown;
    }

    public void setAllKnown(Set<String> allKnown) {
        this.allKnown = allKnown;
    }
}
