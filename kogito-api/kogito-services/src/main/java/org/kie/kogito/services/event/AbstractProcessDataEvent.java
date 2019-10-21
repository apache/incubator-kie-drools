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

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.kie.kogito.event.DataEvent;

public abstract class AbstractProcessDataEvent<T> implements DataEvent<T> {
    
    private static final String SPEC_VERSION = "0.3";

    protected String specversion;
    protected String id;
    protected String source;
    protected String type;
    protected String time;
    protected T data;

    protected String kogitoProcessinstanceId;
    protected String kogitoParentProcessinstanceId;
    protected String kogitoRootProcessinstanceId;
    protected String kogitoProcessId;
    protected String kogitoRootProcessId;
    protected String kogitoProcessinstanceState;
    
    protected String kogitoReferenceId;
    
    public AbstractProcessDataEvent(String source, 
                                    T body,
                                    String kogitoProcessinstanceId,
                                    String kogitoParentProcessinstanceId,
                                    String kogitoRootProcessinstanceId,
                                    String kogitoProcessId,
                                    String kogitoRootProcessId,
                                    String kogitoProcessinstanceState) {
        
        this.specversion = SPEC_VERSION;
        this.id = UUID.randomUUID().toString();
        this.source = source;
        this.type = this.getClass().getSimpleName();
        this.time = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        this.data = body;

        this.kogitoProcessinstanceId = kogitoProcessinstanceId;
        this.kogitoParentProcessinstanceId = kogitoParentProcessinstanceId;
        this.kogitoRootProcessinstanceId = kogitoRootProcessinstanceId;
        this.kogitoProcessId = kogitoProcessId;
        this.kogitoRootProcessId = kogitoRootProcessId;
        this.kogitoProcessinstanceState = kogitoProcessinstanceState;            
    }

    public AbstractProcessDataEvent(String type,
                             String source, 
                             T body,
                             String kogitoProcessinstanceId,
                             String kogitoParentProcessinstanceId,
                             String kogitoRootProcessinstanceId,
                             String kogitoProcessId,
                             String kogitoRootProcessId,
                             String kogitoProcessinstanceState) {
        this.specversion = SPEC_VERSION;
        this.id = UUID.randomUUID().toString();
        this.source = source;
        this.type = type;
        this.time = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        this.data = body;

        this.kogitoProcessinstanceId = kogitoProcessinstanceId;
        this.kogitoParentProcessinstanceId = kogitoParentProcessinstanceId;
        this.kogitoRootProcessinstanceId = kogitoRootProcessinstanceId;
        this.kogitoProcessId = kogitoProcessId;
        this.kogitoRootProcessId = kogitoRootProcessId;
        this.kogitoProcessinstanceState = kogitoProcessinstanceState;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public String getSpecversion() {
        return specversion;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getTime() {
        return time;
    }

    @Override
    public T getData() {        
        return data;
    }

    public String getKogitoProcessinstanceId() {
        return kogitoProcessinstanceId;
    }

    public String getKogitoParentProcessinstanceId() {
        return kogitoParentProcessinstanceId;
    }

    public String getKogitoRootProcessinstanceId() {
        return kogitoRootProcessinstanceId;
    }

    public String getKogitoProcessId() {
        return kogitoProcessId;
    }
    
    public String getKogitoRootProcessId() {
        return kogitoRootProcessId;
    }

    public String getKogitoProcessinstanceState() {
        return kogitoProcessinstanceState;
    }

    public String getKogitoReferenceId() {
        return this.kogitoReferenceId;
    }
    
    public void setKogitoReferenceId(String kogitoReferenceId) {
        this.kogitoReferenceId = kogitoReferenceId;
    }
}
