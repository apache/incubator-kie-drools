/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.codegen.unit;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.DataStream;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.api.conf.Clock;
import org.drools.ruleunits.api.conf.ClockType;
import org.drools.ruleunits.impl.datasources.EventListDataStream;
import org.kie.kogito.codegen.data.StockTick;
import org.kie.kogito.codegen.data.ValueDrop;

@Clock(ClockType.PSEUDO)
public class StockUnit implements RuleUnitData {

    private DataStream<StockTick> stockTicks;
    private DataStore<ValueDrop> valueDrops;

    public StockUnit() {
        this(EventListDataStream.create(), DataSource.createStore());
    }

    public StockUnit(DataStream<StockTick> stockTicks, DataStore<ValueDrop> valueDrops) {
        this.stockTicks = stockTicks;
        this.valueDrops = valueDrops;
    }

    public DataStream<StockTick> getStockTicks() {
        return stockTicks;
    }

    public DataStore<ValueDrop> getValueDrops() {
        return valueDrops;
    }
}
