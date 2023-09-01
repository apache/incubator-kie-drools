/**
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
package org.kie.api.builder;

import org.kie.api.io.Resource;


/**
 * KieRepository is a singleton acting as a repository for all the available KieModules
 * regardless if they are stored in the maven repository or programmatically built by the user
 */
public interface KieRepository {

    /**
     * Returns the default ReleaseId used to identify a KieModule in this KieRepository
     * if the user didn't explicitly provide one
     * @return The default ReleaseId
     */
    ReleaseId getDefaultReleaseId();

    /**
     * Adds a new KieModule to this KieRepository
     */
    void addKieModule(KieModule kModule);

    /**
     * Creates a new KieModule using the provided resource and dependencies
     * and automatically adds it to this KieRepository
     * @param resource
     * @param dependencies
     * @return The newly created KieModule
     */
    KieModule addKieModule(Resource resource, Resource... dependencies);

    /**
     * Retrieve a KieModule with the given ReleaseId in this KieRepository.
     * It is possible to use maven's conventions and version ranges like in
     * <pre>
     *     KieModule kieModule = kieRepository.getKieModule( KieServices.Factory.get().newReleaseId("group", "artifact", "LATEST") );
     * </pre>
     * or
     * <pre>
     *     KieModule kieModule = kieRepository.getKieModule( KieServices.Factory.get().newReleaseId("group", "artifact", "[1.0,1.2)") );
     * </pre>
     * @param releaseId The releaseId identifying the KieModule to be returned
     * @return The KieModule identified by the given releaseId or null if such KieModule doesn't exist
     */
    KieModule getKieModule(ReleaseId releaseId);

    /**
     * Remove a no longer useful KieModule, identified by the given ReleaseId, from this KieRepository
     * @param releaseId The releaseId identifying the KieModule to be removed
     * @return The removed KieModule or null if such KieModule didn't exist
     */
    KieModule removeKieModule(ReleaseId releaseId);
}
