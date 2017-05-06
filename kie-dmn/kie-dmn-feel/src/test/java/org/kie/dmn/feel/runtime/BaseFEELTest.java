/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.feel.runtime;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.api.feel.runtime.events.FEELEventListener;
import org.kie.dmn.feel.FEEL;

import java.util.ArrayList;
import java.util.List;

@RunWith(Parameterized.class)
public abstract class BaseFEELTest {

    @Parameterized.Parameter(0)
    public String expression;

    @Parameterized.Parameter(1)
    public Object result;

    @Test
    public void testExpression() {
        assertResult( expression, result );
    }

    protected void assertResult( String expression, Object result ) {
        FEEL feel = FEEL.newInstance();
        ErrorListener el = new ErrorListener();
        feel.addListener( el );
        if( result == null ) {
            assertThat( "Evaluating: '" + expression + "'", feel.evaluate( expression ), is( nullValue() ) );
        } else if( result instanceof Class<?> ) {
            assertThat( "Evaluating: '" + expression + "'", feel.evaluate( expression ), is( instanceOf( (Class<?>) result ) ) );
        } else {
            assertThat( "Evaluating: '"+expression+"'", feel.evaluate( expression ), is( result ) );
        }
        assertThat( el.events.toString(), el.events.isEmpty(), is( true ) );
    }

    public static class ErrorListener implements FEELEventListener {
        public final List<FEELEvent> events = new ArrayList<>(  );
        @Override
        public void onEvent(FEELEvent event) {
            events.add( event );
        }
    }
}
