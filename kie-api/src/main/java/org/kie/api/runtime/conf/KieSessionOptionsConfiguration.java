package org.kie.api.runtime.conf;

import org.kie.api.conf.OptionsConfiguration;

/**
 * A base interface for type safe configurations
 */
public interface KieSessionOptionsConfiguration extends OptionsConfiguration<KieSessionOption, SingleValueKieSessionOption, MultiValueKieSessionOption> {

}
