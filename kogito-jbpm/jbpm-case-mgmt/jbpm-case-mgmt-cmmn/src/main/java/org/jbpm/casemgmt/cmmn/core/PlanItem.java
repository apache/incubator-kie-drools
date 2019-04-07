/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.casemgmt.cmmn.core;

import java.io.Serializable;

public class PlanItem implements Serializable {

    private static final long serialVersionUID = 4L;

    private String id;
    private String definitionRef;

    private Sentry entryCriterion;
    private Sentry exitCriterion;

    public PlanItem(String id, String definitionRef) {
        this.id = id;
        this.definitionRef = definitionRef;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDefinitionRef() {
        return definitionRef;
    }

    public void setDefinitionRef(String definitionRef) {
        this.definitionRef = definitionRef;
    }

    public Sentry getEntryCriterion() {
        return entryCriterion;
    }

    public void setEntryCriterion(Sentry entryCriterion) {
        this.entryCriterion = entryCriterion;
    }

    public Sentry getExitCriterion() {
        return exitCriterion;
    }

    public void setExitCriterion(Sentry exitCriterion) {
        this.exitCriterion = exitCriterion;
    }

    @Override
    public String toString() {
        return "PlanItem [id=" + id + ", definitionRef=" + definitionRef + ", entryCriterion=" + entryCriterion + ", exitCriterion=" + exitCriterion + "]";
    }

}
