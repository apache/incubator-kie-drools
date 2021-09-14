/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.verifier.core.cache.inspectors.action;

import java.util.Iterator;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.model.FieldAction;

public class FieldActionInspector
        extends ActionInspector {

    public FieldActionInspector(final FieldAction action,
                                final AnalyzerConfiguration configuration) {
        super(action,
              configuration);
    }

    public String toHumanReadableString() {
        return ((FieldAction) action).getField().getName() + " = " + getValuesString();
    }

    private String getValuesString() {
        final StringBuilder builder = new StringBuilder();

        final Iterator<Comparable> iterator = action.getValues().iterator();

        while (iterator.hasNext()) {
            builder.append(iterator.next());
            if (iterator.hasNext()) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    @Override
    public boolean isRedundant(final Object other) {
        if (other instanceof FieldActionInspector) {
            if (areFieldsEqual((FieldActionInspector) other)) {
                return super.isRedundant(other);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean conflicts(final Object other) {
        if (other instanceof FieldActionInspector) {
            if (areFieldsEqual((FieldActionInspector) other)) {
                return super.conflicts(other);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean areFieldsEqual(final FieldActionInspector other) {
        FieldAction otherAction = (FieldAction) other.action;
        return ((FieldAction) action).getField().equals(otherAction.getField());
    }
}
