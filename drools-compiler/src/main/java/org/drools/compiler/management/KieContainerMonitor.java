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
package org.drools.compiler.management;

import org.drools.core.impl.InternalKieContainer;
import org.kie.api.management.GAV;
import org.kie.api.management.KieContainerMonitorMXBean;

public class KieContainerMonitor implements KieContainerMonitorMXBean {
    private InternalKieContainer kieContainer;

    public KieContainerMonitor(InternalKieContainer kieContainer) {
        this.kieContainer = kieContainer;
    }

    @Override
    public String getContainerId() {
        return kieContainer.getContainerId();
    }

    @Override
    public String getConfiguredReleaseIdStr() {
        return ( kieContainer.getConfiguredReleaseId() != null )
                ? kieContainer.getConfiguredReleaseId().toString()
                : KieContainerMonitorMXBean.CLASSPATH_KIECONTAINER_RELEASEID.toString() ;
    }

    @Override
    public String getResolvedReleaseIdStr() {
        return ( kieContainer.getResolvedReleaseId() != null )
                ? kieContainer.getResolvedReleaseId().toString()
                : KieContainerMonitorMXBean.CLASSPATH_KIECONTAINER_RELEASEID.toString() ;
    }

    @Override
    public GAV getConfiguredReleaseId() {
        return ( kieContainer.getConfiguredReleaseId() != null )
                ? GAV.from(kieContainer.getConfiguredReleaseId())
                : KieContainerMonitorMXBean.CLASSPATH_KIECONTAINER_RELEASEID ;
    }

    @Override
    public GAV getResolvedReleaseId() {
        return ( kieContainer.getResolvedReleaseId() != null )
                ? GAV.from(kieContainer.getResolvedReleaseId())
                : KieContainerMonitorMXBean.CLASSPATH_KIECONTAINER_RELEASEID ;
    }
}
