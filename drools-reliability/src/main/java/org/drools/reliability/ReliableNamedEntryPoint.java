/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.drools.reliability;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.rule.EntryPointId;
import org.drools.kiesession.entrypoints.NamedEntryPoint;

public class ReliableNamedEntryPoint extends NamedEntryPoint {

    public ReliableNamedEntryPoint(EntryPointId entryPoint,
                           EntryPointNode entryPointNode,
                           ReteEvaluator reteEvaluator) {
        super(entryPoint, entryPointNode, reteEvaluator);
    }

    @Override
    protected void initObjectStore(EntryPointId entryPoint, RuleBaseConfiguration conf, ReteEvaluator reteEvaluator) {
        this.objectStore = new ReliableObjectStore(CacheManager.INSTANCE.getOrCreateCacheForSession(reteEvaluator, "ep" + getEntryPointId()));
    }
}
