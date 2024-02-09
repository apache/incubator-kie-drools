/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.compiler.builder.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

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

import static java.util.Arrays.asList;

public class BuildResultCollectorImpl implements BuildResultCollector {
    private Collection<KnowledgeBuilderResult> results = new LinkedHashSet<>(); // avoid duplicate errors

    public BuildResultCollectorImpl() {
    }

    @Override
    public void addBuilderResult(KnowledgeBuilderResult result) {
        this.results.add(result);
    }

    /**
     * This will return true if there were errors in the package building and
     * compiling phase
     */
    @Override
    public boolean hasErrors() {
        return !getErrorList().isEmpty();
    }

    public Collection<KnowledgeBuilderResult> getAllResults() {
        return results;
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

    @Override
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
    }

    public void addAll(Collection<? extends KnowledgeBuilderResult> results) {
        this.results.addAll(results);
    }
}
