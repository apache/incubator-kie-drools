/*
* Copyright 2011 JBoss Inc
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
package org.drools.workbench.models.testscenarios.backend.verifiers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import org.drools.core.base.TypeResolver;
import org.drools.workbench.models.testscenarios.backend.Cheese;
import org.drools.workbench.models.testscenarios.shared.VerifyFact;
import org.drools.workbench.models.testscenarios.shared.VerifyField;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FactVerifierTest {

    private KieSession ksession;

    @Before
    public void setUp() throws Exception {
        ksession = mock( KieSession.class );
    }

    @Test
    public void testVerifyAnonymousFacts() throws Exception {
        TypeResolver typeResolver = mock( TypeResolver.class );
        FactVerifier factVerifier = new FactVerifier( new HashMap<String, Object>(), typeResolver, ksession, new HashMap<String, Object>() );

        Cheese c = new Cheese();
        c.setPrice( 42 );
        c.setType( "stilton" );

        // configure the mock to return the value
        Set o = Collections.singleton( (Object) c );
        when( ksession.getObjects() ).thenReturn( o );

        VerifyFact vf = new VerifyFact( "Cheese",
                                        new ArrayList<VerifyField>(),
                                        true );
        vf.getFieldValues().add( new VerifyField( "price",
                                                  "42",
                                                  "==" ) );
        vf.getFieldValues().add( new VerifyField( "type",
                                                  "stilton",
                                                  "==" ) );

        factVerifier.verify( vf );
        assertTrue( vf.wasSuccessful() );

        vf = new VerifyFact( "Person",
                             new ArrayList<VerifyField>(),
                             true );
        vf.getFieldValues().add( new VerifyField( "age",
                                                  "42",
                                                  "==" ) );

        factVerifier.verify( vf );
        assertFalse( vf.wasSuccessful() );

        vf = new VerifyFact( "Cheese",
                             new ArrayList<VerifyField>(),
                             true );
        vf.getFieldValues().add( new VerifyField( "price",
                                                  "43",
                                                  "==" ) );
        vf.getFieldValues().add( new VerifyField( "type",
                                                  "stilton",
                                                  "==" ) );

        factVerifier.verify( vf );
        assertFalse( vf.wasSuccessful() );
        assertEquals( Boolean.FALSE,
                      vf.getFieldValues().get( 0 ).getSuccessResult() );

        vf = new VerifyFact( "Cell",
                             new ArrayList<VerifyField>(),
                             true );
        vf.getFieldValues().add( new VerifyField( "value",
                                                  "43",
                                                  "==" ) );

        factVerifier.verify( vf );
        assertFalse( vf.wasSuccessful() );
        assertEquals( Boolean.FALSE,
                      vf.getFieldValues().get( 0 ).getSuccessResult() );

    }

    @Test
    public void testVerifyFactsWithOperator() throws Exception {
        TypeResolver typeResolver = mock( TypeResolver.class );

        Cheese f1 = new Cheese( "cheddar",
                                42 );
        HashMap<String, Object> populatedData = new HashMap<String, Object>();
        populatedData.put( "f1", f1 );

        // configure the mock to return the value
        Set o = Collections.singleton( (Object) f1);
        when( ksession.getObjects() ).thenReturn( o );

        FactVerifier factVerifier = new FactVerifier( populatedData, typeResolver, ksession, new HashMap<String, Object>() );

        // test all true
        VerifyFact vf = new VerifyFact();
        vf.setName( "f1" );
        vf.getFieldValues().add( new VerifyField( "type",
                                                  "cheddar",
                                                  "==" ) );
        vf.getFieldValues().add( new VerifyField( "price",
                                                  "4777",
                                                  "!=" ) );

        factVerifier.verify( vf );

        for ( int i = 0; i < vf.getFieldValues().size(); i++ ) {
            assertTrue( vf.getFieldValues().get( i ).getSuccessResult() );
        }

        vf = new VerifyFact();
        vf.setName( "f1" );
        vf.getFieldValues().add( new VerifyField( "type",
                                                  "cheddar",
                                                  "!=" ) );
        factVerifier.verify( vf );
        assertFalse( vf.getFieldValues().get( 0 ).getSuccessResult() );

    }

    @Test
    public void testVerifyFactsWithExpression() throws Exception {
        TypeResolver typeResolver = mock( TypeResolver.class );

        Cheese f1 = new Cheese( "cheddar",
                                42 );
        f1.setPrice( 42 );

        HashMap<String, Object> populatedData = new HashMap<String, Object>();
        populatedData.put( "f1", f1 );

        // configure the mock to return the value
        Set o = Collections.singleton( (Object) f1 );
        when( ksession.getObjects() ).thenReturn( o );

        FactVerifier factVerifier = new FactVerifier( populatedData, typeResolver, ksession, new HashMap<String, Object>() );

        // test all true
        VerifyFact vf = new VerifyFact();
        vf.setName( "f1" );
        vf.getFieldValues().add( new VerifyField( "price",
                                                  "= 40 + 2",
                                                  "==" ) );
        factVerifier.verify( vf );

        assertTrue( vf.getFieldValues().get( 0 ).getSuccessResult() );
    }

    @Test
    public void testVerifyFactExplanation() throws Exception {
        Cheese f1 = new Cheese();
        f1.setType( null );

        TypeResolver typeResolver = mock( TypeResolver.class );

        HashMap<String, Object> populatedData = new HashMap<String, Object>();
        populatedData.put( "f1", f1 );

        // configure the mock to return the value
        Set o = Collections.singleton( (Object) f1 );
        when( ksession.getObjects() ).thenReturn( o );

        FactVerifier factVerifier = new FactVerifier( populatedData, typeResolver, ksession, new HashMap<String, Object>() );

        VerifyFact vf = new VerifyFact();
        vf.setName( "f1" );
        vf.getFieldValues().add( new VerifyField( "type",
                                                  "boo",
                                                  "!=" ) );

        factVerifier.verify( vf );
        VerifyField vfl = vf.getFieldValues().get( 0 );
        assertEquals( "[f1] field [type] was not [boo].",
                      vfl.getExplanation() );

    }

    @Test
    public void testVerifyFieldAndActualIsNull() throws Exception {
        Cheese f1 = new Cheese();
        f1.setType( null );

        TypeResolver typeResolver = mock( TypeResolver.class );

        HashMap<String, Object> populatedData = new HashMap<String, Object>();
        populatedData.put( "f1", f1 );

        // configure the mock to return the value
        Set o = Collections.singleton( (Object) f1 );
        when( ksession.getObjects() ).thenReturn( o );

        FactVerifier factVerifier = new FactVerifier( populatedData, typeResolver, ksession, new HashMap<String, Object>() );

        VerifyFact vf = new VerifyFact();
        vf.setName( "f1" );
        vf.getFieldValues().add( new VerifyField( "type",
                                                  "boo",
                                                  "==" ) );

        factVerifier.verify( vf );
        VerifyField vfl = vf.getFieldValues().get( 0 );

        assertEquals( "[f1] field [type] was [] expected [boo].",
                      vfl.getExplanation() );
        assertEquals( "boo",
                      vfl.getExpected() );
        assertEquals( "",
                      vfl.getActualResult() );

    }

}
