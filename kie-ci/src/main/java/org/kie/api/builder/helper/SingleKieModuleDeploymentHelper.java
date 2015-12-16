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

package org.kie.api.builder.helper;

import java.util.List;

import org.kie.api.builder.KieModule;

public interface SingleKieModuleDeploymentHelper {

    /**
     * General API
     */

    public abstract KieModule createKieJar(String groupId, String artifactId, String version, 
            String kbaseName, String ksessionName,
            List<String> resourceFilePaths);

    public abstract KieModule createKieJar(String groupId, String artifactId, String version, 
            String kbaseName, String ksessionName,
            List<String> resourceFilePaths, List<Class<?>> classesForKjar);

    public abstract KieModule createKieJar(String groupId, String artifactId, String version, 
            String kbaseName, String ksessionName,
            List<String> resourceFilePaths, List<Class<?>> classesForKjar, 
            List<String> dependencies);

    public abstract void createKieJarAndDeployToMaven(String groupId, String artifactId, String version, 
            String kbaseName, String ksessionName,
            List<String> resourceFilePaths);

    public abstract void createKieJarAndDeployToMaven(String groupId, String artifactId, String version, 
            String kbaseName, String ksessionName,
            List<String> resourceFilePaths, List<Class<?>> classesForKjar);

    public abstract void createKieJarAndDeployToMaven(String groupId, String artifactId, String version, 
            String kbaseName, String ksessionName, 
            List<String> resourceFilePaths, List<Class<?>> classesForKjar, 
            List<String> dependencies);

}