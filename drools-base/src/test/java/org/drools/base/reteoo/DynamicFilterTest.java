/*
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
package org.drools.base.reteoo;

import java.lang.reflect.Field;

import org.drools.base.base.ValueResolver;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.constraint.NegConstraint;
import org.junit.Test;
import org.kie.api.runtime.rule.FactHandle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DynamicFilterTest {

    @Test
    public void adapterRegisteredBeforeEpochCounterWrapsStillReceivesSignal() throws Exception {
        // NegConstraint(false) allows any non-negated handle and has no required declarations (alpha path).
        DynamicFilter filter = new DynamicFilter(new DynamicFilterProto(new NegConstraint(false), 0));

        // Put the delivery epoch counter on the edge of overflow, as after ~2^31 assertObject calls on one filter.
        setDeliveryEpoch(filter, Integer.MAX_VALUE);

        RecordingSignalAdapter adapter = new RecordingSignalAdapter();
        filter.addSignalAdapter(adapter);

        // This assertObject increments the epoch counter past Integer.MAX_VALUE.
        filter.assertObject(new TestFactHandle(), null);

        assertThat(adapter.received)
                .as("a live adapter must still receive the signal when the delivery epoch counter wraps")
                .isEqualTo(1);
    }

    @Test
    public void nonBetaConstraintWithRequiredDeclarationsFailsWithExplanatoryException() {
        // A constraint that is NOT a BetaConstraint but reports required declarations: test() must fail
        // with a clear IllegalStateException rather than a bare ClassCastException.
        NegConstraint nonBetaWithDeclarations = new NegConstraint(false) {
            @Override
            public Declaration[] getRequiredDeclarations() {
                return new Declaration[]{new Declaration("var", null, null)};
            }
        };
        DynamicFilter filter = new DynamicFilter(new DynamicFilterProto(nonBetaWithDeclarations, 0));
        filter.addSignalAdapter(new RecordingSignalAdapter());

        assertThatThrownBy(() -> filter.assertObject(new TestFactHandle(), null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("non-beta constraint");
    }

    private static void setDeliveryEpoch(DynamicFilter filter, int value) throws Exception {
        Field field = DynamicFilter.class.getDeclaredField("deliveryEpoch");
        field.setAccessible(true);
        if (field.getType() == long.class) {
            field.setLong(filter, value);
        } else {
            field.setInt(filter, value);
        }
    }

    private static class RecordingSignalAdapter extends SignalAdapter {
        int received;

        RecordingSignalAdapter() {
            super(null, 0, 0, null);
        }

        @Override
        public BaseTuple getAnchorTuple() {
            return null;
        }

        @Override
        public void receive(ValueResolver reteEvaluator, FactHandle factHandle) {
            received++;
        }
    }

    private static class TestFactHandle implements FactHandle {
        @Override public Object getObject() { return new Object(); }
        @Override public boolean isNegated() { return false; }
        @Override public boolean isEvent() { return false; }
        @Override public long getId() { return 1L; }
        @Override public long getRecency() { return 1L; }
        @Override public <K> K as(Class<K> klass) { return null; }
        @Override public boolean isValid() { return true; }
        @Override public String toExternalForm() { return "handle:1"; }
    }
}
