package org.kie.internal.runtime.conf;

import org.kie.api.definition.rule.Rule;

public interface ForceEagerActivationFilter {
    boolean accept(Rule rule);
}
