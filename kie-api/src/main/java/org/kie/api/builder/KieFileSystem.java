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
 * KieFileSystem is an in memory file system used to programmatically define
 * the resources composing a KieModule
 */
public interface KieFileSystem {

    /**
     * Generates a basic maven pom file with the given ReleaseId (groupId, artifactId and version)
     * and adds it to this KieFileSystem
     */
    KieFileSystem generateAndWritePomXML(ReleaseId releaseId);

    /**
     * Adds the given pom.xml file to this KieFileSystem
     */
    KieFileSystem writePomXML(byte[] content);

    /**
     * Adds the given pom.xml file to this KieFileSystem
     */
    KieFileSystem writePomXML(String content);

    /**
     * Adds the given kmodule.xml file to this KieFileSystem
     */
    KieFileSystem writeKModuleXML(byte[] content);

    /**
     * Adds the given kmodule.xml file to this KieFileSystem
     */
    KieFileSystem writeKModuleXML(String content);

    /**
     * Adds the given content to this KieFileSystem in the specified path
     */
    KieFileSystem write(String path, byte[] content);

    /**
     * Adds the given content to this KieFileSystem in the specified path
     */
    KieFileSystem write(String path, String content);

    /**
     * Adds the given Resource to this KieFileSystem in the specified path
     */
    KieFileSystem write(String path, Resource resource);

    /**
     * Adds the given Resource to this KieFileSystem
     */
    KieFileSystem write(Resource resource);

    /**
     * Removes the files in the given paths from this KieFileSystem
     */
    void delete(String... paths);

    /**
     * Returns the content of the file in the specified path as a byte[]
     */
    byte[] read(String path);
}
