package org.optaplanner.core.impl.domain.common.accessor;

import java.util.function.Function;

public abstract class AbstractMemberAccessor implements MemberAccessor {

    // We cache this so that the same reference is always returned; useful for CS node sharing.
    private final Function getterFuction = this::executeGetter;

    @Override
    public final <Fact_, Result_> Function<Fact_, Result_> getGetterFunction() {
        return getterFuction;
    }

}
