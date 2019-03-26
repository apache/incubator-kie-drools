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

package org.drools.core.datasources;

import org.drools.core.WorkingMemoryEntryPoint;
import org.kie.api.runtime.rule.RuleUnit;

public class BindableArray implements BindableDataProvider {

    private final Object[] objects;

    public BindableArray( Object[] objects ) {
        this.objects = objects;
    }

    @Override
    public void bind( RuleUnit unit, WorkingMemoryEntryPoint ep ) {
        for (Object obj : objects) {
            ep.insert( obj );
        }
    }
}
