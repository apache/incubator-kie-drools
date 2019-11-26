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

package org.drools.ruleunit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TestRuleUnit implements RuleUnit {

    private final Integer[] numbersArray;

    @UnitVar("numberVariable")
    private BigDecimal number;

    private final List<String> stringList;
    private final List<SimpleFact> simpleFactList;
    // This is a little hack to be able to test unbinding of datasources.
    // It cannot be done normally, because everything is done privately in the RuleUnitDescr, therefore cannot be mocked.
    // This property is visible through getter as a datasource, but upon each call it gets switched
    // It should be called just twice(bind/unbind), so therefore we can test if this was unbound.
    public boolean bound = false;

    public TestRuleUnit() {
        this(new Integer[]{}, BigDecimal.ZERO);
    }

    public TestRuleUnit(final Integer[] numbersArray, final BigDecimal number) {
        this.numbersArray = numbersArray;
        this.number = number;
        this.stringList = new ArrayList<>();
        this.simpleFactList = new ArrayList<>();
    }

    public Integer[] getNumbersArray() {
        return numbersArray;
    }

    public BigDecimal getNumber() {
        return number;
    }

    public List<String> getStringList() {
        return stringList;
    }

    public List<SimpleFact> getSimpleFactList() {
        return simpleFactList;
    }

    public boolean getBound() {
        bound = !bound;
        return bound;
    }

    public void addSimpleFact(final SimpleFact simpleFact) {
        simpleFactList.add(simpleFact);
    }

    public void addString(final String string) {
        stringList.add(string);
    }

    @Override
    public void onStart() {
        // Intentionally empty.
    }

    @Override
    public void onEnd() {
        // Intentionally empty.
    }

    @Override
    public void onSuspend() {
        // Intentionally empty.
    }

    @Override
    public void onResume() {
        // Intentionally empty.
    }

    @Override
    public void onYield(final RuleUnit other) {
        // Intentionally empty.
    }
}
