/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.models.testscenarios.backend.executors;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.drools.workbench.models.testscenarios.shared.CallFieldValue;
import org.drools.workbench.models.testscenarios.shared.CallMethod;
import org.junit.Test;
import org.drools.workbench.models.testscenarios.backend.Cheesery;

public class MethodExecutorTest {

    @Test
    public void testCallMethodNoArgumentOnFact() throws Exception {
        HashMap<String, Object> populatedData = new HashMap<String, Object>();
        MethodExecutor methodExecutor = new MethodExecutor( populatedData );

        Cheesery listChesse = new Cheesery();
        listChesse.setTotalAmount( 1000 );
        populatedData.put( "cheese",
                           listChesse );
        CallMethod mCall = new CallMethod();
        mCall.setVariable( "cheese" );
        mCall.setMethodName( "setTotalAmountToZero" );

        methodExecutor.executeMethod( mCall );

        assertTrue( listChesse.getTotalAmount() == 0 );
    }

    @Test
    public void testCallMethodOnStandardArgumentOnFact() throws Exception {

        HashMap<String, Object> populatedData = new HashMap<String, Object>();
        MethodExecutor methodExecutor = new MethodExecutor( populatedData );

        Cheesery listChesse = new Cheesery();
        listChesse.setTotalAmount( 1000 );
        populatedData.put( "cheese",
                           listChesse );
        CallMethod mCall = new CallMethod();
        mCall.setVariable( "cheese" );
        mCall.setMethodName( "setTotalAmount" );
        CallFieldValue field = new CallFieldValue();
        field.value = "1005";
        mCall.addFieldValue( field );

        methodExecutor.executeMethod( mCall );
        assertTrue( listChesse.getTotalAmount() == 1005 );
    }

    @Test
    public void testCallMethodOnClassArgumentOnFact() throws Exception {

        HashMap<String, Object> populatedData = new HashMap<String, Object>();
        MethodExecutor methodExecutor = new MethodExecutor( populatedData );

        Cheesery listChesse = new Cheesery();
        listChesse.setTotalAmount( 1000 );
        populatedData.put( "cheese",
                           listChesse );
        Cheesery.Maturity m = Cheesery.Maturity.OLD;
        populatedData.put( "m",
                           m );
        CallMethod mCall = new CallMethod();
        mCall.setVariable( "cheese" );
        mCall.setMethodName( "setMaturity" );
        CallFieldValue field = new CallFieldValue();
        field.value = "=m";
        mCall.addFieldValue( field );

        methodExecutor.executeMethod( mCall );

        assertTrue( listChesse.getMaturity().equals( m ) );
        assertTrue( listChesse.getMaturity() == m );
    }

    @Test
    public void testCallMethodOnClassArgumentAndOnArgumentStandardOnFact() throws Exception {

        HashMap<String, Object> populatedData = new HashMap<String, Object>();
        MethodExecutor methodExecutor = new MethodExecutor( populatedData );

        Cheesery listCheese = new Cheesery();
        listCheese.setTotalAmount( 1000 );
        populatedData.put( "cheese",
                           listCheese );
        Cheesery.Maturity m = Cheesery.Maturity.YOUNG;
        populatedData.put( "m",
                           m );
        CallMethod mCall = new CallMethod();
        mCall.setVariable( "cheese" );
        mCall.setMethodName( "setMaturityAndStatus" );
        CallFieldValue field = new CallFieldValue();
        field.value = "=m";
        mCall.addFieldValue( field );
        CallFieldValue field2 = new CallFieldValue();
        field2.value = "1";
        mCall.addFieldValue( field2 );

        methodExecutor.executeMethod( mCall );
        assertEquals( m, listCheese.getMaturity() );
        assertEquals( 1, listCheese.getStatus() );
    }
}
