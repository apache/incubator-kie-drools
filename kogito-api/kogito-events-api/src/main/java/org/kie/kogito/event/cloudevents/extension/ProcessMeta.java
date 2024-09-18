/*
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
package org.kie.kogito.event.cloudevents.extension;

import java.util.Collections;

import org.kie.kogito.correlation.CompositeCorrelation;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.workitem.KogitoWorkItem;

import static org.kie.kogito.internal.process.runtime.KogitoProcessInstance.STATE_ABORTED;
import static org.kie.kogito.internal.process.runtime.KogitoProcessInstance.STATE_ACTIVE;
import static org.kie.kogito.internal.process.runtime.KogitoProcessInstance.STATE_COMPLETED;
import static org.kie.kogito.internal.process.runtime.KogitoProcessInstance.STATE_ERROR;
import static org.kie.kogito.internal.process.runtime.KogitoProcessInstance.STATE_PENDING;
import static org.kie.kogito.internal.process.runtime.KogitoProcessInstance.STATE_SUSPENDED;

/**
 * Holds Process Metadata Information from a given process.
 * Can be used to export internal process information to external boundaries such as a broker, external system, rest service, and so on.
 */
public class ProcessMeta extends KogitoProcessExtension {

    public ProcessMeta() {
        super();
    }

    public static ProcessMeta fromKogitoWorkItem(final KogitoWorkItem kogitoWorkItem) {
        return fromKogitoProcessInstance(kogitoWorkItem.getProcessInstance());
    }

    public static ProcessMeta fromKogitoProcessInstance(final KogitoProcessInstance pi) {
        final ProcessMeta meta = new ProcessMeta();
        if (pi != null) {
            meta.setKogitoProcessInstanceId(pi.getStringId());
            meta.setKogitoProcessInstanceVersion(pi.getProcess().getVersion());
            meta.setKogitoParentProcessinstanceId(pi.getParentProcessInstanceId());
            meta.setKogitoRootProcessInstanceId(pi.getRootProcessInstanceId());
            meta.setKogitoProcessId(pi.getProcessId());
            meta.setKogitoRootProcessId(pi.getRootProcessId());
            meta.setKogitoProcessInstanceState(fromState(pi.getState()));
            meta.setKogitoReferenceId(pi.getReferenceId());
            meta.setKogitoBusinessKey(pi.getBusinessKey());
            meta.setKogitoProcessType(pi.getProcess().getType());
            if (pi.unwrap() != null) {
                pi.unwrap().correlation().map(c -> c instanceof CompositeCorrelation ? ((CompositeCorrelation) c).getValue() : Collections.singleton(c))
                        .ifPresent(correlations -> correlations.forEach(c -> meta.addExtension(c.getKey(), c.asString())));
            }
        }
        return meta;
    }

    public static String fromState(int state) {
        switch (state) {
            case STATE_ABORTED:
                return "Aborted";
            case STATE_ACTIVE:
                return "Active";
            case STATE_COMPLETED:
                return "Completed";
            case STATE_ERROR:
                return "Error";
            case STATE_PENDING:
                return "Pending";
            case STATE_SUSPENDED:
                return "Suspended";
            default:
                return null;
        }
    }
}
