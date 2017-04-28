/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.ruleunit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import org.drools.core.base.TypeResolver;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.kie.api.runtime.rule.RuleUnit;

import static org.drools.core.ruleunit.RuleUnitUtil.getUnitName;

public class RuleUnitRegistry {

    private State state = State.UNKNOWN;

    private enum State {
        UNIT, NO_UNIT, UNKNOWN;

        State hasUnit(boolean hasUnit) {
            if (hasUnit) {
                if (this == NO_UNIT) {
                    throw new IllegalStateException( "Cannot mix rules with and without unit" );
                }
                return UNIT;
            } else {
                if (this == UNIT) {
                    throw new IllegalStateException( "Cannot mix rules with and without unit" );
                }
                return NO_UNIT;
            }
        }

        State merge(State other) {
            if (this == UNKNOWN) {
                return other;
            }
            if (other == UNKNOWN) {
                return this;
            }
            if (this != other) {
                throw new IllegalStateException( "Cannot mix rules with and without unit" );
            }
            return this;
        }
    }

    private transient final TypeResolver typeResolver;

    private final Map<String, RuleUnitDescr> ruleUnits = new HashMap<>();

    private final Set<String> nonExistingUnits = new HashSet<>();

    public RuleUnitRegistry() {
        this(null);
    }

    public RuleUnitRegistry( TypeResolver typeResolver ) {
        this.typeResolver = typeResolver;
    }

    public RuleUnitDescr getRuleUnitDescr( RuleUnit ruleUnit ) {
        RuleUnitDescr ruleUnitDescr = ruleUnits.get( getUnitName(ruleUnit) );
        if (ruleUnitDescr == null) {
            throw new IllegalStateException( "Unknown RuleUnit: " + getUnitName(ruleUnit) );
        }
        return ruleUnitDescr;
    }

    public Optional<RuleUnitDescr> getRuleUnitFor( RuleImpl rule ) {
        String unitClassName = rule.getRuleUnitClassName();
        state = state.hasUnit( unitClassName != null );
        return Optional.ofNullable( unitClassName )
                       .map( name -> ruleUnits.computeIfAbsent( name, this::findRuleUnitDescr ) );
    }

    private RuleUnitDescr findRuleUnitDescr( String ruleUnit ) {
        if (nonExistingUnits.contains( ruleUnit )) {
            return null;
        }
        try {
            return new RuleUnitDescr((Class<? extends RuleUnit>) typeResolver.resolveType( ruleUnit ));
        } catch (ClassNotFoundException e) {
            nonExistingUnits.add( ruleUnit );
            return null;
        }
    }

    public void registerRuleUnit( String unitName, Supplier<Class<? extends RuleUnit>> unitSupplier) {
        ruleUnits.computeIfAbsent( unitName, n -> new RuleUnitDescr( unitSupplier.get() ) );
    }

    public void add( RuleUnitRegistry other ) {
        if (other != null) {
            ruleUnits.putAll( other.ruleUnits );
            state = state.merge( other.state );
            nonExistingUnits.addAll( other.nonExistingUnits );
        }
    }

    public boolean hasUnits() {
        return !ruleUnits.isEmpty();
    }
}
