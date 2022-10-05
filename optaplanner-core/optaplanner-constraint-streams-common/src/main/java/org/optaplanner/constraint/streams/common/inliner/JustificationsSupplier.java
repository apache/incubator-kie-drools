package org.optaplanner.constraint.streams.common.inliner;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintJustification;
import org.optaplanner.core.api.score.stream.DefaultConstraintJustification;

/**
 * Allows to create justifications and indictments lazily if and only if constraint matches are enabled.
 *
 * Justification and indictment creation is performance expensive and constraint matches are typically disabled.
 * So justifications and indictments are created lazily, outside of the typical hot path.
 */
public final class JustificationsSupplier {

    public static JustificationsSupplier empty() {
        return new JustificationsSupplier(DefaultConstraintJustification::of, Collections::emptyList);
    }

    public static JustificationsSupplier of(Function<Score<?>, ConstraintJustification> constraintJustificationSupplier,
            Supplier<Collection<Object>> indictedObjectsSupplier) {
        return new JustificationsSupplier(constraintJustificationSupplier, indictedObjectsSupplier);
    }

    private final Function<Score<?>, ConstraintJustification> constraintJustificationSupplier;
    private final Supplier<Collection<Object>> indictedObjectsSupplier;

    private JustificationsSupplier(Function<Score<?>, ConstraintJustification> constraintJustificationSupplier,
            Supplier<Collection<Object>> indictedObjectsSupplier) {
        this.constraintJustificationSupplier = Objects.requireNonNull(constraintJustificationSupplier);
        this.indictedObjectsSupplier = Objects.requireNonNull(indictedObjectsSupplier);
    }

    public ConstraintJustification createConstraintJustification(Score<?> impact) {
        return constraintJustificationSupplier.apply(impact);
    }

    public Collection<Object> createIndictedObjects() {
        return indictedObjectsSupplier.get();
    }

}
