package org.drools.model.codegen.project;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.kie.internal.builder.KnowledgeBuilderResult;

public class RuleCodegenError extends Error {

    private final KnowledgeBuilderResult[] errors;

    public RuleCodegenError(Collection<? extends KnowledgeBuilderResult> errors) {
        this(errors.toArray(new KnowledgeBuilderResult[errors.size()]));
    }

    public RuleCodegenError(KnowledgeBuilderResult... errors) {
        super("Errors were generated during the code-generation process:\n" +
                Arrays.stream(errors)
                        .map(KnowledgeBuilderResult::toString)
                        .collect(Collectors.joining("\n")));
        this.errors = errors;
    }

    public RuleCodegenError(Exception ex, KnowledgeBuilderResult... errors) {
        super("Errors were generated during the code-generation process:\n" +
                ex.getMessage() + "\n" +
                Arrays.stream(errors)
                        .map(KnowledgeBuilderResult::toString)
                        .collect(Collectors.joining("\n")),
                ex);
        this.errors = errors;
    }

    public KnowledgeBuilderResult[] getErrors() {
        return errors;
    }
}
