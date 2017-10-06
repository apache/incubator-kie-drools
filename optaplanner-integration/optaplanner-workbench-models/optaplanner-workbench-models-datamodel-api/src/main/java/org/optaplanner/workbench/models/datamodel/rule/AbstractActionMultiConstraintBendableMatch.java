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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.drools.workbench.models.datamodel.rule.InterpolationVariable;
import org.drools.workbench.models.datamodel.rule.TemplateAware;
import org.optaplanner.workbench.models.datamodel.util.TemplateUtils;

public abstract class AbstractActionMultiConstraintBendableMatch implements ActionConstraintMatch,
                                                                            TemplateAware {

    private List<ActionBendableHardConstraintMatch> actionBendableHardConstraintMatches;

    private List<ActionBendableSoftConstraintMatch> actionBendableSoftConstraintMatches;

    public AbstractActionMultiConstraintBendableMatch() {
    }

    public AbstractActionMultiConstraintBendableMatch(final List<ActionBendableHardConstraintMatch> actionBendableHardConstraintMatches,
                                                      final List<ActionBendableSoftConstraintMatch> actionBendableSoftConstraintMatches) {
        this.actionBendableHardConstraintMatches = actionBendableHardConstraintMatches;
        this.actionBendableSoftConstraintMatches = actionBendableSoftConstraintMatches;
    }

    public List<ActionBendableHardConstraintMatch> getActionBendableHardConstraintMatches() {
        return actionBendableHardConstraintMatches;
    }

    public void setActionBendableHardConstraintMatches(List<ActionBendableHardConstraintMatch> actionBendableHardConstraintMatches) {
        this.actionBendableHardConstraintMatches = actionBendableHardConstraintMatches;
    }

    public List<ActionBendableSoftConstraintMatch> getActionBendableSoftConstraintMatches() {
        return actionBendableSoftConstraintMatches;
    }

    public void setActionBendableSoftConstraintMatches(List<ActionBendableSoftConstraintMatch> actionBendableSoftConstraintMatches) {
        this.actionBendableSoftConstraintMatches = actionBendableSoftConstraintMatches;
    }

    @Override
    public Collection<InterpolationVariable> extractInterpolationVariables() {
        List<InterpolationVariable> interpolationVariableList = new ArrayList<>();
        if (getActionBendableHardConstraintMatches() != null) {
            for (ActionBendableHardConstraintMatch hardConstraintMatch : getActionBendableHardConstraintMatches()) {
                interpolationVariableList.addAll(TemplateUtils.extractInterpolationVariables(hardConstraintMatch.getConstraintMatch()));
            }
        }
        if (getActionBendableSoftConstraintMatches() != null) {
            for (ActionBendableSoftConstraintMatch softConstraintMatch : getActionBendableSoftConstraintMatches()) {
                interpolationVariableList.addAll(TemplateUtils.extractInterpolationVariables(softConstraintMatch.getConstraintMatch()));
            }
        }
        return interpolationVariableList;
    }

    @Override
    public void substituteTemplateVariables(Function<String, String> keyToValueFunction) {
        if (getActionBendableHardConstraintMatches() != null) {
            for (ActionBendableHardConstraintMatch hardConstraintMatch : getActionBendableHardConstraintMatches()) {
                hardConstraintMatch.substituteTemplateVariables(keyToValueFunction);
            }
        }
        if (getActionBendableSoftConstraintMatches() != null) {
            for (ActionBendableSoftConstraintMatch softConstraintMatch : getActionBendableSoftConstraintMatches()) {
                softConstraintMatch.substituteTemplateVariables(keyToValueFunction);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AbstractActionMultiConstraintBendableMatch that = (AbstractActionMultiConstraintBendableMatch) o;

        if (actionBendableHardConstraintMatches != null ? !actionBendableHardConstraintMatches.equals(that.actionBendableHardConstraintMatches) : that.actionBendableHardConstraintMatches != null) {
            return false;
        }
        return actionBendableSoftConstraintMatches != null ? actionBendableSoftConstraintMatches.equals(that.actionBendableSoftConstraintMatches) : that.actionBendableSoftConstraintMatches == null;
    }

    @Override
    public int hashCode() {
        int result = actionBendableHardConstraintMatches != null ? actionBendableHardConstraintMatches.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (actionBendableSoftConstraintMatches != null ? actionBendableSoftConstraintMatches.hashCode() : 0);
        result = ~~result;
        return result;
    }
}
