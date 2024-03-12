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
package org.drools.mvel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.drools.mvel.MVELConstraintBuilder.StringCoercionCompatibilityEvaluator;
import org.drools.util.DateUtils;
import org.drools.mvel.expr.MVELDateCoercion;
import org.junit.Test;
import org.mvel2.DataConversion;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.util.CompatibilityStrategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mvel2.MVEL.executeExpression;

public class MVELDateCoercionTest {

    @Test
    public void testDate() {
        MVELDateCoercion co = new MVELDateCoercion();
        assertThat(co.canConvertFrom(Date.class)).isTrue();
        assertThat(co.canConvertFrom(Number.class)).isFalse();

        Date d = new Date();
        assertThat(co.convertFrom(d)).isSameAs(d);
    }

    @Test
    public void testString() throws Exception {
        MVELDateCoercion co = new MVELDateCoercion();
        assertThat(co.canConvertFrom(Date.class)).isTrue();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.UK);

        String dt = df.format(df.parse("10-Jul-1974"));
        Date dt_ = DateUtils.parseDate(dt);
        assertThat(co.convertFrom(dt)).isEqualTo(dt_);
    }

    @Test
    public void testCoercionDuringAnalyze() {
        CompatibilityStrategy.setCompatibilityEvaluator(StringCoercionCompatibilityEvaluator.INSTANCE);

        DataConversion.addConversionHandler(Date.class,
                                            new MVELDateCoercion());

        String expr = "f.departureTime >= \"01-Jan-1970\" && f.departureTime <= \"01-Jan-2018\"";

        ParserContext ctx = new ParserContext();
        ctx.setStrongTyping(true);
        ctx.setStrictTypeEnforcement(true);
        ctx.addInput("f", Flight.class);

        Class cls = MVEL.analyze( expr,
                                   ctx );

        assertThat(cls).isNotNull();
    }

    public static class Flight {
        private Date departureTime;

        public Flight(Date departureTime) {
            this.departureTime = departureTime;
        }

        public Date getDepartureTime() {
            return departureTime;
        }

        public void setDepartureTime(Date departureTime) {
            this.departureTime = departureTime;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Flight flight = (Flight) o;

            return Objects.equals(departureTime, flight.departureTime);
        }

        @Override
        public int hashCode() {
            return departureTime != null ? departureTime.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "Flight{" +
                   "departureTime=" + departureTime +
                   '}';
        }
    }
}
