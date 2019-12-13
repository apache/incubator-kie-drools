/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.rules.units;

import org.drools.core.base.DefaultKnowledgeHelper;

public class RuleUnitKnowledgeHelper extends DefaultKnowledgeHelper {

    public void run(String ruleUnitName) {
        throw new UnsupportedOperationException();
    }

    public void run(Object ruleUnit) {
        throw new UnsupportedOperationException();
    }

    public void run(Class<?> ruleUnitClass) {
        throw new UnsupportedOperationException();
    }

    public void guard(Object ruleUnit) {
        throw new UnsupportedOperationException();
    }

    public void guard(Class<?> ruleUnitClass) {
        throw new UnsupportedOperationException();
    }
}
