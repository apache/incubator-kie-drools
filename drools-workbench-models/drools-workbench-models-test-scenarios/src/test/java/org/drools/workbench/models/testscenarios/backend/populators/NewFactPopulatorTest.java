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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.drools.core.base.ClassTypeResolver;
import org.drools.core.base.TypeResolver;
import org.drools.workbench.models.testscenarios.shared.FactData;
import org.drools.workbench.models.testscenarios.shared.Field;
import org.drools.workbench.models.testscenarios.shared.FieldData;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class NewFactPopulatorTest {

    private TypeResolver            typeResolver;
    private HashMap<String, Object> populatedData;
    private KieSession workingMemory;

    @Before
    public void setUp() throws Exception {
        typeResolver = new ClassTypeResolver( new HashSet<String>(),
                                              Thread.currentThread().getContextClassLoader() );
        populatedData = new HashMap<String, Object>();
        workingMemory = mock( KieSession.class );
    }

    @Test
    public void testDummyRunNoRules() throws Exception {
        typeResolver.addImport( "org.drools.workbench.models.testscenarios.backend.Cheese" );

        List<Field> fieldData = new ArrayList<Field>();
        fieldData.add( new FieldData( "type",
                                      "cheddar" ) );
        fieldData.add( new FieldData( "price",
                                      "42" ) );
        FactData fact = new FactData( "Cheese",
                                      "c1",
                                      fieldData,
                                      false );

        NewFactPopulator newFactPopulator = new NewFactPopulator(
                                                                  populatedData,
                                                                  typeResolver,
                                                                  fact );

        newFactPopulator.populate( workingMemory, new HashMap<String, FactHandle>() );

        assertTrue( populatedData.containsKey( "c1" ) );
        assertNotNull( populatedData.get( "c1" ) );
        
        verify( workingMemory ).insert( populatedData.get( "c1" ) );
    }

}
