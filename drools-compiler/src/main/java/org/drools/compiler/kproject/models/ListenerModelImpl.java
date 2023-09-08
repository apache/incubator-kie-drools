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
package org.drools.compiler.kproject.models;

import org.kie.api.builder.model.ListenerModel;
import org.kie.api.builder.model.QualifierModel;

public class ListenerModelImpl implements ListenerModel {

    private KieSessionModelImpl kSession;
    private String type;
    private ListenerModel.Kind kind;
    private QualifierModel qualifier;

    public ListenerModelImpl() { }

    public ListenerModelImpl(KieSessionModelImpl kSession, String type, ListenerModel.Kind kind) {
        this.kSession = kSession;
        this.type = type;
        this.kind = kind;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ListenerModel.Kind getKind() {
        return kind;
    }

    public void setKind(ListenerModel.Kind kind) {
        this.kind = kind;
    }

    public QualifierModel getQualifierModel() {
        return qualifier;
    }

    public void setQualifierModel(QualifierModel qualifier) {
        this.qualifier = qualifier;
    }

    public QualifierModel newQualifierModel(String type) {
        QualifierModelImpl qualifier = new QualifierModelImpl(type);
        this.qualifier = qualifier;
        return qualifier;
    }

    public KieSessionModelImpl getKSession() {
        return kSession;
    }

    public void setKSession(KieSessionModelImpl kSession) {
        this.kSession = kSession;
    }
}
