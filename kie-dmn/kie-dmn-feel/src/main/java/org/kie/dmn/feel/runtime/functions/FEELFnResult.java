package org.kie.dmn.feel.runtime.functions;

import java.util.Optional;
import java.util.function.Function;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.util.Either;

public class FEELFnResult<T> extends Either<FEELEvent, T> {

    protected FEELFnResult(Optional<FEELEvent> left, Optional<T> right) {
        super(left, right);
    }

    public static <T> FEELFnResult<T> ofError(FEELEvent event) {
        return new FEELFnResult<>(Optional.of(event), Optional.empty());
    }
    
    public static <T> FEELFnResult<T> ofResult(T value) {
        return new FEELFnResult<>(Optional.empty(), Optional.ofNullable(value));
    }
    
    public <X> FEELFnResult<X> map(Function<T, X> rightFn) {
        return isLeft()
                ? ofError(this.getLeft().get())
                : ofResult(rightFn.apply(this.getRight().get()));
    }
    
    public <X> FEELFnResult<X> flatMap(Function<T, FEELFnResult<X>> rightFn) {
        return isLeft()
                ? ofError(this.getLeft().get())
                : rightFn.apply(this.getRight().get());
    }

}