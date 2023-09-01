package org.kie.internal.builder.conf;
import org.kie.api.conf.OptionsConfiguration;

/**
 * A base interface for type safe configurations
 */
public interface KnowledgeBuilderOptionsConfiguration extends OptionsConfiguration<KnowledgeBuilderOption, SingleValueKieBuilderOption, MultiValueKieBuilderOption> {

}
