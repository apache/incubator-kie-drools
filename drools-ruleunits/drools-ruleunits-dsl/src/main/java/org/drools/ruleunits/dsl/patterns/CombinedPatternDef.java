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
package org.drools.ruleunits.dsl.patterns;

import org.drools.model.Condition;
import org.drools.model.view.CombinedExprViewItem;
import org.drools.model.view.ViewItem;

public class CombinedPatternDef implements InternalPatternDef {

    private final Condition.Type type;
    private final InternalPatternDef[] patternDefs;

    public CombinedPatternDef(Condition.Type type, InternalPatternDef... patternDefs) {
        this.type = type;
        this.patternDefs = patternDefs;
    }

    @Override
    public ViewItem toExecModelItem() {
        ViewItem[] expressions = new ViewItem[patternDefs.length];
        for (int i = 0; i < patternDefs.length; i++) {
            expressions[i] = patternDefs[i].toExecModelItem();
        }
        return new CombinedExprViewItem(type, expressions);
    }
}
