/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.kie.api.builder;

import org.kie.api.io.Resource;

/**
 * KieBuilder is a builder for the resources contained in a KieModule
 */
public interface KieBuilder {

    /**
     * Sets the other KieModules from which the KieModule that has to be built by this KieBuilder depends on
     */
    KieBuilder setDependencies(KieModule... dependencies);

    /**
     * Sets the other Resources from which the KieModule that has to be built by this KieBuilder depends on
     */
    KieBuilder setDependencies(Resource... dependencies);

    /**
     * Builds all the KieBases contained in the KieModule for which this KieBuilder has been created
     */
    KieBuilder buildAll();

    /**
     * Returns the Results of the building process.
     * Invokes <code>buildAll()</code> if the KieModule hasn't been built yet
     */
    Results getResults();

    /**
     * Returns the KieModule for which this KieBuilder has been created
     */
    KieModule getKieModule();
}
