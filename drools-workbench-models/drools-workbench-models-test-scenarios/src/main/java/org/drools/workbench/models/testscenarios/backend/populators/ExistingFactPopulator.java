/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.models.testscenarios.backend.populators;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import org.drools.core.base.TypeResolver;
import org.drools.workbench.models.testscenarios.shared.FactData;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

class ExistingFactPopulator extends FactPopulatorBase {

    public ExistingFactPopulator( Map<String, Object> populatedData,
                                  TypeResolver typeResolver,
                                  FactData fact ) throws ClassNotFoundException {
        super( populatedData,
               typeResolver,
               fact );
    }

    protected Object resolveFactObject() throws ClassNotFoundException {
        if ( !populatedData.containsKey( fact.getName() ) ) {
            throw new IllegalArgumentException( "Was not a previously inserted fact. [" + fact.getName() + "]" );
        }
        return populatedData.get( fact.getName() );
    }

    @Override
    public List<FieldPopulator> getFieldPopulators() throws ClassNotFoundException,
            IllegalAccessException,
            InstantiationException,
            InvocationTargetException,
            NoSuchMethodException {
        return getFieldPopulators( resolveFactObject() );
    }

    @Override
    public void populate( KieSession ksession,
                          Map<String, FactHandle> factHandles ) {
        ksession.update( factHandles.get( fact.getName() ),
                         populatedData.get( fact.getName() ) );

    }
}
