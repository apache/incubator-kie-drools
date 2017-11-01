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

package org.kie.dmn.signavio.feel.runtime;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEventListener;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.signavio.KieDMNSignavioProfile;
import org.mockito.ArgumentCaptor;

@RunWith(Parameterized.class)
public abstract class ExtendedFunctionsBaseFEELTest {

    private final FEEL feel = FEEL.newInstance(Arrays.asList(new KieDMNSignavioProfile()));

    @Parameterized.Parameter(0)
    public String expression;

    @Parameterized.Parameter(1)
    public Object result;

    @Parameterized.Parameter(2)
    public FEELEvent.Severity severity;

    @Test
    public void testExpression() {
        FEELEventListener listener = mock( FEELEventListener.class );
        feel.addListener( listener );
        feel.addListener( evt -> {
            System.out.println(evt);
        } );
        assertResult(expression, result);

        if( severity != null ) {
            ArgumentCaptor<FEELEvent> captor = ArgumentCaptor.forClass( FEELEvent.class );
            verify( listener , atLeastOnce()).onEvent( captor.capture() );
            assertThat( captor.getValue().getSeverity(), is( severity ) );
        } else {
            verify( listener, never() ).onEvent( any(FEELEvent.class) );
        }
    }

    protected void assertResult(String expression, Object result) {
        if (result == null) {
            assertThat("Evaluating: '" + expression + "'", feel.evaluate(expression), is(nullValue()));
        } else if (result instanceof Class<?>) {
            assertThat("Evaluating: '" + expression + "'", feel.evaluate(expression), is(instanceOf((Class<?>) result)));
        } else {
            assertThat("Evaluating: '" + expression + "'", feel.evaluate(expression), is(result));
        }
    }
}
