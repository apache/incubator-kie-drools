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

package org.kie.api.runtime.manager;

import org.kie.api.builder.ReleaseId;

public interface RuntimeEnvironmentBuilderFactory {

	/**
     * Provides completely empty <code>RuntimeEnvironmentBuilder</code> instance that allows to manually
     * set all required components instead of relying on any defaults.
     * @return new instance of <code>RuntimeEnvironmentBuilder</code>
     */
    public RuntimeEnvironmentBuilder newEmptyBuilder();
    
    /**
     * Provides default configuration of <code>RuntimeEnvironmentBuilder</code> that is based on:
     * <ul>
     * 	<li>DefaultRuntimeEnvironment</li>
     * </ul>
     * @return new instance of <code>RuntimeEnvironmentBuilder</code> that is already preconfigured with defaults
     * 
     * see DefaultRuntimeEnvironment
     */
    public RuntimeEnvironmentBuilder newDefaultBuilder();
    
    /**
     * Provides default configuration of <code>RuntimeEnvironmentBuilder</code> that is based on:
     * <ul>
     * 	<li>DefaultRuntimeEnvironment</li>
     * </ul>
     * but it does not have persistence for process engine configured so it will only store process instances in memory
     * @return new instance of <code>RuntimeEnvironmentBuilder</code> that is already preconfigured with defaults
     * 
     * see DefaultRuntimeEnvironment
     */
    public RuntimeEnvironmentBuilder newDefaultInMemoryBuilder();
    
    /**
     * Provides default configuration of <code>RuntimeEnvironmentBuilder</code> that is based on:
     * <ul>
     * 	<li>DefaultRuntimeEnvironment</li>
     * </ul>
     * This one is tailored to works smoothly with kjars as the notion of kbase and ksessions
     * @param groupId group id of kjar
     * @param artifactId artifact id of kjar
     * @param version version number of kjar
     * @return new instance of <code>RuntimeEnvironmentBuilder</code> that is already preconfigured with defaults
     * 
     * see DefaultRuntimeEnvironment
     */
    public RuntimeEnvironmentBuilder newDefaultBuilder(String groupId, String artifactId, String version);
    
    /**
     * Provides default configuration of <code>RuntimeEnvironmentBuilder</code> that is based on:
     * <ul>
     * 	<li>DefaultRuntimeEnvironment</li>
     * </ul>
     * This one is tailored to works smoothly with kjars as the notion of kbase and ksessions
     * @param groupId group id of kjar
     * @param artifactId artifact id of kjar
     * @param version version number of kjar
     * @param kbaseName name of the kbase defined in kmodule.xml stored in kjar
     * @param ksessionName name of the ksession define in kmodule.xml stored in kjar
     * @return new instance of <code>RuntimeEnvironmentBuilder</code> that is already preconfigured with defaults
     * 
     * see DefaultRuntimeEnvironment
     */
    public RuntimeEnvironmentBuilder newDefaultBuilder(String groupId, String artifactId, String version, String kbaseName, String ksessionName);
    
    /**
     * Provides default configuration of <code>RuntimeEnvironmentBuilder</code> that is based on:
     * <ul>
     * 	<li>DefaultRuntimeEnvironment</li>
     * </ul>
     * This one is tailored to works smoothly with kjars as the notion of kbase and ksessions
     * @param releaseId <code>ReleaseId</code> that described the kjar
     * @return new instance of <code>RuntimeEnvironmentBuilder</code> that is already preconfigured with defaults
     * 
     * see DefaultRuntimeEnvironment
     */
    public RuntimeEnvironmentBuilder newDefaultBuilder(ReleaseId releaseId);
    
    /**
     * Provides default configuration of <code>RuntimeEnvironmentBuilder</code> that is based on:
     * <ul>
     * 	<li>DefaultRuntimeEnvironment</li>
     * </ul>
     * This one is tailored to works smoothly with kjars as the notion of kbase and ksessions
     * @param releaseId <code>ReleaseId</code> that described the kjar
     * @param kbaseName name of the kbase defined in kmodule.xml stored in kjar
     * @param ksessionName name of the ksession define in kmodule.xml stored in kjar
     * @return new instance of <code>RuntimeEnvironmentBuilder</code> that is already preconfigured with defaults
     * 
     * see DefaultRuntimeEnvironment
     */
    public RuntimeEnvironmentBuilder newDefaultBuilder(ReleaseId releaseId, String kbaseName, String ksessionName);
    
    /**
     * Provides default configuration of <code>RuntimeEnvironmentBuilder</code> that is based on:
     * <ul>
     * 	<li>DefaultRuntimeEnvironment</li>
     * </ul>
     * It relies on KieClasspathContainer that requires to have kmodule.xml present in META-INF folder which 
     * defines the kjar itself.
     * Expects to use default kbase and ksession from kmodule.
     * @return new instance of <code>RuntimeEnvironmentBuilder</code> that is already preconfigured with defaults
     * 
     * see DefaultRuntimeEnvironment
     */
    public RuntimeEnvironmentBuilder newClasspathKmoduleDefaultBuilder();
    
    /**
     * Provides default configuration of <code>RuntimeEnvironmentBuilder</code> that is based on:
     * <ul>
     * 	<li>DefaultRuntimeEnvironment</li>
     * </ul>
     * It relies on KieClasspathContainer that requires to have kmodule.xml present in META-INF folder which 
     * defines the kjar itself.
     * @param kbaseName name of the kbase defined in kmodule.xml
     * @param ksessionName name of the ksession define in kmodule.xml   
     * @return new instance of <code>RuntimeEnvironmentBuilder</code> that is already preconfigured with defaults
     * 
     * see DefaultRuntimeEnvironment
     */
    public RuntimeEnvironmentBuilder newClasspathKmoduleDefaultBuilder(String kbaseName, String ksessionName);
}
