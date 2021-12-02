/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.wiring.statics;

import org.drools.wiring.statics.StaticServiceRegistry;

public class KogitoStaticServiceRegistry extends StaticServiceRegistry {

    static final KogitoStaticServiceRegistry INSTANCE = new KogitoStaticServiceRegistry();

    protected void wireServices() {
        super.wireServices();
        registerService("org.kie.kogito.rules.DataSource$Factory", "org.kie.kogito.rules.units.impl.DataSourceFactoryImpl", false);
        registerService("org.kie.internal.ruleunit.RuleUnitComponentFactory", "org.kie.kogito.rules.units.impl.RuleUnitComponentFactoryImpl", false);

        registerService("org.drools.core.reteoo.RuntimeComponentFactory", "org.drools.core.kogito.factory.KogitoRuntimeComponentFactory", false);
    }
}
