package org.drools.mvel.integrationtests;

import java.io.StringReader;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.kie.api.internal.assembler.KieAssemblerService;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;

public class FakeDRFAssemblerService implements KieAssemblerService {

    private static final String GATEWAY_RULE = "package com.example.rules\n" +
                                               "import com.example.*;\n" +
                                               "rule \"RuleFlow-Split-example-xxx-DROOLS_DEFAULT\"  @Propagation(EAGER)\n" +
                                               "      ruleflow-group \"DROOLS_SYSTEM\"\n" +
                                               "    when\n" +
                                               "      exists String(this == \"Left\")\n" +
                                               "    then\n" +
                                               "end";

    @Override
    public ResourceType getResourceType() {
        return ResourceType.DRF;
    }

    @Override
    public void addResourceAfterRules(Object kbuilder, Resource resource, ResourceType type, ResourceConfiguration configuration) throws Exception {
        // Just add one fake gateway drl rule. Not for process capability testing
        ((KnowledgeBuilderImpl) kbuilder).addPackageFromDrl(new StringReader(GATEWAY_RULE), resource);
    }
}
