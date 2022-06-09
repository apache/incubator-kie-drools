package org.optaplanner.constraint.streams.common.inliner;

import java.util.List;
import java.util.function.Supplier;

/**
 * This interface allows to create justifications lazily
 * if and only if constraint matches are enabled.
 *
 * Justifications creation is performance expensive and constraint matches are typically disabled.
 * So justifications are created lazily, outside of the typical hot path.
 */
@FunctionalInterface
public interface JustificationsSupplier extends Supplier<List<Object>> {
}
