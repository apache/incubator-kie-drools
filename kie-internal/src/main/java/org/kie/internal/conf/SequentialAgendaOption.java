package org.kie.internal.conf;

import org.kie.api.conf.OptionKey;
import org.kie.api.conf.SingleValueRuleBaseOption;

/**
 * An Enum for SequentialAgenda option.
 *
 * drools.sequential.agenda = &lt;sequential|dynamic&gt;
 *
 * DEFAULT = sequential
 */
public enum SequentialAgendaOption implements SingleValueRuleBaseOption {

    SEQUENTIAL,
    DYNAMIC;

    /**
     * The property name for the sequential mode option
     */
    public static final String PROPERTY_NAME = "drools.sequential.agenda";

    public static OptionKey<SequentialAgendaOption> KEY = new OptionKey<>(TYPE, PROPERTY_NAME);

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

}
