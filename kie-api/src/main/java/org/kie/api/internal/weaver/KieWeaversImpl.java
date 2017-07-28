/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.api.internal.weaver;

import java.util.HashMap;
import java.util.Map;

import org.kie.api.io.ResourceType;

public class KieWeaversImpl implements KieWeavers {
    private Map<ResourceType, KieWeaverService> weavers;

    public KieWeaversImpl() {
        weavers = new HashMap<ResourceType, KieWeaverService>();
    }

    public Map<ResourceType, KieWeaverService> getWeavers() {
        return this.weavers;
    }
}
