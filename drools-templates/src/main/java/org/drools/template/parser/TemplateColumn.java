package org.drools.template.parser;

import org.drools.template.model.Rule;

/**
 * A column condition for a rule template to be generated.
 */
interface TemplateColumn {
    void addCondition(final Rule rule);

    String getName();

    boolean isNotCondition();

    String getCondition();
}
