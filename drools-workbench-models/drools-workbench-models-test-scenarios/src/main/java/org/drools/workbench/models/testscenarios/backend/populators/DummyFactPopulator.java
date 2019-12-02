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

package org.drools.workbench.models.testscenarios.backend.populators;

import java.util.HashMap;
import java.util.Map;

import org.drools.workbench.models.testscenarios.shared.FactData;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.drools.core.addon.TypeResolver;

public class DummyFactPopulator extends NewFactPopulator {

    public DummyFactPopulator( Map<String, Object> populatedData, TypeResolver typeResolver, FactData fact ) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        super( populatedData, typeResolver, fact );
    }

    @Override
    public void populate( KieSession ksession, Map<String, FactHandle> factHandles) { }

    public static Map<String, Object> factDataToObjects(TypeResolver typeResolver, FactData factData) {
        Map<String, Object> populatedData = new HashMap<>();
        FactPopulator factPopulator = new FactPopulator(populatedData);
        try {
            factPopulator.add(new DummyFactPopulator(populatedData, typeResolver, factData));
            factPopulator.populate();
        } catch (Exception e) {
            throw new RuntimeException( e );
        }
        return populatedData;
    }
}
