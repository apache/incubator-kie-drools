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

package org.kie.pmml.pmml_4_2.transformations;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.definition.type.FactType;
import org.kie.pmml.pmml_4_2.DroolsAbstractPMMLTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Ignore
public class AliasedFieldsTest extends DroolsAbstractPMMLTest {

    private static final boolean VERBOSE = true;
    private static final String source = "org/kie/pmml/pmml_4_2/test_derived_fields_alias.xml";
    private static final String packageName = "org.kie.pmml.pmml_4_2.test";



    @Before
    public void setUp() throws Exception {
        setKSession(getModelSession(source, VERBOSE));
        setKbase(getKSession().getKieBase());
    }

    @After
    public void tearDown() {
        getKSession().dispose();
    }

    @Test
    public void testKonst() throws Exception {
        FactType konst = getKbase().getFactType(packageName, "Konst");
        assertNotNull(konst);

        getKSession().fireAllRules();

        System.out.println(reportWMObjects(getKSession()));

        assertEquals(1, getKSession().getObjects().size());

        checkFirstDataFieldOfTypeStatus(konst,true,false, null,8);

        checkGeneratedRules();
    }


    @Test
    public void testAlias() throws Exception {
        FactType alias = getKbase().getFactType( packageName, "AliasAge" );
        FactType aliasmm = getKbase().getFactType( packageName, "AliasAgeMM" );
        assertNotNull( alias );
        assertNotNull( aliasmm );

        getKSession().getEntryPoint( "in_Age" ).insert( 33 );
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus( alias, true, false, null, 33 );

        refreshKSession();

        getKSession().getEntryPoint( "in_Age" ).insert( -1 );
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus( alias, true, true, null, -1 );
        checkFirstDataFieldOfTypeStatus( aliasmm, true, false, null, 99 );

        checkGeneratedRules();
    }





}
