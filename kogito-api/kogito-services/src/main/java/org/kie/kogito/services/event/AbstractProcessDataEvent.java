/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.services.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.kie.kogito.event.AbstractDataEvent;

public abstract class AbstractProcessDataEvent<T> extends AbstractDataEvent<T> {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected String kogitoParentProcessinstanceId;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected String kogitoProcessinstanceState;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected String kogitoReferenceId;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected String kogitoStartFromNode;

    public AbstractProcessDataEvent() {}

    public AbstractProcessDataEvent(String source,
                                    T body,
                                    String kogitoProcessinstanceId,
                                    String kogitoParentProcessinstanceId,
                                    String kogitoRootProcessinstanceId,
                                    String kogitoProcessId,
                                    String kogitoRootProcessId,
                                    String kogitoProcessinstanceState,
                                    String kogitoAddons) {
        this(null,
             source,
             body,
             kogitoProcessinstanceId,
             kogitoParentProcessinstanceId,
             kogitoRootProcessinstanceId,
             kogitoProcessId,
             kogitoRootProcessId,
             kogitoProcessinstanceState,
             kogitoAddons);
    }

    public AbstractProcessDataEvent(String type,
                                    String source,
                                    T body,
                                    String kogitoProcessinstanceId,
                                    String kogitoParentProcessinstanceId,
                                    String kogitoRootProcessinstanceId,
                                    String kogitoProcessId,
                                    String kogitoRootProcessId,
                                    String kogitoProcessinstanceState,
                                    String kogitoAddons) {
        this(
            type,
            source,
            body,
            kogitoProcessinstanceId,
            kogitoParentProcessinstanceId,
            kogitoRootProcessinstanceId,
            kogitoProcessId,
            kogitoRootProcessId,
            kogitoProcessinstanceState,
            kogitoAddons,
            null);
    }

    public AbstractProcessDataEvent(String type,
                                    String source,
                                    T body,
                                    String kogitoProcessinstanceId,
                                    String kogitoParentProcessinstanceId,
                                    String kogitoRootProcessinstanceId,
                                    String kogitoProcessId,
                                    String kogitoRootProcessId,
                                    String kogitoProcessinstanceState,
                                    String kogitoAddons,
                                    String kogitoReferenceId) {
        super(type,
              source,
              body,
              kogitoProcessinstanceId,
              kogitoRootProcessinstanceId,
              kogitoProcessId,
              kogitoRootProcessId,
              kogitoAddons);
        this.kogitoParentProcessinstanceId = kogitoParentProcessinstanceId;
        this.kogitoProcessinstanceState = kogitoProcessinstanceState;
        this.kogitoReferenceId = kogitoReferenceId;
    }

    public String getKogitoParentProcessinstanceId() {
        return kogitoParentProcessinstanceId;
    }

    public String getKogitoProcessinstanceState() {
        return kogitoProcessinstanceState;
    }

    public String getKogitoReferenceId() {
        return this.kogitoReferenceId;
    }

    public void setKogitoStartFromNode(String kogitoStartFromNode) {
        this.kogitoStartFromNode = kogitoStartFromNode;
    }

    public String getKogitoStartFromNode() {
        return this.kogitoStartFromNode;
    }
}
