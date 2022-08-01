/**
 * This package contains performance-sensitive code.
 * Much of it is directly on the hot path of the solver.
 * It contains various micro-optimizations, the benefits of which have been confirmed by extensive benchmarking.
 * When it comes to this code, assumptions and pre-conceived notions of JVM performance should not be trusted.
 * Instead, any likely performance-altering modifications to this code should be carefully benchmarked.
 */
package org.optaplanner.constraint.streams.bavet.common;