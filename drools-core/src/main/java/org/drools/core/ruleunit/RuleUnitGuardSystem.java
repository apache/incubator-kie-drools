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

package org.drools.core.ruleunit;

import org.drools.core.impl.RuleUnitExecutorSession;
import org.drools.core.spi.Activation;
import org.kie.api.runtime.rule.RuleUnit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class RuleUnitGuardSystem {

    private static final RuleUnit ROOT_UNIT = new RuleUnit() { };

    private final RuleUnitExecutorSession session;

    private Map<Guard, Guard> guardMap = new HashMap<>();

    private Map<Activation, List<Guard>> guardsByActivation = new HashMap<>();
    private Map<RuleUnit, Set<Guard>> guardsByActivatingUnit = new HashMap<>();

    public RuleUnitGuardSystem( RuleUnitExecutorSession session ) {
        this.session = session;
    }

    public void registerGuard( RuleUnit ruleUnit, Activation activation ) {
        Guard g = new Guard( ruleUnit, activation.getRule() );
        Guard guard = guardMap.computeIfAbsent( g, x -> g );

        guard.addActivation(activation);
        guardsByActivation.computeIfAbsent( activation, a -> new ArrayList<>() ).add( guard );
        guardsByActivatingUnit.computeIfAbsent( getCurrentRuleUnit(), ru -> new HashSet<>() ) .add(guard);
    }

    public void removeActivation( Activation activation ) {
        List<Guard> guards = guardsByActivation.get( activation );
        if (guards == null) {
            return;
        }

        guards.removeIf( guard -> {
            guard.removeActivation( activation );
            if ( !guard.isActive() ) {
                guardMap.remove( guard );
                guardsByActivatingUnit.computeIfPresent( getCurrentRuleUnit(), ( s, gs ) -> {
                    gs.remove( guard );
                    return gs.isEmpty() ? null : gs;
                } );
                return true;
            }
            return false;
        } );

        if (guards.isEmpty()) {
            guardsByActivation.remove( activation );
        }
    }

    private RuleUnit getCurrentRuleUnit() {
        return session.getCurrentRuleUnit() != null ? session.getCurrentRuleUnit() : ROOT_UNIT;
    }

    public int fireActiveUnits() {
        return fireActiveUnits( ROOT_UNIT );
    }

    public int fireActiveUnits(RuleUnit ruleUnit) {
        return fireActiveUnits(ruleUnit, new HashSet<>());
    }

    private int fireActiveUnits(RuleUnit ruleUnit, Set<RuleUnit> firedUnits) {
        Set<Guard> guards = guardsByActivatingUnit.get(ruleUnit);
        if (guards == null) {
            return 0;
        }
        int result = 0;
        while (true) {
            Optional<RuleUnit> unit = guards.stream().map( Guard::getGuardedUnit )
                                            .filter( u -> !firedUnits.contains( u ) ).findFirst();
            if (!unit.isPresent()) {
                break;
            }
            RuleUnit firingUnit = unit.get();
            result += session.internalExecuteUnit( firingUnit );
            firedUnits.add(firingUnit);
            result += fireActiveUnits( firingUnit, firedUnits );
        }
        return result;
    }
}
