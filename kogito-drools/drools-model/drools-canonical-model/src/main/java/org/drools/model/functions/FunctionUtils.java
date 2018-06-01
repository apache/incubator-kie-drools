package org.drools.model.functions;

public final class FunctionUtils {

    private FunctionUtils() { }

    public static <R> FunctionN<R> toFunctionN(final Function0<R> f) {
        return f == null ? null : objs -> f.apply();
    }

    public static <A, R> FunctionN<R> toFunctionN(final Function1<A, R> f) {
        return f == null ? null : objs -> f.apply( (A)objs[0] );
    }

    public static <A, B, R> FunctionN<R> toFunctionN(final Function2<A, B, R> f) {
        return f == null ? null : objs -> f.apply( (A)objs[0], (B)objs[1] );
    }

    public static <A, B, C, R> FunctionN<R> toFunctionN(final Function3<A, B, C, R> f) {
        return f == null ? null : objs -> f.apply((A) objs[0], (B) objs[1], (C) objs[2]);
    }

    public static <A, B, C, D, R> FunctionN<R> toFunctionN(final Function4<A, B, C, D, R> f) {
        return f == null ? null : objs -> f.apply((A) objs[0], (B) objs[1], (C) objs[2], (D) objs[3]);
    }

    public static <A, B, C, D, E, R> FunctionN<R> toFunctionN(final Function5<A, B, C, D, E, R> f) {
        return f == null ? null : objs -> f.apply((A) objs[0], (B) objs[1], (C) objs[2], (D) objs[3], (E) objs[4]);
    }

    public static <A, B, C, D, E, F, R> FunctionN<R> toFunctionN(final Function6<A, B, C, D, E, F, R> f) {
        return f == null ? null : objs -> f.apply((A) objs[0], (B) objs[1], (C) objs[2], (D) objs[3], (E) objs[4], (F) objs[5]);
    }
}
