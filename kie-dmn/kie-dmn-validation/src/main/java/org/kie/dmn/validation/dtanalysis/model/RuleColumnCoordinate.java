/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.validation.dtanalysis.model;

import org.kie.dmn.feel.util.Generated;

public class RuleColumnCoordinate {
    public final int rule;
    public final int column;
    public final String feelText;

    public RuleColumnCoordinate(int rule, int column, String feelText) {
        super();
        this.rule = rule;
        this.column = column;
        this.feelText = feelText;
    }

    @Generated("org.eclipse.jdt.internal.corext.codemanipulation")
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + column;
        result = prime * result + ((feelText == null) ? 0 : feelText.hashCode());
        result = prime * result + rule;
        return result;
    }

    @Generated("org.eclipse.jdt.internal.corext.codemanipulation")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RuleColumnCoordinate other = (RuleColumnCoordinate) obj;
        if (column != other.column) {
            return false;
        }
        if (feelText == null) {
            if (other.feelText != null) {
                return false;
            }
        } else if (!feelText.equals(other.feelText)) {
            return false;
        }
        if (rule != other.rule) {
            return false;
        }
        return true;
    }

    @Generated("org.eclipse.jdt.internal.corext.codemanipulation")
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RuleColumnCoordinate [rule=");
        builder.append(rule);
        builder.append(", column=");
        builder.append(column);
        builder.append(", feelText=");
        builder.append(feelText);
        builder.append("]");
        return builder.toString();
    }


}
