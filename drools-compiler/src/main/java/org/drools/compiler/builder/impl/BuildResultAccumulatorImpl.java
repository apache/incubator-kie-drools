package org.drools.compiler.builder.impl;

import org.drools.compiler.compiler.ConfigurableSeverityResult;
import org.drools.compiler.compiler.DroolsErrorWrapper;
import org.drools.compiler.compiler.DroolsWarning;
import org.drools.compiler.compiler.DroolsWarningWrapper;
import org.drools.compiler.compiler.PackageBuilderErrors;
import org.drools.compiler.compiler.PackageBuilderResults;
import org.drools.drl.parser.BaseKnowledgeBuilderResultImpl;
import org.drools.drl.parser.DroolsError;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.KnowledgeBuilderResults;
import org.kie.internal.builder.ResultSeverity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;

public class BuildResultAccumulatorImpl implements BuildResultAccumulator {
    private Collection<KnowledgeBuilderResult> results = new ArrayList<>();

    public BuildResultAccumulatorImpl() {
    }

    @Override
    public void addBuilderResult(KnowledgeBuilderResult result) {
        this.results.add(result);
    }

    /**
     * This will return true if there were errors in the package building and
     * compiling phase
     */
    public boolean hasErrors() {
        return !getErrorList().isEmpty();
    }

    public KnowledgeBuilderResults getResults(ResultSeverity... problemTypes) {
        List<KnowledgeBuilderResult> problems = getResultList(problemTypes);
        return new PackageBuilderResults(problems.toArray(new BaseKnowledgeBuilderResultImpl[problems.size()]));
    }

    private List<KnowledgeBuilderResult> getResultList(ResultSeverity... severities) {
        List<ResultSeverity> typesToFetch = asList(severities);
        ArrayList<KnowledgeBuilderResult> problems = new ArrayList<>();
        for (KnowledgeBuilderResult problem : results) {
            if (typesToFetch.contains(problem.getSeverity())) {
                problems.add(problem);
            }
        }
        return problems;
    }

    public boolean hasResults(ResultSeverity... problemTypes) {
        return !getResultList(problemTypes).isEmpty();
    }

    private List<DroolsError> getErrorList() {
        List<DroolsError> errors = new ArrayList<>();
        for (KnowledgeBuilderResult problem : results) {
            if (problem.getSeverity() == ResultSeverity.ERROR) {
                if (problem instanceof ConfigurableSeverityResult) {
                    errors.add(new DroolsErrorWrapper(problem));
                } else {
                    errors.add((DroolsError) problem);
                }
            }
        }
        return errors;
    }

    public boolean hasWarnings() {
        return !getWarnings().isEmpty();
    }

    public boolean hasInfo() {
        return !getInfoList().isEmpty();
    }

    public List<DroolsWarning> getWarnings() {
        List<DroolsWarning> warnings = new ArrayList<>();
        for (KnowledgeBuilderResult problem : results) {
            if (problem.getSeverity() == ResultSeverity.WARNING) {
                if (problem instanceof ConfigurableSeverityResult) {
                    warnings.add(new DroolsWarningWrapper(problem));
                } else {
                    warnings.add((DroolsWarning) problem);
                }
            }
        }
        return warnings;
    }

    private List<KnowledgeBuilderResult> getInfoList() {
        return getResultList(ResultSeverity.INFO);
    }

    public void reportError(KnowledgeBuilderError error) {
        getErrors().add(error);
    }

    /**
     * @return A list of Error objects that resulted from building and compiling
     * the package.
     */
    public PackageBuilderErrors getErrors() {
        List<DroolsError> errors = getErrorList();
        return new PackageBuilderErrors(errors.toArray(new DroolsError[errors.size()]));
    }

    /**
     * Reset the error list. This is useful when incrementally building
     * packages. Care should be used when building this, if you clear this when
     * there were errors on items that a rule depends on (eg functions), then
     * you will get spurious errors which will not be that helpful.
     */
    public void resetErrors() {
        resetProblemType(ResultSeverity.ERROR);
    }

    public void resetWarnings() {
        resetProblemType(ResultSeverity.WARNING);
    }

    private void resetProblemType(ResultSeverity problemType) {
        List<KnowledgeBuilderResult> toBeDeleted = new ArrayList<>();
        for (KnowledgeBuilderResult problem : results) {
            if (problemType != null && problemType.equals(problem.getSeverity())) {
                toBeDeleted.add(problem);
            }
        }
        this.results.removeAll(toBeDeleted);
    }

    public void resetProblems() {
        this.results.clear();
//        if (this.processBuilder != null) {
//            this.processBuilder.getErrors().clear();
//        }
    }
}
