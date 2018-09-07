/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.command;

import org.drools.core.command.impl.RegistryContext;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;

public class AbstractNewKieContainerCommand {

    protected KieContainer getKieContainer(RegistryContext context, ReleaseId releaseId) {
        KieContainer kieContainer;
        if (releaseId != null) {
            // use the new API to retrieve the session by ID
            KieServices kieServices = KieServices.Factory.get();
            kieContainer = kieServices.newKieContainer(releaseId);
        } else {
            kieContainer = context.lookup(KieContainer.class);
            if (kieContainer == null) {
                throw new RuntimeException("ReleaseId was not specified, nor was an existing KieContainer assigned to the Registry");
            }
        }
        return kieContainer;
    }
}
