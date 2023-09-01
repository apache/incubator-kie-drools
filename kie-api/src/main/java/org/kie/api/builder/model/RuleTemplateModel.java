package org.kie.api.builder.model;

/**
 * RuleTemplateModel is a model allowing to programmatically apply a Drools Rule Template to a Decision Table
 */
public interface RuleTemplateModel {
    String getDtable();
    String getTemplate();
    int getRow();
    int getCol();
}
