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
package org.drools.kiesession.management;

import javax.management.ObjectName;

import org.drools.core.common.InternalWorkingMemoryActions;
import org.drools.core.management.DroolsManagementAgent;
import org.drools.core.management.GenericKieSessionMonitoringImpl;
import org.kie.api.builder.model.KieSessionModel.KieSessionType;
import org.kie.api.event.KieRuntimeEventManager;
import org.kie.api.management.KieSessionMonitoringMXBean;

public class KieSessionMonitoringImpl extends GenericKieSessionMonitoringImpl implements KieSessionMonitoringMXBean {
    
    private ObjectName name;

    public KieSessionMonitoringImpl(String containerId, String kbaseId, String ksessionName) {
        super(containerId, kbaseId, ksessionName);
        
        this.name = DroolsManagementAgent.createObjectNameBy(containerId, kbaseId, KieSessionType.STATEFUL, ksessionName);
    }
    
    @Override
    public ObjectName getName() {
        return this.name;
    }

    @Override
    public long getTotalSessions() {
        return ksessions.size();
    }

    @Override
    public long getTotalFactCount() {
        long result = 0;
        for (KieRuntimeEventManager s : ksessions) {
            result += ((InternalWorkingMemoryActions) s).getFactCount();
        }
        return result;
    }
}
