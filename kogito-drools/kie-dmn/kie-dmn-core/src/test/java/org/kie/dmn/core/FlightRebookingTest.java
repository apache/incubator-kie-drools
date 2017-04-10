/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.core;

import org.junit.Test;
import org.kie.dmn.api.core.*;
import org.kie.dmn.core.api.*;
import org.kie.dmn.core.api.event.*;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class FlightRebookingTest {

    @Test
    public void testSolution1() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "0019-flight-rebooking.dmn", this.getClass() );
        DMNModel dmnModel = runtime.getModel( "https://www.drools.org/kie-dmn", "0019-flight-rebooking" );
        assertThat( dmnModel, notNullValue() );
        //assertThat( formatMessages( dmnModel.getMessages() ), dmnModel.hasErrors(), is(false) ); // need proper type support to enable this

        DMNContext context = DMNFactory.newContext();

        List passengerList = loadPassengerList();
        List flightList = loadFlightList();

        context.set( "Passenger List", passengerList );
        context.set( "Flight List", flightList );

        DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );

        DMNContext result = dmnResult.getContext();

        assertThat( result.get( "Rebooked Passengers" ), is( loadExpectedResult() ) );
    }

    @Test
    public void testSolutionAlternate() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "0019-flight-rebooking-alternative.dmn", this.getClass() );
        DMNModel dmnModel = runtime.getModel( "https://www.drools.org/kie-dmn", "0019-flight-rebooking" );
        assertThat( dmnModel, notNullValue() );

        DMNContext context = DMNFactory.newContext();

        List passengerList = loadPassengerList();
        List flightList = loadFlightList();

        context.set( "Passenger List", passengerList );
        context.set( "Flight List", flightList );

        DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );

        DMNContext result = dmnResult.getContext();

        assertThat( result.get( "Rebooked Passengers" ), is( loadExpectedResult() ) );
    }

    @Test
    public void testSolutionSingletonLists() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "0019-flight-rebooking-singleton-lists.dmn", this.getClass() );
        DMNModel dmnModel = runtime.getModel( "https://www.drools.org/kie-dmn", "0019-flight-rebooking" );
        assertThat( dmnModel, notNullValue() );

        DMNContext context = DMNFactory.newContext();

        List passengerList = loadPassengerList();
        List flightList = loadFlightList();

        context.set( "Passenger List", passengerList );
        context.set( "Flight List", flightList );

        DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );

        DMNContext result = dmnResult.getContext();

        assertThat( result.get( "Rebooked Passengers" ), is( loadExpectedResult() ) );
    }

    @Test
    public void testSolutionBadExample() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "0019-flight-rebooking-bad-example.dmn", this.getClass() );
        DMNModel dmnModel = runtime.getModel( "https://www.drools.org/kie-dmn", "0019-flight-rebooking" );
        assertThat( dmnModel, notNullValue() );

        DMNContext context = DMNFactory.newContext();

        List passengerList = loadPassengerList();
        List flightList = loadFlightList();

        context.set( "Passenger List", passengerList );
        context.set( "Flight List", flightList );

        DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );

        DMNContext result = dmnResult.getContext();

        assertThat( result.get( "Rebooked Passengers" ), is( loadExpectedResult() ) );
    }

    @Test
    public void testUninterpreted() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "0019-flight-rebooking-uninterpreted.dmn", this.getClass() );
        DMNModel dmnModel = runtime.getModel( "http://www.trisotech.com/dmn/definitions/_188d6caf-a355-49b5-a692-bd6ce713da08", "0019-flight-rebooking" );
        runtime.addListener( DMNRuntimeUtil.createListener() );
        assertThat( dmnModel, notNullValue() );

        DMNContext context = DMNFactory.newContext();

        List passengerList = loadPassengerList();
        List flightList = loadFlightList();

        context.set( "Passenger List", passengerList );
        context.set( "Flight List", flightList );

        DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );

        assertThat( dmnResult.getDecisionResultByName( "Rebooked Passengers" ).getEvaluationStatus(), is( DMNDecisionResult.DecisionEvaluationStatus.SKIPPED ) );
    }

    private List loadPassengerList() {
        Object[][] passengerData = new Object[][] {
                {"Tom", "bronze", 10, "UA123"},
                {"Igor", "gold", 50000, "UA123"},
                {"Jenny", "gold", 500000, "UA123"},
                {"Harry", "gold", 100000, "UA123"},
                {"Dick", "silver", 100, "UA123"}};

        List<Map<String,Object>> passengerList = new ArrayList<>(  );
        for( Object[] pd : passengerData ) {
            Map<String, Object> p = new HashMap<>(  );
            p.put( "Name", pd[0] );
            p.put( "Status", pd[1] );
            p.put( "Miles", number( (Number) pd[2] ) );
            p.put( "Flight Number", pd[3] );
            passengerList.add( p );
        }
        return passengerList;
    }

    private List loadFlightList() {
        Object[][] flightData = new Object[][] {
                {"UA123", "SFO", "SNA", date("2017-01-01T18:00:00"), date("2017-01-01T19:00:00"), 5, "cancelled"},
                {"UA456", "SFO", "SNA", date("2017-01-01T19:00:00"), date("2017-01-01T20:00:00"), 2, "scheduled"},
                {"UA789", "SFO", "SNA", date("2017-01-01T21:00:00"), date("2017-01-01T23:00:00"), 2, "scheduled"},
                {"UA1001", "SFO", "SNA", date("2017-01-01T23:00:00"), date("2017-01-02T05:00:00"), 0, "scheduled"},
                {"UA1111", "SFO", "LAX", date("2017-01-01T23:00:00"), date("2017-01-02T05:00:00"), 2, "scheduled"}
        };

        List<Map<String,Object>> flightList = new ArrayList<>(  );
        for( Object[] pd : flightData ) {
            Map<String, Object> p = new HashMap<>(  );
            p.put( "Flight Number", pd[0] );
            p.put( "From", pd[1] );
            p.put( "To", pd[2] );
            p.put( "Departure", pd[3] );
            p.put( "Arrival", pd[4] );
            p.put( "Capacity", pd[5] );
            p.put( "Status", pd[6] );
            flightList.add( p );
        }
        return flightList;
    }

    private List loadExpectedResult() {
        Object[][] passengerData = new Object[][] {
                {"Jenny", "gold", 500000, "UA456"},
                {"Harry", "gold", 100000, "UA456"},
                {"Igor", "gold", 50000, "UA789"},
                {"Dick", "silver", 100, "UA789"},
                {"Tom", "bronze", 10, null}
                };

        List<Map<String,Object>> passengerList = new ArrayList<>(  );
        for( Object[] pd : passengerData ) {
            Map<String, Object> p = new HashMap<>(  );
            p.put( "Name", pd[0] );
            p.put( "Status", pd[1] );
            p.put( "Miles", number( (Number) pd[2] ) );
            p.put( "Flight Number", pd[3] );
            passengerList.add( p );
        }
        return passengerList;
    }

    private LocalDateTime date( String date ) {
        return LocalDateTime.parse( date );
    }

    private BigDecimal number( Number n ) {
        return BigDecimal.valueOf( n.longValue() );
    }

    private String formatMessages(List<DMNMessage> messages) {
        return messages.stream().map( m -> m.toString() ).collect( Collectors.joining( "\n" ) );
    }

}

