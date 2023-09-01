package org.kie.pmml.evaluator.core.utils;

import java.util.function.Supplier;

import org.kie.pmml.api.models.PMMLStep;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;

/**
 * Common utility methods related to <code>PMMLListener</code>
 */
public class PMMLListenerUtils {

    /**
     * Send the <code>PMMLStep</code> to all registered <code>PMMLListener</code>s
     *
     * @param stepSupplier
     * @param context
     */
    public static void stepExecuted(final Supplier<PMMLStep> stepSupplier, final PMMLRuntimeContext context) {
        if (!context.getEfestoListeners().isEmpty()) {
            final PMMLStep step = stepSupplier.get();
            context.getEfestoListeners().forEach(listener -> listener.stepExecuted(step));
        }
    }

    private PMMLListenerUtils() {
    }
}
