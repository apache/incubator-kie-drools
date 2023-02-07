package org.kie.dmn.feel.runtime.functions.twovaluelogic;

import java.util.List;
import java.util.function.Function;

import org.kie.dmn.model.api.GwtIncompatible;

@GwtIncompatible
public class TwoValueLogicFunctions {
    private static NNAllFunction allFunction = NNAllFunction.INSTANCE;
    private static NNAnyFunction anyFunction = NNAnyFunction.INSTANCE;
    private static NNSumFunction sumFunction = NNSumFunction.INSTANCE;
    private static NNMeanFunction meanFunction = NNMeanFunction.INSTANCE;
    private static NNCountFunction countFunction = NNCountFunction.INSTANCE;
    private static NNMaxFunction maxFunction = NNMaxFunction.INSTANCE;
    private static NNMinFunction minFunction = NNMinFunction.INSTANCE;
    private static NNMedianFunction medianFunction = NNMedianFunction.INSTANCE;
    private static NNModeFunction modeFunction = NNModeFunction.INSTANCE;

    @GwtIncompatible
    private static NNStddevFunction stddevFunction = NNStddevFunction.INSTANCE;

    public static Boolean all(List<Boolean> list) {
        return allFunction.invoke(list).cata(e -> Boolean.FALSE, Function.identity());
    }

    public static Boolean any(List<Boolean> list) {
        return anyFunction.invoke(list).cata(e -> Boolean.FALSE, Function.identity());
    }

    public static Number sum(List<Boolean> list) {
        return sumFunction.invoke(list).cata(e -> null, Function.identity());
    }

    public static Number mean(List<Number> list) {
        return meanFunction.invoke(list).cata(e -> null, Function.identity());
    }

    public static Number count(List<Number> list) {
        return countFunction.invoke(list).cata(e -> null, Function.identity());
    }

    public static Object max(List<Number> list) {
        return maxFunction.invoke(list).cata(e -> null, Function.identity());
    }

    public static Object min(List<Number> list) {
        return minFunction.invoke(list).cata(e -> null, Function.identity());
    }

    public static Number median(List<Number> list) {
        return medianFunction.invoke(list).cata(e -> null, Function.identity());
    }

    public static List mode(List<Number> list) {
        return modeFunction.invoke(list).cata(e -> null, Function.identity());
    }

    @GwtIncompatible
    public static Number stddev(List<Number> list) {
        return stddevFunction.invoke(list).cata(e -> null, Function.identity());
    }
}
