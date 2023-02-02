package org.kie.internal.conf;

import org.kie.api.conf.MultiValueOption;
import org.kie.api.conf.Option;
import org.kie.api.conf.OptionsConfiguration;
import org.kie.api.conf.SingleValueOption;
import org.kie.internal.utils.ChainedProperties;

public interface ConfigurationFactory<T extends Option, S extends SingleValueOption, M extends MultiValueOption> {

    String type();

    OptionsConfiguration<T, S, M> create(CompositeConfiguration<T, S, M> compConfig, ClassLoader classLoader, ChainedProperties chainedProperties);
}
