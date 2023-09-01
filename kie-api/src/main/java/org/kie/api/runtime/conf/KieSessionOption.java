package org.kie.api.runtime.conf;

import org.kie.api.conf.Option;

/**
 * A markup interface for KieSessionConfiguration options
 */
public interface KieSessionOption extends Option {
    String TYPE = "Base";

    default String type() {
        return TYPE;
    }
}
