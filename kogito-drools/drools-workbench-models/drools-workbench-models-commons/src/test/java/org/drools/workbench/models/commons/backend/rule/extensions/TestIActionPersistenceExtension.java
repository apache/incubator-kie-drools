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

package org.drools.workbench.models.commons.backend.rule.extensions;

import org.drools.workbench.models.commons.backend.rule.RuleModelIActionPersistenceExtension;
import org.drools.workbench.models.commons.backend.rule.actions.TestIAction;
import org.drools.workbench.models.datamodel.rule.IAction;

public class TestIActionPersistenceExtension implements RuleModelIActionPersistenceExtension {

    @Override
    public boolean accept(final IAction iAction) {
        return iAction instanceof TestIAction;
    }

    @Override
    public String marshal(final IAction iAction) {
        return "testIAction();";
    }

    @Override
    public boolean accept(final String iActionString) {
        return iActionString != null && iActionString.trim().equals("testIAction();");
    }

    @Override
    public IAction unmarshal(final String iActionString) {
        return new TestIAction();
    }
}
