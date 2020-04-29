/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.factmodel.traits;

import org.drools.core.factmodel.traits.TraitClassBuilderImpl;
import org.drools.core.factmodel.traits.TraitFactoryImpl;
import org.drools.core.reteoo.KieComponentFactory;
import org.drools.core.util.StandaloneTraitFactory;
import org.drools.reflective.classloader.ProjectClassLoader;

public class TraitTestUtils {

    public static StandaloneTraitFactory createStandaloneTraitFactory() {
        return new StandaloneTraitFactory(ProjectClassLoader.createProjectClassLoader()) {
            @Override
            protected KieComponentFactory getComponentFactory() {
                KieComponentFactory componentFactory = super.getComponentFactory();
                componentFactory.setTraitFactory(new TraitFactoryImpl());
                componentFactory.getClassBuilderFactory().setTraitBuilder(new TraitClassBuilderImpl());
                return componentFactory;
            }
        };
    }
}
