package org.kie.dmn.feel.runtime.functions.twovaluelogic;

import java.util.List;
import java.util.function.Function;

public class TwoValueLogicFunctions {
    private static AllFunction allFunction = new AllFunction();
    private static AnyFunction anyFunction = new AnyFunction();
    private static SumFunction sumFunction = new SumFunction();
    private static MeanFunction meanFunction = new MeanFunction();
    private static CountFunction countFunction = new CountFunction();
    private static MaxFunction maxFunction = new MaxFunction();
    private static MinFunction minFunction = new MinFunction();
    private static MedianFunction medianFunction = MedianFunction.INSTANCE;
    private static ModeFunction modeFunction = ModeFunction.INSTANCE;

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

}
