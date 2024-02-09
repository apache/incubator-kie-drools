/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.pmml.api.identifiers;

import java.util.Map;

import org.kie.efesto.common.api.identifiers.ComponentRoot;
import org.kie.efesto.common.api.identifiers.EfestoComponentRoot;

import static org.kie.efesto.common.api.utils.EfestoAppRootHelper.getComponentRootBySPI;

public class KiePmmlComponentRoot implements EfestoComponentRoot {

    private static final Map<Class<? extends ComponentRoot>, ComponentRoot> INSTANCES;

    static {
        INSTANCES = getComponentRootBySPI(PmmlComponentRoot.class);
    }

    @Override
    public <T extends ComponentRoot> T get(Class<T> providerId) {
        return (T) INSTANCES.get(providerId);
    }
}
