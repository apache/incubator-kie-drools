package org.drools.impl.adapters;

import org.drools.conf.KnowledgeBaseOption;
import org.drools.conf.MultiValueKnowledgeBaseOption;
import org.drools.conf.SingleValueKnowledgeBaseOption;
import org.drools.definition.KnowledgeDefinition;
import org.drools.runtime.conf.KnowledgeSessionOption;
import org.drools.runtime.conf.MultiValueKnowledgeSessionOption;
import org.drools.runtime.conf.SingleValueKnowledgeSessionOption;
import org.kie.api.conf.KieBaseOption;
import org.kie.api.conf.MultiValueKieBaseOption;
import org.kie.api.conf.SingleValueKieBaseOption;
import org.kie.api.definition.KieDefinition;
import org.kie.api.runtime.conf.KieSessionOption;
import org.kie.api.runtime.conf.MultiValueKieSessionOption;
import org.kie.api.runtime.conf.SingleValueKieSessionOption;
import org.kie.internal.builder.KnowledgeBuilderErrors;
import org.kie.internal.builder.ResultSeverity;

public class AdapterUtil {

    public static KnowledgeDefinition.KnowledgeType adaptKnowledgeType(KieDefinition.KnowledgeType type) {
        return KnowledgeDefinition.KnowledgeType.valueOf(type.name());
    }

    public static org.drools.builder.ResultSeverity adaptResultSeverity(ResultSeverity resultSeverity) {
        return org.drools.builder.ResultSeverity.valueOf(resultSeverity.name());
    }

    public static ResultSeverity adaptResultSeverity(org.drools.builder.ResultSeverity resultSeverity) {
        return ResultSeverity.valueOf(resultSeverity.name());
    }

    public static org.drools.builder.ResultSeverity[] adaptResultSeverity(ResultSeverity[] resultSeverities) {
        org.drools.builder.ResultSeverity[] result = new org.drools.builder.ResultSeverity[resultSeverities.length];
        for (int i = 0; i < resultSeverities.length; i++) {
            result[i] = adaptResultSeverity(resultSeverities[i]);
        }
        return result;
    }

    public static ResultSeverity[] adaptResultSeverity(org.drools.builder.ResultSeverity[] resultSeverities) {
        ResultSeverity[] result = new ResultSeverity[resultSeverities.length];
        for (int i = 0; i < resultSeverities.length; i++) {
            result[i] = adaptResultSeverity(resultSeverities[i]);
        }
        return result;
    }

    public static KieBaseOption adaptOption(KnowledgeBaseOption option) {
        throw new RuntimeException("Not Implemented");
    }

    public static SingleValueKnowledgeBaseOption adaptOption(SingleValueKieBaseOption option) {
        throw new RuntimeException("Not Implemented");
    }

    public static Class<? extends SingleValueKieBaseOption> adaptSingleValueBaseOption(Class<? extends SingleValueKnowledgeBaseOption> option) {
        throw new RuntimeException("Not Implemented");
    }

    public static MultiValueKnowledgeBaseOption adaptOption(MultiValueKieBaseOption option) {
        throw new RuntimeException("Not Implemented");
    }

    public static Class<? extends MultiValueKieBaseOption> adaptMultiValueBaseOption(Class<? extends MultiValueKnowledgeBaseOption> option) {
        throw new RuntimeException("Not Implemented");
    }

    public static KieSessionOption adaptOption(KnowledgeSessionOption option) {
        throw new RuntimeException("Not Implemented");
    }

    public static SingleValueKnowledgeSessionOption adaptOption(SingleValueKieSessionOption option) {
        throw new RuntimeException("Not Implemented");
    }

    public static Class<? extends SingleValueKieSessionOption> adaptSingleValueSessionOption(Class<? extends SingleValueKnowledgeSessionOption> option) {
        throw new RuntimeException("Not Implemented");
    }

    public static MultiValueKnowledgeSessionOption adaptOption(MultiValueKieSessionOption option) {
        throw new RuntimeException("Not Implemented");
    }

    public static Class<? extends MultiValueKieSessionOption> adaptMultiValueSessionOption(Class<? extends MultiValueKnowledgeSessionOption> option) {
        throw new RuntimeException("Not Implemented");
    }
}
