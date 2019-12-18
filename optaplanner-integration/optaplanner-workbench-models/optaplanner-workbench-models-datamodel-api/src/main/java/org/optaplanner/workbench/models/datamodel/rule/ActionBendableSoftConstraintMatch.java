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

import org.drools.workbench.models.datamodel.rule.TemplateAware;

public class ActionBendableSoftConstraintMatch extends AbstractActionBendableConstraintMatch {

    public ActionBendableSoftConstraintMatch() {
    }

    public ActionBendableSoftConstraintMatch(final int position,
                                             final String constraintMatch) {
        super(constraintMatch,
              position);
    }

    @Override
    public TemplateAware cloneTemplateAware() {
        return new ActionBendableSoftConstraintMatch(getPosition(),
                                                     getConstraintMatch());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        ActionBendableSoftConstraintMatch that = (ActionBendableSoftConstraintMatch) o;

        return position == that.position;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = ~~result;
        result = 31 * result + position;
        result = ~~result;
        return result;
    }

    @Override
    public String getStringRepresentation() {
        return new StringBuilder()
                .append("scoreHolder.addSoftConstraintMatch(kcontext, ")
                .append(getPosition())
                .append(", ")
                .append(getConstraintMatch())
                .append(")").toString();
    }
}
