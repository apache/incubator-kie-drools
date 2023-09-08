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
package org.drools.ruleunits.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStream;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.api.conf.Clock;
import org.drools.ruleunits.api.conf.ClockType;
import org.drools.ruleunits.api.conf.EventProcessing;
import org.drools.ruleunits.api.conf.EventProcessingType;
import org.drools.ruleunits.impl.domain.SimpleFact;

@EventProcessing(EventProcessingType.STREAM)
@Clock(ClockType.PSEUDO)
public class TestRuleUnit implements RuleUnitData {

    private final Integer[] numbersArray;

    private BigDecimal number;

    private final DataStream<String> strings = DataSource.createStream();

    private final List<String> stringList;
    private final List<SimpleFact> simpleFactList;
    // This is a little hack to be able to test unbinding of datasources.
    // It cannot be done normally, because everything is done privately in the RuleUnitDescr, therefore cannot be mocked.
    // This property is visible through getter as a datasource, but upon each call it gets switched
    // It should be called just twice(bind/unbind), so therefore we can test if this was unbound.
    public boolean bound = false;

    public TestRuleUnit() {
        this(new Integer[] {}, BigDecimal.ZERO);
    }

    public TestRuleUnit(final Integer[] numbersArray, final BigDecimal number) {
        this.numbersArray = numbersArray;
        this.number = number;
        this.stringList = new ArrayList<>();
        this.simpleFactList = new ArrayList<>();
    }

    public DataStream<String> getStrings() {
        return strings;
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
}
