/*
 * Copyright 2011 JBoss Inc
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

package org.drools.pmml.pmml_4_2.transformations;


import org.drools.pmml.pmml_4_2.DroolsAbstractPMMLTest;
import org.junit.After;
import org.junit.Test;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.rule.FactHandle;

import static org.junit.Assert.assertEquals;


public class UserDefinedFunctionsTest extends DroolsAbstractPMMLTest {

    private static final boolean VERBOSE = false;
    private static final String source0  = "org/drools/pmml/pmml_4_2/test_user_functions_simple.xml";
    private static final String source1  = "org/drools/pmml/pmml_4_2/test_user_functions_nested.xml";
    private static final String source2  = "org/drools/pmml/pmml_4_2/test_user_functions_complex.xml";
    private static final String source3 = "org/drools/pmml/pmml_4_2/test_user_functions_simpleTransformations.xml";
    private static final String packageName = "org.drools.pmml.pmml_4_2.test";

    @After
    public void tearDown() {
        getKSession().dispose();
    }


    @Test
    public void testFunctions0() throws Exception {

        setKSession( getModelSession( source0, VERBOSE ) );
        setKbase( getKSession().getKieBase() );

        FactType userAge1 = getKbase().getFactType( packageName, "UserAge" );

        getKSession().getEntryPoint( "in_Age" ).insert( 2.2 );

        getKSession().fireAllRules();

        System.out.println( reportWMObjects( getKSession() ) );

        checkFirstDataFieldOfTypeStatus( userAge1, true, false, null, 22.0 );
    }

    @Test
    public void testFunctions0Overwrite() throws Exception {

        setKSession( getModelSession( source0, VERBOSE ) );
        setKbase( getKSession().getKieBase() );

        FactType userAge1 = getKbase().getFactType( packageName, "UserAge" );

        getKSession().getEntryPoint( "in_Age" ).insert( 8.4 );

        getKSession().fireAllRules();

        getKSession().getEntryPoint( "in_Age" ).insert( 2.2 );

        getKSession().fireAllRules();

        System.out.println( reportWMObjects( getKSession() ) );

        checkFirstDataFieldOfTypeStatus( userAge1, true, false, null, 22.0 );
    }

    @Test
    public void testFunctions1() throws Exception {

        setKSession( getModelSession( source3, true ) );
        setKbase( getKSession().getKieBase() );

        FactType userAge1 = getKbase().getFactType( packageName, "UserAge1" );

        getKSession().getEntryPoint( "in_Age" ).insert( 10 );

        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus( userAge1, true, false, null, 22.0 );
    }


    @Test
    public void testFunctions2() throws Exception {

        setKSession( getModelSession( source3, VERBOSE ) );
        setKbase( getKSession().getKieBase() );

        FactType userAge2 = getKbase().getFactType( packageName, "UserAge2" );

        getKSession().getEntryPoint( "in_Age" ).insert( 10 );

        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus( userAge2, true, false, null, 0.1 );

    }

    @Test
    public void testFunctions3() throws Exception {

        setKSession( getModelSession( source3, VERBOSE ) );
        setKbase( getKSession().getKieBase() );

        FactType userAge3 = getKbase().getFactType( packageName, "UserAge3" );

        getKSession().getEntryPoint( "in_Age" ).insert( 10 );

        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus( userAge3, true, false, null, 10 );

    }

    @Test
    public void testFunctions4() throws Exception {

        setKSession( getModelSession( source3, VERBOSE ) );
        setKbase( getKSession().getKieBase() );

        FactType userAge4 = getKbase().getFactType( packageName, "UserAge4" );

        getKSession().getEntryPoint( "in_Age" ).insert( 10 );

        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus( userAge4, true, false, null, 24 );

    }

    @Test
    public void testFunctions5() throws Exception {

        setKSession( getModelSession( source3, VERBOSE ) );
        setKbase( getKSession().getKieBase() );

        FactType userAge5 = getKbase().getFactType( packageName, "UserAge5" );

        getKSession().getEntryPoint( "in_Age" ).insert( 10 );

        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus( userAge5, true, false, null, 45.5 );
    }

    @Test
    public void testFunctions6() throws Exception {

        setKSession( getModelSession( source3, VERBOSE ) );
        setKbase( getKSession().getKieBase() );

        FactType userAge6 = getKbase().getFactType( packageName, "UserAge6" );

        getKSession().getEntryPoint( "in_Age" ).insert( 10 );

        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus( userAge6, true, false, null, 1.0 );

    }

    @Test
    public void testFunctionsNested() throws Exception {

        setKSession( getModelSession( source1, VERBOSE ) );
        setKbase( getKSession().getKieBase() );

        FactType userAge1 = getKbase().getFactType( packageName, "UserAge" );

        getKSession().getEntryPoint( "in_Age" ).insert( 10.0 );

        getKSession().fireAllRules();

        System.out.println( reportWMObjects( getKSession() ) );

        checkFirstDataFieldOfTypeStatus( userAge1, true, false, null, 130.0 );

    }



    @Test
    public void testComplexFunctionsNested() throws Exception {

        setKSession( getModelSession( source2, true ) );
        setKbase( getKSession().getKieBase() );

        FactType userAge1 = getKbase().getFactType( packageName, "UserAge" );

        getKSession().getEntryPoint( "in_Age" ).insert( 10.0 );

        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus( userAge1, true, false, null, 6270.0 );

    }

    @Test
    public void testComplexFunctionsNested2() throws Exception {

        setKSession( getModelSession( source2, VERBOSE ) );
        setKbase( getKSession().getKieBase() );

        FactType userAge1 = getKbase().getFactType( packageName, "UserAge" );
        FactType userAge2 = getKbase().getFactType( packageName, "UserAgeComplex" );

        FactHandle h = getKSession().getEntryPoint( "in_Age" ).insert( 10.0 );

        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus( userAge1, true, false, null, 6270.0 );
        checkFirstDataFieldOfTypeStatus( userAge2, true, false, null, 44.1 );

        System.out.println( "_________________________________________________________________" );

        FactType age = getKbase().getFactType( packageName, "Age" );
        Object aged = getKSession().getObjects( new ClassObjectFilter( age.getFactClass() ) ).iterator().next();

        getKSession().delete( getKSession().getFactHandle( aged ) );
        getKSession().fireAllRules();

        assertEquals( 0, getKSession().getFactCount() );

        getKSession().getEntryPoint( "in_Age" ).insert( 20.0 );
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus( userAge1, true, false, null, 9570.0 );
        checkFirstDataFieldOfTypeStatus( userAge2, true, false, null, 115.2 );

        getKSession().getEntryPoint( "in_Age" ).insert( 30.0 );
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus( userAge1, true, false, null, 12870.0 );
        checkFirstDataFieldOfTypeStatus( userAge2, true, false, null, 306.3 );

        assertEquals( 4, getKSession().getFactCount() );

    }





}
