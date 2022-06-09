package org.optaplanner.constraint.streams.drools;

import static org.drools.model.DSL.declarationOf;
import static org.drools.model.DSL.from;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.drools.model.DSL;
import org.drools.model.Variable;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.function.TriFunction;

public final class DroolsVariableFactory {

    private final AtomicLong counter = new AtomicLong(0);

    DroolsVariableFactory() {
        // No external instances.
    }

    private String generateUniqueId(String baseName) {
        return baseName + "_" + counter.incrementAndGet();
    }

    /**
     * Declare a new {@link Variable} with a given name and no declared source.
     * Delegates to {@link DSL#declarationOf(Class, String)}.
     *
     * @param clz type of the variable. Using {@link Object} will work in all cases, but Drools will spend unnecessary
     *        amount of time looking up applicable instances of that variable, as it has to traverse instances of all
     *        types in the working memory. Therefore, it is desirable to be as specific as possible.
     * @param baseName name of the variable, mostly useful for debugging purposes. Will be decorated by a numeric
     *        identifier to prevent multiple variables of the same name to exist within left-hand side of a single rule.
     * @param <U> generic type of the input variable
     * @param <V> generic type of the output variable
     * @return new variable declaration, not yet bound to anything
     */
    public <U, V extends U> Variable<V> createVariable(Class<U> clz, String baseName) {
        return (Variable<V>) declarationOf(clz, generateUniqueId(baseName));
    }

    /**
     * Declares a new {@link Object}-typed variable, see {@link #createVariable(Class, String)} for details.
     */
    public <U> Variable<U> createVariable(String baseName) {
        return createVariable(Object.class, baseName);
    }

    /**
     * Declare a new {@link Variable} with a given name, which will hold the result of applying a given mapping
     * function on values of the provided variables.
     *
     * @param baseName name of the variable, mostly useful for debugging purposes. Will be decorated by a numeric
     *        identifier to prevent multiple variables of the same name to exist within left-hand side of a single rule.
     * @param source1 never null; value of this is passed to the mapping function
     * @param source2 never null; value of this is passed to the mapping function
     * @param mapping never null
     * @param <U> generic type of the first input variable
     * @param <V> generic type of the second input variable
     * @param <Result_> generic type of the new variable
     * @return never null
     */
    public <U, V, Result_> Variable<Result_> createVariable(String baseName, Variable<U> source1, Variable<V> source2,
            BiFunction<U, V, Result_> mapping) {
        return (Variable<Result_>) declarationOf(Object.class, generateUniqueId(baseName),
                from(source1, source2, (value1, value2) -> {
                    Result_ result = mapping.apply(value1, value2);
                    if (result instanceof Iterable) { // Avoid flattening, which is a default from() behavior.
                        return Collections.singleton(result);
                    }
                    return result;
                }));
    }

    /**
     * As defined by {@link #createVariable(String, Variable, Variable, BiFunction)}.
     */
    public <U, V, W, Result_> Variable<Result_> createVariable(String baseName, Variable<U> source1, Variable<V> source2,
            Variable<W> source3, TriFunction<U, V, W, Result_> mapping) {
        return (Variable<Result_>) declarationOf(Object.class, generateUniqueId(baseName),
                from(source1, source2, source3, (value1, value2, value3) -> {
                    Result_ result = mapping.apply(value1, value2, value3);
                    if (result instanceof Iterable) { // Avoid flattening, which is a default from() behavior.
                        return Collections.singleton(result);
                    }
                    return result;
                }));
    }

    /**
     * As defined by {@link #createVariable(String, Variable, Variable, BiFunction)}.
     */
    public <U, V, W, Y, Result_> Variable<Result_> createVariable(String baseName, Variable<U> source1, Variable<V> source2,
            Variable<W> source3, Variable<Y> source4, QuadFunction<U, V, W, Y, Result_> mapping) {
        return (Variable<Result_>) declarationOf(Object.class, generateUniqueId(baseName),
                from(source1, source2, source3, source4, (value1, value2, value3, value4) -> {
                    Result_ result = mapping.apply(value1, value2, value3, value4);
                    if (result instanceof Iterable) { // Avoid flattening, which is a default from() behavior.
                        return Collections.singleton(result);
                    }
                    return result;
                }));
    }

    /**
     * Declare a new {@link Variable} with a given name, which will hold the individual results of applying the given
     * mapping function on the value of the provided variable.
     * Each such result will trigger a single rule firing.
     * (Default behavior of Drools' From node.)
     *
     * @param baseName name of the variable, mostly useful for debugging purposes. Will be decorated by a numeric
     *        identifier to prevent multiple variables of the same name to exist within left-hand side of a single rule.
     * @param source never null; value of this is passed to the mapping function
     * @param mapping never null
     * @param <U> generic type of the input variable
     * @param <Result_> generic type of the new variable
     * @return
     */
    public <U, Result_> Variable<Result_> createFlattenedVariable(String baseName, Variable<U> source,
            Function<U, Iterable<Result_>> mapping) {
        return (Variable<Result_>) declarationOf(Object.class, generateUniqueId(baseName),
                from(source, mapping::apply)); // By default, from() flattens.
    }

}
