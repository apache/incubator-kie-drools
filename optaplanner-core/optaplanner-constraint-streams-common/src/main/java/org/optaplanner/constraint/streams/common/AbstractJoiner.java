package org.optaplanner.constraint.streams.common;

import java.util.Objects;
import java.util.function.Function;

import org.optaplanner.core.impl.score.stream.JoinerType;

public abstract class AbstractJoiner<Right_> {

    protected final Function<Right_, Object>[] rightMappings;
    protected final JoinerType[] joinerTypes;

    protected <Property_> AbstractJoiner(Function<Right_, Property_> rightMapping, JoinerType joinerType) {
        this(new Function[] { rightMapping }, new JoinerType[] { joinerType });
    }

    protected <Property_> AbstractJoiner(Function<Right_, Property_>[] rightMappings, JoinerType[] joinerTypes) {
        this.rightMappings = (Function<Right_, Object>[]) Objects.requireNonNull(rightMappings);
        this.joinerTypes = Objects.requireNonNull(joinerTypes);
    }

    public final Function<Right_, Object> getRightMapping(int index) {
        return rightMappings[index];
    }

    public final int getJoinerCount() {
        return joinerTypes.length;
    }

    public final JoinerType getJoinerType(int index) {
        return joinerTypes[index];
    }

}
