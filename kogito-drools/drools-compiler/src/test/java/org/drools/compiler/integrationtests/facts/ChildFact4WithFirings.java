/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.integrationtests.facts;

import java.util.ArrayList;
import java.util.List;

public class ChildFact4WithFirings {

    private final int id;
    private final int parentId;
    private final List<String> firings;

    private String evaluationName;

    public ChildFact4WithFirings(final int id, final int parentId) {
        this.id = id;
        this.parentId = parentId;
        this.firings = new ArrayList<>();
        this.evaluationName = "";
    }

    public int getId() {
        return id;
    }

    public int getParentId() {
        return parentId;
    }

    public List<String> getFirings() {
        return firings;
    }

    public String getEvaluationName() {
        return evaluationName;
    }

    public void setEvaluationName(final String evaluationName) {
        this.evaluationName = evaluationName;
    }
}
