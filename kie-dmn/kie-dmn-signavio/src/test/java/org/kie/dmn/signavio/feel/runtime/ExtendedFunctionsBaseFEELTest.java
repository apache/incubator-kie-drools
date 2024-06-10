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
package org.kie.dmn.signavio.feel.runtime;

import java.util.List;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEventListener;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.impl.FEELBuilder;
import org.kie.dmn.signavio.KieDMNSignavioProfile;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public abstract class ExtendedFunctionsBaseFEELTest {

    private final FEEL feel = FEELBuilder.builder().withProfiles(List.of(new KieDMNSignavioProfile())).build();

    public String expression;

    public Object result;

    public FEELEvent.Severity severity;

    public void expression(String expression, Object result, FEELEvent.Severity severity) {
        this.expression = expression;
        this.result = result;
        this.severity = severity;

        FEELEventListener listener = mock( FEELEventListener.class );
        feel.addListener( listener );
        feel.addListener(System.out::println);
        assertResult(expression, result);

        if( severity != null ) {
            ArgumentCaptor<FEELEvent> captor = ArgumentCaptor.forClass( FEELEvent.class );
            verify( listener , atLeastOnce()).onEvent( captor.capture() );
            assertThat(captor.getValue().getSeverity()).isEqualTo(severity);
        } else {
            verify( listener, never() ).onEvent( any(FEELEvent.class) );
        }
    }

    abstract protected void instanceTest(String expression, Object result, FEELEvent.Severity severity);

    protected void assertResult(String expression, Object result) {
        if (result == null) {
            assertThat(feel.evaluate(expression)).as("Evaluating: '" + expression + "'").isNull();
        } else if (result instanceof Class<?>) {
        	assertThat(feel.evaluate(expression)).as("Evaluating: '" + expression + "'").isInstanceOf((Class<?>) result);
        } else {
        	assertThat(feel.evaluate(expression)).as("Evaluating: '" + expression + "'").isEqualTo(result);
        }
    }
}
