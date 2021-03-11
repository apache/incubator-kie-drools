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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEventListener;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.FEELProfile;
import org.kie.dmn.feel.parser.feel11.profiles.DoCompileFEELProfile;
import org.kie.dmn.feel.parser.feel11.profiles.KieExtendedFEELProfile;
import org.mockito.ArgumentCaptor;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(Parameterized.class)
public abstract class BaseFEELTest {

    public enum FEEL_TARGET {
        AST_INTERPRETED,
        JAVA_TRANSLATED
    }

    private FEEL feel = null; // due to @Parameter injection by JUnit framework, need to defer FEEL init to actual instance method, to have the opportunity for the JUNit framework to initialize all the @Parameters

    @Parameterized.Parameter(0)
    public String expression;

    @Parameterized.Parameter(1)
    public Object result;

    @Parameterized.Parameter(2)
    public FEELEvent.Severity severity;

    @Parameterized.Parameter(3)
    public FEEL_TARGET testFEELTarget;

    @Parameterized.Parameter(4)
    public boolean useExtendedProfile;

    @Test
    public void testExpression() {
        final List<FEELProfile> profiles = getFEELProfilesForTests();
        feel = FEEL.newInstance(profiles);
        final FEELEventListener listener = mock(FEELEventListener.class );
        feel.addListener( listener );
        feel.addListener(System.out::println);
        assertResult( expression, result );

        if( severity != null ) {
            final ArgumentCaptor<FEELEvent> captor = ArgumentCaptor.forClass(FEELEvent.class );
            verify( listener , atLeastOnce()).onEvent( captor.capture() );
            assertThat( captor.getValue().getSeverity(), is( severity ) );
        } else {
            verify( listener, never() ).onEvent( any(FEELEvent.class) );
        }
    }

    protected void assertResult(final String expression, final Object result ) {
        if( result == null ) {
            assertThat( "Evaluating: '" + expression + "'", feel.evaluate( expression ), is( nullValue() ) );
        } else if( result instanceof Class<?> ) {
            assertThat( "Evaluating: '" + expression + "'", feel.evaluate( expression ), is( instanceOf( (Class<?>) result ) ) );
        } else {
            assertThat( "Evaluating: '"+expression+"'", feel.evaluate( expression ), is( result ) );
        }
    }

    protected static List<Object[]> addAdditionalParameters(final Object[][] cases, final boolean useExtendedProfile) {
        final List<Object[]> results = new ArrayList<>();
        for (final Object[] c : cases) {
            results.add(new Object[]{c[0], c[1], c[2], FEEL_TARGET.AST_INTERPRETED, useExtendedProfile});
        }
        for (final Object[] c : cases) {
            results.add(new Object[]{c[0], c[1], c[2], FEEL_TARGET.JAVA_TRANSLATED, useExtendedProfile});
        }
        return results;
    }

    private List<FEELProfile> getFEELProfilesForTests() {
        final List<FEELProfile> profiles = new ArrayList<>();
        if (testFEELTarget == FEEL_TARGET.JAVA_TRANSLATED) {
            profiles.add(new DoCompileFEELProfile());
        }
        if (useExtendedProfile) {
            profiles.add(new KieExtendedFEELProfile());
        }
        return profiles;
    }
}
