/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.process.meta;

import org.kie.kogito.event.cloudevents.extension.KogitoProcessExtension;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;

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
            meta.setKogitoProcessInstanceState(String.valueOf(pi.getState()));
            meta.setKogitoReferenceId(pi.getReferenceId());
            meta.setKogitoBusinessKey(pi.getBusinessKey());
        }
        return meta;
    }

}
