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

package org.kie.dmn.signavio;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.kie.dmn.api.marshalling.v1_1.DMNExtensionRegister;
import org.kie.dmn.core.compiler.DMNProfile;
import org.kie.dmn.core.compiler.DRGElementCompiler;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.signavio.feel.runtime.functions.AppendAllFunction;
import org.kie.dmn.signavio.feel.runtime.functions.AreElementsOfFunction;
import org.kie.dmn.signavio.feel.runtime.functions.AvgFunction;
import org.kie.dmn.signavio.feel.runtime.functions.ContainsOnlyFunction;
import org.kie.dmn.signavio.feel.runtime.functions.DateTimeFunction;
import org.kie.dmn.signavio.feel.runtime.functions.DayAddFunction;
import org.kie.dmn.signavio.feel.runtime.functions.DayDiffFunction;
import org.kie.dmn.signavio.feel.runtime.functions.DayFunction;
import org.kie.dmn.signavio.feel.runtime.functions.DiffFunction;
import org.kie.dmn.signavio.feel.runtime.functions.HourDiffFunction;
import org.kie.dmn.signavio.feel.runtime.functions.HourFunction;
import org.kie.dmn.signavio.feel.runtime.functions.IntegerFunction;
import org.kie.dmn.signavio.feel.runtime.functions.IsAlphaFunction;
import org.kie.dmn.signavio.feel.runtime.functions.IsAlphanumericFunction;
import org.kie.dmn.signavio.feel.runtime.functions.IsNumericFunction;
import org.kie.dmn.signavio.feel.runtime.functions.IsSpacesFunction;
import org.kie.dmn.signavio.feel.runtime.functions.LeftFunction;
import org.kie.dmn.signavio.feel.runtime.functions.LenFunction;
import org.kie.dmn.signavio.feel.runtime.functions.LowerFunction;
import org.kie.dmn.signavio.feel.runtime.functions.MedianFunction;
import org.kie.dmn.signavio.feel.runtime.functions.MidFunction;
import org.kie.dmn.signavio.feel.runtime.functions.MinuteFunction;
import org.kie.dmn.signavio.feel.runtime.functions.MinutesDiffFunction;
import org.kie.dmn.signavio.feel.runtime.functions.ModeFunction;
import org.kie.dmn.signavio.feel.runtime.functions.MonthAddFunction;
import org.kie.dmn.signavio.feel.runtime.functions.MonthDiffFunction;
import org.kie.dmn.signavio.feel.runtime.functions.MonthFunction;
import org.kie.dmn.signavio.feel.runtime.functions.NotContainsAnyFunction;
import org.kie.dmn.signavio.feel.runtime.functions.PercentFunction;
import org.kie.dmn.signavio.feel.runtime.functions.PowerFunction;
import org.kie.dmn.signavio.feel.runtime.functions.RemoveAllFunction;
import org.kie.dmn.signavio.feel.runtime.functions.RightFunction;
import org.kie.dmn.signavio.feel.runtime.functions.RoundDownFunction;
import org.kie.dmn.signavio.feel.runtime.functions.RoundFunction;
import org.kie.dmn.signavio.feel.runtime.functions.RoundUpFunction;
import org.kie.dmn.signavio.feel.runtime.functions.SecondFunction;
import org.kie.dmn.signavio.feel.runtime.functions.SignavioEndsWithFunction;
import org.kie.dmn.signavio.feel.runtime.functions.SignavioNumberFunction;
import org.kie.dmn.signavio.feel.runtime.functions.SignavioRemoveFunction;
import org.kie.dmn.signavio.feel.runtime.functions.SignavioStartsWithFunction;
import org.kie.dmn.signavio.feel.runtime.functions.TextFunction;
import org.kie.dmn.signavio.feel.runtime.functions.TextOccurrencesFunction;
import org.kie.dmn.signavio.feel.runtime.functions.TrimFunction;
import org.kie.dmn.signavio.feel.runtime.functions.UpperFunction;
import org.kie.dmn.signavio.feel.runtime.functions.WeekdayFunction;
import org.kie.dmn.signavio.feel.runtime.functions.YearAddFunction;
import org.kie.dmn.signavio.feel.runtime.functions.YearDiffFunction;
import org.kie.dmn.signavio.feel.runtime.functions.YearFunction;
import org.kie.dmn.signavio.feel.runtime.functions.ZipFunction;

public class KieDMNSignavioProfile implements DMNProfile {
    
    protected final static List<DMNExtensionRegister> EXT_REGISTERS = Collections.unmodifiableList(Arrays.asList(
            new MultiInstanceDecisionLogicRegister()
    ));
    
    protected final static List<DRGElementCompiler> COMPILATION_EXT = Collections.unmodifiableList(Arrays.asList(
            new MultiInstanceDecisionLogic.MultiInstanceDecisionNodeCompiler()
    ));

    @Override
    public List<DMNExtensionRegister> getExtensionRegisters() {
        return EXT_REGISTERS;
    }

    @Override
    public List<DRGElementCompiler> getDRGElementCompilers() {
        return COMPILATION_EXT;
    }

    @Override
    public List<FEELFunction> getFEELFunctions() {
        return Arrays.asList(SIGNAVIO_FUNCTIONS);
    }

    public final static FEELFunction[] SIGNAVIO_FUNCTIONS = new FEELFunction[]{// signavio profile functions
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
}