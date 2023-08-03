package org.kie.internal.conf;

import org.kie.api.conf.OptionKey;
import org.kie.api.conf.SingleValueRuleBaseOption;

/**
 * Determines is the engine should evaluate rules and execute their consequences sequentially or in parallel.
 *
 * drools.parallelExecution = &lt;sequential|parallel_evaluation|fully_parallel&gt;
 *
 * DEFAULT = SEQUENTIAL
 */
public enum ParallelExecutionOption implements SingleValueRuleBaseOption {

    SEQUENTIAL, PARALLEL_EVALUATION, FULLY_PARALLEL;

    public static final String PROPERTY_NAME = "drools.parallelExecution";

    public static OptionKey<ParallelExecutionOption> KEY = new OptionKey<>(TYPE, PROPERTY_NAME);

    @Override
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public static ParallelExecutionOption determineParallelExecution(final String value) {
        if ("sequential".equalsIgnoreCase(value)) {
            return SEQUENTIAL;
        }
        if ("parallel_evaluation".equalsIgnoreCase(value)) {
            return PARALLEL_EVALUATION;
        }
        if ("fully_parallel".equalsIgnoreCase(value)) {
            return FULLY_PARALLEL;
        }
        throw new IllegalArgumentException("Illegal enum value '" + value + "' for ParallelExecution");
    }

    public boolean isParallel() {
        return this != SEQUENTIAL;
    }

    public String toExternalForm() {
        return this.toString().toLowerCase();
    }
}
