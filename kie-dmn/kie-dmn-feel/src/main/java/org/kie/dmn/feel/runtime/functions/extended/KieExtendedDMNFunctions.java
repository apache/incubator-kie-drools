package org.kie.dmn.feel.runtime.functions.extended;

import java.util.stream.Stream;

import org.kie.dmn.feel.runtime.FEELFunction;

/**
 * additional functions not part of the spec version 1.1
 */
public class KieExtendedDMNFunctions {

    protected static final FEELFunction[] FUNCTIONS = new FEELFunction[]{
                                                                         TimeFunction.INSTANCE,
                                                                         DateFunction.INSTANCE,
                                                                         DurationFunction.INSTANCE,

                                                                         // additional functions not part of the spec version 1.1
                                                                         new NowFunction(),
                                                                         new TodayFunction(),
                                                                         new AbsFunction(),
                                                                         new ModuloFunction(),
                                                                         new ProductFunction(),
                                                                         new CodeFunction(),
                                                                         new InvokeFunction(),
                                                                         new SplitFunction(),
    };

    public static FEELFunction[] getFunctions() {
        return FUNCTIONS;
    }

    public static <T extends FEELFunction> T getFunction(Class<T> functionClazz) {
        return (T) Stream.of(FUNCTIONS).filter(f -> functionClazz.isAssignableFrom(f.getClass())).findFirst().get();
    }
}
