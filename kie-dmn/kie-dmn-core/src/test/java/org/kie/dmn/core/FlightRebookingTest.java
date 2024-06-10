/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.core;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;

import static org.assertj.core.api.Assertions.assertThat;

public class FlightRebookingTest extends BaseInterpretedVsCompiledTest {
    
    @ParameterizedTest
    @MethodSource("params")
    void solution1(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0019-flight-rebooking.dmn", this.getClass() );
        final DMNModel dmnModel = runtime.getModel("https://www.drools.org/kie-dmn", "0019-flight-rebooking" );
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();// need proper type support to enable this

        final DMNContext context = DMNFactory.newContext();

        final List passengerList = loadPassengerList();
        final List flightList = loadFlightList();

        context.set( "Passenger List", passengerList );
        context.set( "Flight List", flightList );

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context );

        final DMNContext result = dmnResult.getContext();

        assertThat(result.get( "Rebooked Passengers")).isEqualTo(loadExpectedResult());
    }

    @ParameterizedTest
    @MethodSource("params")
    void solutionAlternate(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0019-flight-rebooking-alternative.dmn", this.getClass() );
        final DMNModel dmnModel = runtime.getModel("https://www.drools.org/kie-dmn", "0019-flight-rebooking" );
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();

        final List passengerList = loadPassengerList();
        final List flightList = loadFlightList();

        context.set( "Passenger List", passengerList );
        context.set( "Flight List", flightList );

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context );

        final DMNContext result = dmnResult.getContext();

        assertThat(result.get( "Rebooked Passengers")).isEqualTo(loadExpectedResult());
    }

    @ParameterizedTest
    @MethodSource("params")
    void solutionSingletonLists(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0019-flight-rebooking-singleton-lists.dmn", this.getClass() );
        final DMNModel dmnModel = runtime.getModel("https://www.drools.org/kie-dmn", "0019-flight-rebooking" );
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();
        
        final DMNContext context = DMNFactory.newContext();

        final List passengerList = loadPassengerList();
        final List flightList = loadFlightList();

        context.set( "Passenger List", passengerList );
        context.set( "Flight List", flightList );

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context );

        final DMNContext result = dmnResult.getContext();

        assertThat(result.get("Rebooked Passengers")).isEqualTo(loadExpectedResult());
    }

    @ParameterizedTest
    @MethodSource("params")
    void solutionBadExample(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0019-flight-rebooking-bad-example.dmn", this.getClass() );
        final DMNModel dmnModel = runtime.getModel("https://www.drools.org/kie-dmn", "0019-flight-rebooking" );
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();

        final List passengerList = loadPassengerList();
        final List flightList = loadFlightList();

        context.set( "Passenger List", passengerList );
        context.set( "Flight List", flightList );

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context );

        final DMNContext result = dmnResult.getContext();

        assertThat(result.get( "Rebooked Passengers")).isEqualTo(loadExpectedResult());
    }

    @ParameterizedTest
    @MethodSource("params")
    void uninterpreted(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0019-flight-rebooking-uninterpreted.dmn", this.getClass() );
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_188d6caf-a355-49b5-a692-bd6ce713da08", "0019-flight-rebooking" );
        runtime.addListener( DMNRuntimeUtil.createListener() );
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();

        final List passengerList = loadPassengerList();
        final List flightList = loadFlightList();

        context.set( "Passenger List", passengerList );
        context.set( "Flight List", flightList );

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context );

        assertThat(dmnResult.getDecisionResultByName("Rebooked Passengers").getEvaluationStatus()).isEqualTo(DMNDecisionResult.DecisionEvaluationStatus.SKIPPED);
    }

    private List loadPassengerList() {
        final Object[][] passengerData = new Object[][] {
                {"Tom", "bronze", 10, "UA123"},
                {"Igor", "gold", 50000, "UA123"},
                {"Jenny", "gold", 500000, "UA123"},
                {"Harry", "gold", 100000, "UA123"},
                {"Dick", "silver", 100, "UA123"}};

        final List<Map<String,Object>> passengerList = new ArrayList<>(  );
        for( final Object[] pd : passengerData ) {
            final Map<String, Object> p = new HashMap<>(  );
            p.put( "Name", pd[0] );
            p.put( "Status", pd[1] );
            p.put( "Miles", number( (Number) pd[2] ) );
            p.put( "Flight Number", pd[3] );
            passengerList.add( p );
        }
        return passengerList;
    }

    private List loadFlightList() {
        final Object[][] flightData = new Object[][] {
                {"UA123", "SFO", "SNA", date("2017-01-01T18:00:00"), date("2017-01-01T19:00:00"), 5, "cancelled"},
                {"UA456", "SFO", "SNA", date("2017-01-01T19:00:00"), date("2017-01-01T20:00:00"), 2, "scheduled"},
                {"UA789", "SFO", "SNA", date("2017-01-01T21:00:00"), date("2017-01-01T23:00:00"), 2, "scheduled"},
                {"UA1001", "SFO", "SNA", date("2017-01-01T23:00:00"), date("2017-01-02T05:00:00"), 0, "scheduled"},
                {"UA1111", "SFO", "LAX", date("2017-01-01T23:00:00"), date("2017-01-02T05:00:00"), 2, "scheduled"}
        };

        final List<Map<String,Object>> flightList = new ArrayList<>(  );
        for( final Object[] pd : flightData ) {
            final Map<String, Object> p = new HashMap<>(  );
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
        final Object[][] passengerData = new Object[][] {
                {"Jenny", "gold", 500000, "UA456"},
                {"Harry", "gold", 100000, "UA456"},
                {"Igor", "gold", 50000, "UA789"},
                {"Dick", "silver", 100, "UA789"},
                {"Tom", "bronze", 10, null}
                };

        final List<Map<String,Object>> passengerList = new ArrayList<>(  );
        for( final Object[] pd : passengerData ) {
            final Map<String, Object> p = new HashMap<>(  );
            p.put( "Name", pd[0] );
            p.put( "Status", pd[1] );
            p.put( "Miles", number( (Number) pd[2] ) );
            p.put( "Flight Number", pd[3] );
            passengerList.add( p );
        }
        return passengerList;
    }

    private LocalDateTime date(final String date ) {
        return LocalDateTime.parse( date );
    }

    private BigDecimal number(final Number n ) {
        return BigDecimal.valueOf( n.longValue() );
    }
}

