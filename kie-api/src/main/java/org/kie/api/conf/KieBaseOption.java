package org.kie.api.conf;

import org.kie.api.KieBase;

/**
 * A markup interface for {@link KieBase} options.
 */
public interface KieBaseOption extends Option {
    String TYPE = "Base";

    default String type() {
        return TYPE;
    }
}
