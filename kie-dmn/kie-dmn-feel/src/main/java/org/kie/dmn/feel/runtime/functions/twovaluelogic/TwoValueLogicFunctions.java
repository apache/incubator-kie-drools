package org.kie.dmn.feel.runtime.functions.twovaluelogic;

import java.util.List;
import java.util.function.Function;

public class TwoValueLogicFunctions {
    private static AllFunction allFunction = new AllFunction();
    private static AnyFunction anyFunction = new AnyFunction();

    public static Boolean all(List<Boolean> list) {
        return allFunction.invoke(list).cata(e -> Boolean.FALSE, Function.identity());
    }

    public static Boolean any(List<Boolean> list) {
        return anyFunction.invoke(list).cata(e -> Boolean.FALSE, Function.identity());
    }

}
