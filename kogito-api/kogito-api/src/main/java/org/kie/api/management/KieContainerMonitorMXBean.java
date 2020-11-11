/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.api.management;

public interface KieContainerMonitorMXBean {
    public static final GAV CLASSPATH_KIECONTAINER_RELEASEID = new GAV("classpath", "classpath", "0.0.0");

    String getContainerId();

    /**
     * The RelaseId configured while creating the KieContainer.
     * <p />
     * If the KieContainer has been created from Classpath instead, the hardcoded value {@link #CLASSPATH_KIECONTAINER_RELEASEID} will be returned. 
     * @return
     */
    GAV getConfiguredReleaseId();

    /**
     * The RelaseId configured while creating the KieContainer.
     * <p />
     * If the KieContainer has been created from Classpath instead, the hardcoded value {@link #CLASSPATH_KIECONTAINER_RELEASEID} will be returned. 
     * @return
     */
    String getConfiguredReleaseIdStr();

    /**
     * The actual resolved ReleaseId.
     * <p />
     * If the KieContainer has been created from Classpath instead, the hardcoded value {@link #CLASSPATH_KIECONTAINER_RELEASEID} will be returned. 
     * @return
     */
    GAV getResolvedReleaseId();

    /**
     * The actual resolved ReleaseId.
     * <p />
     * If the KieContainer has been created from Classpath instead, the hardcoded value {@link #CLASSPATH_KIECONTAINER_RELEASEID} will be returned. 
     * @return
     */
    String getResolvedReleaseIdStr();
}
