/*
 * Copyright (C) 2017 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.models.datamodel.oracle;

import org.drools.workbench.models.datamodel.rule.DSLSentence;
import org.kie.soup.project.datamodel.oracle.ExtensionKind;

public class DSLActionSentence implements ExtensionKind<DSLSentence> {

    public static final DSLActionSentence INSTANCE = new DSLActionSentence();

    @Override
    public boolean equals(Object obj) {
        return obj instanceof DSLActionSentence;
    }

    @Override
    public int hashCode() {
        return DSLActionSentence.class.hashCode();
    }
}
