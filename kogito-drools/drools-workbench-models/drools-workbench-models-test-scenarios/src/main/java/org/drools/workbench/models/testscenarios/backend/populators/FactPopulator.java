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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

public class FactPopulator {

    private Map<String, Populator> toBePopulatedData = new HashMap<String, Populator>();

    private final Map<String, Object> populatedData;
    private final Map<String, FactHandle> factHandles = new HashMap<String, FactHandle>();

    private final KieSession ksession;

    public FactPopulator( KieSession ksession,
                          Map<String, Object> populatedData ) {
        this.ksession = ksession;
        this.populatedData = populatedData;
    }

    public void populate() throws ClassNotFoundException,
            InstantiationException,
            IllegalAccessException,
            InvocationTargetException,
            NoSuchMethodException {
        List<FieldPopulator> fieldPopulators = new ArrayList<FieldPopulator>();

        for ( Populator populator : toBePopulatedData.values() ) {
            fieldPopulators.addAll( populator.getFieldPopulators() );
        }

        for ( FieldPopulator fieldPopulator : fieldPopulators ) {
            fieldPopulator.populate( populatedData );
        }

        for ( Populator populator : toBePopulatedData.values() ) {
            populator.populate( ksession,
                                factHandles );
        }

        toBePopulatedData.clear();
    }

    public void retractFact( String retractFactName ) {
        this.ksession.delete( this.factHandles.get( retractFactName ) );
        this.populatedData.remove( retractFactName );
    }

    public void add( Populator factPopulator ) {
        toBePopulatedData.put( factPopulator.getName(),
                               factPopulator );
    }
}
