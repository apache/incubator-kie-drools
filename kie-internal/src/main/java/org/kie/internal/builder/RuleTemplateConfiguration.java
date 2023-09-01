package org.kie.internal.builder;

import org.kie.api.io.Resource;

public interface RuleTemplateConfiguration {

    Resource getTemplate();
    int getRow();
    int getCol();
}
