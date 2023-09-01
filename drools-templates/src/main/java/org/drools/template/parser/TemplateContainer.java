package org.drools.template.parser;

import java.util.Map;

public interface TemplateContainer {

    Map<String, RuleTemplate> getTemplates();

    Column[] getColumns();

    Column getColumn(String name);

    String getHeader();

}
