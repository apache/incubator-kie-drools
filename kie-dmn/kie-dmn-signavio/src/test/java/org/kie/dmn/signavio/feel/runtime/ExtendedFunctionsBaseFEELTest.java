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

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEventListener;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.CompiledExpression;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.impl.EvaluationContextImpl;
import org.kie.dmn.feel.lang.impl.FEELEventListenersManager;
import org.kie.dmn.feel.lang.impl.FEELImpl;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.runtime.functions.AppendAllFunction;
import org.kie.dmn.feel.runtime.functions.AreElementsOfFunction;
import org.kie.dmn.feel.runtime.functions.AvgFunction;
import org.kie.dmn.feel.runtime.functions.ContainsOnlyFunction;
import org.kie.dmn.feel.runtime.functions.DateTimeFunction;
import org.kie.dmn.feel.runtime.functions.DayAddFunction;
import org.kie.dmn.feel.runtime.functions.DayDiffFunction;
import org.kie.dmn.feel.runtime.functions.DayFunction;
import org.kie.dmn.feel.runtime.functions.DiffFunction;
import org.kie.dmn.feel.runtime.functions.HourDiffFunction;
import org.kie.dmn.feel.runtime.functions.HourFunction;
import org.kie.dmn.feel.runtime.functions.IntegerFunction;
import org.kie.dmn.feel.runtime.functions.IsAlphaFunction;
import org.kie.dmn.feel.runtime.functions.IsAlphanumericFunction;
import org.kie.dmn.feel.runtime.functions.IsNumericFunction;
import org.kie.dmn.feel.runtime.functions.IsSpacesFunction;
import org.kie.dmn.feel.runtime.functions.LeftFunction;
import org.kie.dmn.feel.runtime.functions.LenFunction;
import org.kie.dmn.feel.runtime.functions.LowerFunction;
import org.kie.dmn.feel.runtime.functions.MedianFunction;
import org.kie.dmn.feel.runtime.functions.MidFunction;
import org.kie.dmn.feel.runtime.functions.MinuteFunction;
import org.kie.dmn.feel.runtime.functions.MinutesDiffFunction;
import org.kie.dmn.feel.runtime.functions.ModeFunction;
import org.kie.dmn.feel.runtime.functions.MonthAddFunction;
import org.kie.dmn.feel.runtime.functions.MonthDiffFunction;
import org.kie.dmn.feel.runtime.functions.MonthFunction;
import org.kie.dmn.feel.runtime.functions.NotContainsAnyFunction;
import org.kie.dmn.feel.runtime.functions.PercentFunction;
import org.kie.dmn.feel.runtime.functions.PowerFunction;
import org.kie.dmn.feel.runtime.functions.RemoveAllFunction;
import org.kie.dmn.feel.runtime.functions.RightFunction;
import org.kie.dmn.feel.runtime.functions.RoundDownFunction;
import org.kie.dmn.feel.runtime.functions.RoundFunction;
import org.kie.dmn.feel.runtime.functions.RoundUpFunction;
import org.kie.dmn.feel.runtime.functions.SecondFunction;
import org.kie.dmn.feel.runtime.functions.SignavioEndsWithFunction;
import org.kie.dmn.feel.runtime.functions.SignavioNumberFunction;
import org.kie.dmn.feel.runtime.functions.SignavioRemoveFunction;
import org.kie.dmn.feel.runtime.functions.SignavioStartsWithFunction;
import org.kie.dmn.feel.runtime.functions.TextFunction;
import org.kie.dmn.feel.runtime.functions.TextOccurrencesFunction;
import org.kie.dmn.feel.runtime.functions.TrimFunction;
import org.kie.dmn.feel.runtime.functions.UpperFunction;
import org.kie.dmn.feel.runtime.functions.WeekdayFunction;
import org.kie.dmn.feel.runtime.functions.YearAddFunction;
import org.kie.dmn.feel.runtime.functions.YearDiffFunction;
import org.kie.dmn.feel.runtime.functions.YearFunction;
import org.kie.dmn.feel.runtime.functions.ZipFunction;
import org.kie.dmn.feel.util.EvalHelper;
import org.mockito.ArgumentCaptor;

@RunWith(Parameterized.class)
public abstract class ExtendedFunctionsBaseFEELTest {

    private final FEEL feel = FEEL.newInstance();

    protected final static FEELFunction[] SIGNAVIO_FUNCTIONS = new FEELFunction[]{// signavio profile functions
                                                                         new DayFunction(),
                                                                         new MonthFunction(),
                                                                         new YearFunction(),
                                                                         new HourFunction(),
                                                                         new MinuteFunction(),
                                                                         new SecondFunction(),
                                                                         new DateTimeFunction(),
                                                                         new RoundFunction(),
                                                                         new RoundDownFunction(),
                                                                         new RoundUpFunction(),
                                                                         new IntegerFunction(),
                                                                         new PercentFunction(),
                                                                         new PowerFunction(),
                                                                         new WeekdayFunction(),
                                                                         new YearDiffFunction(),
                                                                         new MonthDiffFunction(),
                                                                         new DayDiffFunction(),
                                                                         new HourDiffFunction(),
                                                                         new MinutesDiffFunction(),
                                                                         new DiffFunction(),
                                                                         new YearAddFunction(),
                                                                         new MonthAddFunction(),
                                                                         new DayAddFunction(),
                                                                         new AppendAllFunction(),
                                                                         new ZipFunction(),
                                                                         new NotContainsAnyFunction(),
                                                                         new ContainsOnlyFunction(),
                                                                         new AreElementsOfFunction(),
                                                                         new SignavioRemoveFunction(),
                                                                         new RemoveAllFunction(),
                                                                         new AvgFunction(),
                                                                         new MedianFunction(),
                                                                         new ModeFunction(),
                                                                         new SignavioEndsWithFunction(),
                                                                         new SignavioStartsWithFunction(),
                                                                         new TextOccurrencesFunction(),
                                                                         new TextFunction(),
                                                                         new LeftFunction(),
                                                                         new RightFunction(),
                                                                         new MidFunction(),
                                                                         new SignavioNumberFunction(),
                                                                         new UpperFunction(),
                                                                         new LowerFunction(),
                                                                         new TrimFunction(),
                                                                         new LenFunction(),
                                                                         new IsSpacesFunction(),
                                                                         new IsNumericFunction(),
                                                                         new IsAlphanumericFunction(),
                                                                         new IsAlphaFunction()
    };

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

        CompilerContext compilerCtx = feel.newCompilerContext();
        for (FEELFunction f : SIGNAVIO_FUNCTIONS) {
            compilerCtx.addFEELFunctions(f);
        }
        CompiledExpression compiledExpr = feel.compile(expression, compilerCtx);

        FEELEventListenersManager eventsManager = ((FEELImpl) feel).getEventsManager(Collections.emptyList());
        EvaluationContextImpl evalCtx = new EvaluationContextImpl(eventsManager);
        for (FEELFunction f : SIGNAVIO_FUNCTIONS) {
            evalCtx.peek().setValue(EvalHelper.normalizeVariableName(f.getName()), f);
        }
        Object feelEvaluationResult = feel.evaluate(compiledExpr, evalCtx);

        assertResult(expression, feelEvaluationResult, result);

        if( severity != null ) {
            ArgumentCaptor<FEELEvent> captor = ArgumentCaptor.forClass( FEELEvent.class );
            verify( listener , atLeastOnce()).onEvent( captor.capture() );
            assertThat( captor.getValue().getSeverity(), is( severity ) );
        } else {
            verify( listener, never() ).onEvent( any(FEELEvent.class) );
        }
    }

    protected void assertResult(String expression, Object actual, Object expected) {
        if (expected == null) {
            assertThat("Evaluating: '" + expression + "'", actual, is(nullValue()));
        } else if (expected instanceof Class<?>) {
            assertThat("Evaluating: '" + expression + "'", actual, is(instanceOf((Class<?>) expected)));
        } else {
            assertThat("Evaluating: '" + expression + "'", actual, is(expected));
        }
    }
}
