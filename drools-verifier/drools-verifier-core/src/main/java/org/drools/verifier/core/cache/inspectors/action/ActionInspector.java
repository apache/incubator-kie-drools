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
package org.drools.verifier.core.cache.inspectors.action;

import java.util.Date;
import java.util.Iterator;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.keys.Key;
import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.index.keys.Values;
import org.drools.verifier.core.index.model.Action;
import org.drools.verifier.core.maps.util.HasKeys;
import org.drools.verifier.core.relations.HumanReadable;
import org.drools.verifier.core.relations.IsConflicting;
import org.drools.verifier.core.relations.IsRedundant;
import org.drools.verifier.core.relations.IsSubsuming;

public abstract class ActionInspector
        implements IsRedundant,
                   IsSubsuming,
                   IsConflicting,
                   HumanReadable,
                   HasKeys {

    private UUIDKey uuidKey;

    protected Action action;
    private AnalyzerConfiguration configuration;

    protected ActionInspector(final Action action,
                              final AnalyzerConfiguration configuration) {
        this.action = action;

        this.uuidKey = configuration.getUUID(this);

        this.configuration = configuration;
    }

    @Override
    public boolean isRedundant(final Object other) {
        if (other instanceof ActionInspector) {
            return areValuesRedundant(((ActionInspector) other).action.getValues());
        } else {
            return false;
        }
    }

    private boolean areValuesRedundant(final Values<Comparable> others) {

        for (final Comparable comparable : action.getValues()) {
            if (!isValueRedundant(others,
                                  comparable)) {
                return false;
            }
        }

        for (final Comparable comparable : others) {
            if (!isValueRedundant(action.getValues(),
                                  comparable)) {
                return false;
            }
        }

        return !(action.getValues().isEmpty() && others.isEmpty());
    }

    private boolean isValueRedundant(final Values<Comparable> others,
                                     final Comparable comparable) {
        for (final Comparable other : others) {
            if (isValueRedundant(comparable,
                                 other)) {
                return true;
            }
        }
        return false;
    }

    private boolean isValueRedundant(final Comparable value,
                                     final Comparable other) {
        if (value.equals(other)) {
            return true;
        } else if (value instanceof Date) {
            return areDatesEqual((Date) value,
                                 other);
        } else if (other instanceof Date) {
            return areDatesEqual((Date) other,
                                 value);
        } else {
            return value.toString().equals(other.toString());
        }
    }

    private boolean areDatesEqual(final Date value,
                                  final Comparable other) {
        if (other instanceof String) {
            return format(value).equals(other);
        } else {
            return false;
        }
    }

    private String format(final Date dateValue) {
        return configuration.formatDate(dateValue);
    }

    @Override
    public boolean conflicts(final Object other) {
        if (other instanceof ActionInspector) {
            final ActionInspector otherActionInspector = (ActionInspector) other;
            return !areValuesRedundant(otherActionInspector.action.getValues());
        } else {
            return false;
        }
    }

    @Override
    public boolean subsumes(final Object other) {
        // At the moment we are not smart enough to figure out subsumption in the RHS.
        // So redundancy == subsumption in this case.
        return isRedundant(other);
    }

    public String toHumanReadableString() {
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

    public boolean hasValue() {
        return action.getValues().isEmpty();
    }

    @Override
    public UUIDKey getUuidKey() {
        return uuidKey;
    }

    @Override
    public Key[] keys() {
        return new Key[0];
    }
}
