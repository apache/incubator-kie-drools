/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.workbench.models.datamodel.rule;

import java.util.Collection;
import java.util.function.Function;

import org.drools.workbench.models.datamodel.rule.InterpolationVariable;
import org.drools.workbench.models.datamodel.rule.TemplateAware;
import org.optaplanner.workbench.models.datamodel.util.TemplateUtils;

public abstract class AbstractActionConstraintMatch implements ActionConstraintMatch,
                                                               TemplateAware {

    private String constraintMatch;

    public AbstractActionConstraintMatch() {
    }

    public AbstractActionConstraintMatch(final String constraintMatch) {
        this.constraintMatch = constraintMatch;
    }

    public String getConstraintMatch() {
        return constraintMatch;
    }

    public void setConstraintMatch(String constraintMatch) {
        this.constraintMatch = constraintMatch;
    }

    @Override
    public Collection<InterpolationVariable> extractInterpolationVariables() {
        return TemplateUtils.extractInterpolationVariables(getConstraintMatch());
    }

    @Override
    public void substituteTemplateVariables(Function<String, String> keyToValueFunction) {
        setConstraintMatch(TemplateUtils.substituteTemplateVariable(getConstraintMatch(),
                                                                    keyToValueFunction));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AbstractActionConstraintMatch that = (AbstractActionConstraintMatch) o;

        return constraintMatch != null ? constraintMatch.equals(that.constraintMatch) : that.constraintMatch == null;
    }

    @Override
    public int hashCode() {
        return constraintMatch != null ? constraintMatch.hashCode() : 0;
    }
}
