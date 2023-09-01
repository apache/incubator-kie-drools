package org.kie.dmn.feel.runtime.functions.extended;

import java.util.stream.Stream;

import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.runtime.functions.twovaluelogic.NNAllFunction;
import org.kie.dmn.feel.runtime.functions.twovaluelogic.NNAnyFunction;
import org.kie.dmn.feel.runtime.functions.twovaluelogic.NNCountFunction;
import org.kie.dmn.feel.runtime.functions.twovaluelogic.NNMaxFunction;
import org.kie.dmn.feel.runtime.functions.twovaluelogic.NNMeanFunction;
import org.kie.dmn.feel.runtime.functions.twovaluelogic.NNMedianFunction;
import org.kie.dmn.feel.runtime.functions.twovaluelogic.NNMinFunction;
import org.kie.dmn.feel.runtime.functions.twovaluelogic.NNModeFunction;
import org.kie.dmn.feel.runtime.functions.twovaluelogic.NNStddevFunction;
import org.kie.dmn.feel.runtime.functions.twovaluelogic.NNSumFunction;

/**
 * additional functions not part of the spec version 1.x, or not incorporated in the spec yet.
 */
public class KieExtendedDMNFunctions {

    protected static final FEELFunction[] FUNCTIONS = new FEELFunction[]{
                                                                         TimeFunction.INSTANCE,
                                                                         DateFunction.INSTANCE,
                                                                         DurationFunction.INSTANCE,

                                                                         // additional functions not part of the spec version 1.1
                                                                         new NowFunction(),
                                                                         new TodayFunction(),
                                                                         new CodeFunction(),
                                                                         new InvokeFunction(),

                                                                         ContextPutFunction.INSTANCE,
                                                                         ContextMergeFunction.INSTANCE,
                                                                         ContextFunction.INSTANCE,
                                                                         FloorFunction.INSTANCE,
                                                                         CeilingFunction.INSTANCE,
                                                                         RoundUpFunction.INSTANCE,
                                                                         RoundDownFunction.INSTANCE,
                                                                         RoundHalfUpFunction.INSTANCE,
                                                                         RoundHalfDownFunction.INSTANCE,

                                                                         // CQL based, two value logic functions
                                                                        NNAnyFunction.INSTANCE,
                                                                        NNAllFunction.INSTANCE,
                                                                        NNCountFunction.INSTANCE,
                                                                        NNMaxFunction.INSTANCE,
                                                                        NNMeanFunction.INSTANCE,
                                                                        NNMedianFunction.INSTANCE,
                                                                        NNMinFunction.INSTANCE,
                                                                        NNModeFunction.INSTANCE,
                                                                        NNStddevFunction.INSTANCE,
                                                                        NNSumFunction.INSTANCE,

                                                                        StringJoinFunction.INSTANCE,
                                                                        RangeFunction.INSTANCE,

    };

    public static FEELFunction[] getFunctions() {
        return FUNCTIONS;
    }

    public static <T extends FEELFunction> T getFunction(Class<T> functionClazz) {
        return (T) Stream.of(FUNCTIONS)
                .filter(f -> functionClazz.isAssignableFrom(f.getClass()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cannot find function by class " + functionClazz.getCanonicalName() + "!"));
    }
}
