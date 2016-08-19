/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.kie.builder.impl;

import org.drools.compiler.kie.builder.impl.event.KieServicesEventListerner;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;

public interface InternalKieServices extends KieServices {
    void registerListener(KieServicesEventListerner listener);

    /**
     * Clear the containerId reference from the internal registry hold by the KieServices.
     * Epsecially helpful to avoid leaking reference on container dispose(), to inadvertently keep a reference in the internal registry which would never be GC.
     */
	void clearRefToContainerId(String containerId, KieContainer containerRef);
}
