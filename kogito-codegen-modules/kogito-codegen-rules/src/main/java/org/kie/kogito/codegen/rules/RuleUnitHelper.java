/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.codegen.rules;

import org.drools.ruleunits.impl.AssignableChecker;
import org.drools.ruleunits.impl.ReflectiveRuleUnitDescription;
import org.kie.internal.ruleunit.RuleUnitDescription;

public class RuleUnitHelper {
    private AssignableChecker defaultChecker;
    private AssignableChecker assignableChecker;

    public RuleUnitHelper() {
    }

    public RuleUnitHelper(ClassLoader cl, boolean hotReloadMode) {
        this.defaultChecker = AssignableChecker.create(cl, hotReloadMode);
    }

    public RuleUnitHelper(AssignableChecker assignableChecker) {
        this.assignableChecker = assignableChecker;
    }

    void initRuleUnitHelper(RuleUnitDescription ruleUnitDesc) {
        if (ruleUnitDesc instanceof ReflectiveRuleUnitDescription) {
            assignableChecker = ((ReflectiveRuleUnitDescription) ruleUnitDesc).getAssignableChecker();
        } else {
            if (assignableChecker == null) {
                assignableChecker = defaultChecker;
            }
        }
    }

    public AssignableChecker getAssignableChecker() {
        return assignableChecker;
    }

    public boolean isAssignableFrom(Class<?> source, Class<?> target) {
        return assignableChecker.isAssignableFrom(source, target);
    }
}
