package org.kie.dmn.core.compiler;

import org.kie.api.conf.Option;

public interface DMNOption extends Option {
    default String type() {
        return "DMN";
    }

}
